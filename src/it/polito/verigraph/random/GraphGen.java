package it.polito.verigraph.random;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Random;
import java.util.Vector;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;

import it.polito.verifoo.rest.jaxb.FunctionalTypes;
import it.polito.verigraph.model.Configuration;
import it.polito.verigraph.model.Graph;
import it.polito.verigraph.model.Neighbour;
import it.polito.verigraph.model.Node;
import it.polito.verigraph.random.PolicyGen.PolicyType;

class GraphGen extends Graph {

    private HashMap<String,PolicyGen> policies;

    private Node clientNodes[];
    private Node serverNodes[];
    private Node middleNodes[];

    private FunctionalTypes clientTypes[]={FunctionalTypes.ENDHOST};
    private FunctionalTypes serverTypes[]={FunctionalTypes.MAILSERVER,FunctionalTypes.WEBSERVER};
    private FunctionalTypes middleTypes[]={FunctionalTypes.ANTISPAM,FunctionalTypes.CACHE,FunctionalTypes.DPI, FunctionalTypes.FIELDMODIFIER, FunctionalTypes.FIREWALL,FunctionalTypes.NAT};

    private PolicyGen.PolicyType types[]={PolicyType.REACHABILITY, PolicyType.ISOLATION,PolicyType.TRAVERSAL};

    private static int count=0;

    public static void resetCounter() {
        count=0;
    }

    GraphGen(Random random, long seed) {
        super(count);
        count++;

        // create policies hash map
        policies = new HashMap<String,PolicyGen>();

        // create nodes
        clientNodes = createNodeSubset(clientTypes, random, 10);
        serverNodes = createNodeSubset(serverTypes, random, 10);
        middleNodes = createNodeSubset(middleTypes, random, 20);

        // create links
        createLinks(random);

        // create policies for this nffg
        createPolicies(random);

    }

    /**
     * Creates a random set of new nodes with types taken from a specified list of types
     * The new nodes are added to the instance nodes set
     * The nodes set is assumed to be already existing (not null)
     * @param types	the types to be used for the new nodes
     * @param random	the random generator to be used for initializing the new nodes
     * @param maxNum	the maximum number of new nodes to be created
     * @return	an array including all the new nodes created
     */
    private Node[] createNodeSubset(FunctionalTypes types[], Random random, int maxNum) {

        if (types == null || types.length == 0)
            return new Node[0];

        int numNodes = random.nextInt(maxNum); // at least 1 node
        if((numNodes%2)==0)
            numNodes +=2;
        else numNodes +=1;
        int existingnode = nodes.size();
        Vector<Node> nodeSubset = new Vector<Node>();

        for (int i=0; i<numNodes; i++) {
            // create and store single node
            FunctionalTypes type;
            if(types.length==1 || numNodes == 1)
                type= types[0];
            else type = chooseType(types,random);
            String name =(type.toString().replace("_", ""))+i;

            Configuration conf = createConfiguration(name,type.toString(), random.nextBoolean()); //TODO
            nodeSubset.add(i, new Node(existingnode+i,name,type.toString().toLowerCase(), conf)); //No configuration yet
            nodes.put(nodeSubset.get(i).getId(), nodeSubset.get(i));

            if(type == FunctionalTypes.VPNACCESS){
                i++;
                name = (FunctionalTypes.VPNEXIT.toString().replace("_", ""))+i;
                conf= createConfiguration(name, FunctionalTypes.VPNEXIT.toString(), true);
                nodeSubset.add(i, new Node(i,name,FunctionalTypes.VPNEXIT.toString().toLowerCase(), conf));
                nodes.put(nodeSubset.get(i).getId(), nodeSubset.get(i));
                //numNodes -=1;
            } else if (type == FunctionalTypes.VPNEXIT) {
                i++;
                name = (FunctionalTypes.VPNACCESS.toString().replace("_", ""))+i;
                conf= createConfiguration(name, FunctionalTypes.VPNACCESS.toString(), true);
                nodeSubset.add(i,new Node(i,name,FunctionalTypes.VPNACCESS.toString().toLowerCase(), conf));
                nodes.put(nodeSubset.get(i).getId(), nodeSubset.get(i));
                numNodes-=1;
            }

        }
        Node[] nodes = new Node[nodeSubset.size()];
        return nodeSubset.toArray(nodes);
    }

