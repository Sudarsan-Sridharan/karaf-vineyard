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
package org.apache.karaf.vineyard.common;

/**
 * Describe a service endpoint (actual or on the gateway).
 */
public class Endpoint {

    /** Location (URI) of the service */
    private String location;

    /** Input data format */
    private DataFormat input;

    /** Output data format */
    private DataFormat output;
    

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public DataFormat getInput() {
        return input;
    }

    public void setInput(DataFormat input) {
        this.input = input;
    }

    public DataFormat getOutput() {
        return output;
    }

    public void setOutput(DataFormat output) {
        this.output = output;
    }
}
