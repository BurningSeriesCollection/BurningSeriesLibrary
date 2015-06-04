package de.jonesboard.burningseries.interfaces;

public interface HosterInterface {
	// Hoster Name
	public void setHoster(String hoster);
	public String getName();
	
	// Which part is it?
	public void setPart(String part);
	public int getPart();
	
	// Internal ID
	public void setId(String id);
	public int getId();
	
	// Part of the URL
	public void setUrl(String url);
	public String getUrl();
	
	// Episode (internal ID)
	public void setEpi(String epi);
	public int getEpisode();
	
	// Full url
	public void setFullurl(String fullurl);
	public String getFullurl();
}
