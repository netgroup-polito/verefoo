package it.polito.verefoo.utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import it.polito.verefoo.graph.IPAddress;
import it.polito.verefoo.graph.PortInterval;
import it.polito.verefoo.graph.Predicate;
import it.polito.verefoo.jaxb.L4ProtocolTypes;

public class APUtils {
	
	public APUtils() {}
	
	//Given a list of already computed atomicPredicates and a list of new predicates to insert into the list, transfrom predicates into atomic
	//predicates, add them to the list and return the new list
	//Algorithm 3 Yang_Lam_2015
	public List<Predicate> computeAtomicPredicates(List<Predicate> atomicPredicates, List<Predicate> predicates){
		List<Predicate> newAtomicPredicates = new ArrayList<>();
		Predicate first = null;
		List<Predicate> firstNeg = null;
		int count = -1;
		
		System.out.println("Computing atomic predicates:");
		int debugIndex = 0;
		for(Predicate sp: predicates) {
			if(debugIndex == 150) {
				debugIndex = 0;
				System.out.println();
			}
			debugIndex++;
			System.out.print("*");
			//If sp is the first predicate to transform and atomicPredicates is empty
			if(atomicPredicates.isEmpty() && count == -1) {
				first = sp;
				firstNeg = neg(sp);
				count = 1;
			}
			else if(count == 1) {
				//There is already a predicate in the list, and this is the second
				Predicate sp1 = computeIntersection(first, sp);
				if(sp1 != null) atomicPredicates.add(sp1);
				
				for(Predicate s: firstNeg) {
					Predicate sp2 = computeIntersection(s, sp);
					if(sp2 != null) atomicPredicates.add(sp2);
				}
				
				for(Predicate s: neg(sp)) {
					Predicate sp3 = computeIntersection(first,s);
					if(sp3 != null) atomicPredicates.add(sp3);
				}
				
				for(Predicate s1: neg(sp)) {
					for(Predicate s2: firstNeg) {
						Predicate sp4 = computeIntersection(s1,s2);
						if(sp4 != null) atomicPredicates.add(sp4);
					}
				}
				
				count = -1;
			} else {
				//there are already more then 2 predicates
				for(Predicate prevSp: atomicPredicates) {
					Predicate res1 = computeIntersection(prevSp, sp);
					if(res1 != null) newAtomicPredicates.add(res1);
					
					for(Predicate s: neg(sp)) {
						Predicate res2 = computeIntersection(prevSp,s);
						if(res2 != null) newAtomicPredicates.add(res2);
					}
				}
				atomicPredicates = new ArrayList<>(newAtomicPredicates);
				newAtomicPredicates = new ArrayList<>();
			}
		}
		System.out.println();
		if(count == 1) {
			firstNeg.add(first);
			return firstNeg;
		}
		return atomicPredicates;
	}

	//function that checks if ip1 is included in ip2 (NOTE: ip should use wildcards)
	//NOTE: here not considering if neg or not
	public boolean isIncludedIPString(String ip1, String ip2) {
		String fields1[] = ip1.split("\\.");
		String fields2[] = ip2.split("\\.");
		if((fields1[0].equals(fields2[0]) || fields2[0].equals("-1")) 
				&& (fields1[1].equals(fields2[1]) || fields2[1].equals("-1"))
				&& (fields1[2].equals(fields2[2]) || fields2[2].equals("-1"))
				&& (fields1[3].equals(fields2[3]) || fields2[3].equals("-1")))
			return true;
		return false;
	}
	
