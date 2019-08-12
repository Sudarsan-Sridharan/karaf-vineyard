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

public interface GatewayService {

    void publish(API api) throws Exception;

    void publish(API api, RestResource restResource) throws Exception;

    void delete(API api) throws Exception;

    void remove(API api, RestResource resource) throws Exception;

    void resume(API api, RestResource resource) throws Exception;

    void suspend(API api, RestResource resource) throws Exception;

    /**
     * Return the status of the resource: NotPublished Starting Started Stopping Stopped Suspending
     * Suspended
     *
     * @param apiId
     * @param resourceId
     * @return the status of the resource
     * @throws Exception
     */
    String getStatus(String apiId, String resourceId) throws Exception;
}
