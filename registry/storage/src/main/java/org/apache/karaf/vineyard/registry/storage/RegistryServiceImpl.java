package org.apache.karaf.vineyard.registry.storage;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.aries.jpa.template.JpaTemplate;
import org.apache.aries.jpa.template.TransactionType;
import org.apache.karaf.vineyard.common.API;
import org.apache.karaf.vineyard.common.DataFormat;
import org.apache.karaf.vineyard.common.Resource;
import org.apache.karaf.vineyard.registry.api.RegistryService;
import org.apache.karaf.vineyard.registry.storage.entity.ApiEntity;
import org.apache.karaf.vineyard.registry.storage.entity.DataFormatEntity;
import org.apache.karaf.vineyard.registry.storage.entity.MetadataEntity;
import org.apache.karaf.vineyard.registry.storage.entity.ResourceEntity;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * Implementation of the Registry service using the JPA entity manager service (provided by Karaf).
 */
@Component(service = RegistryService.class, immediate = true)
public class RegistryServiceImpl implements RegistryService {

    @Reference(target = "(osgi.unit.name=vineyard)")
    private JpaTemplate jpaTemplate;

    @Override
    public void addApi(API api) {
        api.setId(UUID.randomUUID().toString());
        jpaTemplate.tx(TransactionType.RequiresNew, entityManager -> {
            entityManager.persist(mapTo(api));
            entityManager.flush();
        });
    }

    @Override
    public void deleteApi(API api) {
        if (api != null) {
            deleteApi(api.getId());
        }
    }

    @Override
    public void deleteApi(String id) {
        jpaTemplate.tx(TransactionType.RequiresNew, entityManager -> {
            ApiEntity apiEntity = entityManager.find(ApiEntity.class, id);
            if (apiEntity !=  null) {
                entityManager.remove(apiEntity);
            }
        });
    }

    @Override
    public void updateApi(API api) {
        //TODO
    }

    @Override
    public API getApi(String id) {
        ApiEntity apiEntity = jpaTemplate.txExpr(TransactionType.Supports,
                entityManager -> entityManager.find(ApiEntity.class, id));
        return mapTo(apiEntity);
    }

    @Override
    public Collection<API> getApis() {
        List<ApiEntity> list = jpaTemplate.txExpr(TransactionType.Supports,
                entityManager -> entityManager.createQuery("SELECT a FROM ApiEntity a", ApiEntity.class).getResultList());
        Collection<API> results = new ArrayList<>();
        for (ApiEntity entity : list) {
            results.add(mapTo(entity));
        }
        return results;
    }

    @Override
    public void addDataFormat(DataFormat dataFormat) {
        dataFormat.setId(UUID.randomUUID().toString());
        jpaTemplate.tx(TransactionType.RequiresNew, entityManager -> {
            entityManager.persist(mapTo(dataFormat));
            entityManager.flush();
        });
    }

    @Override
    public void deleteDataFormat(DataFormat dataFormat) {
        if (dataFormat != null) {
            deleteDataFormat(dataFormat.getId());
        }
    }

    @Override
    public void deleteDataFormat(String id) {
        jpaTemplate.tx(TransactionType.RequiresNew, entityManager -> {
            DataFormatEntity dataFormatEntity = entityManager.find(DataFormatEntity.class, id);
            if (dataFormatEntity !=  null) {
                entityManager.remove(dataFormatEntity);
            }
        });
    }

    @Override
    public void updateDataFormat(DataFormat dataFormat) {
        //TODO
    }

    @Override
    public DataFormat getDataFormat(String id) {
        DataFormatEntity dataFormatEntity = jpaTemplate.txExpr(TransactionType.Supports,
                entityManager -> entityManager.find(DataFormatEntity.class, id));
        return mapTo(dataFormatEntity);
    }

    @Override
    public Collection<DataFormat> getDataFormats() {
        List<DataFormatEntity> list = jpaTemplate.txExpr(TransactionType.Supports,
                entityManager -> entityManager.createQuery("SELECT a FROM DataFormatEntity a", DataFormatEntity.class).getResultList());
        Collection<DataFormat> results = new ArrayList<>();
        for (DataFormatEntity entity : list) {
            results.add(mapTo(entity));
        }
        return results;
    }

