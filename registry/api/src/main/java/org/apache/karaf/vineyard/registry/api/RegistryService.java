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

import java.util.Collection;

import org.apache.karaf.vineyard.common.*;

/**
 * Describe the actions and processes that can be done around services and registry.
 */
public interface RegistryService {

    /**
     * Add a new API in the registry.
     *
     * @param api The API to add.
     */
    void addApi(API api);

    /**
     * Delete an existing API from the registry.
     *
     * @param api The API to remove.
     */
    void deleteApi(API api);

    /**
     * Delete an existing API from the registry, identified by ID.
     *
     * @param id The API ID.
     */
    void deleteApi(String id);

    /**
     * Update an existing API.
     *
     * @param api The API details.
     */
    void updateApi(API api);

    /**
     * Retrieve API details.
     *
     * @param id The API ID.
     * @return The API description.
     */
    API getApi(String id);

    /**
     * Retrieve all the APIs in the registry.
     *
     * @return The list of API.
     */
    Collection<API> getApis();

    /**
     * Add a new data format in the registry.
     *
     * @param dataFormat The data format to add.
     */
    void addDataFormat(DataFormat dataFormat);

    /**
     * Delete an existing data format from the registry.
     *
     * @param dataFormat The data format to remove.
     */
    void deleteDataFormat(DataFormat dataFormat);

    /**
     * Delete an existing data format from the registry, identified by ID.
     *
     * @param id The data format ID.
     */
    void deleteDataFormat(String id);

    /**
     * Update an existing data format.
     *
     * @param dataFormat The data format details.
     */
    void updateDataFormat(DataFormat dataFormat);

    /**
     * Retrieve data format details.
     *
     * @param id The data format ID.
     * @return The data format description.
     */
    DataFormat getDataFormat(String id);

    /**
     * Retrieve all the data formats in the registry.
     *
     * @return The collection of data formats.
     */
    Collection<DataFormat> getDataFormats();

}
