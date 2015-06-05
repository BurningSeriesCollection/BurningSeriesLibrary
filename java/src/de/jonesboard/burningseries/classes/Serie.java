package de.jonesboard.burningseries.classes;

import java.util.Arrays;
import java.util.HashMap;

import de.jonesboard.burningseries.BurningSeries;
import de.jonesboard.burningseries.interfaces.SerieInterface;


public class Serie implements SerieInterface {
	private int id;
	private String name;
	private String url;
	private String description;
	private int start;
	private int end;
	private boolean hasMovies;
	private int seasons;
	private String[] producer;
	private String[] director;
	private String[] author;
	private String[] actor;
	private String mainGenre;
	private String[] genre;
	
	
	@Override
	public void setId(int id) {
		this.id = id;
	}

	@Override
	public int getId() {
		return this.id;
	}

	@Override
	public void setSeries(String name) {
		this.name = name;
	}

	@Override
	public void setName(String name) {
		this.name = name;
	}

	@Override
	public String getName() {
		return this.name;
	}

	@Override
	public void setUrl(String url) {
		this.url = url;
	}

	@Override
	public String getUrl() {
		return this.url;
	}

	@Override
	public void setDescription(String description) {
		this.description = description;
	}

	@Override
	public String getDescription() {
		return this.description;
	}

	@Override
	public void setStart(String start) {
		if(start != null) {
			this.start = Integer.parseInt(start);
		}
	}

	@Override
	public int getStart() {
		return this.start;
	}

	@Override
	public void setEnd(String end) {
		if(end != null) {
			this.end = Integer.parseInt(end);
		}
	}

	@Override
	public int getEnd() {
		return this.end;
	}

	@Override
	public void setMovies(String movies) {;
		this.hasMovies = movies.equals("1");
	}

	@Override
	public boolean hasMovies() {
		return this.hasMovies;
	}

	@Override
	public void setSeasons(String seasons) {
		this.seasons = Integer.parseInt(seasons);
	}

	@Override
	public int getSeasons() {
		return this.seasons;
	}

	@Override
	public void setData(HashMap<String, Object> data) {
		this.producer = BurningSeries.getMapper().convertValue(data.get("producer"), String[].class);
		this.director = BurningSeries.getMapper().convertValue(data.get("director"), String[].class);
		this.author = BurningSeries.getMapper().convertValue(data.get("author"), String[].class);
		this.actor = BurningSeries.getMapper().convertValue(data.get("actor"), String[].class);
		this.mainGenre = (String) data.get("genre_main");
		this.genre = BurningSeries.getMapper().convertValue(data.get("genre"), String[].class);
	}

	@Override
	public String[] getProducer() {
		return this.producer;
	}

	@Override
	public String[] getDirector() {
		return this.director;
	}

	@Override
	public String[] getAuthor() {
		return this.author;
	}

	@Override
	public String[] getActor() {
		return this.actor;
	}

	@Override
	public String getMainGenre() {
		return this.mainGenre;
	}

	@Override
	public String[] getGenre() {
		return this.genre;
	}

	@Override
	public String toString() {
		return "Serie [id=" + id + ", name=" + name + ", url=" + url
				+ ", description=" + description + ", start=" + start
				+ ", end=" + end + ", hasMovies=" + hasMovies + ", seasons="
				+ seasons + ", producer=" + Arrays.toString(producer)
				+ ", director=" + Arrays.toString(director) + ", author="
				+ Arrays.toString(author) + ", actor=" + Arrays.toString(actor)
				+ ", mainGenre=" + mainGenre + ", genre="
				+ Arrays.toString(genre) + "]";
	}

	@Override
	public int compareTo(SerieInterface compare) {
		if( this.getId() < compare.getId() )
			return 1;
		if( this.getId() > compare.getId() )
			return -1;
		
		return 0;
	}

}
