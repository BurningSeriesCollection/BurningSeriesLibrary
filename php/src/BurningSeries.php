<?php

class BurningSeries
{
	/** @var string $baseUrl */
	private $baseUrl = 'http://bs.to/serie/';
	/** @var string $baseApiUrl */
	private $baseApiUrl = 'http://bs.to/api/';
	/** @var string $coverUrl */
	private $coverUrl = '//s.bs.to/img/cover/{id}.jpg';
	/** @var string $sessionId */
	private $sessionId = null;

	const SORT_ALPHABETICAL = 0;
	const SORT_GENRE = 1;
	const SORT_NEWEST = 2;

	const ANDROID = 'android';

	/** @var int $calls */
	private $calls = 0;
	/** @var int $postCalls */
	private $postCalls = 0;

	/*****************************************
	 ************ General Helpers ************
	 *****************************************/

	/**
	 * Search series which contain "$name"
	 *
	 * @param string $name
	 * @param bool   $exact Whether or not only exact matches should be returned
	 *
	 * @return array
	 */
	public function search($name, $exact = false)
	{
		$name = (string)$name;

		$series = $this->getSeries();

		$result = array();
		foreach ($series as $serie) {
			if (!$exact && stripos($serie['series'], $name) !== false) {
				$result[] = $serie;
			} elseif ($exact && strtolower($serie['series']) == strtolower($name)) {
				$result[] = $serie;
			}
		}

		// If we're searching exact names we only expect one result
		if ($exact && count($result) == 1) {
			$result = $result[0];
		} elseif ($exact && count($result) > 1) {
			$result = array();
		}

		return $result;
	}

	/**
	 * Search a series by name
	 *
	 * @param string $name
	 *
	 * @return array
	 */
	public function getByName($name)
	{
		$serie = $this->search($name, true);

		if (empty($serie)) {
			return array();
		}

		return $this->getSerie($serie['id']);
	}

	/**
	 * Build the link to a serie
	 *
	 * @param int|string|array $serie
	 *
	 * @return string
	 */
	public function buildSerieUrl($serie)
	{
		$serie = $this->getSerieObject($serie);

		if (empty($serie)) {
			return '';
		}

		return $this->buildLink($serie['url']);
	}

	/**
	 * Build the link to a season
	 *
	 * @param int|string|array $serie
	 * @param int              $season
	 *
	 * @return string
	 */
	public function buildSeasonUrl($serie, $season)
	{
		$season = (int)$season;

		$serieLink = $this->buildSerieUrl($serie);

		return $serieLink . '/' . $season;
	}

	/**
	 * Build the link to an episode
	 *
	 * @param int|string|array $serie
	 * @param int              $season
	 * @param int|array        $episode
	 *
	 * @return string
	 */
	public function buildEpisodeUrl($serie, $season, $episode)
	{
		$episode = (int)$episode;

		$seasonLink = $this->buildSeasonUrl($serie, $season);

		return $seasonLink . '/' . $episode . '-Episode';
	}

	/**
	 * Build the link to an episode, a season or a serie, depending on the number of parameters
	 *
	 * @param int|string|array $serie
	 * @param int              $season
	 * @param int|array        $episode
	 *
	 * @return string
	 */
	public function buildUrl($serie, $season = null, $episode = null)
	{
		if ($episode !== null) {
			return $this->buildEpisodeUrl($serie, $season, $episode);
		} elseif ($season !== null) {
			return $this->buildSeasonUrl($serie, $season);
		}

		return $this->buildSerieUrl($serie);
	}

	/**
	 * Get the cover of a series
	 *
	 * @param int|string|array $serie
	 *
	 * @return string
	 */
	public function getCover($serie)
	{
		$serie = $this->getSerieObject($serie);

		return str_replace('{id}', $serie['id'], $this->coverUrl);
	}

	/**
	 * Get an array of all available genres
	 *
	 * @return array
	 */
	public function getGenres()
	{
		return array_keys($this->getSeries(self::SORT_GENRE));
	}

