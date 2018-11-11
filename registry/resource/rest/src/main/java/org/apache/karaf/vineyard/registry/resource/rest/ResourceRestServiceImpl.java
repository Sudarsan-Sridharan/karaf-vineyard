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
package org.apache.karaf.vineyard.registry.resource.rest;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import org.apache.aries.jpa.template.JpaTemplate;
import org.apache.aries.jpa.template.TransactionType;
import org.apache.karaf.vineyard.common.ResourceRegistryService;
import org.apache.karaf.vineyard.common.RestResource;
import org.apache.karaf.vineyard.registry.resource.rest.entity.RestPolicyEntity;
import org.apache.karaf.vineyard.registry.resource.rest.entity.RestResourceEntity;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * Implementation of the Rest Resource service using the JPA entity manager service (provided by
 * Karaf).
 */
@Component(service = ResourceRegistryService.class, immediate = true)
public class ResourceRestServiceImpl implements ResourceRegistryService {

    @Reference(target = "(osgi.unit.name=vineyard-registry-resource-rest)")
    private JpaTemplate jpaTemplate;

    @Override
    public Object add(Object resource) throws Exception {
        RestResource res = (RestResource) resource;
        res.setId(UUID.randomUUID().toString());
        jpaTemplate.tx(
                TransactionType.RequiresNew,
                entityManager -> {
                    entityManager.persist(mapTo(res));
                    entityManager.flush();
                });
        return res;
    }

    @Override
    public void delete(String id) {
        jpaTemplate.tx(
                TransactionType.RequiresNew,
                entityManager -> {
                    RestResourceEntity entity = entityManager.find(RestResourceEntity.class, id);
                    if (entity != null) {
                        entityManager.remove(entity);
                        entityManager.flush();
                    }
                });
    }

    @Override
    public void update(Object resource) {
        RestResource res = (RestResource) resource;
        jpaTemplate.tx(
                TransactionType.RequiresNew,
                entityManager -> {
                    RestResourceEntity entity =
                            entityManager.find(RestResourceEntity.class, res.getId());

                    if (entity != null) {
                        // we don't update the PK or the Metadatas
                        entity.setAccept(res.getAccept());
                        entity.setDescription(res.getDescription());
                        entity.setEndpoint(res.getEndpoint());
                        entity.setMediaType(res.getMediaType());
                        entity.setMethod(res.getMethod());
                        entity.setPath(res.getPath());
                        entity.setResponse(res.getResponse());
                        entity.setVersion(res.getVersion());
                        entityManager.merge(entity);
                        entityManager.flush();
                    }
                });
    }

    @Override
    public RestResource get(String id) {
        RestResourceEntity entity =
                jpaTemplate.txExpr(
                        TransactionType.Supports,
                        entityManager -> entityManager.find(RestResourceEntity.class, id));
        return mapTo(entity);
    }

    @Override
    public Collection list() {
        List<RestResourceEntity> list =
                jpaTemplate.txExpr(
                        TransactionType.Supports,
                        entityManager ->
                                entityManager
                                        .createQuery(
                                                "SELECT r FROM RestResourceEntity r",
                                                RestResourceEntity.class)
                                        .getResultList());
        Collection<RestResource> results = new ArrayList<>();
        for (RestResourceEntity entity : list) {
            results.add(mapTo(entity));
        }
        return results;
    }

    @Override
    public void addPolicy(String idResource, String idPolicy, Integer policyOrder) {
        jpaTemplate.tx(
                TransactionType.RequiresNew,
                entityManager -> {
                    RestResourceEntity entity =
                            entityManager.find(RestResourceEntity.class, idResource);
                    if (entity != null) {
                        RestPolicyEntity policyEntity = new RestPolicyEntity();
                        policyEntity.setId(idPolicy);
                        policyEntity.setOrdering(policyOrder);
                        policyEntity.setResource(entity);
                        entityManager.persist(policyEntity);
                        entityManager.flush();
                    }
                });
    }

    @Override
    public void removePolicy(String idResource, String idPolicy) {
        jpaTemplate.tx(
                TransactionType.RequiresNew,
                entityManager -> {
                    RestPolicyEntity policyEntity =
                            entityManager
                                    .createQuery(
                                            "SELECT p FROM RestPolicyEntity p WHERE p.id = :idPolicy AND p.resource.id = :idResource",
                                            RestPolicyEntity.class)
                                    .setParameter("idPolicy", idPolicy)
                                    .setParameter("idResource", idResource)
                                    .getSingleResult();

                    if (policyEntity != null) {
                        entityManager.remove(policyEntity);
                        entityManager.flush();
                    }
                });
    }

    @Override
    public Collection<String> listPolicies(String idResource) {
        List<RestPolicyEntity> list =
                jpaTemplate.txExpr(
                        TransactionType.Supports,
                        entityManager ->
                                entityManager
                                        .createQuery(
                                                "SELECT p FROM RestPolicyEntity p WHERE p.resource.id = :idResource",
                                                RestPolicyEntity.class)
                                        .setParameter("idResource", idResource)
                                        .getResultList());
        Collection<String> results = new ArrayList<>();
        for (RestPolicyEntity entity : list) {
            results.add(entity.getId());
        }
        return results;
    }

    private RestResource mapTo(RestResourceEntity entity) {
        if (entity != null) {
            RestResource resource = new RestResource();
            resource.setId(entity.getId());
            resource.setAccept(entity.getAccept());
            resource.setDescription(entity.getDescription());
            resource.setEndpoint(entity.getEndpoint());
            resource.setMediaType(entity.getMediaType());
            resource.setMethod(entity.getMethod());
            resource.setPath(entity.getPath());
            resource.setResponse(entity.getResponse());
            resource.setVersion(entity.getVersion());
            return resource;
        } else {
            return null;
        }
    }

    private RestResourceEntity mapTo(RestResource resource) {
        if (resource != null) {
            RestResourceEntity entity = new RestResourceEntity();
            entity.setId(resource.getId());
            entity.setAccept(resource.getAccept());
            entity.setDescription(resource.getDescription());
            entity.setEndpoint(resource.getEndpoint());
            entity.setMediaType(resource.getMediaType());
            entity.setMethod(resource.getMethod());
            entity.setPath(resource.getPath());
            entity.setResponse(resource.getResponse());
            entity.setVersion(resource.getVersion());
            return entity;
        } else {
            return null;
        }
    }
}
