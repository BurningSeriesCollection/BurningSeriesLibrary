package de.jonesboard.burningseries;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Random;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import de.jonesboard.burningseries.classes.Episode;
import de.jonesboard.burningseries.classes.Hoster;
import de.jonesboard.burningseries.classes.Season;
import de.jonesboard.burningseries.classes.Serie;
import de.jonesboard.burningseries.interfaces.EpisodeInterface;
import de.jonesboard.burningseries.interfaces.HosterInterface;
import de.jonesboard.burningseries.interfaces.SeasonInterface;
import de.jonesboard.burningseries.interfaces.SerieInterface;

public class BurningSeries {
	private String baseUrl = "http://bs.to/serie/";
	private String baseApiUrl = "http://bs.to/api/";
	private String coverUrl = "//s.bs.to/img/cover/{id}.jpg";
	private String sessionId = null;
	
	public static final int SORT_ALPHABETICAL = 0;
	public static final int SORT_NEWEST = 1;
	
	public static final String ANDROID = "android";
	
	private int calls = 0;
	private int postCalls = 0;

	private boolean enableCaching = true;
	private HashMap<String, String> cache = new HashMap<String, String>();
	private String[] dontCache = {
				"watch",
				"unwatch",
				"user/series/set",
				"login",
				"logout"
	};
	
	private static ObjectMapper mapper = new ObjectMapper(); // can reuse, share globally

	
	/*****************************************
	 ************ General Helpers ************
	 *****************************************/
	
	public SerieInterface[] search(String name, boolean exact) throws Exception
	{
		SerieInterface[] series = this.getSeries();
		
		ArrayList<SerieInterface> result = new ArrayList<SerieInterface>();
		
		for(SerieInterface serie : series) {
			if(!exact && serie.getName().toLowerCase().indexOf(name.toLowerCase()) > -1) {
				result.add(serie);
			} else if(exact && serie.getName().toLowerCase().equals(name.toLowerCase())) {
				result.add(serie);
			}
		}

		// If we're searching exact names we only expect one result
		if (exact && result.size() > 1) {
			return new SerieInterface[0];
		}

		return mapper.convertValue(result, Serie[].class);
	}
	
	public SerieInterface[] search(String name) throws Exception
	{
		return this.search(name, false);
	}
	
	public SerieInterface getByName(String name) throws Exception
	{
		SerieInterface[] serie = this.search(name, true);
		
		if(serie.length != 1) {
			return null;
		}
		
		return this.getSerie(serie[0].getId());
	}
	
	public String buildSerieUrl(SerieInterface serie)
	{
		if(serie.getUrl().equals("")) {
			return "";
		}
		
		return this.buildLink(serie.getUrl());
	}

	public String buildSerieUrl(int serie) throws Exception
	{
		return this.buildSerieUrl(this.getSerie(serie));
	}
	
	public String buildSerieUrl(String serie) throws Exception
	{
		return this.buildSerieUrl(this.getByName(serie));
	}
	
	public String buildSeasonUrl(SerieInterface serie, int season)
	{
		return this.buildSerieUrl(serie) + "/" + season;
	}
	
	public String buildSeasonUrl(int serie, int season) throws Exception
	{
		return this.buildSeasonUrl(this.getSerie(serie), season);
	}
	
	public String buildSeasonUrl(String serie, int season) throws Exception
	{
		return this.buildSeasonUrl(this.getByName(serie), season);
	}
	
	public String buildEpisodeUrl(SerieInterface serie, int season, int episode)
	{
		return this.buildSeasonUrl(serie, season) + "/" + episode + "-Episode";
	}
	
	public String buildEpisodeUrl(int serie, int season, int episode) throws Exception
	{
		return this.buildEpisodeUrl(this.getSerie(serie), season, episode);
	}
	
	public String buildEpisodeUrl(String serie, int season, int episode) throws Exception
	{
		return this.buildEpisodeUrl(this.getByName(serie), season, episode);
	}
	
	// TODO: buildUrl function (9 functions)

	public String getCover(int serie)
	{
		return this.coverUrl.replace("{id}", String.valueOf(serie));
	}
	
	public String getCover(SerieInterface serie)
	{
		return this.getCover(serie.getId());
	}
	
	public String getCover(String serie) throws Exception
	{
		return this.getCover(this.getByName(serie));
	}
	
	public String[] getGenres() throws Exception
	{
		return mapper.convertValue(this.getByGenre().keySet().toArray(), String[].class);
	}
	
