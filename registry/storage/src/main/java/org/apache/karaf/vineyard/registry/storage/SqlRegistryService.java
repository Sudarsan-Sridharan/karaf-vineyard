/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.karaf.vineyard.registry.storage;

import org.apache.karaf.vineyard.common.DataFormat;
import org.apache.karaf.vineyard.common.JmsAPI;
import org.apache.karaf.vineyard.common.RestAPI;
import org.apache.karaf.vineyard.common.Resource;
import org.apache.karaf.vineyard.registry.api.RegistryService;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.sql.*;
import java.util.*;

/**
 * Implementation of the service processing, storing the services into a database.
 */
@Component(
        name = "org.apache.karaf.vineyard.registry.storage.sqlService",
        immediate = true
)
public class SqlRegistryService implements RegistryService {

    private final static Logger LOGGER = LoggerFactory.getLogger(SqlRegistryService.class);

    @Reference(target = "(osgi.jndi.service.name=jdbc/vineyard)")
    private DataSource dataSource;

    private String dialect;

    @Activate
    public void activate(ComponentContext context) {
        open(context.getProperties());
    }
    
    public void open(Dictionary<String, Object> config) {
        this.dialect = getValue(config, "dialect", "derby");
        LOGGER.debug("Dialect {} ", this.dialect);
        LOGGER.debug("Datasource {} ", this.dataSource);
        try (Connection connection = dataSource.getConnection()) {
            createTables(connection);
        } catch (Exception e) {
            LOGGER.error("Error creating table ", e);
        }
    }

    private String getValue(Dictionary<String, Object> config, String key, String defaultValue) {
        String value = (String) config.get(key);
        return (value != null) ? value : defaultValue;
    }

    private void createTables(Connection connection) {

        DatabaseMetaData dbm;
        ResultSet tables;
        
        try {
            dbm = connection.getMetaData();
            
            tables = dbm.getTables(null, "VINEYARD", "SERVICE", null);
            if (!tables.next()) {
                LOGGER.info("Tables does not exist");
                // Tables does not exist so we create all the tables
                String[] createTemplate = null;
                if (dialect.equalsIgnoreCase("mysql")) {
                    //TODO createTableQueryMySQLTemplate;
                } else if (dialect.equalsIgnoreCase("derby")) {
                    createTemplate = SqlRegistryConstants.createTableQueryDerbyTemplate;
                } else {
                    //TODO createTableQueryGenericTemplate;
                }
                try (Statement createStatement = connection.createStatement()) {
                    for (int cpt = 0; cpt < createTemplate.length; cpt++) {
                        createStatement.addBatch(createTemplate[cpt]);
                    }
                    if (createStatement.executeBatch().length == 0) {
                        throw new SQLException("No table has been created !");
                    }
                    LOGGER.info("Schema and tables has been created");
                } catch (SQLException exception) {
                    LOGGER.error("Can't create tables", exception);
                }
            } else {
                LOGGER.info("Tables already exist");
            }
        } catch (SQLException exception) {
            LOGGER.error("Can't verify tables existence", exception);
        }
    }

    @Override
    public void addRestAPI(RestAPI api) {
        try (Connection connection = dataSource.getConnection()) {
            
            if (connection.getAutoCommit()) {
                connection.setAutoCommit(false);
            }
            
            try (PreparedStatement insertStatement = 
                    connection.prepareStatement(SqlRegistryConstants.insertRestApiSql, PreparedStatement.RETURN_GENERATED_KEYS)) {

                // set values
                insertStatement.setString(1, api.getName());
                insertStatement.setString(2, api.getDescription());
                insertStatement.setString(3, api.getEndpoint());
                insertStatement.setString(4, api.getContext());
                if (api.getFormat() != null) {
                    insertStatement.setString(5, api.getFormat().getId());
                } else {
                    insertStatement.setString(5, null);
                }
                insertStatement.executeUpdate();

                int newId = 0;
                ResultSet rs = insertStatement.getGeneratedKeys();

                if (rs.next()) {
                    newId = rs.getInt(1);
                }

                connection.commit();

                api.setId(String.valueOf(newId));
                LOGGER.debug("Restapi created with id = {}", newId);
            
            } catch (SQLException exception) {
                connection.rollback();
                LOGGER.error("Can't insert Restapi with name {}", api.getName(), exception);
            }
            
        } catch (Exception exception) {
            LOGGER.error("Error getting connection ", exception);
        }
    }

