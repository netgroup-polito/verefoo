/*
 * Copyright 2016 Politecnico di Torino
 * Authors:
 * Project Supervisor and Contact: Riccardo Sisto (riccardo.sisto@polito.it)
 * 
 * This file is part of Verigraph.
 * 
 * Verigraph is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of
 * the License, or (at your option) any later version.
 * 
 * Verigraph is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public
 * License along with Verigraph.  If not, see
 * <http://www.gnu.org/licenses/>.
 */
package mcnet.components;

import com.microsoft.z3.Context;
import com.microsoft.z3.DatatypeExpr;

/** Represents a generic network object.
 * 
 * @author Giacomo Costantini
 *
 */
public abstract class NetworkObject extends Core{
	
	public NetworkObject(Context ctx,Object[]... args) {
		super(ctx,args);
	}

	protected DatatypeExpr z3Node;
	protected boolean isEndHost;
	/**
	 * Get a reference to the z3 node this class wraps around
	 * @return
	 */
	abstract public DatatypeExpr getZ3Node();
	
	public String toString(){
	        return z3Node.toString();
	}
	
	//There is probably an error: z3Node.hashCode = 0 because AST.hashCode() has always hash=0 
	/*public int hashCode(){
		return z3Node.hashCode();
	}*/
	        	
	/**
	 * A simple way to determine the set of endhosts
	 * @return
	 */
	public boolean isEndHost(){
	    return isEndHost;
	}
	
	/**
	 * Wrap methods to set policy
	 * @param policy
	 * @throws UnsupportedOperationException
	 */
	void setPolicy (Object policy) throws UnsupportedOperationException{
	     throw new UnsupportedOperationException();
	}
}
