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
package org.apache.karaf.vineyard.importer.json;

import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import org.apache.johnzon.mapper.MapperBuilder;
import org.junit.Assert;
import org.junit.Test;

public class TestJsonImporter {

    @Test
    public void testRead() throws Exception {
        URL url = this.getClass().getResource("/registry.json");
        Assert.assertNotNull(url);
        URLConnection connection = url.openConnection();
        InputStream inputStream = connection.getInputStream();
        Assert.assertNotNull(inputStream);
        JsonRegistry registry =
                new MapperBuilder().build().readObject(inputStream, JsonRegistry.class);
        Assert.assertFalse("List api is empty", registry.getApis().isEmpty());
        Assert.assertFalse("List policy is empty", registry.getPolicies().isEmpty());

        System.out.println(
                "Apis: "
                        + registry.getApis().size()
                        + " | "
                        + "Policies: "
                        + registry.getPolicies().size());
    }
}
