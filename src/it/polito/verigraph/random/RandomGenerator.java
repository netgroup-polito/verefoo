package it.polito.verigraph.random;

import java.io.File;
import java.io.IOException;
// import java.text.SimpleDateFormat; // for debug printing
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Random;
import java.util.Set;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;

import it.polito.verigraph.model.Graph;
import it.polito.verigraph.model.Neighbour;
import it.polito.verigraph.model.Node;

public class RandomGenerator {

    private Random random;

    private HashMap<String,GraphGen> graphs;
    private HashMap<String,PolicyGen> policies;
    it.polito.verifoo.rest.jaxb.Graph translatedGraph;
    public RandomGenerator() {
        long seed=new Date().getTime();
        init(seed,4,4,10);
        ObjectMapper mapper = new ObjectMapper();
        Graph g = this.getGraphs().stream().findFirst().get();
    	//this.pringGraph(g);
        try {
            // Serialize Java object info JSON file under the examples folders
            //mapper.writeValue(file, g);
            String str =mapper.writeValueAsString(g);
            //System.out.println(str);
            str = str.replace("\"nodes\"", "\"node\"");
            str = str.replace("\"neighbours\"", "\"neighbour\"");
            str = str.replace("\"functional_type\"", "\"functionalType\"");
            //System.out.println(str);
            it.polito.verifoo.rest.jaxb.Graph graph = new it.polito.verifoo.rest.jaxb.Graph();
            ObjectMapper objectMapper = new ObjectMapper();
            translatedGraph = (it.polito.verifoo.rest.jaxb.Graph) objectMapper.readValue(str, it.polito.verifoo.rest.jaxb.Graph.class);
           } catch (IOException e) {
                e.printStackTrace();
                return;
            }

    }

    public RandomGenerator(int maxClients, int maxServers, int maxInternalNodes) {
    	long seed=new Date().getTime();
        init(seed, maxClients, maxServers, maxInternalNodes);
        ObjectMapper mapper = new ObjectMapper();
        Graph g = this.getGraphs().stream().findFirst().get();
    	//this.pringGraph(g);
        try {
            // Serialize Java object info JSON file under the examples folders
            //mapper.writeValue(file, g);
            String str =mapper.writeValueAsString(g);
            //System.out.println(str);
            str = str.replace("\"nodes\"", "\"node\"");
            str = str.replace("\"neighbours\"", "\"neighbour\"");
            str = str.replace("\"functional_type\"", "\"functionalType\"");
            //System.out.println(str);
            it.polito.verifoo.rest.jaxb.Graph graph = new it.polito.verifoo.rest.jaxb.Graph();
            ObjectMapper objectMapper = new ObjectMapper();
            translatedGraph = (it.polito.verifoo.rest.jaxb.Graph) objectMapper.readValue(str, it.polito.verifoo.rest.jaxb.Graph.class);
           } catch (IOException e) {
                e.printStackTrace();
                return;
            }
	}

	public it.polito.verifoo.rest.jaxb.Graph getTranslatedGraph() {
		return translatedGraph;
	}

	private void init(long seed, int maxClients, int maxServers, int maxInternalNodes) {
        //System.out.println("Random Graph Generator: Using seed: " + seed);

        // initialize random generator and start time
        random = new Random(seed);
        // initialize hash maps
        graphs = new HashMap<String,GraphGen>();
        policies = new HashMap<String,PolicyGen>();

        // create graphs and policies
        int numGraphs = random.nextInt(1)+1; // at least 1 graph
        for (int i=0; i<numGraphs; i++) {
            // create single graph
            GraphGen graph = new GraphGen(random,i, maxClients, maxServers, maxInternalNodes);
            // and add it to hash map
            graphs.put("Graph"+graph.getId(), graph);

            // extract policies from this graph
            HashMap<String,PolicyGen> policyMap = graph.getPolicies();
            // and put them all into the policies hash map
            policies.putAll(policyMap);
            // and into the set of policies associated with the current graph
            //policiesByGraphName.put(graph.getName(), new LinkedHashSet<PolicyImpl>(policyMap.values()));
        }
    }