    private API mapTo(ApiEntity apiEntity) {
        if (apiEntity != null) {
            API api = new API();
            api.setId(apiEntity.getId());
            api.setContext(apiEntity.getContext());
            api.setDescription(apiEntity.getDescription());
            api.setName(apiEntity.getName());
            api.setVersion(apiEntity.getVersion());
            api.getMetadata().putAll(mapTo(apiEntity.getMetadata()));
            for (ResourceEntity resourceEntity : apiEntity.getResources()) {
                api.getResources().add(mapTo(resourceEntity));
            }
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
            apiEntity.setVersion(api.getVersion());
            apiEntity.getMetadata().addAll(mapTo(api.getMetadata()));
            for (Resource resource : api.getResources()) {
                apiEntity.getResources().add(mapTo(resource));
            }
            return apiEntity;
        } else {
            return null;
        }
    }

    private Resource mapTo(ResourceEntity resourceEntity) {
        if (resourceEntity != null) {
            Resource resource = new Resource();
            resource.setBridge(resourceEntity.getBridge());
            resource.setMethod(resourceEntity.getMethod());
            resource.setPath(resourceEntity.getPath());
            resource.setInFormat(mapTo(resourceEntity.getInFormat()));
            resource.setOutFormat(mapTo(resourceEntity.getOutFormat()));
            resource.setBridge(resourceEntity.getBridge());
            resource.setUseBridge(resourceEntity.isUseBridge());
            resource.setResponse(resourceEntity.getResponse());
            return resource;
        } else {
            return null;
        }
    }

    private ResourceEntity mapTo(Resource resource) {
        if (resource != null) {
            ResourceEntity resourceEntity = new ResourceEntity();
            resourceEntity.setBridge(resource.getBridge());
            resourceEntity.setMethod(resource.getMethod());
            resourceEntity.setPath(resource.getPath());
            resourceEntity.setInFormat(mapTo(resource.getInFormat()));
            resourceEntity.setOutFormat(mapTo(resource.getOutFormat()));
            resourceEntity.setBridge(resource.getBridge());
            resourceEntity.setUseBridge(resource.isUseBridge());
            resourceEntity.setResponse(resource.getResponse());
            return resourceEntity;
        } else {
            return null;
        }
    }

    private DataFormat mapTo(DataFormatEntity dataFormatEntity) {
        if (dataFormatEntity != null) {
            DataFormat dataFormat = new DataFormat();
            dataFormat.setId(dataFormatEntity.getId());
            dataFormat.setName(dataFormatEntity.getName());
            dataFormat.setSample(dataFormatEntity.getSample());
            dataFormat.setSchema(dataFormatEntity.getSchema());
            return dataFormat;
        } else {
            return null;
        }
    }

    private DataFormatEntity mapTo(DataFormat dataFormat) {
        if (dataFormat != null) {
            DataFormatEntity dataFormatEntity = new DataFormatEntity();
            dataFormatEntity.setId(dataFormat.getId());
            dataFormatEntity.setName(dataFormat.getName());
            dataFormatEntity.setSample(dataFormat.getSample());
            dataFormatEntity.setSchema(dataFormat.getSchema());
            return dataFormatEntity;
        } else {
            return null;
        }
    }

    private Collection<MetadataEntity> mapTo(Map<String, String> metadata) {
        if (metadata != null && !metadata.isEmpty()) {
            Collection<MetadataEntity> metadataEntities = new ArrayList<>();

            for (String key : metadata.keySet()) {
                MetadataEntity metadataEntity = new MetadataEntity();
                metadataEntity.setKey(key);
                metadataEntity.setValue(metadata.get(key));
                metadataEntities.add(metadataEntity);
            }
            return metadataEntities;

        } else {
            return null;
        }
    }

    private Map<String, String> mapTo(Collection<MetadataEntity> metadataEntities) {
        if (metadataEntities != null && !metadataEntities.isEmpty()) {
            Map<String, String> metadatas = new HashMap<>();

            for (MetadataEntity metadataEntity : metadataEntities) {
                metadatas.put(metadataEntity.getKey(), metadataEntity.getValue());
            }
            return metadatas;

        } else {
            return null;
        }
    }

}
