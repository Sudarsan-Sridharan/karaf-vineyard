/*
 *  Licensed to the Apache Software Foundation (ASF) under one or more
 *  contributor license agreements.  See the NOTICE file distributed with
 *  this work for additional information regarding copyright ownership.
 *  The ASF licenses this file to You under the Apache License, Version 2.0
 *  (the "License"); you may not use this file except in compliance with
 *  the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.apache.karaf.vineyard.registry.storage.entity;

import java.io.Serializable;

/**
 * A regular JPA entity, using JPA annotations.
 */
public class ResourcePkEntity implements Serializable {

    private String path;

    private String api;

    public String getPath() {
        return this.path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getApi() {
        return api;
    }

    public void setApi(String api) {
        this.api = api;
    }

    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (!(obj instanceof ResourcePkEntity)) return false;
        ResourcePkEntity pk = (ResourcePkEntity) obj;
        return pk.path.equals(this.path) && pk.api.equals(this.api);
    }

}