package de.jonesboard.burningseries;

import java.util.Random;
import de.jonesboard.burningseries.interfaces.*;

public class BurningSeries {
	private String baseUrl = "http://bs.to/serie/";
	private String baseApiUrl = "http://bs.to/api/";
	private String coverUrl = "//s.bs.to/img/cover/{id}.jpg";
	private String sessionId = null;
	
	public final int SORT_ALPHABETICAL = 0;
	public final int SORT_GENRE = 1;
	public final int SORT_NEWEST = 2;
	
	public final String ANDROID = "android";
	
	private int calls = 0;
	private int postCalls = 0;


	/*****************************************
	 ************ General Helpers ************
	 *****************************************/
	
	public Serie[] search(String name, boolean exact)
	{
		// TODO
	}
	
	public Serie[] search(String name)
	{
		return this.search(name, false);
	}
	
	public Serie getByName(String name)
	{
		Serie[] serie = this.search(name, true);
		
		return this.getSerie(serie[0].getId());
	}
	
	public String buildSerieUrl(Serie serie)
	{
		// TODO
	}

	public String buildSerieUrl(int serie)
	{
		return this.buildSerieUrl(this.getSerie(serie));
	}
	
	public String buildSerieUrl(String serie)
	{
		return this.buildSerieUrl(this.getByName(serie));
	}
	
	public String buildSeasonUrl(Serie serie, int season)
	{
		// TODO
	}
	
	public String buildSeasonUrl(int serie, int season)
	{
		return this.buildSeasonUrl(this.getSerie(serie), season);
	}
	
	public String buildSeasonUrl(String serie, int season)
	{
		return this.buildSeasonUrl(this.getByName(serie), season);
	}
	
	public String buildEpisodeUrl(Serie serie, int season, int episode)
	{
		// TODO
	}
	
	public String buildEpisodeUrl(int serie, int season, int episode)
	{
		return this.buildEpisodeUrl(this.getSerie(serie), season, episode);
	}
	
	public String buildEpisodeUrl(String serie, int season, int episode)
	{
		return this.buildEpisodeUrl(this.getByName(serie), season, episode);
	}
	
	// TODO: buildUrl function (9 functions)

	public String getCover(int serie)
	{
		// TODO
	}
	
	public String getCover(Serie serie)
	{
		return this.getCover(serie.getId());
	}
	
	public String getCover(String serie)
	{
		return this.getCover(this.getByName(serie));
	}
	
	public String[] getGenres()
	{
		// TODO
	}
	
	public boolean hasWatched(int serie, int season, int episode)
	{
		// TODO
	}
	
	public boolean hasWatched(Serie serie, int season, int episode)
	{
		return this.hasWatched(serie.getId(), season, episode);
	}
	
	public boolean hasWatched(String serie, int season, int episode)
	{
		return this.hasWatched(this.getByName(serie), season, episode);
	}
	
	public Episode getNextUnwatchedEpisode(int serie, int season, int offset)
	{
		// TODO
	}
	
	public Episode getNextUnwatchedEpisode(Serie serie, int season, int offset)
	{
		return this.getNextUnwatchedEpisode(serie.getId(), season, offset);
	}
	
	public Episode getNextUnwatchedEpisode(String serie, int season, int offset)
	{
		return this.getNextUnwatchedEpisode(this.getByName(serie), season, offset);
	}

	public Episode getNextUnwatchedEpisode(int serie, int season)
	{
		return this.getNextUnwatchedEpisode(serie, season, -1);
	}
	
	public Episode getNextUnwatchedEpisode(Serie serie, int season)
	{
		return this.getNextUnwatchedEpisode(serie, season, -1);
	}
	
	public Episode getNextUnwatchedEpisode(String serie, int season)
	{
		return this.getNextUnwatchedEpisode(serie, season, -1);
	}
	
	public Episode getNextUnwatchedEpisode(int serie)
	{
		return this.getNextUnwatchedEpisode(serie, -1, -1);
	}
	
	public Episode getNextUnwatchedEpisode(Serie serie)
	{
		return this.getNextUnwatchedEpisode(serie, -1, -1);
	}
	
	public Episode getNextUnwatchedEpisode(String serie)
	{
		return this.getNextUnwatchedEpisode(serie, -1, -1);
	}
	