	public boolean hasWatched(int serie, int season, int episode) throws Exception
	{
		if(this.sessionId == null) {
			return false;
		}
		
		SeasonInterface seasonObject = this.getSeason(serie, season);
		
		for(EpisodeInterface episodeObject : seasonObject.getEpisodes()) {
			if(episodeObject.getEpisodeNumber() != episode) {
				continue;
			}
			
			return episodeObject.hasWatched();
		}
		
		return false;
	}
	
	public boolean hasWatched(SerieInterface serie, int season, int episode) throws Exception
	{
		return this.hasWatched(serie.getId(), season, episode);
	}
	
	public boolean hasWatched(String serie, int season, int episode) throws Exception
	{
		return this.hasWatched(this.getByName(serie), season, episode);
	}
	
	public EpisodeInterface getNextUnwatchedEpisode(SerieInterface serie, int season, int offset) throws Exception
	{
		ArrayList<Integer> seasons = new ArrayList<Integer>();
		
		if(season > -1) {
			seasons.add(season);
		} else {
			for(int i = 1; i <= serie.getSeasons(); i++) {
				seasons.add(i);
			}
		}
		
		for(int testSeason : seasons) {
			EpisodeInterface[] episodes = this.getSeason(serie.getId(), testSeason).getEpisodes();
			Arrays.sort(episodes);

			if(offset > -1) {
				episodes = Arrays.copyOfRange(episodes, offset, episodes.length);
			}
			
			for(EpisodeInterface episode : episodes) {
				if(episode.hasWatched()) {
					continue;
				}
				
				return this.getEpisode(serie.getId(), season, episode.getEpisodeNumber());
			}
		}
		
		return null;
	}
	
	public EpisodeInterface getNextUnwatchedEpisode(int serie, int season, int offset) throws Exception
	{
		return this.getNextUnwatchedEpisode(this.getSerie(serie), season, offset);
	}
	
	public EpisodeInterface getNextUnwatchedEpisode(String serie, int season, int offset) throws Exception
	{
		return this.getNextUnwatchedEpisode(this.getByName(serie), season, offset);
	}

	public EpisodeInterface getNextUnwatchedEpisode(int serie, int season) throws Exception
	{
		return this.getNextUnwatchedEpisode(serie, season, -1);
	}
	
	public EpisodeInterface getNextUnwatchedEpisode(SerieInterface serie, int season) throws Exception
	{
		return this.getNextUnwatchedEpisode(serie, season, -1);
	}
	
	public EpisodeInterface getNextUnwatchedEpisode(String serie, int season) throws Exception
	{
		return this.getNextUnwatchedEpisode(serie, season, -1);
	}
	
	public EpisodeInterface getNextUnwatchedEpisode(int serie) throws Exception
	{
		return this.getNextUnwatchedEpisode(serie, -1, -1);
	}
	
	public EpisodeInterface getNextUnwatchedEpisode(SerieInterface serie) throws Exception
	{
		return this.getNextUnwatchedEpisode(serie, -1, -1);
	}
	
	public EpisodeInterface getNextUnwatchedEpisode(String serie) throws Exception
	{
		return this.getNextUnwatchedEpisode(serie, -1, -1);
	}
	
	public EpisodeInterface getNextUnwatchedMovie(int serie) throws Exception
	{
		return this.getNextUnwatchedEpisode(serie, 0);
	}
	
	public EpisodeInterface getNextUnwatchedMovie(SerieInterface serie) throws Exception
	{
		return this.getNextUnwatchedEpisode(serie, 0);
	}
	
	public EpisodeInterface getNextUnwatchedMovie(String serie) throws Exception
	{
		return this.getNextUnwatchedEpisode(serie, 0);
	}
	
	public boolean markAsFavorite(int serie) throws Exception
	{
		if(this.sessionId == null) {
			return false;
		}
		
		SerieInterface[] favorites = this.getFavoriteSeries();
		ArrayList<Integer> favoriteIds = new ArrayList<Integer>();
		for(SerieInterface favorite : favorites) {
			favoriteIds.add(favorite.getId());
		}
		
		if(favoriteIds.contains(serie)) {
			return true;
		}
		
		favoriteIds.add(serie);
		
		return this.setFavoriteSeries(favoriteIds);
	}
	
	public boolean markAsFavorite(SerieInterface serie) throws Exception
	{
		return this.markAsFavorite(serie.getId());
	}
	
	public boolean markAsFavorite(String serie) throws Exception
	{
		return this.markAsFavorite(this.getByName(serie));
	}
	
