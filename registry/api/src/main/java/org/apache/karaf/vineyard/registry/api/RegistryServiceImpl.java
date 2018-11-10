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
package org.apache.karaf.vineyard.registry.api;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import org.apache.aries.jpa.template.JpaTemplate;
import org.apache.aries.jpa.template.TransactionType;
import org.apache.karaf.vineyard.common.API;
import org.apache.karaf.vineyard.common.ApiRegistryService;
import org.apache.karaf.vineyard.common.Resource;
import org.apache.karaf.vineyard.common.ResourceType;
import org.apache.karaf.vineyard.registry.api.entity.ApiEntity;
import org.apache.karaf.vineyard.registry.api.entity.ApiMetaEntity;
import org.apache.karaf.vineyard.registry.api.entity.ApiResourceEntity;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * Implementation of the Registry service using the JPA entity manager service (provided by Karaf).
 */
@Component(service = ApiRegistryService.class, immediate = true)
public class RegistryServiceImpl implements ApiRegistryService {

    @Reference(target = "(osgi.unit.name=vineyard-registry-api)")
    private JpaTemplate jpaTemplate;

    @Override
    public API add(API api) {
        api.setId(UUID.randomUUID().toString());
        jpaTemplate.tx(
                TransactionType.RequiresNew,
                entityManager -> {
                    entityManager.persist(mapTo(api));
                    entityManager.flush();
                });
        return api;
    }

    @Override
    public void definition(API api, InputStream inputStream) {
        jpaTemplate.tx(
                TransactionType.RequiresNew,
                entityManager -> {
                    ApiEntity apiEntity = entityManager.find(ApiEntity.class, api.getId());

                    if (apiEntity != null) {
                        byte[] buffer = new byte[1024];
                        try (ByteArrayOutputStream output = new ByteArrayOutputStream()) {
                            int cpt = 0;
                            while ((cpt = inputStream.read(buffer)) > -1) {
                                output.write(buffer, 0, cpt);
                            }
                            apiEntity.setDefinition(output.toByteArray());
                        } catch (IOException e) {
                            // do nothing
                        }
                        entityManager.merge(apiEntity);
                        entityManager.flush();
                    }
                });
    }

    @Override
    public void delete(String id) {
        jpaTemplate.tx(
                TransactionType.RequiresNew,
                entityManager -> {
                    ApiEntity apiEntity = entityManager.find(ApiEntity.class, id);
                    if (apiEntity != null) {
                        entityManager.remove(apiEntity);
                        entityManager.flush();
                    }
                });
    }

    @Override
    public void update(API api) {
        jpaTemplate.tx(
                TransactionType.RequiresNew,
                entityManager -> {
                    ApiEntity apiEntity = entityManager.find(ApiEntity.class, api.getId());

                    if (apiEntity != null) {
                        // we don't update the PK or the Resources and Metadatas
                        apiEntity.setName(api.getName());
                        apiEntity.setContext(api.getContext());
                        apiEntity.setDescription(api.getDescription());
                        entityManager.merge(apiEntity);
                        entityManager.flush();
                    }
                });
    }

    @Override
    public API get(String id) {
        ApiEntity apiEntity =
                jpaTemplate.txExpr(
                        TransactionType.Supports,
                        entityManager -> entityManager.find(ApiEntity.class, id));
        return mapTo(apiEntity);
    }

    @Override
    public Collection<API> list() {
        List<ApiEntity> list =
                jpaTemplate.txExpr(
                        TransactionType.Supports,
                        entityManager ->
                                entityManager
                                        .createQuery("SELECT a FROM ApiEntity a", ApiEntity.class)
                                        .getResultList());
        Collection<API> results = new ArrayList<>();
        for (ApiEntity entity : list) {
            results.add(mapTo(entity));
        }
        return results;
    }

    @Override
    public Resource addResource(API api, Resource resource) {
        resource.setId(UUID.randomUUID().toString());
        jpaTemplate.tx(
                TransactionType.RequiresNew,
                entityManager -> {
                    ApiEntity apiEntity = entityManager.find(ApiEntity.class, api.getId());

                    if (apiEntity != null) {
                        entityManager.persist(mapTo(resource, apiEntity));
                        entityManager.flush();
                    }
                });
        return resource;
    }

    @Override
    public void deleteResource(API api, Resource resource) {
        jpaTemplate.tx(
                TransactionType.RequiresNew,
                entityManager -> {
                    ApiEntity apiEntity = entityManager.find(ApiEntity.class, api.getId());
                    ApiResourceEntity apiResourceEntity =
                            entityManager.find(ApiResourceEntity.class, resource.getId());
                    if (apiEntity != null
                            && apiResourceEntity != null
                            && apiResourceEntity.getApi().getId().equals(apiEntity.getId())) {
                        entityManager.remove(apiResourceEntity);
                        entityManager.flush();
                    }
                });
    }

