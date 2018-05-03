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

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

import javax.ws.rs.*;
import javax.ws.rs.core.Response;

import org.apache.karaf.vineyard.common.*;
import org.apache.karaf.vineyard.registry.api.RegistryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Path("/")
@Consumes({"application/json"})
@Produces({"application/json"})
public class RegistryRest {

    private static Logger LOGGER = LoggerFactory.getLogger(RegistryRest.class);

    private RegistryService registry;
    
    public void setRegistry(RegistryService registry) {
        this.registry = registry;
    }
    
    @Path("/service")
    @POST
    public Response addService(Service service) {
        if (registry != null) {
           registry.add(service);
           return Response.ok().build();
        } else {
            return Response.serverError().build();
        }
    }

    @Path("/service")
    @DELETE
    public Response deleteService(Service service) {
        if (registry != null) {
            registry.delete(service);
            return Response.ok().build();
         } else {
             return Response.serverError().build();
         }
    }
    
    @Path("/service/{id}")
    @DELETE
    public Response deleteService(@PathParam("id") String id) {
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
    public Response getService(@PathParam("id") String id) {
        
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
    public Response addEnvironment(Environment environment) {
        if (registry != null) {
            registry.addEnvironment(environment);
            return Response.ok().build();
        } else {
            return Response.serverError().build();
        }
    }

    @Path("/environment")
    @DELETE
    public Response deleteEnvironment(Environment environment) {
        if (registry != null) {
            registry.deleteEnvironment(environment);
            return Response.ok().build();
        } else {
            return Response.serverError().build();
        }
    }

    @Path("/environment/{id}")
    @DELETE
    public Response deleteEnvironment(@PathParam("id") String id) {
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
    public Response getEnvironment(@PathParam("id") String id) {

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
    public Response addMaintainer(Maintainer maintainer) {
        if (registry != null) {
            registry.addMaintainer(maintainer);
            try {
                return Response.created(new URI("/maintainer/" + maintainer.getName())).build();
            } catch (URISyntaxException e) {
                LOGGER.error(e.getMessage());
                return Response.serverError().build();
            }
        } else {
            LOGGER.error("Registry service is null !");
            return Response.serverError().build();
        }
    }

    @Path("/maintainer")
    @DELETE
    public Response deleteMaintainer(Maintainer maintainer) {
        if (registry != null) {
            registry.deleteMaintainer(maintainer);
            return Response.ok().build();
        } else {
            LOGGER.error("Registry service is null !");
            return Response.serverError().build();
        }
    }

    @Path("/maintainer")
    @PUT
    public Response updateMaintainer(Maintainer maintainer) {
        if (registry != null) {
            Maintainer origin = registry.getMaintainer(maintainer.getName());
            if (origin != null) {
                registry.updateMaintainer(maintainer);
                return Response.ok().build();
            } else {
                return Response.status(Response.Status.NOT_FOUND).build();
            }
        } else {
            LOGGER.error("Registry service is null !");
            return Response.serverError().build();
        }
    }

    @Path("/maintainer/{name}")
    @DELETE
    public Response deleteMaintainer(@PathParam("name") String name) {
        if (registry != null) {
            registry.deleteMaintainer(name);
            return Response.ok().build();
        } else {
            LOGGER.error("Registry service is null !");
            return Response.serverError().build();
        }
    }

    @Path("/maintainer/{name}")
    @GET
    @Produces("application/json")
    public Response getMaintainer(@PathParam("name") String name) {

        if (registry != null) {
            Maintainer maintainer = registry.getMaintainer(name);
            if (maintainer != null) {
                return Response.ok(maintainer).build();
            } else {
                return Response.noContent().build();
            }
        } else {
            LOGGER.error("Registry service is null !");
            return Response.serverError().build();
        }
    }

    @Path("/maintainer")
    @GET
    @Produces("application/json")
    public Response listMaintainers() {

        if (registry != null) {
            List<Maintainer> maintainers = registry.getAllMaintainers();
            if (maintainers != null) {
                return Response.ok(maintainers).build();
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
            try {
                return Response.created(new URI("/dataformat/" + dataformat.getId())).build();
            } catch (URISyntaxException e) {
                LOGGER.error(e.getMessage());
                return Response.serverError().build();
            }
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

    @Path("/dataformat")
    @PUT
    public Response updateDataFormat(DataFormat dataFormat) {
        if (registry != null) {
            DataFormat origin = registry.getDataFormat(dataFormat.getId());
            if (origin != null) {
                registry.updateDataFormat(dataFormat);
                return Response.ok().build();
            } else {
                return Response.status(Response.Status.NOT_FOUND).build();
            }
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
    public Response listDataFormats() {

        if (registry != null) {
            List<DataFormat> dataformats = registry.getAllDataFormats();
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

    @Path("/endpoint")
    @POST
    public Response addEndpoint(Endpoint endpoint) {
        if (registry != null) {
            registry.addEndpoint(endpoint);
            return Response.ok().build();
        } else {
            return Response.serverError().build();
        }
    }

    @Path("/endpoint")
    @DELETE
    public Response deleteEndpoint(Endpoint endpoint) {
        if (registry != null) {
            registry.deleteEndpoint(endpoint);
            return Response.ok().build();
        } else {
            return Response.serverError().build();
        }
    }

    @Path("/endpoint/{id}")
    @DELETE
    public Response deleteEndpoint(@PathParam("id") String id) {
        if (registry != null) {
            registry.deleteEndpoint(id);
            return Response.ok().build();
        } else {
            return Response.serverError().build();
        }
    }

    @Path("/endpoint/{id}")
    @GET
    @Produces("application/json")
    public Response getEndpoint(@PathParam("id") String id) {

        if (registry != null) {
            Endpoint endpoint = registry.getEndpoint(id);
            if (endpoint != null) {
                return Response.ok(endpoint).build();
            } else {
                return Response.noContent().build();
            }
        } else {
            return Response.serverError().build();
        }
    }

    @Path("/endpoint")
    @GET
    @Produces("application/json")
    public Response listEndpoints() {

        if (registry != null) {
            List<Endpoint> endpoints = registry.getAllEndpoints();
            if (endpoints != null) {
                return Response.ok(endpoints).build();
            } else {
                return Response.noContent().build();
            }
        } else {
            return Response.serverError().build();
        }
    }
}
