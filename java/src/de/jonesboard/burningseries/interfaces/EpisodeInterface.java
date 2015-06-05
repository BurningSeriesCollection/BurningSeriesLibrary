package de.jonesboard.burningseries.interfaces;

public interface EpisodeInterface extends Comparable<EpisodeInterface> {
	
	// German name
	
	/**
	 * @param german
	 */
	public void setGerman(String german);

	/**
	 * @return
	 */
	public String getGerman();
	
	
	// English name

	/**
	 * @param english
	 */
	public void setEnglish(String english);
	
	/**
	 * @return
	 */
	public String getEnglish();

	
	// Episode number
	
	/**
	 * @param epi
	 */
	public void setEpi(int epi);
	
	/**
	 * @return
	 */
	public int getEpisodeNumber();
	

	// Has watched?
	
	/**
	 * @param watched
	 */
	public void setWatched(String watched);
	
	/**
	 * @return
	 */
	public boolean hasWatched();
	

	// Description
	
	/**
	 * @param description
	 */
	public void setDescription(String description);
	
	/**
	 * @return
	 */
	public String getDescription();
	

	// Internal ID
	
	/**
	 * @param id
	 */
	public void setId(int id);
	
	/**
	 * @return
	 */
	public int getId();
}
