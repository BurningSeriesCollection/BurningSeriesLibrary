package de.jonesboard.burningseries.examples;

import de.jonesboard.burningseries.BurningSeries;
import de.jonesboard.burningseries.interfaces.SerieInterface;

public class searchSeries {

	public static void main(String[] args) {
		String search = "Dragonball";

		BurningSeries bs = new BurningSeries();

		// If the second parameter is set to "true" only exact matches will be returned
		SerieInterface[] series = bs.search(search);

		System.out.println("Search for " + search + " gave back:");
		
		for(SerieInterface serie : series) {
			System.out.println(serie.getName() + " (ID: " + serie.getId() + ")");
		}
	}


}
