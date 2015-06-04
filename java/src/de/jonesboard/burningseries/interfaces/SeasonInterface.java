package de.jonesboard.burningseries.interfaces;

public interface SeasonInterface {
	// Episodes
	public void setEpi(EpisodeInterface[] episodes);
	public EpisodeInterface[] getEpisodes();
	
	// Season number
	public void setSeason(int season);
	public int getSeason();
}
