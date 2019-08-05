<!--
    Licensed to the Apache Software Foundation (ASF) under one
    or more contributor license agreements.  See the NOTICE file
    distributed with this work for additional information
    regarding copyright ownership.  The ASF licenses this file
    to you under the Apache License, Version 2.0 (the
    "License"); you may not use this file except in compliance
    with the License.  You may obtain a copy of the License at

      http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing,
    software distributed under the License is distributed on an
    "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
    KIND, either express or implied.  See the License for the
    specific language governing permissions and limitations
    under the License.
-->

# Apache Karaf Vineyard

[Apache Karaf Vineyard](http://karaf.apache.org/vineyard) is a full featured platform to manage and define services and
API in your ecosystem.

## Overview

Karaf Vineyard is composed by three main modules:

* Karaf Vineyard Registry is where the services and API definitions are stored. It's where you can describe the API,
 including resources, policies, metadata. The registry can be manipulated by a REST API.
* Karaf Vineyard Gateway is the API gateway. Other applications and services in your ecosystem will only use
 the Vineyard Gateway to use the actual underlying services and API. Karaf Vineyard Gateway façade the services and proxy
 to the concrete backend services. Vineyard Gateway can interact with the Vineyard Registry to
 retrieve and expose API.
* Karaf Vineyard Importers is a set of plugins that you can execute to import existing services present in your
ecosystem, creating the corresponding entry in the registry and exposing them in the gateway.

## Getting Started

### Installation

Karaf Vinyard is available as a Karaf features repository (and also Karaf KAR file and profile).
You can directly install Karaf Vineyard modules in a running Karaf instance using the corresponding Vineyard
features:

* karaf-vineyard-registry
* karaf-vineyard-gateway
* karaf-vineyard-importer

Simply register Karaf Vineyard features repository:

```
karaf@root()> feature:repo-add mvn:org.apache.karaf.vineyard/apache-karaf-vineyard/1.0.0-SNAPSHOT/xml/features
```

Then you can directly install the Karaf Vineyard features using `feature:install` command.

### Building

In order to build Karaf Vineyard, you need the following requirements:

* JDK 1.8.x or above
* Apache Maven 3.x

For instance, on a Linux Ubuntu system, it can be installed with:

```
apt-get install openjdk-8-jdk maven
```

Then, you can clone the Karaf Vineyard git repository using:

```
git clone https://github.com/jbonofre/karaf-vineyard
```

To build Karaf Vineyard, you do:

```
mvn clean install
```

NB: you can bypass the tests to have a faster build using

```
mvn clean install -DskipTests
```

## Contact Us

To get involved in Apache Karaf Vineyard and Apache Karaf:

* [Subscribe](mailto:user-subscribe@karaf.apache.org) or [mail](mailto:user@karaf.apache.org) the [user@karaf.apache.org](http://mail-archives.apache.org/mod_mbox/karaf-user/) list.
* [Subscribe](mailto:dev-subscribe@karaf.apache.org) or [mail](mailto:dev@karaf.apache.org) the [dev@karaf.apache.org](http://mail-archives.apache.org/mod_mbox/karaf-dev/) list.
* Report issues on [JIRA](https://issues.apache.org/jira/browse/KARAF).

Take a look on the [contribute](http://karaf.apache.org/contribute) section of the website.

## More Information

* [Apache Karaf | http://karaf.apache.org](http://karaf.apache.org)


Thanks for using Apache Karaf & Apache Karaf Vineyard !
--
The Apache Karaf Team
