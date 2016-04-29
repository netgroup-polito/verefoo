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
package it.polito.vnfchainverification.server;

import java.io.FileNotFoundException;

import javax.xml.ws.Endpoint;
import javax.xml.ws.http.HTTPBinding;

import it.polito.vnfchainverification.VNFChainVerificationImpl;
import it.polito.vnfchainverification.XmlFileProvider;

public class VNFChainVerificationServer {

	public static void main(String[] args) {
		Endpoint e;
		String xsdFilename = "generated/classes/META-INF/checkIsolationProperty.xsd";
		String xsdURL = "http://localhost:8081/WebServiceSample/checkIsolationProperty.xsd";
		try {
			e = Endpoint.create( HTTPBinding.HTTP_BINDING,
			        	 new XmlFileProvider(xsdFilename));
			e.publish(xsdURL);
		} catch (FileNotFoundException e1) {
			System.err.println("Unable to open xsd file");
			e1.printStackTrace();
		}
		
		Endpoint.publish(
				"http://localhost:8081/WebServiceSample/VNFChainVerificationService",
				new VNFChainVerificationImpl());
		System.out.println("Server running");
	}
}
