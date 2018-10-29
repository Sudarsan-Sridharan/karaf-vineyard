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

/**
 * Service managing the {@link Policy} registry.
 */
public interface PolicyRegistryService {

    /**
     * Add a new policy in the registry.
     * @param policy
     * @return The policy created
     */
    Policy add(Policy policy);

    /**
     * Search all the policy in the registry.
     * @return the list of the policy
     */
    Collection<Policy> list();

    /**
     * Update a policy.
     * @param policy
     */
    void update(Policy policy);

    /**
     * Delete a policy.
     * @param id
     */
    void delete(String id);

    /**
     * Get a policy by id.
     * @param id
     * @return the policy
     */
    Policy get(String id);

}
