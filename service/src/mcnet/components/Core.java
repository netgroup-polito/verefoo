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
import com.microsoft.z3.Solver;

/**Core component for everything that matters
 * 
 * @author Giacomo Costantini
 *
 */
public abstract class Core{

		final int MAX_PORT = 512;
	    
	   /**
	    * Base class for all objects in the modeling framework
	    * @param ctx
	    * @param args
	    */
	    public Core(Context ctx, Object[]... args){ // Object[]... -> The nearest way to implement variable length argument lists 
	    											//in Java, in the most generic way.  
		    init(ctx,args);
	    }
	    /**
	     * Override _init for any constructor initialization. Avoids having to explicitly call super.__init__ every Time.class
	     * @param ctx
	     * @param args
	     */
	    abstract protected void init(Context ctx,Object[]... args);
	    
	    /**
	     * Add constraints to solver
	     * @param solver
	     */
	    abstract protected void addConstraints(Solver solver);
}


