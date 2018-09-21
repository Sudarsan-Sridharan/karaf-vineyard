package org.apache.karaf.vineyard.registry.storage;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.aries.jpa.template.JpaTemplate;
import org.apache.aries.jpa.template.TransactionType;
import org.apache.karaf.vineyard.common.API;
import org.apache.karaf.vineyard.common.DataFormat;
import org.apache.karaf.vineyard.registry.api.RegistryService;
import org.apache.karaf.vineyard.registry.storage.entity.ApiEntity;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * Implementation of the Registry service using the JPA entity manager service (provided by Karaf).
 */
@Component(service = RegistryServiceImpl.class, immediate = true)
public class RegistryServiceImpl implements RegistryService {

    @Reference(target = "(osgi.unit.name=vineyard)")
    private JpaTemplate jpaTemplate;

    @Override
    public void addApi(API api) {
        jpaTemplate.tx(TransactionType.RequiresNew, entityManager -> {
            entityManager.persist((ApiEntity) api);
            entityManager.flush();
        });
    }

    @Override
    public void deleteApi(API api) {
        jpaTemplate.tx(TransactionType.RequiresNew, entityManager -> {
            ApiEntity apiEntity = entityManager.find(ApiEntity.class, api.getId());
            if (apiEntity !=  null) {
                entityManager.remove(apiEntity);
            }
        });
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

    }

    @Override
    public API getApi(String id) {
        return jpaTemplate.txExpr(TransactionType.Supports,
                entityManager -> entityManager.find(ApiEntity.class, id));
    }

    @Override
    public Collection<API> getApis() {
        List<ApiEntity> list = jpaTemplate.txExpr(TransactionType.Supports,
                entityManager -> entityManager.createQuery("SELECT a FROM ApiEntity a", ApiEntity.class).getResultList());
        Collection<API> results = new ArrayList<>();
        for (ApiEntity entity : list) {
            ((ArrayList<API>) results).add(entity);
        }
        return results;
    }

    @Override
    public void addDataFormat(DataFormat dataFormat) {

    }

    @Override
    public void deleteDataFormat(DataFormat dataFormat) {

    }

    @Override
    public void deleteDataFormat(String id) {

    }

    @Override
    public void updateDataFormat(DataFormat dataFormat) {

    }

    @Override
    public DataFormat getDataFormat(String id) {
        return null;
    }

    @Override
    public Collection<DataFormat> getDataFormats() {
        return null;
    }
}
