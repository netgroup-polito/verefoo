package it.polito.verefoo.rest.spring;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.server.ResponseStatusException;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

@Controller
@RequestMapping(value = "/fwd/deploy")
public class DeployerController {

	FDWService service = new FDWService();
	@Autowired
	private HttpServletRequest request;



	@ApiOperation(value = "get Fortinet configuration for a node", notes = "get configuration file for fortinet device")
	@RequestMapping(value = "getFortinet/{nid}", method = RequestMethod.GET)
	@ApiResponses(value = { @ApiResponse(code = 200, message = "OK"),
			@ApiResponse(code = 404, message = "Not Found"), })
	@ResponseBody
	public void getFortinet(@PathVariable("nid") long nid, HttpServletResponse response) {
	       
			File file = service.getFile(nid,"fortinet");
			if (file==null) {
				throw new ResponseStatusException(HttpStatus.NOT_FOUND, "not found");
			}
			
			 String contentType = null;
		        contentType = request.getServletContext().getMimeType(file.getAbsolutePath());

		        if(contentType == null) {
		            contentType = "application/octet-stream";
		        }
			    response.setContentType(contentType);
			    response.setHeader("Content-disposition", "attachment; filename=" + file.getName());
			    OutputStream out;
				try {
					out = response.getOutputStream();
				    FileInputStream in = new FileInputStream(file);
				    IOUtils.copy(in,out);
				    out.close();
				    in.close();
				} catch (IOException e) {
					throw new ResponseStatusException(HttpStatus.NOT_FOUND, "not found");
				}

			    file.delete();
			}
        


	
	
	
	@ApiOperation(value = "get ipfirewall configuration for a node", notes = "get configuration file for fortinet device")
	@RequestMapping(value = "getIpfw/{nid}", method = RequestMethod.GET)
	@ApiResponses(value = { @ApiResponse(code = 200, message = "OK"),
			@ApiResponse(code = 404, message = "Not Found"), })
	@ResponseBody
	public void getIpfw(@PathVariable("nid") long nid, HttpServletResponse response) {
	       
			File file = service.getFile(nid,"ipfw");
			if (file==null) {
				throw new ResponseStatusException(HttpStatus.NOT_FOUND, "not found");
			}
			 String contentType = null;
		        contentType = request.getServletContext().getMimeType(file.getAbsolutePath());

		        if(contentType == null) {
		            contentType = "application/octet-stream";
		        }
			    response.setContentType(contentType);
			    response.setHeader("Content-disposition", "attachment; filename=" + file.getName());
			    OutputStream out;
				try {
					out = response.getOutputStream();
				    FileInputStream in = new FileInputStream(file);
				    IOUtils.copy(in,out);
				    out.close();
				    in.close();
				} catch (IOException e) {;
					throw new ResponseStatusException(HttpStatus.NOT_FOUND, "not found");
				}

			    file.delete();
			}
	

	@ApiOperation(value = "get iptables configuration for a node", notes = "get configuration file for fortinet device")
	@RequestMapping(value = "getIptables/{nid}", method = RequestMethod.GET)
	@ApiResponses(value = { @ApiResponse(code = 200, message = "OK"),
			@ApiResponse(code = 404, message = "Not Found"), })
	@ResponseBody
	public void getIptables(@PathVariable("nid") long nid, HttpServletResponse response) {
	       
			File file = service.getFile(nid,"iptables");
			if (file==null) {
				throw new ResponseStatusException(HttpStatus.NOT_FOUND, "not found");
			}
			

			 String contentType = null;
		        contentType = request.getServletContext().getMimeType(file.getAbsolutePath());

		        if(contentType == null) {
		            contentType = "application/octet-stream";
		        }
			    response.setContentType(contentType);
			    response.setHeader("Content-disposition", "attachment; filename=" + file.getName());
			    OutputStream out;
				try {
					out = response.getOutputStream();
				    FileInputStream in = new FileInputStream(file);

				    IOUtils.copy(in,out);

				    out.close();
				    in.close();
				} catch (IOException e) {
					throw new ResponseStatusException(HttpStatus.NOT_FOUND, "not found");
				}

			    file.delete();
			}
	

