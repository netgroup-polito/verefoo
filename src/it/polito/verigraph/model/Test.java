/*******************************************************************************
 * Copyright (c) 2017 Politecnico di Torino and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Apache License, Version 2.0
 * which accompanies this distribution, and is available at
 * http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package it.polito.verigraph.model;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlTransient;

public class Test {
    private List<Node> nodes= new ArrayList<Node>();
    private String result;
    private String model;
    private long graphId;

    public Test() {

    }

    public Test(List<Node> paths, int result, String m, long id) {
        switch (result) {
        case 0:
            this.result = "SAT";
            break;
        case -1:
            this.result = "UNSAT";
            break;
        case -2:
            this.result = "UNKNOWN";
            break;
        default:
            this.result = "UNKNWON";
            break;
        }
        this.nodes = paths;
        this. model = m;
        this.graphId = id;
    }

    public Test(List<Node> paths, String result, String m, long id) {
        this.nodes = paths;
        this.result = result;
        this. model = m;
        this.graphId=id;
    }

    public List<Node> getPath() {
        return nodes;
    }

    public void setPath(List<Node> paths) {
        this.nodes = paths;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public void setModel(String m){
        this.model= m;
    }

    //This annotation will hide the produced model by Z3 on the verification response.
    //This model is available on a file under the log folder.
    @XmlTransient
    public String getModel(){
        return this.model;
    }

    public void setGraphId(long id){
        this.graphId=id;
    }

    public long getGraphId(){
        return this.graphId;
    }
}
