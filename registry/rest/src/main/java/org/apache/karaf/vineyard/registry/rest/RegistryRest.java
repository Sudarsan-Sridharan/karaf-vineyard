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

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

import org.apache.karaf.vineyard.common.Environment;
import org.apache.karaf.vineyard.common.Maintainer;
import org.apache.karaf.vineyard.common.Service;
import org.apache.karaf.vineyard.registry.api.RegistryService;

@Path("/")
@Consumes({"application/json"})
@Produces({"application/json"})
@Api(tags = {"registryRest"})
public class RegistryRest {

    private RegistryService registry;
    
    public void setRegistry(RegistryService registry) {
        this.registry = registry;
    }
    
    @Path("/service")
    @POST
    @ApiOperation(value = "Create a Service", notes = "Create a new service in the registry")
    public Response addService(@ApiParam(value = "the Service to create",
            required = true) Service service) {
        if (registry != null) {
           registry.add(service);
           return Response.ok().build();
        } else {
            return Response.serverError().build();
        }
    }

    @Path("/service")
    @DELETE
    @ApiOperation(value = "Delete a Service", notes = "Delete a service to find in the registry")
    public Response deleteService(@ApiParam(value = "the Service to delete",
            required = true) Service service) {
        if (registry != null) {
            registry.delete(service);
            return Response.ok().build();
         } else {
             return Response.serverError().build();
         }
    }
    
    @Path("/service/{id}")
    @DELETE
    @ApiOperation(value = "Delete a Service", notes = "Delete a service to find in the registry")
    public Response deleteService(@ApiParam(value = "id of the service", required = true) @PathParam("id") String id) {
        if (registry != null) {
            registry.delete(id);
            return Response.ok().build();
         } else {
             return Response.serverError().build();
         }
    }
    
    @Path("/service/{id}")
    @GET
    @Produces("application/json")
    @ApiOperation(value = "Find one Service", notes = "Service id of the service to find in the registry", 
        response = Service.class, responseContainer = "Service")
    public Response getService(@ApiParam(value = "id of the service", required = true) @PathParam("id") String id) {
        
        if (registry != null) {
            Service service = registry.get(id);
            if (service != null) {
                return Response.ok(service).build();
            } else {
                return Response.noContent().build();
            }
        } else {
            return Response.serverError().build();
        }
    }

    @Path("/service")
    @GET
    @Produces("application/json")
    @ApiOperation(value = "Retrieve all the services in the registry", notes = "n/a",
            response = Service.class, responseContainer = "Service")
    public Response listServices() {
        
        if (registry != null) {
            List<Service> services = registry.getAll();
            if (services != null) {
                return Response.ok(services).build();
            } else {
                return Response.noContent().build();
            }
        } else {
            return Response.serverError().build();
        }
    }

    @Path("/environment")
    @POST
    @ApiOperation(value = "Create an Environment", notes = "Create a new environment in the registry")
    public Response addEnvironment(@ApiParam(value = "the Environment to create",
            required = true) Environment environment) {
        if (registry != null) {
            registry.addEnvironment(environment);
            return Response.ok().build();
        } else {
            return Response.serverError().build();
        }
    }

    @Path("/environment")
    @DELETE
    @ApiOperation(value = "Delete an Environment", notes = "Delete an environment to find in the registry")
    public Response deleteEnvironment(@ApiParam(value = "the Environment to delete",
            required = true) Environment environment) {
        if (registry != null) {
            registry.deleteEnvironment(environment);
            return Response.ok().build();
        } else {
            return Response.serverError().build();
        }
    }

    @Path("/environment/{id}")
    @DELETE
    @ApiOperation(value = "Delete an Environment", notes = "Delete an environment to find in the registry")
    public Response deleteEnvironment(@ApiParam(value = "id of the environment", required = true) @PathParam("id") String id) {
        if (registry != null) {
            registry.delete(id);
            return Response.ok().build();
        } else {
            return Response.serverError().build();
        }
    }

    @Path("/environment/{id}")
    @GET
    @Produces("application/json")
    @ApiOperation(value = "Find one Environment", notes = "Environment id of the environment to find in the registry",
            response = Environment.class, responseContainer = "Environment")
    public Response getEnvironment(@ApiParam(value = "id of the environment", required = true) @PathParam("id") String id) {

        if (registry != null) {
            Environment environment = registry.getEnvironment(id);
            if (environment != null) {
                return Response.ok(environment).build();
            } else {
                return Response.noContent().build();
            }
        } else {
            return Response.serverError().build();
        }
    }

    @Path("/environment")
    @GET
    @Produces("application/json")
    @ApiOperation(value = "Retrieve all the environment in the registry", notes = "n/a",
            response = Environment.class, responseContainer = "Environment")
    public Response listEnvironments() {

        if (registry != null) {
            List<Environment> environments = registry.getAllEnvironments();
            if (environments != null) {
                return Response.ok(environments).build();
            } else {
                return Response.noContent().build();
            }
        } else {
            return Response.serverError().build();
        }
    }

    @Path("/maintainer")
    @POST
    @ApiOperation(value = "Create a Maintainer", notes = "Create a new maintainer in the registry")
    public Response addMaintainer(@ApiParam(value = "the Maintainer to create",
            required = true) Maintainer maintainer) {
        if (registry != null) {
            registry.addMaintainer(maintainer);
            return Response.ok().build();
        } else {
            return Response.serverError().build();
        }
    }

    @Path("/maintainer")
    @DELETE
    @ApiOperation(value = "Delete an Maintainer", notes = "Delete a maintainer to find in the registry")
    public Response deleteMaintainer(@ApiParam(value = "the Maintainer to delete",
            required = true) Maintainer maintainer) {
        if (registry != null) {
            registry.deleteMaintainer(maintainer);
            return Response.ok().build();
        } else {
            return Response.serverError().build();
        }
    }

    @Path("/maintainer/{name}")
    @DELETE
    @ApiOperation(value = "Delete an Maintainer", notes = "Delete an maintainer to find in the registry")
    public Response deleteMaintainer(@ApiParam(value = "name of the maintainer", required = true) @PathParam("name") String name) {
        if (registry != null) {
            registry.deleteMaintainer(name);
            return Response.ok().build();
        } else {
            return Response.serverError().build();
        }
    }

    @Path("/maintainer/{name}")
    @GET
    @Produces("application/json")
    @ApiOperation(value = "Find one Maintainer", notes = "Maintainer name of the maintainer to find in the registry",
            response = Maintainer.class, responseContainer = "Maintainer")
    public Response getMaintainer(@ApiParam(value = "name of the maintainer", required = true) @PathParam("name") String name) {

        if (registry != null) {
            Maintainer maintainer = registry.getMaintainer(name);
            if (maintainer != null) {
                return Response.ok(maintainer).build();
            } else {
                return Response.noContent().build();
            }
        } else {
            return Response.serverError().build();
        }
    }

    @Path("/maintainer")
    @GET
    @Produces("application/json")
    @ApiOperation(value = "Retrieve all the maintainer in the registry", notes = "n/a",
            response = Maintainer.class, responseContainer = "Maintainer")
    public Response listMaintainers() {

        if (registry != null) {
            List<Maintainer> maintainers = registry.getAllMaintainers();
            if (maintainers != null) {
                return Response.ok(maintainers).build();
            } else {
                return Response.noContent().build();
            }
        } else {
            return Response.serverError().build();
        }
    }
}
