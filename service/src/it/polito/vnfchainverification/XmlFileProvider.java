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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.ws.Provider;
import javax.xml.ws.WebServiceException;
import javax.xml.ws.WebServiceProvider;
import javax.xml.ws.ServiceMode;
import javax.xml.ws.Service;

@WebServiceProvider
@ServiceMode(value=Service.Mode.PAYLOAD)
public class XmlFileProvider implements Provider<Source> {
	File file;
	
	public XmlFileProvider(String filename) throws FileNotFoundException {
		super();
		this.file = new File(filename);
		if (!file.canRead())
			throw new FileNotFoundException();
	}

	public Source invoke(Source source) {
		Source reply;
		try {
			reply = new StreamSource(new FileInputStream(file));
	        return reply;

		} catch (FileNotFoundException e) {
			e.printStackTrace();
			throw new WebServiceException(e);
		}
     }
}
