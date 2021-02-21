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

import it.polito.verefoo.jaxb.Connection;
import it.polito.verefoo.jaxb.Connections;
import it.polito.verefoo.jaxb.FunctionalTypes;
import it.polito.verefoo.jaxb.Graphs;
import it.polito.verefoo.jaxb.Host;
import it.polito.verefoo.jaxb.Hosts;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class SubstrateIT {

    @Autowired
    private MockMvc mvc;

    ObjectMapper objectMapper;


    Graphs graphs;

    Hosts hosts;

    Connections connections;




    @Before
    public void start() throws Exception {
        mvc.perform(delete("/adp/DEBUG_removeAllNodes")).andExpect(status().isOk());

        objectMapper = new ObjectMapper();

        // convert Hosts.json and Connections.json into a Hosts and a Connections objects
        String folder = "src/" + this.getClass().getPackage().getName().replace(".", "/") + "/";
        Path path1 = Paths.get(folder + "Hosts.json");
        hosts = objectMapper.readValue(path1.toFile(), Hosts.class);
        Path path2 = Paths.get(folder + "Connections.json");
        connections = objectMapper.readValue(path2.toFile(), Connections.class);
        Path path3 = Paths.get(folder + "Graphs.json");
        graphs = objectMapper.readValue(path3.toFile(), Graphs.class);

        // create graphs (> 1)
        createGraphs(graphs);
    }


    /**
     * Test controllers which manage all the {@code Substrate} resources at a time
     * @throws Exception
     */
    @Test
    public void test0AllSubstrates() throws Exception {
        // create substrates (> 1)
        Long substrateId1 = createSubstrate();
        Long substrateId2 = createSubstrate();

        // get all substrates
        List<Long> createdSubstrateIds = getSubstrates();
        assertTrue(createdSubstrateIds.contains(substrateId1));
        assertTrue(createdSubstrateIds.contains(substrateId2));

        // delete all substrates
        deleteSubstrates();

    }



    /**
     * Test controllers which manage a single {@code Substrate} resource at a time
     */
    @Test
    public void test1OneSubstrate() throws Exception {
        // create substrates (1)
        Long substrateId = createSubstrate();

        // create hosts
        createHosts(substrateId, hosts);

        // get hosts
        Hosts createdHosts = getHosts(substrateId);
        assertEquals(hosts.getHost().size(), createdHosts.getHost().size());
        assertTrue(createdHosts.getHost().stream().anyMatch(host -> host.getFixedEndpoint().equals(hosts.getHost().get(0).getFixedEndpoint()) && host.getCores() == hosts.getHost().get(0).getCores()));
    
        // delete hosts
        deleteHosts(substrateId);

        // delete substrate
        deleteSubstrate(substrateId);
    }



    /**
     * Test controllers which manage a single {@code Host} resource at a time
     */
    @Test
    public void test2OneHost() throws Exception {
        // create substrates (1)
        Long substrateId = createSubstrate();

        // create hosts
        createHosts(substrateId, hosts);

        // update host
        Host host = hosts.getHost().get(0);
        host.setCpu(host.getCpu() + 5);
        host.setActive(!host.isActive());
        host.getNodeRef().remove(1);
        host.getSupportedVNF().get(0).setFunctionalType(FunctionalTypes.WEB_APPLICATION_FIREWALL);
        updateHost(substrateId, host.getName(), host);

        // get host
        Host updatedHost = getHost(substrateId, host.getName());
        assertEquals(host.getCpu(), updatedHost.getCpu());
        assertEquals(host.isActive(), updatedHost.isActive());
        assertTrue(updatedHost.getNodeRef().stream().anyMatch(nodeRef -> host.getNodeRef().get(0).getNode().equals(nodeRef.getNode())));
    
        // delete host
        deleteHost(substrateId, host.getName());

        // get hosts
        Hosts deletedHosts = getHosts(substrateId);
        hosts.getHost().remove(1);
        assertEquals(hosts.getHost().size(), deletedHosts.getHost().size());

        deleteSubstrate(substrateId);
    }



    /**
     * Test controllers which manage all the {@code Connection} resources of a substrate
     * @throws Exception
     */
    @Test
    public void test3Connections() throws Exception {
        // create substrates (1)
        Long substrateId = createSubstrate();

        // create hosts
        createHosts(substrateId, hosts);

        // create connections (1)
        createConnections(substrateId, connections);

        // get connections
        Connections createdConnections = getConnections(substrateId);
        assertEquals(connections.getConnection().size(), createdConnections.getConnection().size());
        assertTrue(createdConnections.getConnection().stream().anyMatch(connection -> connections.getConnection().get(0).getSourceHost().equals(connection.getSourceHost())));

        // update connections
        connections.getConnection().get(0).setAvgLatency(1564);
        Connection connection = new Connection();
        connection.setSourceHost(connections.getConnection().get(0).getDestHost());
        connection.setDestHost(connections.getConnection().get(0).getSourceHost());
        connections.getConnection().add(connection);
        updateConnections(substrateId, connections);
        Connections updatedConnections = getConnections(substrateId);
        assertEquals(connections.getConnection().size(), updatedConnections.getConnection().size());
        assertTrue(updatedConnections.getConnection().stream().anyMatch(updatedConnection -> connections.getConnection().get(0).getAvgLatency().equals(updatedConnection.getAvgLatency())));

        // delete connections
        deleteConnections(substrateId);

        deleteSubstrate(substrateId);
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

    private Long createSubstrate() throws Exception {

        String rawResponse = mvc.perform(post("/adp/substrates").contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)).andExpect(status().isCreated()).andReturn()
                .getResponse().getContentAsString();

        JsonNode jsonResponse = objectMapper.readTree(rawResponse);
        String rawContent = jsonResponse.get("_embedded").get("longList").elements().next().toString();

        return objectMapper.readValue(rawContent, Long.class);
    }

    private List<Long> getSubstrates() throws Exception {
        String rawResponse = mvc
                .perform(get("/adp/substrates").contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()).andReturn().getResponse().getContentAsString();

        JsonNode jsonResponse = objectMapper.readTree(rawResponse);
        String rawContent = jsonResponse.get("_embedded").get("longList").toString();
        return Arrays.asList(objectMapper.readValue(rawContent, Long[].class));
    }

    private void deleteSubstrates() throws Exception {
        mvc.perform(delete("/adp/substrates").contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk());
    }

    private void createHosts(Long substrateId, Hosts hosts) throws Exception {
        String body = objectMapper.writeValueAsString(hosts);

        mvc.perform(post("/adp/substrates/" + substrateId + "/hosts").contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON).content(body)).andExpect(status().isCreated());
    }

    private Hosts getHosts(Long substrateId) throws Exception {
        String rawResponse = mvc
                .perform(get("/adp/substrates/" + substrateId + "/hosts").contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()).andReturn().getResponse().getContentAsString();

        JsonNode jsonResponse = objectMapper.readTree(rawResponse);
        String rawContent = jsonResponse.get("_embedded").get("hostsList").elements().next().toString();
        return objectMapper.readValue(rawContent, Hosts.class);
    }

    private void deleteHosts(Long substrateId) throws Exception {
        mvc.perform(delete("/adp/substrates/" + substrateId + "/hosts").contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());
    }

    private void deleteSubstrate(Long substrateId) throws Exception {
        mvc.perform(delete("/adp/substrates/" + substrateId).contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());
    }

    private void updateHost(Long substrateId, String hostName, Host host) throws Exception {
        String body = objectMapper.writeValueAsString(host);
        mvc.perform(put("/adp/substrates/" + substrateId + "/hosts/" + hostName).contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON).content(body)).andExpect(status().isOk());
    }

    private Host getHost(Long substrateId, String hostName) throws Exception {
        String rawResponse = mvc
                .perform(get("/adp/substrates/" + substrateId + "/hosts/" + hostName).contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()).andReturn().getResponse().getContentAsString();

        JsonNode jsonResponse = objectMapper.readTree(rawResponse);
        String rawContent = jsonResponse.get("_embedded").get("hostList").elements().next().toString();
        return objectMapper.readValue(rawContent, Host.class);
    }

    private void deleteHost(Long substrateId, String hostName) throws Exception {
        mvc.perform(delete("/adp/substrates/" + substrateId + "/hosts/" + hostName).contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());
    }

    private void createConnections(Long substrateId, Connections connections) throws Exception {
        String body = objectMapper.writeValueAsString(connections);

        mvc.perform(post("/adp/substrates/" + substrateId + "/connections").contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON).content(body)).andExpect(status().isCreated());
    }

    private Connections getConnections(Long substrateId) throws Exception {
        String rawResponse = mvc
                .perform(get("/adp/substrates/" + substrateId + "/connections").contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()).andReturn().getResponse().getContentAsString();

        JsonNode jsonResponse = objectMapper.readTree(rawResponse);
        String rawContent = jsonResponse.get("_embedded").get("connectionsList").elements().next().toString();
        return objectMapper.readValue(rawContent, Connections.class);
    }

    private void updateConnections(Long substrateId, Connections connections) throws Exception {
        String body = objectMapper.writeValueAsString(connections);
        mvc.perform(put("/adp/substrates/" + substrateId + "/connections/").contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON).content(body)).andExpect(status().isOk());
    }

    private void deleteConnections(Long substrateId) throws Exception {
        mvc.perform(delete("/adp/substrates/" + substrateId + "/connections").contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());
    }
    
}