    @Override
    public void deleteRestAPI(RestAPI api) {
        try (Connection connection = dataSource.getConnection()) {
            
            if (connection.getAutoCommit()) {
                connection.setAutoCommit(false);
            }
            
            try (PreparedStatement deleteStatement = 
                    connection.prepareStatement(SqlRegistryConstants.deleteRestApiSql)) {
                // where values
                deleteStatement.setString(1, api.getId());
                deleteStatement.executeUpdate();
                deleteExtraDataForRestApi(connection, api);
                connection.commit();
                LOGGER.debug("RestAPI deleted with id = {}", api.getId());
            } catch (SQLException exception) {
                connection.rollback();
                LOGGER.error("Can't delete RestAPI with name {}", api.getId(), exception);
            }
            
        } catch (Exception exception) {
            LOGGER.error("Error getting connection ", exception);
        }
    }

    @Override
    public void deleteRestAPI(String id) {
        RestAPI api = getRestAPI(id);
        if (api != null) {
            deleteRestAPI(api);
        }
    }
    
    private void deleteExtraDataForRestApi(Connection connection, RestAPI api) throws SQLException {

        String sqlQuery = SqlRegistryConstants.deleteRestApiMetadataSql;
        
        try (PreparedStatement deleteStatement = connection.prepareStatement(sqlQuery)) {
            // where values
            deleteStatement.setString(1, api.getId());
            deleteStatement.executeUpdate();
            
            LOGGER.debug("RestAPI extra data updated with id = {}", api.getId());
        } catch (SQLException exception) {
            LOGGER.error("Can't udpate RestAPI extra data with id {}", api.getName(), exception);
            throw exception;
        }

        sqlQuery = SqlRegistryConstants.deleteRestApiResourcesSql;

        try (PreparedStatement deleteStatement = connection.prepareStatement(sqlQuery)) {
            // where values
            deleteStatement.setString(1, api.getId());
            deleteStatement.executeUpdate();

            LOGGER.debug("RestAPI updated resources with id = {}", api.getId());
        } catch (SQLException exception) {
            LOGGER.error("Can't udpate RestAPI resources with id {}", api.getName(), exception);
            throw exception;
        }
    }

    @Override
    public void updateRestAPI(RestAPI api) {
        try (Connection connection = dataSource.getConnection()) {
            
            if (connection.getAutoCommit()) {
                connection.setAutoCommit(false);
            }
            
            try (PreparedStatement updateStatement = 
                    connection.prepareStatement(SqlRegistryConstants.updateRestApiSql)) {
                // set values
                updateStatement.setString(1, api.getName());
                updateStatement.setString(2, api.getDescription());
                updateStatement.setString(3, api.getEndpoint());
                updateStatement.setString(4, api.getContext());
                if (api.getFormat() != null) {
                    updateStatement.setString(5, api.getFormat().getId());
                } else {
                    updateStatement.setString(5, null);
                }
                // where values
                updateStatement.setString(6, api.getId());
                updateStatement.executeUpdate();

                updateExtraDataForRestApi(connection, api);
                connection.commit();
                LOGGER.debug("RestAPI updated with id = {}", api.getId());
            } catch (SQLException exception) {
                connection.rollback();
                LOGGER.error("Can't udpate RestAPI with name {}", api.getName(), exception);
            }
            
        } catch (Exception exception) {
            LOGGER.error("Error getting connection ", exception);
        }
    }
    
