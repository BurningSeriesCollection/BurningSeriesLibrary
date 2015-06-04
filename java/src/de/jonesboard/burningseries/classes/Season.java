package de.jonesboard.burningseries.classes;

import java.util.Arrays;

import de.jonesboard.burningseries.interfaces.EpisodeInterface;
import de.jonesboard.burningseries.interfaces.SeasonInterface;

public class Season implements SeasonInterface {
	private EpisodeInterface[] episodes;
	private int season;

	@Override
	public void setEpi(EpisodeInterface[] epi) {
		this.episodes = epi;
	}

	@Override
	public EpisodeInterface[] getEpisodes() {
		return this.episodes;
	}

	@Override
	public void setSeason(int season) {
		this.season = season;
	}

	@Override
	public int getSeason() {
		return this.season;
	}

	@Override
	public String toString() {
		return "Season [episodes=" + Arrays.toString(episodes) + ", season="
				+ season + "]";
	}

}
