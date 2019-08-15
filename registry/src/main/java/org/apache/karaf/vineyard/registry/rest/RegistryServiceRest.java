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

import io.swagger.v3.oas.annotations.servers.Server;
import io.swagger.v3.oas.annotations.tags.*;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collection;
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
import org.apache.cxf.jaxrs.ext.multipart.Multipart;
import org.apache.cxf.jaxrs.ext.multipart.MultipartBody;
import org.apache.cxf.rs.security.cors.CrossOriginResourceSharing;
import org.apache.karaf.vineyard.common.API;
import org.apache.karaf.vineyard.common.Policy;
import org.apache.karaf.vineyard.common.RegistryService;
import org.apache.karaf.vineyard.common.RestResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Path("/")
@Consumes({MediaType.APPLICATION_JSON})
@Produces({MediaType.APPLICATION_JSON})
@CrossOriginResourceSharing(allowAllOrigins = true, allowCredentials = true)
@Server(url = "/cxf/vineyard-registry")
public class RegistryServiceRest {

    private static Logger LOGGER = LoggerFactory.getLogger(RegistryServiceRest.class);

    private RegistryService registry;

    public void setRegistry(RegistryService registry) {
        this.registry = registry;
    }

    @Path("/api")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Tag(name = "Api")
    public Response addApi(API api) throws Exception {

        API newApi = registry.add(api);
        try {
            return Response.created(new URI("/api/" + newApi.getId())).build();
        } catch (URISyntaxException e) {
            return Response.serverError().build();
        }
    }

    @Path("/api/{id}/upload-definition")
    @PUT
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Multipart(value = "root", type = MediaType.APPLICATION_OCTET_STREAM)
    @Tag(name = "Api")
    public Response uploadDefinitionApi(@PathParam("id") String id, MultipartBody body) {

        API api = registry.get(id);
        try (InputStream inputStream = body.getRootAttachment().getDataHandler().getInputStream()) {
            registry.definition(api, inputStream);
        } catch (Exception exception) {
            return Response.status(
                            Response.Status.UNSUPPORTED_MEDIA_TYPE.getStatusCode(),
                            exception.getMessage())
                    .build();
        }
        return Response.ok().build();
    }

    @Path("/api")
    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Tag(name = "Api")
    public Response updateApi(API api) {

        registry.update(api);
        return Response.ok().build();
    }

    @Path("/api/{id}")
    @DELETE
    @Tag(name = "Api")
    public Response deleteApi(@PathParam("id") String id) {

        API api = registry.get(id);
        if (api != null) {
            registry.delete(api.getId());
            return Response.ok().build();
        } else {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
    }

    @Path("/api/{id}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Tag(name = "Api")
    public Response getApi(@PathParam("id") String id) {

        API api = registry.get(id);
        if (api != null) {
            return Response.ok(api).build();
        } else {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
    }

    @Path("/api")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Tag(name = "Api")
    public Response getApis() {

        Collection<API> apis = registry.list();
        if (apis != null) {
            return Response.ok(apis).build();
        } else {
            return Response.noContent().build();
        }
    }

    @Path("/api/{id}/rest-resources")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Tag(name = "Api")
    public Response getApiRestResources(@PathParam("id") String id) {

        API api = registry.get(id);
        if (api != null) {
            Collection<RestResource> restResources = registry.listRestResources(api);
            if (restResources != null) {
                return Response.ok(restResources).build();
            } else {
                return Response.noContent().build();
            }
        } else {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
    }

    @Path("/api/{id}/rest-resources")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Tag(name = "Api")
    public Response addApiRestResources(@PathParam("id") String id, RestResource restResource) {

        API api = registry.get(id);
        if (api != null) {
            RestResource result = registry.addRestResource(api, restResource);
            if (result != null) {
                return Response.ok().build();
            } else {
                return Response.notModified().build();
            }
        } else {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
    }

    @Path("/api/{id}/rest-resources/{idRestResource}/policies")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Tag(name = "Api")
    public Response getApiRestResourcesPolicies(
            @PathParam("id") String id, @PathParam("idRestResource") String idRestResource) {

        API api = registry.get(id);
        if (api != null) {
            RestResource restResource =
                    registry.listRestResources(api)
                            .stream()
                            .filter(restResource1 -> restResource1.getId().equals(idRestResource))
                            .findFirst()
                            .get();

            if (restResource != null) {
                Map<Integer, Policy> appliedPolicies = registry.listAppliedPolicies(restResource);
                if (appliedPolicies != null && !appliedPolicies.isEmpty()) {
                    return Response.ok(appliedPolicies).build();
                } else {
                    return Response.noContent().build();
                }
            } else {
                return Response.status(Response.Status.NOT_FOUND).build();
            }
        } else {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
    }

    @Path("/api/{id}/rest-resources/{idRestResource}/policies/{order}")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Tag(name = "Api")
    public Response applyPolicy(
            @PathParam("id") String id,
            @PathParam("idRestResource") String idRestResource,
            @PathParam("order") int order,
            Policy policy) {

        API api = registry.get(id);
        if (api != null) {
            registry.applyPolicy(idRestResource, policy.getId(), order, policy.getParam());
            return Response.ok().build();
        } else {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
    }

    @Path("/api/{id}/rest-resources/{idRestResource}/policies/{idPolicy}")
    @DELETE
    @Produces(MediaType.APPLICATION_JSON)
    @Tag(name = "Api")
    public Response unapplyPolicy(
            @PathParam("id") String id,
            @PathParam("idRestResource") String idRestResource,
            @PathParam("idPolicy") String idPolicy) {

        API api = registry.get(id);
        if (api != null) {
            registry.unapplyPolicy(idRestResource, idPolicy);
            return Response.ok().build();
        } else {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
    }

    @Path("/policy")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Tag(name = "Policy")
    public Response addPolicy(Policy policy) throws Exception {

        Policy newPolicy = registry.addPolicy(policy);
        try {
            return Response.created(new URI("/policy/" + newPolicy.getId())).build();
        } catch (URISyntaxException e) {
            return Response.serverError().build();
        }
    }

    @Path("/policy/{id}")
    @DELETE
    @Tag(name = "Policy")
    public Response deletePolicy(@PathParam("id") String id) {

        registry.deletePolicy(id);
        return Response.ok().build();
    }

    @Path("/policy")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Tag(name = "Policy")
    public Response getPolicies() {

        Collection<Policy> policies = registry.listPolicies();
        if (policies != null) {
            return Response.ok(policies).build();
        } else {
            return Response.noContent().build();
        }
    }

    @Path("/policy/{id}/rest-resources")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Tag(name = "Policy")
    public Response getRestResourcesApplyPolicy() {

        Collection<Policy> policies = registry.listPolicies();
        if (policies != null) {
            return Response.ok(policies).build();
        } else {
            return Response.noContent().build();
        }
    }
}
