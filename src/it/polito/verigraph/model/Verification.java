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
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(value = "Policy verification")
public class Verification {

    @ApiModelProperty(example = "SAT | UNSAT | UNKNOWN")
    private String result;
    private String comment;
    private List<Test> tests= new ArrayList<Test>();

    public Verification() {

    }

    public Verification(String result) {
        this.result = result;
    }

    public Verification(String result, List<Test> tests, String comment){
        this.result = result;
        this.tests = tests;
        this.comment = comment;
    }

    public Verification(String result, String comment){
        this.result = result;
        this.comment = comment;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public List<Test> getTests() {
        return tests;
    }

    public void setTests(List<Test> tests) {
        this.tests = tests;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

}