    private void updateExtraDataForRestApi(Connection connection, RestAPI api) throws SQLException {
        
        deleteExtraDataForRestApi(connection, api);
        
        for (Resource resource : api.getResources()) {
            try (PreparedStatement insertStatement = 
                    connection.prepareStatement(SqlRegistryConstants.insertRestApiResourcesSql)) {
                // set values
                insertStatement.setString(1, api.getId());
                insertStatement.setString(2, resource.getPath());
                insertStatement.setString(3, resource.getMethod());
                insertStatement.executeUpdate();
                
                LOGGER.debug("RestAPI updated with id = {}", api.getId());
            } catch (SQLException exception) {
                LOGGER.error("Can't udpate RestAPI with name {}", api.getName(), exception);
                throw exception;
            }
        }

        for (String metadataKey : api.getMetadata().keySet()) {
            try (PreparedStatement insertStatement =
                         connection.prepareStatement(SqlRegistryConstants.insertRestApiMetadataSql)) {
                // set values
                insertStatement.setString(1, api.getId());
                insertStatement.setString(2, metadataKey);
                insertStatement.setString(3, api.getMetadata().get(metadataKey));
                insertStatement.executeUpdate();

                LOGGER.debug("RestAPI updated with id = {}", api.getId());
            } catch (SQLException exception) {
                LOGGER.error("Can't udpate RestAPI with name {}", api.getName(), exception);
                throw exception;
            }
        }
    }

    @Override
    public RestAPI getRestAPI(String id) {

        RestAPI api = null;
        try (Connection connection = dataSource.getConnection()) {
            
            String sqlQuery = SqlRegistryConstants.selectRestApiSql + " where id = ?";
            try (PreparedStatement selectStatement = connection.prepareStatement(sqlQuery)) {
                selectStatement.setString(1, id);
                ResultSet rs = selectStatement.executeQuery();
                
                if (rs.next()) {
                    api = new RestAPI();
                    api.setId(rs.getString("id"));
                    api.setName(rs.getString("name"));
                    api.setDescription(rs.getString("description"));
                    api.setEndpoint(rs.getString("endpoint"));
                    api.setContext(rs.getString("context"));
                    api.setFormat(getDataFormat(rs.getString("id_dataformat")));
                    api.setMetadata(selectRestApiMetadata(connection, api.getId()));
                    api.setResources(selectRestApiResources(connection, api.getId()));
                    api.setPolicies(null);//TODO get all policies for api
                }
            
            } catch (SQLException exception) {
                LOGGER.error("Can't find RestAPI with id {}", id, exception);
            }

        } catch (Exception exception) {
            LOGGER.error("Error getting connection ", exception);
        }

        return api;
    }

    @Override
    public List<RestAPI> getAllRestAPI() {

        List<RestAPI> apis = new ArrayList<>();

        try (Connection connection = dataSource.getConnection()) {

            String sqlQuery = SqlRegistryConstants.selectRestApiSql;
            try (PreparedStatement selectStatement = connection.prepareStatement(sqlQuery)) {
                ResultSet rs = selectStatement.executeQuery();

                if (rs.getFetchSize() > 0) {
                    while (rs.next()) {
                        RestAPI api = new RestAPI();
                        api.setId(rs.getString("id"));
                        api.setName(rs.getString("name"));
                        api.setDescription(rs.getString("description"));
                        api.setEndpoint(rs.getString("endpoint"));
                        api.setContext(rs.getString("context"));
                        api.setFormat(getDataFormat(rs.getString("id_dataformat")));
                        api.setMetadata(selectRestApiMetadata(connection, api.getId()));
                        api.setResources(selectRestApiResources(connection, api.getId()));
                        api.setPolicies(null);//TODO get all policies for api
                        apis.add(api);
                    }
                }

            } catch (SQLException exception) {
                LOGGER.error("Can't retrieve all RestAPI", exception);
            }

        } catch (Exception exception) {
            LOGGER.error("Error getting connection ", exception);
        }

        return apis;
    }