	//Given a predicate ap, this function computes its negation (it will be the disjunction of more predicates according to DeMorgan Law)
	//i.e. !{10.0.0.1, *, 20.0.0.2, *} =  {!10.0.0.1, *, *, *} V {*, *, !20.0.0.2, *} , whose atomic predicates (excluding ap) are
	//{10.0.0.1, *, !20.0.0.2, *}, {!10.0.0.1, *, 20.0.0.2, *}, {!10.0.0.1, *, !20.0.0.2, *}
	//This is the list returned
	public List<Predicate> neg(Predicate ap){
		List<Predicate> neg = new ArrayList<>();
			//check IPSrc
			for(IPAddress src: ap.getIPSrcList()) {
				if(!src.equalsStar()) {
					Predicate sp = new Predicate(src.toString(), src.isNeg(), "*", false, "*", false, "*", false, L4ProtocolTypes.ANY);
					neg.add(sp);
					Predicate sp2 = new Predicate(src.toString(), !src.isNeg(), "*", false, "*", false, "*", false, L4ProtocolTypes.ANY);
					neg.add(sp2);
				}
			}
			//check IPDst
			for(IPAddress dst: ap.getIPDstList()) {
				if(!dst.equalsStar()) {
					Predicate sp = new Predicate("*", false, dst.toString(), dst.isNeg(), "*", false, "*", false, L4ProtocolTypes.ANY);
					neg.add(sp);
					Predicate sp2 = new Predicate("*", false, dst.toString(), !dst.isNeg(), "*", false, "*", false, L4ProtocolTypes.ANY);
					neg.add(sp2);
				}
			}
			//check pSrc
			for(PortInterval psrc: ap.getpSrcList()) {
				if(!psrc.equalStar()) {
					Predicate sp = new Predicate("*", false, "*", false, psrc.toString(), psrc.isNeg(), "*", false, L4ProtocolTypes.ANY);
					neg.add(sp);
					Predicate sp2 = new Predicate("*", false, "*", false, psrc.toString(), !psrc.isNeg(), "*", false, L4ProtocolTypes.ANY);
					neg.add(sp2);
				}
			}
			//check pDst
			for(PortInterval pdst: ap.getpDstList()) {
				if(!pdst.equalStar()) {
					Predicate sp = new Predicate("*", false, "*", false,  "*", false, pdst.toString(), pdst.isNeg(), L4ProtocolTypes.ANY);
					neg.add(sp);
					Predicate sp2 = new Predicate("*", false, "*", false,  "*", false, pdst.toString(), !pdst.isNeg(), L4ProtocolTypes.ANY);
					neg.add(sp2);
				}
			}
			//check protoType
			List<L4ProtocolTypes> list = ap.getProtoTypeList();
			//If different from ANY and different from the full set (all others L4ProtocolTypes without ANY) 
			if(!list.contains(L4ProtocolTypes.ANY) && list.size() != L4ProtocolTypes.values().length-1) {
				Predicate sp1 = new Predicate("*", false, "*", false,  "*", false, "*", false, L4ProtocolTypes.ANY);
				sp1.setProtoTypeList(list);
				neg.add(sp1);
				List<L4ProtocolTypes> negList = computeDifferenceL4ProtocolTypes(list);
				if(!negList.isEmpty()) {
					Predicate sp2 = new Predicate("*", false, "*", false,  "*", false, "*", false, L4ProtocolTypes.ANY);
					sp2.setProtoTypeList(negList);
					neg.add(sp2);
				}
			}

			//Now we have to compute the atomic Predicates
			neg = computeAtomicPredicatesForNeg(neg);
			//Remove form atomicPredicates the predicate equal to ap
			int index = 0;
			for(Predicate sp: neg) {
				if(APCompare(ap, sp)) {
					neg.remove(index);
					break;
				}
				index++;
			}
			return neg;
	}
	
	//return true if the two predicates are equal
	public boolean APCompare(Predicate p1, Predicate p2) {
		//comparing lists size
		if(p1.getIPSrcList().size() != p2.getIPSrcList().size() || p1.getIPDstList().size() != p2.getIPDstList().size() 
				|| p1.getpSrcList().size() != p2.getpSrcList().size() || p1.getpDstList().size() != p2.getpDstList().size())
			return false;
		//NOTE: since list1 and list2 have the same size and they don't contain duplicates, list1.containsAll(list2) is sufficient
		//No need to call also list2.containsAll(list1)
		if(!p1.getIPSrcList().containsAll(p2.getIPSrcList()))
			return false;
		if(!p1.getIPDstList().containsAll(p2.getIPDstList()))
			return false;
		if(!APComparePortList(p1.getpSrcList(), p2.getpSrcList()))
			return false;
		if(!APComparePortList(p1.getpDstList(), p2.getpDstList()))
			return false;
		return APComparePrototypeList(p1.getProtoTypeList(), p2.getProtoTypeList());
	}
	
	public boolean APComparePrototypeList(List<L4ProtocolTypes> list1, List<L4ProtocolTypes> list2) {
		if(!list1.containsAll(list2))
			return false;
		return true;
	}
	
	public boolean APComparePortList(List<PortInterval> list1, List<PortInterval> list2) {
		if(!list1.containsAll(list2))
			return false;
		return true;
	}
	
	public boolean isPredicateContainedIn(Predicate p, List<Predicate> list) {
		for(Predicate p2: list) {
			if(APCompare(p, p2))
				return true;
		}
		return false;
	}
	
