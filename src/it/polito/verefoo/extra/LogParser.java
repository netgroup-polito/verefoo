package it.polito.verefoo.extra;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

// auxiliary class for parsing the performance test output 
public class LogParser {
	private static final String FILE_HEADER = ",Seed,Assertions,CheckerTime,Time";
	private static final String AVG_HEADER = ",AVG,MAX,MIN";
	private static final String NEW_LINE_SEPARATOR = "\n";

	public static void main(String[] args) throws IOException {

		//here you have to set the name of the output csv file
		FileWriter csvOutputFile = new FileWriter("./log/result.csv");
		
		//here you have to set the name of the input log file
		File testFile = new File("./log/66365_AP40_PR60_N0_L0_20_name.log"); 
		
		
		
		try (BufferedReader br = new BufferedReader(new FileReader(testFile))) {
		    String logEntryLine;
		    while ((logEntryLine = br.readLine()) != null) {
	
				String[] splitedLine = logEntryLine.split("\\s+");
				System.out.println(logEntryLine);


				if (splitedLine[4].startsWith("===========FILE ")) {
					csvOutputFile.append(splitedLine[4]+" "+FILE_HEADER.toString());
					csvOutputFile.append(NEW_LINE_SEPARATOR); 
					}else if(splitedLine[4].startsWith("Seed")){
								String[] splited = splitedLine[4].split(":");
								csvOutputFile.append(","+ splited[1]);
							}else if(splitedLine[4].startsWith("time")){
								String[] splited = splitedLine[5].split("m");
								float time = Float.parseFloat(splited[0])/1000;
								csvOutputFile.append(","+ time);
								csvOutputFile.append(NEW_LINE_SEPARATOR);
							}else if(splitedLine[4].startsWith("UNSAT")){
								csvOutputFile.append(NEW_LINE_SEPARATOR);
							}else if(splitedLine[4].startsWith("AVG")){
								break;
							}else if(splitedLine[4].startsWith("MAX")){
								break;
							}else if(splitedLine[4].startsWith("MIN")){
								break;
							}
						
					}
				
		}


		csvOutputFile.flush();
		csvOutputFile.close();
		

	}

}
