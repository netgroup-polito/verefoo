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
import com.microsoft.z3.Expr;
import com.microsoft.z3.Model;
import com.microsoft.z3.Status;


/**Data structure for the response to a check request for data isolation property
 * 
 * @author Giacomo Costantini
 *
 */
public class DataIsolationResult {

	Context ctx;
	public NetContext nctx;
	public Status result;
	public Model model;
	public Expr violating_packet,last_hop,last_time,t_1;
	public BoolExpr [] assertions;

	public DataIsolationResult(Context ctx,Status result, Expr violating_packet, Expr last_hop, Expr last_time, NetContext nctx, BoolExpr[] assertions, Model model){
            this.ctx = ctx;
            this.result = result;
            this.violating_packet = violating_packet;
            this.last_hop = last_hop;
            this.model = model;
            this.last_time = last_time;
            this.assertions = assertions;
	}
}

