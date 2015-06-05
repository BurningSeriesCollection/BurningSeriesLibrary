package de.jonesboard.burningseries.interfaces;

public interface SeasonInterface {
	
	// Episodes
	
	/**
	 * @param episodes
	 */
	public void setEpi(EpisodeInterface[] episodes);
	
	/**
	 * @return
	 */
	public EpisodeInterface[] getEpisodes();

	
	// Season number
	
	/**
	 * @param season
	 */
	public void setSeason(int season);
	
	/**
	 * @return
	 */
	public int getSeason();
}
