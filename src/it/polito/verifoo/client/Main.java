package it.polito.verifoo.client;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;

import it.polito.verifoo.rest.jaxb.NFV;

public class Main {

	public static void main(String[] args) {
        if (args.length == 0){
            System.out.println("Specify XML Filename as parameter!");
            return;
        }
        /*
        JAXBContext jc = JAXBContext.newInstance( "it.polito.verifoo.rest.jaxb" );
        Unmarshaller u = jc.createUnmarshaller();
        NFV root = (NFV) u.unmarshal();
*/
		try {
			String xmlread=java.nio.file.Files.lines(Paths.get(args[0])).collect(Collectors.joining("\n"));
			//System.out.println(xmlread);
			Response res = ClientBuilder.newClient()
					.target("http://127.0.0.1:8080/verifoo")
					.path("/rest")
					.request(MediaType.APPLICATION_XML)
					.accept(MediaType.APPLICATION_XML)
					.post(Entity.entity(xmlread,MediaType.APPLICATION_XML));
			FileWriter fw=new FileWriter("result.xml");
			fw.write(res.readEntity(String.class));
			fw.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		

	}

}
