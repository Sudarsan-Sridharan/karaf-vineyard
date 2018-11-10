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

import org.apache.karaf.shell.api.action.Action;
import org.apache.karaf.shell.api.action.Argument;
import org.apache.karaf.shell.api.action.Command;
import org.apache.karaf.shell.api.action.Option;
import org.apache.karaf.shell.api.action.lifecycle.Reference;
import org.apache.karaf.shell.api.action.lifecycle.Service;
import org.apache.karaf.vineyard.common.ResourceRegistryService;
import org.apache.karaf.vineyard.common.RestResource;

@Service
@Command(
        scope = "vineyard",
        name = "resource-rest-add",
        description = "Add a Rest Resource in the registry")
public class AddCommand implements Action {

    @Reference private ResourceRegistryService resourceRegistryService;

    @Argument(
            index = 0,
            name = "endpoint",
            description = "Rest Resource endpoint",
            required = true,
            multiValued = false)
    String endpoint;

    @Option(
            name = "--method",
            description = "Rest Resource method",
            required = true,
            multiValued = false)
    String method;

    @Option(
            name = "--path",
            description = "Rest Resource path",
            required = true,
            multiValued = false)
    String path;

    @Option(
            name = "--description",
            description = "Rest Resource description",
            required = false,
            multiValued = false)
    String description;

    @Option(
            name = "--version",
            description = "Rest Resource version",
            required = false,
            multiValued = false)
    String version;

    @Option(
            name = "--mediaType",
            description = "Rest Resource mediaType",
            required = false,
            multiValued = false)
    String mediaType;

    @Option(
            name = "--accept",
            description = "Rest Resource accept",
            required = false,
            multiValued = false)
    String accept;

    @Option(
            name = "--response",
            description = "Rest Resource response",
            required = false,
            multiValued = false)
    String response;

    @Override
    public Object execute() throws Exception {
        RestResource resource = new RestResource();
        resource.setEndpoint(endpoint);
        resource.setMethod(method);
        resource.setPath(path);
        resource.setDescription(description);
        resource.setVersion(version);
        resource.setMediaType(mediaType);
        resource.setAccept(accept);
        resource.setResponse(response);
        resource = (RestResource) resourceRegistryService.add(resource);
        System.out.println(
                "RestResource "
                        + endpoint
                        + " - "
                        + path
                        + " has been added ("
                        + resource.getId()
                        + ")");
        return resource;
    }
}
