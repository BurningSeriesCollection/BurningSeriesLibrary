package de.jonesboard.burningseries.examples;

import de.jonesboard.burningseries.BurningSeries;
import de.jonesboard.burningseries.interfaces.SerieInterface;

public class getSeries {

	public static void main(String[] args) {
		BurningSeries bs = new BurningSeries();

		SerieInterface[] series = bs.getSeries();
		int numSeries = series.length;

		System.out.println("We currently have " + String.valueOf(numSeries) + " Series:");
		
		for(SerieInterface serie : series) {
			System.out.println(serie.getName() + " (ID: " + serie.getId() + ")");
		}
	}


}