	public boolean unmarkAsFavorite(int serie) throws Exception
	{
		if(this.sessionId == null) {
			return false;
		}
		
		SerieInterface[] favorites = this.getFavoriteSeries();
		
		ArrayList<Integer> favoriteIds = new ArrayList<Integer>();
		boolean isFavorite = false;
		for(SerieInterface favorite : favorites) {
			if(favorite.getId() == serie) {
				isFavorite = true;
				continue;
			}
			
			favoriteIds.add(favorite.getId());
		}
		
		if(!isFavorite) {
			return true;
		}
		
		return this.setFavoriteSeries(favoriteIds);
	}
	
	public boolean unmarkAsFavorite(SerieInterface serie) throws Exception
	{
		return this.unmarkAsFavorite(serie.getId());
	}
	
	public boolean unmarkAsFavorite(String serie) throws Exception
	{
		return this.unmarkAsFavorite(this.getByName(serie));
	}
	
	public boolean isFavoritedSerie(int serie) throws Exception
	{
		if(this.sessionId == null) {
			return false;
		}
		
		SerieInterface[] favorites = this.getFavoriteSeries();
		
		for(SerieInterface favorite : favorites) {
			if(favorite.getId() == serie) {
				return true;
			}
		}
		
		return false;
	}
	
	public boolean isFavoritedSerie(SerieInterface serie) throws Exception
	{
		return this.isFavoritedSerie(serie.getId());
	}
	
	public boolean isFavoritedSerie(String serie) throws Exception
	{
		return this.isFavoritedSerie(this.getByName(serie));
	}
	

	/*************************************
	 ************ General API ************
	 *************************************/
	
	public SerieInterface[] getSeries(int sort) throws Exception
	{
		SerieInterface[] series = BurningSeries.mapper.readValue(this.call("series"), Serie[].class);
		
		if(sort == BurningSeries.SORT_NEWEST) {
			Arrays.sort(series);
		}
		
		return series;
	}
	
	public SerieInterface[] getSeries() throws Exception
	{
		return this.getSeries(BurningSeries.SORT_ALPHABETICAL);
	}

	public HashMap<String, SerieInterface[]> getByGenre() throws Exception
	{
		HashMap<String, HashMap<String, Object>> temp = BurningSeries.mapper.readValue(this.call("series:genre"), new TypeReference<HashMap<String, HashMap<String, Object>>>() {});
		
		HashMap<String, SerieInterface[]> returnValue = new HashMap<String, SerieInterface[]>();
		
		for(Entry<String, HashMap<String, Object>> entry : temp.entrySet()) {
			HashMap<String, Object> value = (HashMap<String, Object>) entry.getValue();

			Serie[] series = mapper.convertValue(value.get("series"), Serie[].class);
			returnValue.put(entry.getKey(), series);
		}
		
		return returnValue;
	}
	
	public SerieInterface[] getByGenre(int genre)
	{
		// TODO
		return new Serie[0];
	}
	
	public SerieInterface[] getByGenre(String genre) throws Exception
	{
		HashMap<String, SerieInterface[]> genres = this.getByGenre();
		
		if(genres.containsKey(genre)) {
			return genres.get(genre);
		}
		
		return new Serie[0];
	}
	
	public SerieInterface[] getNewest() throws Exception
	{
		return this.getSeries(BurningSeries.SORT_NEWEST);
	}
	
	public SerieInterface getSerie(int serie) throws Exception
	{
		HashMap<String, Object> temp = BurningSeries.mapper.readValue(this.call("series/" + serie + "/1"), new TypeReference<HashMap<String, Object>>() {});
		return mapper.convertValue(temp.get("series"), Serie.class);
	}
	
	public SeasonInterface getSeason(int serie, int season) throws Exception
	{
		HashMap<String, Object> temp = BurningSeries.mapper.readValue(this.call("series/" + serie + "/" + season), new TypeReference<HashMap<String, Object>>() {});
		EpisodeInterface[] episodes = mapper.convertValue(temp.get("epi"), Episode[].class);
		
		SeasonInterface seasonObject = new Season();
		seasonObject.setEpi(episodes);
		seasonObject.setSeason(season);
		return seasonObject;
	}
	
	public SeasonInterface getMovies(int serie) throws Exception
	{
		return this.getSeason(serie, 0);
	}
	
