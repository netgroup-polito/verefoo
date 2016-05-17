package it.polito.escape.verify.resources;

import java.util.*;
import java.util.Map.Entry;
import javax.xml.bind.annotation.*;
import javax.xml.bind.annotation.adapters.XmlAdapter;

import org.eclipse.persistence.oxm.annotations.XmlVariableNode;

import it.polito.escape.verify.model.Neighbour;
 
public class NodeMapAdapter extends XmlAdapter<NodeMapAdapter.NodeAdaptedMap, Map<Long, String>> {
 
    public static class NodeAdaptedMap {
         
        @XmlVariableNode("key")
        List<NodeAdaptedEntry> entries = new ArrayList<NodeAdaptedEntry>();
         
    }
 
    public static class NodeAdaptedEntry {
         
        @XmlTransient
        public Long key;
         
        @XmlValue
        public String neighbourId;
 
    }
 
    @Override
    public NodeAdaptedMap marshal(Map<Long, String> map) throws Exception {
        NodeAdaptedMap adaptedMap = new NodeAdaptedMap();
        for(Entry<Long, String> entry : map.entrySet()) {
            NodeAdaptedEntry adaptedEntry = new NodeAdaptedEntry();
            adaptedEntry.key = entry.getKey();
            adaptedEntry.neighbourId = entry.getValue();
            adaptedMap.entries.add(adaptedEntry);
        }
        return adaptedMap;
    }
 
    @Override
    public Map<Long, String> unmarshal(NodeAdaptedMap adaptedMap) throws Exception {
        List<NodeAdaptedEntry> adaptedEntries = adaptedMap.entries;
        Map<Long, String> map = new HashMap<Long, String>(adaptedEntries.size());
        for(NodeAdaptedEntry adaptedEntry : adaptedEntries) {
            map.put(adaptedEntry.key, adaptedEntry.neighbourId);
        }
        return map;
    }
 
}