    private Map<String, String> selectRestApiMetadata(Connection connection, String idRestApi) {

        try (PreparedStatement selectStatement =
                     connection.prepareStatement(SqlRegistryConstants.selectRestApiMetadataSql)) {
            selectStatement.setString(1, idRestApi);
            ResultSet rs = selectStatement.executeQuery();

            if (rs.getFetchSize() > 0) {
                Map<String, String> metadatas = new HashMap<>();
                while (rs.next()) {
                    metadatas.put(rs.getString("key"),rs.getString("key"));
                }
                return metadatas;
            }
        } catch (SQLException exception) {
            LOGGER.error("Can't find metadata for restApi with id {}", idRestApi, exception);
        }
        return null;
    }

    private Collection<Resource> selectRestApiResources(Connection connection, String idRestApi) {

        try (PreparedStatement selectStatement =
                     connection.prepareStatement(SqlRegistryConstants.selectRestApiResourcesSql)) {
            selectStatement.setString(1, idRestApi);
            ResultSet rs = selectStatement.executeQuery();

            if (rs.getFetchSize() > 0) {
                Collection<Resource> resources = new ArrayList<>();
                while (rs.next()) {
                    Resource resource = new Resource();
                    resource.setMethod(rs.getString("method"));
                    resource.setMethod(rs.getString("path"));
                    resources.add(resource);
                }
                return resources;
            }
        } catch (SQLException exception) {
            LOGGER.error("Can't find metadata for restApi with id {}", idRestApi, exception);
        }
        return null;
    }

    @Override
    public void addJmsAPI(JmsAPI api) {
        try (Connection connection = dataSource.getConnection()) {

            if (connection.getAutoCommit()) {
                connection.setAutoCommit(false);
            }

            try (PreparedStatement insertStatement =
                         connection.prepareStatement(SqlRegistryConstants.insertJmsApiSql, PreparedStatement.RETURN_GENERATED_KEYS)) {

                // set values
                insertStatement.setString(1, api.getName());
                insertStatement.setString(2, api.getDescription());
                insertStatement.setString(3, api.getEndpoint());
                insertStatement.setString(4, api.getConnectionFactory());
                insertStatement.setString(5, api.getDestination());
                insertStatement.setString(6, api.getType());
                if (api.getFormat() != null) {
                    insertStatement.setString(7, api.getFormat().getId());
                } else {
                    insertStatement.setString(7, null);
                }
                insertStatement.executeUpdate();

                int newId = 0;
                ResultSet rs = insertStatement.getGeneratedKeys();

                if (rs.next()) {
                    newId = rs.getInt(1);
                }

                connection.commit();

                api.setId(String.valueOf(newId));
                LOGGER.debug("JmsAPI created with id = {}", newId);

            } catch (SQLException exception) {
                connection.rollback();
                LOGGER.error("Can't insert JmsAPI with name {}", api.getName(), exception);
            }

        } catch (Exception exception) {
            LOGGER.error("Error getting connection ", exception);
        }
    }

    @Override
    public void deleteJmsAPI(JmsAPI api) {
        try (Connection connection = dataSource.getConnection()) {

            if (connection.getAutoCommit()) {
                connection.setAutoCommit(false);
            }

            try (PreparedStatement deleteStatement =
                         connection.prepareStatement(SqlRegistryConstants.deleteJmsApiSql)) {
                // where values
                deleteStatement.setString(1, api.getId());
                deleteStatement.executeUpdate();
                deleteExtraDataForJmsApi(connection, api);
                connection.commit();
                LOGGER.debug("JmsAPI deleted with id = {}", api.getId());
            } catch (SQLException exception) {
                connection.rollback();
                LOGGER.error("Can't delete JmsAPI with name {}", api.getId(), exception);
            }

        } catch (Exception exception) {
            LOGGER.error("Error getting connection ", exception);
        }
    }

