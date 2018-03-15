package it.polito.verigraph.random;

import java.util.Random;

import it.polito.verigraph.model.Node;

class PolicyGen {

    private String name;
    private GraphGen graph;
    private Node nodesrc;
    private Node nodedst;
    private boolean result;

    public enum PolicyType {
        REACHABILITY,
        ISOLATION,
        TRAVERSAL
    }
    private PolicyType type;

    private static int count=0;

    public static void resetCounter() {
        count=0;
    }

    PolicyGen(GraphGen graph, Random random, Node src, Node dst, PolicyType policytype) {
        setName("Policy"+count);
        count++;

        this.graph = graph;
        nodesrc=src;
        nodedst=dst;
        type=policytype;
    }

    public GraphGen getGraph() {
        return graph;
    }

    public Boolean getResult() {
        return result;
    }

    public Node getNodesrc () {
        return nodesrc;
    }

    public void setNodesrc (Node nodesrc) {
        this.nodesrc = nodesrc;
    }

    public String getName () {
        return name;
    }

    public void setName (String name) {
        this.name = name;
    }

    public Node getNodedst () {
        return nodedst;
    }

    public void setNodedst (Node nodedst) {
        this.nodedst = nodedst;
    }

    public PolicyType getPolicyType () {
        return type;
    }

    public void setPolicyType (PolicyType type) {
        this.type = type;
    }

}
