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

import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.InetAddress;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayDeque;
import java.util.Collection;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import org.apache.aries.jpa.supplier.EmSupplier;
import org.apache.aries.jpa.support.impl.ResourceLocalJpaTemplate;
import org.apache.aries.jpa.template.JpaTemplate;
import org.apache.derby.drda.NetworkServerControl;
import org.apache.derby.jdbc.ClientDataSource;
import org.apache.karaf.vineyard.common.API;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.osgi.framework.Bundle;
import org.osgi.service.coordinator.Coordination;
import org.osgi.service.coordinator.Coordinator;
import org.osgi.service.coordinator.Participant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RegistryServiceImplTest {

    private static final Logger LOG = LoggerFactory.getLogger(RegistryServiceImplTest.class);

    private static NetworkServerControl derbyServer;

    private RegistryServiceImpl registryService;

    @BeforeClass
    public static void beforeClass() throws Exception {
        LOG.info("Starting Derby database");

        System.setProperty("derby.locks.waitTimeout", "2");
        System.setProperty("derby.stream.error.file", "target/derby.log");

        derbyServer = new NetworkServerControl(InetAddress.getByName("localhost"), 9999);
        StringWriter out = new StringWriter();
        derbyServer.start(new PrintWriter(out));
        boolean started = false;
        int count = 0;
        while (!started && count < 30) {
            if (out.toString().contains("started")) {
                started = true;
            } else {
                count++;
                Thread.sleep(500);
                try {
                    derbyServer.ping();
                    started = true;
                } catch (Throwable t) {
                    // ignore, still trying to start
                }
            }
        }
    }

    @AfterClass
    public static void afterClass() throws Exception {
        if (derbyServer != null) {
            derbyServer.shutdown();
        }
    }

    @Before
    public void setup() {
        registryService = new RegistryServiceImpl();
        EntityManagerFactory emf = createTestEMF();
        EmSupplier emSupplier = createEmSupplier(emf);
        Coordinator coordinator = new DummyCoordinator();
        JpaTemplate jpaTemplate = new ResourceLocalJpaTemplate(emSupplier, coordinator);
        registryService.setJpaTemplate(jpaTemplate);
    }

    @Test
    public void testSimpleAddApi() throws Exception {
        API api = new API();
        api.setContext("/simple");
        api.setDescription("Simple API");
        api.setName("Simple");
        registryService.add(api);

        ClientDataSource dataSource = new ClientDataSource();
        dataSource.setDatabaseName("target/vineyard");
        dataSource.setServerName("localhost");
        dataSource.setPortNumber(9999);

        try (Connection connection = dataSource.getConnection()) {
            try (Statement statement = connection.createStatement()) {
                try (ResultSet resultSet = statement.executeQuery("select * from VINEYARD.API")) {
                    resultSet.next();
                    Assert.assertNotNull(resultSet.getString("ID"));
                    Assert.assertEquals(resultSet.getString("NAME"), "Simple");
                    Assert.assertEquals(resultSet.getString("DESCRIPTION"), "Simple API");
                    Assert.assertEquals(resultSet.getString("CONTEXT"), "/simple");
                    Assert.assertFalse(resultSet.next());
                }
            }
        }
    }

    private EmSupplier createEmSupplier(EntityManagerFactory emf) {
        final EntityManager em = emf.createEntityManager();
        EmSupplier emSupplier =
                new EmSupplier() {
                    @Override
                    public void preCall() {}

                    @Override
                    public EntityManager get() {
                        return em;
                    }

                    @Override
                    public void postCall() {}
                };
        return emSupplier;
    }

    private EntityManagerFactory createTestEMF() {
        Map<String, String> properties = new HashMap<>();
        properties.put("javax.persistence.jdbc.driver", "org.apache.derby.jdbc.ClientDriver");
        properties.put(
                "javax.persistence.jdbc.url",
                "jdbc:derby://localhost:9999/target/vineyard;create=true");
        EntityManagerFactory emf =
                Persistence.createEntityManagerFactory("vineyard-registry", properties);
        return emf;
    }

    class DummyCoordinator implements Coordinator {

        private Deque<Coordination> coordinations = new ArrayDeque<>();

        @Override
        public Coordination create(String name, long timeMillis) {
            throw new IllegalStateException();
        }

        @Override
        public Coordination begin(String name, long timeMillis) {
            Coordination oldCoordination = coordinations.peekLast();
            Coordination coordination = new DummyCoordination(oldCoordination);
            this.coordinations.push(coordination);
            return coordination;
        }

        @Override
        public Coordination peek() {
            return coordinations.peek();
        }

        @Override
        public Coordination pop() {
            return coordinations.pop();
        }

        @Override
        public boolean fail(Throwable cause) {
            return false;
        }

        @Override
        public boolean addParticipant(Participant participant) {
            return false;
        }

        @Override
        public Collection<Coordination> getCoordinations() {
            return null;
        }

        @Override
        public Coordination getCoordination(long id) {
            return null;
        }
    }

    class DummyCoordination implements Coordination {

        private Set<Participant> participants = new HashSet<>();
        private Map<Class<?>, Object> vars = new HashMap<>();
        private Coordination enclosing;

        public DummyCoordination(Coordination enclosing) {
            this.enclosing = enclosing;
        }

        @Override
        public long getId() {
            return 0;
        }

        @Override
        public String getName() {
            return null;
        }

        @Override
        public void end() {
            Iterator<Participant> it = participants.iterator();
            while (it.hasNext()) {
                try {
                    it.next().ended(this);
                } catch (Exception e) {
                    // nothing to do
                }
            }
        }

        @Override
        public boolean fail(Throwable cause) {
            return false;
        }

        @Override
        public Throwable getFailure() {
            return null;
        }

        @Override
        public boolean isTerminated() {
            return false;
        }

        @Override
        public void addParticipant(Participant participant) {
            this.participants.add(participant);
        }

        @Override
        public List<Participant> getParticipants() {
            return null;
        }

        @Override
        public Map<Class<?>, Object> getVariables() {
            return vars;
        }

        @Override
        public long extendTimeout(long timeMillis) {
            return 0;
        }

        @Override
        public void join(long timeMillis) throws InterruptedException {}

        @Override
        public Coordination push() {
            return null;
        }

        @Override
        public Thread getThread() {
            return null;
        }

        @Override
        public Bundle getBundle() {
            return null;
        }

        @Override
        public Coordination getEnclosingCoordination() {
            return enclosing;
        }
    }
}
