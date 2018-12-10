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
package org.apache.karaf.vineyard.registry.resource.rest.rest;

import io.swagger.v3.oas.annotations.servers.Server;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collection;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.apache.cxf.rs.security.cors.CrossOriginResourceSharing;
import org.apache.karaf.vineyard.common.ResourceRegistryService;
import org.apache.karaf.vineyard.common.RestResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Path("/")
@Consumes({MediaType.APPLICATION_JSON})
@Produces({MediaType.APPLICATION_JSON})
@CrossOriginResourceSharing(allowAllOrigins = true, allowCredentials = true)
@Server(url = "/cxf/vineyard-registry-resource-rest")
public class ResourceRestRegistryRest {

    private static Logger LOGGER = LoggerFactory.getLogger(ResourceRestRegistryRest.class);

    private ResourceRegistryService registry;

    public void setRegistry(ResourceRegistryService registry) {
        this.registry = registry;
    }

    @Path("/")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Tag(name = "Resource - Rest")
    public Response addResource(RestResource resource) throws Exception {

        resource = (RestResource) registry.add(resource);

        try {
            return Response.created(new URI("/" + resource.getId())).build();
        } catch (URISyntaxException e) {
            return Response.serverError().build();
        }
    }

    @Path("/")
    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Tag(name = "Resource - Rest")
    public Response updateResource(RestResource resource) {

        registry.update(resource);
        return Response.ok().build();
    }

    @Path("/{id}")
    @DELETE
    @Tag(name = "Resource - Rest")
    public Response deleteResource(@PathParam("id") String id) {

        RestResource resource = (RestResource) registry.get(id);
        if (resource != null) {
            registry.delete(resource.getId());
            return Response.ok().build();
        } else {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
    }

    @Path("/{id}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Tag(name = "Resource - Rest")
    public Response getResource(@PathParam("id") String id) {

        RestResource resource = (RestResource) registry.get(id);
        if (resource != null) {
            return Response.ok(resource).build();
        } else {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
    }

    @Path("/")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Tag(name = "Resource - Rest")
    public Response getResources() {

        Collection<RestResource> resources = registry.list();
        if (resources != null) {
            return Response.ok(resources).build();
        } else {
            return Response.noContent().build();
        }
    }
}
