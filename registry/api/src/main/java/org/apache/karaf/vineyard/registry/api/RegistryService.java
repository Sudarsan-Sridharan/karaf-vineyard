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

import java.io.InputStream;
import java.util.Collection;
import java.util.Map;

import org.apache.karaf.vineyard.common.API;
import org.apache.karaf.vineyard.common.DataFormat;
import org.apache.karaf.vineyard.common.Resource;

/**
 * Describe the actions and processes that can be done around services and registry.
 */
public interface RegistryService {

    /**
     * Add a new API in the registry.
     *
     * @param api The API to add.
     * @return The API created.
     */
    API addApi(API api);

    /**
     * Upload the file definition of an API in the registry.
     *
     * @param api The API.
     * @param inputStream The file uploaded.
     */
    void updateApiDefinition(API api, InputStream inputStream);

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
     * Add a new Resource for an API in the registry.
     *
     * @param api The API for the resource to add.
     * @param resource The resource to add.
     */
    void addResource(API api, Resource resource);

    /**
     * Delete an existing API from the registry.
     *
     * @param api The API for the resource to remove.
     * @param resource The resource to remove.
     */
    void deleteResource(API api, Resource resource);

    /**
     * Update an existing API.
     *
     * @param api The API for the resource to update.
     * @param resource The resource to update.
     */
    void updateResource(API api, Resource resource);

    /**
     * Retrieve all the APIs in the registry.
     *
     * @param api The API for the resource to retreive.
     * @return The list of Resource.
     */
    Collection<Resource> getResources(API api);

    /**
     * Add a new Resource for an API in the registry.
     *
     * @param api The API for the metadata to add.
     * @param metadataKey The key of the metadata to add.
     * @param metadataValue The value of the metadata to add.
     */
    void addMetadatas(API api, Map<String, String> metadatas);

    /**
     * Delete an existing API from the registry.
     *
     * @param api The API for the metadata to remove.
     * @param metadataKey The key of the metadata to remove.
     */
    void deleteMetadata(API api, String metadataKey);

    /**
     * Update an existing API.
     *
     * @param api The API for the metadata to update.
     * @param metadataKey The key of the metadata to update.
     * @param metadataValue The value of the metadata to update..
     */
    void updateMetadatas(API api, Map<String, String> metadatas);

    /**
     * Retrieve all the APIs in the registry.
     *
     * @param api The API for the resource to retreive.
     * @return The map of metadata represented by "key,value".
     */
    Map<String, String> getMetadatas(API api);

    /**
     * Add a new data format in the registry.
     *
     * @param dataFormat The data format to add.
     * @return The dataFormat created.
     */
    DataFormat addDataFormat(DataFormat dataFormat);

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
