package it.polito.verifoo.rest.test;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LogParser {
	private static final String FILE_HEADER = ",Seed,Assertions,CheckerTime,Time";
	private static final String AVG_HEADER = ",AVG,MAX,MIN";
	private static final String COMMA_DELIMITER = ",";
	private static final String NEW_LINE_SEPARATOR = "\n";

	public static void main(String[] args) throws IOException {

		FileWriter csvOutputFile = new FileWriter("./log/result.csv");
		FileWriter csvAvgFile = new FileWriter("./log/average.csv");
		// Write the CSV file header
		//csvOutputFile.append(FILE_HEADER.toString());
		// Add a new line separator after the header
		//csvOutputFile.append(NEW_LINE_SEPARATOR);
		csvAvgFile.append(AVG_HEADER.toString());
		csvAvgFile.append(NEW_LINE_SEPARATOR);

		File testFile = new File("./log/result.log");
		Scanner s = new Scanner(testFile);
		int count = 0;

		String fileName = "(\\d{4}-\\d{2}-\\d{2}) (\\d{2}:\\d{2}:\\d{2}.\\d{3}) \\[(.*)\\] ([^ ]*) +([^ ]*) - (.*)$";
		while (s.hasNextLine()) {
			String logEntryLine = s.nextLine();

			Pattern p = Pattern.compile(fileName);
			Matcher matcher = p.matcher(logEntryLine);
			System.out.println(matcher.matches());
			if (!matcher.matches()) {
				System.err.println("Bad log entry (or problem with RE?):");
				System.err.println(logEntryLine);
				return;
			}

			/*
			 * System.out.println("Date&Time: " + matcher.group(1));
			 * System.out.println("Hostname: " + matcher.group(2));
			 * System.out.println("Program Name: " + matcher.group(3));
			 * System.out.println("Log: " + matcher.group(4));
			 */

			if (matcher.group(6).startsWith("===========FILE ")) {
				csvOutputFile.append(matcher.group(6)+" "+FILE_HEADER.toString());
				csvOutputFile.append(NEW_LINE_SEPARATOR);
				while (s.hasNextLine()) {
					String results = s.nextLine();
					Matcher matcher2 = p.matcher(results);
					if (matcher2.matches()) {
						if(matcher2.group(6).startsWith("Seed")){
							String[] splited = matcher2.group(6).split(":");
							csvOutputFile.append(","+ splited[1]);
						}else if(matcher2.group(6).startsWith("---")){
							String[] splited = matcher2.group(6).split("\\s+");
							csvOutputFile.append(","+ splited[2]);
						}else if(matcher2.group(6).startsWith("Only")){
							String[] splited = matcher2.group(6).split("\\s+");
							csvOutputFile.append(","+splited[2].replaceAll("\\D+",""));
						}else if(matcher2.group(6).startsWith("time")){
							String[] splited = matcher2.group(6).split("\\s+");
							csvOutputFile.append(","+splited[1].replaceAll("\\D+",""));
							csvOutputFile.append(NEW_LINE_SEPARATOR);
						}else if(matcher2.group(6).startsWith("UNSAT")){
							csvOutputFile.append(NEW_LINE_SEPARATOR);
						}else if(matcher2.group(6).startsWith("AVG")){
							csvAvgFile.append(matcher.group(6)+","+matcher2.group(6).split("\\s+")[4].replaceAll("\\D+",""));
						}else if(matcher2.group(6).startsWith("MAX")){
							csvAvgFile.append(","+matcher2.group(6).split("\\s+")[4].replaceAll("\\D+",""));
						}else if(matcher2.group(6).startsWith("MIN")){
							csvAvgFile.append(","+matcher2.group(6).split("\\s+")[4].replaceAll("\\D+",""));
							csvAvgFile.append(NEW_LINE_SEPARATOR);
							break;
						}
					}
				}
			}
			

		}
		csvOutputFile.flush();
		csvAvgFile.flush();
		
		csvOutputFile.close();
		csvAvgFile.close();
		

		s.close();

	}

}
