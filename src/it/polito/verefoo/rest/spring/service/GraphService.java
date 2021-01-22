package it.polito.verefoo.rest.spring.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import it.polito.verefoo.DbGraph;
import it.polito.verefoo.jaxb.Graph;
import it.polito.verefoo.jaxb.Graphs;
import it.polito.verefoo.jaxb.Node;
import it.polito.verefoo.rest.spring.converter.GraphConverter;
import it.polito.verefoo.rest.spring.repository.GraphRepository;

@Service
public class GraphService {

        @Autowired
        GraphRepository graphRepository;

        @Autowired
        GraphConverter converter;

        /**
         * Each graph given in input and its neighbours must not have set any id
         * 
         * @param graphs
         * @return the generated ids for the created graphs
         */
        public List<Long> createGraphs(Graphs graphs) {
                List<Long> ids = new ArrayList<>();
                graphs.getGraph().forEach(graph -> {
                        DbGraph dbGraph = graphRepository.save(converter.deserializeGraph(graph));
                        ids.add(dbGraph.getId());
                });

                return ids;
        }

        public Graphs getGraphs() {
                Graphs graphs = new Graphs();
                graphRepository.findAll().forEach(dbGraph -> {
                        graphs.getGraph().add(converter.serializeGraph(dbGraph));
                });
                return graphs;
        }

        public void deleteGraphs() {
                graphRepository.deleteAll();
        }

        /**
         * The graph given in input and its neighbours must not have set any id
         * 
         * @param gid
         * @param graph
         * @return a new id for the modified graph
         */
        public Long updateGraph(Long gid, Graph graph) {
                graphRepository.deleteById(gid);
                DbGraph dbGraph = graphRepository.save(converter.deserializeGraph(graph));
                return dbGraph.getId();
        }

        public void deleteGraph(Long gid) {
                graphRepository.deleteById(gid);
        }

        public Graph getGraph(Long id) {
                Optional<DbGraph> dbGraph = graphRepository.findById(id);
                if (dbGraph.isPresent())
                        return converter.serializeGraph(dbGraph.get());
                else
                        return null;
        }

        public Integer createNode(long gid, Node node) {
                return null;
        }

}
