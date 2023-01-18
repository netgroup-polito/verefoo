package it.polito.verefoo.graph;

import java.util.ArrayList;
import java.util.List;

/**
 * This class is used to model source and destination port numbers within an IP
 * header. It can represent a range of values included between min and max,
 * within the
 * range of all possible values starting from 0 to 65535, or a single specific
 * port number
 * (in this case min = max). Here too, it is possible to use wildcards,
 * representing the
 * full range [0, 65535], by setting min = max = -1.
 */
public class PortInterval {
	
	int min;
	int max;
	boolean neg;
	
	/**
	 * This constructor simply populates the port interval [min,max] of "this"
	 * object including
	 * the negation status.
	 * 
	 * @param min Is the lower bound of the port interval [min,max]
	 * @param max Is the upper bound of the port interval [min,max]
	 * @param neg Negation status of the port interval that is equivalent to "!"
	 */
	public PortInterval(int min, int max, boolean neg) {
		super();
		this.min = min;
		this.max = max;
		this.neg = neg;
	}
	/**
	 * This constructor simply populates the port interval [min,max] of "this"
	 * object including
	 * the negation status.
	 * 
	 * @param port Is the lower bound and upper bound of the port interval in the
	 *             string format "min-max"
	 * @param neg  Negation status of the port interval that is equivalent to "!"
	 */
	public PortInterval(String port, boolean neg) {
		if(port.equals("*")) {
			this.min = -1;
			this.max = -1;
		} else if(port.contains("-")){
			String fields[] = port.split("\\-");
			this.min = Integer.valueOf(fields[0]);
			this.max = Integer.valueOf(fields[1]);
		} else {
			this.min = Integer.valueOf(port);
			this.max = this.min;
		}
		this.neg = neg;
	}
	// Maximal Flows Algorithm constructor
	public PortInterval(PortInterval toCopy, boolean neg) {
		super();
		this.min = toCopy.getMin();
		this.max = toCopy.getMax();
		this.neg = neg;
	}

	/**
	 * Getter method for the lower bound of port interval (min)
	 * @return lower bound of port interval
	 */
	public int getMin() {
		return min;
	}
	/**
	 * Setter method for lower bound of port interval (min)
	 * @param min Is the lower bound value between [0-65535]
	 */
	public void setMin(int min) {
		this.min = min;
	}
	/**
	 * Getter method for the upper bound of port interval (max)
	 * @return upper bound of port interval
	 */
	public int getMax() {
		return max;
	}
	/**
	 * Setter method for upper bound of port interval (max)
	 * @param max Is the upper bound value between [0,65535]
	 */
	public void setMax(int max) {
		this.max = max;
	}
	/**
	 * Getter method for the negation status of the port interval
	 * @return True if negation exist, False otherwise.
	 */
	public boolean isNeg() {
		return neg;
	}
	/**
	 * Setter method for the negation status of port interval
	 * @param neg Is the negation status
	 */
	public void setNeg(boolean neg) {
		this.neg = neg;
	}
	
	/**
	 * Checks if "this" is included in the parameter 'p' or equal to it.
	 * @param p Contains port interval
	 * @return True if "this" is included or equal to 'p', False otherwise.
	 */
	public boolean isIncludedInPortInterval(PortInterval p) {
		if((min >= p.getMin() || p.getMin() == -1) && (max <= p.getMax() || p.getMax() == -1))
			return true;
		return false;
	}
	
	/**
	 * Checks if the PortInterval represents the full set of ports [0,65535]
	 * 
	 * @return returns true if the PortInterval represents the full set of ports
	 *         [0,65535],
	 *         False otherwise.
	 */
	public boolean equalStar() {
		if(min == -1 && max == -1)
			return true;
		return false;
	}
	
	@Override
	public String toString() {
		if(min == -1 && max == -1)
			return "*";
		if(min == max)
			return String.valueOf(min);
		return String.valueOf(min)+"-"+String.valueOf(max);
	}
	
	@Override
	public boolean equals(Object obj) {
		PortInterval pi = (PortInterval) obj;
		if(min == pi.getMin() && max == pi.getMax() && neg == pi.isNeg())
			return true;
		return false;
	}
	
	/**
	 * Checks if "this" is equal to 'pi' without considering the neg attribute
	 * 
	 * @param pi Contains port interval
	 * @return True if the two port intervals have the same (min,max) values, False
	 *         otherwise.
	 */
	public boolean equalFileds(PortInterval pi) {
		if(min == pi.getMin() && max == pi.getMax())
			return true;
		return false;
	}
}
