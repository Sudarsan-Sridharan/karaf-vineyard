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

import org.apache.karaf.vineyard.common.API;
import org.apache.karaf.vineyard.common.DataFormat;
import org.apache.karaf.vineyard.registry.api.RegistryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import java.util.Collection;

@Path("/")
@Consumes({"application/json"})
@Produces({"application/json"})
public class RegistryRest {

    private static Logger LOGGER = LoggerFactory.getLogger(RegistryRest.class);

    private RegistryService registry;
    
    public void setRegistry(RegistryService registry) {
        this.registry = registry;
    }
    
    @Path("/api")
    @POST
    public Response addApi(API api) {
        if (registry != null) {
            registry.addApi(api);
            return Response.ok().build();
        } else {
            LOGGER.error("Registry service is null !");
            return Response.serverError().build();
        }
    }

    @Path("/api")
    @DELETE
    public Response deleteApi(API api) {
        if (registry != null) {
             registry.deleteApi(api);
             return Response.ok().build();
         } else {
             LOGGER.error("Registry service is null !");
             return Response.serverError().build();
         }
    }
    
    @Path("/api/{id}")
    @DELETE
    public Response deleteApi(@PathParam("id") String id) {
        if (registry != null) {
             registry.deleteApi(id);
             return Response.ok().build();
         } else {
             LOGGER.error("Registry service is null !");
             return Response.serverError().build();
         }
    }
    
    @Path("/api/{id}")
    @GET
    @Produces("application/json")
    public Response getApi(@PathParam("id") String id) {
        
        if (registry != null) {
            API api = registry.getApi(id);
            if (api != null) {
                return Response.ok(api).build();
            } else {
                return Response.noContent().build();
            }
        } else {
            LOGGER.error("Registry service is null !");
            return Response.serverError().build();
        }
    }

    @Path("/api")
    @GET
    @Produces("application/json")
    public Response getApis() {
        
        if (registry != null) {
            Collection<API> apis = registry.getApis();
            if (apis != null) {
                return Response.ok(apis).build();
            } else {
                return Response.noContent().build();
            }
        } else {
            LOGGER.error("Registry service is null !");
            return Response.serverError().build();
        }
    }

    @Path("/dataformat")
    @POST
    public Response addDataFormat(DataFormat dataformat) {
        if (registry != null) {
            registry.addDataFormat(dataformat);
            return Response.ok().build();
        } else {
            LOGGER.error("Registry service is null !");
            return Response.serverError().build();
        }
    }

    @Path("/dataformat")
    @DELETE
    public Response deleteDataFormat(DataFormat dataformat) {
        if (registry != null) {
            registry.deleteDataFormat(dataformat);
            return Response.ok().build();
        } else {
            LOGGER.error("Registry service is null !");
            return Response.serverError().build();
        }
    }

    @Path("/dataformat/{id}")
    @DELETE
    public Response deleteDataFormat(@PathParam("id") String id) {
        if (registry != null) {
            registry.deleteDataFormat(id);
            return Response.ok().build();
        } else {
            LOGGER.error("Registry service is null !");
            return Response.serverError().build();
        }
    }

    @Path("/dataformat/{id}")
    @GET
    @Produces("application/json")
    public Response getDataFormat(@PathParam("id") String id) {

        if (registry != null) {
            DataFormat dataformat = registry.getDataFormat(id);
            if (dataformat != null) {
                return Response.ok(dataformat).build();
            } else {
                return Response.noContent().build();
            }
        } else {
            LOGGER.error("Registry service is null !");
            return Response.serverError().build();
        }
    }

    @Path("/dataformat")
    @GET
    @Produces("application/json")
    public Response listDataFormat() {

        if (registry != null) {
            Collection<DataFormat> dataformats = registry.getDataFormats();
            if (dataformats != null) {
                return Response.ok(dataformats).build();
            } else {
                return Response.noContent().build();
            }
        } else {
            LOGGER.error("Registry service is null !");
            return Response.serverError().build();
        }
    }
}
