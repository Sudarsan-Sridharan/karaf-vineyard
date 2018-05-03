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

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.apache.karaf.vineyard.common.DataFormat;
import org.apache.karaf.vineyard.common.Endpoint;
import org.apache.karaf.vineyard.common.Environment;
import org.apache.karaf.vineyard.common.Maintainer;
import org.apache.karaf.vineyard.common.Role;
import org.apache.karaf.vineyard.common.Service;
import org.apache.karaf.vineyard.common.Registration;
import org.apache.karaf.vineyard.registry.api.RegistryService;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
    public void add(Service service) {
        try (Connection connection = dataSource.getConnection()) {
            
            if (connection.getAutoCommit()) {
                connection.setAutoCommit(false);
            }
            
            try (PreparedStatement insertStatement = 
                    connection.prepareStatement(SqlRegistryConstants.insertServiceSql, PreparedStatement.RETURN_GENERATED_KEYS)) {
                    // set values
                    insertStatement.setString(1, service.getName());
                    insertStatement.setString(2, service.getDescription());
                    insertStatement.executeUpdate();
                    
                    int newId = 0;
                    ResultSet rs = insertStatement.getGeneratedKeys();
                    
                    if (rs.next()) {
                        newId = rs.getInt(1);
                    }
                    
                    connection.commit();
                    
                    service.setId(String.valueOf(newId));
                    LOGGER.debug("Service created with id = {}", newId);
            
            } catch (SQLException exception) {
                connection.rollback();
                LOGGER.error("Can't insert service with name {}", service.getName(), exception);
            }
            
        } catch (Exception exception) {
            LOGGER.error("Error getting connection ", exception);
        }
    }

    @Override
    public void delete(Service service) {
        try (Connection connection = dataSource.getConnection()) {
            
            if (connection.getAutoCommit()) {
                connection.setAutoCommit(false);
            }
            
            try (PreparedStatement deleteStatement = 
                    connection.prepareStatement(SqlRegistryConstants.deleteServiceSql)) {
                    // where values
                    deleteStatement.setString(1, service.getId());
                    deleteStatement.executeUpdate();
                    deleteExtraDataForService(connection, service);
                    connection.commit();
                    LOGGER.debug("Service deleted with id = {}", service.getId());
            } catch (SQLException exception) {
                connection.rollback();
                LOGGER.error("Can't delete service with name {}", service.getId(), exception);
            }
            
        } catch (Exception exception) {
            LOGGER.error("Error getting connection ", exception);
        }
    }

    @Override
    public void delete(String id) {
        Service service = get(id);
        if (service != null) {
            delete(service);
        }
    }
    
    private void deleteExtraDataForService(Connection connection, Service service) throws SQLException {
        String sqlQuery = SqlRegistryConstants.deleteMetadataRegistrationSql + " where id_service = ?";
        
        try (PreparedStatement deleteStatement = connection.prepareStatement(sqlQuery)) {
            // where values
            deleteStatement.setString(1, service.getId());
            deleteStatement.executeUpdate();
            
            LOGGER.debug("Service updated with id = {}", service.getId());
        } catch (SQLException exception) {
            LOGGER.error("Can't udpate service with name {}", service.getName(), exception);
            throw exception;
        }
        
        sqlQuery = SqlRegistryConstants.deleteRegistrationSql + " where id = ?";
        
        try (PreparedStatement deleteStatement = connection.prepareStatement(sqlQuery)) {
            // where values
            deleteStatement.setString(1, service.getId());
            deleteStatement.executeUpdate();
            
            LOGGER.debug("Service updated with id = {}", service.getId());
        } catch (SQLException exception) {
            LOGGER.error("Can't udpate service with name {}", service.getName(), exception);
            throw exception;
        }
    }

    @Override
    public void update(Service service) {
        try (Connection connection = dataSource.getConnection()) {
            
            if (connection.getAutoCommit()) {
                connection.setAutoCommit(false);
            }
            
            try (PreparedStatement updateStatement = 
                    connection.prepareStatement(SqlRegistryConstants.updateServiceSql)) {
                    // set values
                    updateStatement.setString(1, service.getName());
                    updateStatement.setString(2, service.getDescription());
                    // where values
                    updateStatement.setString(3, service.getId());
                    updateStatement.executeUpdate();
                    
                    updateExtraDataForService(connection, service);
                    connection.commit();
                    LOGGER.debug("Service updated with id = {}", service.getId());
            } catch (SQLException exception) {
                connection.rollback();
                LOGGER.error("Can't udpate service with name {}", service.getName(), exception);
            }
            
        } catch (Exception exception) {
            LOGGER.error("Error getting connection ", exception);
        }
    }
    
    private void updateExtraDataForService(Connection connection, Service service) throws SQLException {
        
        deleteExtraDataForService(connection, service);
        
        for (Registration registration : service.getRegistrations()) {
            try (PreparedStatement insertStatement = 
                    connection.prepareStatement(SqlRegistryConstants.insertRegistrationSql)) {
                // set values
                insertStatement.setString(1, service.getId());
                insertStatement.setString(2, registration.getEnvironment().getId());
                insertStatement.setString(3, registration.getState());
                insertStatement.setString(4, registration.getVersion());
                insertStatement.setString(5, registration.getEndpoint().getLocation());
                insertStatement.setString(6, registration.getGateway().getLocation());
                insertStatement.setLong(7, registration.getThrottling());
                insertStatement.executeUpdate();
                
                LOGGER.debug("Service updated with id = {}", service.getId());
            } catch (SQLException exception) {
                LOGGER.error("Can't udpate service with name {}", service.getName(), exception);
                throw exception;
            }
            
            for (String metadataKey : registration.getMetadata().keySet()) {
                try (PreparedStatement insertStatement = 
                        connection.prepareStatement(SqlRegistryConstants.insertMetadataRegistrationSql)) {
                    // set values
                    insertStatement.setString(1, registration.getId());
                    insertStatement.setString(2, metadataKey);
                    insertStatement.setString(3, registration.getMetadata().get(metadataKey));
                    insertStatement.executeUpdate();
                    
                    LOGGER.debug("Service updated with id = {}", service.getId());
                } catch (SQLException exception) {
                    LOGGER.error("Can't udpate service with name {}", service.getName(), exception);
                    throw exception;
                }
            }
        }
    }

    @Override
    public Service get(String id) {
        
        Service service = null;
        try (Connection connection = dataSource.getConnection()) {
            
            String sqlQuery = SqlRegistryConstants.selectServiceSql + " where id = ?";
            try (PreparedStatement selectStatement = connection.prepareStatement(sqlQuery)) {
                selectStatement.setString(1, id);
                ResultSet rs = selectStatement.executeQuery();
                
                if (rs.next()) {
                    service = new Service();
                    service.setId(rs.getString("id"));
                    service.setName(rs.getString("name"));
                    service.setDescription(rs.getString("description"));
                }
            
            } catch (SQLException exception) {
                LOGGER.error("Can't find service with id {}", id, exception);
            }
            
            sqlQuery = SqlRegistryConstants.selectRegistrationSql;
            try (PreparedStatement selectStatement = connection.prepareStatement(sqlQuery)) {
                selectStatement.setString(1, id);
                ResultSet rs = selectStatement.executeQuery();
                
                if (rs.getFetchSize() > 0) {
                    service.setRegistrations(new ArrayList<>());
                    if (rs.next()) {
                        Environment environment = selectEnvironment(connection, rs.getString("id"));
                        
                        if (environment != null) {
                            Registration registration = new Registration();
                            registration.setEnvironment(environment);
                            registration.setState(rs.getString("state"));
                            registration.setVersion(rs.getString("version"));
                            registration.setEndpoint(selectEndpoint(connection, rs.getString("endpoint")));
                            registration.setGateway(selectEndpoint(connection, rs.getString("gateway")));
                            registration.setThrottling(rs.getLong("throttling"));
                            registration.setMetadata(selectMetadata(connection, registration.getId()));
                            // TODO populate maintainers registration.maintainers
                            // TODO populate policies registration.policies
                            service.getRegistrations().add(registration);
                        }
                    }
                }
        
            } catch (SQLException exception) {
                LOGGER.error("Can't find service with id {}", id, exception);
            }
            
        } catch (Exception exception) {
            LOGGER.error("Error getting connection ", exception);
        }
        return service;
    }
    
    private Map<String, String> selectMetadata(Connection connection, String idRegistration) {
        
        try (PreparedStatement selectStatement = 
                connection.prepareStatement(SqlRegistryConstants.selectMetadataRegistrationSql)) {
            selectStatement.setString(1, idRegistration);
            ResultSet rs = selectStatement.executeQuery();
            
            if (rs.getFetchSize() > 0) {
                Map<String, String> metadatas = new HashMap<>();
                while (rs.next()) {
                    metadatas.put(rs.getString("key"),rs.getString("key"));
                }
                return metadatas;
            }
        } catch (SQLException exception) {
            LOGGER.error("Can't find metadata for service with id {}", idRegistration, exception);
        }
        return null;
    }
    
    @Override
    public List<Service> getAll() {
        
        List<Service> services = new ArrayList<>();
        
        try (Connection connection = dataSource.getConnection()) {
            
            try (PreparedStatement selectStatement = connection.prepareStatement(SqlRegistryConstants.selectServiceSql)) {
                    ResultSet rs = selectStatement.executeQuery();
                    
                    while (rs.next()) {
                        Service service = new Service();
                        service.setId(rs.getString("id"));
                        service.setName(rs.getString("name"));
                        service.setDescription(rs.getString("description"));
                        services.add(service);
                        // TODO get extra content
                    }
            
            } catch (SQLException exception) {
                LOGGER.error("Can't retreive the services", exception);
            }
            
        } catch (Exception exception) {
            LOGGER.error("Error getting connection ", exception);
        }
        return services;
    }

    @Override
    public void addEnvironment(Environment environment) {
        try (Connection connection = dataSource.getConnection()) {
            
            if (connection.getAutoCommit()) {
                connection.setAutoCommit(false);
            }
            
            try (PreparedStatement insertStatement = 
                    connection.prepareStatement(SqlRegistryConstants.insertEnvironmentSql, PreparedStatement.RETURN_GENERATED_KEYS)) {
                // set values
                insertStatement.setString(1, environment.getName());
                insertStatement.setString(2, environment.getDescription());
                insertStatement.setString(3, environment.getScope());
                insertStatement.executeUpdate();
                
                int newId = 0;
                ResultSet rs = insertStatement.getGeneratedKeys();
                
                if (rs.next()) {
                    newId = rs.getInt(1);
                }
                
                connection.commit();
                
                environment.setId(String.valueOf(newId));
                LOGGER.debug("Environment created with id = {}", newId);
            
            } catch (SQLException exception) {
                connection.rollback();
                LOGGER.error("Can't insert environment with name {}", environment.getName(), exception);
            }
            
        } catch (Exception exception) {
            LOGGER.error("Error getting connection ", exception);
        }
    }

    @Override
    public void deleteEnvironment(Environment environment) {
        deleteEnvironment(environment.getId());
    }

    @Override
    public void deleteEnvironment(String id) {
        try (Connection connection = dataSource.getConnection()) {
            
            if (connection.getAutoCommit()) {
                connection.setAutoCommit(false);
            }

            String sqlQuery = SqlRegistryConstants.deleteMetadataRegistrationSql;
            
            try (PreparedStatement deleteStatement = 
                    connection.prepareStatement(sqlQuery)) {
                // where values
                deleteStatement.setString(1, id);
                deleteStatement.executeUpdate();
                
                LOGGER.debug("Environment metadata registration deleted with id = {}", id);
            } catch (SQLException exception) {
                connection.rollback();
                LOGGER.error("Can't delete environment metadata registration with id {}", id, exception);
                throw exception;
            }
            
            sqlQuery = SqlRegistryConstants.deleteRegistrationSql;
            
            try (PreparedStatement deleteStatement = 
                    connection.prepareStatement(sqlQuery)) {
                // where values
                deleteStatement.setString(1, id);
                deleteStatement.executeUpdate();
                
                LOGGER.debug("Environment registration deleted with id = {}", id);
            } catch (SQLException exception) {
                connection.rollback();
                LOGGER.error("Can't delete environment registration with id {}", id, exception);
                throw exception;
            }
            
            try (PreparedStatement deleteStatement = 
                    connection.prepareStatement(SqlRegistryConstants.deleteEnvironmentSql)) {
                // where values
                deleteStatement.setString(1, id);
                deleteStatement.executeUpdate();
                
                LOGGER.debug("Environment deleted with id = {}", id);
            } catch (SQLException exception) {
                connection.rollback();
                LOGGER.error("Can't delete environment with id {}", id, exception);
                throw exception;
            }
            
            connection.commit();
            
        } catch (Exception exception) {
            LOGGER.error("Error when deleting environment", exception);
        }
    }

    @Override
    public void updateEnvironment(Environment environment) {
        try (Connection connection = dataSource.getConnection()) {
            
            if (connection.getAutoCommit()) {
                connection.setAutoCommit(false);
            }
            
            try (PreparedStatement updateStatement = 
                    connection.prepareStatement(SqlRegistryConstants.updateEnvironmentSql)) {
                // set values
                updateStatement.setString(1, environment.getName());
                updateStatement.setString(2, environment.getDescription());
                updateStatement.setString(3, environment.getScope());
                // where values
                updateStatement.setString(4, environment.getId());
                updateStatement.executeUpdate();
                
                updateMaintainerForEnvironment(connection, environment);
                
                connection.commit();
                LOGGER.debug("Environment updated with id = {}", environment.getId());
            } catch (SQLException exception) {
                connection.rollback();
                LOGGER.error("Can't udpate environment with name {}", environment.getName(), exception);
            }
            
        } catch (Exception exception) {
            LOGGER.error("Error getting connection ", exception);
        }
    }
    
    private void updateMaintainerForEnvironment(Connection connection, Environment environment) throws SQLException {
        
        String sqlQuery = SqlRegistryConstants.deleteMaintainerForEnvironmentSql + " where id_environment = ?";
        
        try (PreparedStatement deleteStatement = 
                    connection.prepareStatement(sqlQuery)) {
            // where values
            deleteStatement.setString(1, environment.getId());
            deleteStatement.executeUpdate();
            
            LOGGER.debug("Environment updated with id = {}", environment.getId());
        } catch (SQLException exception) {
            LOGGER.error("Can't udpate environment with name {}", environment.getName(), exception);
            throw exception;
        }
        
        for (String maintainer : environment.getMaintainers().keySet()) {
            try (PreparedStatement insertStatement = 
                    connection.prepareStatement(SqlRegistryConstants.insertMaintainerForEnvironmentSql)) {
                // set values
                insertStatement.setString(1, environment.getId());
                insertStatement.setString(2, maintainer);
                insertStatement.setString(3, environment.getMaintainers().get(maintainer).name());
                insertStatement.executeUpdate();
                
                LOGGER.debug("Environment updated with id = {}", environment.getId());
            } catch (SQLException exception) {
                LOGGER.error("Can't udpate environment with name {}", environment.getName(), exception);
                throw exception;
            }
        }
    }

    @Override
    public Environment getEnvironment(String id) {
        
        try (Connection connection = dataSource.getConnection()) {
            return selectEnvironment(connection, id);
            
        } catch (Exception exception) {
            LOGGER.error("Error getting connection ", exception);
        }
        return null;
    }
    
    private Environment selectEnvironment(Connection connection, String id) {
        Environment environment = null;
        String sqlQuery = SqlRegistryConstants.selectEnvironmentSql + " where id = ?";
        
        try (PreparedStatement selectStatement = connection.prepareStatement(sqlQuery)) {
                selectStatement.setString(1, id);
            ResultSet rs = selectStatement.executeQuery();
            
            if (rs.next()) {
                environment = new Environment();
                environment.setId(id);
                environment.setName(rs.getString("name"));
                environment.setDescription(rs.getString("description"));
                environment.setScope(rs.getString("scope"));
            }
        
        } catch (SQLException exception) {
            LOGGER.error("Can't find environment with id {}", id, exception);
        }
        
        if (environment != null) {
            sqlQuery = SqlRegistryConstants.selectMaintainerForEnvironmentSql;
            try (PreparedStatement selectStatement = connection.prepareStatement(sqlQuery)) {
                selectStatement.setString(1, id);
                ResultSet rs = selectStatement.executeQuery();
                
                if (rs.getFetchSize() > 0) {
                    environment.setMaintainers(new HashMap<>());
                    if (rs.next()) {
                        Maintainer maintainer = new Maintainer();
                        maintainer.setName(rs.getString("name"));
                        maintainer.setEmail(rs.getString("email"));
                        maintainer.setTeam(rs.getString("team"));
                        environment.getMaintainers().put(maintainer.getName(), Role.valueOf(rs.getString("role")));
                    }
                }
        
            } catch (SQLException exception) {
                LOGGER.error("Can't find environment with id {}", id, exception);
            }
        }
        return environment;
    }

    @Override
    public List<Environment> getAllEnvironments() {
        List<Environment> environments = new ArrayList<>();
        
        try (Connection connection = dataSource.getConnection()) {
            
            try (PreparedStatement selectStatement = connection.prepareStatement(SqlRegistryConstants.selectEnvironmentSql)) {
                ResultSet rs = selectStatement.executeQuery();
                
                while (rs.next()) {
                    Environment environment = new Environment();
                    environment.setId(rs.getString("id"));
                    environment.setName(rs.getString("name"));
                    environment.setDescription(rs.getString("description"));
                    environment.setScope(rs.getString("scope"));
                    environments.add(environment);
                    // TODO get extra content
                }
            
            } catch (SQLException exception) {
                LOGGER.error("Can't retreive the environments", exception);
            }
            
        } catch (Exception exception) {
            LOGGER.error("Error getting connection ", exception);
        }
        return environments;
    }

    @Override
    public void addMaintainer(Maintainer maintainer) {
        try (Connection connection = dataSource.getConnection()) {
            
            if (connection.getAutoCommit()) {
                connection.setAutoCommit(false);
            }
            
            try (PreparedStatement insertStatement = 
                    connection.prepareStatement(SqlRegistryConstants.insertMaintainerSql)) {
                // replace space caracters for URI encoding
                maintainer.setName(maintainer.getName().replace(" ","-"));
                // set values
                insertStatement.setString(1, maintainer.getName());
                insertStatement.setString(2, maintainer.getEmail());
                insertStatement.setString(3, maintainer.getTeam());
                insertStatement.executeUpdate();
                
                connection.commit();
                LOGGER.debug("Maintainer created with name = {}", maintainer.getName());
            
            } catch (SQLException exception) {
                connection.rollback();
                LOGGER.error("Can't insert maintainer with name {}", maintainer.getName(), exception);
            }
            
        } catch (Exception exception) {
            LOGGER.error("Error getting connection ", exception);
        }
    }

    @Override
    public void deleteMaintainer(Maintainer maintainer) {
        deleteMaintainer(maintainer.getName());
    }

    @Override
    public void deleteMaintainer(String name) {
        try (Connection connection = dataSource.getConnection()) {
            
            if (connection.getAutoCommit()) {
                connection.setAutoCommit(false);
            }
            
            String sqlQuery = SqlRegistryConstants.deleteMaintainerForEnvironmentSql +
                    "where name_maintainer = ?";
            
            try (PreparedStatement deleteStatement = 
                    connection.prepareStatement(sqlQuery)) {
                // where values
                deleteStatement.setString(1, name);
                deleteStatement.executeUpdate();
                
                LOGGER.debug("Maintainer deleted with name = {}", name);
            } catch (SQLException exception) {
                connection.rollback();
                LOGGER.error("Can't delete maintainer with name {}", name, exception);
                throw exception;
            }
            
            try (PreparedStatement deleteStatement = 
                    connection.prepareStatement(SqlRegistryConstants.deleteMaintainerSql)) {
                // where values
                deleteStatement.setString(1, name);
                deleteStatement.executeUpdate();
                
                LOGGER.debug("Maintainer deleted with name = {}", name);
            } catch (SQLException exception) {
                connection.rollback();
                LOGGER.error("Can't delete maintainer with name {}", name, exception);
                throw exception;
            }
            
            connection.commit();
            
        } catch (Exception exception) {
            LOGGER.error("Error when deleting maintainer", exception);
        }
    }

    @Override
    public void updateMaintainer(Maintainer maintainer) {
        try (Connection connection = dataSource.getConnection()) {
            
            if (connection.getAutoCommit()) {
                connection.setAutoCommit(false);
            }
            
            try (PreparedStatement updateStatement = 
                    connection.prepareStatement(SqlRegistryConstants.updateMaintainerSql)) {
                // set values
                updateStatement.setString(1, maintainer.getEmail());
                updateStatement.setString(2, maintainer.getTeam());
                // where values
                updateStatement.setString(3, maintainer.getName());
                updateStatement.executeUpdate();
                connection.commit();
                LOGGER.debug("Maintainer updated with name = {}", maintainer.getName());
            } catch (SQLException exception) {
                connection.rollback();
                LOGGER.error("Can't udpate maintainer with name {}", maintainer.getName(), exception);
            }
            
        } catch (Exception exception) {
            LOGGER.error("Error getting connection ", exception);
        }
    }

    @Override
    public Maintainer getMaintainer(String name) {
        try (Connection connection = dataSource.getConnection()) {
            
            String sqlQuery = SqlRegistryConstants.selectMaintainerSql + " where name = ?";
            
            try (PreparedStatement selectStatement = connection.prepareStatement(sqlQuery)) {
                selectStatement.setString(1, name);
                ResultSet rs = selectStatement.executeQuery();
                
                if (rs.next()) {
                    Maintainer maintainer = new Maintainer();
                    maintainer.setName(name);
                    maintainer.setEmail(rs.getString("email"));
                    maintainer.setTeam(rs.getString("team"));
                    return maintainer;
                }
            
            } catch (SQLException exception) {
                LOGGER.error("Can't find maintainer with name {}", name, exception);
            }
            
        } catch (Exception exception) {
            LOGGER.error("Error getting connection ", exception);
        }
        return null;
    }

    @Override
    public List<Maintainer> getAllMaintainers() {
        List<Maintainer> maintainers = new ArrayList<>();
        
        try (Connection connection = dataSource.getConnection()) {
            
            try (PreparedStatement selectStatement = connection.prepareStatement(SqlRegistryConstants.selectMaintainerSql)) {
                ResultSet rs = selectStatement.executeQuery();
                
                while (rs.next()) {
                    Maintainer maintainer = new Maintainer();
                    maintainer.setName(rs.getString("name"));
                    maintainer.setEmail(rs.getString("email"));
                    maintainer.setTeam(rs.getString("team"));
                    maintainers.add(maintainer);
                }
            
            } catch (SQLException exception) {
                LOGGER.error("Can't retreive the maintainers", exception);
            }
            
        } catch (Exception exception) {
            LOGGER.error("Error getting connection ", exception);
        }
        return maintainers;
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

    @Override
    public void addEndpoint(Endpoint endpoint) {
        try (Connection connection = dataSource.getConnection()) {
            
            if (connection.getAutoCommit()) {
                connection.setAutoCommit(false);
            }
            
            try (PreparedStatement insertStatement = 
                    connection.prepareStatement(SqlRegistryConstants.insertEndpointSql)) {
                // set values
                insertStatement.setString(1, endpoint.getLocation());
                insertStatement.setString(2, endpoint.getInput().getId());
                insertStatement.setString(3, endpoint.getOutput().getId());
                insertStatement.executeUpdate();
                connection.commit();
                
                LOGGER.debug("Endpoint created with location = {}", endpoint.getLocation());
            
            } catch (SQLException exception) {
                connection.rollback();
                LOGGER.error("Can't insert endpoint with location {}", endpoint.getLocation(), exception);
            }
            
        } catch (Exception exception) {
            LOGGER.error("Error getting connection ", exception);
        }
    }

    @Override
    public void deleteEndpoint(Endpoint endpoint) {
        deleteEndpoint(endpoint.getId());
    }

    @Override
    public void deleteEndpoint(String id) {
        try (Connection connection = dataSource.getConnection()) {
            
            if (connection.getAutoCommit()) {
                connection.setAutoCommit(false);
            }
            
            try (PreparedStatement deleteStatement = 
                    connection.prepareStatement(SqlRegistryConstants.deleteEndpointSql)) {
                // where values
                deleteStatement.setString(1, id);
                deleteStatement.executeUpdate();
                connection.commit();
                LOGGER.debug("Endpoint deleted with id = {}", id);
            } catch (SQLException exception) {
                connection.rollback();
                LOGGER.error("Can't delete endpoint with id {}", id, exception);
                throw exception;
            }
            
        } catch (Exception exception) {
            LOGGER.error("Error when deleting endpoint", exception);
        }
    }

    @Override
    public void updateEndpoint(Endpoint endpoint) {
        try (Connection connection = dataSource.getConnection()) {
            
            if (connection.getAutoCommit()) {
                connection.setAutoCommit(false);
            }
            
            try (PreparedStatement updateStatement = 
                    connection.prepareStatement(SqlRegistryConstants.updateEndpointSql)) {
                // set values
                updateStatement.setString(1, endpoint.getLocation());
                updateStatement.setString(2, endpoint.getInput().getId());
                updateStatement.setString(3, endpoint.getOutput().getId());
                // where values
                updateStatement.setString(4, endpoint.getId());
                updateStatement.executeUpdate();
                connection.commit();
                LOGGER.debug("Endpoint updated with id = {}", endpoint.getId());
            } catch (SQLException exception) {
                connection.rollback();
                LOGGER.error("Can't udpate endpoint with id {}", endpoint.getId(), exception);
            }
            
        } catch (Exception exception) {
            LOGGER.error("Error getting connection ", exception);
        }
        
    }

    @Override
    public Endpoint getEndpoint(String id) {
        try (Connection connection = dataSource.getConnection()) {
            return selectEndpoint(connection, id);
        } catch (Exception exception) {
            LOGGER.error("Error getting connection ", exception);
        }
        return null;
    }
    
    private Endpoint selectEndpoint(Connection connection, String id) {
        String sqlQuery = SqlRegistryConstants.selectEndpointSql + " where id = ?";
        
        try (PreparedStatement selectStatement = connection.prepareStatement(sqlQuery)) {
            selectStatement.setString(1, id);
            ResultSet rs = selectStatement.executeQuery();
            
            if (rs.next()) {
                Endpoint endpoint = new Endpoint();
                endpoint.setId(id);
                endpoint.setLocation(rs.getString("location"));
                int inputId = rs.getInt("input");
                int outputId = rs.getInt("output");
                if (inputId != 0) {
                    endpoint.setInput(selectDataFormat(connection, String.valueOf(inputId)));
                }
                if (outputId != 0) {
                    endpoint.setOutput(selectDataFormat(connection, String.valueOf(outputId)));
                }
                return endpoint;
            }
        
        } catch (SQLException exception) {
            LOGGER.error("Can't find endpoint with id {}", id, exception);
        }
        return null;
    }

    @Override
    public List<Endpoint> getAllEndpoints() {
        List<Endpoint> endpoints = new ArrayList<>();
        
        try (Connection connection = dataSource.getConnection()) {
            
            try (PreparedStatement selectStatement = connection.prepareStatement(SqlRegistryConstants.selectServiceSql)) {
                ResultSet rs = selectStatement.executeQuery();
                
                while (rs.next()) {
                    Endpoint endpoint = new Endpoint();
                    endpoint.setId(rs.getString("id"));
                    endpoint.setLocation(rs.getString("location"));
                    int inputId = rs.getInt("input");
                    int outputId = rs.getInt("output");
                    if (inputId != 0) {
                        endpoint.setInput(selectDataFormat(connection, String.valueOf(inputId)));
                    }
                    if (outputId != 0) {
                        endpoint.setOutput(selectDataFormat(connection, String.valueOf(outputId)));
                    }
                    endpoints.add(endpoint);
                }
            
            } catch (SQLException exception) {
                LOGGER.error("Can't retreive the endpoints", exception);
            }
            
        } catch (Exception exception) {
            LOGGER.error("Error getting connection ", exception);
        }
        return endpoints;
    }


}
