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
package org.apache.karaf.vineyard.registry.policy;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.apache.aries.jpa.template.JpaTemplate;
import org.apache.aries.jpa.template.TransactionType;
import org.apache.karaf.vineyard.common.Policy;
import org.apache.karaf.vineyard.common.PolicyRegistryService;
import org.apache.karaf.vineyard.registry.policy.entity.PolicyEntity;
import org.apache.karaf.vineyard.registry.policy.entity.PolicyMetaEntity;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * Implementation of the Policy service using the JPA entity manager service (provided by Karaf).
 */
@Component(service = PolicyRegistryService.class, immediate = true)
public class PolicyServiceImpl implements PolicyRegistryService {

    @Reference(target = "(osgi.unit.name=vineyard-registry-policy)")
    private JpaTemplate jpaTemplate;

    @Override
    public Policy add(Policy policy) {
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
    public void delete(String id) {
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
    public void update(Policy policy) {
        jpaTemplate.tx(
                TransactionType.RequiresNew,
                entityManager -> {
                    PolicyEntity policyEntity =
                            entityManager.find(PolicyEntity.class, policy.getId());

                    if (policyEntity != null) {
                        // we don't update the PK or the Metadatas
                        policyEntity.setClassName(policy.getClassName());
                        policyEntity.setDescription(policy.getDescription());
                        entityManager.merge(policyEntity);
                        entityManager.flush();
                    }
                });
    }

    @Override
    public Policy get(String id) {
        PolicyEntity policyEntity =
                jpaTemplate.txExpr(
                        TransactionType.Supports,
                        entityManager -> entityManager.find(PolicyEntity.class, id));
        return mapTo(policyEntity);
    }

    @Override
    public void addMeta(Policy policy, Map<String, String> meta) {
        PolicyEntity policyEntity =
                jpaTemplate.txExpr(
                        TransactionType.Supports,
                        entityManager -> entityManager.find(PolicyEntity.class, policy.getId()));

        jpaTemplate.tx(
                TransactionType.RequiresNew,
                entityManager -> {
                    Collection<PolicyMetaEntity> metas = mapTo(policyEntity, meta);
                    if (metas != null) {
                        for (PolicyMetaEntity metaEntity : metas) {
                            entityManager.persist(metaEntity);
                        }
                        entityManager.flush();
                    }
                });
    }

    @Override
    public void deleteMeta(Policy policy, String key) {
        jpaTemplate.tx(
                TransactionType.RequiresNew,
                entityManager -> {
                    Collection<PolicyMetaEntity> metaEntities =
                            entityManager
                                    .createQuery(
                                            "SELECT p FROM PolicyMetaEntity p where p.key = :key, p.policy.id = :policyId",
                                            PolicyMetaEntity.class)
                                    .setParameter("key", key)
                                    .setParameter("policyId", policy.getId())
                                    .getResultList();

                    if (metaEntities != null && metaEntities.size() == 1) {
                        entityManager.remove(metaEntities.iterator().next());
                        entityManager.flush();
                    }
                });
    }

    @Override
    public void updateMeta(Policy policy, Map<String, String> meta) {
        for (String key : meta.keySet()) {
            jpaTemplate.tx(
                    TransactionType.RequiresNew,
                    entityManager -> {
                        Collection<PolicyMetaEntity> metaEntities =
                                entityManager
                                        .createQuery(
                                                "SELECT p FROM PolicyMetaEntity p where p.key = :key, p.policy.id = :policyId",
                                                PolicyMetaEntity.class)
                                        .setParameter("key", key)
                                        .setParameter("policyId", policy.getId())
                                        .getResultList();

                        if (metaEntities != null && metaEntities.size() == 1) {
                            PolicyMetaEntity metaEntity = metaEntities.iterator().next();
                            metaEntity.setValue(meta.get(key));
                            entityManager.merge(metaEntity);
                            entityManager.flush();
                        }
                    });
        }
    }

    @Override
    public Map<String, String> getMeta(Policy policy) {
        Collection<PolicyMetaEntity> metaEntities =
                jpaTemplate.txExpr(
                        TransactionType.Supports,
                        entityManager ->
                                entityManager
                                        .createQuery(
                                                "SELECT p FROM PolicyMetaEntity p where p.policy.id = :policyId",
                                                PolicyMetaEntity.class)
                                        .setParameter("policyId", policy.getId())
                                        .getResultList());

        return mapTo(metaEntities);
    }

    @Override
    public Collection<Policy> list() {
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

    private Policy mapTo(PolicyEntity policyEntity) {
        if (policyEntity != null) {
            Policy policy = new Policy();
            policy.setId(policyEntity.getId());
            policy.setClassName(policyEntity.getClassName());
            policy.setDescription(policyEntity.getDescription());
            return policy;
        } else {
            return null;
        }
    }

    private PolicyEntity mapTo(Policy policy) {
        if (policy != null) {
            PolicyEntity policyEntity = new PolicyEntity();
            policyEntity.setId(policy.getId());
            policyEntity.setClassName(policy.getClassName());
            policyEntity.setDescription(policy.getDescription());
            return policyEntity;
        } else {
            return null;
        }
    }

    private Collection<PolicyMetaEntity> mapTo(
            PolicyEntity policyEntity, Map<String, String> meta) {
        if (meta != null && !meta.isEmpty()) {
            Collection<PolicyMetaEntity> metas = new ArrayList<>();
            for (String key : meta.keySet()) {
                PolicyMetaEntity metaEntity = new PolicyMetaEntity();
                metaEntity.setKey(key);
                metaEntity.setValue(meta.get(key));
                metaEntity.setPolicy(policyEntity);
                metas.add(metaEntity);
            }
            return metas;
        } else {
            return null;
        }
    }

    private Map<String, String> mapTo(Collection<PolicyMetaEntity> metaEntities) {
        if (metaEntities != null && !metaEntities.isEmpty()) {
            Map<String, String> metas = new HashMap<>();
            for (PolicyMetaEntity metaEntity : metaEntities) {
                metas.put(metaEntity.getKey(), metaEntity.getValue());
            }
            return metas;
        } else {
            return null;
        }
    }
}
