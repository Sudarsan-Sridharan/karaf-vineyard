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
package org.apache.karaf.vineyard.registry.policy.rest;

import io.swagger.v3.oas.annotations.servers.Server;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
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
import org.apache.karaf.vineyard.common.Policy;
import org.apache.karaf.vineyard.common.PolicyRegistryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Path("/")
@Consumes({MediaType.APPLICATION_JSON})
@Produces({MediaType.APPLICATION_JSON})
@CrossOriginResourceSharing(
        allowAllOrigins = true,
        allowCredentials = true
)
@Server(url = "/cxf/vineyard/registry")
public class PolicyRegistryRest {

    private static Logger LOGGER = LoggerFactory.getLogger(PolicyRegistryRest.class);

    private PolicyRegistryService registry;
    
    public void setRegistry(PolicyRegistryService registry) {
        this.registry = registry;
    }
    
    @Path("/policy")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Tag(name = "Policy")
    public Response addPolicy(Policy policy) throws Exception {

        Policy newPolicy = registry.add(policy);
        try {
            return Response.created(new URI("/policy/" + newPolicy.getId())).build();
        } catch (URISyntaxException e) {
            return Response.serverError().build();
        }
    }

    @Path("/policy")
    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Tag(name = "Policy")
    public Response updatePolicy(Policy policy) {

        registry.update(policy);
        return Response.ok().build();
    }

    @Path("/policy/{id}")
    @DELETE
    @Tag(name = "Policy")
    public Response deletePolicy(@PathParam("id") String id) {

        Policy policy = registry.get(id);
        if (policy != null) {
            registry.delete(policy.getId());
            return Response.ok().build();
        } else {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
    }
    
    @Path("/policy/{id}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Tag(name = "Policy")
    public Response getPolicy(@PathParam("id") String id) {
        
        Policy policy = registry.get(id);
        if (policy != null) {
            return Response.ok(policy).build();
        } else {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
    }

    @Path("/policy")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Tag(name = "Policy")
    public Response getPolicies() {
        
        Collection<Policy> policies = registry.list();
        if (policies != null) {
            return Response.ok(policies).build();
        } else {
            return Response.noContent().build();
        }
    }

    @Path("/policy/{id}/meta")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Tag(name = "Policy")
    public Response addMeta(@PathParam("id") String id, Map<String, String> metas) throws Exception {

        Policy policy = registry.get(id);
        if (policy != null) {
            registry.addMeta(policy, metas);
            return Response.ok().build();
        } else {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
    }

    @Path("/policy/{id}/meta")
    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Tag(name = "Policy")
    public Response updateMeta(@PathParam("id") String id, Map<String, String> metas) {

        Policy policy = registry.get(id);
        if (policy != null) {
            registry.updateMeta(policy, metas);
            return Response.ok().build();
        } else {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
    }

    @Path("/policy/{id}/meta/{key}")
    @DELETE
    @Tag(name = "Policy")
    public Response deleteMeta(@PathParam("id") String id, @PathParam("key") String key) {

        Policy policy = registry.get(id);
        if (policy != null) {
            registry.deleteMeta(policy, key);
            return Response.ok().build();
        } else {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
    }

    @Path("/policy/{id}/meta")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Tag(name = "Policy")
    public Response getMetas(@PathParam("id") String id) {

        Policy policy = registry.get(id);
        if (policy != null) {
            Map<String, String> metas = registry.getMeta(policy);
            if (metas != null) {
                return Response.ok(metas).build();
            } else {
                return Response.ok(new HashMap<String, String>()).build();
            }
        } else {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
    }

}
