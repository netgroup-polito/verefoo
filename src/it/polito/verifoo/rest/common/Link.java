package it.polito.verifoo.rest.common;

public class Link {
	String sourceNode;
	String destNode;
	/**
	 * @param sourceNode
	 * @param destNode
	 */
	public Link(String sourceNode, String destNode) {
		super();
		this.sourceNode = sourceNode;
		this.destNode = destNode;
	}
	/**
	 * @return the sourceNode
	 */
	public String getSourceNode() {
		return sourceNode;
	}
	/**
	 * @param sourceNode the sourceNode to set
	 */
	public void setSourceNode(String sourceNode) {
		this.sourceNode = sourceNode;
	}
	/**
	 * @return the destNode
	 */
	public String getDestNode() {
		return destNode;
	}
	/**
	 * @param destNode the destNode to set
	 */
	public void setDestNode(String destNode) {
		this.destNode = destNode;
	}

}
