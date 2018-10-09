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

import io.swagger.v3.oas.annotations.tags.*;
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
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collection;
import java.util.Map;

import io.swagger.v3.oas.annotations.servers.Server;
import org.apache.cxf.jaxrs.ext.multipart.Multipart;
import org.apache.cxf.jaxrs.ext.multipart.MultipartBody;
import org.apache.cxf.rs.security.cors.CrossOriginResourceSharing;
import org.apache.karaf.vineyard.common.API;
import org.apache.karaf.vineyard.common.DataFormat;
import org.apache.karaf.vineyard.common.Resource;
import org.apache.karaf.vineyard.registry.api.RegistryService;
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
public class RegistryRest {

    private static Logger LOGGER = LoggerFactory.getLogger(RegistryRest.class);

    private RegistryService registry;
    
    public void setRegistry(RegistryService registry) {
        this.registry = registry;
    }
    
    @Path("/api")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Tag(name = "Api")
    public Response addApi(API api) {

        API newApi = registry.addApi(api);
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

        API api = registry.getApi(id);
        try (InputStream inputStream = body.getRootAttachment().getDataHandler().getInputStream()) {
            registry.updateApiDefinition(api, inputStream);
        } catch (Exception exception) {
            return Response.status(Response.Status.UNSUPPORTED_MEDIA_TYPE.getStatusCode(),
                    exception.getMessage()).build();
        }
        return Response.ok().build();
    }

    @Path("/api")
    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Tag(name = "Api")
    public Response updateApi(API api) {

        registry.updateApi(api);
        return Response.ok().build();
    }

    @Path("/api/{id}")
    @DELETE
    @Tag(name = "Api")
    public Response deleteApi(@PathParam("id") String id) {

        API api = registry.getApi(id);
        if (api != null) {
            registry.deleteApi(api);
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
        
        API api = registry.getApi(id);
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
        
        Collection<API> apis = registry.getApis();
        if (apis != null) {
            return Response.ok(apis).build();
        } else {
            return Response.noContent().build();
        }
    }

    @Path("/api/{id}/resource")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Tag(name = "Resource")
    public Response addResource(@PathParam("id") String id, Resource resource) {

        API api = registry.getApi(id);
        if (api != null) {
            if (resource.getInFormat() != null) {
                DataFormat inDataFormat = registry.getDataFormat(resource.getInFormat().getId());
                resource.setInFormat(inDataFormat);
            }
            if (resource.getOutFormat() != null) {
                DataFormat outDataFormat = registry.getDataFormat(resource.getOutFormat().getId());
                resource.setOutFormat(outDataFormat);
            }
            registry.addResource(api, resource);
            return Response.ok().build();
        } else {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
    }

    @Path("/api/{id}/resource")
    @DELETE
    @Consumes(MediaType.APPLICATION_JSON)
    @Tag(name = "Resource")
    public Response deleteResource(@PathParam("id") String id, Resource resource) {

        API api = registry.getApi(id);
        if (api != null) {
            registry.deleteResource(api, resource);
            return Response.ok().build();
        } else {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
    }

    @Path("/api/{id}/resource")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Tag(name = "Resource")
    public Response getResources(@PathParam("id") String id) {

        API api = registry.getApi(id);
        if (api != null) {
            Collection<Resource> resources = registry.getResources(api);
            if (resources != null) {
                return Response.ok(resources).build();
            } else {
                return Response.noContent().build();
            }
        } else {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
    }

    @Path("/api/{id}/metadata")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Tag(name = "Metadata")
    public Response addMetadatas(@PathParam("id") String id, Map<String, String> metadatas) {

        API api = registry.getApi(id);
        if (api != null) {
            registry.addMetadatas(api, metadatas);
            return Response.ok().build();
        } else {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
    }

    @Path("/api/{id}/metadata")
    @DELETE
    @Consumes(MediaType.APPLICATION_JSON)
    @Tag(name = "Metadata")
    public Response deleteMetadata(@PathParam("id") String id, String key) {

        API api = registry.getApi(id);
        if (api != null) {
            registry.deleteMetadata(api, key);
            return Response.ok().build();
        } else {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
    }

    @Path("/api/{id}/metadata")
    @GET
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Tag(name = "Metadata")
    public Response getMetadatas(@PathParam("id") String id) {

        API api = registry.getApi(id);
        if (api != null) {
            Map<String, String> metadatas = registry.getMetadatas(api);
            if (metadatas != null) {
                return Response.ok(metadatas).build();
            } else {
                return Response.noContent().build();
            }
        } else {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
    }

    @Path("/dataformat")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Tag(name = "DataFormat")
    public Response addDataFormat(DataFormat dataformat) {

        DataFormat newDataFormat = registry.addDataFormat(dataformat);
        try {
            return Response.created(new URI("/dataformat/" + newDataFormat.getId())).build();
        } catch (URISyntaxException e) {
            return Response.serverError().build();
        }
    }

    @Path("/dataformat")
    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Tag(name = "DataFormat")
    public Response updateDataFormat(DataFormat dataformat) {

        registry.updateDataFormat(dataformat);
        return Response.ok().build();
    }

    @Path("/dataformat/{id}")
    @DELETE
    @Tag(name = "DataFormat")
    public Response deleteDataFormat(@PathParam("id") String id) {

        DataFormat out = registry.getDataFormat(id);
        if (out != null) {
            registry.deleteDataFormat(out);
            return Response.ok().build();
        } else {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
    }

    @Path("/dataformat/{id}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Tag(name = "DataFormat")
    public Response getDataFormat(@PathParam("id") String id) {

        DataFormat dataformat = registry.getDataFormat(id);
        if (dataformat != null) {
            return Response.ok(dataformat).build();
        } else {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
    }

    @Path("/dataformat")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Tag(name = "DataFormat")
    public Response listDataFormat() {

        Collection<DataFormat> dataformats = registry.getDataFormats();
        if (dataformats != null) {
            return Response.ok(dataformats).build();
        } else {
            return Response.noContent().build();
        }
    }
}