    public Set<Graph> getGraphs() {
        if (graphs == null)
            return new LinkedHashSet<Graph>();
        else
            return new LinkedHashSet<Graph>(graphs.values());
    }

    public Graph getGraph(String name) {
        if (graphs != null && graphs.containsKey(name)) // could remove second check
            return graphs.get(name);
        else
            return null;
    }

    public Set<PolicyGen> getPolicies() {
        if (policies == null)
            return new LinkedHashSet<PolicyGen>();
        else
            return new LinkedHashSet<PolicyGen>(policies.values());
    }
    
    public static void main(String [] args){
        //Cerate Random Generator and initialize it
        RandomGenerator gen = new RandomGenerator();
        ObjectMapper mapper = new ObjectMapper();

        System.out.println("Number of graphs: "+ gen.getGraphs().size());
        for(Graph g : gen.getGraphs()){
            gen.pringGraph(g);
            File file = new File("./testfile/Random/graph_"+g.getId()+".json");
            try {
                // Serialize Java object info JSON file under the examples folders
                mapper.writeValue(file, g);
                String str =mapper.writeValueAsString(g);
                System.out.println(str);
                str = str.replace("\"nodes\"", "\"node\"");
                str = str.replace("\"neighbours\"", "\"neighbour\"");
                str = str.replace("\"functional_type\"", "\"functionalType\"");
                System.out.println(str);
                it.polito.verifoo.rest.jaxb.Graph graph = new it.polito.verifoo.rest.jaxb.Graph();
                ObjectMapper objectMapper = new ObjectMapper();
            	graph = (it.polito.verifoo.rest.jaxb.Graph) objectMapper.readValue(str, it.polito.verifoo.rest.jaxb.Graph.class);
                JAXBContext jc;
                // create a JAXBContext capable of handling the generated classes
    			jc= JAXBContext.newInstance( "it.polito.verifoo.rest.jaxb" );
                Marshaller m = jc.createMarshaller();
                m.setProperty( Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE );
                m.setProperty( Marshaller.JAXB_NO_NAMESPACE_SCHEMA_LOCATION,"./xsd/nfvSchema.xsd");
                m.marshal( graph, System.out ); 
            } catch (IOException | JAXBException e) {
                e.printStackTrace();
                return;
            }
        }
    }

    public void pringGraph(Graph g){
        if(g instanceof GraphGen){
            System.out.println("Graph ID: "+ g.getId());
            for(Node node: g.getNodes().values()){
                System.out.println("Node ID: "+ node.getId());
                System.out.println("Node NAME: "+ node.getName());
                System.out.println("Node TYPE: "+ node.getFunctional_type().toString());
                if(node.getConfiguration()!=null)
                    if(node.getConfiguration().getConfiguration()!=null){
                        System.out.println("Node CONF: "+ node.getConfiguration().getConfiguration().toString());
                        System.out.println("Node CONF_DESC: "+ node.getConfiguration().getDescription());
                    }
                System.out.println("Node NEIGHBOUR: ");
                if(node.getNeighbours() != null)
                    for(Neighbour n : node.getNeighbours().values()){
                        System.out.println("----"+n.getId()+"----> "+n.getName());
                    }
            }
            System.out.println("----------------------");
            for(PolicyGen policy : ((GraphGen) g).getPolicies().values()){
                System.out.println("Policiy NAME: "+ policy.getName());
                System.out.println("Policiy NODE_SRC: "+ policy.getNodesrc().getName());
                System.out.println("Policiy NODE_DST: "+ policy.getNodedst().getName());
                System.out.println("Policiy TYPE: "+ policy.getPolicyType());
            }

        }
    }
}
