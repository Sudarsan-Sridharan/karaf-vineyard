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
package org.apache.karaf.vineyard.registry.policy.entity;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.ManyToOne;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;

/**
 * A regular JPA entity, using JPA annotations.
 */
@IdClass(PolicyMetaPkEntity.class)
@Entity
@Table(name = "POLICY_META", schema = "VINEYARD")
public class PolicyMetaEntity implements Serializable {

	@Id
	@Column(name = "META_KEY")
	private String key;

	@Id
	@ManyToOne(fetch= FetchType.LAZY)
	@PrimaryKeyJoinColumn(name="BOX_ID")
	private PolicyEntity policy;

	@Column(name = "META_VALUE")
	private String value;

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
	    this.key = key;
	}

	public PolicyEntity getPolicy() {
		return policy;
	}

	public void setPolicy(PolicyEntity policy) {
		this.policy = policy;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

}
