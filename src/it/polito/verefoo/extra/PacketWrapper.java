package it.polito.verefoo.extra;

import it.polito.verefoo.jaxb.EType;
import it.polito.verefoo.jaxb.Endhost;
import it.polito.verefoo.jaxb.Property;
import it.polito.verigraph.extra.PacketModel;
import it.polito.verigraph.solver.NetContext;
/**
 * This class converts our EndHost Packet Model configuration in the Verefoo
 */
public class PacketWrapper extends PacketModel {

	/**
	 * This method converts our EndHost Packet Model configuration in the Verefoo one
	 * @param eh EndHost Packet Model
	 * @param nctx Network Context.
	 * @throws BadGraphError Invalid Configuration
	 */
	public PacketWrapper(Endhost eh, NetContext nctx) throws BadGraphError {
		if(eh!=null){
			try {
				if(eh.getBody()!=null && !eh.getBody().isEmpty())
					this.setBody(String.valueOf(eh.getBody()).hashCode());
				if(eh.getEmailFrom()!=null && !eh.getEmailFrom().isEmpty())
					this.setEmailFrom(String.valueOf(eh.getEmailFrom()).hashCode());
				if(eh.getDestination()!=null && !eh.getDestination().isEmpty())
					this.setIp_dest(nctx.addressMap.get(eh.getDestination()));
				if(eh.getOptions()!=null && !eh.getOptions().isEmpty())
					this.setOptions(String.valueOf(eh.getOptions()).hashCode());
				if(eh.getProtocol()!=null){
					if(eh.getProtocol().value().equals("HTTP_RESPONSE")){
						this.setProto(nctx.HTTP_REQUEST);
					}else if(eh.getProtocol().value().equals("HTTP_RESPONSE")){
						this.setProto(nctx.HTTP_RESPONSE);
					}else if(eh.getProtocol().value().equals("POP3_REQUEST")){
						this.setProto(nctx.POP3_REQUEST);
					}else if(eh.getProtocol().value().equals("POP3_RESPONSE")){
						this.setProto(nctx.POP3_RESPONSE);
					}else{
						throw new BadGraphError("Endhost Protocol " + eh.getProtocol().value() + " not supported",EType.INVALID_NODE_CONFIGURATION);
					}
				}
				if(eh.getSequence()!=null)
					this.setSeq(eh.getSequence().intValue());
				if(eh.getUrl()!=null && !eh.getUrl().isEmpty())
					this.setUrl(String.valueOf(eh.getUrl()).hashCode());
			} catch (NumberFormatException e) {
				e.printStackTrace();
				throw new BadGraphError(e.getMessage(),EType.INVALID_NODE_CONFIGURATION);
			}
		}
	}
	/**
	 * Extract the information from the property that force certain characteristics of the packet
	 * @param prop
	 * @param nctx
	 */
	public void setProperties(Property prop, NetContext nctx) {
		
		if(prop.getHTTPDefinition() != null){
			String body = prop.getHTTPDefinition().getBody();
			String options = prop.getHTTPDefinition().getOptions();
			String url = prop.getHTTPDefinition().getUrl();
			this.setBody(body.hashCode());
			if(options != null){
				this.setOptions(options.hashCode()); 
			}
			if(url != null){
				this.setUrl(url.hashCode());
			}
			this.setProto(nctx.HTTP_REQUEST);
		}
		if(prop.getPOP3Definition() != null){
			String body = prop.getPOP3Definition().getBody();
			String email_from = prop.getPOP3Definition().getEmailFrom();
			this.setBody(body.hashCode());
			this.setEmailFrom(email_from.hashCode());
			this.setProto(nctx.POP3_REQUEST);
		}
	}

}
