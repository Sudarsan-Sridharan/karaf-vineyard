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

/**
 * Represent a pk meta box.
 */
public class PolicyMetaPkEntity implements Serializable {

	private String key;
	private String box;

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getBox() {
		return box;
	}

	public void setBox(String box) {
		this.box = box;
	}

	public boolean equals(Object obj) {
		if (obj == this) return true;
		if (!(obj instanceof PolicyMetaPkEntity)) return false;
		PolicyMetaPkEntity pk = (PolicyMetaPkEntity) obj;
		return pk.key.equals(this.key) && pk.box.equals(this.box);
	}

}
