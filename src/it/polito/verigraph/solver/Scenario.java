package it.polito.verigraph.solver;

import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import it.polito.verigraph.model.Graph;
import it.polito.verigraph.model.Node;

public class Scenario {
	Graph graph;
	List<String> path=new ArrayList<String>();
	Map<String, Map<String, String>> chn=new HashMap<String, Map<String, String>>();
	Map<String, Map<String, String>> routing=new HashMap<String, Map<String, String>>();
	Map<String, Map<String, String>> config_obj=new HashMap<String, Map<String, String>>();
	Map<String,List<String>> config_array=new HashMap<String, List<String>>();
	
	
	List<String> nodes_names=new ArrayList<String>();
	List<String> nodes_types=new ArrayList<String>();
	List<String> nodes_addresses=new ArrayList<String>();
	List<String> nodes_ip_mappings=new ArrayList<String>();
	Map<String, Map<String, String>> nodes_rt=new HashMap<String, Map<String, String>>();
	
	public Scenario(Graph graph, List<String> s) {

		this.graph=graph;
		this.path=s;
	}

	@SuppressWarnings("unchecked")
	public void createScenario() {
		List<Node> nodes=new ArrayList<Node>();

		//creazione lista di nodi
		for(String s : path){
			Node n=graph.searchNodeByName(s);
			if(n==null){
				System.out.println("Nodo presente nel path (sanitizesPath) non presente nel grafo");
			}
			else{
				nodes.add(n);
			}
		}

		//per ogni nodo si crea una mappa da isnerire in chn
		for(int i=0; i<nodes.size(); i++){
			
			String name=nodes.get(i).getName();
			nodes_names.add(name);
			
			String type=nodes.get(i).getFunctional_type().toLowerCase();
			nodes_types.add(type);
			
			nodes_addresses.add("ip_"+name);
			nodes_ip_mappings.add("ip_"+name);
			
			
			JsonNode configuration=nodes.get(i).getConfiguration().getConfiguration();

			//fill chn
			Map<String, String> nodo=new HashMap<String, String>();
			nodo.put("address", "ip_" + name);
			nodo.put("functional_type", type);
			chn.put(name, nodo);

			//fill routing
			Map<String, String> route=new HashMap<String, String>();			
			
			
				for(int k=i-1; ; k--){		
					if(k==-1)
						break;
					else{
						String ip_dest="ip_"+nodes.get(k).getName();
						String next_hop=nodes.get(i-1).getName();
						route.put(ip_dest, next_hop); 
					}
					
				}
			
			for(int j=i+1; j<nodes.size(); j++){
				String ip_dest="ip_"+nodes.get(j).getName();
				String next_hop=nodes.get(i+1).getName();
				route.put(ip_dest, next_hop);
			}
			routing.put(name, route);
			
			
		

		//fill configuration
		setConfiguration(name, type, configuration, config_obj, config_array);
					
		}
		
}

