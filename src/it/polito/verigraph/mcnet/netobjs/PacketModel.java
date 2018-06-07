/*******************************************************************************
 * Copyright (c) 2017 Politecnico di Torino and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Apache License, Version 2.0
 * which accompanies this distribution, and is available at
 * http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package it.polito.verigraph.mcnet.netobjs;

import com.microsoft.z3.DatatypeExpr;

/*
 * Fields that can be configured -> "dest","body","seq","proto","emailFrom","url","options"
 */
public class PacketModel {

    private DatatypeExpr ip_dest;
    private Integer body;
    private Integer seq;
    private Integer proto;
    private Integer emailFrom;
    private Integer url;
    private Integer options;

    public DatatypeExpr getIp_dest() {
        return ip_dest;
    }
    public void setIp_dest(DatatypeExpr ip_dest) {
        this.ip_dest = ip_dest;
    }
    public Integer getBody() {
        return body;
    }
    public void setBody(Integer body) {
        this.body = body;
    }
    public Integer getSeq() {
        return seq;
    }
    public void setSeq(Integer seq) {
        this.seq = seq;
    }
    public Integer getProto() {
        return proto;
    }
    public void setProto(Integer proto) {
        this.proto = proto;
    }
    public Integer getEmailFrom() {
        return emailFrom;
    }
    public void setEmailFrom(Integer emailFrom) {
        this.emailFrom = emailFrom;
    }
    public Integer getUrl() {
        return url;
    }
    public void setUrl(Integer url) {
        this.url = url;
    }
    public Integer getOptions() {
        return options;
    }
    public void setOptions(Integer options) {
        this.options = options;
    }

}
