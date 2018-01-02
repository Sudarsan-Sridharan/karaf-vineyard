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

import org.apache.karaf.vineyard.common.Service;

/**
 * Describe the actions and processes that can be done around services and registry.
 */
public interface RegistryService {

    /**
     * Add a new service in the registry.
     *
     * @param service The service to add.
     */
    void add(Service service);

    /**
     * Delete an existing service from the registry.
     *
     * @param service The service to remove.
     */
    void delete(Service service);

    /**
     * Delete an existing service from the registry, identified by ID.
     *
     * @param id The service ID.
     */
    void delete(String id);

    /**
     * Update an existing service.
     *
     * @param service The service details.
     */
    void update(Service service);

    /**
     * Retrieve service details.
     *
     * @param id The service ID.
     * @return The service description.
     */
    Service get(String id);

    /**
     * Retrieve all the services in the registry.
     *
     * @return The list of services.
     */
    List<Service> getAllServices();
    // TODO add same method with filter
    
    // TODO complete

}
