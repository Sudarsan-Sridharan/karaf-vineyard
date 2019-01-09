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
package org.apache.karaf.vineyard.registry;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import org.apache.aries.jpa.template.JpaTemplate;
import org.apache.aries.jpa.template.TransactionType;
import org.apache.karaf.vineyard.common.API;
import org.apache.karaf.vineyard.common.Policy;
import org.apache.karaf.vineyard.common.RegistryService;
import org.apache.karaf.vineyard.common.RestResource;
import org.apache.karaf.vineyard.registry.entity.ApiEntity;
import org.apache.karaf.vineyard.registry.entity.MetaEntity;
import org.apache.karaf.vineyard.registry.entity.PolicyEntity;
import org.apache.karaf.vineyard.registry.entity.RestResourceEntity;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * Implementation of the Registry service using the JPA entity manager service (provided by Karaf).
 */
@Component(service = RegistryService.class, immediate = true)
public class RegistryServiceImpl implements RegistryService {

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
    public RestResource addRestResource(API api, RestResource restResource) {
        jpaTemplate.tx(
                TransactionType.RequiresNew,
                entityManager -> {
                    ApiEntity apiEntity = entityManager.find(ApiEntity.class, api.getId());

                    if (apiEntity != null) {
                        entityManager.persist(mapTo(restResource, apiEntity));
                        entityManager.flush();
                    }
                });
        return restResource;
    }

    @Override
    public void deleteRestResource(API api, RestResource restResource) {
        jpaTemplate.tx(
                TransactionType.RequiresNew,
                entityManager -> {
                    ApiEntity apiEntity = entityManager.find(ApiEntity.class, api.getId());
                    RestResourceEntity restResourceEntity =
                            entityManager.find(RestResourceEntity.class, restResource.getId());
                    if (apiEntity != null
                            && restResourceEntity != null
                            && restResourceEntity.getApi().getId().equals(apiEntity.getId())) {
                        entityManager.remove(restResourceEntity);
                        entityManager.flush();
                    }
                });
    }

    @Override
    public Collection<RestResource> listRestResources(API api) {
        List<RestResourceEntity> list =
                jpaTemplate.txExpr(
                        TransactionType.Supports,
                        entityManager ->
                                entityManager
                                        .createQuery(
                                                "SELECT r FROM RestResourceEntity r where r.api.id = :apiId",
                                                RestResourceEntity.class)
                                        .setParameter("apiId", api.getId())
                                        .getResultList());
        Collection<RestResource> results = new ArrayList<>();
        for (RestResourceEntity entity : list) {
            results.add(mapTo(entity));
        }
        return results;
    }

    @Override
    public Policy addPolicy(RestResource restResource, Policy policy) {
        jpaTemplate.tx(
                TransactionType.RequiresNew,
                entityManager -> {
                    RestResourceEntity restResourceEntity = entityManager.find(RestResourceEntity.class, restResource.getId());
                    if (restResourceEntity != null) {
                        entityManager.persist(mapTo(policy, restResourceEntity));
                        entityManager.flush();
                    }
                });
        return policy;
    }

    @Override
    public void deletePolicy(RestResource restResource, Policy policy) {
        jpaTemplate.tx(
                TransactionType.RequiresNew,
                entityManager -> {
                    RestResourceEntity restResourceEntity = entityManager.find(RestResourceEntity.class, restResource.getId());
                    PolicyEntity policyEntity = entityManager.find(PolicyEntity.class, policy.getId());
                    if (restResourceEntity != null
                        && policyEntity != null
                        && policyEntity.getRestResourceEntity().getId().equals(restResourceEntity.getId())) {
                        entityManager.remove(policyEntity);
                        entityManager.flush();
                    }
                });
    }

    @Override
    public Collection<Policy> listPolicies(RestResource restResource) {
        List<PolicyEntity> list =
                jpaTemplate.txExpr(
                        TransactionType.Supports,
                        entityManager -> entityManager.createQuery(
                                "SELECT p FROM PolicyEntity p WHERE p.resource.id = :resourceId", PolicyEntity.class)
                        .setParameter("resourceId", restResource.getId())
                        .getResultList());
        Collection<Policy> results = new ArrayList<>();
        for (PolicyEntity entity : list) {
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
                            MetaEntity entity = new MetaEntity();
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
                    MetaEntity entity =
                            entityManager
                                    .createQuery(
                                            "SELECT m FROM MetaEntity m where m.api.id = :apiId and m.key = :key",
                                            MetaEntity.class)
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

                    List<MetaEntity> list =
                            entityManager
                                    .createQuery(
                                            "SELECT m FROM MetaEntity m where m.api.id = :apiId and m.key = :key",
                                            MetaEntity.class)
                                    .setParameter("apiId", api.getId())
                                    .setParameter("key", api.getId())
                                    .getResultList();

                    if (apiEntity != null && !list.isEmpty()) {

                        // we clean existing metas
                        for (MetaEntity entity : list) {
                            entityManager.remove(entity);
                        }

                        // we replace by new metas
                        for (String key : meta.keySet()) {
                            MetaEntity newEntity = new MetaEntity();
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
        List<MetaEntity> list =
                jpaTemplate.txExpr(
                        TransactionType.Supports,
                        entityManager ->
                                entityManager
                                        .createQuery(
                                                "SELECT m FROM MetaEntity m where m.api.id = :apiId",
                                                MetaEntity.class)
                                        .setParameter("apiId", api.getId())
                                        .getResultList());
        Map<String, String> results = new HashMap<>();
        for (MetaEntity entity : list) {
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

    private RestResourceEntity mapTo(RestResource restResource, ApiEntity apiEntity) {
        if (restResource != null) {
            RestResourceEntity entity = new RestResourceEntity();
            entity.setId(restResource.getId());
            entity.setApi(apiEntity);
            return entity;
        } else {
            return null;
        }
    }

    private RestResource mapTo(RestResourceEntity entity) {
        if (entity != null) {
            RestResource restResource = new RestResource();
            restResource.setId(entity.getId());
            return restResource;
        } else {
            return null;
        }
    }

    private Policy mapTo(PolicyEntity entity) {
        if (entity != null) {
            Policy policy = new Policy();
            policy.setId(entity.getId());
            policy.setDescription(entity.getDescription());
            policy.setClassName(entity.getClassName());
            return policy;
        } else {
            return null;
        }
    }

    private PolicyEntity mapTo(Policy policy, RestResourceEntity restResourceEntity) {
        if (policy != null) {
            PolicyEntity entity = new PolicyEntity();
            entity.setId(policy.getId());
            entity.setDescription(policy.getDescription());
            entity.setClassName(policy.getClassName());
            entity.setRestResourceEntity(restResourceEntity);
            return entity;
        } else {
            return null;
        }
    }
}
