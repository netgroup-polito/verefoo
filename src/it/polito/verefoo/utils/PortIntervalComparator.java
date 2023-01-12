package it.polito.verefoo.utils;

import java.util.Comparator;

import it.polito.verefoo.graph.PortInterval;

public class PortIntervalComparator implements Comparator<PortInterval>{

	@Override
	public int compare(PortInterval o1, PortInterval o2) {
		return Integer.compare(o1.getMin(), o2.getMin());
	}
}
