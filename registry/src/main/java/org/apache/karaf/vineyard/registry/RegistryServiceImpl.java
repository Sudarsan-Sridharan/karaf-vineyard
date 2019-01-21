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

import static org.apache.karaf.vineyard.registry.entity.mapper.EntityMapper.mapTo;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.apache.aries.jpa.template.JpaTemplate;
import org.apache.aries.jpa.template.TransactionType;
import org.apache.karaf.vineyard.common.API;
import org.apache.karaf.vineyard.common.Policy;
import org.apache.karaf.vineyard.common.RegistryService;
import org.apache.karaf.vineyard.common.RestResource;
import org.apache.karaf.vineyard.registry.entity.ApiEntity;
import org.apache.karaf.vineyard.registry.entity.PolicyEntity;
import org.apache.karaf.vineyard.registry.entity.PolicyRestResourceJoinEntity;
import org.apache.karaf.vineyard.registry.entity.RestResourceEntity;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * Implementation of the Registry service using the JPA entity manager service (provided by Karaf).
 */
@Component(service = RegistryService.class, immediate = true)
public class RegistryServiceImpl implements RegistryService {

    @Reference(target = "(osgi.unit.name=vineyard-registry)")
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
    public void addMeta(API api, Map<String, String> meta) {
        jpaTemplate.tx(
                TransactionType.RequiresNew,
                entityManager -> {
                    ApiEntity apiEntity = entityManager.find(ApiEntity.class, api.getId());

                    if (apiEntity != null) {
                        if (apiEntity.getMeta() == null) {
                            apiEntity.setMeta(new Hashtable<>());
                        }
                        apiEntity.getMeta().putAll(meta);
                        entityManager.merge(apiEntity);
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

                    if (apiEntity != null && apiEntity.getMeta() != null) {
                        apiEntity.getMeta().remove(key);
                        entityManager.merge(apiEntity);
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

                    if (apiEntity != null && apiEntity.getMeta() != null) {

                        apiEntity.getMeta().clear();
                        apiEntity.getMeta().putAll(meta);
                        entityManager.merge(apiEntity);
                        entityManager.flush();
                    }
                });
    }

    @Override
    public Map<String, String> getMeta(API api) {

        API apiGet = get(api.getId());
        if (apiGet != null) {
            return apiGet.getMeta();
        } else {
            return null;
        }
    }

    @Override
    public RestResource addRestResource(API api, RestResource restResource) {
        restResource.setId(UUID.randomUUID().toString());
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
    public RestResource getRestResource(String id) {
        RestResourceEntity restResourceEntity =
                jpaTemplate.txExpr(
                        TransactionType.Supports,
                        entityManager -> entityManager.find(RestResourceEntity.class, id));
        return mapTo(restResourceEntity);
    }

    @Override
    public Policy addPolicy(Policy policy) {
        policy.setId(UUID.randomUUID().toString());
        jpaTemplate.tx(
                TransactionType.RequiresNew,
                entityManager -> {
                    entityManager.persist(mapTo(policy));
                    entityManager.flush();
                });
        return policy;
    }

    @Override
    public void deletePolicy(String id) {
        jpaTemplate.tx(
                TransactionType.RequiresNew,
                entityManager -> {
                    PolicyEntity policyEntity = entityManager.find(PolicyEntity.class, id);
                    if (policyEntity != null) {
                        entityManager.remove(policyEntity);
                        entityManager.flush();
                    }
                });
    }

    @Override
    public Policy getPolicy(String id) {
        PolicyEntity policyEntity =
                jpaTemplate.txExpr(
                        TransactionType.Supports,
                        entityManager -> entityManager.find(PolicyEntity.class, id));
        return mapTo(policyEntity);
    }

    @Override
    public Collection<Policy> listPolicies() {
        List<PolicyEntity> list =
                jpaTemplate.txExpr(
                        TransactionType.Supports,
                        entityManager ->
                                entityManager
                                        .createQuery(
                                                "SELECT p FROM PolicyEntity p", PolicyEntity.class)
                                        .getResultList());
        Collection<Policy> results = new ArrayList<>();
        for (PolicyEntity entity : list) {
            results.add(mapTo(entity));
        }
        return results;
    }

    @Override
    public void applyPolicy(
            String restResourceId, String policyId, int order, Map<String, String> params) {
        jpaTemplate.tx(
                TransactionType.RequiresNew,
                entityManager -> {
                    RestResourceEntity restResourceEntity =
                            entityManager.find(RestResourceEntity.class, restResourceId);
                    PolicyEntity policyEntity = entityManager.find(PolicyEntity.class, policyId);

                    if (restResourceEntity != null && policyEntity != null) {
                        PolicyRestResourceJoinEntity join = new PolicyRestResourceJoinEntity();
                        join.setPolicy(policyEntity);
                        join.setRestResource(restResourceEntity);
                        join.setPolicyOrder(order);
                        join.setParam(new Hashtable<>());

                        if (params != null && !params.isEmpty()) {
                            params.forEach(
                                    (key, value) -> {
                                        join.getParam().put(key, value);
                                    });
                        }
                        restResourceEntity.getPolicyRestResourceJoins().add(join);
                        entityManager.merge(restResourceEntity);
                        entityManager.flush();
                    }
                });
    }

    @Override
    public void unapplyPolicy(String restResourceId, String policyId) {
        jpaTemplate.tx(
                TransactionType.RequiresNew,
                entityManager -> {
                    RestResourceEntity restResourceEntity =
                            entityManager.find(RestResourceEntity.class, restResourceId);
                    PolicyEntity policyEntity = entityManager.find(PolicyEntity.class, policyId);
                    if (restResourceEntity != null && policyEntity != null) {
                        PolicyRestResourceJoinEntity join = new PolicyRestResourceJoinEntity();
                        join.setPolicy(policyEntity);
                        join.setRestResource(restResourceEntity);
                        restResourceEntity.getPolicyRestResourceJoins().remove(join);
                        entityManager.merge(policyEntity);
                        entityManager.flush();
                    }
                });
    }

    @Override
    public Collection<Policy> listAppliedPolicies(RestResource restResource) {
        List<PolicyRestResourceJoinEntity> list =
                jpaTemplate.txExpr(
                        TransactionType.Supports,
                        entityManager ->
                                entityManager
                                        .createQuery(
                                                "SELECT p FROM PolicyRestResourceJoinEntity p WHERE p.resource.id = :resourceId",
                                                PolicyRestResourceJoinEntity.class)
                                        .setParameter("resourceId", restResource.getId())
                                        .getResultList());

        Collection<Policy> results = new ArrayList<>();
        list.forEach(
                policyRestResourceJoinEntity -> {
                    ((ArrayList<Policy>) results).add(mapTo(policyRestResourceJoinEntity));
                });
        return results;
    }

    @Override
    public void addPolicyMeta(Policy policy, Map<String, String> meta) {
        jpaTemplate.tx(
                TransactionType.RequiresNew,
                entityManager -> {
                    PolicyEntity policyEntity =
                            entityManager.find(PolicyEntity.class, policy.getId());

                    if (policyEntity != null) {
                        if (policyEntity.getMeta() == null) {
                            policyEntity.setMeta(new Hashtable<>());
                        }
                        policyEntity.getMeta().putAll(meta);
                        entityManager.merge(policyEntity);
                        entityManager.flush();
                    }
                });
    }

    @Override
    public void deletePolicyMeta(Policy policy, String key) {
        jpaTemplate.tx(
                TransactionType.RequiresNew,
                entityManager -> {
                    PolicyEntity policyEntity =
                            entityManager.find(PolicyEntity.class, policy.getId());

                    if (policyEntity != null && policyEntity.getMeta() != null) {
                        policyEntity.getMeta().remove(key);
                        entityManager.merge(policyEntity);
                        entityManager.flush();
                    }
                });
    }

    @Override
    public void updatePolicyMeta(Policy policy, Map<String, String> meta) {
        jpaTemplate.tx(
                TransactionType.RequiresNew,
                entityManager -> {
                    PolicyEntity policyEntity =
                            entityManager.find(PolicyEntity.class, policy.getId());

                    if (policyEntity != null) {
                        policyEntity.getMeta().clear();
                        policyEntity.getMeta().putAll(meta);
                        entityManager.merge(policyEntity);
                        entityManager.flush();
                    }
                });
    }

    @Override
    public Map<String, String> getPolicyMeta(Policy policy) {

        Policy policyGet = getPolicy(policy.getId());
        if (policyGet != null) {
            return policyGet.getMeta();
        } else {
            return null;
        }
    }
}
