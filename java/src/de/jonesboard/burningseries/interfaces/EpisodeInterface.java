package de.jonesboard.burningseries.interfaces;

public interface EpisodeInterface extends Comparable<EpisodeInterface> {
	// German name
	public void setGerman(String german);
	public String getGerman();
	
	// English name
	public void setEnglish(String english);
	public String getEnglish();
	
	// Episode number
	public void setEpi(String epi);
	public int getEpisodeNumber();
	
	// Has watched?
	public void setWatched(String watched);
	public boolean hasWatched();
	
	// Description
	public void setDescription(String description);
	public String getDescription();
	
	// Internal ID
	public void setId(String id);
	public int getId();
}
