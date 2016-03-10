package it.polito.escape.verify.database;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import it.polito.escape.verify.model.Node;

public class DatabaseClass {
	
	private static ConcurrentHashMap<Long, Node> nodes = new ConcurrentHashMap<>();
	
	public static ConcurrentHashMap<Long, Node> getNodes(){
		return nodes;
	}
	public synchronized static int getNumberOfNodes(){
		return nodes.size();
	}
}
