package it.polito.verefoo.rest.spring;

import static org.junit.Assert.fail;

import java.io.File;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import it.polito.verefoo.extra.*;

import javax.xml.XMLConstants;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.server.ResponseStatusException;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import it.polito.verefoo.VerefooSerializer;
import it.polito.verefoo.extra.TestCaseGeneratorAmsterdam;
import it.polito.verefoo.jaxb.NFV;

@Controller
@RequestMapping(value = "/adp/test")
public class TestCasesController {
	
	private static final int N = 1;
	String prefix = new String("Isol");
	String IPClient[] = new String[N];
	String IPAllocationPlace[] = new String[N];
	String IPServer[] = new String[N];
	Random rand;
	
	private long totTime = 0;
	private long maxTotTime = 0,minTotTime = 0;
	private int nSAT = 0, nUNSAT = 0, i = 0, err = 0;
	NFV root;
	private ch.qos.logback.classic.Logger logger; 
			

	private Logger loggerModel = LogManager.getLogger("model");

	
	@ApiOperation(value = "runTestCases", notes = "run test cases")
	@RequestMapping(value = "", method = RequestMethod.GET)
	@ApiResponses(value = {
		    		@ApiResponse(code = 200, message = "Ok"),
		    		@ApiResponse(code = 400, message = "Bad Request")
		    		})
	@ResponseBody
	public void runtestCase(@RequestParam(value="id", required = true) Integer id, //0 only allocation, 1 only policies, 2 both
							@RequestParam(value="seed", required = true) Integer seed,
							@RequestParam(value="i", required = true) Integer i,
							@RequestParam(value="j", required = true) Integer j,
							@RequestParam(value="logfile", required = true) String logfile
							) {
		String pathfile = "/home/verefoo/log/" + logfile;
		logger = Package1LoggingClass.createLoggerFor(logfile, pathfile);
		rand = new Random(seed);
		int k=0;
		try {

			List<TestCaseGeneratorAmsterdam> nfv = new ArrayList<>();

	
			if(id == 0) {
				nfv.add(new TestCaseGeneratorAmsterdam(prefix + i + "AP" + j + "PR", i, 0, j, seed));
			} else if(id == 1) {
				nfv.add(new TestCaseGeneratorAmsterdam(prefix + i + "AP" + j + "PR", i, j, 0, seed));
			}
			
			
			
			
			for(TestCaseGeneratorAmsterdam f : nfv){
				totTime = 0;
				maxTotTime = 0;
				minTotTime = Integer.MAX_VALUE;
				nSAT = 0;
				nUNSAT = 0;
				i = 0;
				err = 0;
				logger.info("===========FILE " + f.getName() + "===========");
					

		        
		        do{
		        	for(k = 0; k < N; k++) {
							try {
								
					         

					             //no random
					             //root = f.changeIP(IPClient[k], IPAllocationPlace[k], IPServer[k]);
					             //random
					             root = f.changeIP(seed + k*10000);
					             
					             //no random
					             //logger.debug("Client: "+ IPClient[k] +" AllocationPlace: "+  IPAllocationPlace[k] + " IPServer: "+ IPServer[k]);
								
					             //random
					             int seedPrint = seed + k*10000;
					             logger.debug("Seed:" + seedPrint);
					             //for debug purpose 
								 //m.marshal( testCoarse(root), System.out );  
								 i++;
								 NFV resultNFV = testCoarse(root);
							} catch (Exception e) {
								e.printStackTrace();
								err++;
							}
					
		        	}
				}while(i<1);
				
				logger.info("Ok -> " + k + " / Error -> " + err);
				if(nSAT > 0) {
					logger.info("Total time -> " + (totTime/nSAT) + "ms");
				}

			}

		}catch(Exception e) {
			e.printStackTrace();
			throw new ResponseStatusException(
					  HttpStatus.BAD_REQUEST, "bad request"
					);
		}
		

	}
	
	
	
	private NFV testCoarse(NFV root) throws Exception{
		long beginAll=System.currentTimeMillis();
		VerefooSerializer test = new VerefooSerializer(root);
		
		long endAll=System.currentTimeMillis();
		 if(test.isSat()){
			nSAT++;
			maxTotTime = maxTotTime<(endAll-beginAll)? (endAll-beginAll) : maxTotTime;
			minTotTime = minTotTime>(endAll-beginAll)? (endAll-beginAll) : minTotTime;
			totTime += (endAll-beginAll);
		 }
	 	else{
	 		logger.debug("UNSAT");	
			nUNSAT++;
	 	}
		
        return test.getResult();
	}
	
}
