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
import java.util.Map;

/** Service managing Karaf Vineyard registry. */
public interface RegistryService {

    /**
     * Add a new {@link API} in the registry.
     *
     * @param api The {@link API} to add.
     * @return The {@link API} created.
     */
    API add(API api) throws Exception;

    /**
     * Provide an {@link API} definition.
     *
     * @param api The {@link API}.
     * @param inputStream The file uploaded.
     */
    void definition(API api, InputStream inputStream) throws Exception;

    /**
     * Delete an existing {@link API} from the registry, identified by ID.
     *
     * @param id The {@link API} ID.
     */
    void delete(String id);

    /**
     * Update an existing {@link API}.
     *
     * @param api The {@link API} details.
     */
    void update(API api);

    /**
     * Retrieve {@link API} details.
     *
     * @param id The {@link API} ID.
     * @return The {@link API} description.
     */
    API get(String id);

    /**
     * Retrieve all the {@link API} in the registry.
     *
     * @return The list of {@link API}.
     */
    Collection<API> list();

    /**
     * Add a new meta for an existing {@link API}.
     *
     * @param api The {@link API} for the metadata to add.
     * @param meta The list of meta to add.
     */
    void addMeta(API api, Map<String, String> meta);

    /**
     * Delete a meta from a given {@link API}.
     *
     * @param api The {@link API} for the metadata to remove.
     * @param key The key of the metadata to remove.
     */
    void deleteMeta(API api, String key);

    /**
     * Update meta in an existing {@link API}.
     *
     * @param api The {@link API} for the metadata to update.
     * @param meta The updated meta.
     */
    void updateMeta(API api, Map<String, String> meta);

    /**
     * Retrieve all meta for a given {@link API}.
     *
     * @param api The {@link API} for the resource to retrieve.
     * @return The meta represented by "key,value".
     */
    Map<String, String> getMeta(API api);

    /**
     * Add a new REST resource in an {@link API} into the registry.
     *
     * @param api The {@link API}.
     * @param restResource The {@link RestResource} to add.
     * @return The {@link RestResource} created for the {@link API}.
     */
    RestResource addRestResource(API api, RestResource restResource);

    /**
     * Delete an existing {@link RestResource} from an existing {@link API}.
     *
     * @param api The {@link API} for the resource to remove.
     * @param restResource The {@link RestResource} to remove.
     */
    void deleteRestResource(API api, RestResource restResource);

    /**
     * Retrieve all the {@link RestResource} for an existing {@link API}.
     *
     * @param api The {@link API}.
     * @return The list of resources.
     */
    Collection<RestResource> listRestResources(API api);

    /**
     * Retrieve {@link RestResource} details.
     *
     * @param id The {@link RestResource} ID.
     * @return The {@link RestResource} description.
     */
    RestResource getRestResource(String id);

    /**
     * Add a {@link Policy}.
     *
     * @param policy The {@link Policy} to add.
     * @return The {@link Policy} created.
     */
    Policy addPolicy(Policy policy);

    /**
     * Delete a given {@link Policy}.
     *
     * @param id The {@link Policy} to delete.
     */
    void deletePolicy(String id);

    /**
     * Retrieve {@link Policy} details.
     *
     * @param id The {@link Policy} ID.
     * @return The {@link Policy} description.
     */
    Policy getPolicy(String id);

    /** List all the policies in the registry. */
    Collection<Policy> listPolicies();

    /**
     * Apply a {@link Policy} to a {@link RestResource}.
     *
     * @param restResourceId The id of the {@link RestResource}
     * @param policyId The id of the {@link Policy} to apply
     * @param order The order of the policy
     * @param params List of the parameters for the applied {@link Policy} to the {@link
     *     RestResource}
     */
    void applyPolicy(String restResourceId, String policyId, int order, Map<String, String> params);

    /**
     * Unapply a {@link Policy} to a {@link RestResource}.
     *
     * @param restResourceId The id of the {@link RestResource}
     * @param policyId The id of the {@link Policy} to unapply
     */
    void unapplyPolicy(String restResourceId, String policyId);

    /**
     * List the policies for a given {@link RestResource}.
     *
     * @param restResource The {@link RestResource}.
     */
    Collection<Policy> listAppliedPolicies(RestResource restResource);

    /**
     * Add a new meta for an existing {@link Policy}.
     *
     * @param policy The {@link Policy} for the metadata to add.
     * @param meta The list of meta to add.
     */
    void addPolicyMeta(Policy policy, Map<String, String> meta);

    /**
     * Delete a meta from a given {@link Policy}.
     *
     * @param policy The {@link Policy} for the metadata to remove.
     * @param key The key of the metadata to remove.
     */
    void deletePolicyMeta(Policy policy, String key);

    /**
     * Update meta in an existing {@link Policy}.
     *
     * @param policy The {@link Policy} for the metadata to update.
     * @param meta The updated meta.
     */
    void updatePolicyMeta(Policy policy, Map<String, String> meta);

    /**
     * Retrieve all meta for a given {@link Policy}.
     *
     * @param policy The {@link Policy} for the resource to retrieve.
     * @return The meta represented by "key,value".
     */
    Map<String, String> getPolicyMeta(Policy policy);
}
