package it.polito.verifoo.client;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.stream.Collectors;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

public class Main {

	public static void main(String[] args) {
        if (args.length == 0){
            System.out.println("Specify XML Filename as parameter!");
            return;
        }
		try {
			String xmlread=java.nio.file.Files.lines(Paths.get(args[0])).collect(Collectors.joining("\n"));
			Response res = ClientBuilder.newClient()
					.target("http://127.0.0.1:8080/verifoo")
					.path("/rest")
					.request(MediaType.APPLICATION_XML)
					.accept(MediaType.APPLICATION_XML)
					.post(Entity.entity(xmlread,MediaType.APPLICATION_XML));
			if(res.getStatusInfo()==Status.OK){
				FileWriter fw=new FileWriter("result.xml");
				fw.write(res.readEntity(String.class));
				fw.close();
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
