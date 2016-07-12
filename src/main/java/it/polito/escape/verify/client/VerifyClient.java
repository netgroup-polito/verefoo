package it.polito.escape.verify.client;

import java.io.File;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import it.polito.escape.verify.model.Graph;
import it.polito.escape.verify.model.Neighbour;
import it.polito.escape.verify.model.Node;

public class VerifyClient {

	private WebTarget	baseTarget;

	private WebTarget	graphsTarget;
	
	private WebTarget	graphTarget;

	private WebTarget	nodesTarget;
	
	private WebTarget	nodeTarget;

	private WebTarget	neighboursTarget;
	
	private WebTarget	neighbourTarget;

	public VerifyClient(String address) {
		Client client = ClientBuilder.newClient();

		this.baseTarget = client.target(address);
		this.graphsTarget = baseTarget.path("graphs");
		this.graphTarget = graphsTarget.path("/{graphId}");
		this.nodesTarget = graphTarget.path("/nodes");
		this.nodeTarget = nodesTarget.path("//{nodeId}");
		this.neighboursTarget = nodeTarget.path("/neighbours");
		this.neighbourTarget = neighboursTarget.path("/{neighbourId}");
	}

	public Response createGraph(Graph graph) {
		return graphsTarget.request().post(Entity.json(graph));
	}

	public Response createGraph(String graph) {
		return graphsTarget.request().post(Entity.entity(graph, "application/json"));
	}

	public Response retrieveGraph(long graphId) {
		return graphTarget.resolveTemplate("graphId", graphId).request().get();
	}

	public Response updateGraph(int graphId, Graph graph) {
		return graphTarget.resolveTemplate("graphId", graphId).request().put(Entity.json(graph));
	}

	public Response deleteGraph(int graphId) {
		return graphTarget.resolveTemplate("graphId", graphId).request().delete();
	}

	public Response createNode(int graphId, Node node) {
		return nodesTarget.resolveTemplate("graphId", graphId).request().post(Entity.json(node));
	}

	public Response retrieveNode(int graphId, int nodeId) {
		return nodeTarget.resolveTemplate("graphId", graphId).resolveTemplate("nodeId", nodeId).request().get();
	}

	public Response updateNode(int graphId, int nodeId, Node node) {
		return nodeTarget	.resolveTemplate("graphId", graphId)
							.resolveTemplate("nodeId", nodeId)
							.request()
							.put(Entity.json(node));
	}

	public Response deleteNode(int graphId, int nodeId) {
		return nodeTarget.resolveTemplate("graphId", graphId).resolveTemplate("nodeId", nodeId).request().delete();
	}

	public Response createNeighbour(int graphId, int nodeId, Neighbour neighbour) {
		return neighboursTarget	.resolveTemplate("graphId", graphId)
								.resolveTemplate("nodeId", nodeId)
								.request()
								.post(Entity.json(neighbour));
	}

	public Response retrieveNeighbour(int graphId, int nodeId, int neighbourId) {
		return neighbourTarget	.resolveTemplate("graphId", graphId)
								.resolveTemplate("nodeId", nodeId)
								.resolveTemplate("neighbourId", neighbourId)
								.request()
								.get();
	}

	public Response updateNeighbour(int graphId, int nodeId, int neighbourId, Neighbour neighbour) {
		return neighbourTarget	.resolveTemplate("graphId", graphId)
								.resolveTemplate("nodeId", nodeId)
								.resolveTemplate("neighbourId", neighbourId)
								.request()
								.put(Entity.json(neighbour));
	}

	public Response deleteNeighbour(int graphId, int nodeId, int neighbourId) {
		return neighbourTarget	.resolveTemplate("graphId", graphId)
								.resolveTemplate("nodeId", nodeId)
								.resolveTemplate("neighbourId", neighbourId)
								.request()
								.delete();
	}
	
	//unused
	public static String deserializeString(File file) throws IOException {
		int len;
		char[] chr = new char[4096];
		final StringBuffer buffer = new StringBuffer();
		final FileReader reader = new FileReader(file);
		try {
			while ((len = reader.read(chr)) > 0) {
				buffer.append(chr, 0, len);
			}
		}
		finally {
			reader.close();
		}
		return buffer.toString();
	}
	
	public List<File> getFiles(){
		List<File> filesList = new ArrayList<File>();
		
		String folderString = System.getProperty("folder");
		File folder;
		if (folderString == null)
			folder = new File(System.getProperty("user.dir") + "/examples");
		else
			folder = new File(folderString);
		
		System.out.println("Folder set to " + folder.getAbsolutePath());

		File[] files = folder.listFiles(new FilenameFilter() {

			@Override
			public boolean accept(File dir, String name) {
				return name.endsWith(".json");
			}
		});

		for (File f : files) {
			filesList.add(f);
		}
		
		return filesList;
	}
	
	public Graph addGraph(File file) throws JsonParseException, JsonMappingException, IOException, Exception{
		System.out.println("Parsing graph of file '" + file.getAbsolutePath() + "'...");
		Graph graph = new ObjectMapper().readValue(file, Graph.class);
		Response createGraphResponse = createGraph(graph);
		if (createGraphResponse.getStatus() != Status.CREATED.getStatusCode()) {
			throw new Exception("Creation of graph contained in file '"	+ file.getAbsolutePath() + "' returned status "
								+ createGraphResponse.getStatus());
		}
		String responseString = createGraphResponse.readEntity(String.class);
		System.out.println("Response:");
		System.out.println(responseString);
		Graph response = new ObjectMapper().readValue(responseString, Graph.class);
		System.out.println("Graph " + response.getId());
		for (Node n : response.getNodes().values()){
			System.out.println("\tNode " + n.getId());
			System.out.println("\tName " + n.getName());
			System.out.println("\tFunctional type: " + n.getFunctional_type());
			for (Neighbour neighbour : n.getNeighbours().values()){
				System.out.println("\t\tNeighbour " + neighbour.getId());
				System.out.println("\t\tName: " + neighbour.getName());
			}
		}
		return response;
	}
	
	public Map<String, Graph> addFiles(List<File> files) throws JsonParseException, JsonMappingException, IOException, Exception{
		Map<String, Graph> graphs = new HashMap<String, Graph>();
		
		for (File f : files){
			Graph graph = addGraph(f);
			graphs.put(f.getName(), graph);
		}
		
		for (Map.Entry<String, Graph> graph: graphs.entrySet()){
			System.out.println(graph.getKey() + " -> graph " + graph.getValue().getId());
		}
		System.out.println("Graphs added");
		
		return graphs;
	}

	public static void main(String[] args) throws IOException, Exception {
		System.out.println("Adding graphs");
		VerifyClient verifyClient = new VerifyClient("http://localhost:8080/verify/api");
//		verifyClient.addExampleGraphs();

		List<File> files = verifyClient.getFiles();
		Map<String, Graph> graphs = verifyClient.addFiles(files);
		
		for (Graph g : graphs.values()){
			Response response = verifyClient.retrieveGraph(g.getId());
			String responseString = response.readEntity(String.class);
			System.out.println("Response");
			System.out.println(responseString);
			Graph graph = new ObjectMapper().readValue(responseString, Graph.class);
			System.out.println("Read graph " + graph.getId());
			System.out.println(response.getStatus());
		}
		
	}

}
