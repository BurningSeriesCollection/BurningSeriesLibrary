package de.jonesboard.burningseries.classes;

import de.jonesboard.burningseries.interfaces.EpisodeInterface;

public class Episode implements EpisodeInterface {
	private String german;
	private String english;
	private int episode;
	private boolean hasWatched;
	private String description;
	private int id;
	
	@Override
	public void setGerman(String german) {
		this.german = german;
	}

	@Override
	public String getGerman() {
		return this.german;
	}

	@Override
	public void setEnglish(String english) {
		this.english = english;
	}

	@Override
	public String getEnglish() {
		return this.english;
	}

	@Override
	public void setEpi(String epi) {
		this.episode = Integer.parseInt(epi);
	}

	@Override
	public int getEpisodeNumber() {
		return this.episode;
	}

	@Override
	public void setWatched(String watched) {
		this.hasWatched = watched.equals("1");
	}

	@Override
	public boolean hasWatched() {
		return this.hasWatched;
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
	public void setId(String id) {
		this.id = Integer.parseInt(id);
	}

	@Override
	public int getId() {
		return this.id;
	}

	@Override
	public String toString() {
		return "Episode [german=" + german + ", english=" + english
				+ ", episode=" + episode + ", hasWatched=" + hasWatched
				+ ", description=" + description + ", id=" + id + "]";
	}

	@Override
	public int compareTo(EpisodeInterface compare) {
		if( this.getEpisodeNumber() < compare.getEpisodeNumber() )
			return -1;
		if( this.getEpisodeNumber() > compare.getEpisodeNumber() )
			return 1;
		
		return 0;
	}

}
