package de.jonesboard.burningseries.examples;

import de.jonesboard.burningseries.BurningSeries;
import de.jonesboard.burningseries.interfaces.SerieInterface;

public class getSerie {

	public static void main(String[] args) {
		int serie = 3;

		BurningSeries bs = new BurningSeries();

		try {
			SerieInterface serieObject = bs.getSerie(serie);

			if(serieObject == null) {
				System.out.println("No serie with ID  " + String.valueOf(serie));
				return;
			}

			System.out.println("=== " + serieObject.getName() + " ===");
			System.out.print("Start: "); System.out.println(serieObject.getStart() != 0 ? serieObject.getStart() : "Unknown");
			System.out.print("End: "); System.out.println(serieObject.getStart() != 0 ? serieObject.getEnd() : "Unknown");
			System.out.print("Has Movie(s): "); System.out.println(serieObject.hasMovies() ? "Yes" : "No");
			System.out.println("Seasons: " + String.valueOf(serieObject.getSeasons()));
			System.out.println(serieObject.getDescription());
		} catch(Exception e) {
			e.printStackTrace();
		}
	}


}
