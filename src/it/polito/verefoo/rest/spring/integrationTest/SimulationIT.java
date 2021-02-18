package it.polito.verefoo.rest.spring.integrationTest;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;

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
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import it.polito.verefoo.jaxb.Graphs;
import it.polito.verefoo.jaxb.NFV;
import it.polito.verefoo.jaxb.PropertyDefinition;

/**
 * This class is not intended to test the Verefoo core, but just the APIs and the correct storage in the db
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class SimulationIT {

    @Autowired
    private MockMvc mvc;

    ObjectMapper objectMapper;



    NFV nfv;

    NFV nfv2;

    NFV nfv3;




    @Before
    public void start() throws Exception {
        mvc.perform(delete("/adp/DEBUG_removeAllNodes")).andExpect(status().isOk());

        objectMapper = new ObjectMapper();
        JAXBContext jc = JAXBContext.newInstance( "it.polito.verefoo.jaxb" );
        // create an Unmarshaller
        Unmarshaller u = jc.createUnmarshaller();

        String folder = "src/" + this.getClass().getPackage().getName().replace(".", "/") + "/";
        Path path1 = Paths.get(folder + "NFV.json");
        nfv = objectMapper.readValue(path1.toFile(), NFV.class);
        Path path2 = Paths.get(folder + "FWCorrect02.xml");
        nfv2 = (NFV) u.unmarshal(path2.toFile());
        Path path3 = Paths.get(folder + "FWCorrect07.xml");
        nfv3 = (NFV) u.unmarshal(path3.toFile());
    }


    /**
     * Test the controller to run a simulation by passing the whole {@code NFV} resource
     * @throws Exception
     */
    @Test
    public void test0ByNFV() throws Exception {

        // perform simulation
        Long simulationId = performSimulationByNFV(nfv);

        // get result
        NFV resultNFV = getSimulationResult(simulationId);
        assertEquals(nfv.getGraphs().getGraph().size(), resultNFV.getGraphs().getGraph().size());
        assertEquals(nfv.getPropertyDefinition().getProperty().size(), resultNFV.getPropertyDefinition().getProperty().size());
    
        // clean up
        end();
    }

    /**
     * Test the controller to run a simulation by passing previously-defined resources as parameters
     * @throws Exception
     */
    @Test
    public void test1ByParams() throws Exception {

        // create a graph and a requirements set
        List<Long> graphIds = createGraphs(nfv.getGraphs());
        Long graphId = graphIds.get(0);
        nfv.getPropertyDefinition().getProperty().forEach(property -> property.setGraph(graphId));
        Long requirementsSetId = createRequirementsSet(nfv.getPropertyDefinition());

        // perform simulation
        Long simulationId = performSimulationByParams(graphId, requirementsSetId, null);
        
        // get result
        NFV resultNFV = getSimulationResult(simulationId);
        assertEquals(1, resultNFV.getGraphs().getGraph().size());
        assertEquals(nfv.getPropertyDefinition().getProperty().size(), resultNFV.getPropertyDefinition().getProperty().size());
    }

    /**
     * Test the controller to run a simulation by passing the whole {@code NFV} resource
     * @throws Exception
     */
    @Test
    public void test2ByNFV() throws Exception {

        // perform simulation
        Long simulationId = performSimulationByNFV(nfv2);

        // get result
        NFV resultNFV2 = getSimulationResult(simulationId);
        assertEquals(nfv2.getGraphs().getGraph().size(), resultNFV2.getGraphs().getGraph().size());
        assertEquals(nfv2.getPropertyDefinition().getProperty().size(), resultNFV2.getPropertyDefinition().getProperty().size());
    
        // clean up
        end();
    }

    /**
     * Test the controller to run a simulation by passing previously-defined resources as parameters
     * @throws Exception
     */
    @Test
    public void test3ByParams() throws Exception {

        // create a graph and a requirements set
        assertNotNull(nfv3.getGraphs());
        List<Long> graphIds = createGraphs(nfv3.getGraphs());
        Long graphId = graphIds.get(0);
        nfv3.getPropertyDefinition().getProperty().forEach(property -> property.setGraph(graphId));
        Long requirementsSetId = createRequirementsSet(nfv3.getPropertyDefinition());

        // perform simulation
        Long simulationId = performSimulationByParams(graphId, requirementsSetId, null);
        
        // get result
        NFV resultNFV3 = getSimulationResult(simulationId);
        assertEquals(nfv3.getGraphs().getGraph().size(), resultNFV3.getGraphs().getGraph().size());
        assertEquals(nfv3.getPropertyDefinition().getProperty().size(), resultNFV3.getPropertyDefinition().getProperty().size());
    
        // clean up
        end();
    }




    @After
    public void end() throws Exception {
        mvc.perform(delete("/adp/DEBUG_removeAllNodes")).andExpect(status().isOk());
    }



    private Long performSimulationByNFV(NFV nfv) throws Exception {
        String body = objectMapper.writeValueAsString(nfv);

        String rawResponse = mvc.perform(post("/adp/simulations").contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON).content(body)).andExpect(status().isOk()).andReturn()
                .getResponse().getContentAsString();

        JsonNode jsonResponse = objectMapper.readTree(rawResponse);
        String rawContent = jsonResponse.get("_embedded").get("longList").elements().next().toString();

        return objectMapper.readValue(rawContent, Long.class);
    }

    private NFV getSimulationResult(Long simulationId) throws Exception {
        String rawResponse = mvc
                .perform(get("/adp/simulations/" + simulationId).contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()).andReturn().getResponse().getContentAsString();

        JsonNode jsonResponse = objectMapper.readTree(rawResponse);
        String rawContent = jsonResponse.get("_embedded").get("nFVList").elements().next().toString();
        return objectMapper.readValue(rawContent, NFV.class);
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

    private Long createRequirementsSet(PropertyDefinition propertyDefinition) throws Exception {
        String body = objectMapper.writeValueAsString(propertyDefinition);

        String rawResponse = mvc
                .perform(post("/adp/requirements").contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isCreated()).andReturn().getResponse().getContentAsString();

        JsonNode jsonResponse = objectMapper.readTree(rawResponse);
        String rawContent = jsonResponse.get("_embedded").get("longList").elements().next().toString();

        return objectMapper.readValue(rawContent, Long.class);
    }

    private Long performSimulationByParams(Long gid, Long rid, Long sid) throws Exception {
        MockHttpServletRequestBuilder request = post("/adp/simulations/byParams").contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON);
        
        request = request.param("gid", Long.toString(gid));
        if (rid != null) {
            request = request.param("rid", Long.toString(rid));
        }
        if (sid != null) {
            request = request.param("sid", Long.toString(sid));
        }

        String rawResponse = mvc.perform(request)
                .andExpect(status().isOk()).andReturn()
                .getResponse().getContentAsString();

        JsonNode jsonResponse = objectMapper.readTree(rawResponse);
        String rawContent = jsonResponse.get("_embedded").get("longList").elements().next().toString();

        return objectMapper.readValue(rawContent, Long.class);
    }

}