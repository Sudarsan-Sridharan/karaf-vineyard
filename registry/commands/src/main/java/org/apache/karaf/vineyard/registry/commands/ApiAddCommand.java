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
import org.apache.karaf.shell.api.action.lifecycle.Service;
import org.apache.karaf.vineyard.common.API;

/**
 * Command to add an api in the registry.
 */
@Command(scope = "vineyard", name = "api-add", description = "Add an api in the Registry")
@Service
public class ApiAddCommand extends VineyardRegistryCommandSupport {
    
    @Argument(index = 0, name = "name", description = "Shortcut name of the api", required = true, multiValued = false)
    private String name;
    
    @Argument(index = 1, name = "context", description = "Context of the api", required = false, multiValued = false)
    private String context;

    @Argument(index = 2, name = "description", description = "Description of the api", required = false, multiValued = false)
    private String description;

    @Argument(index = 3, name = "version", description = "Version of the api", required = false, multiValued = false)
    private String version;

    protected Object doExecute() throws Exception {
        
        API api = new API();
        api.setName(name);
        api.setContext(context);
        api.setDescription(description);
        api.setVersion(version);
        
        getRegistryService().addApi(api);

        return null;
    }

}
