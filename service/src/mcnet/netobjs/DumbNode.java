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
package mcnet.netobjs;

import com.microsoft.z3.Context;
import com.microsoft.z3.DatatypeExpr;
import com.microsoft.z3.Solver;

import mcnet.components.NetworkObject;

/**
 * This is just a wrapper around z3 instances. The idea is that by using this we perhaps need to have 
 * fewer (or no) ifs to deal with the case where we don't instantiate an object for a node
 * @author Giacomo Costantini
 *
 */
public class DumbNode extends NetworkObject {
	public DumbNode(Context ctx, Object[]... args){
		super(ctx,args);
	}
	
	@Override
	protected void addConstraints(Solver solver) {
		return;	
	}

	@Override
	protected void init(Context ctx, Object[]... args) {
		isEndHost=true;
		this.z3Node = (DatatypeExpr)args[0][0];
	}
	@Override
	public DatatypeExpr getZ3Node() {
		return z3Node;
	}
}
