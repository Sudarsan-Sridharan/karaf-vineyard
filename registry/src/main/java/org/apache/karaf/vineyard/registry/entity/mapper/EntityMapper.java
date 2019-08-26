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
package org.apache.karaf.vineyard.registry.entity.mapper;

import java.util.HashMap;
import org.apache.karaf.vineyard.common.API;
import org.apache.karaf.vineyard.common.Policy;
import org.apache.karaf.vineyard.common.RestResource;
import org.apache.karaf.vineyard.registry.entity.ApiEntity;
import org.apache.karaf.vineyard.registry.entity.PolicyEntity;
import org.apache.karaf.vineyard.registry.entity.PolicyRestResourceJoinEntity;
import org.apache.karaf.vineyard.registry.entity.RestResourceEntity;

/** Mapper of the JPA Entity from/to common POJO. */
public class EntityMapper {

    private EntityMapper() {}

    public static API mapTo(ApiEntity entity) {
        if (entity != null) {
            API api = new API();
            api.setId(entity.getId());
            api.setContext(entity.getContext());
            api.setDescription(entity.getDescription());
            api.setName(entity.getName());
            return api;
        } else {
            return null;
        }
    }

    public static ApiEntity mapTo(API api) {
        if (api != null) {
            ApiEntity entity = new ApiEntity();
            entity.setId(api.getId());
            entity.setContext(api.getContext());
            entity.setDescription(api.getDescription());
            entity.setName(api.getName());
            return entity;
        } else {
            return null;
        }
    }

    public static RestResourceEntity mapTo(RestResource restResource, ApiEntity apiEntity) {
        if (restResource != null) {
            RestResourceEntity entity = new RestResourceEntity();
            entity.setId(restResource.getId());
            entity.setAccept(restResource.getAccept());
            entity.setDescription(restResource.getDescription());
            entity.setEndpoint(restResource.getEndpoint());
            entity.setMediaType(restResource.getMediaType());
            entity.setMethod(restResource.getMethod());
            entity.setPath(restResource.getPath());
            entity.setResponse(restResource.getResponse());
            entity.setVersion(restResource.getVersion());
            entity.setApi(apiEntity);
            return entity;
        } else {
            return null;
        }
    }

    public static RestResource mapTo(RestResourceEntity entity) {
        if (entity != null) {
            RestResource restResource = new RestResource();
            restResource.setId(entity.getId());
            restResource.setAccept(entity.getAccept());
            restResource.setDescription(entity.getDescription());
            restResource.setEndpoint(entity.getEndpoint());
            restResource.setMediaType(entity.getMediaType());
            restResource.setMethod(entity.getMethod());
            restResource.setPath(entity.getPath());
            restResource.setResponse(entity.getResponse());
            restResource.setVersion(entity.getVersion());
            return restResource;
        } else {
            return null;
        }
    }

    public static Policy mapTo(PolicyEntity entity) {
        if (entity != null) {
            Policy policy = new Policy();
            policy.setId(entity.getId());
            policy.setDescription(entity.getDescription());
            policy.setClassName(entity.getClassName());
            policy.setMeta(new HashMap<>());
            entity.getMeta().forEach((key, value) -> policy.getMeta().put(key, value));
            return policy;
        } else {
            return null;
        }
    }

    public static PolicyEntity mapTo(Policy policy) {
        if (policy != null) {
            PolicyEntity entity = new PolicyEntity();
            entity.setId(policy.getId());
            entity.setDescription(policy.getDescription());
            entity.setClassName(policy.getClassName());
            entity.setMeta(new HashMap<>());
            policy.getMeta().forEach((key, value) -> entity.getMeta().put(key, value));
            return entity;
        } else {
            return null;
        }
    }

    public static Policy mapTo(PolicyRestResourceJoinEntity entity) {
        if (entity != null) {
            Policy policy = mapTo(entity.getPolicy());
            policy.setParam(new HashMap<>());
            entity.getParam().forEach((key, value) -> policy.getParam().put(key, value));
            return policy;
        } else {
            return null;
        }
    }
}