	/**
	 * Check whether a specific version has been watched
	 *
	 * @param int|string|array $serie
	 * @param int              $season
	 * @param int              $episode
	 *
	 * @return bool
	 */
	public function hasWatched($serie, $season, $episode)
	{
		if (empty($this->sessionId)) {
			return false;
		}

		$serie = $this->getSerieObject($serie);

		$season = $this->getSeason($serie['id'], $season);

		$episode = (int)$episode;

		foreach ($season as $epi) {
			if ($epi['epi'] != $episode) {
				continue;
			}

			return (bool)$epi['watched'];
		}

		return false;
	}

	/**
	 * Get the first unwatched episode for a series
	 *
	 * @param int|string|array $serie
	 * @param int              $season if set only that season will be checked
	 * @param int              $offset if set all episodes before (and including) the specified one will be ignored
	 *
	 * @return array
	 */
	public function getNextUnwatchedEpisode($serie, $season = null, $offset = null)
	{
		$serie = $this->getSerieObject($serie);

		if ($season !== null) {
			$seasons = array((int)$season);
		} else {
			$seasons = range(1, $serie['seasons']);
		}

		foreach ($seasons as $season) {
			$episodes = $this->getSeason($serie['id'], $season);

			usort($episodes, array($this, 'sortEpisodes'));

			if ($offset !== null) {
				$episodes = array_slice($episodes, $offset);
			}

			foreach ($episodes as $episode) {
				if ($episode['watched']) {
					continue;
				}

				return $this->getEpisode($serie['id'], $season, $episode['epi']);
			}
		}

		return array();
	}

	/**
	 * Get the first unwatched movie for a series
	 *
	 * @param int|string|array $serie
	 *
	 * @return array
	 */
	public function getNextUnwatchedMovie($serie)
	{
		return $this->getNextUnwatchedEpisode($serie, 0);
	}

	/**
	 * Mark a serie as favorite
	 *
	 * @param int|string|array $serie
	 *
	 * @return bool
	 */
	public function markAsFavorite($serie)
	{
		if (empty($this->sessionId)) {
			return false;
		}

		$serie = $this->getSerieObject($serie);

		$favorites = $this->getFavoriteSeries();

		$favoriteIds = array();
		foreach ($favorites as $favorite) {
			$favoriteIds[] = $favorite['id'];
		}

		if (in_array($serie['id'], $favoriteIds)) {
			return true;
		}

		$favoriteIds[] = $serie['id'];

		return $this->setFavoriteSeries($favoriteIds);
	}

	/**
	 * Unmark a serie as favorite
	 *
	 * @param int|string|array $serie
	 *
	 * @return bool
	 */
	public function unmarkAsFavorite($serie)
	{
		if (empty($this->sessionId)) {
			return false;
		}

		$serie = $this->getSerieObject($serie);

		$favorites = $this->getFavoriteSeries();

		$favoriteIds = array();
		$isFavorite = false;
		foreach ($favorites as $favorite) {
			if ($favorite['id'] == $serie['id']) {
				$isFavorite = true;
				continue;
			}

			$favoriteIds[] = $favorite['id'];
		}

		if (!$isFavorite) {
			return true;
		}

		return $this->setFavoriteSeries($favoriteIds);
	}

	/**
	 * Check whether a serie is marked as favorite
	 *
	 * @param int|string|array $serie
	 *
	 * @return bool
	 */
	public function isFavoritedSerie($serie)
	{
		if (empty($this->sessionId)) {
			return false;
		}

		$serie = $this->getSerieObject($serie);

		$favorites = $this->getFavoriteSeries();

		foreach ($favorites as $favorite) {
			if ($favorite['id'] == $serie['id']) {
				return true;
			}
		}

		return false;
	}

	/*************************************
	 ************ General API ************
	 *************************************/

	/**
	 * Get all series sorted by either alphabetical order, by genre or by upload date
	 *
	 * @param int $sort
	 *
	 * @return array
	 */
	public function getSeries($sort = self::SORT_ALPHABETICAL)
	{
		$link = 'series';
		if ($sort == self::SORT_GENRE) {
			$link = 'series:genre';
		}

		$series = $this->call($link);

		if ($sort == self::SORT_NEWEST) {
			usort($series, array($this, 'sortById'));
		}

		return $series;
	}

