package it.polito.verefoo.rest.spring.integrationTest;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.junit.Assert.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.After;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.http.HttpMessageConverters;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.client.RestTemplate;

import it.polito.verefoo.jaxb.Cache;
import it.polito.verefoo.jaxb.Configuration;
import it.polito.verefoo.jaxb.FunctionalTypes;
import it.polito.verefoo.jaxb.Graph;
import it.polito.verefoo.jaxb.Graphs;
import it.polito.verefoo.jaxb.Neighbour;
import it.polito.verefoo.jaxb.Node;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class GraphIT {

    @Autowired
    private MockMvc mvc;

    Graphs graphs;

    List<Long> graphIds;

    ObjectMapper objectMapper;


    
    @Before
    public void start() throws JsonParseException, JsonMappingException, IOException {
        objectMapper = new ObjectMapper();
        graphIds = new ArrayList<>();

        // convert Graphs.json into a Graphs object
        String folder = "src/" + this.getClass().getPackageName().replace(".", "/") + "/";
        Path path = Paths.get(folder + "Graphs.json");
        graphs = objectMapper.readValue(path.toFile(), Graphs.class);
    }



    @Test
    public void test0AllGraphs() throws Exception {

        // create graphs (> 1)
        List<Long> responseCreate = createGraphs(graphs);
        graphIds.addAll(responseCreate);
        assertEquals(graphs.getGraph().size(), responseCreate.size());

        // get all graphs
        Graphs responseGet = getGraphs();
        assertEquals(graphs.getGraph().size(), responseGet.getGraph().size());
        assertEquals(graphs.getGraph().get(0).getNode().size(), responseGet.getGraph().get(0).getNode().size());

        // delete all graphs
        deleteGraphs();
    }



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

        deleteGraphs();

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




    @After
    public void end() throws Exception {
        // mvc.perform(delete("/adp/DEBUG_removeAllNodes")).andExpect(status().isOk());
    }

}
