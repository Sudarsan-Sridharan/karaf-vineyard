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

import org.apache.karaf.vineyard.common.API;
import org.apache.karaf.vineyard.common.DataFormat;
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
 * Implementation of the API registry service, storing the API into a database.
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
            
            tables = dbm.getTables(null, "VINEYARD", "API", null);
            if (!tables.next()) {
                LOGGER.info("Tables does not exist");
                // Tables does not exist so we create all the tables
                String[] createTemplate = null;
                if (dialect.equalsIgnoreCase("derby")) {
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
    public void addApi(API api) {
        try (Connection connection = dataSource.getConnection()) {
            
            if (connection.getAutoCommit()) {
                connection.setAutoCommit(false);
            }
            
            try (PreparedStatement insertStatement = 
                    connection.prepareStatement(SqlRegistryConstants.insertApiSql, PreparedStatement.RETURN_GENERATED_KEYS)) {

                // set values
                insertStatement.setString(1, api.getName());
                insertStatement.setString(2, api.getContext());
                insertStatement.setString(3, api.getDescription());
                insertStatement.setString(4, api.getVersion());
                insertStatement.executeUpdate();

                int newId = 0;
                ResultSet rs = insertStatement.getGeneratedKeys();

                if (rs.next()) {
                    newId = rs.getInt(1);
                }

                connection.commit();

                api.setId(String.valueOf(newId));
                LOGGER.debug("Api created with id = {}", newId);
            
            } catch (SQLException exception) {
                connection.rollback();
                LOGGER.error("Can't insert Api with name {}", api.getName(), exception);
            }
            
        } catch (Exception exception) {
            LOGGER.error("Error getting connection ", exception);
        }
    }

    @Override
    public void deleteApi(API api) {
        try (Connection connection = dataSource.getConnection()) {
            
            if (connection.getAutoCommit()) {
                connection.setAutoCommit(false);
            }
            
            try (PreparedStatement deleteStatement = 
                    connection.prepareStatement(SqlRegistryConstants.deleteApiSql)) {
                // where values
                deleteStatement.setString(1, api.getId());
                deleteStatement.executeUpdate();
                deleteExtraDataForApi(connection, api);
                connection.commit();
                LOGGER.debug("API deleted with id = {}", api.getId());
            } catch (SQLException exception) {
                connection.rollback();
                LOGGER.error("Can't delete API with name {}", api.getId(), exception);
            }
            
        } catch (Exception exception) {
            LOGGER.error("Error getting connection ", exception);
        }
    }

    @Override
    public void deleteApi(String id) {
        API api = getApi(id);
        if (api != null) {
            deleteApi(api);
        }
    }
    
    private void deleteExtraDataForApi(Connection connection, API api) throws SQLException {

        String sqlQuery = SqlRegistryConstants.deleteApiMetadataSql;
        
        try (PreparedStatement deleteStatement = connection.prepareStatement(sqlQuery)) {
            // where values
            deleteStatement.setString(1, api.getId());
            deleteStatement.executeUpdate();
            
            LOGGER.debug("API extra data updated with id = {}", api.getId());
        } catch (SQLException exception) {
            LOGGER.error("Can't udpate API extra data with id {}", api.getName(), exception);
            throw exception;
        }

        sqlQuery = SqlRegistryConstants.deleteApiResourcesSql;

        try (PreparedStatement deleteStatement = connection.prepareStatement(sqlQuery)) {
            // where values
            deleteStatement.setString(1, api.getId());
            deleteStatement.executeUpdate();

            LOGGER.debug("API updated resources with id = {}", api.getId());
        } catch (SQLException exception) {
            LOGGER.error("Can't udpate API resources with id {}", api.getName(), exception);
            throw exception;
        }
    }

    @Override
    public void updateApi(API api) {
        try (Connection connection = dataSource.getConnection()) {
            
            if (connection.getAutoCommit()) {
                connection.setAutoCommit(false);
            }
            
            try (PreparedStatement updateStatement = 
                    connection.prepareStatement(SqlRegistryConstants.updateApiSql)) {
                // set values
                updateStatement.setString(1, api.getName());
                updateStatement.setString(2, api.getContext());
                updateStatement.setString(3, api.getDescription());
                updateStatement.setString(4, api.getVersion());
                // where values
                updateStatement.setString(5, api.getId());
                updateStatement.executeUpdate();

                updateExtraDataForApi(connection, api);
                connection.commit();
                LOGGER.debug("API updated with id = {}", api.getId());
            } catch (SQLException exception) {
                connection.rollback();
                LOGGER.error("Can't udpate API with name {}", api.getName(), exception);
            }
            
        } catch (Exception exception) {
            LOGGER.error("Error getting connection ", exception);
        }
    }
    
    private void updateExtraDataForApi(Connection connection, API api) throws SQLException {
        
        deleteExtraDataForApi(connection, api);
        
        for (Resource resource : api.getResources()) {
            try (PreparedStatement insertStatement = 
                    connection.prepareStatement(SqlRegistryConstants.insertApiResourcesSql)) {
                // set values
                insertStatement.setString(1, api.getId());
                insertStatement.setString(2, resource.getPath());
                insertStatement.setString(3, resource.getMethod());
                insertStatement.setBoolean(4, resource.isUseBridge());
                insertStatement.setString(5, resource.getResponse());
                insertStatement.executeUpdate();
                
                LOGGER.debug("API updated with id = {}", api.getId());
            } catch (SQLException exception) {
                LOGGER.error("Can't udpate API with name {}", api.getName(), exception);
                throw exception;
            }
        }

        for (String metadataKey : api.getMetadata().keySet()) {
            try (PreparedStatement insertStatement =
                         connection.prepareStatement(SqlRegistryConstants.insertApiMetadataSql)) {
                // set values
                insertStatement.setString(1, api.getId());
                insertStatement.setString(2, metadataKey);
                insertStatement.setString(3, api.getMetadata().get(metadataKey));
                insertStatement.executeUpdate();

                LOGGER.debug("API updated with id = {}", api.getId());
            } catch (SQLException exception) {
                LOGGER.error("Can't udpate API with name {}", api.getName(), exception);
                throw exception;
            }
        }
    }

    @Override
    public API getApi(String id) {

        API api = null;
        try (Connection connection = dataSource.getConnection()) {
            
            String sqlQuery = SqlRegistryConstants.selectApiSql;
            sqlQuery +=  " where id = ?";
            try (PreparedStatement selectStatement = connection.prepareStatement(sqlQuery)) {
                selectStatement.setString(1, id);
                ResultSet rs = selectStatement.executeQuery();
                
                if (rs.next()) {
                    api = new API();
                    api.setId(rs.getString("id"));
                    api.setName(rs.getString("name"));
                    api.setContext(rs.getString("context"));
                    api.setDescription(rs.getString("description"));
                    api.setVersion(rs.getString("version"));
                    api.setMetadata(selectApiMetadata(connection, api.getId()));
                    api.setResources(selectApiResources(connection, api.getId()));
                }
            
            } catch (SQLException exception) {
                LOGGER.error("Can't find API with id {}", id, exception);
            }

        } catch (Exception exception) {
            LOGGER.error("Error getting connection ", exception);
        }

        return api;
    }

    @Override
    public Collection<API> getApis() {

        Collection<API> apis = new ArrayList<>();

        try (Connection connection = dataSource.getConnection()) {

            String sqlQuery = SqlRegistryConstants.selectApiSql;
            try (PreparedStatement selectStatement = connection.prepareStatement(sqlQuery)) {
                ResultSet rs = selectStatement.executeQuery();

                if (rs.getFetchSize() > 0) {
                    while (rs.next()) {
                        API api = new API();
                        api.setId(rs.getString("id"));
                        api.setName(rs.getString("name"));
                        api.setContext(rs.getString("context"));
                        api.setDescription(rs.getString("description"));
                        api.setVersion(rs.getString("version"));
                        api.setMetadata(selectApiMetadata(connection, api.getId()));
                        api.setResources(selectApiResources(connection, api.getId()));
                        apis.add(api);
                    }
                }

            } catch (SQLException exception) {
                LOGGER.error("Can't retrieve all API", exception);
            }

        } catch (Exception exception) {
            LOGGER.error("Error getting connection ", exception);
        }

        return apis;
    }

    private Map<String, String> selectApiMetadata(Connection connection, String idAPI) {

        try (PreparedStatement selectStatement =
                     connection.prepareStatement(SqlRegistryConstants.selectApiMetadataSql)) {
            selectStatement.setString(1, idAPI);
            ResultSet rs = selectStatement.executeQuery();

            if (rs.getFetchSize() > 0) {
                Map<String, String> metadatas = new HashMap<>();
                while (rs.next()) {
                    metadatas.put(rs.getString("key"),rs.getString("key"));
                }
                return metadatas;
            }
        } catch (SQLException exception) {
            LOGGER.error("Can't find metadata for API with id {}", idAPI, exception);
        }
        return null;
    }

    private Collection<Resource> selectApiResources(Connection connection, String idAPI) {

        try (PreparedStatement selectStatement =
                     connection.prepareStatement(SqlRegistryConstants.selectApiResourcesSql)) {
            selectStatement.setString(1, idAPI);
            ResultSet rs = selectStatement.executeQuery();

            if (rs.getFetchSize() > 0) {
                Collection<Resource> resources = new ArrayList<>();
                while (rs.next()) {
                    Resource resource = new Resource();
                    resource.setMethod(rs.getString("method"));
                    resource.setPath(rs.getString("path"));
                    resource.setBridge(rs.getString("bridge"));
                    resource.setUseBridge(rs.getBoolean("use_bridge"));
                    resource.setResponse(rs.getClob("response").toString());
                    resources.add(resource);
                }
                return resources;
            }
        } catch (SQLException exception) {
            LOGGER.error("Can't find metadata for API with id {}", idAPI, exception);
        }
        return null;
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
    public List<DataFormat> getDataFormats() {
        
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
