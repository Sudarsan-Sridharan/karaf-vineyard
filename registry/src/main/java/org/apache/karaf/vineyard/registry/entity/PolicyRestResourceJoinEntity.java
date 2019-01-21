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

import java.util.Hashtable;
import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;

@Entity
@Table(name = "POLICY_REST_RESOURCE_JOIN", schema = "VINEYARD")
public class PolicyRestResourceJoinEntity {

    @Column(name = "POLICY_ORDER")
    private int policyOrder;

    @Id
    @ManyToOne
    @PrimaryKeyJoinColumn(name = "REST_RESOURCE_ID", referencedColumnName = "ID")
    private RestResourceEntity restResource;

    @Id
    @ManyToOne
    @PrimaryKeyJoinColumn(name = "POLICY_ID", referencedColumnName = "ID")
    private PolicyEntity policy;

    @CollectionTable(name = "POLICY_REST_RESOURCE_JOIN_PARAM", schema = "VINEYARD")
    @ElementCollection(fetch = FetchType.LAZY)
    private Hashtable<String, String> param;

    public int getPolicyOrder() {
        return policyOrder;
    }

    public void setPolicyOrder(int policyOrder) {
        this.policyOrder = policyOrder;
    }

    public RestResourceEntity getRestResource() {
        return restResource;
    }

    public void setRestResource(RestResourceEntity restResource) {
        this.restResource = restResource;
    }

    public PolicyEntity getPolicy() {
        return policy;
    }

    public void setPolicy(PolicyEntity policy) {
        this.policy = policy;
    }

    public Hashtable<String, String> getParam() {
        return param;
    }

    public void setParam(Hashtable<String, String> param) {
        this.param = param;
    }

    public boolean equals(Object object) {
        if (object instanceof PolicyRestResourceJoinEntity) {
            PolicyRestResourceJoinEntity otherId = (PolicyRestResourceJoinEntity) object;
            return otherId.restResource.getId().equals(this.restResource.getId())
                    && otherId.policy.getId().equals(this.policy.getId());
        }
        return false;
    }
}
