package de.jonesboard.burningseries.interfaces;

import java.util.HashMap;

public interface SerieInterface extends Comparable<SerieInterface> {

	// Series ID
	
	/**
	 * @param id
	 */
	public void setId(int id);
	
	/**
	 * @return
	 */
	public int getId();


	// Name of serie
	
	/**
	 * @param name
	 */
	public void setSeries(String name);
	
	/**
	 * @param name
	 */
	public void setName(String name);
	
	/**
	 * @return
	 */
	public String getName();
	

	// Serie url
	
	/**
	 * @param url
	 */
	public void setUrl(String url);
	
	/**
	 * @return
	 */
	public String getUrl();
	

	// Description
	
	/**
	 * @param description
	 */
	public void setDescription(String description);
	
	/**
	 * @return
	 */
	public String getDescription();
	

	// Start Date
	
	/**
	 * @param start
	 */
	public void setStart(int start);
	
	/**
	 * @return
	 */
	public int getStart();
	

	// End Date
	
	/**
	 * @param end
	 */
	public void setEnd(int end);
	
	/**
	 * @return
	 */
	public int getEnd();
	

	// Has movie?
	
	/**
	 * @param movies
	 */
	public void setMovies(String movies);
	
	/**
	 * @return
	 */
	public boolean hasMovies();
	

	// Seasons

	/**
	 * @param seasons
	 */
	public void setSeasons(int seasons);
	
	/**
	 * @return
	 */
	public int getSeasons();
	
	
	// Data Array
	
	/**
	 * @param data
	 */
	public void setData(HashMap<String, Object> data);
	
	/**
	 * @return
	 */
	public String[] getProducer();
	
	/**
	 * @return
	 */
	public String[] getDirector();
	
	/**
	 * @return
	 */
	public String[] getAuthor();
	
	/**
	 * @return
	 */
	public String[] getActor();
	
	/**
	 * @return
	 */
	public String getMainGenre();
	
	/**
	 * @return
	 */
	public String[] getGenre();
}
