# Verifoo
##### Verification and Optimization Orchestrator component for join Service Graph mapping and verification. The component exploits the z3Opt engine for solving MaxSAT and VNE problem

## Folder Structure:
-   docs/ --- Documentation of the code (Javadoc) and of service and schema (Pdf=
    -   VerifooDocs.pdf --- Documentation of the web service and other
        useful information
    -   verigraphdoc.pdf --- Verigraph documentation for further details
-   lib/ --- All the external library (e.g. Z3 library)
    -   junit/ --- Library for running
    -   lib4j/ --- Library for manage the logging operations
-   log/ --- All the logs (for debugging purposes)
-   resources/ ---
    -   log4j2.xml --- Settings of the logs
-   src/ --- Java classes
    -   it/polito/verifoo/components/ --- Basical Verifoo classes
    -   it/polito/verifoo/rest/app/ --- Classes to start the Rest
        application
    -   it/polito/verifoo/rest/common/ --- Classes that retrieve the
        informations from the XML and pass them to Verifoo
    -   it/polito/verifoo/rest/jaxb/ --- Automatically generated JAXB
        classes
    -   it/polito/verifoo/rest/logger/ --- Classes that handle the
        logging operations
    -   it/polito/verifoo/rest/main/ --- Main class for debugging
        purposes
    -   it/polito/verifoo/rest/test/ --- Test classes
    -   it/polito/verifoo/rest/webservice/ --- WebService classes
    -   it/polito/verifoo/test --- Simple examples on how Verifoo works
    -   it/polito/verigraph/ --- Basical Verigraph classes
-   target/ --- Folder for the war file
-   testfile/ --- XML files that are used to test the application
-   WebContent/ --- Files needed in order to deploy the service
-   xsd/ --- XML schemas needed for the application
	- errorSchema.xsd -- XML schema of Rest error response
	- nfvInfo.xsd  -- XML schema of Verifoo
	- xml_components.xsd -- XML schema of Verigraph (used into verifoo)
-   build.xml --- Ant script

## Resources:

 * [VerifooDocs.pdf](https://github.com/netgroup-polito/verifoo/blob/rest-service/doc/VerifooDocs.pdf) for documentation.
