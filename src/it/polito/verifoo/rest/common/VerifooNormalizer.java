package it.polito.verifoo.rest.common;

import java.util.List;
import java.util.Map;

import javax.xml.XMLConstants;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.PropertyException;
import javax.xml.bind.Unmarshaller;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import org.apache.commons.lang3.SerializationUtils;
import org.xml.sax.SAXException;

import it.polito.verifoo.rest.jaxb.*;
import static java.util.stream.Collectors.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
public class VerifooNormalizer {
	private NFV root, originalNfv;
	private Map<String, String> networkGroups, flowGroups;
	public VerifooNormalizer(NFV root){
		try{
			JAXBContext jc = JAXBContext.newInstance( "it.polito.verifoo.rest.jaxb" );
			Marshaller m = jc.createMarshaller();
	        m.setProperty( Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE );
	        m.setProperty( Marshaller.JAXB_NO_NAMESPACE_SCHEMA_LOCATION,"./xsd/nfvSchema.xsd");
	        OutputStream out = new FileOutputStream("./testfile/tmp.xml");
	        m.marshal( root, out ); 
	        Unmarshaller u = jc.createUnmarshaller();
	        SchemaFactory sf = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI); 
	        Schema schema;
			schema = sf.newSchema( new File( "./xsd/nfvSchema.xsd" ));
			u.setSchema(schema);
			
			
			networkGroups = new HashMap<>();
			flowGroups = new HashMap<>();

			this.originalNfv = root;
			this.root = (NFV) u.unmarshal( new FileInputStream( "./testfile/tmp.xml" ) );
			//this.root = root;
			normalize();
		}catch(Exception e){
			throw new BadGraphError("Error during deserializing");
		}
	}
	
	private void normalize(){
		normalizeNetworks();
		normalizeSameFlows();
		//System.out.println(flowGroups);
		//System.out.println(networkGroups);
		/*try {
			JAXBContext jc = JAXBContext.newInstance( "it.polito.verifoo.rest.jaxb" );
			Marshaller m = jc.createMarshaller();
	        m.setProperty( Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE );
	        m.setProperty( Marshaller.JAXB_NO_NAMESPACE_SCHEMA_LOCATION,"./xsd/nfvSchema.xsd");
	        m.marshal( root.getPropertyDefinition(), System.out ); 
		} catch (JAXBException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
	}
	
	private void normalizeNetworks() {
		List<Property> rootProperties = root.getPropertyDefinition().getProperty();
		root.getGraphs().getGraph().forEach((g) -> {
			List<Node> nodes = g.getNode();
			Map<String,List<Property>> props = root.getPropertyDefinition().getProperty().stream()
																			.filter(p -> p.getGraph() == g.getId() && p.getSrc().contains("-1"))
																			.collect(groupingBy(p -> p.getSrc(),toList()));
			props.entrySet().forEach(e -> {
				List<Node> nodesInNetwork = nodes.stream()
												.filter(n -> inNetwork(e.getKey(),n.getName()) && 
														(n.getFunctionalType().equals(FunctionalTypes.WEBCLIENT) ||
														 n.getFunctionalType().equals(FunctionalTypes.MAILCLIENT) ||
														 n.getFunctionalType().equals(FunctionalTypes.ENDHOST)))
												.collect(toList());
				if(nodesInNetwork.isEmpty())
					throw new BadGraphError("You specified a network ("+e.getKey()+") in the property that contains none of the nodes declared in the service graph", EType.INVALID_PROPERTY_DEFINITION);
				e.getValue().forEach(p -> {
					nodesInNetwork.forEach(n -> {
						Property newP = copyProperty(p);
						newP.setSrc(n.getName());
						networkGroups.put(n.getName(), e.getKey());
						rootProperties.add(newP);
					});
					rootProperties.remove(p);
				});
				
			});
		});
	}

	private Property copyProperty(Property p) {
		Property newP = new Property();
		newP.setDst(p.getDst());
		newP.setDstPort(p.getDstPort());
		newP.setGraph(p.getGraph());
		newP.setHTTPDefinition(p.getHTTPDefinition());
		newP.setLv4Proto(p.getLv4Proto());
		newP.setName(p.getName());
		newP.setPOP3Definition(p.getPOP3Definition());
		newP.setSrc(p.getSrc());
		newP.setSrcPort(p.getSrcPort());
		return newP;
	}

	private void normalizeSameFlows(){
		
		root.getGraphs().getGraph().forEach((g) -> {
			List<Node> nodes = g.getNode();
			Map<String,List<Property>> props = root.getPropertyDefinition().getProperty().stream()
																			.filter(p -> p.getGraph() == g.getId())
																			.collect(groupingBy(p -> p.getSrc()+"_"+p.getDst(),toList()));
			props.entrySet().forEach(e -> {
				if(e.getValue().size() > 1){
					List<String> abstractNodes = new ArrayList<>();
					Node src = nodes.stream().filter(n -> n.getName().equals(e.getValue().get(0).getSrc())).findFirst().get();
					for(int i = 0; i < e.getValue().size(); i++){
						Node abstractDuplicate = new Node();
						abstractDuplicate.setName(src.getName()+"_"+i);
						abstractDuplicate.setFunctionalType(src.getFunctionalType());
						abstractDuplicate.setConfiguration(src.getConfiguration());
						abstractDuplicate.getNeighbour().addAll(src.getNeighbour());
						flowGroups.put(abstractDuplicate.getName(), src.getName());
						g.getNode().add(abstractDuplicate);
						e.getValue().get(i).setSrc(abstractDuplicate.getName());
						abstractNodes.add(abstractDuplicate.getName());
					}
					nodes.forEach(n ->{
						List<Neighbour> neighbours = n.getNeighbour();
						List<Neighbour> addNeighbours = new ArrayList<>();
						List<Neighbour> removeNeighbours = new ArrayList<>();
						for(Neighbour neigh : neighbours) {
							if(neigh.getName().equals(src.getName())){
								abstractNodes.forEach( absNode -> {
									Neighbour newN = new Neighbour();
									newN.setName(absNode);
									addNeighbours.add(newN);
								});
								removeNeighbours.add(neigh);
							}
						}
						neighbours.removeAll(removeNeighbours);
						neighbours.addAll(addNeighbours);
					});
					nodes.remove(src);
				}
			});
		});
	}
	
	private boolean inNetwork(String network, String ip){
    	String[] decimalNotationIp = ip.split("\\.");
    	String[] decimalNotationNetwork = network.split("\\.");
    	int i = 0;
    	for(String s : decimalNotationNetwork){
    		if(!decimalNotationIp[i].equals(s) && !s.equals("-1"))
    			return false;
    		i++;
    	}
    	return true;
	}

	public NFV getOriginalNfv() {
		return originalNfv;
	}

	public void setOriginalNfv(NFV originalRoot) {
		this.originalNfv = originalRoot;
	}

	/**
	 * @return the normalized NFV root element
	 */
	public NFV getRoot() {
		return root;
	}

	/**
	 * @return the networkGroups
	 */
	public Map<String, String> getNetworkGroups() {
		return networkGroups;
	}

	/**
	 * @return the flowGroups
	 */
	public Map<String, String> getFlowGroups() {
		return flowGroups;
	}
	
	public Graph getOriginalGraph(long id){
		return originalNfv.getGraphs().getGraph().stream().filter(g -> g.getId()==id).findFirst().orElse(null);		
	}

}
