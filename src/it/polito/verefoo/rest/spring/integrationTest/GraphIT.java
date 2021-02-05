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
import it.polito.verefoo.jaxb.Graph;
import it.polito.verefoo.jaxb.Graphs;

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
    public void test0CreateGraphs() throws Exception {

        String body = objectMapper.writeValueAsString(graphs);

        String rawResponse = mvc.perform(post("/adp/graphs").contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON).content(body)).andExpect(status().isCreated()).andReturn()
                .getResponse().getContentAsString();

        JsonNode jsonResponse = objectMapper.readTree(rawResponse);
        String rawContent = jsonResponse.get("_embedded").get("longList").toString();

        List<Long> response = Arrays.asList(objectMapper.readValue(rawContent, Long[].class));
        graphIds.addAll(response);
        assertEquals(2, response.size());

    }

    @Test
    public void test1GetGraphs() throws Exception {
        String rawResponse = mvc.perform(get("/adp/graphs").contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()).andReturn()
                .getResponse().getContentAsString();

        JsonNode jsonResponse = objectMapper.readTree(rawResponse);
        String rawContent = jsonResponse.get("_embedded").get("graphsList").elements().next().toString();
        Graphs response = objectMapper.readValue(rawContent, Graphs.class);

        assertEquals(graphs.getGraph().size(), response.getGraph().size());
        assertEquals(graphs.getGraph().get(0).getNode().size(), response.getGraph().get(0).getNode().size());
    }

    @Test
    public void test2UpdateGraph() throws UnsupportedEncodingException, Exception {
        Graph graph = graphs.getGraph().get(0);

        // modify arbitrarily the graph
        graph.setServiceGraph(true);

        Configuration configuration = new Configuration();
        configuration.setName("test configuration");
        Cache cache = new Cache();
        cache.getResource().add("first test resource");
        cache.getResource().add("second test resource");
        configuration.setCache(cache);
        graph.getNode().get(0).setConfiguration(configuration);

        assertEquals(2, graphIds.size());

        String body = objectMapper.writeValueAsString(graph);

        mvc.perform(put("/adp/graphs/" + graphIds.get(0)).contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isOk());
    }

    @Test
    public void test3DeleteGraphs() throws Exception {
        mvc.perform(delete("/adp/graphs").contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }


    @After
    public void end() throws Exception {
        // mvc.perform(delete("/adp/DEBUG_removeAllNodes")).andExpect(status().isOk());
    }
}
