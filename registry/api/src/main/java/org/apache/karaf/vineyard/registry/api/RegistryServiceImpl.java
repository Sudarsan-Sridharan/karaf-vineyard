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
import org.apache.karaf.vineyard.registry.api.entity.ApiEntity;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * Implementation of the Registry service using the JPA entity manager service (provided by Karaf).
 */
@Component(service = ApiRegistryService.class, immediate = true)
public class RegistryServiceImpl implements ApiRegistryService {

    @Reference(target = "(osgi.unit.name=vineyard)")
    private JpaTemplate jpaTemplate;

    @Override
    public API add(API api) {
        api.setId(UUID.randomUUID().toString());
        jpaTemplate.tx(TransactionType.RequiresNew, entityManager -> {
            entityManager.persist(mapTo(api));
            entityManager.flush();
        });
        return api;
    }

    @Override
    public void definition(API api, InputStream inputStream) {
        jpaTemplate.tx(TransactionType.RequiresNew, entityManager -> {
            ApiEntity apiEntity = entityManager.find(ApiEntity.class, api.getId());

            if (apiEntity !=  null) {
                byte[] buffer = new byte[1024];
                try (ByteArrayOutputStream output = new ByteArrayOutputStream()) {
                    int cpt = 0;
                    while ((cpt = inputStream.read(buffer)) > -1) {
                        output.write(buffer, 0, cpt);
                    }
                    apiEntity.setDefinition(output.toByteArray());
                } catch (IOException e) {
                    //do nothing
                }
                entityManager.merge(apiEntity);
                entityManager.flush();
            }
        });
    }

    @Override
    public void delete(API api) {
        if (api != null) {
            delete(api.getId());
        }
    }

    @Override
    public void delete(String id) {
        jpaTemplate.tx(TransactionType.RequiresNew, entityManager -> {
            ApiEntity apiEntity = entityManager.find(ApiEntity.class, id);
            if (apiEntity !=  null) {
                entityManager.remove(apiEntity);
                entityManager.flush();
            }
        });
    }

    @Override
    public void update(API api) {
        jpaTemplate.tx(TransactionType.RequiresNew, entityManager -> {
            ApiEntity apiEntity = entityManager.find(ApiEntity.class, api.getId());

            if (apiEntity !=  null) {
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
        ApiEntity apiEntity = jpaTemplate.txExpr(TransactionType.Supports,
                entityManager -> entityManager.find(ApiEntity.class, id));
        return mapTo(apiEntity);
    }

    @Override
    public Collection<API> list() {
        List<ApiEntity> list = jpaTemplate.txExpr(TransactionType.Supports,
                entityManager -> entityManager.createQuery("SELECT a FROM ApiEntity a", ApiEntity.class).getResultList());
        Collection<API> results = new ArrayList<>();
        for (ApiEntity entity : list) {
            results.add(mapTo(entity));
        }
        return results;
    }

    @Override
    public void addResource(API api, Resource resource) {
        // TODO
    }

    @Override
    public void deleteResource(API api, Resource resource) {
        // TODO
    }

    @Override
    public Collection<Resource> listResources(API api) {
        // TODO
        return null;
    }

    @Override
    public void addMeta(API api, Map<String, String> meta) {
        // TODO
    }

    @Override
    public void deleteMeta(API api, String key) {
        // TODO
    }

    @Override
    public void updateMeta(API api, Map<String, String> meta) {
        // TODO
    }

    @Override
    public Map<String, String> getMeta(API api) {
        // TODO
        return null;
    }

    private API mapTo(ApiEntity apiEntity) {
        if (apiEntity != null) {
            API api = new API();
            api.setId(apiEntity.getId());
            api.setContext(apiEntity.getContext());
            api.setDescription(apiEntity.getDescription());
            api.setName(apiEntity.getName());
            return api;
        } else {
            return null;
        }
    }

    private ApiEntity mapTo(API api) {
        if (api != null) {
            ApiEntity apiEntity = new ApiEntity();
            apiEntity.setId(api.getId());
            apiEntity.setContext(api.getContext());
            apiEntity.setDescription(api.getDescription());
            apiEntity.setName(api.getName());
            return apiEntity;
        } else {
            return null;
        }
    }

}
