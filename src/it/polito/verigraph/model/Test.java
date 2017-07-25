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

public class Test {
    private List<Node> nodes= new ArrayList<Node>();
    private String result;

    public Test() {

    }

    public Test(List<Node> paths, int result) {
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
    }

    public Test(List<Node> paths, String result) {
        this.nodes = paths;
        this.result = result;
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

}
