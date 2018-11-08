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

@Service
@Command(scope = "vineyard", name = "api-add", description = "Add a new API in the registry")
public class AddCommand implements Action {

    @Reference private ApiRegistryService apiRegistryService;

    @Argument(
            index = 0,
            name = "name",
            description = "API name",
            required = true,
            multiValued = false)
    String name;

    @Option(
            name = "--context",
            description = "API base context URL",
            required = false,
            multiValued = false)
    String context;

    @Option(
            name = "--description",
            description = "API description",
            required = false,
            multiValued = false)
    String description;

    @Override
    public Object execute() throws Exception {
        API api = new API();
        api.setName(name);
        api.setContext(context);
        api.setDescription(description);
        api = apiRegistryService.add(api);
        System.out.println("API " + name + " has been added (" + api.getId() + ")");
        return api;
    }
}
