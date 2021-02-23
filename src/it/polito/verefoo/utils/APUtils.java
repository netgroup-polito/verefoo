package it.polito.verefoo.utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import it.polito.verefoo.graph.SimplePredicate;

public class APUtils {
	
	public APUtils() {}

	public List<SimplePredicate> computeAtomicPredicates(List<SimplePredicate> atomicPredicates, List<SimplePredicate> predicates){
		List<SimplePredicate> newAtomicPredicates = new ArrayList<>();
		SimplePredicate first = null;
		List<SimplePredicate> firstNeg = null;
		int count = -1;
		
		for(SimplePredicate sp: predicates) {
			if(atomicPredicates.isEmpty() && count == -1) {
				first = sp;
				firstNeg = neg(sp);
				count = 1;
			}
			else if(count == 1) {
				SimplePredicate sp1 = computeIntersection(first, sp);
				if(sp1 != null) atomicPredicates.add(sp1);
				
				for(SimplePredicate s: firstNeg) {
					SimplePredicate sp2 = computeIntersection(s, sp);
					if(sp2 != null) atomicPredicates.add(sp2);
				}
				
				for(SimplePredicate s: neg(sp)) {
					SimplePredicate sp3 = computeIntersection(first,s);
					if(sp3 != null) atomicPredicates.add(sp3);
				}
				
				for(SimplePredicate s1: neg(sp)) {
					for(SimplePredicate s2: firstNeg) {
						SimplePredicate sp4 = computeIntersection(s1,s2);
						if(sp4 != null) atomicPredicates.add(sp4);
					}
				}
				
				count = -1;
			} else {
				for(SimplePredicate prevSp: atomicPredicates) {
					SimplePredicate res1 = computeIntersection(prevSp, sp);
					if(res1 != null) newAtomicPredicates.add(res1);
					
					for(SimplePredicate s: neg(sp)) {
						SimplePredicate res2 = computeIntersection(prevSp,s);
						if(res2 != null) newAtomicPredicates.add(res2);
					}
				}
				atomicPredicates = new ArrayList<>(newAtomicPredicates);
				newAtomicPredicates = new ArrayList<>();
			}
		}
		if(count == 1) {
			firstNeg.add(first);
			return firstNeg;
		}
		return atomicPredicates;
	}
	
	public List<SimplePredicate> neg(SimplePredicate ap){
		List<SimplePredicate> neg = new ArrayList<>();
			//check IPSrc
			for(String src: ap.getIPSrcList()) {
				if(!src.equals("*")) {
					SimplePredicate sp = new  SimplePredicate(this, src, ap.isNegIPSrc(), "*", false, "*", false, "*", false);
					neg.add(sp);
					SimplePredicate sp2 = new  SimplePredicate(this, src, !ap.isNegIPSrc(), "*", false, "*", false, "*", false);
					neg.add(sp2);
				}
			}
			//check IPDst
			for(String dst: ap.getIPDstList()) {
				if(!dst.equals("*")) {
					SimplePredicate sp = new  SimplePredicate(this, "*", false, dst, ap.isNegIPDst(), "*", false, "*", false);
					neg.add(sp);
					SimplePredicate sp2 = new  SimplePredicate(this, "*", false, dst, !ap.isNegIPDst(), "*", false, "*", false);
					neg.add(sp2);
				}
			}
			//check pSrc
			for(String psrc: ap.getpSrcList()) {
				if(!psrc.equals("*")) {
					SimplePredicate sp = new  SimplePredicate(this, "*", false, "*", false, psrc, ap.isNegPSrc(), "*", false);
					neg.add(sp);
					SimplePredicate sp2 = new  SimplePredicate(this, "*", false, "*", false, psrc, !ap.isNegPSrc(), "*", false);
					neg.add(sp2);
				}
			}
			//check pDst
			for(String pdst: ap.getpDstList()) {
				if(!pdst.equals("*")) {
					SimplePredicate sp = new  SimplePredicate(this, "*", false, "*", false, "*", false, pdst, ap.isNegPDst());
					neg.add(sp);
					SimplePredicate sp2 = new  SimplePredicate(this, "*", false, "*", false, "*", false, pdst, !ap.isNegPDst());
					neg.add(sp2);
				}
			}
			
			//Now we have to compute the atomic Predicates
			neg = computeAtomicPredicatesForNeg(neg);
			//Remove form atomicPredicates the predicate equal to "this" AP
			int index = 0;
			for(SimplePredicate sp: neg) {
				if(APCompare(ap, sp)) {
					neg.remove(index);
					break;
				}
				index++;
			}
			return neg;
	}
	
