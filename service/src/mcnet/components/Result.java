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

import com.microsoft.z3.BoolExpr;
import com.microsoft.z3.Context;
import com.microsoft.z3.Model;

/**Data structure for the core of the response to a check request for data isolation property
 * 
 * @author Giacomo Costantini
 *
 */
public class Result {
	Context ctx;
	public Model model;
	public BoolExpr[] unsat_core;

/**
 * 	
 * @param ctx
 * @param model
 */
    public Result(Context ctx, Model model){
            this.ctx = ctx;
            this.model = model;
	}
    
/**
 * 
 * @param ctx
 * @param unsat_core
 */
    public Result(Context ctx, BoolExpr[] unsat_core){
        this.ctx = ctx;
        this.unsat_core = unsat_core;
}
}
