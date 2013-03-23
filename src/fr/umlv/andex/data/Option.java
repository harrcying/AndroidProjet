package fr.umlv.andex.data;

import java.io.Serializable;

public class Option implements Serializable{
	
	private static final long serialVersionUID = 1L;
	private String description;
	private int id;

	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}

	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
}
