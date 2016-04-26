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

/** A data structure which is an utility to make a generic couple of objects with different types in Java
 *
 *@author Giacomo Costantini
 */
public class Tuple<T, U> {
	  public final T _1;
	  public final U _2;

	 
	  public Tuple(T arg1,U arg2) {
	    super();
	    this._1 = arg1;
	    this._2 = arg2;
	  }
	  
	  public Tuple(){
		  this._1 = null;
		    this._2 = null;
	  }
	
	  @Override
	  public String toString() {
	    return String.format("(%s, %s)", _1, _2);
	  }
	}