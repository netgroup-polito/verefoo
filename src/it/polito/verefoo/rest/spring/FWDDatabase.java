package it.polito.verefoo.rest.spring;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;

import it.polito.verefoo.firewall.BPFFirewall;
import it.polito.verefoo.firewall.Fortinet;
import it.polito.verefoo.firewall.IpFirewall;
import it.polito.verefoo.firewall.Iptables;
import it.polito.verefoo.firewall.OpenvSwitch;
import it.polito.verefoo.jaxb.Configuration;
import it.polito.verefoo.jaxb.Elements;
import it.polito.verefoo.jaxb.Firewall;
import it.polito.verefoo.jaxb.FunctionalTypes;
import it.polito.verefoo.jaxb.Node;

public class FWDDatabase {
	private static FWDDatabase db = new FWDDatabase();
	private static long lastNodeID = 0;
	private static long lastElementID = 0;
	private ConcurrentHashMap<Long, Node> nodes;
	private ConcurrentHashMap<Long, ConcurrentHashMap<Long, Integer>> policyByNodeId;

	FWDDatabase() {
		nodes = new ConcurrentHashMap<Long, Node>();
		policyByNodeId = new ConcurrentHashMap<Long, ConcurrentHashMap<Long, Integer>>();

	}

	public static FWDDatabase getDatabase() {
		return db;
	}

	public synchronized long getNextNodeId() {
		return ++lastNodeID;
	}

	public synchronized long getNextElementId() {
		return ++lastElementID;
	}

	public synchronized Node createNode(Long nid, Node node) {
		if (node.getFunctionalType().equals(FunctionalTypes.FIREWALL))
			return null;
		node.setId(nid);
		if (nodes.putIfAbsent(nid, node) == null && policyByNodeId.get(nid)==null) {
			List <Elements> policy =node.getConfiguration().getFirewall().getElements();
			ConcurrentHashMap<Long, Integer> policyMap = new ConcurrentHashMap<Long, Integer>();
			if(policy!=null) {
				long id;
				for (int i = 0; i < policy.size(); i++) {
					id=getNextElementId();
						policyMap.put(id, i);

					node.getConfiguration().getFirewall().getElements().get(i).setId(id);
					
				}
				policyByNodeId.put(nid, policyMap);
			}

			return node;
		} else {
			Node empty = new Node();
			empty.setName(null);
			return empty;
		}
	}

	public List<Node> getNodes(int beforeInclusive, int afterInclusive) {
		// TODO Auto-generated method stub
		List<Node> list = new ArrayList<>();
		if (beforeInclusive < afterInclusive && afterInclusive - beforeInclusive > 20)
			return null;
		Node n;
		for (long i = beforeInclusive; i <= afterInclusive; i++) {
			n = nodes.get(i);
			if (n != null)
				list.add(n);
		}
		return list;
	}

	public Node getNode(long nid) {
		// TODO Auto-generated method stub
		Node n;
		n = nodes.get(nid);
		if (n == null)
			return null;
		else {
			return n;
		}
	}

	public synchronized List<Node> deleteNodes() {
		// TODO Auto-generated method stub
		if (nodes.isEmpty() && policyByNodeId.isEmpty())
			return null;
		nodes.clear();
		policyByNodeId.clear();
		return new LinkedList<Node>();
	}

	public synchronized Node updateNode(long nid, Node node) {
		// TODO Auto-generated method stub
		if (nodes.get(nid) == null)
			return null;
		boolean add = false;
		long id = 0;
		List<Elements> policy = node.getConfiguration().getFirewall().getElements();
		ConcurrentHashMap<Long, Integer> policyMap = new ConcurrentHashMap<Long, Integer>();
		for (int i = 0; i < policy.size(); i++) {
			if (policy.get(i).getId() != null) {
				id = policy.get(i).getId();
				if (policyByNodeId.get(nid).get(id) != null) {
					policyMap.put(id, i);
				} else {
					add = true;
				}
			} else {
				add = true;
			}
			if (add) {
				id = getNextElementId();
				policyMap.put(id, i);
			}

			node.getConfiguration().getFirewall().getElements().get(i).setId(id);
			add = false;
		}
		node.setId(nid);
		nodes.compute(nid, (k, v) -> v = node);
		policyByNodeId.get(nid).clear();
		policyByNodeId.get(nid).putAll(policyMap);
		return node;

	}

