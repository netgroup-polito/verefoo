![](/resources/verefoo_icon.png)

##### VEREFOO (VErified REFinement and Optimized Orchestrator) is a framework  designed to provide an automatic way to allocate packet filters - the most common and traditional firewall technology - in a Service Graph defined by the service designer and an auto-configuration technique to create firewall rules with respect to the specified security requirements.

## Z3 library support

[Download](https://github.com/Z3Prover/z3/releases) the correct version of Z3 according to your OS and your JVM endianness. For the correct functioning of the application, you must have the Z3 native library and include it to Java Library Path. The most convenient way to do this is add the path that the library to the dynamic linking library path.

* In Linux is `LD_LIBRARY_PATH`
* In MacOS is `DYLD_LIBRARY_PATH`
* In Windows is `PATH`

> e.g.,
> * `sudo nano /etc/environment`
> * `LD_LIBRARY_PATH = $LD_LIBRARY_PATH:/home/verefoo/z3/bin/`
> * `Z3 = /home/verefoo/z3/bin/` (also required)

## Installing Verefoo via Maven (Spring Boot application with Embedded Tomcat)  [Solution 1]

* install [jdk1.8.X YY](http://www.oracle.comntechnetwork/java/javase/downloads/jdk8-downloads-2133151.html);
* install [maven](https://maven.apache.org/install.html)
* `mvn clean package`
* `java -jar target/verifoo-0.0.1-SNAPSHOT.jar`

Swagger documentation can be accessed at [localhost:8085/verefoo](localhost:8085/verefoo).

## Installing Verefoo via Ant (Apache Tomcat required) [Solution 2]

* install [jdk1.8.X YY](http://www.oracle.comntechnetwork/java/javase/downloads/jdk8-downloads-2133151.html);
* install [Apache Tomcat 8](https://tomcat.apache.org/download-80.cgi);
  * set CATALINA HOME ambient variable to the directory where you  installed Apache;
  * (optional) configure Tomcat Manager:
  * open the file ``%CATALINA_HOME%\conf\tomcat-users.xml``
  * under the ``tomcat-users`` tag place, initialize an user with roles  "tomcat, manager-gui, manager-script".  An example is the following  content:
   ``xml   <role rolename="manager-gui"/>  <role rolename="manager-script"/>  <role rolename="admin-gui"/>   <role rolename="admin-script"/>  <user username="admin" password="admin" roles="manager-gui,manager-script,admin-scripts"/>``;
  * edit the "to\_be\_defined" fields in tomcat-build.xml with the before defined credentials;
* execute the `generate` ant task in order to generate the .war;
* launch Tomcat 8 with the startup script  ``%CATALINA_HOME%\bin\startup.bat`` or by the start-tomcat task ant;
* (optional) if you previously configured Tomcat Manager you can open a  browser and navigate to `this link <http://localhost:8080/manager>`  and login using the proper username and password (e.g.,  ``admin/admin`` in the previous example);
* (optional) you can `deploy/undeploy/redeploy` the downloaded WARs through the web interface.

## REST APIs

### Connecting to the REST APIs

In order to access the core of Verefoo, some REST APIs are available. Follow these steps to boot the environment:

* Run the Neo4j server: open a shell in the folder */neo4j-server/neo4j-community-3.5.25/bin* and type ```./neo4j console```;
* Run Tomcat: open another shell in the project root folder and type the following two commands: ```mvn clean package``` and ```java -jar target/verifoo-0.0.1-SNAPSHOT.jar```;
* Interact with the REST APIs: start doing requests to Verefoo by means of a RESTful client; for any doubt about the REST APIs, their documentation can be found at [localhost:8085/verefoo](localhost:8085/verefoo).

### Verefoo Interaction
In order to interact with Verefoo, it is necessary to specify the algorithm used. Currently, this version of Verefoo is equipped with two algorithms: Maximal Flows (MF) and Atomic Predicates (AP), each of which is suitable for distinct use cases. To select the algorithm utilized, the query parameter that specifies the algorithm should be included in the REST API request, subsequent to the framework's launch.

> Example:
> * `http://localhost:8085/verefoo/adp/simulations?Algorithm=AP` 
> * `http://localhost:8085/verefoo/adp/simulations?Algorithm=MF` 

The primary API is `/adp/simulations` as it serves as the API where the algorithm is executed. The Service Graph written in XML must be incorporated in the body of a POST API. Furthermore, it is obligatory to indicate the Algorithm to be employed by the framework, for instance, `?Algorithm=AP` is specified to execute the Atomic Predicate Algorithm, or `?Algorithm=MF` to select the Maximal Flows Algorithm. It should be noted that the specification of the algorithm is mandatory, otherwise, the framework will return an error.

### Launching the integration tests

Some integration tests are available for the REST APIs.

In order to launch them, it is enough to:

* Run the Neo4j server: open a shell in the folder */neo4j-server/neo4j-community-3.5.25/bin* and type ```./neo4j console```;
* Launch the tests: open another shell in the project root folder and type ```mvn clean verify```.

In case of failure, the detailed report can be found at */target/failsafe-report/failsafe-summary.xml*.

### Neo4j compatibility recommendations

The current version of the Neo4j server is 3.5.25 (Community Edition): it is compatible with the Neo4j Spring Data dependency in use. It is advisable to not employ at all newer versions of the server or Spring Data, despite available, for three main reasons:

* The required JDK for more recent releases may be the JDK 11 at least, while the current compiler for Verifoo is Java 8; the migration to a newer JDK **must** be first agreed with all the developers of the project;
* The configuration of Spring in the file SpringBootConfiguration should be changed because some classes are not available anymore;
* The Neo4j annotations in the DAO classes (which start with *db*) may change formalism.

## Regression Tests

Kindly note that the regression tests performed on the framework have been executed using the Z3 library versions 4.8.8 and 4.8.14.