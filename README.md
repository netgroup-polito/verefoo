# Verifoo
##### Verification and Optimization Orchestrator component for join Service Graph mapping and verification. The component exploits the z3Opt engine for solving MaxSAT and VNE problem

## Folder Structure:
-   docs/ -- Documentation of the code (including javadoc)
    -   VerifooDocs.pdf --- Documentation of the web service and other
        useful information
    -   verigraph\_doc.pdf --- Documentation of Verigraph for further
        details
-   lib/ --- All the external libraries (e.g. Z3 library)
    -   junit/ --- Libraries for running tests
    -   lib4j/ --- Libraries for managing the logging operations
-   log/ --- All the logs (for debugging purposes)
-   resources/ ---
    -   log4j2.xml --- Settings of the logs
-   src/ --- Java classes (for further information see the javadoc)
    -   it/polito/verifoo/components/ --- Basical Verifoo classes
    -   it/polito/verifoo/rest/app/ --- Classes to start the Rest
        application
    -   it/polito/verifoo/rest/common/ --- Classes that retrieve the
        informations from the JAXB class objects and pass them to
        Verifoo
    -   it/polito/verifoo/rest/jaxb/ --- Automatically generated JAXB
        classes
    -   it/polito/verifoo/rest/main/ --- Main class for debugging
        purposes
    -   it/polito/verifoo/rest/test/ --- Classes that manage all the
        tests
    -   it/polito/verifoo/rest/webservice/ --- Classes needed for the
        WebService
    -   it/polito/verifoo/test --- Simple examples on how Verifoo works
    -   it/polito/verigraph/\* --- Basical Verigraph classes
-   target/ --- Folder for the war file
-   testfile/ --- XML files that are used to test the application
-   WebContent/ --- Files needed in order to deploy the service
-   xsd/ --- XML schemas needed for the application
    -   errorSchema.xsd -- XML schema of the response in case an error
        occurred
    -   nfvInfo.xsd -- XML schema of Verifoo
    -   xml\_components.xsd -- XML schema of Verigraph (used into
        verifoo)
    -   hateoasLinks.xsd -- XML schema used by the root resource to let
        the client know all the links of the REST WebService
-   build.xml --- Ant script to automate the compiling and the
    deployment

## Resources:

 * [VerifooDocs.pdf](https://github.com/netgroup-polito/verifoo/blob/rest-service/doc/VerifooDocs.pdf) for documentation.
