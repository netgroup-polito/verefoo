package it.polito.verefoo.utils;

import java.util.Comparator;
import it.polito.verefoo.graph.PortInterval;


public class PortIntervalComparator implements Comparator<PortInterval>{

	@Override
	public int compare(PortInterval o1, PortInterval o2) {
		int res;
		if((res = o1.getMin() - o2.getMin()) != 0)
			return res;
		else if((res = o1.getMax() - o2.getMax()) != 0)
			return res;
		else return 0;
	}

}
