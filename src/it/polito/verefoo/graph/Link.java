package it.polito.verefoo.graph;
/**
 * 
 * This class is used to convert the neighbor node notion into a more convenient form in order to pass the correct information to Verifoo 
 *
 */
public class Link {
	String sourceNode;
	String destNode;
	/**
	 * Public constructor of Link object
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
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((destNode == null) ? 0 : destNode.hashCode());
		result = prime * result + ((sourceNode == null) ? 0 : sourceNode.hashCode());
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof Link)) {
			return false;
		}
		Link other = (Link) obj;
		if (destNode == null) {
			if (other.destNode != null) {
				return false;
			}
		} else if (!destNode.equals(other.destNode)) {
			return false;
		}
		if (sourceNode == null) {
			if (other.sourceNode != null) {
				return false;
			}
		} else if (!sourceNode.equals(other.sourceNode)) {
			return false;
		}
		return true;
	}

}
