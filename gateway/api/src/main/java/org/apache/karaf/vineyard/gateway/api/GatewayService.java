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
package org.apache.karaf.vineyard.gateway.api;

import org.apache.karaf.vineyard.common.Environment;
import org.apache.karaf.vineyard.common.Service;

import java.util.Map;

/**
 * Describe gateway service.
 */
public interface GatewayService {

    /**
     * Register a service into the gateway.
     *
     * @param service The service to register.
     */
    void register(Service service);

    /**
     * Register a service for a given environment.
     *
     * @param service The service to register.
     * @param environment The environment for which we deploy the service.
     */
    void register(Service service, Environment environment);

    /**
     * Disable a service in the gateway. The service is not removed and the gateway
     * keeps the metrics for this service, however the service is not accessible for
     * the gateway clients.
     *
     * @param id The service id.
     */
    void disable(String id);

    /**
     * Enable a service in the gateway.
     *
     * @param id The service id.
     */
    void enable(String id);

    /**
     * Remove a service from the gateway. All data about the service, including metrics are
     * deleted as well.
     *
     * @param id The service id.
     */
    void remove(String id);

    /**
     * Retrieve the metrics for a given service (as number of call, average response time, ...).
     *
     * @param id The service id.
     * @return THe service metrics.
     */
    Map<String, Object> metrics(String id);

    /**
     * Add processing on the service, before calling the actual backend endpoint.
     *
     * @param id The service id.
     * @param processing The processing definition.
     */
    void addProcessing(String id, Object processing);

}