    private FunctionalTypes chooseType(FunctionalTypes[] array, Random random) {
        // choose node type randomly
        return array[random.nextInt(array.length)];
    }

    private Configuration createConfiguration (String nodename, String functype, boolean nextBoolean) { //TODO complete configurations

        Configuration conf=null;
        JsonNodeFactory nodeFactory = JsonNodeFactory.instance;
        ObjectNode conf_node = nodeFactory.objectNode();
        ArrayNode conf_array = nodeFactory.arrayNode();
        switch(functype){
        case "ENDHOST":
            if(!nextBoolean){
                conf_node.put("url", "www.deepweb.com");
                conf_node.put("body", "weapons");
                conf_node.put("email_from", "spam@polito.it");
            } else{
                conf_node.put("url", "www.facebook.com");
                conf_node.put("body", "cats");
                conf_node.put("email_from", "verigraph@polito.it");
            }
            conf_array.add(conf_node);
            conf = new Configuration(nodename, "Endhost configuration", conf_array);
            break;
        case "MAILSERVER":
            //conf = new Configuration(nodename, "Mail Server configuration", conf_array);
            break;
        case "WEBSERVER":
            //conf = new Configuration(nodename, "Web Sever configuration", conf_array);  //do not print
            break;
        case "ANTISPAM":
            if(!nextBoolean){
                conf_array.add("spam@polito.it");
            } else{
                conf_array.add("verigraph@polito.it");
            }
            conf = new Configuration(nodename, "Antispam configuration", conf_array);
            break;
        case "CACHE":
            for(Node n : clientNodes)
                conf_array.add(n.getName());
            conf = new Configuration(nodename, "Web Cache configuration", conf_array);
            break;
        case "DPI":
            conf_array.add("weapons");
            conf = new Configuration(nodename, "DPI configuration", conf_array);
            break;
        case "FIELDMODIFIER":
            if(!nextBoolean){
                //conf_node.put("url", "www.facebook.com");
                conf_node.put("body", "weapon");
                conf_node.put("email_from", "spam@polito.it");
                conf_array.add(conf_node);
            }
            conf = new Configuration(nodename, "FieldModifier configuration", conf_array);
            break;
        case "FIREWALL":
            if(!nextBoolean){
                //DROPS ANY PACKET
                for(Node client : clientNodes){
                    for(Node server : serverNodes)
                        conf_node.put(client.getName(), server.getName());
                }
                conf_array.add(conf_node);
            }
            conf = new Configuration(nodename, "Firewall configuration", conf_array);
            break;
        case "NAT" :
            for(Node n : clientNodes)
                conf_array.add(n.getName());
            conf = new Configuration(nodename, "NAT configuration", conf_array);
            break;
        case "VPNACCESS":
            conf_node.put("vpnexit", "VPNEXIT"+(Integer.parseInt(nodename.substring(9))+1)); //"vpnaccess"-> 9 char
            conf_array.add(conf_node);
            conf = new Configuration(nodename, "VPN Access gateway configuration", conf_array);
            break;
        case "VPNEXIT":
            conf_node.put("vpnaccess", "VPNACCESS"+(Integer.parseInt(nodename.substring(7))+1)); //"vpnexit"-> 7 char nodename.replaceFirst("VPNEXIT", "VPNACCESS")
            conf_array.add(conf_node);
            conf = new Configuration(nodename, "VPN Exit gateway configuration", conf_array);
            break;
        default:
            return null;
        }

        return conf;
    }

