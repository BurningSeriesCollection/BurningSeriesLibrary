package de.jonesboard.burningseries.classes;

import de.jonesboard.burningseries.interfaces.HosterInterface;

public class Hoster implements HosterInterface {
	private String name;
	private int part;
	private int id;
	private String url;
	private int episode;
	private String fullurl;
	
	@Override
	public void setHoster(String hoster) {
		this.name = hoster;
	}

	@Override
	public String getName() {
		return this.name;
	}

	@Override
	public void setPart(String part) {
		this.part = Integer.parseInt(part);
	}

	@Override
	public int getPart() {
		return this.part;
	}

	@Override
	public void setId(String id) {
		this.id = Integer.parseInt(id);
	}

	@Override
	public int getId() {
		return this.id;
	}

	@Override
	public void setUrl(String url) {
		this.url = url;
	}

	@Override
	public String getUrl() {
		return this.url;
	}

	@Override
	public void setEpi(String epi) {
		this.episode = Integer.parseInt(epi);
	}

	@Override
	public int getEpisode() {
		return this.episode;
	}

	@Override
	public void setFullurl(String fullurl) {
		this.fullurl = fullurl;
	}

	@Override
	public String getFullurl() {
		return this.fullurl;
	}

	@Override
	public String toString() {
		return "Hoster [name=" + name + ", part=" + part + ", id=" + id
				+ ", url=" + url + ", episode=" + episode + ", fullurl="
				+ fullurl + "]";
	}

}