	public EpisodeInterface getEpisode(int serie, int season, int episode) throws Exception
	{
		HashMap<String, Object> temp = BurningSeries.mapper.readValue(this.call("series/" + serie + "/" + season + "/" + episode), new TypeReference<HashMap<String, Object>>() {});
		EpisodeInterface episodeObject = mapper.convertValue(temp.get("epi"), Episode.class);
		episodeObject.setEpi(episode);
		return episodeObject;
	}
	
	// TODO: Check whether "get" function can be added
	
	public HosterInterface[] getHoster(int serie, int season, int episode) throws Exception
	{
		HashMap<String, Object> temp = BurningSeries.mapper.readValue(this.call("series/" + serie + "/" + season + "/" + episode), new TypeReference<HashMap<String, Object>>() {});
		return mapper.convertValue(temp.get("links"), Hoster[].class);
	}
	
	public HosterInterface getVideo(int id) throws Exception
	{
		HosterInterface hoster = BurningSeries.mapper.readValue(this.call("watch/" + id), Hoster.class);
		this.markAsUnwatched(hoster.getEpisode());
		return hoster;
	}
	
	public boolean markAsWatched(int id) throws Exception
	{
		if(this.sessionId == null) {
			return false;
		}
		
		this.call("watch/" + id);
		
		return true;
	}
	
	public boolean markAsWatched(int serie, int season, int episode) throws Exception
	{
		HosterInterface[] hoster = this.getHoster(serie, season, episode);
		int randomHoster = new Random().nextInt(hoster.length);
		return this.markAsWatched(hoster[randomHoster].getId());
	}
	
	public boolean markAsUnwatched(int id) throws Exception
	{
		if(this.sessionId == null) {
			return false;
		}
		
		HashMap<String, Boolean> temp = BurningSeries.mapper.readValue(this.call("unwatch/" + id), new TypeReference<HashMap<String, Boolean>>() {});
		
		return temp.get("success");
	}
	
	public boolean markAsUnwatched(int serie, int season, int episode) throws Exception
	{
		EpisodeInterface episodeObj = this.getEpisode(serie, season, episode);
		return this.markAsUnwatched(episodeObj.getId());
	}
	
	public SerieInterface[] getFavoriteSeries() throws Exception
	{
		if(this.sessionId == null) {
			return new Serie[0];
		}
		
		return BurningSeries.mapper.readValue(this.call("user/series"), Serie[].class);	
	}
	
	public boolean setFavoriteSeries(int[] series) throws Exception
	{
		if(this.sessionId == null) {
			return false;
		}
		
		String seriesParam = "";
		String glue = "";
		for(int serie : series) {
			seriesParam += glue + serie;
			glue = ",";
		}
		
		if(seriesParam.equals("")) {
			seriesParam = "0";
		}
		
		this.call("user/series/set/" + seriesParam);
		
		return true;
	}
	
	public boolean setFavoriteSeries(SerieInterface[] series) throws Exception
	{
		ArrayList<Integer> favorites = new ArrayList<Integer>();
		for(SerieInterface serie : series) {
			favorites.add(serie.getId());
		}
		return this.setFavoriteSeries(favorites);
	}
	
	public boolean setFavoriteSeries(ArrayList<Integer> series) throws Exception
	{
		int[] favorites = new int[series.size()];
		for(int i = 0; i < series.size(); i++) {
			favorites[i] = series.get(i);
		}
		return this.setFavoriteSeries(favorites);
	}
	
	public String login(String name, String password) throws Exception
	{
		HashMap<String, String> login = new HashMap<String, String>();
		login.put("login[user]", name);
		login.put("login[pass]", password);
		
		HashMap<String, String> response = BurningSeries.mapper.readValue(this.call("login", login), new TypeReference<HashMap<String, String>>() {});

		this.setSessionId(response.get("session"));
		
		return response.get("session");
	}
	
	public void logout()
	{
		this.call("logout");
		this.setSessionId();
	}
	
	public int getVersion(String system) throws Exception
	{
		HashMap<String, Integer> temp = BurningSeries.mapper.readValue(this.call("version/" + system), new TypeReference<HashMap<String, Integer>>() {});
		return temp.get("version");
	}
	
	/****************************************
	 ************ Config Helpers ************
	 ****************************************/
	
	public BurningSeries()
	{
		mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
	};
	
	public BurningSeries(String session)
	{
		this();
		this.setSessionId(session);
	}
	
