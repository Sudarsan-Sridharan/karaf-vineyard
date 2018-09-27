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

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.ws.rs.HttpMethod;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

public class RegistryRestTest {

    @Test
    @Ignore
    public void testCreateData() throws Exception {

        String dataformatId = "";
        String apiId = "";

        // DATA_FORMAT
        try {
            String URL = "http://localhost:8181/cxf/vineyard/registry/dataformat";
            java.net.URL urlDataformat = new URL(URL);

            // Call add dataformat service
            String jsonAddDataformat = "{\n" +
                    "  \"name\": \"json\",\n" +
                    "  \"schema\": \"json\",\n" +
                    "  \"sample\": \"json-sample\"\n" +
                    "}";
            System.out.println("Call POST " + URL);
            HttpURLConnection connection = (HttpURLConnection) urlDataformat.openConnection();
            connection.setRequestMethod(HttpMethod.POST);
            connection.setDoOutput(true);
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            OutputStream os = connection.getOutputStream();
            os.write(jsonAddDataformat.getBytes());
            os.flush();

            if (connection.getResponseCode() == HttpURLConnection.HTTP_CREATED) {
                Assert.assertTrue(true);
                String location = connection.getHeaderField("Location");
                System.out.println("Location created: " + location);

                urlDataformat = new URL(location);
                connection = (HttpURLConnection) urlDataformat.openConnection();
                connection.setRequestMethod(HttpMethod.GET);
                connection.connect();

                StringBuffer sb = new StringBuffer();
                if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                    BufferedReader buffer = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    String line;

                    while ((line = buffer.readLine()) != null) {
                        sb.append(line);
                    }
                    if (sb.length() == 0) {
                        System.out.println("Dataformat not found");
                        Assert.assertTrue(false);
                    } else {
                        System.out.println(sb.toString());
                        Assert.assertTrue(true);
                    }
                } else {
                    System.out.println("Error when sending GET method : HTTP_CODE = " + connection.getResponseCode());
                    Assert.assertTrue(false);
                }
                connection.disconnect();

                if (sb.length() != 0) {
                    JsonReader reader = Json.createReader(new StringReader(sb.toString()));
                    JsonObject jsonObject = reader.readObject();
                    dataformatId = jsonObject.getString("id");
                    System.out.println("Dataformat id = " + dataformatId);
                }
            } else {
                System.out.println("Error when sending POST method : HTTP_CODE = " + connection.getResponseCode());
                Assert.assertTrue(false);
            }

            connection.disconnect();

        } catch (Exception e) {
            System.out.println(e.getMessage());
            Assert.assertTrue(false);
        }

        // API
        try {
            String URL = "http://localhost:8181/cxf/vineyard/registry/api";
            URL urlApi= new URL(URL);

            // Call add rest-api
            String jsonAddRestApi = "{\n" +
                    "  \"name\": \"authenticate service\",\n" +
                    "  \"context\": \"api/authenticate\",\n" +
                    "  \"description\": \"use to authenticate user with token\",\n" +
                    "  \"version\": \"1.0.0\"" +
                    "}";
            System.out.println("Call POST " + URL);
            HttpURLConnection connection = (HttpURLConnection) urlApi.openConnection();
            connection.setRequestMethod(HttpMethod.POST);
            connection.setDoOutput(true);
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            OutputStream os = connection.getOutputStream();
            os.write(jsonAddRestApi.getBytes());
            os.flush();

            if (connection.getResponseCode() == HttpURLConnection.HTTP_CREATED) {
                Assert.assertTrue(true);
                String location = connection.getHeaderField("Location");
                System.out.println("Location created: " + location);

                urlApi= new URL(location);
                connection = (HttpURLConnection) urlApi.openConnection();
                connection.setRequestMethod(HttpMethod.GET);
                connection.connect();

                StringBuffer sb = new StringBuffer();

                if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                    BufferedReader buffer = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    String line;

                    while ((line = buffer.readLine()) != null) {
                        sb.append(line);
                    }
                    if (sb.length() == 0) {
                        System.out.println("api not found");
                        Assert.assertTrue(false);
                    } else {
                        System.out.println(sb.toString());
                        Assert.assertTrue(true);
                    }
                } else {
                    System.out.println("Error when sending GET method : HTTP_CODE = " + connection.getResponseCode());
                    Assert.assertTrue(false);
                }
                connection.disconnect();

                if (sb.length() != 0) {
                    JsonReader reader = Json.createReader(new StringReader(sb.toString()));
                    JsonObject jsonObject = reader.readObject();
                    apiId = jsonObject.getString("id");
                    System.out.println("API id = " + apiId);
                }

            } else {
                System.out.println("Error when sending POST method : HTTP_CODE = " + connection.getResponseCode());
                Assert.assertTrue(false);
            }

