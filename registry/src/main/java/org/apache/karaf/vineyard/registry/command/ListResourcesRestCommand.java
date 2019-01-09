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
package org.apache.karaf.vineyard.registry.command;

import org.apache.karaf.shell.api.action.Action;
import org.apache.karaf.shell.api.action.Argument;
import org.apache.karaf.shell.api.action.Command;
import org.apache.karaf.shell.api.action.Completion;
import org.apache.karaf.shell.api.action.lifecycle.Reference;
import org.apache.karaf.shell.api.action.lifecycle.Service;
import org.apache.karaf.shell.support.table.ShellTable;
import org.apache.karaf.vineyard.common.API;
import org.apache.karaf.vineyard.common.RegistryService;
import org.apache.karaf.vineyard.common.RestResource;

@Service
@Command(
        scope = "vineyard",
        name = "api-resource-list",
        description = "List the Resources of an Api in the registry")
public class ListResourcesRestCommand implements Action {

    @Reference private RegistryService registryService;

    @Argument(name = "id", description = "ID of the Api", required = true, multiValued = false)
    @Completion(RegistryService.class)
    String id;

    @Override
    public Object execute() throws Exception {

        API api = registryService.get(id);
        if (api == null) {
            System.out.println("The api " + id + " doesn't exist in the registry!");
            return null;
        }

        final ShellTable shellTable = new ShellTable();
        shellTable.column("ID");
        shellTable.column("Path");

        for (RestResource resource : registryService.listRestResources(api)) {
            shellTable.addRow().addContent(resource.getId(), resource.getPath());
        }
        shellTable.print(System.out);
        return null;
    }
}
