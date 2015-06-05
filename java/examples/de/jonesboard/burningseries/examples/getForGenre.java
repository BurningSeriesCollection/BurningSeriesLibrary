package de.jonesboard.burningseries.examples;

import de.jonesboard.burningseries.BurningSeries;
import de.jonesboard.burningseries.interfaces.SerieInterface;

public class getForGenre {

	public static void main(String[] args) {
		String genre = "Anime";

		BurningSeries bs = new BurningSeries();

		try {
			SerieInterface[] series = bs.getByGenre(genre);
	
			System.out.println("The following series have genre " + genre + ":");

			for(SerieInterface serie : series) {
				System.out.println(serie.getName() + " (ID: " + serie.getId() + ")");
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
	}


}
