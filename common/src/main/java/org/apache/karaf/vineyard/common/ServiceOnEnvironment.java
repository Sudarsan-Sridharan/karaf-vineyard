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

import java.util.List;
import java.util.Map;

/**
 * Describe a service on a given environment.
 */
public class ServiceOnEnvironment {

    /** The service ID */
    public String serviceId;

    /** The environment ID */
    public String environmentId;

    /** Define the current state of the service (production ready, staging, ...) */
    public String state;

    /** Define the current version of the service. NB: the registry can deal with different versions of the same service. */
    public String version;

    /** The service maintainers with corresponding roles */
    public Map<Maintainer, Role> maintainers;

    /** The actual location (endpoint) of the service */
    public Endpoint endpoint;

    /** The endpoint used on the gateway to expose this service */
    public Endpoint gateway;

    /** Additional metadata (custom fields) of this service */
    public Map<String, String> metadata;

    public List<Policy> policies;

}
