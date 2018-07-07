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
package org.apache.karaf.vineyard.registry.commands;

import java.util.Collection;

import org.apache.karaf.shell.api.action.Command;
import org.apache.karaf.shell.api.action.lifecycle.Service;
import org.apache.karaf.shell.support.table.ShellTable;
import org.apache.karaf.vineyard.common.API;

/**
 * Command to list all apis in the registry.
 */
@Command(scope = "vineyard", name = "api-list", description = "List all apis in the registry")
@Service
public class ApiListCommand extends VineyardRegistryCommandSupport {

    protected Object doExecute() throws Exception {
        Collection<API> apis = getRegistryService().getApis();

        ShellTable table = new ShellTable();
        table.column("Id");
        table.column("Name");
        table.column("Context");
        table.column("Description");
        table.column("Version");
        // TODO add extra content
        for (API api : apis) {
            table.addRow().addContent(api.getId(), api.getName(), api.getContext(), api.getDescription(), api.getVersion());
        }

        table.print(System.out);

        return null;
    }

}
