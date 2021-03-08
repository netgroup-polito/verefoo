package it.polito.verefoo.graph;

import java.util.ArrayList;
import java.util.List;

public class PortInterval {
	int min;
	int max;
	boolean neg;
	public PortInterval(int min, int max, boolean neg) {
		super();
		this.min = min;
		this.max = max;
		this.neg = neg;
	}
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
	public int getMin() {
		return min;
	}
	public void setMin(int min) {
		this.min = min;
	}
	public int getMax() {
		return max;
	}
	public void setMax(int max) {
		this.max = max;
	}
	public boolean isNeg() {
		return neg;
	}
	public void setNeg(boolean neg) {
		this.neg = neg;
	}
	
	//function that checks if "this" is included in p
	public boolean isIncludedInPortInterval(PortInterval p) {
		if((min >= p.getMin() || p.getMin() == -1) && (max <= p.getMax() || p.getMax() == -1))
			return true;
		return false;
	}
	
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
	
	//function that checks if "this" is equal to pi, but without considering neg attribute
	public boolean equalFileds(PortInterval pi) {
		if(min == pi.getMin() && max == pi.getMax())
			return true;
		return false;
	}
}
