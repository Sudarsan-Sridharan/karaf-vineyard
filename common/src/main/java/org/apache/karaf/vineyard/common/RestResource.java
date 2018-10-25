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

import java.util.Map;

/**
 * Describe a resource in a Rest API.
 */
public class RestResource {

    /** Unique RestResource ID */
    private String id;

    /** Open text description */
    private String description;

    /** The base path of the Resource */
    private String path;

    /** The HTTP method of the Resource : GET, POST, PUT, DELETE, PATCH */
    private String method;

    /** The version of the Resource */
    private String version;

    /** The media type accepted by the Resource */
    private String accept;

    /** The media type produced by the Resource */
    private String mediaType;

    /** The policies to apply on the Resource by the Gateway ordered by the integer key of the map */
    private Map<Integer, Policy> policies;

    /** The response of the Resource, it could be static */
    private String response;

    /** The endpoint consumed by the Resource */
    private String endpoint;

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

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getAccept() {
        return accept;
    }

    public void setAccept(String accept) {
        this.accept = accept;
    }

    public String getMediaType() {
        return mediaType;
    }

    public void setMediaType(String mediaType) {
        this.mediaType = mediaType;
    }

    public Map<Integer, Policy> getPolicies() {
        return policies;
    }

    public void setPolicies(Map<Integer, Policy> policies) {
        this.policies = policies;
    }

    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        this.response = response;
    }

    public String getEndpoint() {
        return endpoint;
    }

    public void setEndpoint(String endpoint) {
        this.endpoint = endpoint;
    }
}
