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
package org.apache.karaf.vineyard.registry.resource.rest.entity;

import java.io.Serializable;
import java.util.Collection;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

/**
 * A regular JPA entity representing Policy.
 */
@Entity
@Table(name = "REST_RESOURCE", schema = "VINEYARD")
public class RestResourceEntity implements Serializable {

    @Id
    private String id;

    @OneToMany(mappedBy="policy", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private Collection<RestPolicyEntity> policies;

    public String getId() {
    return id;
    }

    public void setId(String id) {
    this.id = id;
    }

    public Collection<RestPolicyEntity> getPolicies() {
        return policies;
    }

    public void setPolicies(Collection<RestPolicyEntity> policies) {
        this.policies = policies;
    }

}
