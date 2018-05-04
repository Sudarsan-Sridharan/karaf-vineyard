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

public class JmsAPI extends API {

    /** The identifier of the connection factory (JNDI name) */
    private String connectionFactory;

    /** The JMS destination */
    private String destination;

    /** The JMS destination type */
    private String type;

    /** The resource operation data format (json, xml, ...) */
    private DataFormat format;

    private Environment environment;

    private long throttling;

    private Collection<Policy> policies;

    private Map<String, String> metadata;

    /** The resource endpoint location */
    private String endpoint;

    public String getConnectionFactory() {
        return connectionFactory;
    }

    public void setConnectionFactory(String connectionFactory) {
        this.connectionFactory = connectionFactory;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public DataFormat getFormat() {
        return format;
    }

    public void setFormat(DataFormat format) {
        this.format = format;
    }

    public Environment getEnvironment() {
        return environment;
    }

    public void setEnvironment(Environment environment) {
        this.environment = environment;
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

    public Map<String, String> getMetadata() {
        return metadata;
    }

    public void setMetadata(Map<String, String> metadata) {
        this.metadata = metadata;
    }

    public String getEndpoint() {
        return endpoint;
    }

    public void setEndpoint(String endpoint) {
        this.endpoint = endpoint;
    }
}