	public BurningSeries(String name, String password)
	{
		this();
		
		// Don't throw errors in the constructor (better error handling will be added later anyways)
		try {
			this.login(name, password);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void setBaseUrl(String baseUrl)
	{
		if(baseUrl != "") {
			this.baseUrl = baseUrl;
		}
	}
	
	public String getBaseUrl()
	{
		return this.baseUrl;
	}
	
	public void setApiUrl(String baseApiUrl)
	{
		if(baseApiUrl != "") {
			this.baseApiUrl = baseApiUrl;
		}
	}
	
	public String getApiUrl()
	{
		return this.baseApiUrl;
	}
	
	public void setCoverUrl(String coverUrl)
	{
		if(coverUrl != "" && coverUrl.indexOf("{id}") != -1) {
			this.coverUrl = coverUrl;
		}
	}
	
	public String getCoverUrl()
	{
		return this.coverUrl;
	}
	
	public void setSessionId(String sessionId)
	{
		this.sessionId = sessionId;
	}
	
	public void setSessionId()
	{
		this.setSessionId(null);
	}
	
	public String getSessionId()
	{
		return this.sessionId;
	}
	
	public static ObjectMapper getMapper()
	{
		return mapper;
	}
	
	/******************************************
	 ************ Internal Helpers ************
	 ******************************************/
	
	protected String buildLink(String link)
	{
		return this.baseUrl + link;
	}
	
	@SuppressWarnings("deprecation")
	protected String call(String link, HashMap<String, String> post)
	{
		// Only return cache if caching is enabled, this url should be cached, has a cache and if it's not a post
		if (this.isCaching() && this.shouldBeCached(link) && this.hasCache(link) && post.size() == 0) {
			return this.getCache(link);
		}

		String cacheName = link;
		
		link = this.baseApiUrl + link;
		
		if(this.sessionId != null) {
			link += "?s=" + this.sessionId;
		}

		this.calls++;
		
		HttpURLConnection connection = null;
		try {
			URL url = new URL(link);
			connection = (HttpURLConnection) url.openConnection();
			connection.setUseCaches(false);
			connection.setDoOutput(true);
			connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
			connection.setRequestProperty("User-Agent", "Java Library");

			String urlParameter = "";
			if(post.size() > 0) {
				this.postCalls++;

				String add = "";
				for(Entry<String, String> entry : post.entrySet()) {
					urlParameter += add + entry.getKey() + "=" + URLEncoder.encode(entry.getValue());
					add = "&";
				}
				
				connection.setRequestMethod("POST");
			} else {
				connection.setRequestMethod("GET");
			}

			connection.setRequestProperty("Content-Length", Integer.toString(urlParameter.getBytes().length));
			
			DataOutputStream wr = new DataOutputStream (connection.getOutputStream());
			wr.writeBytes(urlParameter);
			wr.close();

			//Get Response  
			InputStream is = connection.getInputStream();
			BufferedReader rd = new BufferedReader(new InputStreamReader(is));
			StringBuilder response = new StringBuilder();
			String line;
			while((line = rd.readLine()) != null) {
				response.append(line);
				response.append('\r');
			}
			rd.close();
			String cache = response.toString();
			
			// Only cache if caching is enabled, this url should be cached and if it's not a post request
			if (this.isCaching() && this.shouldBeCached(cacheName) && post.size() == 0) {
				this.putCache(cacheName, cache);
			}
			
			return cache;
		} catch (IOException e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
			return null;
		} finally {
			if(connection != null) {
				connection.disconnect(); 
			}
		}
	}
	
	protected String call(String link)
	{
		return call(link, new HashMap<String, String>());
	}

	/*****************************************
	 ************ Caching Helpers ************
	 *****************************************/

	private void putCache(String url, String data)
	{
		this.cache.put(url, data);
	}
	
	private boolean hasCache(String url)
	{
		return this.cache.containsKey(url);
	}
	
	private String getCache(String url)
	{
		return this.cache.get(url);
	}
	
	private boolean shouldBeCached(String url)
	{
		for(String notCache : this.dontCache) {
			if(url.indexOf(notCache) == 0) {
				return false;
			}
		}
		
		return true;
	}
	
	public void invalidateCache(String url)
	{
		this.cache.remove(url);
	}
	
	public void disableCache()
	{
		this.enableCaching = false;
	}
	
	public void enableCache()
	{
		this.enableCaching = true;
	}
	
	public boolean isCaching()
	{
		return this.enableCaching;
	}

	/*****************************************
	 ************* Debug Helpers *************
	 *****************************************/
	
	public int getNumCalls()
	{
		return this.calls;
	}
	
	public int getNumPostCalls()
	{
		return this.postCalls;
	}
	
	public int getNumGetCalls()
	{
		return this.calls - this.postCalls;
	}
	
	
	
	
}
