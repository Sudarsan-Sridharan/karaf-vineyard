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
package org.apache.karaf.vineyard.gateway.bridge;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.impl.DefaultRoute;
import org.apache.karaf.vineyard.common.API;
import org.apache.karaf.vineyard.gateway.api.GatewayService;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.util.HashMap;
import java.util.Map;

public class GatewayServiceImpl implements GatewayService {

  private DefaultCamelContext camelContext;

  /**
   * Init the gateway.
   */
  public void init() throws Exception {
    camelContext = new DefaultCamelContext();
    camelContext.setName("karaf-vineyard-gateway");
    camelContext.start();
  }

  @Override public void register(API api) throws Exception {
    camelContext.addRoutes(new RouteBuilder() {
      @Override public void configure() throws Exception {
        // TODO improve
        from(api.getEndpoint())
            .id(api.getId())
            .to(api.getEndpoint());
      }
    });
  }

  @Override public void disable(String id) throws Exception {
    DefaultRoute route = (DefaultRoute) camelContext.getRoute(id);
    if (route == null) {
      throw new IllegalStateException("Service registration not available in the gateway");
    }
    route.suspend();
  }

  @Override public void enable(String id) throws Exception {
    DefaultRoute route = (DefaultRoute) camelContext.getRoute(id);
    if (route == null) {
      throw new IllegalStateException("Service registration not available in the gateway");
    }
    if (route.isSuspended()) {
      route.resume();
    }
  }

  @Override public void remove(String id) throws Exception {
    camelContext.removeRoute(id);
  }

  @Override public String status(String id) {
    DefaultRoute route = (DefaultRoute) camelContext.getRoute(id);
    if (route == null) {
      return "N/A";
    }
    if (route.isSuspended()) {
      return "Disabled";
    }
    if (route.isStarted()) {
      return "Enabled";
    }
    return route.getStatus().toString();
  }

  @Override public Map<String, Object> metrics(String id) {
    DefaultRoute route = (DefaultRoute) camelContext.getRoute(id);
    Map<String, Object> metrics = new HashMap<>();
    if (route != null) {
      // uptime
      metrics.put("uptimeMillis", route.getUptimeMillis());
      metrics.put("uptime", route.getUptime());
      // properties
      metrics.putAll(route.getProperties());
    }
    return metrics;
  }

  @Override public void addProcessing(String id, Object processing) {
    throw new NotImplementedException();
  }

}
