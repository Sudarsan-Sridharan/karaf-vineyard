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
package org.apache.karaf.vineyard.registry.entity;

import java.io.Serializable;
import java.util.List;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Entity
@Table(name = "POLICY", schema = "VINEYARD")
public class PolicyEntity implements Serializable {

    @Id private String id;

    private String description;

    private String className;

    //    @ManyToMany(mappedBy = "policies")
    //    private Collection<RestResourceEntity> restResources;

    @OneToMany(mappedBy = "policy")
    private List<PolicyRestResourceJoin> policyRestResourceJoins;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public List<PolicyRestResourceJoin> getPolicyRestResourceJoins() {
        return policyRestResourceJoins;
    }

    public void setPolicyRestResourceJoins(List<PolicyRestResourceJoin> policyRestResourceJoins) {
        this.policyRestResourceJoins = policyRestResourceJoins;
    }
}
