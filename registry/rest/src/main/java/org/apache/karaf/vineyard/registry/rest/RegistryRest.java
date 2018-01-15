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
package org.apache.karaf.vineyard.registry.rest;

import java.util.List;

import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

import org.apache.karaf.vineyard.common.Service;
import org.apache.karaf.vineyard.registry.api.RegistryService;

@Path("/")
public class RegistryRest {

    private RegistryService registry;
    
    @Path("/service")
    @POST
    public void addService(Service service) throws Exception {
        registry.add(service);
    }

    @Path("/service")
    @DELETE
    public void deleteService(Service service) throws Exception {
        registry.delete(service);
    }
    
    @Path("/service")
    @DELETE
    public void deleteService(@PathParam("id") String id) throws Exception {
        registry.delete(id);
    }

    @Path("/service")
    @Produces("application/json")
    @GET
    public List<Service> listServices() throws Exception {
        return registry.getAll();
    }
}
