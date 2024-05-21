
package it.polito.verefoo.utils;

/** A data structure which is an utility to make a generic couple of objects with different types in Java
 *
 *@author Giacomo Costantini
 */
public class Quadruple<T, U, V, X> {
	  public T _1;
	  public U _2;
	  public V _3;
	  public X _4;

	 
	  public Quadruple(T arg1,U arg2, V arg3,X arg4) {
	    super();
	    this._1 = arg1;
	    this._2 = arg2; 
	    this._3 = arg3;
	    this._4 = arg4;
	  }
	  
	  public Quadruple(){
		  this._1 = null;
		    this._2 = null;
		    this._3 = null;
		    this._4 = null;
	  }
	
	  @Override
	  public String toString() {
	    return String.format("(%s, %s, %s, %s)", _1, _2, _3,_4);
	  }
	}