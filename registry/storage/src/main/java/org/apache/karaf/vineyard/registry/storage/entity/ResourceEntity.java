/*
 *  Licensed to the Apache Software Foundation (ASF) under one or more
 *  contributor license agreements.  See the NOTICE file distributed with
 *  this work for additional information regarding copyright ownership.
 *  The ASF licenses this file to You under the Apache License, Version 2.0
 *  (the "License"); you may not use this file except in compliance with
 *  the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.apache.karaf.vineyard.registry.storage.entity;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;
import java.io.Serializable;

/**
 * A regular JPA entity, using JPA annotations.
 */
@IdClass(ResourcePkEntity.class)
@Entity
@Table(name = "RESOURCE", schema = "VINEYARD")
public class ResourceEntity implements Serializable {

    @Id
    private String path;

    @Id
    @ManyToOne(fetch= FetchType.LAZY)
    @PrimaryKeyJoinColumn(name="API_ID")
    private ApiEntity api;

    private String method;

    @ManyToOne(fetch=FetchType.LAZY, cascade = CascadeType.DETACH)
    @JoinColumn(name = "IN_FORMAT_ID")
    private DataFormatEntity inFormat;

    @ManyToOne(fetch=FetchType.LAZY, cascade = CascadeType.DETACH)
    @JoinColumn(name = "OUT_FORMAT_ID")
    private DataFormatEntity outFormat;

    //private Collection<Policy> policies;

    @Column(name = "USE_BRIDGE")
    private boolean useBridge;

    private String response;

    private String bridge;

    public String getPath() {
        return this.path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public ApiEntity getApi() {
        return api;
    }

    public void setApi(ApiEntity api) {
        this.api = api;
    }

    public String getMethod() {
        return this.method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public DataFormatEntity getInFormat() {
        return this.inFormat;
    }

    public void setInFormat(DataFormatEntity inFormat) {
        this.inFormat = inFormat;
    }

    public DataFormatEntity getOutFormat() {
        return this.outFormat;
    }

    public void setOutFormat(DataFormatEntity outFormat) {
        this.outFormat = outFormat;
    }

    /*
    @Column
    public Collection<Policy> getPolicies() {
        return super.getPolicies();
    }

    public void setPolicies(Collection<Policy> policies) {
        super.setPolicies(policies);
    }
    */

    public boolean isUseBridge() {
        return this.useBridge;
    }

    public void setUseBridge(boolean useBridge) {
        this.useBridge = useBridge;
    }

    public String getResponse() {
        return this.response;
    }

    public void setResponse(String response) {
        this.response = response;
    }

    public String getBridge() {
        return this.bridge;
    }

    public void setBridge(String bridge) {
        this.bridge = bridge;
    }

}