	//Compute atomic predicates considering the neg list of a predicate
	public List<Predicate> computeAtomicPredicatesForNeg(List<Predicate> predicates){
		List<Predicate> retList = new ArrayList<>();
		List<Predicate> tmpList = new ArrayList<>();
		int i = 0;
		
		while(i != predicates.size()) {
			if(i == 0) {
				retList.add(predicates.get(i));
				i++;
				retList.add(predicates.get(i));
				i++;
			}
			else {
				for(Predicate sp: retList) {
					Predicate res = computeIntersection(sp, predicates.get(i));
					Predicate res2 =  computeIntersection(sp, predicates.get(i+1));
					if(res != null) tmpList.add(res);
					if(res2 != null) tmpList.add(res2);
				}
				i = i+2;
				retList = new ArrayList<>(tmpList);
				tmpList = new ArrayList<>();
			}
		}
		
		return retList;
	}
	
	//Compute the intersection of two IPAddress (if the intersection exists, otherwise it returns an empty list)
	public List<IPAddress> intersectionIPAddressNew(IPAddress ip1, IPAddress ip2){
		List<IPAddress> retList = new ArrayList<>();
		if(!ip1.isNeg() && !ip2.isNeg()) { //both not neg
			if(ip1.isIncludedIn(ip2)) {
				retList.add(ip1);
				return retList;
			}
			if(ip2.isIncludedIn(ip1)) {
				retList.add(ip2);
				return retList;
			}
		} else if(!ip1.isNeg() && ip2.isNeg()) { //ip1 not neg, ip2 neg
			if(!ip1.equalFileds(ip2) && ip2.isIncludedIn(ip1)) {
				retList.add(ip1);
				retList.add(ip2);
				return retList;
			}
			if(!ip1.equalFileds(ip2) && !ip1.isIncludedIn(ip2)) {
				retList.add(ip1);
				return retList;
			}
		} else if(ip1.isNeg() && !ip2.isNeg()) { //ip1 neg, ip2 not neg
			if(!ip1.equalFileds(ip2) && ip1.isIncludedIn(ip2)) {
				retList.add(ip1);
				retList.add(ip2);
				return retList;
			}
			if(!ip1.equalFileds(ip2) && !ip2.isIncludedIn(ip1)) {
				retList.add(ip2);
				return retList;
			}
		} else { //both neg
			if(ip1.equalFileds(ip2)) {
				retList.add(ip1);
				return retList;
			} else {
				if(ip1.isIncludedIn(ip2)) {
					retList.add(ip2);
					return retList;
				} 
				if(ip2.isIncludedIn(ip1)) {
					retList.add(ip1);
					return retList;
				}
				retList.add(ip1);
				retList.add(ip2);
				return retList;
			}
		}
		
		return retList;
	}
	
	//Compute the intersection of two PortInterval (if the intersection exists, otherwise it returns an empty list)
	public List<PortInterval> intersectionPortIntervalNew(PortInterval pi1, PortInterval pi2){
		List<PortInterval> retList = new ArrayList<>();
		if(!pi1.isNeg() && !pi2.isNeg()) { //both not neg
			if(pi1.isIncludedInPortInterval(pi2)) {
				retList.add(pi1);
				return retList;
			}
			if(pi2.isIncludedInPortInterval(pi1)) {
				retList.add(pi2);
				return retList;
			}
		}
		else if(!pi1.isNeg() && pi2.isNeg()) { //pi1 not neg, pi2 neg
			if(!pi1.equalFileds(pi2) && pi2.isIncludedInPortInterval(pi1)) {
				retList.add(pi1);
				retList.add(pi2);
				return retList;
			}
			if(!pi1.equalFileds(pi2) && !pi1.isIncludedInPortInterval(pi2)) {
				retList.add(pi1);
				return retList;
			}
		}
		else if(pi1.isNeg() && !pi2.isNeg()) { //pi1 neg, pi2 not neg
			if(!pi1.equalFileds(pi2) && pi1.isIncludedInPortInterval(pi2)) {
				retList.add(pi1);
				retList.add(pi2);
				return retList;
			}
			if(!pi1.equalFileds(pi2) && !pi2.isIncludedInPortInterval(pi1)) {
				retList.add(pi2);
				return retList;
			}
		} else { //both neg
			if(pi1.equalFileds(pi2)) {
				retList.add(pi1);
				return retList;
			} else {
				if(pi1.isIncludedInPortInterval(pi2)) {
					retList.add(pi2);
					return retList;
				}
				if(pi2.isIncludedInPortInterval(pi1)) {
					retList.add(pi1);
					return retList;
				}
				retList.add(pi1);
				retList.add(pi2);
				return retList;
			}
		}
		
		return retList;
	}
	
