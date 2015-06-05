package de.jonesboard.burningseries.examples;

import de.jonesboard.burningseries.BurningSeries;
import de.jonesboard.burningseries.interfaces.SerieInterface;

public class getFavoritesAndLogin {

	static String username = "";
	static String password = "";
	
	public static void main(String[] args) {
		if(username.equals("") || password.equals("")) {
			System.out.println("Please update username and password in this file!");
			return;
		}

		BurningSeries bs = new BurningSeries(username, password);

		try {
			SerieInterface[] favorites = bs.getFavoriteSeries();

			System.out.println("The session key for this request is " + bs.getSessionId() + " \n");
			System.out.println("Your favorite series are:");

			for(SerieInterface favorite : favorites) {
				System.out.println(favorite.getName() + " (ID: " + favorite.getId()+")");
			}

			// Don't forget to logout if you're not using the sessionkey somewhere else!
			bs.logout();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}


}