    private void deleteExtraDataForJmsApi(Connection connection, JmsAPI api) throws SQLException {

        String sqlQuery = SqlRegistryConstants.deleteJmsApiMetadataSql;

        try (PreparedStatement deleteStatement = connection.prepareStatement(sqlQuery)) {
            // where values
            deleteStatement.setString(1, api.getId());
            deleteStatement.executeUpdate();

            LOGGER.debug("JmsAPI extra data updated with id = {}", api.getId());
        } catch (SQLException exception) {
            LOGGER.error("Can't udpate JmsAPI extra data with id {}", api.getName(), exception);
            throw exception;
        }
    }

    @Override
    public void deleteJmsAPI(String id) {
        JmsAPI api = getJmsAPI(id);
        if (api != null) {
            deleteJmsAPI(api);
        }
    }

    @Override
    public void updateJmsAPI(JmsAPI api) {
        try (Connection connection = dataSource.getConnection()) {

            if (connection.getAutoCommit()) {
                connection.setAutoCommit(false);
            }

            try (PreparedStatement updateStatement =
                         connection.prepareStatement(SqlRegistryConstants.updateJmsApiSql)) {
                // set values
                updateStatement.setString(1, api.getName());
                updateStatement.setString(2, api.getDescription());
                updateStatement.setString(3, api.getEndpoint());
                updateStatement.setString(4, api.getConnectionFactory());
                updateStatement.setString(5, api.getDestination());
                updateStatement.setString(6, api.getType());
                if (api.getFormat() != null) {
                    updateStatement.setString(7, api.getFormat().getId());
                } else {
                    updateStatement.setString(7, null);
                }
                // where values
                updateStatement.setString(8, api.getId());
                updateStatement.executeUpdate();

                updateExtraDataForJmsApi(connection, api);
                connection.commit();
                LOGGER.debug("JmsAPI updated with id = {}", api.getId());
            } catch (SQLException exception) {
                connection.rollback();
                LOGGER.error("Can't udpate JmsAPI with name {}", api.getName(), exception);
            }

        } catch (Exception exception) {
            LOGGER.error("Error getting connection ", exception);
        }
    }

    private void updateExtraDataForJmsApi(Connection connection, JmsAPI api) throws SQLException {

        deleteExtraDataForJmsApi(connection, api);

        for (String metadataKey : api.getMetadata().keySet()) {
            try (PreparedStatement insertStatement =
                         connection.prepareStatement(SqlRegistryConstants.insertJmsApiMetadataSql)) {
                // set values
                insertStatement.setString(1, api.getId());
                insertStatement.setString(2, metadataKey);
                insertStatement.setString(3, api.getMetadata().get(metadataKey));
                insertStatement.executeUpdate();

                LOGGER.debug("JmsAPI updated with id = {}", api.getId());
            } catch (SQLException exception) {
                LOGGER.error("Can't update JmsAPI with name {}", api.getName(), exception);
                throw exception;
            }
        }
    }

    @Override
    public JmsAPI getJmsAPI(String id) {
        JmsAPI api = null;
        try (Connection connection = dataSource.getConnection()) {

            String sqlQuery = SqlRegistryConstants.selectJmsApiSql + " where id = ?";
            try (PreparedStatement selectStatement = connection.prepareStatement(sqlQuery)) {
                selectStatement.setString(1, id);
                ResultSet rs = selectStatement.executeQuery();

                if (rs.next()) {
                    api = new JmsAPI();
                    api.setId(rs.getString("id"));
                    api.setName(rs.getString("name"));
                    api.setDescription(rs.getString("description"));
                    api.setEndpoint(rs.getString("endpoint"));
                    api.setConnectionFactory(rs.getString("connectionFactory"));
                    api.setDestination(rs.getString("destination"));
                    api.setType(rs.getString("type"));
                    api.setFormat(getDataFormat(rs.getString("id_dataformat")));
                    api.setMetadata(selectJmsApiMetadata(connection, api.getId()));
                    api.setPolicies(null);//TODO get all policies for api
                }

            } catch (SQLException exception) {
                LOGGER.error("Can't find JmsApi with id {}", id, exception);
            }

        } catch (Exception exception) {
            LOGGER.error("Error getting connection ", exception);
        }

        return api;
    }

