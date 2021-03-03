package it.polito.verefoo.utils;

import java.util.Comparator;

import it.polito.verefoo.graph.IPAddress;

public class IPAddressComparator implements Comparator<IPAddress> {

	@Override
	public int compare(IPAddress o1, IPAddress o2) {
		int res;
		if((res = o1.getFirstByte().compareTo(o2.getFirstByte())) != 0)
			return res;
		else if((res = o1.getSecondByte().compareTo(o2.getSecondByte())) != 0)
			return res;
		else if((res = o1.getThirdByte().compareTo(o2.getThirdByte())) != 0)
			return res;
		else if((res = o1.getFourthByte().compareTo(o2.getFourthByte())) != 0)
			return res;
		else return 0;
	}
}
