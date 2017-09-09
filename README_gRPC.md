gRPC Project
============
This project contains the interfaces for a web service based on gRPC.

<i class="icon-cog"></i>How to install:
--------------------------------------------------------------
For gRPC interface, add to your `pom.xml` (in the project this part is already present):
```
	<dependency>
		<groupId>io.grpc</groupId>
		<artifactId>grpc-netty</artifactId>
		<version>${grpc.version}</version>
	</dependency>
	<dependency>
		<groupId>io.grpc</groupId>
		<artifactId>grpc-protobuf</artifactId>
		<version>${grpc.version}</version>
	</dependency>
	<dependency>
		<groupId>io.grpc</groupId>
		<artifactId>grpc-stub</artifactId>
		<version>${grpc.version}</version>
	</dependency>

```

For protobuf-based codegen integrated with the Maven build system, you can use protobuf-maven-plugin : 

```
	<build>
		<extensions>
			<extension>
				<groupId>kr.motd.maven</groupId>
				<artifactId>os-maven-plugin</artifactId>
				<version>1.4.1.Final</version>
			</extension>
		</extensions>
		<plugins>
			<plugin>
				<groupId>org.xolstice.maven.plugins</groupId>
				<artifactId>protobuf-maven-plugin</artifactId>
				<version>0.5.0</version>
				<configuration>
					<protocArtifact>com.google.protobuf:protoc:3.1.0:exe:${os.detected.classifier}</protocArtifact>
					<pluginId>grpc-java</pluginId>
					<pluginArtifact>io.grpc:protoc-gen-grpc-java:${grpc.version}:exe:${os.detected.classifier}</pluginArtifact>
				</configuration>
				<executions>
					<execution>
						<goals>
							<goal>compile</goal>
							<goal>compile-custom</goal>
						</goals>
					</execution>
				</executions>
			</plugin>
		</plugins>
	</build>

```    
	

