package de.jonesboard.burningseries.examples;

import de.jonesboard.burningseries.BurningSeries;

public class buildUrls {

	public static void main(String[] args) {
		// Serie can be the ID, the Name or if the series is already loaded the array
		int serie = 3; // 24
		int season = 1;
		int episode = 1;

		BurningSeries bs = new BurningSeries();

		try {
			String serieUrl = bs.buildSerieUrl(serie);
			String seasonUrl = bs.buildSeasonUrl(serie, season);
			String episodeUrl = bs.buildEpisodeUrl(serie, season, episode);
	
			System.out.println("Serie for " + serie + ";\tLink: " + serieUrl);
			System.out.println("Season " + season + ";\tLink: " + seasonUrl);
			System.out.println("Episode " + episode + ";\tLink: " + episodeUrl);
		} catch(Exception e) {
			e.printStackTrace();
		}
	}


}
