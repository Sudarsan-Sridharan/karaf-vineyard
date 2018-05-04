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

import org.apache.karaf.jaas.boot.principal.RolePrincipal;
import org.junit.After;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.ops4j.pax.exam.junit.PaxExam;
import org.ops4j.pax.exam.spi.reactors.ExamReactorStrategy;
import org.ops4j.pax.exam.spi.reactors.PerClass;

import javax.ws.rs.HttpMethod;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

@RunWith(PaxExam.class)
@ExamReactorStrategy(PerClass.class)
public class VineyardRegistryCommandTest extends VineyardTestSupport {

    private static final RolePrincipal[] ADMIN_ROLES = {
            new RolePrincipal("admin"),
            new RolePrincipal("manager")
    };

    @Test
    public void testVineyardRegistryStorage() throws InterruptedException {
        installVineyard();
        Thread.sleep(DEFAULT_TIMEOUT);
        System.out.println(executeCommand("feature:install vineyard-registry", ADMIN_ROLES));
        Thread.sleep(DEFAULT_TIMEOUT);

        System.out.println(executeCommand("vineyard:services", ADMIN_ROLES));
        System.out.println(executeCommand("vineyard:service-add my-service service-for-test", ADMIN_ROLES));

        String serviceList = executeCommand("vineyard:services", ADMIN_ROLES);
        System.out.println(serviceList);
        Assert.assertTrue(serviceList.contains("my-service"));

        System.out.println(executeCommand("vineyard:service-delete 1", ADMIN_ROLES));
        serviceList = executeCommand("vineyard:services", ADMIN_ROLES);
        System.out.println(serviceList);
        Assert.assertFalse(serviceList.contains("my-service"));
    }


    @After
    public void tearDown() {
        try {
            unInstallVineyard();
        } catch (Exception ex) {
            //Ignore
        }
    }

}