	/**
	 * Get series by genre
	 *
	 * @param string|int $genre If not set all series will be returned sorted by genre. If set all series for that
	 *                          genre (either specified by name or ID) will be returned
	 *
	 * @return array
	 */
	public function getByGenre($genre = '')
	{
		$series = $this->getSeries(self::SORT_GENRE);

		// Malformed integer? There isn't a genre name where this would be true
		if ((int)$genre == $genre && strlen((int)$genre) == strlen($genre)) {
			$genre = (int)$genre;
		}

		if (is_string($genre) && !empty($genre)) {
			if (isset($series[$genre])) {
				return $series[$genre];
			}

			return array();
		} elseif (is_int($genre)) {
			foreach ($series as $genreInformation) {
				if ($genreInformation['id'] == $genre) {
					return $genreInformation;
				}
			}

			return array();

		}

		return $series;
	}

	public function getNewest()
	{
		return $this->getSeries(self::SORT_NEWEST);
	}

	/**
	 * Get informations for one series
	 *
	 * @param int $serie
	 *
	 * @return array
	 */
	public function getSerie($serie)
	{
		$serie = (int)$serie;

		$result = $this->call("series/{$serie}/1");

		return isset($result['series']) ? $result['series'] : array();
	}

	/**
	 * Get informations for one season
	 *
	 * @param int $serie
	 * @param int $season
	 *
	 * @return array
	 */
	public function getSeason($serie, $season)
	{
		$serie = (int)$serie;
		$season = (int)$season;

		$result = $this->call("series/{$serie}/{$season}");

		return isset($result['epi']) ? $result['epi'] : array();
	}

	/**
	 * Get the movies for a serie
	 *
	 * @param int $serie
	 *
	 * @return array
	 */
	public function getMovies($serie)
	{
		return $this->getSeason($serie, 0);
	}

	/**
	 * Get informations for one episode
	 *
	 * @param int $serie
	 * @param int $season
	 * @param int $episode
	 *
	 * @return array
	 */
	public function getEpisode($serie, $season, $episode)
	{
		$serie = (int)$serie;
		$season = (int)$season;
		$episode = (int)$episode;

		$result = $this->call("series/{$serie}/{$season}/{$episode}");

		return isset($result['epi']) ? $result['epi'] : array();
	}

	/**
	 * Get informations for one episode, season or serie, depending on the number of parameters
	 *
	 * @param int $serie
	 * @param int $season
	 * @param int $episode
	 *
	 * @return array
	 */
	public function get($serie, $season = null, $episode = null)
	{
		if ($episode !== null) {
			return $this->getEpisode($serie, $season, $episode);
		} elseif ($season !== null) {
			return $this->getSeason($serie, $season);
		}

		return $this->getSerie($serie);
	}

	/**
	 * Get all hosters for one episode
	 *
	 * @param int $serie
	 * @param int $season
	 * @param int $episode
	 *
	 * @return array
	 */
	public function getHoster($serie, $season, $episode)
	{
		$serie = (int)$serie;
		$season = (int)$season;
		$episode = (int)$episode;

		$result = $this->call("series/{$serie}/{$season}/{$episode}");

		return isset($result['links']) ? $result['links'] : array();
	}

	/**
	 * @param int $id
	 *
	 * @return array
	 */
	public function getVideo($id)
	{
		$id = (int)$id;

		$videoData = $this->call("watch/{$id}");

		// Episode is marked as watched automatically so unmark it
		$this->markAsUnwatched($videoData['epi']);

		return $videoData;
	}

	/**
	 * Mark a specific version as watched
	 *
	 * @param int $id      Either the correct video id or (if 3 parameters are set) the serie id
	 * @param int $season  If the first parameter is the series this would be the season
	 * @param int $episode If the first parameter is the series this would be the episode
	 *
	 * @return bool
	 */
	public function markAsWatched($id, $season = null, $episode = null)
	{
		if (empty($this->sessionId)) {
			return false;
		}

		if ($season !== null && $episode !== null) {
			$hoster = $this->getHoster($id, $season, $episode);
			$randomHoster = array_rand($hoster);
			$id = $hoster[$randomHoster]['id'];
		}

		$id = (int)$id;

		$this->call("watch/{$id}");

		return true;
	}

