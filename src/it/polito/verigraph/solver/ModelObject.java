package it.polito.verigraph.solver;

import com.microsoft.z3.Context;
import com.microsoft.z3.DatatypeExpr;
import com.microsoft.z3.Solver;

import it.polito.verigraph.mcnet.components.NetContext;
import it.polito.verigraph.mcnet.components.NetworkObject;

public class ModelObject extends NetworkObject{
	DatatypeExpr node;

	public ModelObject(Context ctx, Object[]... args) {
		super(ctx, args);
		
	}

	

	@Override
	public DatatypeExpr getZ3Node() {
		return node;
	}

	@Override
	protected void init(Context ctx, Object[]... args) {
		if(args[1][2].toString().compareTo("endhost")==0)
			this.isEndHost = true;
		else
			this.isEndHost = false;
		this.node = this.z3Node = ((NetworkObject)args[0][0]).getZ3Node();
		
	}

	@Override
	protected void addConstraints(Solver solver) {
		
		
	}

}
