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

import java.io.InputStream;
import java.util.Collection;

/**
 * Service managing {@link API} registry.
 */
public interface ApiRegistryService {

    /**
     * Add a new API in the registry.
     *
     * @param api The API to add.
     * @return The API created.
     */
    API add(API api) throws Exception;

    /**
     * Provide a API definition.
     *
     * @param api The API.
     * @param inputStream The file uploaded.
     */
    void definition(API api, InputStream inputStream) throws Exception;

    /**
     * Delete an existing API from the registry.
     *
     * @param api The API to remove.
     */
    void delete(API api);

    /**
     * Delete an existing API from the registry, identified by ID.
     *
     * @param id The API ID.
     */
    void delete(String id);

    /**
     * Update an existing API.
     *
     * @param api The API details.
     */
    void update(API api);

    /**
     * Retrieve API details.
     *
     * @param id The API ID.
     * @return The API description.
     */
    API get(String id);

    /**
     * Retrieve all the APIs in the registry.
     *
     * @return The list of API.
     */
    Collection<API> list();

    /**
     * Add a new resource for an API in the registry.
     *
     * @param api The API for the resource to add.
     * @param resource The resource to add.
     */
    // void addResource(API api, RestResource resource);

    /**
     * Delete an existing resource from an existing API.
     *
     * @param api The API for the resource to remove.
     * @param resource The resource to remove.
     */
    // void deleteResource(API api, RestResource resource);

    /**
     * Update a resource.
     *
     * @param api The API for the resource to update.
     * @param resource The resource to update.
     */
    // void updateResource(API api, RestResource resource);

    /**
     * Retrieve all the resources in an existing API.
     *
     * @param api The API for the resource to retrieve.
     * @return The list of resources.
     */
    // Collection<RestResource> getResources(API api);

    /**
     * Add a new resource for an existing API.
     *
     * @param api The API for the metadata to add.
     */
    // void addMetadata(API api, Map<String, String> metadata);

    /**
     * Delete an existing API from the registry.
     *
     * @param api The API for the metadata to remove.
     * @param metadataKey The key of the metadata to remove.
     */
    // void deleteMetadata(API api, String metadataKey);

    /**
     * Update an existing API.
     *
     * @param api The API for the metadata to update.
     */
    // void updateMetadata(API api, Map<String, String> metadata);

    /**
     * Retrieve all the APIs in the registry.
     *
     * @param api The API for the resource to retrieve.
     * @return The map of metadata represented by "key,value".
     */
    // Map<String, String> getMetadata(API api);

}