	/**
	 * Mark a specific version as watched
	 *
	 * @param int $id      Either the correct episode id or (if 3 parameters are set) the serie id
	 * @param int $season  If the first parameter is the series this would be the season
	 * @param int $episode If the first parameter is the series this would be the episode
	 *
	 * @return bool
	 */
	public function markAsUnwatched($id, $season = null, $episode = null)
	{
		if (empty($this->sessionId)) {
			return false;
		}

		if ($season !== null && $episode !== null) {
			$id = $this->getEpisode($id, $season, $episode);
		}

		if (is_array($id) && isset($id['id'])) {
			$id = $id['id'];
		}

		$id = (int)$id;

		$sucess = $this->call("unwatch/{$id}");

		return $sucess['success'];
	}

	/**
	 * Get all favorited series
	 *
	 * @return array
	 */
	public function getFavoriteSeries()
	{
		if (empty($this->sessionId)) {
			return array();
		}

		return $this->call('user/series');
	}

	/**
	 * Set the favorite series. NOTE: This overwrites existing favorites. Use "markAsFavorite" or "unmarkAsFavorite" if
	 * you need to modify one series
	 *
	 * @param array $series
	 *
	 * @return bool
	 */
	public function setFavoriteSeries(array $series)
	{
		if (empty($this->sessionId)) {
			return false;
		}

		$series = implode(',', array_map('intval', $series));

		if (empty($series)) {
			$series = 0;
		}

		$this->call("user/series/set/{$series}");

		return true;
	}

	/**
	 * Login with a given username and password
	 *
	 * @param string $name
	 * @param string $password
	 *
	 * @return string
	 *
	 * @throws Exception
	 */
	public function login($name, $password)
	{
		if (!is_string($name) || !is_string($password)) {
			throw new Exception('Name and Password need to be strings');
		}

		$session = $this->call('login', array(
			'login[user]' => $name,
			'login[pass]' => $password
		));

		$this->setSessionId($session['session']);

		return $session['session'];
	}

	/**
	 * Logout
	 */
	public function logout()
	{
		$this->call('logout');
		$this->setSessionId();
	}

	/**
	 * @param string $system
	 *
	 * @return int|bool Either the newest version as integer or false on failure
	 */
	public function getVersion($system)
	{
		$version = $this->call("version/{$system}");

		return isset($version['version']) ? $version['version'] : false;
	}

	/****************************************
	 ************ Config Helpers ************
	 ****************************************/

	/**
	 * Login constructor. If only the first parameter is set it'll be used as session id
	 *
	 * @param string $login Either the username (if password is set) or the session id previously generated
	 * @param string $password
	 */
	public function __construct($login = null, $password = null)
	{
		if ($login !== null) {
			// Login
			if ($password != null) {
				$this->login($login, $password);
			} // Already logged in
			else {
				$this->setSessionId($login);
			}
		}
	}

	/**
	 * Set the site base URL
	 *
	 * @param string $baseUrl
	 */
	public function setBaseUrl($baseUrl)
	{
		if (!empty($baseUrl)) {
			$this->baseUrl = $baseUrl;
		}
	}

	/**
	 * @return string
	 */
	public function getBaseUrl()
	{
		return $this->baseUrl;
	}

	/**
	 * Set the base API URL
	 *
	 * @param string $baseApiUrl
	 */
	public function setApiUrl($baseApiUrl)
	{
		if (!empty($baseApiUrl)) {
			$this->baseApiUrl = $baseApiUrl;
		}
	}

	/**
	 * @return string
	 */
	public function getApiUrl()
	{
		return $this->baseApiUrl;
	}

	/**
	 * Set the cover url. Needs to contain "{id}" which will be replaced with the actual id
	 *
	 * @param string $coverUrl
	 */
	public function setCoverUrl($coverUrl)
	{
		if (!empty($coverUrl) && strpos($coverUrl, '{id}') !== false) {
			$this->coverUrl = $coverUrl;
		}
	}

