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
package org.apache.karaf.vineyard.registry.commands.completers;

import org.apache.karaf.shell.api.action.lifecycle.Reference;
import org.apache.karaf.shell.api.action.lifecycle.Service;
import org.apache.karaf.shell.api.console.CommandLine;
import org.apache.karaf.shell.api.console.Completer;
import org.apache.karaf.shell.api.console.Session;
import org.apache.karaf.shell.support.completers.StringsCompleter;
import org.apache.karaf.vineyard.common.JmsAPI;
import org.apache.karaf.vineyard.common.RestAPI;
import org.apache.karaf.vineyard.registry.api.RegistryService;

import java.util.List;

/**
 * Completer of the Karaf Vineyard Registry services.
 */
@Service
public class RegistryJmsApiCompleter implements Completer {

    @Reference
    private RegistryService registryService;

    @Override
    public int complete(Session session, CommandLine commandLine, List<String> list) {
        StringsCompleter delegate = new StringsCompleter();
        for (JmsAPI vineyardJmsAPI :
            registryService.getAllJmsAPI()) {
            delegate.getStrings().add(vineyardJmsAPI.getName());
        }
        return delegate.complete(session, commandLine, list);
    }

}