	@ApiOperation(value = "get openvswitch configuration for a node", notes = "get configuration file for fortinet device")
	@RequestMapping(value = "getOpenVswitch/{nid}", method = RequestMethod.GET)
	@ApiResponses(value = { @ApiResponse(code = 200, message = "OK"),
			@ApiResponse(code = 404, message = "Not Found"), })
	@ResponseBody
	public void getOpenvswitch(@PathVariable("nid") long nid,HttpServletResponse response) {
       
		File file = service.getFile(nid,"opnvswitch");
		if (file==null) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "not found");
		}
		 String contentType = null;
	        contentType = request.getServletContext().getMimeType(file.getAbsolutePath());
	        if(contentType == null) {
	            contentType = "application/octet-stream";
	        }
		    response.setContentType(contentType);
		    response.setHeader("Content-disposition", "attachment; filename=" + file.getName());
		    OutputStream out;
			try {
				out = response.getOutputStream();
			    FileInputStream in = new FileInputStream(file);
			    IOUtils.copy(in,out);
			    out.close();
			    in.close();
			} catch (IOException e) {
				throw new ResponseStatusException(HttpStatus.NOT_FOUND, "not found");
			}

		    file.delete();
		}
	
	


	
	@ApiOperation(value = "get BpfFirewall configuration for a node", notes = "get configuration file for fortinet device")
	@RequestMapping(value = "getBpfFirewall/{nid}", method = RequestMethod.GET)
	@ApiResponses(value = { @ApiResponse(code = 200, message = "OK"),
			@ApiResponse(code = 404, message = "Not Found"), })
	public void getBpfFirewall(@PathVariable("nid") long nid,HttpServletResponse response) {
		File file = service.getFile(nid,"bpf_iptables");
		//determine the type of the file
		if (file==null) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "not found");
		}
		
		 String contentType = null;
	        contentType = request.getServletContext().getMimeType(file.getAbsolutePath());

	        //if type could not be determined
	        if(contentType == null) {
	            contentType = "application/octet-stream";
	        }
		    response.setContentType(contentType);
		    response.setHeader("Content-disposition", "attachment; filename=" + file.getName());
		    OutputStream out;
			try {
				out = response.getOutputStream();
			    FileInputStream in = new FileInputStream(file);

			    // copy from in to out
			    IOUtils.copy(in,out);

			    out.close();
			    in.close();
			} catch (IOException e) {
				throw new ResponseStatusException(HttpStatus.NOT_FOUND, "not found");
			}

		    file.delete();
		}

// old version	
//	@ApiOperation(value = "get BpfFirewall configuration for a node", notes = "get configuration file for fortinet device")
//	@RequestMapping(value = "getBpfFirewall/{nid}", method = RequestMethod.GET)
//	@ApiResponses(value = { @ApiResponse(code = 200, message = "OK"),
//			@ApiResponse(code = 404, message = "Not Found"), })
//	@ResponseBody
//	public ResponseEntity<Resource> getBpfFirewall(@PathVariable("nid") long nid) {
//       
//		Resource resource = service.loadFileAsResource(nid,"bpf_iptables");
//		if (resource==null) {
//			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "not found");
//		}
//        String contentType = null;
//        try {
//            contentType = request.getServletContext().getMimeType(resource.getFile().getAbsolutePath());
//        } catch (IOException ex) {
//        	throw new ResponseStatusException(HttpStatus.NOT_FOUND, "not found");
//        }
//        if(contentType == null) {
//            contentType = "application/octet-stream";
//        }
//
//        return ResponseEntity.ok()
//                .contentType(MediaType.parseMediaType(contentType))
//                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
//                .body(resource);
//	}
	
}
