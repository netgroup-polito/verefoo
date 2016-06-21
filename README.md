How to deploy **rest-verigraph** on Apache Tomcat:

**Windows**
- install `jdk1.8.X_YY` [here](http://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html)
- set ambient variable `JAVA_HOME` to where you installed the jdk (e.g. `C:\Program Files\Java\jdk1.8.X_YY`)
- install Apache Tomcat 8 [here](https://tomcat.apache.org/download-80.cgi)
- set ambient variable `CATALINA_HOME` to the directory where you installed Apache (e.g. `C:\Program Files\Java\apache-tomcat-8.0.30`)
- create `shared` folder under `%CATALINA_HOME%`
- add previously created folder to the Windows `Path` system variable (i.e. append the following string at the end: `;%CATALINA_HOME%\shared`)
- download `mcnet.jar`, `com.microsoft.z3.jar` and `qjutils.jar` from [here](https://github.com/netgroup-polito/verigraph/tree/master/service/build) and copy them to `%CATALINA_HOME%\shared` 
- create custom file setenv.bat under `%CATALINA_HOME%\bin` with the following content:
```bat
set CLASSPATH=%CLASSPATH%;%CATALINA_HOME%\shared\qjutils.jar;%CATALINA_HOME%\shared\mcnet.jar;%CATALINA_HOME%\shared\com.microsoft.z3.jar;.;%CATALINA_HOME%\webapps\verify\WEB-INF\classes\tests
```
- download `neo4jmanager.war` and `verify.war` from [here](https://github.com/netgroup-polito/verigraph/tree/master/dist)
- copy downloaded WARs into `%CATALINA_HOME%\webapps`
- (optional) configure Tomcat Manager:
  - open the file `%CATALINA_HOME%\conf\tomcat-users.xml`
  - under the `tomcat-users` tag place the following content:
  ```xml
  <role rolename="tomcat"/>
  <role rolename="role1"/>
  <user username="tomcat" password="tomcat" roles="tomcat,manager-gui"/>
  <user username="both" password="tomcat" roles="tomcat,role1"/>
  <user username="role1" password="tomcat" roles="role1"/>
  ```
- launch Tomcat 8 with the startup script `%CATALINA_HOME%\bin\startup.bat`
- (optional) if you previously configured Tomcat Manager you can open a browser and navigate to [this link](http://localhost:8080/manager) and login using `tomcat/tomcat` as username/password
- (optional) you can deploy/undeploy/redeploy the downloaded WARs through the web interface

**Unix**
- install `jdk1.8.X_YY` from the command line:
  - go to [this link](http://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html) to check the appropriate version for you OS and architecture
  - copy the desired version to the clipboard (e.g. `http://download.oracle.com/otn-pub/java/jdk/7u79-b15/jdk-7u79-linux-x64.tar.gz`)
  - open a terminal windows and paste the following command (replace `link` with the previously copied link):  
  `wget --no-check-certificate --no-cookies --header "Cookie: oraclelicense=accept-securebackup-cookie" 'link'`  
  e.g.  
  `wget --no-check-certificate --no-cookies --header "Cookie: oraclelicense=accept-securebackup-cookie" http://download.oracle.com/otn-pub/java/jdk/7u79-b15/jdk-7u79-linux-x64.tar.gz`  
  - untar the archive with the following command (replace 'jdk' to match the name of the downloaded archive):  
  `tar zxvf 'jdk'.tar.gz`  
  e.g.  
  `tar zxvf jdk-7u<version>-linux-x64.tar.gz`  
- delete the `.tar.gz` file if you want to save disk space
- install and configure Apache Tomcat 8 with the following commands:  
  `wget http://it.apache.contactlab.it/tomcat/tomcat-8/v8.0.32/bin/apache-tomcat-8.0.32.tar.gz`  
  `tar xvf apache-tomcat-8.0.32.tar.gz`  
  `nano ./apache-tomcat-8.0.32/conf/tomcat-users.xml`  
  - under the `tomcat-users` tag place the following content
  ```xml
  <role rolename="tomcat"/>
  <role rolename="role1"/>
  <user username="tomcat" password="tomcat" roles="tomcat,manager-gui"/>
  <user username="both" password="tomcat" roles="tomcat,role1"/>
  <user username="role1" password="tomcat" roles="role1"/>
  </tomcat-users>
  ```
- set a few environment variables:
`sudo nano ~/.bashrc`
- paste the following content at the end of the file  
`export CATALINA_HOME='/path/to/apache/tomcat/folder'`  
e.g.  
`export CATALINA_HOME=/home/mininet/apache-tomcat-8.0.33`  
`export JRE_HOME='/path/to/jdk/folder'`  
e.g.  
`export JRE_HOME=/home/mininet/jdk1.8.0_92/jre`  
`export JDK_HOME='/path/to/jdk/folder'`  
e.g.  
`export JDK_HOME=/home/mininet/jdk1.8.0_92`  
`export LD_LIBRARY_PATH=$LD_LIBRARY_PATH:$CATALINA_HOME/shared`  
`export JAVA_OPTS="-Djava.library.path=$CATALINA_HOME/shared"`  
- `exec bash`
- download `mcnet.jar`, `com.microsoft.z3.jar` and `qjutils.jar` from [here](https://github.com/netgroup-polito/verigraph/tree/master/service/build) and copy them to `$CATALINA_HOME/shared`
- customize Tomcat classpath  
  `nano $CATALINA_HOME/bin/setenv.sh`
  - paste the following content and save file:
  ```bash
  #!/bin/sh
  export CLASSPATH=$CLASSPATH:$CATALINA_HOME/shared/qjutils.jar:$CATALINA_HOME/shared/mcnet.jar:$CATALINA_HOME/shared/com.microsoft.z3.jar:.:$CATALINA_HOME/webapps/verify/WEB-INF/classes/tests
  ```
  - save and close the file (`CTRL+O`, `CTRL+X`)
  `sudo chmod +x $CATALINA_HOME/bin/setenv.sh`
- download `neo4jmanager.war` and `verify.war` from [here](https://github.com/netgroup-polito/verigraph/tree/master/dist)
- paste the downloaded WARs into `$CATALINA_HOME/webapps`
- launch Tomcat 8 with the startup script `$CATALINA_HOME/bin/startup.sh`
- open a browser and navigate to [this link](http://localhost:8080/manager) and login using `tomcat/tomcat` as username/password
- you can deploy/undeploy/redeploy the downloaded WARs through the web interface

**Eclipse**
- clone project onto your hard drive with this command: `git clone git@github.com:netgroup-polito/verigraph.git`
- Download Apache Tomcat 8 (see instructions above for Windows and Unix)
- Download JDK (see instructions above for Windows and Unix)
- Configure runtime environment in Eclipse with [the following incstructions](http://crunchify.com/step-by-step-guide-to-setup-and-install-apache-tomcat-server-in-eclipse-development-environment-ide/)
- Add new Tomcat server on port `8080`
- Configure Tomcat server:
    - double-click on the newly created server in the `Servers` tab
    - make sure under `Server Locations` `Use Tomcat installation` is selected
    - Open `Launch Configuration`->`Classpath`
    - add the required JARS (`mcnet.jar`, `com.microsoft.z3.jar` and `qjutils.jar` from [here](https://github.com/netgroup-polito/verigraph/tree/master/service/build)) under `User Entries`
    - Hit `Apply` and `Ok`
- Run the server