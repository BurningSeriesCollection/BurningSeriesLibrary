package de.jonesboard.burningseries.examples;

import java.util.Arrays;

import de.jonesboard.burningseries.BurningSeries;

public class Test {

	public static void main(String[] args) {
		try {
			BurningSeries bs = new BurningSeries("25304fd093c831dce7303e1e9f7a5276");
			System.out.println(bs.isFavoritedSerie(3));
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

}