	public Episode getNextUnwatchedMovie(int serie)
	{
		return this.getNextUnwatchedEpisode(serie, 0);
	}
	
	public Episode getNextUnwatchedMovie(Serie serie)
	{
		return this.getNextUnwatchedEpisode(serie, 0);
	}
	
	public Episode getNextUnwatchedMovie(String serie)
	{
		return this.getNextUnwatchedEpisode(serie, 0);
	}
	
	public boolean markAsFavorite(int serie)
	{
		// TODO
	}
	
	public boolean markAsFavorite(Serie serie)
	{
		return this.markAsFavorite(serie.getId());
	}
	
	public boolean markAsFavorite(String serie)
	{
		return this.markAsFavorite(this.getByName(serie));
	}
	
	public boolean unmarkAsFavorite(int serie)
	{
		// TODO
	}
	
	public boolean unmarkAsFavorite(Serie serie)
	{
		return this.unmarkAsFavorite(serie.getId());
	}
	
	public boolean unmarkAsFavorite(String serie)
	{
		return this.unmarkAsFavorite(this.getByName(serie));
	}
	
	public boolean isFavoritedSerie(int serie)
	{
		// TODO
	}
	
	public boolean isFavoritedSerie(Serie serie)
	{
		return this.isFavoritedSerie(serie.getId());
	}
	
	public boolean isFavoritedSerie(String serie)
	{
		return this.isFavoritedSerie(this.getByName(serie));
	}
	

	/*************************************
	 ************ General API ************
	 *************************************/
	
	public Serie[] getSeries(int sort)
	{
		// TODO
	}
	
	public Serie[] getSeries()
	{
		return this.getSeries(this.SORT_ALPHABETICAL);
	}

	public Serie[] getByGenre()
	{
		return this.getSeries(this.SORT_GENRE);
	}
	
	public Serie[] getByGenre(int genre)
	{
		// TODO
	}
	
	public Serie[] getByGenre(String genre)
	{
		// TODO
	}
	
	public Serie[] getNewest()
	{
		return this.getSeries(this.SORT_NEWEST);
	}
	
	public Serie getSerie(int serie)
	{
		// TODO
	}
	
	public Season getSeason(int serie, int season)
	{
		// TODO
	}
	
	public Season getMovies(int serie)
	{
		return this.getSeason(serie, 0);
	}
	
	public Episode getEpisode(int serie, int season, int episode)
	{
		// TODO
	}
	
	// TODO: Check whether "get" function can be added
	
	public Hoster[] getHoster(int serie, int season, int episode)
	{
		// TODO
	}
	
	public Hoster getVideo(int id)
	{
		// TODO
	}
	
	public boolean markAsWatched(int id)
	{
		// TODO
	}
	
	public boolean markAsWatched(int serie, int season, int episode)
	{
		Hoster[] hoster = this.getHoster(serie, season, episode);
		int randomHoster = new Random().nextInt(hoster.length);
		return this.markAsWatched(hoster[randomHoster].getId());
	}
	
	public boolean markAsUnwatched(int id)
	{
		// TODO
	}
	
	public boolean markAsUnwatched(int serie, int season, int episode)
	{
		Episode episodeObj = this.getEpisode(serie, season, episode);
		return this.markAsUnwatched(episodeObj.getId());
	}
	
	public Serie[] getFavoriteSeries()
	{
		// TODO
	}
	
	public boolean setFavoriteSeries(Serie[] series)
	{
		// TODO
	}
	
	public String login(String name, String password)
	{
		// TODO
	}
	
	public void logout()
	{
		// TODO
	}
	
	public int getVersion(String system)
	{
		// TODO
	}
	
	/****************************************
	 ************ Config Helpers ************
	 ****************************************/
	
	public BurningSeries(String session)
	{
		this.setSessionId(session);
	}
	
	public BurningSeries(String name, String password)
	{
		this.login(name, password);
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
	
	/******************************************
	 ************ Internal Helpers ************
	 ******************************************/
	
	protected String buildLink(String link)
	{
		return this.baseUrl + link;
	}
	
	protected Object call(String link, Object[] post)
	{
		// TODO
	}
	
	protected Object call(String link)
	{
		return call(link, new Object[0]);
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
