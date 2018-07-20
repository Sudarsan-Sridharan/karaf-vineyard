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

import org.apache.karaf.shell.api.action.Argument;
import org.apache.karaf.shell.api.action.Command;
import org.apache.karaf.shell.api.action.Option;
import org.apache.karaf.shell.api.action.lifecycle.Service;
import org.apache.karaf.vineyard.common.API;

/**
 * Command to update an api in the registry.
 */
@Command(scope = "vineyard", name = "api-update", description = "Update an api in the registry")
@Service
public class ApiUpdateCommand extends VineyardRegistryCommandSupport {
    
    @Argument(index = 0, name = "id", description = "Id of the api", required = true, multiValued = false)
    private String id;
    
    @Option(name = "-n", aliases = { "--name" }, description = "Shortcut name of the api", required = false, multiValued = false)
    private String name;

    @Option(name = "-c", aliases = { "--context" }, description = "Context of the api", required = false, multiValued = false)
    private String context;
    
    @Option(name = "-d", aliases = { "--description" }, description = "Description of the api", required = false, multiValued = false)
    private String description;

    @Option(name = "-v", aliases = { "--version" }, description = "Version of the api", required = false, multiValued = false)
    private String version;

    @Option(name = "-m", aliases = { "--mocked" }, description = "Is the api is mocked", required = false, multiValued = false)
    private Boolean mocked;
    
    protected Object doExecute() throws Exception {

        API api = getRegistryService().getApi(id);
        
        if (api != null) {
            if (name != null) {
                api.setName(name);
            }
            if (context != null) {
                api.setContext(context);
            }
            if (description != null) {
                api.setDescription(description);
            }
            if (version != null) {
                api.setVersion(version);
            }
            if (mocked != null) {
                api.setMocked(mocked);
            }
            getRegistryService().updateApi(api);
        }

        return null;
    }
}
