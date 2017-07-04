package it.polito.verigraph.model;

public class Entry {
	private String	direction;
	private String	destination;

	public Entry(String direction, String destination) {
		this.direction = direction;
		this.destination = destination;
	}

	public String getDirection() {
		return direction;
	}

	public void setDirection(String direction) {
		this.direction = direction;
	}

	public String getDestination() {
		return destination;
	}

	public void setDestination(String destination) {
		this.destination = destination;
	}

}
