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

/** Generic service managing {@link Resource} registry. */
public interface ResourceRegistryService<ResourceT> {

    /**
     * Add a new resource in the registry.
     *
     * @param resource The resource to add in the registry.
     * @return The API created.
     */
    ResourceT add(ResourceT resource) throws Exception;

    /**
     * Delete an existing resource from the registry, identified by ID.
     *
     * @param id The resource ID.
     */
    void delete(String id);

    /**
     * Update an existing resource.
     *
     * @param resource The resource details.
     */
    void update(ResourceT resource);

    /**
     * Retrieve resource details.
     *
     * @param id The resource ID.
     * @return The resource description.
     */
    ResourceT get(String id);

    /**
     * Retrieve all the resources in the registry.
     *
     * @return The list of resources.
     */
    Collection<ResourceT> list();

    /**
     * Add a Policy to a Resource.
     *
     * @param idResource The resource ID.
     * @param idPolicy The policy ID.
     * @param policyOrder the order of the policy to be applied by the gateway.
     */
    void addPolicy(String idResource, String idPolicy, Integer policyOrder);

    /**
     * Remove a Policy from a Resource.
     *
     * @param idResource The resource ID.
     * @param idPolicy The policy ID.
     */
    void removePolicy(String idResource, String idPolicy);
}
