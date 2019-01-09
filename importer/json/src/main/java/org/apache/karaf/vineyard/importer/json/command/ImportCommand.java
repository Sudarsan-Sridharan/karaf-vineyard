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
package org.apache.karaf.vineyard.importer.json.command;

import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import org.apache.karaf.shell.api.action.Action;
import org.apache.karaf.shell.api.action.Argument;
import org.apache.karaf.shell.api.action.Command;
import org.apache.karaf.shell.api.action.lifecycle.Reference;
import org.apache.karaf.shell.api.action.lifecycle.Service;
import org.apache.karaf.shell.support.table.ShellTable;
import org.apache.karaf.vineyard.common.RegistryService;
import org.apache.karaf.vineyard.common.Importer;
import org.apache.karaf.vineyard.common.PolicyRegistryService;
import org.apache.karaf.vineyard.common.ResourceRegistryService;

@Service
@Command(
        scope = "vineyard",
        name = "import-json",
        description = "Import a registry definition with json input file")
public class ImportCommand implements Action {

    @Reference private Importer jsonImporter;

    @Reference private RegistryService apiRegistryService;

    @Reference private PolicyRegistryService policyRegistryService;

    @Reference private ResourceRegistryService resourceRegistryService;

    @Argument(
            index = 0,
            name = "file",
            description = "Json file to import",
            required = true,
            multiValued = false)
    String file;

    @Override
    public Object execute() throws Exception {
        URL url = new URL(file);
        URLConnection connection = url.openConnection();
        InputStream inputStream = connection.getInputStream();
        jsonImporter.load(inputStream);
        final ShellTable shellTable = new ShellTable();
        shellTable.column("APIs");
        shellTable.column("Resources");
        shellTable.column("Policies");
        shellTable
                .addRow()
                .addContent(
                        apiRegistryService.list().size(),
                        resourceRegistryService.list().size(),
                        policyRegistryService.list().size());
        shellTable.print(System.out);
        return null;
    }
}
