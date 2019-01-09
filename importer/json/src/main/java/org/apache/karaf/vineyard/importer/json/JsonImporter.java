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
package org.apache.karaf.vineyard.importer.json;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import org.apache.johnzon.mapper.MapperBuilder;
import org.apache.karaf.vineyard.common.API;
import org.apache.karaf.vineyard.common.Importer;
import org.apache.karaf.vineyard.common.Policy;
import org.apache.karaf.vineyard.common.RegistryService;
import org.apache.karaf.vineyard.common.RestResource;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

@Component(service = Importer.class, immediate = true, property = "type=json")
public class JsonImporter implements Importer {

    @Reference private RegistryService registryService;

    @Override
    public void load(InputStream inputStream) throws Exception {

        JsonRegistry registry =
                new MapperBuilder().build().readObject(inputStream, JsonRegistry.class);

        Map<String, Policy> policies = new HashMap<>();
        Map<String, RestResource> resources = new HashMap<>();
        Map<String, API> apis = new HashMap<>();

        /*
        for (Policy policy : registry.getPolicies()) {
            String oldId = policy.getId();
            policies.put(oldId, registryService.add(policy));
        }

        for (RestResource resource : registry.getResources()) {

            Map<Integer, Policy> resourcePolicy = new HashMap<>();
            resourcePolicy.putAll(resource.getPolicies());
            resource.getPolicies().clear();

            String oldId = resource.getId();
            resources.put(oldId, (RestResource) registryService.add(resource));
            for (Integer key : resourcePolicy.keySet()) {
                resourceRegistryService.addPolicy(
                        resources.get(oldId).getId(),
                        policies.get(resourcePolicy.get(key).getId()).getId(),
                        key);
            }
        }

        for (API api : registry.getApis()) {

            Map<String, Resource> apiResources = new HashMap<>();
            for (Resource resource : api.getResources()) {
                apiResources.put(resource.getId(), resource);
            }

            String oldId = api.getId();
            api.getResources().clear();
            apis.put(oldId, apiRegistryService.add(api));
            for (String key : apiResources.keySet()) {
                Resource res = new Resource();
                res.setId(resources.get(apiResources.get(key).getId()).getId());
                res.setType("rest");
                apiRegistryService.addResource(apis.get(oldId), res);
            }
        }
        */
    }
}
