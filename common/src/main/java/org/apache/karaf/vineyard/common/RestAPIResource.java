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
import java.util.Map;

/**
 * Describe a resource in a Rest API.
 */
public class RestAPIResource {

    /** The resource path */
    private String path;

    /** The resource operation method */
    private String method;

    /** The resource operation data format (json, xml, ...) */
    private DataFormat format;

    private Environment environment;

    private long throttling;

    private Collection<Policy> policies;

    private Map<String, String> metadata;

    /** The resource endpoint location */
    private String endpoint;

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

    public DataFormat getFormat() {
        return format;
    }

    public void setFormat(DataFormat format) {
        this.format = format;
    }

    public String getEndpoint() {
        return endpoint;
    }

    public void setEndpoint(String endpoint) {
        this.endpoint = endpoint;
    }

    public long getThrottling() {
        return throttling;
    }

    public void setThrottling(long throttling) {
        this.throttling = throttling;
    }

    public Collection<Policy> getPolicies() {
        return policies;
    }

    public void setPolicies(Collection<Policy> policies) {
        this.policies = policies;
    }

    public Environment getEnvironment() {
        return environment;
    }

    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }

    public Map<String, String> getMetadata() {
        return metadata;
    }

    public void setMetadata(Map<String, String> metadata) {
        this.metadata = metadata;
    }
}
