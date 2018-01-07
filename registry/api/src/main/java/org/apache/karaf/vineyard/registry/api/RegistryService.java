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

import org.apache.karaf.vineyard.common.DataFormat;
import org.apache.karaf.vineyard.common.Environment;
import org.apache.karaf.vineyard.common.Maintainer;
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
    
    
    /**
     * Add a new environment in the registry.
     *
     * @param environment The environment to add.
     */
    void addEnvironment(Environment environment);
    
    /**
     * Delete an existing environment from the registry.
     *
     * @param environment The environment to remove.
     */
    void deleteEnvironment(Environment environment);

    /**
     * Delete an existing environment from the registry, identified by ID.
     *
     * @param id The environment ID.
     */
    void deleteEnvironment(String id);

    /**
     * Update an existing environment.
     *
     * @param environment The environment details.
     */
    void updateEnvironment(Environment environment);

    /**
     * Retrieve environment details.
     *
     * @param id The environment ID.
     * @return The environment description.
     */
    Environment getEnvironment(String id);

    /**
     * Retrieve all the environments used in the registry.
     *
     * @return The list of environments.
     */
    List<Environment> getAllEnvironments();
    // TODO add same method with filter

    
    /**
     * Add a new maintainer in the registry.
     *
     * @param maintainer The maintainer to add.
     */
    void addMaintainer(Maintainer maintainer);
    
    /**
     * Delete an existing maintainer from the registry.
     *
     * @param maintainer The maintainer to remove.
     */
    void deleteMaintainer(Maintainer maintainer);

    /**
     * Delete an existing maintainer from the registry, identified by name.
     *
     * @param name The maintainer name.
     */
    void deleteMaintainer(String name);

    /**
     * Update an existing mainainer.
     *
     * @param maintainer The maintainer details.
     */
    void updateMaintainer(Maintainer maintainer);

    /**
     * Retrieve maintainer details.
     *
     * @param name The maintainer name.
     * @return The maintainer description.
     */
    Maintainer getMaintainer(String name);

    /**
     * Retrieve all the maintainers used in the registry.
     *
     * @return The list of maintainers.
     */
    List<Maintainer> getAllMaintainers();
    // TODO add same method with filter
    
    /**
     * Add a new dataformat in the registry.
     *
     * @param dataformat The dataformat to add.
     */
    void addDataFormat(DataFormat dataformat);
    
    /**
     * Delete an existing dataformat from the registry.
     *
     * @param dataformat The dataformat to remove.
     */
    void deleteDataFormat(DataFormat dataformat);

    /**
     * Delete an existing dataformat from the registry, identified by ID.
     *
     * @param id The dataformat ID.
     */
    void deleteDataFormat(String id);

    /**
     * Update an existing mainainer.
     *
     * @param dataformat The dataformat details.
     */
    void updateDataFormat(DataFormat dataformat);

    /**
     * Retrieve dataformat details.
     *
     * @param id The dataformat ID.
     * @return The dataformat description.
     */
    DataFormat getDataFormat(String id);

    /**
     * Retrieve all the dataformats used in the registry.
     *
     * @return The list of dataformats.
     */
    List<DataFormat> getAllDataFormats();
    // TODO add same method with filter

    // TODO complete
}
