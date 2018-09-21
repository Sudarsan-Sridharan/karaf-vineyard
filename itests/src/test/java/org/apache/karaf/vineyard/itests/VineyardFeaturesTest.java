/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.apache.karaf.vineyard.itests;

import org.apache.karaf.itests.KarafTestSupport;
import org.apache.karaf.jaas.boot.principal.RolePrincipal;
import org.junit.After;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.ops4j.pax.exam.junit.PaxExam;
import org.ops4j.pax.exam.spi.reactors.ExamReactorStrategy;
import org.ops4j.pax.exam.spi.reactors.PerClass;

@RunWith(PaxExam.class)
@ExamReactorStrategy(PerClass.class)
public class VineyardFeaturesTest extends KarafTestSupport {

    private static final RolePrincipal[] ADMIN_ROLES = {
            new RolePrincipal("admin"),
            new RolePrincipal("manager")
    };

    @Test
    public void testVineyardRegistryFeatureInstall() throws Exception {
        // adding vineyard features repository
        addFeaturesRepository("mvn:org.apache.karaf.vineyard/apache-karaf-vineyard/1.0.0-SNAPSHOT/xml");

        String featureList = executeCommand("feature:list");
        System.out.println(featureList);

        executeCommand("feature:install vineyard-registry", ADMIN_ROLES);
        installAndAssertFeature("vineyard-registry");

        String bundleList = executeCommand("bundle:list");
        System.out.println(bundleList);
        Assert.assertTrue(bundleList.contains("Apache Karaf :: Vineyard :: Common"));
        Assert.assertTrue(bundleList.contains("Apache Karaf :: Vineyard :: Registry :: API"));
        Assert.assertTrue(bundleList.contains("Apache Karaf :: Vineyard :: Registry :: Commands"));
        Assert.assertTrue(bundleList.contains("Apache Karaf :: Vineyard :: Registry :: REST"));
        Assert.assertTrue(bundleList.contains("Apache Karaf :: Vineyard :: Registry :: Storage"));

        String jdbcList = executeCommand("jdbc:ds-list");
        System.out.println(jdbcList);
        Assert.assertTrue(jdbcList.contains("jdbc:derby:data/vineyard/derby │ OK"));
    }


    @After
    public void tearDown() {
        executeCommand("feature:uninstall vineyard-registry", ADMIN_ROLES);
    }

}
