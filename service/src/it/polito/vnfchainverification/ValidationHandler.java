/*
 * Copyright 2016 Politecnico di Torino
 * Authors:
 * Project Supervisor and Contact: Riccardo Sisto (riccardo.sisto@polito.it)
 * 
 * This file is part of Verigraph.
 * 
 * Verigraph is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of
 * the License, or (at your option) any later version.
 * 
 * Verigraph is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public
 * License along with Verigraph.  If not, see
 * <http://www.gnu.org/licenses/>.
 */
package it.polito.vnfchainverification;

import static javax.xml.XMLConstants.W3C_XML_SCHEMA_NS_URI;

import java.io.InputStream;
import java.util.Set;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.UnmarshalException;
import javax.xml.bind.Unmarshaller;
import javax.xml.namespace.QName;
import javax.xml.soap.SOAPConstants;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPFault;
import javax.xml.soap.SOAPMessage;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.ws.handler.MessageContext;
import javax.xml.ws.handler.soap.SOAPHandler;
import javax.xml.ws.handler.soap.SOAPMessageContext;

public class ValidationHandler implements SOAPHandler<SOAPMessageContext> {
	protected String schemaLocation = "/META-INF/checkIsolationProperty.xsd";
	protected String jaxbPackage = "it.polito.vnfchainverification";

	@Override
	public boolean handleMessage(SOAPMessageContext context) {
		// Is this an inbound message, i.e., a request?
		Boolean isOutbound = (Boolean)context.get(MessageContext.MESSAGE_OUTBOUND_PROPERTY);

		// Validate the SOAP only if it's inbound
	    if (!isOutbound) {
			SOAPMessage msg = context.getMessage();
			if (msg==null) {
				return false; // stop message processing
			}
		    try {
				SOAPBody body = msg.getSOAPBody();
	
				// Ensure that the SOAP message has a body.
				if (body == null) {
				    generateSOAPFault(msg, "No message body.");
				    return false;
				}
				InputStream schemaStream = ValidationHandler.class.getResourceAsStream(schemaLocation);
				JAXBContext jc = JAXBContext.newInstance(jaxbPackage);
	            Unmarshaller u = jc.createUnmarshaller();
				
	            SchemaFactory sf = SchemaFactory.newInstance(W3C_XML_SCHEMA_NS_URI);
	            try {
	                Schema schema = sf.newSchema(new StreamSource(schemaStream));
	                u.setSchema(schema);
	            } catch (org.xml.sax.SAXException se) {
	            	generateSOAPFault(msg, "Unable to validate due to internal schema error.");
	            	return false;
	            }
	            u.unmarshal(body.getFirstChild());
		    }
		    catch(SOAPException e) { return false; }
		    catch( UnmarshalException ue ) { 
		    	generateSOAPFault(msg, "Invalid input message body.");
		    	return false;
		    }
		    catch (JAXBException e) { 
		    	generateSOAPFault(msg, "Unable to validate input message body."); 
		    	return false;
		    }
		}
	    return true; // continue down the chain	
	}

	@Override
	public boolean handleFault(SOAPMessageContext context) {
		return true;
	}

	@Override
	public void close(MessageContext context) {
		
	}

	@Override
	public Set<QName> getHeaders() {
		return null;
	}
	
    private void generateSOAPFault(SOAPMessage msg, String reason) {
    	try {
    	    SOAPBody body = msg.getSOAPBody();
    	    body.removeContents();
    	    SOAPFault fault = body.addFault();
    	    QName fault_name = 
    		new QName(SOAPConstants.URI_NS_SOAP_1_1_ENVELOPE, "Client");
    	    fault.setFaultCode(fault_name);
    	    fault.setFaultString(reason);
    	}
    	catch(SOAPException e) { }
    }

}
