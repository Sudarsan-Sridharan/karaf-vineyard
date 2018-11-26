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
package org.apache.karaf.vineyard.registry.api.command;

import org.apache.karaf.shell.api.action.Action;
import org.apache.karaf.shell.api.action.Argument;
import org.apache.karaf.shell.api.action.Command;
import org.apache.karaf.shell.api.action.Option;
import org.apache.karaf.shell.api.action.lifecycle.Reference;
import org.apache.karaf.shell.api.action.lifecycle.Service;
import org.apache.karaf.vineyard.common.API;
import org.apache.karaf.vineyard.common.ApiRegistryService;
import org.apache.karaf.vineyard.common.Resource;

@Service
@Command(
        scope = "vineyard",
        name = "api-add-resource",
        description = "Add a Resource to an Api in the registry")
public class AddResourceCommand implements Action {

    @Reference private ApiRegistryService apiRegistryService;

    @Argument(index = 0, name = "api", description = "Api id", required = true, multiValued = false)
    String idApi;

    @Option(name = "--resource", description = "Resource id", required = true, multiValued = false)
    String idResource;

    @Option(
            name = "--type",
            description = "Resource type [REST]",
            required = true,
            multiValued = false)
    String type;

    @Override
    public Object execute() throws Exception {

        API api = apiRegistryService.get(idApi);
        if (api == null) {
            System.out.println("The api " + idApi + " doesn't exist in the registry!");
            return null;
        }
        Resource resource = new Resource();
        resource.setId(idResource);
        resource.setType(type);
        apiRegistryService.addResource(api, resource);
        System.out.println("Resource " + idResource + " has been added to the api " + idApi);
        return null;
    }
}
