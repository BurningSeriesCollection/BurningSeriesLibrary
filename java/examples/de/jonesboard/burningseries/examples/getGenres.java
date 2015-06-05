package de.jonesboard.burningseries.examples;

import de.jonesboard.burningseries.BurningSeries;

public class getGenres {

	public static void main(String[] args) {
		BurningSeries bs = new BurningSeries();

		String[] genres = bs.getGenres();

		System.out.println("The following genres exist:");

		for(String genre : genres) {
			System.out.println(genre);
		}
	}


}
