package de.jonesboard.burningseries.interfaces;

public interface HosterInterface {

	// Hoster Name
	
	/**
	 * @param hoster
	 */
	public void setHoster(String hoster);
	
	/**
	 * @return
	 */
	public String getName();

	
	// Which part is it?
	
	/**
	 * @param part
	 */
	public void setPart(int part);
	
	/**
	 * @return
	 */
	public int getPart();
	

	// Internal ID
	
	/**
	 * @param id
	 */
	public void setId(int id);
	
	/**
	 * @return
	 */
	public int getId();
	

	// Part of the URL
	
	/**
	 * @param url
	 */
	public void setUrl(String url);
	
	/**
	 * @return
	 */
	public String getUrl();
	

	// Episode (internal ID)
	
	/**
	 * @param epi
	 */
	public void setEpi(int epi);
	
	/**
	 * @return
	 */
	public int getEpisode();
	

	// Full url
	
	/**
	 * @param fullurl
	 */
	public void setFullurl(String fullurl);

	/**
	 * @return
	 */
	public String getFullurl();
}
