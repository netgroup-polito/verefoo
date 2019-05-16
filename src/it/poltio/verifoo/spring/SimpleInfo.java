package it.poltio.verifoo.spring;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;



@Controller
public class SimpleInfo {
	
	//ClientToVerifoo clientVerifoo = new ClientToVerifoo(convertFileToString("graph.yml"));
  @RequestMapping(method = RequestMethod.GET, value="/")
  @ResponseBody
  public String infoVerifoo() {
	  System.out.println("Info from Verifoot");
	  return "hi";
  }
  
  
}