package de.jonesboard.burningseries.interfaces;

import java.util.HashMap;

public interface SerieInterface extends Comparable<SerieInterface> {
	// Series ID
	void setId(int id);
	public int getId();

	// Name of serie
	public void setSeries(String name);
	public void setName(String name);
	public String getName();
	
	// Serie url
	public void setUrl(String url);
	public String getUrl();
	
	// Description
	public void setDescription(String description);
	public String getDescription();
	
	// Start Date
	public void setStart(String start);
	public int getStart();
	
	// End Date
	public void setEnd(String end);
	public int getEnd();
	
	// Has movie?
	public void setMovies(String movies);
	public boolean hasMovies();
	
	// Seasons
	public void setSeasons(String seasons);
	public int getSeasons();
	
	// Data Array
	public void setData(HashMap<String, Object> data);
	public String[] getProducer();
	public String[] getDirector();
	public String[] getAuthor();
	public String[] getActor();
	public String getMainGenre();
	public String[] getGenre();
}