            connection.disconnect();

        } catch (Exception e) {
            System.out.println(e.getMessage());
            Assert.assertTrue(false);
        }

        // RESOURCES
        try {
            String URL = "http://localhost:8181/cxf/vineyard/registry/api/" + apiId + "/resource";
            URL urlResource= new URL(URL);

            // Call add rest-api
            String jsonAddRestResource = "{\n" +
                    "  \"path\": \"/token\",\n" +
                    "  \"method\": \"GET\",\n" +
                    "  \"inFormat\": {\"id\": \"" + dataformatId + "\"},\n" +
                    "  \"outFormat\": {\"id\": \"" + dataformatId + "\"},\n" +
                    "  \"useBridge\": \"false\",\n" +
                    "  \"response\": \"response\",\n" +
                    "  \"bridge\": \"bridge\"" +
                    "}";
            System.out.println("Call POST " + URL);
            HttpURLConnection connection = (HttpURLConnection) urlResource.openConnection();
            connection.setRequestMethod(HttpMethod.POST);
            connection.setDoOutput(true);
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            OutputStream os = connection.getOutputStream();
            os.write(jsonAddRestResource.getBytes());
            os.flush();

            if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                Assert.assertTrue(true);

                urlResource= new URL(URL);
                connection = (HttpURLConnection) urlResource.openConnection();
                connection.setRequestMethod(HttpMethod.GET);
                connection.connect();

                StringBuffer sb = new StringBuffer();

                if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                    BufferedReader buffer = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    String line;

                    while ((line = buffer.readLine()) != null) {
                        sb.append(line);
                    }
                    if (sb.length() == 0) {
                        System.out.println("resource not found");
                        Assert.assertTrue(false);
                    } else {
                        System.out.println(sb.toString());
                        Assert.assertTrue(true);
                    }
                } else {
                    System.out.println("Error when sending GET method : HTTP_CODE = " + connection.getResponseCode());
                    Assert.assertTrue(false);
                }
                connection.disconnect();

            } else {
                System.out.println("Error when sending POST method : HTTP_CODE = " + connection.getResponseCode());
                Assert.assertTrue(false);
            }

            connection.disconnect();

        } catch (Exception e) {
            System.out.println(e.getMessage());
            Assert.assertTrue(false);
        }


        // METADATA
        try {
            String URL = "http://localhost:8181/cxf/vineyard/registry/api/" + apiId + "/metadata";
            URL urlResource= new URL(URL);

            // Call add rest-api
            String jsonAddRestMetadata = "{\n" +
                    "  \"documentation\": \"Swagger\",\n" +
                    "  \"manual\": \"pdf\"" +
                    "}";
            System.out.println("Call POST " + URL);
            HttpURLConnection connection = (HttpURLConnection) urlResource.openConnection();
            connection.setRequestMethod(HttpMethod.POST);
            connection.setDoOutput(true);
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            OutputStream os = connection.getOutputStream();
            os.write(jsonAddRestMetadata.getBytes());
            os.flush();

            if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                Assert.assertTrue(true);

                urlResource= new URL(URL);
                connection = (HttpURLConnection) urlResource.openConnection();
                connection.setRequestMethod(HttpMethod.GET);
                connection.connect();

                StringBuffer sb = new StringBuffer();

                if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                    BufferedReader buffer = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    String line;

                    while ((line = buffer.readLine()) != null) {
                        sb.append(line);
                    }
                    if (sb.length() == 0) {
                        System.out.println("resource not found");
                        Assert.assertTrue(false);
                    } else {
                        System.out.println(sb.toString());
                        Assert.assertTrue(true);
                    }
                } else {
                    System.out.println("Error when sending GET method : HTTP_CODE = " + connection.getResponseCode());
                    Assert.assertTrue(false);
                }
                connection.disconnect();

            } else {
                System.out.println("Error when sending POST method : HTTP_CODE = " + connection.getResponseCode());
                Assert.assertTrue(false);
            }

            connection.disconnect();

        } catch (Exception e) {
            System.out.println(e.getMessage());
            Assert.assertTrue(false);
        }
    }

}
