![](/resources/verefoo_icon.png)


##### VEREFOO (VErified REFinement and Optimized Orchestrator) is a framework  designed to provide an automatic way to allocate packet filters – the most common and traditional firewall technology – in a Service Graph defined by the service designer and an auto-configuration technique to create firewall rules with respect to the specified security requirements.

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
-  install [jdk1.8.X YY](http://www.oracle.comntechnetwork/java/javase/downloads/jdk8-downloads-2133151.html);
-  install [Apache Tomcat 8](https://tomcat.apache.org/download-80.cgi);
	-  set CATALINA HOME ambient variable to the directory where you  installed Apache;
	-  (optional) configure Tomcat Manager:
	-  open the file ``%CATALINA_HOME%\conf\tomcat-users.xml``
	-  under the ``tomcat-users`` tag place, initialize an user with roles  "tomcat, manager-gui, manager-script".  An example is the following  content:
   ``xml   <role rolename="manager-gui"/>  <role rolename="manager-script"/>  <role rolename="admin-gui"/>   <role rolename="admin-script"/>  <user username="admin" password="admin" roles="manager-gui,manager-script,admin-scripts"/>``
	-  edit the "to\_be\_defined" fields in tomcat-build.xml with the before
   defined credentials;
-  execute the `generate` ant task in order to generate the .war;
-  launch Tomcat 8 with the startup script  ``%CATALINA_HOME%\bin\startup.bat`` or by the start-tomcat task ant;
-  (optional) if you previously configured Tomcat Manager you can open a  browser and navigate to `this link <http://localhost:8080/manager>`  and login using the proper username and password (e.g.,  ``admin/admin`` in the previous example);
-  (optional) you can `deploy/undeploy/redeploy` the downloaded WARs   through the web interface.




## Resources:

 * [VerifooDocs.pdf](https://github.com/netgroup-polito/verifoo/blob/master/docs/VerifooDocs.pdf) for documentation.


## TODO
mvn clean compile assembly:single

java -jar target\verifoo-0.0.1-SNAPSHOT-jar-with-dependencies.jar 25154 60 10 100

mvn clean package && java -jar target\verifoo-0.0.1-SNAPSHOT.jar