    private Map<String, String> selectJmsApiMetadata(Connection connection, String idJmsApi) {

        try (PreparedStatement selectStatement =
                     connection.prepareStatement(SqlRegistryConstants.selectJmsApiMetadataSql)) {
            selectStatement.setString(1, idJmsApi);
            ResultSet rs = selectStatement.executeQuery();

            if (rs.getFetchSize() > 0) {
                Map<String, String> metadatas = new HashMap<>();
                while (rs.next()) {
                    metadatas.put(rs.getString("key"),rs.getString("key"));
                }
                return metadatas;
            }
        } catch (SQLException exception) {
            LOGGER.error("Can't find metadata for JmsApi with id {}", idJmsApi, exception);
        }
        return null;
    }

    @Override
    public List<JmsAPI> getAllJmsAPI() {
        List<JmsAPI> apis = new ArrayList<>();

        try (Connection connection = dataSource.getConnection()) {

            String sqlQuery = SqlRegistryConstants.selectJmsApiSql;
            try (PreparedStatement selectStatement = connection.prepareStatement(sqlQuery)) {
                ResultSet rs = selectStatement.executeQuery();

                if (rs.getFetchSize() > 0) {
                    while (rs.next()) {
                        JmsAPI api = new JmsAPI();
                        api.setId(rs.getString("id"));
                        api.setName(rs.getString("name"));
                        api.setDescription(rs.getString("description"));
                        api.setEndpoint(rs.getString("endpoint"));
                        api.setConnectionFactory(rs.getString("connectionFactory"));
                        api.setDestination(rs.getString("destination"));
                        api.setType(rs.getString("type"));
                        api.setFormat(getDataFormat(rs.getString("id_dataformat")));
                        api.setMetadata(selectRestApiMetadata(connection, api.getId()));
                        api.setPolicies(null);//TODO get all policies for api
                        apis.add(api);
                    }
                }

            } catch (SQLException exception) {
                LOGGER.error("Can't retrieve all RestAPI", exception);
            }

        } catch (Exception exception) {
            LOGGER.error("Error getting connection ", exception);
        }

        return apis;
    }

    @Override
    public void addDataFormat(DataFormat dataformat) {
        try (Connection connection = dataSource.getConnection()) {
            
            if (connection.getAutoCommit()) {
                connection.setAutoCommit(false);
            }
            
            try (PreparedStatement insertStatement = 
                    connection.prepareStatement(SqlRegistryConstants.insertDataformatSql, PreparedStatement.RETURN_GENERATED_KEYS)) {
                // set values
                insertStatement.setString(1, dataformat.getName());
                insertStatement.setString(2, dataformat.getSample());
                insertStatement.setString(3, dataformat.getSchema());
                insertStatement.executeUpdate();
                
                int newId = 0;
                ResultSet rs = insertStatement.getGeneratedKeys();
                
                if (rs.next()) {
                    newId = rs.getInt(1);
                }
                
                connection.commit();
                
                dataformat.setId(String.valueOf(newId));
                LOGGER.debug("Dataformat created with id = {}", newId);
            
            } catch (SQLException exception) {
                connection.rollback();
                LOGGER.error("Can't insert dataformat with name {}", dataformat.getName(), exception);
            }
            
        } catch (Exception exception) {
            LOGGER.error("Error getting connection ", exception);
        }     
    }

    @Override
    public void deleteDataFormat(DataFormat dataformat) {
        deleteDataFormat(dataformat.getId());
    }

