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

import org.apache.camel.CamelContext;
import org.apache.camel.ExchangePattern;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.karaf.vineyard.common.API;
import org.apache.karaf.vineyard.common.Resource;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;

public class ApiServlet extends HttpServlet {

    private API api;
    private CamelContext camelContext;

    public ApiServlet(API api) {
        this.api = api;
        this.camelContext = new DefaultCamelContext();
    }

    private Resource getResource(HttpServletRequest request) {
        String path = request.getPathInfo();
        for (Resource resource : api.getResources()) {
            if (resource.getPath().equals(path)) {
                return  resource;
            }
        }
        return null;
    }

    private void doService(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Resource resource = getResource(request);

        if (resource == null) {
            throw new IOException("Resource not defined for path " + request.getPathInfo());
        }

        if (!request.getMethod().equalsIgnoreCase(resource.getMethod())) {
            throw new IOException("Method " + request.getMethod() + " is not defined for resource " + request.getPathInfo());
        }

        ProducerTemplate producerTemplate = camelContext.createProducerTemplate();
        if (resource.isUseBridge()) {
            StringBuilder builder = new StringBuilder();
            try (BufferedReader reader = request.getReader()) {
                String line;
                while ((line = reader.readLine()) != null) {
                    builder.append(line).append("\n");
                }
            }
            Object camelResponse = producerTemplate.sendBody(resource.getBridge(), ExchangePattern.InOut, builder.toString());
            try (PrintWriter writer = response.getWriter()) {
                writer.println(camelResponse);
            }
        } else {
            // return the response for mocking
            try (PrintWriter writer = response.getWriter()) {
                writer.println(resource.getResponse());
            }
        }
    }

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        doService(request, response);
    }

    @Override
    public void doPut(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        doService(request, response);
    }

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        doService(request, response);
    }

    @Override
    public void doDelete(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        doService(request, response);
    }

}
