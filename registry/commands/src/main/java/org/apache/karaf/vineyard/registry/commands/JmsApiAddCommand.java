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
import org.apache.karaf.vineyard.common.JmsAPI;
import org.apache.karaf.vineyard.common.RestAPI;

/**
 * Command to list all Registry jmsapi
 */
@Command(scope = "vineyard", name = "jmsapi-add", description = "Add a jmsapi in the Registry")
@Service
public class JmsApiAddCommand extends VineyardRegistryCommandSupport {
    
    @Argument(index = 0, name = "name", description = "Shortcut name of the jmsapi", required = true, multiValued = false)
    private String name;
    
    @Argument(index = 1, name = "description", description = "Description of the jmsapi", required = false, multiValued = false)
    private String description;

    protected Object doExecute() throws Exception {
        
        JmsAPI api = new JmsAPI();
        api.setName(name);
        api.setDescription(description);
        
        getRegistryService().addJmsAPI(api);

        return null;
    }

}
