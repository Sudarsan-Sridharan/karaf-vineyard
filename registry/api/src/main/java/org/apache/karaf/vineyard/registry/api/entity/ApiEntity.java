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
package org.apache.karaf.vineyard.registry.api.entity;

import java.io.Serializable;
import java.util.Collection;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.OneToMany;
import javax.persistence.Table;

/** A regular JPA entity representing API. */
@Entity
@Table(name = "API", schema = "VINEYARD")
public class ApiEntity implements Serializable {

    @Id private String id;

    private String name;

    private String context;

    private String description;

    @Lob
    @Basic(fetch = FetchType.LAZY)
    private byte[] definition;

    @OneToMany(mappedBy = "api", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private Collection<ApiMetaEntity> meta;

    @OneToMany(mappedBy = "api", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private Collection<ApiResourceEntity> resources;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getContext() {
        return context;
    }

    public void setContext(String context) {
        this.context = context;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public byte[] getDefinition() {
        return definition;
    }

    public void setDefinition(byte[] definition) {
        this.definition = definition;
    }

    public Collection<ApiMetaEntity> getMeta() {
        return meta;
    }

    public void setMeta(Collection<ApiMetaEntity> meta) {
        this.meta = meta;
    }

    public Collection<ApiResourceEntity> getResources() {
        return resources;
    }

    public void setResources(Collection<ApiResourceEntity> resources) {
        this.resources = resources;
    }
}