    @Override
    public void deleteDataFormat(String id) {
        try (Connection connection = dataSource.getConnection()) {
            
            if (connection.getAutoCommit()) {
                connection.setAutoCommit(false);
            }
            
            try (PreparedStatement deleteStatement = 
                    connection.prepareStatement(SqlRegistryConstants.deleteDataformatSql)) {
                // where values
                deleteStatement.setString(1, id);
                deleteStatement.executeUpdate();
                connection.commit();
                LOGGER.debug("Dataformat deleted with id = {}", id);
            } catch (SQLException exception) {
                connection.rollback();
                LOGGER.error("Can't delete dataformat with id {}", id, exception);
                throw exception;
            }
            
        } catch (Exception exception) {
            LOGGER.error("Error when deleting environment", exception);
        }
    }

    @Override
    public void updateDataFormat(DataFormat dataformat) {

        try (Connection connection = dataSource.getConnection()) {
            
            if (connection.getAutoCommit()) {
                connection.setAutoCommit(false);
            }
            
            try (PreparedStatement updateStatement = 
                    connection.prepareStatement(SqlRegistryConstants.updateDataformatSql)) {
                // set values
                updateStatement.setString(1, dataformat.getName());
                updateStatement.setString(2, dataformat.getSample());
                updateStatement.setString(3, dataformat.getSchema());
                // where values
                updateStatement.setString(4, dataformat.getId());
                updateStatement.executeUpdate();
                connection.commit();
                LOGGER.debug("Dataformat updated with id = {}", dataformat.getId());
            } catch (SQLException exception) {
                connection.rollback();
                LOGGER.error("Can't udpate dataformat with id {}", dataformat.getId(), exception);
            }
            //TODO update extra content
            
        } catch (Exception exception) {
            LOGGER.error("Error getting connection ", exception);
        }
    }

    @Override
    public DataFormat getDataFormat(String id) {
        try (Connection connection = dataSource.getConnection()) {
            return selectDataFormat(connection, id);
            
        } catch (Exception exception) {
            LOGGER.error("Error getting connection ", exception);
        }
        return null;
    }
    
    private DataFormat selectDataFormat(Connection connection, String id) {
        String sqlQuery = SqlRegistryConstants.selectDataformatSql + " where id = ?";
        
        try (PreparedStatement selectStatement = connection.prepareStatement(sqlQuery)) {
            selectStatement.setString(1, id);
            ResultSet rs = selectStatement.executeQuery();
            
            if (rs.next()) {
                DataFormat dataformat = new DataFormat();
                dataformat.setId(id);
                dataformat.setName(rs.getString("name"));
                dataformat.setSample(rs.getString("sample"));
                dataformat.setSchema(rs.getString("dataschema"));
                return dataformat;
            }
        
        } catch (SQLException exception) {
            LOGGER.error("Can't find dataformat with id {}", id, exception);
        }
        return null;
    }

    @Override
    public List<DataFormat> getAllDataFormats() {
        
        List<DataFormat> dataformats = new ArrayList<>();
        try (Connection connection = dataSource.getConnection()) {
            
            try (PreparedStatement selectStatement = connection.prepareStatement(SqlRegistryConstants.selectDataformatSql)) {
                ResultSet rs = selectStatement.executeQuery();
                
                while (rs.next()) {
                    DataFormat dataformat = new DataFormat();
                    dataformat.setId(rs.getString("id"));
                    dataformat.setName(rs.getString("name"));
                    dataformat.setSample(rs.getString("sample"));
                    dataformat.setSchema(rs.getString("dataschema"));
                    dataformats.add(dataformat);
                }
            
            } catch (SQLException exception) {
                LOGGER.error("Can't retreive the dataformats", exception);
            }
            
        } catch (Exception exception) {
            LOGGER.error("Error getting connection ", exception);
        }
        return dataformats;
    }
}