	@SuppressWarnings("unchecked")
	private void setConfiguration(String name, String type, JsonNode configuration,
			Map<String, Map<String, String>> config_obj2, Map<String, List<String>> config_array2) {
		String empty="[]";
		switch(type.toUpperCase()){
		case "FIREWALL":{			
			Map<String, String> map=new LinkedHashMap();		
			
			if(!configuration.toString().equals(empty)){	
				ObjectMapper mapper=new ObjectMapper();	
				Map<String, String> map_tmp=new LinkedHashMap();
				String input;
				Matcher matcher = Pattern.compile("\\[([^\\]]*)\\]").matcher(configuration.toString());
				if(matcher.find()){
					input=matcher.group(1);
				}else
					input=configuration.toString();
				Pattern pattern=Pattern.compile("\\{([^\\}]*)\\}");
				List<String> list = new ArrayList<String>();
				Matcher match= pattern.matcher(input);
				while (match.find()) {
					list.add(match.group());
				}	

				try{
					for(String string : list){
						//map_tmp.putAll(mapper.readValue(string, LinkedHashMap.class));		
						map.putAll(mapper.readValue(string, LinkedHashMap.class));
						
					}					

				}catch(JsonGenerationException e) {

					e.printStackTrace();

				} catch (JsonMappingException e) {

					e.printStackTrace();

				} catch (IOException e) {

					e.printStackTrace();

				}



			}
			else{
				System.out.println("firewall empty");

			}
			
			config_obj.put(name, map);
			break;


		}



		case "ANTISPAM":{
					
			List<String> source=new ArrayList<String>();

			if(!configuration.toString().equals(empty)){
				ObjectMapper mapper=new ObjectMapper();			
			

				try {

					source = mapper.readValue(configuration.toString(), ArrayList.class);
					

				} catch (JsonGenerationException e) {

					e.printStackTrace();

				} catch (JsonMappingException e) {

					e.printStackTrace();

				} catch (IOException e) {

					e.printStackTrace();

				}

			}
			else{			
				System.out.println("antispam empty");
			}
			config_array.put(name, source);
			break;
		}
		case "CACHE":{
			
			List<String> resource=new ArrayList<String>();

			if(!configuration.toString().equals(empty)){
				ObjectMapper mapper=new ObjectMapper();

				
				try {
					List<String> list_tmp=new ArrayList<String>();
					list_tmp = mapper.readValue(configuration.toString(), ArrayList.class);
					if(list_tmp!=null){
					
						for(String s : list_tmp){
							resource.add("ip_"+s);
						}
					}
					

				} catch (JsonGenerationException e) {

					e.printStackTrace();

				} catch (JsonMappingException e) {

					e.printStackTrace();

				} catch (IOException e) {

					e.printStackTrace();

				}			

			}
			else{
				System.out.println("cache vuota");			
			}
			config_array.put(name, resource);
			break;

		}
		case "DPI":{
			
			List<String> notAllowed=new ArrayList<String>();

			if(!configuration.toString().equals(empty)){
				ObjectMapper mapper=new ObjectMapper();
			
				try {

					notAllowed = mapper.readValue(configuration.toString(), ArrayList.class);
					

				} catch (JsonGenerationException e) {

					e.printStackTrace();

				} catch (JsonMappingException e) {

					e.printStackTrace();

				} catch (IOException e) {

					e.printStackTrace();

				}

			}
			else{
				System.out.println("dpi empty");
			}
			config_array.put(name, notAllowed);
			break;
		}
		case "ENDHOST":{
			
			Map<String, String> map=new LinkedHashMap();	

			if(!configuration.toString().equals(empty)){
				ObjectMapper mapper=new ObjectMapper();		
				Map<String, String> map_tmp=new LinkedHashMap();	
				String input;
				Matcher matcher = Pattern.compile("\\[([^\\]]*)\\]").matcher(configuration.toString());
				if(matcher.find()){
					input=matcher.group(1);
				}else
					input=configuration.toString();

				try{
					map=mapper.readValue(input, java.util.LinkedHashMap.class);
					String ip=map.get("destination");
					if(ip!=null){						
						map.put("destination", "ip_"+ip);					
						
					}

				}catch(JsonGenerationException e) {

					e.printStackTrace();

				} catch (JsonMappingException e) {

					e.printStackTrace();

				} catch (IOException e) {

					e.printStackTrace();

				}


			}
			else{
				System.out.println("endhost empty");
			}
			config_obj.put(name, map);
			break;
		}
		case "ENDPOINT":{
			Map<String, String> map=new LinkedHashMap();	
			config_obj.put(name, map);
			break;
		}
		case "FIELDMODIFIER":{
			Map<String, String> map=new LinkedHashMap();
			
			if(!configuration.toString().equals(empty)){
				ObjectMapper mapper=new ObjectMapper();	
				
				if(!configuration.toString().isEmpty()){				
					String input;
					Matcher matcher = Pattern.compile("\\[([^\\]]*)\\]").matcher(configuration.toString());
					if(matcher.find()){
						input=matcher.group(1);
					}else
						input=configuration.toString();

					Pattern pattern=Pattern.compile("\\{([^\\}]*)\\}");
					List<String> list = new ArrayList<String>();
					Matcher match= pattern.matcher(input);
					while (match.find()) {
						list.add(match.group());
					}	

					try{
						for(String string : list){
							map.putAll(mapper.readValue(string, LinkedHashMap.class));	
							
							
						}

					}catch(JsonGenerationException e) {

						e.printStackTrace();

					} catch (JsonMappingException e) {

						e.printStackTrace();


					} catch (IOException e) {

						e.printStackTrace();

					}
				}	

			}
			else{
				System.out.println("fieldmodifier empty");
			}
			config_obj.put(name, map);
			break;
		}

		case "MAILCLIENT":{
			Map<String, String> map=new LinkedHashMap();
			
			ObjectMapper mapper=new ObjectMapper();			
		
			String input;
			Matcher matcher = Pattern.compile("\\[([^\\]]*)\\]").matcher(configuration.toString());
			if(matcher.find()){
				input=matcher.group(1);
			}else
				input=configuration.toString();
			Map<String, String> map_tmp=new LinkedHashMap();
			Pattern pattern=Pattern.compile("\\{([^\\}]*)\\}");
			List<String> list = new ArrayList<String>();
			Matcher match= pattern.matcher(input);
			while (match.find()) {
				list.add(match.group());
			}	

			try{
				for(String string : list){
					map.putAll(mapper.readValue(string, LinkedHashMap.class));	
					
					
				}	
				String ip=map.get("mailserver");	
				if(ip!=null){
					map.put("mailserver", "ip_"+ip);
						if(!path.contains(ip)){
							if(!nodes_addresses.contains("ip_"+ip))
								nodes_addresses.add("ip_"+ip);
						}
				}	
			}catch(JsonGenerationException e) {

				e.printStackTrace();

			} catch (JsonMappingException e) {

				e.printStackTrace();

			} catch (IOException e) {

				e.printStackTrace();

			}
		config_obj.put(name, map);
			break;
		}
		case "MAILSERVER":{
			Map<String, String> map=new LinkedHashMap();	
			if(!configuration.toString().equals(empty)){
				ObjectMapper mapper=new ObjectMapper();	
					
				String input;
				Matcher matcher = Pattern.compile("\\[([^\\]]*)\\]").matcher(configuration.toString());
				if(matcher.find()){
					input=matcher.group(1);
				}else
					input=configuration.toString();

				Pattern pattern=Pattern.compile("\\{([^\\}]*)\\}");
				List<String> list = new ArrayList<String>();
				Matcher match= pattern.matcher(input);
				while (match.find()) {
					list.add(match.group());
				}	

				try{
					for(String string : list){
						/*1 solo object presente*/
						map.putAll(mapper.readValue(string, LinkedHashMap.class));	
						
					}
				}catch(JsonGenerationException e) {

					e.printStackTrace();

				} catch (JsonMappingException e) {

					e.printStackTrace();

				} catch (IOException e) {

					e.printStackTrace();

				}


			}
			else{
				System.out.println("mailserver empty");
			}
		config_obj.put(name, map);
			break;
		}
		case "NAT":{
		
			List<String> source=new ArrayList<String>();

			if(!configuration.toString().equals(empty)){
				ObjectMapper mapper=new ObjectMapper();
				List<String> list=new ArrayList<String>();
				try {

					list = mapper.readValue(configuration.toString(), ArrayList.class);
					if(!list.isEmpty()){
						for(String s : list){
							source.add("ip_"+s);
						}
					}



				} catch (JsonGenerationException e) {

					e.printStackTrace();

				} catch (JsonMappingException e) {

					e.printStackTrace();

				} catch (IOException e) {

					e.printStackTrace();

				}

			}
			else{
				System.out.println("nat empty");
			}
		config_array.put(name, source);
			break;
		}


		case "VPNACCESS":{
		
			ObjectMapper mapper=new ObjectMapper();	
			Map<String, String> map=new LinkedHashMap();	
			String input;
			Matcher matcher = Pattern.compile("\\[([^\\]]*)\\]").matcher(configuration.toString());
			if(matcher.find()){
				input=matcher.group(1);
			}else
				input=configuration.toString();
			Map<String, String> map_tmp=new LinkedHashMap();	
			Pattern pattern=Pattern.compile("\\{([^\\}]*)\\}");
			List<String> list = new ArrayList<String>();
			Matcher match= pattern.matcher(input);
			while (match.find()) {
				list.add(match.group());
			}	

			try{
				for(String string : list){
					map.putAll(mapper.readValue(string, LinkedHashMap.class));					
				}
				String ip=map.get("vpnexit");
				if(ip!=null){
					map.put("vpnexit", "ip_"+ip);
					if(!path.contains(ip)){
						if(!nodes_addresses.contains("ip_"+ip))
							nodes_addresses.add("ip_"+ip);
					}
				}
			}catch(JsonGenerationException e) {

				e.printStackTrace();

			} catch (JsonMappingException e) {

				e.printStackTrace();

			} catch (IOException e) {

				e.printStackTrace();

			}
			config_obj.put(name, map);
			break;
		}
		case "VPNEXIT":{
			
			ObjectMapper mapper=new ObjectMapper();	
			Map<String, String> map=new LinkedHashMap();	
			String input;
			Matcher matcher = Pattern.compile("\\[([^\\]]*)\\]").matcher(configuration.toString());
			if(matcher.find()){
				input=matcher.group(1);
			}else
				input=configuration.toString();
			Map<String, String> map_tmp=new LinkedHashMap();
			Pattern pattern=Pattern.compile("\\{([^\\}]*)\\}");
			List<String> list = new ArrayList<String>();
			Matcher match= pattern.matcher(input);
			while (match.find()) {
				list.add(match.group());
			}	

			try{
				for(String string : list){
					/* solo 1 stringa presente */
					map_tmp.putAll(mapper.readValue(string, LinkedHashMap.class));					
				}
				String ip=map.get("vpnaccess");
				if(ip!=null){
					map.put("vpnaccess", "ip_"+ip);	
					if(!path.contains(ip)){	
						if(!nodes_addresses.contains("ip_"+ip))
							nodes_addresses.add("ip_"+ip);
					}
				}
			}catch(JsonGenerationException e) {

				e.printStackTrace();

			} catch (JsonMappingException e) {

				e.printStackTrace();

			} catch (IOException e) {

				e.printStackTrace();

			}
			config_obj.put(name, map);
			break;
		}
		case "WEBCLIENT":{
		
			Map<String, String> map=new LinkedHashMap();
			ObjectMapper mapper=new ObjectMapper();
			
			String input;	
			Matcher matcher = Pattern.compile("\\[([^\\]]*)\\]").matcher(configuration.toString());
			if(matcher.find()){
				input=matcher.group(1);
			}else
				input=configuration.toString();

			Pattern pattern=Pattern.compile("\\{([^\\}]*)\\}");
			List<String> list = new ArrayList<String>();
			Matcher match= pattern.matcher(input);
			while (match.find()) {
				list.add(match.group());
			}	

			try{
				for(String string : list){
					/*solo 1 stringa presente */
					map.putAll(mapper.readValue(string, LinkedHashMap.class));						
				}
				
			}catch(JsonGenerationException e) {

				e.printStackTrace();

			} catch (JsonMappingException e) {

				e.printStackTrace();

			} catch (IOException e) {

				e.printStackTrace();

			}
		config_obj.put(name, map);
			break;
		}
		case "WEBSERVER":{
			
			Map<String, String> map=new LinkedHashMap();	
			if(!configuration.toString().equals(empty)){
				ObjectMapper mapper=new ObjectMapper();		
			
				if(configuration.toString().equals(empty)){
					break;
				}else{
					String input;
					Matcher matcher = Pattern.compile("\\[([^\\]]*)\\]").matcher(configuration.toString());
					if(matcher.find()){
						input=matcher.group(1);
					}else
						input=configuration.toString();

					try{
						/*readValue legge solo 1 valore di 1 object*/
						map=mapper.readValue(input, java.util.LinkedHashMap.class);
						
					}catch(JsonGenerationException e) {

						e.printStackTrace();

					} catch (JsonMappingException e) {

						e.printStackTrace();

					} catch (IOException e) {

						e.printStackTrace();

					}
				}	
			}
			else{
				System.out.println("webserver empty");
			}
			config_obj.put(name, map);
			break;
		}
		}
	}

	private void checkConfiguration(List<String> source) {
		for(int i=0; i<source.size(); i++){
			if(!path.contains(source.get(i))){	
				if(!nodes_addresses.contains("ip_"+source.get(i)))
					nodes_addresses.add("ip_"+source.get(i));
						
			}			
		}
		
	}

	private void checkConfiguration(Map<String, String> map) {
		Map<String, String> m=new HashMap<String, String>();
		for(Map.Entry<String, String> a : map.entrySet()){
			if(a.getKey()!=null){				
			
				if(!path.contains(a.getKey())){	
					if(!nodes_addresses.contains("ip_"+a.getKey()))
						nodes_addresses.add("ip_"+a.getKey());
				}
			}
			if(a.getValue()!=null){
				if(!path.contains(a.getValue())){	
					if(!nodes_addresses.contains("ip_"+a.getValue()))
						nodes_addresses.add("ip_"+a.getValue());
				}
			}
		}
	
	}

		

		
	}