	//Computes the intersection between two predicates and returns the resulting predicate or null 
	public Predicate computeIntersection(Predicate p1, Predicate p2){
		//Check IPSrc
		List<IPAddress> resultIPSrcList = p2.getIPSrcList();
		List<IPAddress> tmpList;
		List<IPAddress> tmpList2 = new ArrayList<>();
		List<IPAddress> toInsert1List = new ArrayList<>();
		boolean toInsert1;
		for(IPAddress src1: p1.getIPSrcList()) {
			toInsert1 = false;
			for(IPAddress src2: resultIPSrcList) {
				tmpList = intersectionIPAddressNew(src1, src2);
				if(tmpList.isEmpty())
					return null; //no intersection exists
				for(IPAddress res: tmpList) {
					if(res.equals(src1))
						toInsert1 = true;
					else tmpList2.add(res);
				}
			}
			
			if(resultIPSrcList.isEmpty()) toInsert1 = true;
			resultIPSrcList = new ArrayList<>(tmpList2);
			tmpList2 = new ArrayList<>();
			if(toInsert1) toInsert1List.add(src1);
		}
		resultIPSrcList.addAll(toInsert1List);

		//Check IPDst
		List<IPAddress> resultIPDstList = p2.getIPDstList();
		toInsert1List = new ArrayList<>();
		for(IPAddress dst1: p1.getIPDstList()) {
			toInsert1 = false;
			for(IPAddress dst2: resultIPDstList) {
				tmpList = intersectionIPAddressNew(dst1, dst2);
				if(tmpList.isEmpty())
					return null; //no  intersection exists
				for(IPAddress res: tmpList) {
					if(res.equals(dst1))
						toInsert1 = true;
					else tmpList2.add(res);
				}
			}
			if(resultIPDstList.isEmpty()) toInsert1 = true;
			resultIPDstList = new ArrayList<>(tmpList2);
			tmpList2 = new ArrayList<>();
			if(toInsert1) toInsert1List.add(dst1);
		}
		resultIPDstList.addAll(toInsert1List);

		//Check pSrc
		List<PortInterval> resultPSrcList = p2.getpSrcList();
		List<PortInterval> tmpPList;
		List<PortInterval> tmpPList2 = new ArrayList<>();
		List<PortInterval> toInsert1PList = new ArrayList<>();
		for(PortInterval psrc1: p1.getpSrcList()) {
			toInsert1 = false;
			for(PortInterval psrc2: resultPSrcList) {
				tmpPList = intersectionPortIntervalNew(psrc1, psrc2);
				if(tmpPList.isEmpty())
					return null; //no  intersection exists
				for(PortInterval res: tmpPList) {
					if(res.equals(psrc1))
						toInsert1 = true;
					else tmpPList2.add(res);
				}
			}
			if(resultPSrcList.isEmpty()) toInsert1 = true;
			resultPSrcList = new ArrayList<>(tmpPList2);
			tmpPList2 = new ArrayList<>();
			if(toInsert1) toInsert1PList.add(psrc1);
		}
		resultPSrcList.addAll(toInsert1PList);

		//Check pDst
		List<PortInterval> resultPDstList = p2.getpDstList();
		toInsert1PList = new ArrayList<>();
		for(PortInterval pdst1: p1.getpDstList()) {
			toInsert1 = false;
			for(PortInterval pdst2: resultPDstList) {
				tmpPList = intersectionPortIntervalNew(pdst1, pdst2);
				if(tmpPList.isEmpty())
					return null; //no  intersection exists
				for(PortInterval res: tmpPList) {
					if(res.equals(pdst1))
						toInsert1 = true;
					else tmpPList2.add(res);
				}
			}
			if(resultPDstList.isEmpty()) toInsert1 = true;
			resultPDstList = new ArrayList<>(tmpPList2);
			tmpPList2 = new ArrayList<>();
			if(toInsert1) toInsert1PList.add(pdst1);
		}
		resultPDstList.addAll(toInsert1PList);
		
		//Check proto
		List<L4ProtocolTypes> resultProtoList = new ArrayList<>();
		if(p1.getProtoTypeList().contains(L4ProtocolTypes.ANY))
			resultProtoList =  p2.getProtoTypeList();
		else if(p2.getProtoTypeList().contains(L4ProtocolTypes.ANY))
			resultProtoList =  p1.getProtoTypeList();
		else { //None contains ANY, so compute intersection
			for(L4ProtocolTypes proto1: p1.getProtoTypeList()) {
				if(p2.getProtoTypeList().contains(proto1))
					resultProtoList.add(proto1);
			}
		}
		if(resultProtoList.isEmpty())
			return null; //no intersection exists
	
		Predicate resultPredicate = new Predicate();
		resultPredicate.setIPSrcList(resultIPSrcList);
		resultPredicate.setIPDstList(resultIPDstList);
		resultPredicate.setpSrcList(resultPSrcList);
		resultPredicate.setpDstList(resultPDstList);
		resultPredicate.setProtoTypeList(resultProtoList);
		return resultPredicate;
	}
	
