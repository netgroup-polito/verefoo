package it.polito.escape.verify.resources;

import java.util.*;
import java.util.Map.Entry;
import javax.xml.bind.annotation.*;
import javax.xml.bind.annotation.adapters.XmlAdapter;

import org.eclipse.persistence.oxm.annotations.XmlVariableNode;

import it.polito.escape.verify.model.Node;
 
public class GraphMapAdapter extends XmlAdapter<GraphMapAdapter.GraphAdaptedMap, Map<Long, String>> {
 
    public static class GraphAdaptedMap {
         
        @XmlVariableNode("key")
        List<GraphAdaptedEntry> entries = new ArrayList<GraphAdaptedEntry>();
         
    }
 
    public static class GraphAdaptedEntry {
         
        @XmlTransient
        public Long key;
         
        @XmlValue
        public String nodeId;
 
    }
 
    @Override
    public GraphAdaptedMap marshal(Map<Long, String> map) throws Exception {
        GraphAdaptedMap adaptedMap = new GraphAdaptedMap();
        for(Entry<Long, String> entry : map.entrySet()) {
            GraphAdaptedEntry adaptedEntry = new GraphAdaptedEntry();
            adaptedEntry.key = entry.getKey();
            adaptedEntry.nodeId = entry.getValue();
            adaptedMap.entries.add(adaptedEntry);
        }
        return adaptedMap;
    }
 
    @Override
    public Map<Long, String> unmarshal(GraphAdaptedMap adaptedMap) throws Exception {
        List<GraphAdaptedEntry> adaptedEntries = adaptedMap.entries;
        Map<Long, String> map = new HashMap<Long, String>(adaptedEntries.size());
        for(GraphAdaptedEntry adaptedEntry : adaptedEntries) {
            map.put(adaptedEntry.key, adaptedEntry.nodeId);
        }
        return map;
    }
 
}