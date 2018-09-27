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

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collection;
import java.util.Map;

import org.apache.karaf.vineyard.common.API;
import org.apache.karaf.vineyard.common.DataFormat;
import org.apache.karaf.vineyard.common.Resource;
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
    
    @Path("/api")
    @Consumes("application/json")
    @POST
    public Response addApi(API api) {
        if (registry != null) {
            API newApi = registry.addApi(api);
            try {
                return Response.created(new URI("/api/" + newApi.getId())).build();
            } catch (URISyntaxException e) {
                return Response.serverError().build();
            }
        } else {
            LOGGER.error("Registry service is null !");
            return Response.serverError().build();
        }
    }

    @Path("/api")
    @DELETE
    public Response deleteApi(API api) {
        if (registry != null) {
            API out = registry.getApi(api.getId());
            if (out != null) {
                registry.deleteApi(out);
                return Response.ok().build();
            } else {
                return Response.status(Response.Status.NOT_FOUND).build();
            }
         } else {
             LOGGER.error("Registry service is null !");
             return Response.serverError().build();
         }
    }
    
    @Path("/api/{id}")
    @DELETE
    public Response deleteApi(@PathParam("id") String id) {
        if (registry != null) {
            API api = registry.getApi(id);
            if (api != null) {
                registry.deleteApi(api);
                return Response.ok().build();
            } else {
                return Response.status(Response.Status.NOT_FOUND).build();
            }
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
                return Response.status(Response.Status.NOT_FOUND).build();
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

    @Path("/api/{id}/resource")
    @Consumes("application/json")
    @POST
    public Response addResource(@PathParam("id") String id, Resource resource) {
        if (registry != null) {
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
        } else {
            LOGGER.error("Registry service is null !");
            return Response.serverError().build();
        }
    }

    @Path("/api/{id}/resource")
    @Consumes("application/json")
    @DELETE
    public Response deleteResource(@PathParam("id") String id, Resource resource) {
        if (registry != null) {
            API api = registry.getApi(id);
            if (api != null) {
                registry.deleteResource(api, resource);
                return Response.ok().build();
            } else {
                return Response.status(Response.Status.NOT_FOUND).build();
            }
        } else {
            LOGGER.error("Registry service is null !");
            return Response.serverError().build();
        }
    }

    @Path("/api/{id}/resource")
    @GET
    @Produces("application/json")
    public Response getResources(@PathParam("id") String id) {

        if (registry != null) {
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
        } else {
            LOGGER.error("Registry service is null !");
            return Response.serverError().build();
        }
    }

    @Path("/api/{id}/metadata")
    @Consumes("application/json")
    @POST
    public Response addMetadatas(@PathParam("id") String id, Map<String, String> metadatas) {
        if (registry != null) {
            API api = registry.getApi(id);
            if (api != null) {
                registry.addMetadatas(api, metadatas);
                return Response.ok().build();
            } else {
                return Response.status(Response.Status.NOT_FOUND).build();
            }
        } else {
            LOGGER.error("Registry service is null !");
            return Response.serverError().build();
        }
    }

    @Path("/api/{id}/metadata")
    @Consumes("application/json")
    @DELETE
    public Response deleteMetadata(@PathParam("id") String id, String key) {
        if (registry != null) {
            API api = registry.getApi(id);
            if (api != null) {
                registry.deleteMetadata(api, key);
                return Response.ok().build();
            } else {
                return Response.status(Response.Status.NOT_FOUND).build();
            }
        } else {
            LOGGER.error("Registry service is null !");
            return Response.serverError().build();
        }
    }

    @Path("/api/{id}/metadata")
    @GET
    @Consumes("application/json")
    @Produces("application/json")
    public Response getMetadatas(@PathParam("id") String id) {

        if (registry != null) {
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
        } else {
            LOGGER.error("Registry service is null !");
            return Response.serverError().build();
        }
    }

    @Path("/dataformat")
    @Consumes("application/json")
    @POST
    public Response addDataFormat(DataFormat dataformat) {
        if (registry != null) {
            DataFormat newDataFormat = registry.addDataFormat(dataformat);
            try {
                return Response.created(new URI("/dataformat/" + newDataFormat.getId())).build();
            } catch (URISyntaxException e) {
                return Response.serverError().build();
            }
        } else {
            LOGGER.error("Registry service is null !");
            return Response.serverError().build();
        }
    }

    @Path("/dataformat")
    @Consumes("application/json")
    @DELETE
    public Response deleteDataFormat(DataFormat dataformat) {
        if (registry != null) {
            DataFormat out = registry.getDataFormat(dataformat.getId());
            if (out != null) {
                registry.deleteDataFormat(out);
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
            DataFormat out = registry.getDataFormat(id);
            if (out != null) {
                registry.deleteDataFormat(out);
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
    @GET
    @Produces("application/json")
    public Response getDataFormat(@PathParam("id") String id) {

        if (registry != null) {
            DataFormat dataformat = registry.getDataFormat(id);
            if (dataformat != null) {
                return Response.ok(dataformat).build();
            } else {
                return Response.status(Response.Status.NOT_FOUND).build();
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
