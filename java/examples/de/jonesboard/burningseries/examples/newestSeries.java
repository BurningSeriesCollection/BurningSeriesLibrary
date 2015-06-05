package de.jonesboard.burningseries.examples;

import de.jonesboard.burningseries.BurningSeries;
import de.jonesboard.burningseries.interfaces.SerieInterface;

public class newestSeries {

	public static void main(String[] args) {
		BurningSeries bs = new BurningSeries();

		SerieInterface[] newestSeries = bs.getNewest();

		System.out.println("Latest Series:");
		
		for(int i = 0; i < 5; i++) {
			System.out.println(newestSeries[i].getName() + " (ID: " + newestSeries[i].getId() + ")");
		}
	}


}