	/* Compute rules for firewall */
	//toAdd is the ALLOW rule to insert, denied is the list of denied predicates
	//return allowed = rule-i AND !denied
	List<Predicate> negDeniedRuleList;
	public List<Predicate> computeAllowedForRule(Predicate toAdd, List<Predicate> deniedList, boolean deniedListChanged){
		List<Predicate> retList = new ArrayList<>();
		List<Predicate> tmpList = new ArrayList<>();
		retList.add(toAdd);
		
		if(deniedList.isEmpty()) return retList;
		
		for(Predicate deniedRule: deniedList) {
			//compute !denied
			if(deniedListChanged)
				negDeniedRuleList = neg(deniedRule);
			for(Predicate p1: retList) {
				for(Predicate p2: negDeniedRuleList) {
					Predicate res = computeIntersection(p1, p2);
					if(res != null) {
						tmpList.add(res);
					}
				}
			}
			if(tmpList.isEmpty()) {
				//no intersection exists
				return new ArrayList<>();
			} else {
				retList = new ArrayList<>(tmpList);
				tmpList = new ArrayList<>();
			}
		}
		return retList;
	}
	
	//compute the difference between two sets: the set of all possible values for L4ProtocolTypes - list (from params)
	public List<L4ProtocolTypes> computeDifferenceL4ProtocolTypes(List<L4ProtocolTypes> list){
		List<L4ProtocolTypes> retList = new ArrayList<>();
		if(list.contains(L4ProtocolTypes.ANY))
			return new ArrayList<>();

		for(L4ProtocolTypes ptype: L4ProtocolTypes.values()) {
			if(!ptype.equals(L4ProtocolTypes.ANY) && !list.contains(ptype))
				retList.add(ptype);
		}
		return retList;
	}
	
	
	//This method transform a complex predicate in a list of simple tuple in OR
		public List<Predicate> complexPredicatetoOrTuples(Predicate inputPredicate){
			List<Predicate> returnList = new ArrayList<>();
			
			List<IPAddress> differentIPSrcAddressList = complexIPAddressListInAndToOr(inputPredicate.getIPSrcList());
			List<IPAddress> differentIPDstAddressList = complexIPAddressListInAndToOr(inputPredicate.getIPDstList());
			List<PortInterval> differentPSrcList = complexPortIntervalListInAndToOr(inputPredicate.getpSrcList());
			List<PortInterval> differentPDstList = complexPortIntervalListInAndToOr(inputPredicate.getpDstList());
			
			for(IPAddress IPSrc: differentIPSrcAddressList) {
				for(IPAddress IPDst: differentIPDstAddressList) {
					for(PortInterval pSrc: differentPSrcList) {
						for(PortInterval pDst: differentPDstList) {
							for(L4ProtocolTypes proto: inputPredicate.getProtoTypeList()) {
								returnList.add(new Predicate(IPSrc.toString(), false, IPDst.toString(), false, 
										pSrc.toString(), false, pDst.toString(), false, proto));
							}
						}
					}
				}
			}
			
			return returnList;
		}
		