	public List<SimplePredicate> computeAtomicPredicatesForNeg(List<SimplePredicate> predicates){
		List<SimplePredicate> retList = new ArrayList<>();
		List<SimplePredicate> tmpList = new ArrayList<>();
		int i = 0;
		
		while(i != predicates.size()) {
			if(i == 0) {
				retList.add(predicates.get(i));
				i++;
				retList.add(predicates.get(i));
				i++;
			}
			else {
				for(SimplePredicate sp: retList) {
					SimplePredicate res = computeIntersection(sp, predicates.get(i));
					SimplePredicate res2 =  computeIntersection(sp, predicates.get(i+1));
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
	
	public boolean APCompare(SimplePredicate p1, SimplePredicate p2) {
		
		if(p1.isNegIPSrc() != p2.isNegIPSrc() || p1.isNegIPDst() != p2.isNegIPDst() 
				|| p1.isNegPSrc() != p2.isNegPSrc() || p1.isNegPDst() != p2.isNegPDst())
			return false;
		//comparing lists size
		if(p1.getIPSrcList().size() != p2.getIPSrcList().size() || p1.getIPDstList().size() != p2.getIPDstList().size() 
				|| p1.getpSrcList().size() != p2.getpSrcList().size() || p1.getpDstList().size() != p2.getpDstList().size())
			return false;
		Collections.sort(p1.getIPSrcList());
		Collections.sort(p2.getIPSrcList());
		if(!p1.getIPSrcList().equals(p2.getIPSrcList()))
			return false;
		Collections.sort(p1.getIPDstList());
		Collections.sort(p2.getIPDstList());
		if(!p1.getIPDstList().equals(p2.getIPDstList()))
			return false;
		Collections.sort(p1.getpSrcList());
		Collections.sort(p2.getpSrcList());
		if(!p1.getpSrcList().equals(p2.getpSrcList()))
			return false;
		Collections.sort(p1.getpDstList());
		Collections.sort(p2.getpDstList());
		if(!p1.getpDstList().equals(p2.getpDstList()))
			return false;
		
		return true;
	}
	
	public SimplePredicate computeIntersection(SimplePredicate p1, SimplePredicate p2){
		SimplePredicate retPredicate = new SimplePredicate();
		List<String> IPSrcList;
		boolean negIPSrc;
		List<String> IPDstList;
		boolean negIPDst;
		List<String> pSrcList;
		boolean negpSrc;
		List<String> pDstList;
		boolean negpDst;
		
		//Check IPSrc
		if(!p1.isNegIPSrc()) {
			if(!p2.isNegIPSrc()) {   //both not neg
				IPSrcList = intersection(p1.getIPSrcList(), p2.getIPSrcList());
				negIPSrc = false;
			}
			else { //p1 not neg, p2 neg
				if(p1.getIPSrcList().contains("*")) {
					IPSrcList = new ArrayList<>(p2.getIPSrcList());
					negIPSrc = true;
				} else {
					IPSrcList = notContained(p1.getIPSrcList(), p2.getIPSrcList());
					negIPSrc = false;
				}
			}
		} else {
			if(!p2.isNegIPSrc()) { //p1 neg, p2 not neg
				if(p2.getIPSrcList().contains("*")) {
					IPSrcList = new ArrayList<>(p1.getIPSrcList());
					negIPSrc = true;
				} else {
					IPSrcList = notContained(p2.getIPSrcList(), p1.getIPSrcList());
					negIPSrc = false;
				}
			}
			else { //both neg
				IPSrcList = union(p1.getIPSrcList(), p2.getIPSrcList());
				negIPSrc = true;
			}
		}
		if(IPSrcList.isEmpty())
			return null;		//no intersection exists

		//Check IPDst
		if(!p1.isNegIPDst()) {
			if(!p2.isNegIPDst()) {   //both not neg
				IPDstList = intersection(p1.getIPDstList(), p2.getIPDstList());
				negIPDst = false;
			}
			else { //p1 not neg, p2 neg
				if(p1.getIPDstList().contains("*")) {
					IPDstList = new ArrayList<>(p2.getIPDstList());
					negIPDst = true;
				} else {
					IPDstList = notContained(p1.getIPDstList(), p2.getIPDstList());
					negIPDst = false;
				}
			}
		} else {
			if(!p2.isNegIPDst()) { //p1 neg, p2 not neg
				if(p2.getIPDstList().contains("*")) {
					IPDstList = new ArrayList<>(p1.getIPDstList());
					negIPDst = true;
				} else {
					IPDstList = notContained(p2.getIPDstList(), p1.getIPDstList());
					negIPDst = false;
				}
			}
			else { //both neg
				IPDstList = union(p1.getIPDstList(), p2.getIPDstList());
				negIPDst = true;
			}
		}
		if(IPDstList.isEmpty())
			return null;		//no intersection exists

		//Check pSrc
		if(!p1.isNegPSrc()) {
			if(!p2.isNegPSrc()) {   //both not neg
				pSrcList = intersection(p1.getpSrcList(), p2.getpSrcList());
				negpSrc = false;
			}
			else { //p1 not neg, p2 neg
				if(p1.getpSrcList().contains("*")) {
					pSrcList = new ArrayList<>(p2.getpSrcList());
					negpSrc = true;
				} else {
					pSrcList = notContained(p1.getpSrcList(), p2.getpSrcList());
					negpSrc = false;
				}
			}
		} else {
			if(!p2.isNegPSrc()) { //p1 neg, p2 not neg
				if(p2.getpSrcList().contains("*")) {
					pSrcList = new ArrayList<>(p1.getpSrcList());
					negpSrc = true;
				} else {
					pSrcList = notContained(p2.getpSrcList(), p1.getpSrcList());
					negpSrc = false;
				}
			}
			else { //both neg
				pSrcList = union(p1.getpSrcList(), p2.getpSrcList());
				negpSrc = true;
			}
		}
		if(pSrcList.isEmpty())
			return null;		//no intersection exists

		//Check pDst
		if(!p1.isNegPDst()) {
			if(!p2.isNegPDst()) {   //both not neg
				pDstList = intersection(p1.getpDstList(), p2.getpDstList());
				negpDst = false;
			}
			else { //p1 not neg, p2 neg
				if(p1.getpDstList().contains("*")) {
					pDstList = new ArrayList<>(p2.getpDstList());
					negpDst = true;
				} else {
					pDstList = notContained(p1.getpDstList(), p2.getpDstList());
					negpDst = false;
				}
			}
		} else {
			if(!p2.isNegPDst()) { //p1 neg, p2 not neg
				if(p2.getpDstList().contains("*")) {
					pDstList = new ArrayList<>(p1.getpDstList());
					negpDst = true;
				} else {
					pDstList = notContained(p2.getpDstList(), p1.getpDstList());
					negpDst = false;
				}
			}
			else { //both neg
				pDstList = union(p1.getpDstList(), p2.getpDstList());
				negpDst = true;
			}
		}
		if(pDstList.isEmpty())
			return null;		//no intersection exists

		retPredicate.setIPSrcList(IPSrcList);
		retPredicate.setIPDstList(IPDstList);
		retPredicate.setpSrcList(pSrcList);
		retPredicate.setpDstList(pDstList);
		retPredicate.setNegIPSrc(negIPSrc);
		retPredicate.setNegIPDst(negIPDst);
		retPredicate.setNegPSrc(negpSrc);
		retPredicate.setNegPDst(negpDst);
		return retPredicate;
	}
	
	private List<String> union(List<String> list1, List<String> list2){
		List<String> retList = new ArrayList<>();
		for(String str: list1) {
			if(!retList.contains(str))
				retList.add(str);
		}
		for(String str: list2) {
			if(!retList.contains(str))
				retList.add(str);
		}
		return retList;
	}
	
	private List<String> intersection(List<String> list1, List<String> list2){
		List<String> retList;
		if(list1.contains("*"))
			retList = new ArrayList<>(list2);
		else if(list2.contains("*"))
			retList = new ArrayList<>(list1);
		else {
			retList = new ArrayList<>();
			for(String str: list1) {
				if(list2.contains(str))
					retList.add(str);
			}
		}
		return retList;
	}
	
	private List<String> notContained(List<String> list1, List<String> list2){
		List<String> retList = new ArrayList<>();
		for(String str: list1) {
			if(!list2.contains(str))
				retList.add(str);
		}
		return retList;
	}

}
