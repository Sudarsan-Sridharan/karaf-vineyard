/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.karaf.vineyard.common;

import java.util.Collection;

/**
 * Describe a resource in a Rest API.
 */
public class Resource {

    private String id;

    private String description;

    /** The resource path */
    private String path;

    /** The resource operation method */
    private String method;

    private DataFormat inFormat;

    private DataFormat outFormat;

    private Collection<Policy> policies;

    private boolean useBridge;

    private String response;

    private String bridge;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public DataFormat getInFormat() {
        return inFormat;
    }

    public void setInFormat(DataFormat inFormat) {
        this.inFormat = inFormat;
    }

    public DataFormat getOutFormat() {
        return outFormat;
    }

    public void setOutFormat(DataFormat outFormat) {
        this.outFormat = outFormat;
    }

    public Collection<Policy> getPolicies() {
        return policies;
    }

    public void setPolicies(Collection<Policy> policies) {
        this.policies = policies;
    }

    public boolean isUseBridge() {
        return useBridge;
    }

    public void setUseBridge(boolean useBridge) {
        this.useBridge = useBridge;
    }

    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        this.response = response;
    }

    public String getBridge() {
        return bridge;
    }

    public void setBridge(String bridge) {
        this.bridge = bridge;
    }
}
