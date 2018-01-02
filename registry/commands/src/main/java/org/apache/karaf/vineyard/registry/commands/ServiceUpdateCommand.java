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

/**
 * Command to list all Registry services
 */
@Command(scope = "vineyard", name = "service-update", description = "Update a service in the Registry")
@Service
public class ServiceUpdateCommand extends VineyardRegistryCommandSupport {
    
    @Argument(index = 0, name = "id", description = "Id of the service", required = true, multiValued = false)
    private String id;
    
    @Option(name = "-n", aliases = { "--name" }, description = "Shortcut name of the service", required = false, multiValued = false)
    private String name;
    
    @Option(name = "-d", aliases = { "--description" }, description = "Description of the service", required = false, multiValued = false)
    private String description;
    
    protected Object doExecute() throws Exception {
        
        org.apache.karaf.vineyard.common.Service service = getRegistryService().get(id);
        
        if (service != null) {
            if (name != null) {
                service.name = name;
            }
            if (description != null) {
                service.description = description;
            }
            getRegistryService().update(service);
        }

        return null;
    }
}
