package de.jonesboard.burningseries.examples;

import de.jonesboard.burningseries.BurningSeries;

public class caching {

	public static void main(String[] args) {
		BurningSeries bs = new BurningSeries();

		try {
			// Cache is enabled by default so normally there isn't a need to call that function
			bs.enableCache();
			
			bs.getSeries();
			bs.getNewest();
			bs.search("Naruto");
			bs.getByName("Dragonball");
			
			int withCaching = bs.getNumCalls();

			bs.disableCache();
			
			bs.getSeries();
			bs.getNewest();
			bs.search("Naruto");
			bs.getByName("Dragonball");
			
			int withoutCaching = bs.getNumCalls() - withCaching;
			
			System.out.println("Without caching we'd have run " + withoutCaching + " API calls. Caching reduces this to " + withCaching + " calls.");
		} catch(Exception e) {
			e.printStackTrace();
		}
	}


}
