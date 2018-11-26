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
package org.apache.karaf.vineyard.gateway.resource.rest;

import org.apache.camel.CamelContext;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.karaf.vineyard.common.*;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

@Component(
        service = ResourceGatewayService.class,
        property = {"type=rest"},
        immediate = true)
public class RestResourceGatewayServiceImpl implements ResourceGatewayService {

    @Reference(target = "(type=rest)")
    private ResourceRegistryService<RestResource> restResourceResourceRegistryService;

    @Reference private PolicyRegistryService policyRegistryService;

    private CamelContext camelContext = new DefaultCamelContext();

    @Activate
    public void activate() throws Exception {
        camelContext.start();
    }

    @Override
    public void publish(API api, Resource resource) throws Exception {
        String routeId = getRouteId(api, resource);
        if (camelContext.getRoute(routeId) != null) {
            throw new IllegalArgumentException("API resource already published");
        }
        RestResource restResource = restResourceResourceRegistryService.get(resource.getId());
        RouteBuilder builder =
                new RouteBuilder() {
                    @Override
                    public void configure() throws Exception {
                        String from =
                                "jetty:http://0.0.0.0:9090/vineyard"
                                        + api.getContext()
                                        + restResource.getPath();
                        if (restResource.getResponse() != null) {
                            from(from)
                                    .id(routeId)
                                    // TODO add policy
                                    .transform()
                                    .constant(restResource.getResponse());
                        } else {
                            from(from)
                                    .id(routeId)
                                    // TODO add policy
                                    .to(restResource.getEndpoint());
                        }
                    }
                };
        camelContext.addRoutes(builder);
    }

    @Override
    public void remove(API api, Resource resource) throws Exception {
        String routeId = getRouteId(api, resource);
        if (camelContext.getRoute(routeId) == null) {
            throw new IllegalArgumentException("API resource not published");
        }
        camelContext.removeRoute(routeId);
    }

    @Override
    public void resume(API api, Resource resource) throws Exception {
        camelContext.resumeRoute(getRouteId(api, resource));
    }

    @Override
    public void suspend(API api, Resource resource) throws Exception {
        camelContext.suspendRoute(getRouteId(api, resource));
    }

    private String getRouteId(API api, Resource resource) {
        return api.getId() + "-" + resource.getId();
    }
}
