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
package org.apache.karaf.vineyard.gateway;

import java.util.TreeMap;
import org.apache.camel.CamelContext;
import org.apache.camel.Processor;
import org.apache.camel.component.jetty9.JettyHttpComponent9;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.model.RouteDefinition;
import org.apache.karaf.vineyard.common.API;
import org.apache.karaf.vineyard.common.GatewayService;
import org.apache.karaf.vineyard.common.Policy;
import org.apache.karaf.vineyard.common.RestResource;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;

@Component(service = GatewayService.class, immediate = true)
public class GatewayServiceImpl implements GatewayService {

    private CamelContext camelContext = new DefaultCamelContext();

    @Activate
    public void activate(ComponentContext context) throws Exception {
        DefaultCamelContext.class.cast(camelContext).setName("vineyard-gateway");
        camelContext.start();
        context.getBundleContext().registerService(CamelContext.class, camelContext, null);
        camelContext.addComponent("jetty", new JettyHttpComponent9());
    }

    @Deactivate
    public void deactivate() throws Exception {
        camelContext.stop();
    }

    @Override
    public void publish(API api) throws Exception {
        for (RestResource restResource : api.getRestResources()) {
            publish(api, restResource);
        }
    }

    @Override
    public void delete(API api) throws Exception {
        for (RestResource restResource : api.getRestResources()) {
            remove(api, restResource);
        }
    }

    @Override
    public void publish(API api, RestResource restResource) throws Exception {
        String routeId = getRouteId(api, restResource);
        if (camelContext.getRoute(routeId) != null) {
            throw new IllegalArgumentException("API resource already published");
        }

        final RouteDefinition definition = new RouteDefinition();
        definition.from(
                "jetty:http://0.0.0.0:9090/vineyard" + api.getContext() + restResource.getPath());
        definition.log("Processing " + api.getContext() + restResource.getPath());
        definition.routeId(routeId);

        if (restResource.getPolicies() != null) {
            TreeMap<Integer, Policy> sortedPolicies = new TreeMap<>(restResource.getPolicies());
            for (Policy policy : sortedPolicies.values()) {
                Processor processor =
                        (Processor)
                                Class.forName(policy.getClassName()).getConstructor().newInstance();
                definition.log("Adding policy " + policy.getClassName());
                definition.process(processor);
            }
        }

        if (restResource.getResponse() != null) {
            definition.log("mocking response");
            definition.transform().constant(restResource.getResponse());
        } else {
            definition.log("proxying to endpoint " + restResource.getEndpoint());
            definition.to(restResource.getEndpoint());
        }

        DefaultCamelContext.class.cast(camelContext).addRouteDefinition(definition);
    }

    @Override
    public void remove(API api, RestResource restResource) throws Exception {
        String routeId = getRouteId(api, restResource);
        if (camelContext.getRoute(routeId) == null) {
            throw new IllegalArgumentException("API resource not published");
        }
        camelContext.removeRoute(routeId);
    }

    @Override
    public void resume(API api, RestResource restResource) throws Exception {
        DefaultCamelContext.class.cast(camelContext).resumeRoute(getRouteId(api, restResource));
    }

    @Override
    public void suspend(API api, RestResource restResource) throws Exception {
        DefaultCamelContext.class.cast(camelContext).suspendRoute(getRouteId(api, restResource));
    }

    private String getRouteId(API api, RestResource resource) {
        return api.getId() + "-" + resource.getId();
    }
}
