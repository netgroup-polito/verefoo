![](/resources/verefoo_icon.png)

##### VEREFOO (VErified REFinement and Optimized Orchestrator) is a framework  designed to provide an automatic way to allocate packet filters - the most common and traditional firewall technology - in a Service Graph defined by the service designer and an auto-configuration technique to create firewall rules with respect to the specified security requirements.

## Z3 library support

[Download](https://github.com/Z3Prover/z3/releases) the correct version of Z3 according to your OS and your JVM endianness. The recommended Z3 version number is 4.8.8. For the correct functioning of the application, you must have the Z3 native library and include it in the Java Library Path. The most convenient way to do this is to add the library path to the dynamic linking library path, which

* in Linux is `LD_LIBRARY_PATH`
* in MacOS is `DYLD_LIBRARY_PATH`
* in Windows is `PATH`

> e.g., on Linux,
> * `sudo nano /etc/environment`
> * `LD_LIBRARY_PATH = $LD_LIBRARY_PATH:/home/verefoo/z3/bin/`
> * `Z3 = /home/verefoo/z3/bin/` (also required)

## Installing Verefoo via Maven (Spring Boot application with Embedded Tomcat)  [Solution 1]

* install [jdk1.8.X YY](http://www.oracle.comntechnetwork/java/javase/downloads/jdk8-downloads-2133151.html);
* install [maven](https://maven.apache.org/install.html)
* `mvn clean package`
* `java -jar target/verifoo-0.0.1-SNAPSHOT.jar`

The Swagger documentation can be accessed at [localhost:8085/verefoo](localhost:8085/verefoo).

## Installing Verefoo via Ant (Apache Tomcat required) [Solution 2]

* install [jdk1.8.X YY](http://www.oracle.comntechnetwork/java/javase/downloads/jdk8-downloads-2133151.html);
* install [Apache Tomcat 8](https://tomcat.apache.org/download-80.cgi);
  * set CATALINA HOME environment variable to the directory where you installed Apache Tomcat;
  * (optional) configure Tomcat Manager:
  * open the file ``%CATALINA_HOME%\conf\tomcat-users.xml``
  * under the ``tomcat-users`` tag place, initialize a user with roles  "tomcat, manager-gui, manager-script".  An example is the following content:
   ``xml   <role rolename="manager-gui"/>  <role rolename="manager-script"/>  <role rolename="admin-gui"/>   <role rolename="admin-script"/>  <user username="admin" password="admin" roles="manager-gui,manager-script,admin-scripts"/>``;
  * edit the "to\_be\_defined" fields in tomcat-build.xml with the previously defined credentials;
* execute the `generate` ant task in order to generate the .war file;
* launch Tomcat 8 with the startup script  ``%CATALINA_HOME%\bin\startup.bat`` or by the start-tomcat task ant;
* (optional) if you previously configured Tomcat Manager you can open a  browser and navigate to `this link <http://localhost:8080/manager>`  and login using the proper username and password (e.g.,  ``admin/admin`` in the previous example);
* (optional) you can `deploy/undeploy/redeploy` the downloaded WARs through the web interface.

## REST APIs

### Connecting to the REST APIs

In order to access the core of Verefoo, some REST APIs are available when Verefoo is deployed. Follow these steps to boot the environment, deploy Verefoo and use the APIs (these instructions refer to the version with embedded Tomcat):

* Run the Neo4j server: open a shell in the folder */neo4j-server/neo4j-community-3.5.25/bin* and type ```./neo4j console```;
* Run Tomcat: open another shell in the project root folder and type the following two commands: ```mvn clean package``` and ```java -jar target/verifoo-0.0.1-SNAPSHOT.jar```;
* Interact with the REST APIs: any RESTful client can be used to interact with the Verefoo APIs; the APIs home URI is [localhost:8085/verefoo](localhost:8085/verefoo). For any doubt about the REST APIs, their documentation can be found at [localhost:8085/verefoo](localhost:8085/verefoo).

### Verefoo Interaction
In order to interact with Verefoo, it is necessary to specify the algorithm used. This version of Verefoo is equipped with two algorithms: Maximal Flows (MF) and Atomic Predicates (AP), each of which is suitable for distinct use cases. To select the algorithm utilized, the query parameter that specifies the algorithm should be included in the REST API request, subsequent to the framework's launch.

> Example:
> * `http://localhost:8085/verefoo/adp/simulations?Algorithm=AP` 
> * `http://localhost:8085/verefoo/adp/simulations?Algorithm=MF` 

The primary API is `/adp/simulations` as it serves as the API where the algorithm is executed. The Service Graph written in XML must be incorporated in the body of a POST request. It should be noted that the specification of the algorithm, as shown above, is mandatory, otherwise, the framework will return an error.

### Launching the integration tests

Some integration tests are available for the REST APIs.

In order to launch them, it is enough to:

* Run the Neo4j server: open a shell in the folder */neo4j-server/neo4j-community-3.5.25/bin* and type ```./neo4j console```;
* Launch the tests: open another shell in the project root folder and type ```mvn clean verify```.

In case of failure, the detailed report can be found at */target/failsafe-report/failsafe-summary.xml*.

### Neo4j compatibility recommendations

The framework was tested with the the Neo4j server version 3.5.25 (Community Edition): it is compatible with the Neo4j Spring Data dependency in use. It is advisable to avoid employing newer versions of the server or Spring Data, despite available, for three main reasons:

* The required JDK for more recent releases may be the JDK 11 at least, while the current compiler for Verifoo is Java 8; the migration to a newer JDK **must** be first agreed with all the developers of the project;
* The configuration of Spring in the file SpringBootConfiguration should be changed because some classes are not available anymore;
* The Neo4j annotations in the DAO classes (which start with *db*) may change formalism.

## Regression Tests

Kindly note that the regression tests performed on the framework have been executed using the Z3 library version 4.8.8.


## DEMO

You can find a full demonstration of the VEREFOO framework at the following link: https://youtu.be/QCFNLE2gHgE

In this demo, VEREFOO has been used to automatically compute the firewall allocation scheme and configuration in a virtual network that is devoid of firewalling functionalities.

The input Service Graph represents a ramified network, where multiple different function types are included, e.g., a load balancer, a web cache, a traffic monitor, a network address translators. Some end points are single hosts, whereas other ones are subnetworks representing the office networks of some companies.

![Service Graph](./resources/images-demo/SG.png)

![Service Graph functions](./resources/images-demo/SGfunctions.png)

The input Network Security Requirements establish which traffic flows must be blocked because potentially malicioucs, and which other must be able tor each their destination to ansure network connectivity. 

![Network Security Requirements](./resources/images-demo/NSRs.png)

After running the framework, VEREFOO produces two outputs. On the one hand, it establishes the optimal firewall allocation scheme, composed of the minimum number of firewall instances to be placed in the input Allocation Places. On the other hand, for each allocated instance, it computes the optimal configuration, composed of a default action and the smallest set of filtering rules. 

![Firewall Allocation Scheme](./resources/images-demo/FAS.png)

![Firewall Configuration](./resources/images-demo/FwRules.png)
