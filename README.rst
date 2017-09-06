.. This work is licensed under a Creative Commons Attribution 4.0 International License.
.. http://creativecommons.org/licenses/by/4.0
.. role:: raw-latex(raw)
   :format: latex
..

How to deploy **VeriGraph** on Apache Tomcat:

**Windows**

-  install jdk1.8.X
   YY(http://www.oracle.comntechnetwork/java/javase/downloads/jdk8-downloads-2133151.html);
-  set JAVA HOME environment variable to where you installed the jdk
   (e.g.
   ``C:\Program Files\Java\jdk1.8.XYY``);
-  install Apache Tomcat 8 (https://tomcat.apache.org/download-80.cgi);
-  set CATALINA HOME ambient variable to the directory where you
   installed Apache (e.g.
   ``C:\Program Files\Java\apache-tomcat-8.0.30``);
-  (optional) configure Tomcat Manager:
-  open the file ``%CATALINA_HOME%\conf\tomcat-users.xml``
-  under the ``tomcat-users`` tag place, initialize an user with roles
   "tomcat, manager-gui, manager-script". An example is the following
   content:
   ``xml   <role rolename="tomcat"/>   <role rolename="role1"/>   <user username="tomcat" password="tomcat" roles="tomcat,manager-gui"/>   <user username="both" password="tomcat" roles="tomcat,role1"/>   <user username="role1" password="tomcat" roles="role1"/>``

-  edit the "to\_be\_defined" fields in tomcat-build.xml with the before
   defined credentials;
-  execute the generate-war ant task in order to generate the .war;
-  launch Tomcat 8 with the startup script
   ``%CATALINA_HOME%\bin\startup.bat`` or by the start-tomcat task ant;
-  (optional) if you previously configured Tomcat Manager you can open a
   browser and navigate to `this link <http://localhost:8080/manager>`__
   and login using the proper username and password (e.g.,
   ``tomcat/tomcat`` in the previous example);
-  (optional) you can deploy/undeploy/redeploy the downloaded WARs
   through the web interface.

**Ant script**

Edit the "to\_be\_defined" fields of the tomcat-build.xml. with the
username and password previously configured in Tomcat(e.g.
``name="tomcatUsername" value="tomcat"`` and
``name="tomcatPassword" value="tomcat"`` the values set in
'tomcat-users'). Set ``server.location`` property to the directory where
you installed Apache (e.g.
``C:\Program Files\Java\apache-tomcat-8.0.30``);

Verigraph target:

-  generate-war: it generates the war file;

-  generate-binding: it generates the JAXB classes from xml\_components
   schema;

-  start-tomcat : it starts the Apache Tomcat;

-  deployWS: it deploys the verigraph.war file contained in
   verigraph/war folder;

-  startWS: it starts the webservice;

-  run-test: it runs the tests in tester folder. It is possible to
   choose the iterations number for each verification request by
   launching the test with "-Diteration=n run-test" where n is the
   number of iterations you want;

-  stopWS: it stops the webservice;

-  undeployWS: it undeploys the webservice from Apache Tomcat;

-  stop-tomcat: it stops Apache Tomcat.

**Unix**

-  install ``jdk1.8.X_YY`` from the command line:
-  go to `this
   link <http://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html>`__
   to check the appropriate version for you OS and architecture
-  copy the desired version to the clipboard (e.g.
   ``http://download.oracle.com/otn-pub/java/jdk/7u79-b15/jdk-7u79-linux-x64.tar.gz``)
-  open a terminal windows and paste the following command (replace
   ``link`` with the previously copied link):
   ``wget --no-check-certificate --no-cookies --header "Cookie: oraclelicense=accept-securebackup-cookie" 'link'``
   e.g.
   ``wget --no-check-certificate --no-cookies --header "Cookie: oraclelicense=accept-securebackup-cookie" http://download.oracle.com/otn-pub/java/jdk/7u79-b15/jdk-7u79-linux-x64.tar.gz``
-  untar the archive with the following command (replace 'jdk' to match
   the name of the downloaded archive):
   ``tar zxvf 'jdk'.tar.gz``
   e.g.
   ``tar zxvf jdk-7u<version>-linux-x64.tar.gz``
-  delete the ``.tar.gz`` file if you want to save disk space
-  install and configure Apache Tomcat 8 with the following commands:
-  go to `this URL <http://it.apache.contactlab.it/tomcat/tomcat-8/>`__
   and see what the latest available version is
-  download the archive (substitute every occurrence of '8.0.32' in the
   following command with the latest available version):
   ``wget http://it.apache.contactlab.it/tomcat/tomcat-8/v8.0.32/bin/apache-tomcat-8.0.32.tar.gz``
-  extract downloaded archive:
   ``tar xvf apache-tomcat-8.0.32.tar.gz``
-  edit configuration:
   ``nano ./apache-tomcat-8.0.32/conf/tomcat-users.xml``
-  under the ``tomcat-users`` tag place, initialize an user with roles
   "tomcat, manager-gui, manager-script". An example is the following
   content:
   ``xml   <role rolename="tomcat"/>   <role rolename="role1"/>   <user username="tomcat" password="tomcat" roles="tomcat,manager-gui"/>   <user username="both" password="tomcat" roles="tomcat,role1"/>   <user username="role1" password="tomcat" roles="role1"/>   </tomcat-users>``
-  set a few environment variables: ``sudo nano ~/.bashrc``
-  paste the following content at the end of the file
   ``export CATALINA_HOME='/path/to/apache/tomcat/folder'``
   e.g.
   ``export CATALINA_HOME=/home/mininet/apache-tomcat-8.0.33``
   ``export JRE_HOME='/path/to/jdk/folder'``
   e.g.
   ``export JRE_HOME=/home/mininet/jdk1.8.0_92/jre``
   ``export JDK_HOME='/path/to/jdk/folder'``
   e.g.
   ``export JDK_HOME=/home/mininet/jdk1.8.0_92``
-  ``exec bash``
-  edit the "to\_be\_defined" fields of the tomcat-build.xml. with the
   username and password previously configured in Tomcat(e.g.
   ``name="tomcatUsername" value="tomcat"`` and
   ``name="tomcatPassword" value="tomcat"`` the values set in
   'tomcat-users'). Set ``server.location`` property to the directory
   where you installed Apache (e.g.
   ``C:\Program Files\Java\apache-tomcat-8.0.30``);
-  execute the generate-war ant task in order to generate the .war;
-  launch Tomcat 8 with the startup script
   ``$CATALINA_HOME/bin/startup.sh`` or with start-tomcat ant tast
-  open a browser and navigate to `this
   link <http://localhost:8080/manager>`__ and login using
   ``tomcat/tomcat`` as username/password
-  you can deploy/undeploy/redeploy the downloaded WARs through the web
   interface

**Eclipse**

-  clone project onto your hard drive with this command:
   ``git clone git@github.com:netgroup-polito/verigraph.git``
-  Download Apache Tomcat 8 (see instructions above for Windows and
   Unix)
-  Download JDK (see instructions above for Windows and Unix)
-  Configure runtime environment in Eclipse with `the following
   instructions <http://crunchify.com/step-by-step-guide-to-setup-and-install-apache-tomcat-server-in-eclipse-development-environment-ide/>`__
-  Add new Tomcat server on port ``8080``
-  Configure Tomcat server:

   -  double-click on the newly created server in the ``Servers`` tab
   -  make sure under ``Server Locations`` ``Use Tomcat installation``
      is selected

-  Run the server
-  edit the "to\_be\_defined" fields of the tomcat-build.xml. with the
   username and password previously configured in Tomcat(e.g.
   ``name="tomcatUsername" value="tomcat"`` and
   ``name="tomcatPassword" value="tomcat"`` the values set in
   'tomcat-users'). Set ``server.location`` property to the directory
   where you installed Apache (e.g.
   ``C:\Program Files\Java\apache-tomcat-8.0.30``);
-  execute the generate-war ant task in order to generate the .war;

**How to add you own function ``<type>``**

1. under the the ``it.polito.verigraph.mcnet.netobjs`` package create a
   new class ``<Type>.java``, where ``<type>`` is the desired function
   name (i.e. ``<type>`` will be added to the supported node functional
   types) which extends ``NetworkObject`` and implement the desired
   logic

2. under ``/verigraph/jsonschema/`` create a file ``<type>.json``. This
   file represents a JSON schema (see `here <http://json-schema.org/>`__
   the official documentation). For compatibility with the other
   functions it is mandatory to support an array as the root of the
   configuration, but feel free to specify all the other constraints as
   needed. A sample of ``<type>.json`` to describe an empty
   configuration could be the following:

``json   {       "$schema": "http://json-schema.org/draft-04/schema#",       "title": "Type",       "description": "This is a generic type",       "type": "array",       "items": {           "type": "object"       },       "minItems": 0,       "maxItems": 0,       "uniqueItems": true   }``

3. in the package ``it.polito.verigraph.validation`` create a new class
   file named ``<Type>Validator.java`` (please pay attention to the
   naming convention here: ``<Type>`` is the function type used in the
   previous step capitalized, followed by the suffix ``Validator``)
   which implements ``ValidationInterface``. This class represents a
   custom validator for the newly introduced type and allows for more
   complex constraints, which is not possible to express through a JSON
   schema file. The validate method that has to be implemented is given
   the following objects:

-  ``Graph graph`` represents the nffg that the object node belongs to;
-  ``Node node`` represents the node that the object configuration
   belongs to;
-  ``Configuration configuration`` represents the parsed configuration.
   It is sufficient to call the method ``getConfiguration`` on the
   ``configuration`` object to get a ``JsonNode`` (Jackson's class) and
   iterate over the various fields. In case a configuration is not valid
   please throw a new ``ValidationException`` passing a descriptive
   failure message. Adding a custom validator is not strictly necessary
   whenever a JSON schema is thought to be sufficient. Note though that,
   other than the mandatory validation against a schema, whenever a
   custom validator is not found a default validation is triggered, i.e.
   the value of every JSON property must refer to the name of an
   existing node in the working graph/nffg. If this is not the desired
   behavior it is suggested to write a custom validator with looser
   constraints.

4.  edit the xml\_component schema file in order to add the new element
    in the neo4j database;

5.  execute generate-binding in order to regenerate the
    it.polito.neo4j.jaxb classes;

6.  Insert the serialization logic for the new element type
    configuration in setCofiguration() method in GraphToNeo4j class of
    the it.polito.translator.jaxb package;

7.  Insert the deserialization logic for the new element type
    configuration in setCofiguration() method in Neo4jToGraph class of
    the it.polito.neo4j.translator package;

8.  Insert the new element in the switch case of setConfiguration()
    method of Scenario class in it.polito.verigraph.solver package in
    order to add the configurations element to the Scenario. This method
    retrieves the configuration values of the element in order to make
    the configureDevices() in GenSolver class. The configurations have
    to be stored into the config array or config obj data structures.
    The former is used in the case of a list of values as element
    configuration (e.g. a dpi has a list of not allowed word); the
    latter, in the case of a pair of values, represents a single
    configuration value (e.g. a firewall has a pair destination, source
    as configuration);

9.  Insert the creation of the new element in an else if of setDevice()
    of GenSolver class and put into mo data structure the name of the
    new element and the element itself (e.g. mo.put(host1, endhost));

10. Insert the condition for the installation of the new object created
    in it.polito.verigraph.mcnet.netobj using the data structure where
    you put the configurations of the element (config array or config
    obj);

11. Restart the web service.

**Troubleshooting**

-  The neo4j embedded version must be greater or equal to 3.1.3 as
   specified in pom.xml file. The previous versions could not work
   correctly with Apache Tomcat because of a bug;

-  The location of the database can be edited by the
   neo4jDeploymentFolder field of Neo4jLibrary class in
   it.polito.neo4j.manager;

-  The ant task "init" downloads the com.mirosoft.z3 library, if you
   want to change the version of the library, modify the url in the task
   with the right version. Note that the versions earlier than 4.5
   cannot work properly.

In order to run the automatic testing script test.py, you need the
following dependencies installed on your python distribution: -
"requests" python package -> http://docs.python-requests.org/en/master/
- "jsonschema" python package -> https://pypi.python.org/pypi/jsonschema

IMPORTANT - If you have multiple versions of Python installed on your
machine, check carefully that the version you are actually using when
running the script, has the required packages installed. Requested
version is Python 3+

HINT - to install a package you can raise the following command (Bash on
Linux or DOS shell on Windows): python -m pip install jsonschema python
-m pip install requests

Tested on PYTHON 3.4.3

To add a new test, just put a new .json file inside the testcases
folder. The corresponding JSON schema is in the testcase\_schema.json
file and some examples are already available. Each json file should
specify: - id, an integer for the testcase; - name, the name for the
testcase; - description, an optional description; -
policy\_url\_parameters, the parameters to be appended after the
verification URL (including the '?' character), it is an array. -
results, the expected verification results, it is an array; - graph, the
graph to be tested (the same object that you usually POST to VeriGraph
to create a new graph).

In case of multiple policy\_url\_parameters and results:
``"policy_url_parameters":[    "?type=reachability&source=sap1&destination=webserver1",    "?type=reachability&source=sap3&destination=webserver1"     ],    "results":[    "SAT",    "SAT"    ],``

The test.py script will test each .json file contained into the
testcases folder and will provide a complete output. The result.csv
contains the verification results in the following way (column):

-source\_node; -destination\_node; -graph\_id; -testcase\_id; -result
(FAIL in case of test failed); -the execution time for each execution of
the verification.

It is possible to do several verification for each request in the
policy\_url\_paramters. You have to launch the ant run-test with
"-Diteration=n run-test" or by commandline with "testpy -iteration n"
where n is the iterations number you want.