	public synchronized Node deleteNode(long nid) {
		// TODO Auto-generated method stub
		Node n;
		n = nodes.remove(nid);
		policyByNodeId.remove(nid);
		if (n == null)
			return null;
		return n;
	}

	public synchronized Configuration updateConfiguration(long nid, Configuration configuration) {
		// TODO Auto-generated method stub
		if (nodes.get(nid) == null)
			return null;

		boolean add = false;
		long id = 0;
		List<Elements> policy = configuration.getFirewall().getElements();
		ConcurrentHashMap<Long, Integer> policyMap = new ConcurrentHashMap<Long, Integer>();
		for (int i = 0; i < policy.size(); i++) {
			if (policy.get(i).getId() != null) {
				id = policy.get(i).getId();
				if (policyByNodeId.get(nid).get(id) != null) {
					policyMap.put(id, i);
				} else {
					add = true;
				}
			} else {
				add = true;
			}
			if (add) {
				id = getNextElementId();
				policyMap.put(id, i);
			}

			configuration.getFirewall().getElements().get(i).setId(id);
			add = false;
		}

		policyByNodeId.get(nid).clear();
		policyByNodeId.get(nid).putAll(policyMap);
		nodes.get(nid).setConfiguration(configuration);
		return configuration;
	}

	public synchronized Firewall createFirewall(long nid, Firewall firewall) {
		// TODO Auto-generated method stub
		if (nodes.get(nid) == null)
			return null;

		if (firewall.getDefaultAction().equals(nodes.get(nid).getConfiguration().getFirewall().getDefaultAction())) {
			boolean add = false;
			long id = 0;
			List<Elements> policy = firewall.getElements();
			ConcurrentHashMap<Long, Integer> policyMap = new ConcurrentHashMap<Long, Integer>();
			for (int i = 0; i < policy.size(); i++) {
				if (policy.get(i).getId() != null) {
					id = policy.get(i).getId();
					if (policyByNodeId.get(nid).get(id) != null) {
						policyMap.put(id, i);
					} else {
						add = true;
					}
				} else {
					add = true;
				}
				if (add) {
					id = getNextElementId();
					policyMap.put(id, i);
				}

				firewall.getElements().get(i).setId(id);
				add = false;
			}

			policyByNodeId.get(nid).clear();
			policyByNodeId.get(nid).putAll(policyMap);
			nodes.get(nid).getConfiguration().setFirewall(firewall);
			return firewall;
		} else {
			Firewall f = new Firewall();
			f.setDefaultAction(null);
			return f;
		}

	}

	public synchronized Elements createPolicy(long nid, long eid, Elements element) {
		// TODO Auto-generated method stub
		if (nodes.get(nid) == null)
			return null;

		if (policyByNodeId.get(nid).get(eid) != null) {
			Elements e = new Elements();
			e.setAction(null);
		}
		int index;

		index = nodes.get(nid).getConfiguration().getFirewall().getElements().size();
		nodes.get(nid).getConfiguration().getFirewall().getElements().add(element);
		policyByNodeId.get(nid).put(eid, index);
		element.setId(eid);
		return element;
	}

