package it.polito.verefoo.rest.spring.integrationTest;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.After;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import it.polito.verefoo.jaxb.Cache;
import it.polito.verefoo.jaxb.Configuration;
import it.polito.verefoo.jaxb.Constraints;
import it.polito.verefoo.jaxb.FunctionalTypes;
import it.polito.verefoo.jaxb.Graph;
import it.polito.verefoo.jaxb.Graphs;
import it.polito.verefoo.jaxb.Mailserver;
import it.polito.verefoo.jaxb.Nat;
import it.polito.verefoo.jaxb.Neighbour;
import it.polito.verefoo.jaxb.Node;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class GraphIT {

    @Autowired
    private MockMvc mvc;

    Graphs graphs;

    ObjectMapper objectMapper;

    Constraints constraints;

    

    @Before
    public void start() throws Exception {
        mvc.perform(delete("/adp/DEBUG_removeAllNodes")).andExpect(status().isOk());

        objectMapper = new ObjectMapper();

        // convert Graphs.json into a Graphs object
        String folder = "src/" + this.getClass().getPackage().getName().replace(".", "/") + "/";
        Path path1 = Paths.get(folder + "Graphs.json");
        graphs = objectMapper.readValue(path1.toFile(), Graphs.class);
        Path path2 = Paths.get(folder + "Constraints.json");
        constraints = objectMapper.readValue(path2.toFile(), Constraints.class);
    }


    /**
     * Test controllers which manage the {@code Graphs} collection, that is all the graphs in the workspace
     * @throws Exception
     */
    @Test
    public void test0AllGraphs() throws Exception {

        // create graphs (> 1)
        List<Long> responseCreate = createGraphs(graphs);
        assertEquals(graphs.getGraph().size(), responseCreate.size());

        // get all graphs
        Graphs responseGet = getGraphs();
        assertEquals(graphs.getGraph().size(), responseGet.getGraph().size());
        assertEquals(graphs.getGraph().get(0).getNode().size(), responseGet.getGraph().get(0).getNode().size());

        // delete all graphs
        deleteGraphs();
    }


    /**
     * Test controllers which manage a single {@code Graph} resource at a time
     */
    @Test
    public void test1OneGraph() throws Exception {

        // create a graph
        graphs.getGraph().remove(1);
        List<Long> graphIds = createGraphs(graphs);
        assertEquals(1, graphIds.size());
        Long graphId = graphIds.get(0);

        // get the graph
        Graph graph = getGraph(graphId);
        assertEquals(graphs.getGraph().get(0).isServiceGraph(), graph.isServiceGraph());
        assertEquals(graphs.getGraph().get(0).getNode().size(), graph.getNode().size());

        // update the graph
        graph.setServiceGraph(!graph.isServiceGraph());
        Node node = new Node();
        node.setFunctionalType(FunctionalTypes.CACHE);
        node.setName("40.0.0.2");
        Configuration configuration = new Configuration();
        configuration.setName("test configuration");
        Cache cache = new Cache();
        cache.getResource().add("first test resource");
        cache.getResource().add("second test resource");
        configuration.setCache(cache);
        node.setConfiguration(configuration);
        graph.getNode().add(node);

        updateGraph(graphId, graph);
        Graph updatedGraph = getGraph(graphId);
        assertEquals(graph.isServiceGraph(), updatedGraph.isServiceGraph());
        assertEquals(graph.getNode().size(), updatedGraph.getNode().size());

        deleteGraph(graphId);

    }


    /**
     * Test controllers which manage the {@code Neighbour} resources
     * @throws Exception
     */
    @Test
    public void test2Neighbours() throws Exception {
        // create a graph
        graphs.getGraph().remove(1);
        List<Long> graphIds = createGraphs(graphs);
        Long graphId = graphIds.get(0);
        // get the graph
        Graph graph = getGraph(graphId);

        // create a neighbour
        Neighbour neighbour = new Neighbour();
        // the name must be an existing node if foreign keys are enforced
        neighbour.setName("test neighbour");
        Long nodeId = graph.getNode().get(0).getId();
        Long neighbourId = createNeighbour(graphId, nodeId, neighbour);

        // get the neighbour
        Neighbour createdNeighbour = getGraph(graphId)
            .getNode().stream().filter(n -> n.getId().equals(nodeId)).findFirst().get()
            .getNeighbour().stream().filter(n -> n.getId().equals(neighbourId)).findFirst().get();
        assertEquals(neighbour.getName(), createdNeighbour.getName());

        // delete the neighbour
        deleteNeighbour(graphId, nodeId, neighbourId);
        Graph deletedNeighbourGraph = getGraph(graphId);
        assertTrue(deletedNeighbourGraph
            .getNode().stream().filter(n -> n.getId().equals(nodeId)).findFirst().get()
            .getNeighbour().stream().noneMatch(n -> n.getId().equals(neighbourId)));


        deleteGraphs();

    }


    /**
     * Test controllers which manage the {@code Node} resource
     * @throws Exception
     */
    @Test
    public void test3OneNode() throws Exception {
        // create a graph
        graphs.getGraph().remove(1);
        List<Long> graphIds = createGraphs(graphs);
        Long graphId = graphIds.get(0);
        // get the graph
        Graph graph = getGraph(graphId);

        // create a node
        Node node = new Node();
        node.setName("20.0.0.1");
        node.setFunctionalType(FunctionalTypes.MAILSERVER);
        Configuration configuration = new Configuration();
        Mailserver mailserver = new Mailserver();
        mailserver.setName("test mail server");
        configuration.setMailserver(mailserver);
        node.setConfiguration(configuration);

        Long nodeId = createNode(graphId, node);

        graph = getGraph(graphId);

        assertTrue(graph.getNode().stream().anyMatch(n -> n.getId().equals(nodeId)));
        Node createdNode = graph.getNode().stream().filter(n -> n.getId().equals(nodeId)).findFirst().get();
        assertEquals(createdNode.getName(), node.getName());

        // update the node
        node.setName("10.0.0.5");

        updateNode(graphId, nodeId, node);

        // get the node
        Node updatedNode = getNode(graphId, nodeId);

        assertEquals(node.getName(), updatedNode.getName());

        // update configuration
        updatedNode.setFunctionalType(FunctionalTypes.NAT);
        configuration = new Configuration();
        Nat nat = new Nat();
        nat.getSource().add("test source 1");
        nat.getSource().add("test source 2");
        configuration.setNat(nat);
        configuration.setId(updatedNode.getConfiguration().getId());
        updatedNode.setConfiguration(configuration);
        updateConfiguration(graphId, nodeId, updatedNode.getConfiguration().getId(), configuration);

        // get configuration
        Configuration updatedConfiguration = getConfiguration(graphId, nodeId);
        assertEquals(configuration.getName(), updatedConfiguration.getName());
        assertEquals(configuration.getNat().getSource().size(), updatedConfiguration.getNat().getSource().size());

        // delete node
        deleteNode(graphId, nodeId);

        deleteGraphs();

    }

    /**
     * Test controllers which manage the {@code Constraints} resource
     * @throws Exception
     */
    @Test
    public void test4Constraints() throws Exception {
        // create a graph
        graphs.getGraph().remove(1);
        List<Long> graphIds = createGraphs(graphs);
        Long graphId = graphIds.get(0);

        // create constraints
        createConstraints(graphId, constraints);

        // get constraints
        Constraints createdConstraints = getConstraints(graphId);
        assertEquals(constraints.getAllocationConstraints().getAllocationConstraint().size(), createdConstraints.getAllocationConstraints().getAllocationConstraint().size());
        assertEquals(constraints.getLinkConstraints().getLinkMetrics().size(), createdConstraints.getLinkConstraints().getLinkMetrics().size());
        assertEquals(constraints.getNodeConstraints().getNodeMetrics().size(), createdConstraints.getNodeConstraints().getNodeMetrics().size());

        // update constraints
        constraints.getAllocationConstraints().getAllocationConstraint().remove(1);
        constraints.getLinkConstraints().getLinkMetrics().get(0).setReqLatency(10000);
        constraints.getNodeConstraints().getNodeMetrics().get(1).setCores(13);
        updateConstraints(graphId, constraints);

        // get constraints
        Constraints updatedConstraints = getConstraints(graphId);
        assertEquals(constraints.getAllocationConstraints().getAllocationConstraint().size(), updatedConstraints.getAllocationConstraints().getAllocationConstraint().size());
        assertTrue(updatedConstraints.getLinkConstraints().getLinkMetrics().stream().anyMatch(linkMetric -> linkMetric.getReqLatency() == constraints.getLinkConstraints().getLinkMetrics().get(0).getReqLatency()));
        assertTrue(updatedConstraints.getNodeConstraints().getNodeMetrics().stream().anyMatch(nodeMetric -> nodeMetric.getCores() == constraints.getNodeConstraints().getNodeMetrics().get(1).getCores()));

        // delete constraints
        deleteConstraints(graphId);

        deleteGraphs();
    }


    @After
    public void end() throws Exception {
        mvc.perform(delete("/adp/DEBUG_removeAllNodes")).andExpect(status().isOk());
    }




    private List<Long> createGraphs(Graphs graphs) throws Exception {
        String body = objectMapper.writeValueAsString(graphs);

        String rawResponse = mvc.perform(post("/adp/graphs").contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON).content(body)).andExpect(status().isCreated()).andReturn()
                .getResponse().getContentAsString();

        JsonNode jsonResponse = objectMapper.readTree(rawResponse);
        String rawContent = jsonResponse.get("_embedded").get("longList").toString();

        return Arrays.asList(objectMapper.readValue(rawContent, Long[].class));
    }


    private Graphs getGraphs() throws Exception {
        String rawResponse = mvc
                .perform(get("/adp/graphs").contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()).andReturn().getResponse().getContentAsString();

        JsonNode jsonResponse = objectMapper.readTree(rawResponse);
        String rawContent = jsonResponse.get("_embedded").get("graphsList").elements().next().toString();
        return objectMapper.readValue(rawContent, Graphs.class);
    }


    private void deleteGraphs() throws Exception {
        mvc.perform(delete("/adp/graphs").contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk());
    }


    private Graph getGraph(Long id) throws Exception {
        String rawResponse = mvc
                .perform(get("/adp/graphs/" + id).contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()).andReturn().getResponse().getContentAsString();

        JsonNode jsonResponse = objectMapper.readTree(rawResponse);
        String rawContent = jsonResponse.get("_embedded").get("graphList").elements().next().toString();
        return objectMapper.readValue(rawContent, Graph.class);
    }


    private void updateGraph(Long id, Graph graph) throws Exception {
        String body = objectMapper.writeValueAsString(graph);
        mvc.perform(put("/adp/graphs/" + id).contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON).content(body)).andExpect(status().isOk());
    }


    private void deleteGraph(Long graphId) throws Exception {
        mvc.perform(delete("/adp/graphs/" + graphId).contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());
    }


    private Long createNeighbour(Long graphId, Long nodeId, Neighbour neighbour) throws Exception {
        String body = objectMapper.writeValueAsString(neighbour);

        String rawResponse = mvc.perform(post("/adp/graphs/" + graphId + "/nodes/" + nodeId + "/neighbours").contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON).content(body)).andExpect(status().isCreated()).andReturn()
                .getResponse().getContentAsString();

        JsonNode jsonResponse = objectMapper.readTree(rawResponse);
        String rawContent = jsonResponse.get("_embedded").get("longList").elements().next().toString();

        return objectMapper.readValue(rawContent, Long.class);
    }

    private void deleteNeighbour(Long graphId, Long nodeId, Long neighbourId) throws Exception {
        mvc.perform(delete("/adp/graphs/" + graphId + "/nodes/" + nodeId + "/neighbours/" + neighbourId).contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());
    }

    private Long createNode(Long graphId, Node node) throws Exception {
        String body = objectMapper.writeValueAsString(node);

        String rawResponse = mvc.perform(post("/adp/graphs/" + graphId + "/nodes").contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON).content(body)).andExpect(status().isCreated()).andReturn()
                .getResponse().getContentAsString();

        JsonNode jsonResponse = objectMapper.readTree(rawResponse);
        String rawContent = jsonResponse.get("_embedded").get("longList").elements().next().toString();

        return objectMapper.readValue(rawContent, Long.class);
    }


    private void updateNode(Long graphId, Long nodeId, Node node) throws Exception {
        String body = objectMapper.writeValueAsString(node);
        mvc.perform(put("/adp/graphs/" + graphId + "/nodes/" + nodeId).contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON).content(body)).andExpect(status().isOk());
    }

    private Node getNode(Long graphId, Long nodeId) throws Exception {
        String rawResponse = mvc
                .perform(get("/adp/graphs/" + graphId + "/nodes/" + nodeId).contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()).andReturn().getResponse().getContentAsString();

        JsonNode jsonResponse = objectMapper.readTree(rawResponse);
        String rawContent = jsonResponse.get("_embedded").get("nodeList").elements().next().toString();
        return objectMapper.readValue(rawContent, Node.class);
    }

    private void deleteNode(Long graphId, Long nodeId) throws Exception {
        mvc.perform(delete("/adp/graphs/" + graphId + "/nodes/" + nodeId).contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());
    }

    private void updateConfiguration(Long graphId, Long nodeId, Long configurationId, Configuration configuration) throws Exception {
        String body = objectMapper.writeValueAsString(configuration);
        mvc.perform(put("/adp/graphs/" + graphId + "/nodes/" + nodeId + "/configuration/" + configurationId).contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON).content(body)).andExpect(status().isOk());
    }

    private Configuration getConfiguration(Long graphId, Long nodeId) throws Exception {
        String rawResponse = mvc
                .perform(get("/adp/graphs/" + graphId + "/nodes/" + nodeId + "/configuration").contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()).andReturn().getResponse().getContentAsString();

        JsonNode jsonResponse = objectMapper.readTree(rawResponse);
        String rawContent = jsonResponse.get("_embedded").get("configurationList").elements().next().toString();
        return objectMapper.readValue(rawContent, Configuration.class);
    }

    private void createConstraints(Long graphId, Constraints constraints) throws Exception {
        String body = objectMapper.writeValueAsString(constraints);

        mvc.perform(post("/adp/graphs/" + graphId + "/constraints").contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON).content(body)).andExpect(status().isCreated());
    }

    private Constraints getConstraints(Long graphId) throws Exception {
        String rawResponse = mvc
                .perform(get("/adp/graphs/" + graphId + "/constraints").contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()).andReturn().getResponse().getContentAsString();

        JsonNode jsonResponse = objectMapper.readTree(rawResponse);
        String rawContent = jsonResponse.get("_embedded").get("constraintsList").elements().next().toString();
        return objectMapper.readValue(rawContent, Constraints.class);
    }

    private void updateConstraints(Long graphId, Constraints constraints) throws Exception {
        String body = objectMapper.writeValueAsString(constraints);
        mvc.perform(put("/adp/graphs/" + graphId + "/constraints").contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON).content(body)).andExpect(status().isOk());
    }

    private void deleteConstraints(Long graphId) throws Exception {
        mvc.perform(delete("/adp/graphs/" + graphId + "/constraints").contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());
    }

}
