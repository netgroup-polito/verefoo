package it.polito.verifoo.rest.common;

import it.polito.verifoo.rest.jaxb.EType;
import it.polito.verifoo.rest.jaxb.Endhost;
import it.polito.verigraph.mcnet.components.NetContext;
import it.polito.verigraph.mcnet.netobjs.PacketModel;
/**
 * This class convert our EndHost Packet Model configuration in the verigraph one
 */
public class PacketWrapper extends PacketModel {

	/**
	 * This class convert our EndHost Packet Model configuration in the verigraph one
	 * @param eh EndHost Packet Model
	 * @param nctx Network Context.
	 * @throws BadGraphError Invalid Configuration
	 */
	public PacketWrapper(Endhost eh, NetContext nctx) throws BadGraphError {
		if(eh!=null){
			try {
				if(eh.getBody()!=null && !eh.getBody().isEmpty())
					this.setBody(eh.getBody().hashCode());
				if(eh.getEmailFrom()!=null && !eh.getEmailFrom().isEmpty())
					this.setEmailFrom(eh.getEmailFrom().hashCode());
				if(eh.getDestination()!=null && !eh.getDestination().isEmpty())
					this.setIp_dest(nctx.am.get(eh.getDestination()));
				if(eh.getOptions()!=null && !eh.getOptions().isEmpty())
					this.setOptions(eh.getOptions().hashCode());
				//TODO: Check
				if(eh.getProtocol()!=null)
					this.setProto((eh.getProtocol().ordinal()));
				if(eh.getSequence()!=null)
					this.setSeq(eh.getSequence().intValue());
				if(eh.getUrl()!=null && !eh.getUrl().isEmpty())
					this.setUrl(eh.getUrl().hashCode());
			} catch (NumberFormatException e) {
				e.printStackTrace();
				throw new BadGraphError(e.getMessage(),EType.INVALID_NODE_CONFIGURATION);
			}
		}
	}

}