Due to the fact that the project is intended for Eclipse, you need to install an additional Eclipse plugin because [m2e](https://www.eclipse.org/m2e/) does not evaluate the extension specified in a `pom.xml`. [Download `os-maven-plugin-1.5.0.Final.jar`](http://repo1.maven.org/maven2/kr/motd/maven/os-maven-plugin/1.5.0.Final/os-maven-plugin-1.5.0.Final.jar) and put it into the `<ECLIPSE_HOME>/plugins` directory. 
(As you might have noticed, `os-maven-plugin` is a Maven extension, a Maven plugin, and an Eclipse plugin.)

If you are using IntelliJ IDEA, you should not have any problem. 

If you are using other IDEs such as NetBeans, you need to set the system properties `os-maven-plugin` sets manually when your IDE is launched. You usually use JVM's `-D` flags like the following:

>-Dos.detected.name=linux
>-Dos.detected.arch=x86_64
>-Dos.detected.classifier=linux-x86_64



 <i class="icon-file"></i>Included files:
--------------------

Here you can find a brief description about useful files for the gRPC interface:

**src/main/java:**


 - *it.polito.grpc:*


This package includes 2 classes that represent the client and server.
>**Client.java:**

>	Client of gRPC application. It implements all possible methods necessary for communicate with server.
>	It prints out the received response. 
>	Moreover it provides some static methods that are used for creating the instances of requests.

>**Service.java:**

>	Server of gRPC application. It implements all possible methods necessary for communicate with client.
>	It saves the received request on log.
>	This server could be accessed by multiple clients, because synchronizes concurrent accesses.
>	Each method that is possible to call is has the equivalent operation in REST-interface.

>**GrpcUtils.java:**

>   This class provides some static methods that are used by `Service.java` in order to translate a request into a class that is accepted by Verigraph.
>   Other methods are used to translate the class of Verigraph in the proper gRPC response.
>   These functionalities are exploited by test classes.
>   Furthermore this set of methods is public, so in your application you could call them, even if this should not be useful because `Client.java` provides other high-level functions.  

	
 

 - *it.polito.grpc.test:*

	This package includes classes for testing the gRPC application.
	
	
>**GrpcServerTest.java:**
	
>For each possible method we test if works correctly.
>We create a fake client (so this test doesn't use the method that are present in client class) and test if it receives the expected response.
>In a nutshell, it tests the methods of Client in case of a fake server.
>Please notice that the test prints some errors but this is intentional, because the program tests also error case.
>Indeed, not all methods are tested, because we have another class (ReachabilityTest.java) that is specialized for testing the verification method.
			
>**GrpcTest.java:**
	
>This set of tests is intended to control the most common use cases, in particular all methods that are callable in Client and Service class, apart from verifyPolicy for the same reason as before.
>It tries also to raise an exception and verify if the behavior is as expected.
			
>**MultiThreadTest.java:**
	
>This test creates multiple clients that connect to the server and verify is the result is correct. These methods test the synchronization on 
>server-side.
			
>	**ReachabilityTest.java:**
	
>This file tests the verification method, it exploits the test case already present in the project and consequently has the certainty of testing not so simple case. In particular it reads the file in "src/main/webapp/json" and use this as starting point.
>Some exceptions are thrown in order to verify if they are handled in a correct way.
		
**src/main/proto:**

>**verigraph.proto:**

>File containing the description of the service. This includes the definition of all classes used in the application.
>Moreover contains the definition of the methods that is possible to call.
>Each possible method called by REST API is mapped on a proper gRPC method.
>In case of error a message containing the reason is returned to the client.  
>More details are available in the section about Proto Buffer. 
		
**taget/generated-sources/protobuf/java:**

 - *io.grpc.verigraph:*
 
	This package includes all classes generated from verigraph.proto by means of protoc. For each object you can find 2 classes : 
	
	>**{NameObject}Grpc.java**
	
	>**{NameObject}GrpcOrBuilder.java** 
	
	>The first is the real implementation, the second is the interface.
		
**taget/generated-sources/protobuf/grpc-java:**

 - *io.grpc.verigraph:*
 
	This package includes a single class generated from verigraph.proto by means of protoc. 
	
	>**VerigraphGrpc.java:**
	
	>This is useful in order to create the stubs that are necessary to communicate both for client and server. 

**lib:**

This folder includes a jar used for compiling the project with Ant.
	
>**maven-ant-tasks-2.1.3.jar:**

>This file is used by build.xml in order to include the maven dependencies.
		
**pom.xml:**

Modified in order to add all necessary dependencies. It contains also the build tag used for create the generated-sources folders.
This part is added according to documentation of gRPC for java as explained above in How To Install section.
For further clarification go to [this link](https://github.com/grpc/grpc-java/blob/master/README.md).
	
**build.xml:**

This ant file permit to run and compile the program in a simple way, it exploits the maven-ant-tasks-2.1.3.jar already present in project.
	
It contains 3 fundamental tasks for gRPC interface:
- **build:** compile the program

- **run:** run both client and server

- **run-client :** run only client

- **run-server :** run only server

- **run-test :** launch all tests that are present in the package, prints out the partial results and global result.

Note that the execution of these tests may take up to 1-2 minutes when successful, according to your computer architecture.


<i class="icon-folder-open"></i>More Information About Proto Buffer:
--------------------------------------------------------------------

Further clarification about verigraph.proto: 

- A `simple RPC` where the client sends a request to the server using the stub and waits for a response to come back, just like a normal function call.
	```xml
	// Obtains a graph
	rpc GetGraph (RequestID) returns (GraphGrpc) {}
	
	```
 
In this case we send a request that contains the id of the graph and the response is a Graph.


- A `server-side streaming RPC` where the client sends a request to the server and gets a stream to read a sequence of messages back. The client reads from the returned stream until there are no more messages. As you can see in our example, you specify a server-side streaming method by placing the stream keyword before the response type.
	```xml
  
	// Obtains a list of Nodes
	rpc GetNodes (RequestID) returns (stream NodeGrpc) {}
	
	```
 
In this case we send a request that contains the id of the graph and the response is a list of Nodes that are inside graph.

Further possibilities are available but in this project are not expolied. If you are curious see [here](http://www.grpc.io/docs/tutorials/basic/java.html#defining-the-service).


Our `.proto` file also contains protocol buffer message type definitions for all the request and response types used in our service methods - for example, heres the `RequestID`  message type:
```xml
	message RequestID {
		int64 idGraph = 1;
		int64 idNode = 2;
		int64 idNeighbour = 3;
	}	
```	


The " = 1", " = 2" markers on each element identify the unique "tag" that field uses in the binary encoding. Tag numbers 1-15 require one less byte to encode than higher numbers, so as an optimization you can decide to use those tags for the commonly used or repeated elements, leaving tags 16 and higher for less-commonly used optional elements. Each element in a repeated field requires re-encoding the tag number, so repeated fields are particularly good candidates for this optimization.


Protocol buffers are the flexible, efficient, automated solution to solve exactly the problem of serialization. With protocol buffers, you write a .proto description of the data structure you wish to store. From that, the protocol buffer compiler creates a class that implements automatic encoding and parsing of the protocol buffer data with an efficient binary format. The generated class provides getters and setters for the fields that make up a protocol buffer and takes care of the details of reading and writing the protocol buffer as a unit. Importantly, the protocol buffer format supports the idea of extending the format over time in such a way that the code can still read data encoded with the old format.
	
	syntax = "proto3";
	
	package verigraph;

	option java_multiple_files = true;
	option java_package = "io.grpc.verigraph";
	option java_outer_classname = "VerigraphProto";
	```
This .proto file works for protobuf 3, that is slightly different from the version 2, so be careful if you have code already installed.

The .proto file starts with a package declaration, which helps to prevent naming conflicts between different projects. In Java, the package name is used as the `Java package` unless you have explicitly specified a java_package, as we have here. Even if you do provide a `java_package`, you should still define a normal `package` as well to avoid name collisions in the Protocol Buffers name space as well as in non-Java languages.

After the package declaration, you can see two options that are Java-specific: `java_package` and `java_outer_classname`. `java_package` specifies in what Java package name your generated classes should live. If you don't specify this explicitly, it simply matches the package name given by the package declaration, but these names usually aren't appropriate Java package names (since they usually don't start with a domain name). The `java_outer_classname` option defines the class name which should contain all of the classes in this file. If you don't give a `java_outer_classname explicitly`, it will be generated by converting the file name to camel case. For example, "my_proto.proto" would, by default, use "MyProto" as the outer class name.
In this case this file is not generated, because `java_multiple_files` option is true, so for each message we generate a different class.

For further clarifications see [here](https://developers.google.com/protocol-buffers/docs/javatutorial)


Notes 
--------

For gRPC interface you need that neo4jmanager service is already deployed, so if this is not the case, please follow the instructions at this [link](https://github.com/netgroup-polito/verigraph/blob/a3c008a971a8b16552a20bf2484ebf8717735dd6/README.md).

In this version there are some modified files compared to the original [Verigraph project](https://github.com/netgroup-polito/verigraph)

**it.polito.escape.verify.service.NodeService:**

At line 213 we modified the path, because this service is intended to run not only in container, as Tomcat, so we added other possibility that files is placed in src/main/webapp/json/ folder.

**it.polito.escape.verify.service.VerificationService:**

In the original case it searches for python files in "webapps" folder, that is 	present if the service is deployed in a container, but absent otherwise. So we added another string that will be used in the case the service doesn't run in Tomcat.

**it.polito.escape.verify.databese.DatabaseClass:**

Like before we added the possibility that files are not in "webapps" folder, so is modified in order to run in any environment. Modification in method loadDataBase() and persistDatabase().

Pay attention that Python is needed for the project. If it is not already present on your computer, please [download it]( https://www.python.org/download/releases/2.7.3/).
It works fine with Python 2.7.3, or in general Python 2.

If you have downloaded a Python version for 64-bit architecture please copy the files in "service/z3_64" and paste in "service/build" and substitute them, 
because this project works with Python for 32-bit architecture.

Python and Z3 must support the same architetcure.

Moreover you need the following dependencies installed on your python distribution:

	"requests" python package -> http://docs.python-requests.org/en/master/

	"jsonschema" python package -> https://pypi.python.org/pypi/jsonschema

HINT - to install a package you can raise the following command (Bash on Linux or DOS shell on Windows): python -m pip install jsonschema python -m pip install requests
Pay attention that it is possible that you have to modify the PATH environment variable because is necessary to address the python folder, used for verification phase. 

Remember to read the [README.rtf](https://gitlab.com/serena.spinoso/DP2.2017.SpecialProject2.gRPC/tree/master) and to follow the instructions in order to deploy the Verigraph service.

In the latest version of Maven there is the possibility that the downloaded files are incompatible with Java Version of the project (1.8). 
In this case you have to modify the file `hk2-parent-2.4.0-b31.pom` under your local Maven repository (e.g. 'C:\Users\Standard\.m2\repository') 
and in the path `\org\glassfish\hk2\hk2-parent\2.4.0-b31` find the file and modify at line 1098 (in section `profile`) the `jdk` version to `[1.8,)` .

Admittedly, the version that is supported by the downloaded files from Maven Dependencies is incompatible with jdk of the project.
So modify the file `gson-2.3.pom` in Maven repository, under `com\google\code\gson\gson\2.3` directory, in particular line 91, from `[1.8,` to `[1.8,)`.

This project was also tested on Linux Ubuntu 15.10.