    private void createLinks(Random random) {
        int m ;
        //HashMap<Long, LinkGen> possibleLinks = new HashMap<Long,LinkGen>();

        HashMap<Long, Neighbour> neighbourfromnode;
        Neighbour node;
        // create links for each client
        for (Node client: clientNodes) {
            neighbourfromnode= new HashMap<Long,Neighbour>();

            // connect the client via a bidirectional link to a randomly chosen middlebox
            m = random.nextInt(middleNodes.length);
            node = new Neighbour(middleNodes[m].getId(), middleNodes[m].getName());
            neighbourfromnode.put(node.getId(), node);

            // create the other possible links for this client
            int maxNumLinks = (middleNodes.length%4)+1;
            for (int i=0; i<maxNumLinks; i++) {
                int j = random.nextInt(maxNumLinks);
                if (j != m) {
                    node = new Neighbour(middleNodes[j].getId(), middleNodes[j].getName());
                    neighbourfromnode.put(node.getId(), node);
                }
            }
            nodes.get(client.getId()).setNeighbours(neighbourfromnode);

        }

        //create links for each server
        for (Node server: serverNodes) {
            neighbourfromnode= new HashMap<Long,Neighbour>();
            // connect the server via a bidirectional link to a randomly chosen middlebox
            m = random.nextInt(middleNodes.length);
            node = new Neighbour(middleNodes[m].getId(), middleNodes[m].getName());
            neighbourfromnode.put(middleNodes[m].getId(), node);

            // create the other possible links for this server
            int maxNumLinks = (middleNodes.length%4)+1;
            for (int i=0; i<maxNumLinks; i++) {
                int j = random.nextInt(maxNumLinks);
                if (j != m) {
                    node = new Neighbour(middleNodes[j].getId(), middleNodes[j].getName());
                    neighbourfromnode.put(node.getId(), node);
                }
            }
            nodes.get(server.getId()).setNeighbours(neighbourfromnode);
        }

        // create possible links for middleboxes
        for (int i=0; i<middleNodes.length; i++) {
            neighbourfromnode= new HashMap<Long,Neighbour>();
            for (int j=0; j<middleNodes.length/4; j++) {
                if (i != j){
                    node = new Neighbour(middleNodes[j].getId(), middleNodes[j].getName());
                    neighbourfromnode.put(middleNodes[j].getId(), node);
                }
            }
            int h= random.nextInt(clientNodes.length);
            node = new Neighbour(clientNodes[h].getId(),clientNodes[h].getName());
            neighbourfromnode.put(clientNodes[h].getId(), node);
            int k = random.nextInt(serverNodes.length);
            node = new Neighbour(serverNodes[k].getId(),serverNodes[k].getName());
            neighbourfromnode.put(serverNodes[k].getId(), node);
            middleNodes.clone()[i].setNeighbours(neighbourfromnode);
        }

        return ;
    }

    private void createPolicies(Random random) {
        PolicyGen policy;

        // create policies with client as source
        for (int i=0; i<clientNodes.length; i++) {
            // create vector of possible destinations
            Vector<Node> possibleDestinations = new Vector<Node>(Arrays.asList(serverNodes));
            // choose number of destinations
            int m = random.nextInt(serverNodes.length)+1;
            // for each destination to be used
            for (int j=0; j<m; j++) {
                // choose index of destination to be selected
                int serverIndex = random.nextInt(possibleDestinations.size());
                // TODO choose type of policy
                // create reachability policy
                PolicyType type = types[random.nextInt(types.length)];
                policy = new PolicyGen(this, random, clientNodes[i], serverNodes[serverIndex],type);
                // add created policy to Hashmap
                policies.put(policy.getName(), policy);
                // remove selected destination from Vector
                possibleDestinations.remove(serverIndex);
            } // for each destination
        } // for each client
    }

    HashMap<String, PolicyGen> getPolicies() {
        return policies;
    }

    public Node getNode(String name) {
        if (name == null || nodes == null)
            return null;
        else
            return nodes.get(name);
    }
}
