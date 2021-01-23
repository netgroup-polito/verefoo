package it.polito.verefoo.rest.spring.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import it.polito.verefoo.DbConfiguration;
import it.polito.verefoo.DbConstraints;
import it.polito.verefoo.DbGraph;
import it.polito.verefoo.DbNeighbour;
import it.polito.verefoo.DbNode;
import it.polito.verefoo.jaxb.Configuration;
import it.polito.verefoo.jaxb.Constraints;
import it.polito.verefoo.jaxb.Graph;
import it.polito.verefoo.jaxb.Graphs;
import it.polito.verefoo.jaxb.Neighbour;
import it.polito.verefoo.jaxb.Node;
import it.polito.verefoo.rest.spring.converter.GraphConverter;
import it.polito.verefoo.rest.spring.repository.ConfigurationRepository;
import it.polito.verefoo.rest.spring.repository.ConstraintsRepository;
import it.polito.verefoo.rest.spring.repository.GraphRepository;
import it.polito.verefoo.rest.spring.repository.NeighbourRepository;
import it.polito.verefoo.rest.spring.repository.NodeRepository;

@Service
public class GraphService {

        @Autowired
        GraphRepository graphRepository;

        @Autowired
        NodeRepository nodeRepository;

        @Autowired
        NeighbourRepository neighbourRepository;

        @Autowired
        ConfigurationRepository configurationRepository;

        @Autowired
        ConstraintsRepository constraintsRepository;

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

        @Transactional
        public Long createNode(Long id, Node node) {
                Long nodeId = nodeRepository.save(converter.deserializeNode(node)).getId();
                graphRepository.bindNode(id, nodeId);
                return nodeId;
        }

        /**
         * TODO: decide what happens if this node is defined as neighbour of another
         * node
         * 
         * @param id
         * @param nodeId
         */
        @Transactional
        public void deleteNode(Long id, Long nodeId) {
                graphRepository.unbindNode(id, nodeId);
                nodeRepository.deleteById(nodeId);
        }

        /**
         * As usual, {@code node} and its neighbours must not declare any id.
         * 
         * @param id
         * @param nodeId
         * @param node
         * @return
         */
        public Long updateNode(Long id, Long nodeId, Node node) {
                deleteNode(id, nodeId);
                return createNode(id, node);
        }

        public Node getNode(Long gid, Long nodeId) {
                Optional<DbNode> dbNode = nodeRepository.findById(nodeId);
                if (dbNode.isPresent()) {
                        return converter.serializeNode(dbNode.get());
                } else
                        return null;
        }

        public Long createNeighbour(Long id, Long nodeId, Neighbour neighbour) {
                DbNeighbour dbNeighbour = neighbourRepository.save(converter.deserializeNeighbour(neighbour));
                nodeRepository.bindNeighbour(nodeId, dbNeighbour.getId());
                return dbNeighbour.getId();
        }

        public void deleteNeighbour(Long gid, Long nodeId, Long neighbourId) {
                nodeRepository.unbindNeighbour(nodeId, neighbourId);
                neighbourRepository.deleteById(neighbourId);
        }

        public Configuration getConfiguration(Long gid, Long nodeId) {
                return converter.serializeConfiguration(nodeRepository.findConfiguration(nodeId));
        }

        @Transactional
        public Long updateConfiguration(Long id, Long nodeId, Long configurationId, Configuration configuration) {
                nodeRepository.unbindConfiguration(nodeId);
                configurationRepository.deleteById(configurationId);
                DbConfiguration dbConfiguration = configurationRepository
                                .save(converter.deserializeConfiguration(configuration));
                nodeRepository.bindConfiguration(nodeId, dbConfiguration.getId());
                return dbConfiguration.getId();
        }

        public void createConstraints(Long id, Constraints constraints) {
                DbConstraints dbConstraints = converter.deserializeConstraints(constraints);
                dbConstraints.setGraph(id);
                constraintsRepository.save(dbConstraints);
        }

        public void deleteConstraints(Long id) {
                constraintsRepository.deleteByGraphId(id);
        }

        public Constraints getConstraints(Long id) {
                Optional<DbConstraints> dbConstraints = constraintsRepository.findByGraphId(id);
                if (dbConstraints.isPresent()) {
                        return converter.serializeConstraints(dbConstraints.get());
                } else
                        return null;
        }

        @Transactional
        public void updateConstraints(Long id, Constraints constraints) {
                deleteConstraints(id);
                createConstraints(id, constraints);
        }

}
