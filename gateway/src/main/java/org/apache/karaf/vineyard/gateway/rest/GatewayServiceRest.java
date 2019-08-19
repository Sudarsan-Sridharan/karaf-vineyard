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
package org.apache.karaf.vineyard.gateway.rest;

import io.swagger.v3.oas.annotations.servers.Server;
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
import org.apache.karaf.vineyard.common.API;
import org.apache.karaf.vineyard.common.GatewayService;
import org.apache.karaf.vineyard.common.RestResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Path("/")
@Consumes({MediaType.APPLICATION_JSON})
@Produces({MediaType.APPLICATION_JSON})
@CrossOriginResourceSharing(allowAllOrigins = true, allowCredentials = true)
@Server(url = "/cxf/vineyard-gateway")
public class GatewayServiceRest {

    private static Logger LOGGER = LoggerFactory.getLogger(GatewayServiceRest.class);

    private GatewayService gateway;

    public void setGateway(GatewayService gateway) {
        this.gateway = gateway;
    }

    @Path("/remove")
    @DELETE
    public Response removeApi(API api) {

        try {
            gateway.delete(api);
        } catch (Exception e) {
            LOGGER.error("error while removing api", e);
            return Response.serverError().build();
        }

        return Response.ok().build();
    }

    @Path("/api/{idApi}/rest-resources/{idRestResource}/remove")
    @DELETE
    @Consumes(MediaType.APPLICATION_JSON)
    public Response removeResource(
            @PathParam("idApi") String idApi, @PathParam("idRestResource") String idRestResource) {

        try {
            gateway.remove(idApi, idRestResource);
        } catch (Exception e) {
            LOGGER.error("error while removing resource", e);
            return Response.serverError().build();
        }

        return Response.ok().build();
    }

    @Path("/publish")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response publishApi(API api) throws Exception {

        try {
            gateway.publish(api);
        } catch (Exception e) {
            LOGGER.error("error while publishing api", e);
            return Response.serverError().build();
        }

        return Response.ok().build();
    }

    @Path("/publish")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response publishResource(API api, RestResource restResource) {

        try {
            gateway.publish(api, restResource);
        } catch (Exception e) {
            LOGGER.error("error while publishing resource", e);
            return Response.serverError().build();
        }

        return Response.ok().build();
    }

    @Path("/api/{idApi}/rest-resources/{idRestResource}/suspend")
    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    public Response suspendApi(
            @PathParam("idApi") String idApi, @PathParam("idRestResource") String idRestResource) {

        try {
            gateway.suspend(idApi, idRestResource);
        } catch (Exception e) {
            LOGGER.error("error while suspending resource", e);
            return Response.serverError().build();
        }

        return Response.ok().build();
    }

    @Path("/api/{idApi}/rest-resources/{idRestResource}/resume")
    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    public Response resumeApi(
            @PathParam("idApi") String idApi, @PathParam("idRestResource") String idRestResource) {

        try {
            gateway.resume(idApi, idRestResource);
        } catch (Exception e) {
            LOGGER.error("error while resuming resource", e);
            return Response.serverError().build();
        }

        return Response.ok().build();
    }

    @Path("/api/{idApi}/rest-resources/{idRestResource}/status")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getResourceStatus(
            @PathParam("idApi") String idApi, @PathParam("idRestResource") String idRestResource) {

        try {
            return Response.ok(gateway.getStatus(idApi, idRestResource)).build();
        } catch (Exception e) {
            LOGGER.error("error while retrieving status resource", e);
            return Response.serverError().build();
        }
    }
}
