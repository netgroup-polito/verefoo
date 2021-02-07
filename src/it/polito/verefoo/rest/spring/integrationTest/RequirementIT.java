package it.polito.verefoo.rest.spring.integrationTest;

import static org.junit.Assert.assertEquals;
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

import it.polito.verefoo.jaxb.Graphs;
import it.polito.verefoo.jaxb.HTTPDefinition;
import it.polito.verefoo.jaxb.L4ProtocolTypes;
import it.polito.verefoo.jaxb.PName;
import it.polito.verefoo.jaxb.POP3Definition;
import it.polito.verefoo.jaxb.Property;
import it.polito.verefoo.jaxb.PropertyDefinition;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class RequirementIT {

    @Autowired
    private MockMvc mvc;

    ObjectMapper objectMapper;

    Graphs graphs;

    Long graphId;

    PropertyDefinition requirementsSet;

    Property property;

    @Before
    public void start() throws Exception {
        mvc.perform(delete("/adp/DEBUG_removeAllNodes")).andExpect(status().isOk());

        objectMapper = new ObjectMapper();

        // convert Graphs.json into a Graphs object
        String folder = "src/" + this.getClass().getPackageName().replace(".", "/") + "/";
        Path path1 = Paths.get(folder + "Graphs.json");
        graphs = objectMapper.readValue(path1.toFile(), Graphs.class);
        Path path2 = Paths.get(folder + "PropertyDefinition.json");
        requirementsSet = objectMapper.readValue(path2.toFile(), PropertyDefinition.class);
        Path path3 = Paths.get(folder + "Property.json");
        property = objectMapper.readValue(path3.toFile(), Property.class);

        // create a graph
        graphs.getGraph().remove(1);
        List<Long> graphIds = createGraphs(graphs);
        graphId = graphIds.get(0);

        requirementsSet.getProperty().forEach(property -> property.setGraph(graphId));
    }

    @Test
    public void test0AllRequirementsSets() throws Exception {

        // create requirements set
        Long requirementId = createRequirementsSet(requirementsSet);

        // get requirements sets
        List<PropertyDefinition> createdRequirementsSets = getRequirementsSets();
        PropertyDefinition createdRequirementSet = createdRequirementsSets.get(0);
        assertEquals(requirementsSet.getProperty().size(), createdRequirementSet.getProperty().size());

        // delete requirements sets
        deleteRequirementsSets();
    }

    @Test
    public void test1OneRequirementsSet() throws Exception {
        // create requirements set
        Long requirementsSetId = createRequirementsSet(requirementsSet);

        // get requirements set
        PropertyDefinition createdRequirementSet = getRequirementsSet(requirementsSetId);
        assertEquals(requirementsSet.getProperty().size(), createdRequirementSet.getProperty().size());

        // update requirements set
        requirementsSet.getProperty().add(property);
        updateRequirementsSet(requirementsSetId, requirementsSet);

        // get the requirements set
        PropertyDefinition updatedRequirementSet = getRequirementsSet(requirementsSetId);
        assertEquals(requirementsSet.getProperty().size(), updatedRequirementSet.getProperty().size());

        // delete requirements set
        deleteRequirementsSet(requirementsSetId);

        // delete requirements sets
        deleteRequirementsSets();
    }

    @Test
    public void test2OneProperty() throws Exception {
        // create requirements set
        Long requirementsSetId = createRequirementsSet(requirementsSet);

        // create property
        requirementsSet.getProperty().add(property);
        Long createdPropertyId = createProperty(requirementsSetId, property);

        // get property
        Property createdProperty = getProperty(requirementsSetId, createdPropertyId);
        PropertyDefinition createdRequirementSet = getRequirementsSet(requirementsSetId);
        assertEquals(requirementsSet.getProperty().size(), createdRequirementSet.getProperty().size());
        assertEquals(property.getDst(), createdProperty.getDst());
        assertEquals(property.getHTTPDefinition().getUrl(), createdProperty.getHTTPDefinition().getUrl());
        assertEquals(property.getPOP3Definition().getFrom(), createdProperty.getPOP3Definition().getFrom());

        // delete property
        requirementsSet.getProperty()
                .removeIf(property -> property.getSrc().equals(createdProperty.getSrc())
                        && property.getDst().equals(createdProperty.getDst())
                        && property.getBody().equals(createdProperty.getBody())
                        // add other equivalence criteria if necessary
                        );
        deleteProperty(requirementsSetId, createdPropertyId);

        // delete requirements sets
        deleteRequirementsSets();
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

    private List<PropertyDefinition> getRequirementsSets() throws Exception {
        String rawResponse = mvc
                .perform(get("/adp/requirements").contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()).andReturn().getResponse().getContentAsString();

        JsonNode jsonResponse = objectMapper.readTree(rawResponse);
        String rawContent = jsonResponse.get("_embedded").get("propertyDefinitionList").toString();
        return Arrays.asList(objectMapper.readValue(rawContent, PropertyDefinition[].class));
    }

    private void deleteRequirementsSets() throws Exception {
        mvc.perform(
                delete("/adp/requirements").contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    private PropertyDefinition getRequirementsSet(Long requirementsSetId) throws Exception {
        String rawResponse = mvc
                .perform(get("/adp/requirements/" + requirementsSetId).contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()).andReturn().getResponse().getContentAsString();

        JsonNode jsonResponse = objectMapper.readTree(rawResponse);
        String rawContent = jsonResponse.get("_embedded").get("propertyDefinitionList").elements().next().toString();
        return objectMapper.readValue(rawContent, PropertyDefinition.class);
    }

    private void updateRequirementsSet(Long id, PropertyDefinition requirementsSet) throws Exception {
        String body = objectMapper.writeValueAsString(requirementsSet);
        mvc.perform(put("/adp/requirements/" + id).contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON).content(body)).andExpect(status().isOk());
    }

    private void deleteRequirementsSet(Long requirementsSetId) throws Exception {
        mvc.perform(delete("/adp/requirements/" + requirementsSetId).contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)).andExpect(status().isOk());
    }

    private Long createProperty(Long requirementsSetId, Property property) throws Exception {
        String body = objectMapper.writeValueAsString(property);

        String rawResponse = mvc
                .perform(post("/adp/requirements/" + requirementsSetId + "/properties")
                        .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isCreated()).andReturn().getResponse().getContentAsString();

        JsonNode jsonResponse = objectMapper.readTree(rawResponse);
        String rawContent = jsonResponse.get("_embedded").get("longList").elements().next().toString();

        return objectMapper.readValue(rawContent, Long.class);
    }

    private Property getProperty(Long requirementsSetId, Long propertyId) throws Exception {
        String rawResponse = mvc
                .perform(get("/adp/requirements/" + requirementsSetId + "/properties/" + propertyId)
                        .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()).andReturn().getResponse().getContentAsString();

        JsonNode jsonResponse = objectMapper.readTree(rawResponse);
        String rawContent = jsonResponse.get("_embedded").get("propertyList").elements().next().toString();
        return objectMapper.readValue(rawContent, Property.class);
    }

    private void deleteProperty(Long requirementsSetId, Long propertyId) throws Exception {
        mvc.perform(delete("/adp/requirements/" + requirementsSetId + "/properties/" + propertyId)
                .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)).andExpect(status().isOk());
    }

}
