package it.polito.verifoo.rest.test;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.stream.Collectors;

import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
/**
 * 
 * This class implements the threads that will test the concurrency of the web service
 *
 */
public class Concurrency {
	private ArrayList<String> file=new ArrayList<String>();
    //private final String resturl="http://restfoo.eu-de.mybluemix.net/deployment";
	private final String resturl = System.getProperty("it.polito.rest.test.URL")+"/deployment";
	private ArrayList<Thread> threadlist=new ArrayList<Thread>();
	public Concurrency(String file1,String file2,String file3,String file4) throws IOException {
		this.file.add(java.nio.file.Files.lines(Paths.get(file1)).collect(Collectors.joining("\n")));
		this.file.add(java.nio.file.Files.lines(Paths.get(file2)).collect(Collectors.joining("\n")));
		this.file.add(java.nio.file.Files.lines(Paths.get(file3)).collect(Collectors.joining("\n")));
		this.file.add(java.nio.file.Files.lines(Paths.get(file4)).collect(Collectors.joining("\n")));
	}
	public void runConcurrent(int nThreaddiv4){
		for(int i=0;i<nThreaddiv4;i++){
			for(int j=0;j<4;j++){
				Thread t=new Thread(new MyThread(i,j));
				t.start();
				threadlist.add(t);
			}
		}
		threadlist.forEach(t->{
			try {
				t.join();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		});
	}
	private class MyThread implements Runnable{
		private int i;
		private int j;
		MyThread(int i,int j){
			this.i=i;
			this.j=j;
		}
		@Override
		public void run() {
			try{
				Response res=ClientBuilder.newClient()
						.target(resturl)
						.request(MediaType.APPLICATION_XML)
						.accept(MediaType.APPLICATION_XML)
						.post(Entity.entity(file.get(j),MediaType.APPLICATION_XML));
				if(res.getStatusInfo().equals(Status.OK)){
					FileWriter fw=new FileWriter("result"+i+"-"+j+".xml");
					fw.write(res.readEntity(String.class));
					fw.close();
					System.out.println("Output written to result"+i+"-"+j+".xml");
				}else{
					System.err.println(res.readEntity(String.class));
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}			
		}
		
	}
}