    @Override
    public Collection<Resource> listResources(API api) {
        List<ApiResourceEntity> list =
                jpaTemplate.txExpr(
                        TransactionType.Supports,
                        entityManager ->
                                entityManager
                                        .createQuery(
                                                "SELECT r FROM ApiResourceEntity r where r.api.id = :apiId",
                                                ApiResourceEntity.class)
                                        .setParameter("apiId", api.getId())
                                        .getResultList());
        Collection<Resource> results = new ArrayList<>();
        for (ApiResourceEntity entity : list) {
            results.add(mapTo(entity));
        }
        return results;
    }

    @Override
    public void addMeta(API api, Map<String, String> meta) {
        jpaTemplate.tx(
                TransactionType.RequiresNew,
                entityManager -> {
                    ApiEntity apiEntity = entityManager.find(ApiEntity.class, api.getId());

                    if (apiEntity != null) {
                        for (String key : meta.keySet()) {
                            ApiMetaEntity entity = new ApiMetaEntity();
                            entity.setApi(apiEntity);
                            entity.setKey(key);
                            entity.setValue(meta.get(key));
                            entityManager.persist(entity);
                        }
                        entityManager.flush();
                    }
                });
    }

    @Override
    public void deleteMeta(API api, String key) {
        jpaTemplate.tx(
                TransactionType.RequiresNew,
                entityManager -> {
                    ApiEntity apiEntity = entityManager.find(ApiEntity.class, api.getId());
                    ApiMetaEntity entity =
                            entityManager
                                    .createQuery(
                                            "SELECT m FROM ApiMetaEntity m where m.api.id = :apiId and m.key = :key",
                                            ApiMetaEntity.class)
                                    .setParameter("apiId", api.getId())
                                    .setParameter("key", api.getId())
                                    .getSingleResult();

                    if (apiEntity != null
                            && entity != null
                            && entity.getApi().getId().equals(apiEntity.getId())) {
                        entityManager.remove(entity);
                        entityManager.flush();
                    }
                });
    }

    @Override
    public void updateMeta(API api, Map<String, String> meta) {
        jpaTemplate.tx(
                TransactionType.RequiresNew,
                entityManager -> {
                    ApiEntity apiEntity = entityManager.find(ApiEntity.class, api.getId());

                    List<ApiMetaEntity> list =
                            entityManager
                                    .createQuery(
                                            "SELECT m FROM ApiMetaEntity m where m.api.id = :apiId and m.key = :key",
                                            ApiMetaEntity.class)
                                    .setParameter("apiId", api.getId())
                                    .setParameter("key", api.getId())
                                    .getResultList();

                    if (apiEntity != null && !list.isEmpty()) {

                        // we clean existing metas
                        for (ApiMetaEntity entity : list) {
                            entityManager.remove(entity);
                        }

                        // we replace by new metas
                        for (String key : meta.keySet()) {
                            ApiMetaEntity newEntity = new ApiMetaEntity();
                            newEntity.setApi(apiEntity);
                            newEntity.setKey(key);
                            newEntity.setValue(meta.get(key));
                            entityManager.persist(newEntity);
                        }
                        entityManager.flush();
                    }
                });
    }

    @Override
    public Map<String, String> getMeta(API api) {
        List<ApiMetaEntity> list =
                jpaTemplate.txExpr(
                        TransactionType.Supports,
                        entityManager ->
                                entityManager
                                        .createQuery(
                                                "SELECT m FROM ApiMetaEntity m where m.api.id = :apiId",
                                                ApiMetaEntity.class)
                                        .setParameter("apiId", api.getId())
                                        .getResultList());
        Map<String, String> results = new HashMap<>();
        for (ApiMetaEntity entity : list) {
            results.put(entity.getKey(), entity.getValue());
        }
        return results;
    }

    private API mapTo(ApiEntity entity) {
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

    private ApiEntity mapTo(API api) {
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

    private ApiResourceEntity mapTo(Resource resource, ApiEntity apiEntity) {
        if (resource != null) {
            ApiResourceEntity entity = new ApiResourceEntity();
            entity.setId(resource.getId());
            entity.setType(resource.getType().name());
            entity.setApi(apiEntity);
            return entity;
        } else {
            return null;
        }
    }

    private Resource mapTo(ApiResourceEntity entity) {
        if (entity != null) {
            Resource resource = new Resource();
            resource.setId(entity.getId());
            resource.setType(ResourceType.valueOf(entity.getType()));
            return resource;
        } else {
            return null;
        }
    }
}
