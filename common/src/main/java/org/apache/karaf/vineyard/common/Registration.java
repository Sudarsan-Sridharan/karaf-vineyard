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

import java.util.List;
import java.util.Map;

/**
 * Describe a service on a given environment.
 */
public class Registration {

    /** The registration ID */
    private String id;

    /** The service ID */
    private String serviceId;

    /** The environment ID */
    private Environment environment;

    /** Define the current state of the service (production ready, staging, ...) */
    private String state;

    /** Define the current version of the service. NB: the registry can deal with different versions of the same service. */
    private String version;

    /** The service maintainers with corresponding roles */
    private Map<String, Role> maintainers;

    /** The actual location (endpoint) of the service */
    private Endpoint endpoint;

    /** The endpoint used on the gateway to expose this service */
    private Endpoint gateway;

    /** Additional metadata (custom fields) of this service */
    private Map<String, String> metadata;

    /** The policies definition of this service */
    private List<Policy> policies;

    /** The throttling of this service (-1 means no limit) */
    private long throttling;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getServiceId() {
        return serviceId;
    }

    public void setServiceId(String serviceId) {
        this.serviceId = serviceId;
    }

    public Environment getEnvironment() {
        return environment;
    }

    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public Map<String, Role> getMaintainers() {
        return maintainers;
    }

    public void setMaintainers(Map<String, Role> maintainers) {
        this.maintainers = maintainers;
    }

    public Endpoint getEndpoint() {
        return endpoint;
    }

    public void setEndpoint(Endpoint endpoint) {
        this.endpoint = endpoint;
    }

    public Endpoint getGateway() {
        return gateway;
    }

    public void setGateway(Endpoint gateway) {
        this.gateway = gateway;
    }

    public Map<String, String> getMetadata() {
        return metadata;
    }

    public void setMetadata(Map<String, String> metadata) {
        this.metadata = metadata;
    }

    public List<Policy> getPolicies() {
        return policies;
    }

    public void setPolicies(List<Policy> policies) {
        this.policies = policies;
    }

    public long getThrottling() {
        return throttling;
    }

    public void setThrottling(long throttling) {
        this.throttling = throttling;
    }

}
