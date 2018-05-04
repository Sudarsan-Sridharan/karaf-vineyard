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
public class VineyardRegistryRestTest extends VineyardTestSupport {

    private static final RolePrincipal[] ADMIN_ROLES = {
            new RolePrincipal("admin"),
            new RolePrincipal("manager")
    };

    @Test
    public void testVineyardRegistryFeatureInstall() throws InterruptedException {
        installVineyard();
        Thread.sleep(DEFAULT_TIMEOUT);
        System.out.println(executeCommand("feature:install vineyard-registry", ADMIN_ROLES));
        Thread.sleep(DEFAULT_TIMEOUT);

        try {
            String URL = "http://localhost:8181/cxf/vineyard/registry/maintainer";
            URL urlGetListServices = new URL(URL);

            // Call add maintainer service
            String jsonAddMaintainer = "{\n" +
                    "  \"name\": \"obiwan kenobi\",\n" +
                    "  \"email\": \"okenobi@jedi.org\",\n" +
                    "  \"team\": \"master jedi\"\n" +
                    "}";
            System.out.println("Call POST http://localhost:8181/cxf/vineyard/registry/maintainer");
            HttpURLConnection connection = (HttpURLConnection) urlGetListServices.openConnection();
            connection.setRequestMethod(HttpMethod.POST);
            connection.setDoOutput(true);
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            OutputStream os = connection.getOutputStream();
            os.write(jsonAddMaintainer.getBytes());
            os.flush();

            if (connection.getResponseCode() == HttpURLConnection.HTTP_CREATED) {
                Assert.assertTrue(true);
            } else {
                System.out.println("Error when sending POST method : HTTP_CODE = " + connection.getResponseCode());
                Assert.assertTrue(false);
            }

            // Call list service
            System.out.println("Call GET http://localhost:8181/cxf/vineyard/registry/maintainer");
            connection = (HttpURLConnection) urlGetListServices.openConnection();
            connection.setRequestMethod(HttpMethod.GET);
            connection.connect();

            if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                BufferedReader buffer = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String line;
                StringBuffer sb = new StringBuffer();
                while ((line = buffer.readLine()) != null) {
                    sb.append(line);
                }
                if (sb.length() == 0) {
                    System.out.println("Maintainer list is empty");
                    Assert.assertTrue(false);
                } else {
                    System.out.println(sb.toString());
                    Assert.assertTrue(true);
                }
            } else {
                System.out.println("Error when sending GET method : HTTP_CODE = " + connection.getResponseCode());
                Assert.assertTrue(false);
            }
            connection.disconnect();
        } catch (Exception e) {
            System.out.println(e.getMessage());
            Assert.assertTrue(false);
        }
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
