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
package org.apache.karaf.vineyard.registry.resource.rest.command;

import java.util.Collection;
import org.apache.karaf.shell.api.action.Action;
import org.apache.karaf.shell.api.action.Argument;
import org.apache.karaf.shell.api.action.Command;
import org.apache.karaf.shell.api.action.Completion;
import org.apache.karaf.shell.api.action.lifecycle.Reference;
import org.apache.karaf.shell.api.action.lifecycle.Service;
import org.apache.karaf.shell.support.table.ShellTable;
import org.apache.karaf.vineyard.common.ResourceRegistryService;
import org.apache.karaf.vineyard.registry.resource.rest.command.completer.RestResourceIdCompleter;

@Service
@Command(
        scope = "vineyard",
        name = "resource-rest-policy-list",
        description = "List the Policies of a Rest Resource in the registry")
public class ListPoliciesCommand implements Action {

    @Reference private ResourceRegistryService resourceRegistryService;

    @Argument(
            name = "id",
            description = "ID of the Rest Resource",
            required = true,
            multiValued = false)
    @Completion(RestResourceIdCompleter.class)
    String id;

    @Override
    public Object execute() throws Exception {
        final ShellTable shellTable = new ShellTable();
        shellTable.column("ID");

        Collection<String> policies = resourceRegistryService.listPolicies(id);

        for (String policyId : policies) {
            shellTable.addRow().addContent(policyId);
        }
        shellTable.print(System.out);
        return null;
    }
}
