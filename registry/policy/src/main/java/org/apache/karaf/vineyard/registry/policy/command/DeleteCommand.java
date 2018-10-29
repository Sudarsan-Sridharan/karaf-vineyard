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
package org.apache.karaf.vineyard.registry.policy.command;

import org.apache.karaf.shell.api.action.Action;
import org.apache.karaf.shell.api.action.Argument;
import org.apache.karaf.shell.api.action.Command;
import org.apache.karaf.shell.api.action.Completion;
import org.apache.karaf.shell.api.action.lifecycle.Reference;
import org.apache.karaf.shell.api.action.lifecycle.Service;
import org.apache.karaf.vineyard.common.PolicyRegistryService;
import org.apache.karaf.vineyard.registry.policy.command.completer.PolicyIdCompleter;

@Service
@Command(scope = "vineyard", name = "policy-delete", description = "Delete an Policy from the registry")
public class DeleteCommand implements Action {

    @Reference
    private PolicyRegistryService policyRegistryService;

    @Argument(name = "id", description = "ID of the Policy to delete", required = true, multiValued = false)
    @Completion(PolicyIdCompleter.class)
    String id;

    @Override
    public Object execute() throws Exception {
        policyRegistryService.delete(id);
        return null;
    }

}