	/**
	 * @return string
	 */
	public function getCoverUrl()
	{
		return $this->coverUrl;
	}

	/**
	 * Set the session id for this instance
	 *
	 * @param string $sessionId
	 */
	public function setSessionId($sessionId = null)
	{
		$this->sessionId = $sessionId;
	}

	/**
	 * @return string
	 */
	public function getSessionId()
	{
		return $this->sessionId;
	}

	/******************************************
	 ************ Internal Helpers ************
	 ******************************************/

	/**
	 * Build a site URI
	 *
	 * @param string $link Relative to the base url
	 *
	 * @return string
	 */
	protected function buildLink($link)
	{
		return $this->baseUrl . $link;
	}

	/**
	 * Helper to allow to specify the serie differently
	 *
	 * @param array|int|string $serie Either the series array, it's id or the name of it
	 *
	 * @return array
	 *
	 * @throws Exception
	 */
	protected function getSerieObject($serie)
	{
		// It's an array which seems to be a series? Good
		if (is_array($serie) && isset($serie['id'])) {
			return $serie;
		}

		// If it's an integer we'll assume that it's an ID
		if (is_int($serie)) {
			$serie = $this->getSerie($serie);

			if (empty($serie)) {
				throw new Exception("Error getting the series: <pre>" . print_r($serie, true) . "</pre>");
			}

			return $serie;
		}

		// A string? Guess that's a title we need to search
		if (is_string($serie)) {
			$result = $this->search($serie, true);

			// No series with that name
			if (empty($result)) {
				// Probably it was an ID?
				if ((int)$serie == $serie && strlen((int)$serie) == strlen($serie)) {
					return $this->getSerieObject((int)$serie);
				}

				throw new Exception("Couldn't find a Serie with that name");
			}

			return $this->getSerie($result['id']);
		}

		// Still here?
		throw new Exception('Serie needs to be either an integer, a string or an array');
	}

	/**
	 * Perform the actual API call
	 *
	 * @param string        $link Relative to the API base url
	 * @param boolean|array $post Either a boolean to specify whether it's a post request or an array with post fields
	 *
	 * @return array
	 */
	protected function call($link, $post = false)
	{
		$link = $this->baseApiUrl . $link;

		if ($this->sessionId !== null) {
			$link .= '?s=' . $this->sessionId;
		}

		$this->calls++;

		$ch = curl_init($link);
		curl_setopt($ch, CURLOPT_RETURNTRANSFER, true);
		curl_setopt($ch, CURLOPT_BINARYTRANSFER, true);

		if ($post !== false) {
			$this->postCalls++;

			curl_setopt($ch, CURLOPT_POST, true);

			if (is_array($post)) {
				curl_setopt($ch, CURLOPT_POSTFIELDS, $post);
			}
		}

		$request = @curl_exec($ch);

		curl_close($ch);

		if (@gzdecode($request) !== false) {
			$request = @gzdecode($request);
		}

		return json_decode($request, true);
	}

	/**
	 * @param array $a
	 * @param array $b
	 *
	 * @return int
	 */
	protected function sortEpisodes($a, $b)
	{
		if ($a['epi'] == $b['epi']) {
			return 0;
		}

		return ($a['epi'] < $b['epi']) ? -1 : 1;
	}

	/**
	 * @param array $a
	 * @param array $b
	 *
	 * @return int
	 */
	protected function sortById($a, $b)
	{
		if ($a['id'] == $b['id']) {
			return 0;
		}

		return ($a['id'] > $b['id']) ? -1 : 1;
	}

	/*****************************************
	 ************* Debug Helpers *************
	 *****************************************/

	/**
	 * @return int
	 */
	public function getNumCalls()
	{
		return $this->calls;
	}

	/**
	 * @return int
	 */
	public function getNumPostCalls()
	{
		return $this->postCalls;
	}

	/**
	 * @return int
	 */
	public function getNumGetCalls()
	{
		return $this->calls - $this->postCalls;
	}
}
