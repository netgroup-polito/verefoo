package it.polito.verefoo.rest.spring;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
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
	public ResponseEntity<Resource> getFortinet(@PathVariable("nid") long nid) {
       
		Resource resource = service.loadFileAsResource(nid,"fortinet");
		//determine the type of the file
		if (resource==null) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "not found");
		}
        String contentType = null;
        try {
            contentType = request.getServletContext().getMimeType(resource.getFile().getAbsolutePath());
        } catch (IOException ex) {
           // if type error
        	throw new ResponseStatusException(HttpStatus.NOT_FOUND, "not found");
        }

        //if type could not be determined
        if(contentType == null) {
            contentType = "application/octet-stream";
        }

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
                .body(resource);
	}


	@ApiOperation(value = "get ipfirewall configuration for a node", notes = "get configuration file for fortinet device")
	@RequestMapping(value = "getIpfw/{nid}", method = RequestMethod.GET)
	@ApiResponses(value = { @ApiResponse(code = 200, message = "OK"),
			@ApiResponse(code = 404, message = "Not Found"), })
	@ResponseBody
	public ResponseEntity<Resource> getIpfw(@PathVariable("nid") long nid) {
       
		Resource resource = service.loadFileAsResource(nid,"ipfw");
		//determine the type of the file
		if (resource==null) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "not found");
		}
        String contentType = null;
        try {
            contentType = request.getServletContext().getMimeType(resource.getFile().getAbsolutePath());
        } catch (IOException ex) {
           // if type error
        	throw new ResponseStatusException(HttpStatus.NOT_FOUND, "not found");
        }

        //if type could not be determined
        if(contentType == null) {
            contentType = "application/octet-stream";
        }

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
                .body(resource);
	}
	

	@ApiOperation(value = "get iptables configuration for a node", notes = "get configuration file for fortinet device")
	@RequestMapping(value = "getIptables/{nid}", method = RequestMethod.GET)
	@ApiResponses(value = { @ApiResponse(code = 200, message = "OK"),
			@ApiResponse(code = 404, message = "Not Found"), })
	@ResponseBody
	public ResponseEntity<Resource> getIptables(@PathVariable("nid") long nid) {
       
		Resource resource = service.loadFileAsResource(nid,"iptables");
		//determine the type of the file
		if (resource==null) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "not found");
		}
        String contentType = null;
        try {
            contentType = request.getServletContext().getMimeType(resource.getFile().getAbsolutePath());
        } catch (IOException ex) {
           // if type error
        	throw new ResponseStatusException(HttpStatus.NOT_FOUND, "not found");
        }

        //if type could not be determined
        if(contentType == null) {
            contentType = "application/octet-stream";
        }

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
                .body(resource);
	}
	

	@ApiOperation(value = "get openvswitch configuration for a node", notes = "get configuration file for fortinet device")
	@RequestMapping(value = "getOpenVswitch/{nid}", method = RequestMethod.GET)
	@ApiResponses(value = { @ApiResponse(code = 200, message = "OK"),
			@ApiResponse(code = 404, message = "Not Found"), })
	@ResponseBody
	public ResponseEntity<Resource> getOpenvswitch(@PathVariable("nid") long nid) {
       
		Resource resource = service.loadFileAsResource(nid,"opnvswitch");
		//determine the type of the file
		if (resource==null) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "not found");
		}
        String contentType = null;
        try {
            contentType = request.getServletContext().getMimeType(resource.getFile().getAbsolutePath());
        } catch (IOException ex) {
           // if type error
        	throw new ResponseStatusException(HttpStatus.NOT_FOUND, "not found");
        }

        //if type could not be determined
        if(contentType == null) {
            contentType = "application/octet-stream";
        }

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
                .body(resource);
	}
	
	

	@ApiOperation(value = "get BpfFirewall configuration for a node", notes = "get configuration file for fortinet device")
	@RequestMapping(value = "getBpfFirewall/{nid}", method = RequestMethod.GET)
	@ApiResponses(value = { @ApiResponse(code = 200, message = "OK"),
			@ApiResponse(code = 404, message = "Not Found"), })
	@ResponseBody
	public ResponseEntity<Resource> getBpfFirewall(@PathVariable("nid") long nid) {
       
		Resource resource = service.loadFileAsResource(nid,"bpf_iptables");
		//determine the type of the file
		if (resource==null) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "not found");
		}
        String contentType = null;
        try {
            contentType = request.getServletContext().getMimeType(resource.getFile().getAbsolutePath());
        } catch (IOException ex) {
           // if type error
        	throw new ResponseStatusException(HttpStatus.NOT_FOUND, "not found");
        }

        //if type could not be determined
        if(contentType == null) {
            contentType = "application/octet-stream";
        }

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
                .body(resource);
	}
	
	

}
