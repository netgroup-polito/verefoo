package it.polito.verefoo.rest.spring.integrationTest;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class GraphIT {

    @Autowired
    private MockMvc mvc;

    @Test
    public void testCreateGraphs() throws Exception {
        

        mvc.perform(post("/adp/graphs").contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON).content(graphs))
                .andExpect(status().isCreated());
    }

    @Test
    public void testGetGraphs() throws Exception {
        mvc.perform(get("/adp/graphs").contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }
}
