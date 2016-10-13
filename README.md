How to deploy **VeriGraph** on Apache Tomcat:

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
- download `neo4jmanager.war` and `verify.war` from [here](https://github.com/netgroup-polito/verigraph/tree/master/service/build/windows)
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
  - go to [this URL](http://it.apache.contactlab.it/tomcat/tomcat-8/) and see what the latest available version is
  - download the archive (substitute every occurrence of '8.0.32' in the following command with the latest available version):  
  `wget http://it.apache.contactlab.it/tomcat/tomcat-8/v8.0.32/bin/apache-tomcat-8.0.32.tar.gz`
  - extract downloaded archive:    
  `tar xvf apache-tomcat-8.0.32.tar.gz`
  - edit configuration:    
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
- download `mcnet.jar`, `com.microsoft.z3.jar` and `qjutils.jar` from [here](https://github.com/netgroup-polito/verigraph/tree/master/service/build/linux) and copy them to `$CATALINA_HOME/shared`
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

**How to add you own function `<type>`**

1. under the the `mcnet.netobjs` package (i.e. under `/verify/service/src/mcnet/netobjs`) create a new class `<Type>.java`, where `<type>` is the desired function name (i.e. `<type>` will be added to the supported node functional types) which extends `NetworkObject` and implement the desired logic

2. regenerate `mcnet.jar` selecting the packages `mcnet.components` and `mcnet.netobjs` and exporting the generated JAR to `/verify/service/build` (overwrite the existing file)

3. under `/verify/src/main/webapp/json/` create a file `<type>.json`. This file represents a JSON schema \(see [here](http://json-schema.org/) the official documentation\). For compatibility with the other functions it is mandatory to support an array as the root of the configuration, but feel free to specify all the other constraints as needed. A sample of `<type>.json` to describe an empty configuration could be the following:

  ```json
  {
      "$schema": "http://json-schema.org/draft-04/schema#",
      "title": "Type",
      "description": "This is a generic type",
      "type": "array",
      "items": {
          "type": "object"
      },
      "minItems": 0,
      "maxItems": 0,
      "uniqueItems": true
  }
  ```

4. in the package `it.polito.escape.verify.validation` (i.e. under `src/main/java/it/polito/escape/verify/validation`) create a new class file named `<Type>Validator.java` (please pay attention to the naming convention here: `<Type>` is the function type used in the previous step capitalized, followed by the suffix `Validator`) which implements `ValidationInterface`. This class represents a custom validator for the newly introduced type and allows for more complex constraints, which is not possible to express through a JSON schema file. The validate method that has to be implemented is given the following objects:
  - `Graph graph` represents the nffg that the object node belongs to;
  - `Node node` represents the node that the object configuration belongs to;
  - `Configuration configuration` represents the parsed configuration. It is sufficient to call the method `getConfiguration` on the `configuration` object to get a `JsonNode` (Jackson's class) and iterate over the various fields.
In case a configuration is not valid please throw a new `ValidationException` passing a descriptive failure message.
Adding a custom validator is not strictly necessary whenever a JSON schema is thought to be sufficient. Note though that, other than the mandatory validation against a schema, whenever a custom validator is not found a default validation is triggered, i.e. the value of every JSON property must refer to the name of an existing node in the working graph/nffg. If this is not the desired behavior it is suggested to write a custom validator with looser constraints.

5. customize the class generator and add the support for the newly introduced type:
  - open the file `/verify/service/src/tests/j-verigraph-generator/config.py` and edit the following dictionaries:
    - `devices_to_classes` --> add the following entry: `"<type>" : "<Type>"`
    if you followed these instructions carefully the name of the class implementing the function `<type>` should be `<Type>.java` under the package `mcnet.netobjs`.
    - `devices_to_configuration_methods` --> add the following entry: `"<type>" : "configurationMethod"`
    if `<type>` is a middlebox it should have a configuration method contained in the implementation `<Type>.java` under the package `mcnet.netobjs`.
    - `devices_initialization`: add the following entry: `"<type>" : ["param1", "param2"]`
    if `<type>` requires any parameter when it gets instanciated please enter them in the form of a list. Make sure that these parameters refer to existing keys contained in the configuration schema file (see step 3). For instance the type `webclient` requires the name of a webserver it wants to communicate with. This parameter is passed in the configuration of a weblient by setting a property `webserver` to the name of the desired webserver. The value of this property gets extracted and used by the test generator automatically.
    - `convert_configuration_property_to_ip` --> add the following entry: `"<type>" : ["key", "value"]`
    Note that both `key` and `value` are optional and it is critical to set them only if needed. Since the Z3 provider used for testing works with IP addresses in this dictionary you have to indicate whether it is needed an automatic convertion from names to IP addresses: 
      - in case the keyword `key` is used every key of the JSON configuration parsed will be prepended with the string `ip_`;
      - in case the keyword `value` is used every value of the JSON configuration parsed will be prepended with the string `ip_`;
      - in case the list does not contain neither `key` nor `value` the original configuration won't be touched.
  - open the file `/verify/service/src/tests/j-verigraph-generator/test_class_generator.py` and under the "switch" case in the form of a series of ifs used to configure middle-boxes that starts at line #239 add a branch like the following with the logic to generate the Java code for <type> --> 
  `elif nodes_types[i] == "<type>":`
  You can take inspiration from the other branches to see how to serialize Java code. Note that this addition to the "switch" statement is not needed if `<type>` is not a middlebox or it does not need to be configured.

6. Restart the web service.