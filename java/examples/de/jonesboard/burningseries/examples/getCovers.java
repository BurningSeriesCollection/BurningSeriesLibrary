package de.jonesboard.burningseries.examples;

import de.jonesboard.burningseries.BurningSeries;
import de.jonesboard.burningseries.exceptions.SerieNotFoundException;

public class getCovers {

	public static void main(String[] args) {
		// Serie can be the ID, the Name or if the series is already loaded the array
		String serie = "Dragonball";

		BurningSeries bs = new BurningSeries();

		try {
			String coverUrl = bs.getCover(serie);
	
			System.out.println("Cover for " + serie + ";\tLink: " + coverUrl);
		} catch(SerieNotFoundException e) {
			System.out.println("Couldn't find serie " + serie);
		}
	}


}
