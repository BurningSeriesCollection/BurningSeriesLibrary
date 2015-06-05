package de.jonesboard.burningseries.examples;

import de.jonesboard.burningseries.BurningSeries;
import de.jonesboard.burningseries.exceptions.SerieNotFoundException;
import de.jonesboard.burningseries.interfaces.SerieInterface;

public class getSerie {

	public static void main(String[] args) {
		int serie = 3;

		BurningSeries bs = new BurningSeries();

		try {
			SerieInterface serieObject = bs.getSerie(serie);

			System.out.println("=== " + serieObject.getName() + " ===");
			System.out.print("Start: "); System.out.println(serieObject.getStart() != 0 ? serieObject.getStart() : "Unknown");
			System.out.print("End: "); System.out.println(serieObject.getStart() != 0 ? serieObject.getEnd() : "Unknown");
			System.out.print("Has Movie(s): "); System.out.println(serieObject.hasMovies() ? "Yes" : "No");
			System.out.println("Seasons: " + String.valueOf(serieObject.getSeasons()));
			System.out.println(serieObject.getDescription());
		} catch(SerieNotFoundException e) {
			System.out.println("Couldn't find serie " + serie);
		}
	}


}
