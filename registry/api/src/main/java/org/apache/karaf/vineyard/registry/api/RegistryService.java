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
package org.apache.karaf.vineyard.registry.api;

import java.util.List;

import org.apache.karaf.vineyard.common.*;

/**
 * Describe the actions and processes that can be done around services and registry.
 */
public interface RegistryService {

    /**
     * Add a new restAPI in the registry.
     *
     * @param restAPI The restAPI to add.
     */
    void addRestAPI(RestAPI restAPI);

    /**
     * Delete an existing restAPI from the registry.
     *
     * @param restAPI The restAPI to remove.
     */
    void deleteRestAPI(RestAPI restAPI);

    /**
     * Delete an existing restAPI from the registry, identified by ID.
     *
     * @param id The restAPI ID.
     */
    void deleteRestAPI(String id);

    /**
     * Update an existing restAPI.
     *
     * @param restAPI The restAPI details.
     */
    void updateRestAPI(RestAPI restAPI);

    /**
     * Retrieve restAPI details.
     *
     * @param id The restAPI ID.
     * @return The restAPI description.
     */
    RestAPI getRestAPI(String id);

    /**
     * Retrieve all the restAPI in the registry.
     *
     * @return The list of restAPI.
     */
    List<RestAPI> getAllRestAPI();
    // TODO add same method with filter

    /**
     * Add a new jmsAPI in the registry.
     *
     * @param jmsAPI The jmsAPI to add.
     */
    void addJmsAPI(JmsAPI jmsAPI);

    /**
     * Delete an existing jmsAPI from the registry.
     *
     * @param jmsAPI The jmsAPI to remove.
     */
    void deleteJmsAPI(JmsAPI jmsAPI);

    /**
     * Delete an existing jmsAPI from the registry, identified by ID.
     *
     * @param id The jmsAPI ID.
     */
    void deleteJmsAPI(String id);

    /**
     * Update an existing jmsAPI.
     *
     * @param jmsAPI The jmsAPI details.
     */
    void updateJmsAPI(JmsAPI jmsAPI);

    /**
     * Retrieve jmsAPI details.
     *
     * @param id The jmsAPI ID.
     * @return The jmsAPI description.
     */
    JmsAPI getJmsAPI(String id);

    /**
     * Retrieve all the jmsAPI in the registry.
     *
     * @return The list of jmsAPI.
     */
    List<JmsAPI> getAllJmsAPI();
    // TODO add same method with filter

}