		public List<IPAddress> complexIPAddressListInAndToOr(List<IPAddress> list){
			List<IPAddress> differentIPAddressList = new ArrayList<>();
			HashMap<Integer, List<IPAddress>> IPSrcAddressWildcardsMap = new HashMap<>();
			IPAddress startingIPAddress = null;
			int firstByteWithWildcard = 5;
			
			for(IPAddress IPSrc: list) {
				if(IPSrc.toString().equals("*"))
					continue;
				int n = IPSrc.hasWildcardsInByte();
				if(n < firstByteWithWildcard) {
					firstByteWithWildcard = n;
					startingIPAddress = new IPAddress(IPSrc, false);
				}
				if(IPSrcAddressWildcardsMap.containsKey(n)) {
					IPSrcAddressWildcardsMap.get(n).add(IPSrc);
				} else {
					List<IPAddress> newList = new ArrayList<>();
					newList.add(IPSrc);
					IPSrcAddressWildcardsMap.put(n, newList);
				}
			}
			
			if(firstByteWithWildcard == 5) {
				//no wildcards in IPAddress
				differentIPAddressList.add(list.get(0));
			} else {
				recursiveGenIPAddress(firstByteWithWildcard, startingIPAddress, IPSrcAddressWildcardsMap, differentIPAddressList);
			}
			
			//DEBUG: print list
//			System.out.println("IPAddress list size " + differentIPAddressList.size());
//			for(IPAddress ip: differentIPAddressList) {
//				System.out.println(ip.toString());
//			}
			//END DEBUG
			
			return differentIPAddressList; 
		}
		
		public void recursiveGenIPAddress(int byteNumber, IPAddress startingAddress, HashMap<Integer, List<IPAddress>> IPWildcardsMap,
				List<IPAddress> differentIPAddressList) {
			
			if(byteNumber == 5) {
				//Agguingo e termino la ricorsione
				differentIPAddressList.add(startingAddress);
				return;
			}
			
			//Get number to eliminate
			List<Integer> toEliminateList = new ArrayList<>();
			if(IPWildcardsMap.containsKey(byteNumber+1)) {
				for(IPAddress ip: IPWildcardsMap.get(byteNumber+1)) {
					if(ip.isIncludedIn(startingAddress)) {
						toEliminateList.add(ip.getByteNumber(byteNumber));
					}
				}
			}
			List<Integer> toControlList = new ArrayList<>();
			for(int z=byteNumber+2; z<=5; z++) {
				if(IPWildcardsMap.containsKey(z)) {
					for(IPAddress ip: IPWildcardsMap.get(z)) {
						if(ip.isIncludedIn(startingAddress)) {
							toControlList.add(ip.getByteNumber(byteNumber));
						}
					}
				}
			}
			
			if(toEliminateList.size() == 0 && toControlList.size() == 0) {
				//Questo predicato viene aggiunto cos� com'� alla lista e posso terminare la ricorsione
				differentIPAddressList.add(startingAddress);
				return;
			} else {
				for(int i=1; i<256; i++) {
					//continuo la ricorsione
					if(!toEliminateList.contains(i)) {
						IPAddress newIPAddress = new IPAddress(startingAddress, false);
						newIPAddress.setByteNumberWithValue(byteNumber, i);
						recursiveGenIPAddress(byteNumber+1, newIPAddress, IPWildcardsMap, differentIPAddressList);
					}
				}
			}
		}

		public List<PortInterval> complexPortIntervalListInAndToOr(List<PortInterval> list){
			List<PortInterval> returnList = new ArrayList<>();
			PortInterval largestPI = null;
			List<PortInterval> otherIntervalList = new ArrayList<>();
			
			//Compute largest interval
			for(PortInterval pi: list) {
				if(!pi.isNeg())
					largestPI = pi;
				else otherIntervalList.add(pi);
			}
			
			if(largestPI == null)
				largestPI = new PortInterval("*", false);
			
			if(largestPI.getMin() == -1 && largestPI.getMax() == -1) {
				largestPI.setMin(0);
				largestPI.setMax(65535);
			}
			
			if(otherIntervalList.size() == 0) {
				returnList.add(largestPI);
				return returnList;
			}
			
			//Sort by increasing min
			Collections.sort(otherIntervalList, new PortIntervalComparator());
			
			int index = 0;
			for(PortInterval pi: otherIntervalList) {
				if(index == 0) {
					//first port interval
					if(pi.getMin() > largestPI.getMin()) {
						returnList.add(new PortInterval(largestPI.getMin(), pi.getMin()-1, false));
					}
				}
				else {
					PortInterval previousPI = otherIntervalList.get(index-1);
					returnList.add(new PortInterval(previousPI.getMax()+1, pi.getMin()-1, false));
				}
				index++;
			}
			PortInterval last = otherIntervalList.get(index-1);
			if(last.getMax() < largestPI.getMax())
				returnList.add(new PortInterval(last.getMax()+1, largestPI.getMax(), false));
			
			//DEBUG: print list
//			System.out.println("Lista Port Interval size " + returnList.size());
//			for(PortInterval pi: returnList)
//				System.out.println(pi.toString());
//			System.out.println();
			//END DEBUG
			
			return returnList;
		}

}
