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

import static org.junit.Assert.assertTrue;

import org.junit.After;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.ops4j.pax.exam.junit.PaxExam;
import org.ops4j.pax.exam.spi.reactors.ExamReactorStrategy;
import org.ops4j.pax.exam.spi.reactors.PerClass;

@RunWith(PaxExam.class)
@ExamReactorStrategy(PerClass.class)
public class VineyardFeaturesTest extends VineyardTestSupport {

    private static final String UNINSTALLED = "[uninstalled]";
    private static final String INSTALLED = "[installed  ]";

    @Test
    public void testVineyardFeaturesModule() throws InterruptedException {
        installVineyard();
        Thread.sleep(DEFAULT_TIMEOUT);
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