	public synchronized Elements updatePolicy(long nid, long eid, Elements policy) {
		// TODO Auto-generated method stub
		if (nodes.get(nid) == null)
			return null;
		if (policyByNodeId.get(nid).get(eid) == null)
			return null;
		if (policy.getId() != eid) {
			Elements e = new Elements();
			e.setAction(null);
		}

		policy.setId(eid);
		int index = policyByNodeId.get(nid).remove(eid);
		nodes.get(nid).getConfiguration().getFirewall().getElements().remove(index);
		index = nodes.get(nid).getConfiguration().getFirewall().getElements().size();
		nodes.get(nid).getConfiguration().getFirewall().getElements().add(policy);
		policyByNodeId.get(nid).put(eid, index);
		return policy;
	}

	public Elements getPolicy(long nid, long eid) {
		// TODO Auto-generated method stub
		if (nodes.get(nid) == null)
			return null;
		if (policyByNodeId.get(nid).get(eid) == null)
			return null;

		int index = policyByNodeId.get(nid).get(eid);
		return nodes.get(nid).getConfiguration().getFirewall().getElements().get(index);
	}

	public synchronized Elements deletePolicy(long nid, long eid) {
		// TODO Auto-generated method stub
		if (nodes.get(nid) == null)
			return null;
		if (policyByNodeId.get(nid).get(eid) == null)
			return null;

		int index = policyByNodeId.get(nid).remove(eid);
		return nodes.get(nid).getConfiguration().getFirewall().getElements().remove(index);

	}
	
	

	public Resource createFortinetFirewall(long nid) {
		Node n = nodes.get(nid);
		if (n == null)
			return null;
		try {
			Fortinet fw = new Fortinet(nid,n);
			fw.getFilename();
			File file = new File(fw.getFilename());
			String absolutePath = file.getAbsolutePath();
			Resource resource = new UrlResource(absolutePath);
			if(resource.exists()) {
	            return resource;
	        } else {
	           return null;
	        }
		} catch (Exception e) {
			
			return null;
		}
	}

	public Resource createIPFWFirewall(long nid) {
		// TODO Auto-generated method stub
		Node n = nodes.get(nid);
		if (n == null)
			return null;
		try {
			IpFirewall fw = new IpFirewall(nid,n);
			fw.getFilename();
			File file = new File(fw.getFilename());
			String absolutePath = file.getAbsolutePath();
			Resource resource = new UrlResource(absolutePath);
			if(resource.exists()) {
	            return resource;
	        } else {
	           return null;
	        }
		} catch (Exception e) {
			
			return null;
		}
	}

	public Resource createIptablesFirewall(long nid) {
		// TODO Auto-generated method stub
		Node n = nodes.get(nid);
		if (n == null)
			return null;
		try {
			Iptables fw = new Iptables(nid,n);
			fw.getFilename();
			File file = new File(fw.getFilename());
			String absolutePath = file.getAbsolutePath();
			Resource resource = new UrlResource(absolutePath);
			if(resource.exists()) {
	            return resource;
	        } else {
	           return null;
	        }
		} catch (Exception e) {
			
			return null;
		}
	}

	public Resource createOpnvswitchFirewall(long nid) {
		// TODO Auto-generated method stub
		Node n = nodes.get(nid);
		if (n == null)
			return null;
		try {
			OpenvSwitch fw = new OpenvSwitch(nid,n);
			fw.getFilename();
			File file = new File(fw.getFilename());
			String absolutePath = file.getAbsolutePath();
			Resource resource = new UrlResource(absolutePath);
			if(resource.exists()) {
	            return resource;
	        } else {
	           return null;
	        }
		} catch (Exception e) {
			
			return null;
		}
	}

	public Resource createBPFFirewall(long nid) {
		// TODO Auto-generated method stub
		Node n = nodes.get(nid);
		if (n == null)
			return null;
		try {
			BPFFirewall fw = new BPFFirewall(nid,n);
			fw.getFilename();
			File file = new File(fw.getFilename());
			String absolutePath = file.getAbsolutePath();
			Resource resource = new UrlResource(absolutePath);
			if(resource.exists()) {
	            return resource;
	        } else {
	           return null;
	        }
		} catch (Exception e) {
			
			return null;
		}
	}
	
	
	

}
