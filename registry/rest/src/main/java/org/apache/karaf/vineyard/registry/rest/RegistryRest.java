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

import org.apache.karaf.vineyard.common.JmsAPI;
import org.apache.karaf.vineyard.common.RestAPI;
import org.apache.karaf.vineyard.registry.api.RegistryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import java.util.List;

@Path("/")
@Consumes({"application/json"})
@Produces({"application/json"})
public class RegistryRest {

    private static Logger LOGGER = LoggerFactory.getLogger(RegistryRest.class);

    private RegistryService registry;
    
    public void setRegistry(RegistryService registry) {
        this.registry = registry;
    }
    
    @Path("/rest-api")
    @POST
    public Response addRestAPI(RestAPI restAPI) {
        if (registry != null) {
            registry.addRestAPI(restAPI);
            return Response.ok().build();
        } else {
            LOGGER.error("Registry service is null !");
            return Response.serverError().build();
        }
    }

    @Path("/rest-api")
    @DELETE
    public Response deleteRestAPI(RestAPI restAPI) {
        if (registry != null) {
             registry.deleteRestAPI(restAPI);
             return Response.ok().build();
         } else {
             LOGGER.error("Registry service is null !");
             return Response.serverError().build();
         }
    }
    
    @Path("/rest-api/{id}")
    @DELETE
    public Response deleteRestAPI(@PathParam("id") String id) {
        if (registry != null) {
             registry.deleteRestAPI(id);
             return Response.ok().build();
         } else {
             LOGGER.error("Registry service is null !");
             return Response.serverError().build();
         }
    }
    
    @Path("/rest-api/{id}")
    @GET
    @Produces("application/json")
    public Response getRestAPI(@PathParam("id") String id) {
        
        if (registry != null) {
            RestAPI restAPI = registry.getRestAPI(id);
            if (restAPI != null) {
                return Response.ok(restAPI).build();
            } else {
                return Response.noContent().build();
            }
        } else {
            LOGGER.error("Registry service is null !");
            return Response.serverError().build();
        }
    }

    @Path("/rest-api")
    @GET
    @Produces("application/json")
    public Response listRestAPI() {
        
        if (registry != null) {
            List<RestAPI> restAPIs = registry.getAllRestAPI();
            if (restAPIs != null) {
                return Response.ok(restAPIs).build();
            } else {
                return Response.noContent().build();
            }
        } else {
            LOGGER.error("Registry service is null !");
            return Response.serverError().build();
        }
    }

    @Path("/jms-api")
    @POST
    public Response addJmsAPI(JmsAPI jmsAPI) {
        if (registry != null) {
            registry.addJmsAPI(jmsAPI);
            return Response.ok().build();
        } else {
            LOGGER.error("Registry service is null !");
            return Response.serverError().build();
        }
    }

    @Path("/jms-api")
    @DELETE
    public Response deleteJmsAPI(JmsAPI jmsAPI) {
        if (registry != null) {
            registry.deleteJmsAPI(jmsAPI);
            return Response.ok().build();
        } else {
            LOGGER.error("Registry service is null !");
            return Response.serverError().build();
        }
    }

    @Path("/jms-api/{id}")
    @DELETE
    public Response deleteJmsAPI(@PathParam("id") String id) {
        if (registry != null) {
            registry.deleteJmsAPI(id);
            return Response.ok().build();
        } else {
            LOGGER.error("Registry service is null !");
            return Response.serverError().build();
        }
    }

    @Path("/jms-api/{id}")
    @GET
    @Produces("application/json")
    public Response getJmsAPI(@PathParam("id") String id) {

        if (registry != null) {
            JmsAPI jmsAPI = registry.getJmsAPI(id);
            if (jmsAPI != null) {
                return Response.ok(jmsAPI).build();
            } else {
                return Response.noContent().build();
            }
        } else {
            LOGGER.error("Registry service is null !");
            return Response.serverError().build();
        }
    }

    @Path("/jms-api")
    @GET
    @Produces("application/json")
    public Response listJmsAPI() {

        if (registry != null) {
            List<JmsAPI> jmsAPIs = registry.getAllJmsAPI();
            if (jmsAPIs != null) {
                return Response.ok(jmsAPIs).build();
            } else {
                return Response.noContent().build();
            }
        } else {
            LOGGER.error("Registry service is null !");
            return Response.serverError().build();
        }
    }
}
