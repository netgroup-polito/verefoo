package it.polito.verefoo;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.StringWriter;
import java.net.MalformedURLException;
import javax.xml.XMLConstants;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import java.util.Scanner;  // Import the Scanner class

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.xml.sax.SAXException;

import it.polito.verefoo.extra.BadGraphError;
import it.polito.verefoo.jaxb.*;


/**
 * This is the main class only for testing the Verefoo execution
 */

public class Main {
	static Logger loggerInfo = LogManager.getLogger(Main.class);
	static Logger loggerResult = LogManager.getLogger("result");
	
	public static void main(String[] args) throws MalformedURLException {
		System.setProperty("log4j.configuration", new File("resources", "log4j2.xml").toURI().toURL().toString());
		// Let user input the choice of algorithm to execute
		Scanner myObj = new Scanner(System.in);  // Create a Scanner object
		System.out.println("Enter AP for atomic predicates algorithm Or MF for maximal flows algorithm");
		String algo = myObj.nextLine();
		while (!algo.equals("AP") && !algo.equals("MF")) { // input validation
		System.out.println("Choose Correct Algorithms");
		algo = myObj.nextLine();
		}
		try { // Preparation of input
			JAXBContext jc;
			jc = JAXBContext.newInstance("it.polito.verefoo.jaxb");
			Unmarshaller u = jc.createUnmarshaller();
			SchemaFactory sf = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
			Schema schema = sf.newSchema(new File("./xsd/nfvSchema.xsd"));
			u.setSchema(schema);
			long beginAll = System.currentTimeMillis();
			try {
				Marshaller m = jc.createMarshaller();
				m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
				m.setProperty(Marshaller.JAXB_NO_NAMESPACE_SCHEMA_LOCATION, "./xsd/nfvSchema.xsd");
				// VerefooSearializer takes as parameter the type of algorithm to be used
				VerefooSerializer test = new VerefooSerializer((NFV) u.unmarshal(new FileInputStream("./testfile/RegressioneTestCases/Test9_1.xml")),algo);
				if (test.isSat()) {
					loggerResult.info("SAT");
					loggerResult.info("----------------------OUTPUT----------------------");
					StringWriter stringWriter = new StringWriter();
					m.marshal(test.getResult(), stringWriter);
					loggerResult.info(stringWriter.toString());
					loggerResult.info("--------------------------------------------------");
					//System.out.println(stringWriter);
					//System.out.println(test.getZ3Model());
				} else {
					loggerResult.info("UNSAT");
					loggerResult.info("----------------------OUTPUT----------------------");
					StringWriter stringWriter = new StringWriter();
					m.marshal(test.getResult(), stringWriter);
					loggerResult.info(stringWriter.toString());
					loggerResult.info("--------------------------------------------------");
					System.exit(1);
				}
			} catch (BadGraphError | FileNotFoundException e) {
				loggerInfo.error("Graph semantically incorrect");
				loggerInfo.error(e);
				System.exit(1);
			}
			long endAll = System.currentTimeMillis();
			loggerResult.info("time: " + (endAll - beginAll) + "ms;");
		} catch (JAXBException je) {
			loggerInfo.error("Error while unmarshalling or marshalling");
			loggerInfo.error(je);
			System.exit(1);
		} catch (ClassCastException cce) {
			loggerInfo.error("Wrong data type found in XML document");
			loggerInfo.error(cce);
			System.exit(1);
		} catch (BadGraphError e) {
			loggerInfo.error("Graph semantically incorrect");
			loggerInfo.error(e);
			System.exit(1);
		} catch (SAXException e) {
			loggerInfo.error(e);
			System.exit(1);
		}
	}

}
