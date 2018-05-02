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

import java.util.Map;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.karaf.vineyard.common.Registration;
import org.apache.karaf.vineyard.gateway.api.GatewayService;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

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

  @Override public void register(Registration registration) {

  }

  @Override public void disable(String id) {

  }

  @Override public void enable(String id) {

  }

  @Override public void remove(String id) {

  }

  @Override public String status(String id) {
    return null;
  }

  @Override public Map<String, Object> metrics(String id) {
    return null;
  }

  @Override public void addProcessing(String id, Object processing) {
    throw new NotImplementedException();
  }

}
