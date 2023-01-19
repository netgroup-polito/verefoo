package it.polito.verefoo.utils;

import com.microsoft.z3.BoolExpr;
import com.microsoft.z3.Context;
import com.microsoft.z3.Model;
import com.microsoft.z3.Status;

import it.polito.verefoo.solver.NetContextAP;
import it.polito.verefoo.solver.NetContextMF;


/**
 * Data structure for the response to check requests for verification of security properties
 *
 */
public class VerificationResult {
    Context ctx;
    public NetContextAP nctxAP;
    public NetContextMF nctxMF;
    public Status result;
    public Model model;
    public BoolExpr [] assertions;
	public long time;

    public long getTime() {
		return time;
	}

	public void setTime(long time) {
		this.time = time;
	}

	/**
     * Public constructor of VerificationResult, whose instances wrap all the information about the z3 simulation result, this constructor is specific to Atomic Predicates
     * @param ctx it is the z3 Context instance
     * @param result it is the result status
     * @param nctx it is the NetContext Atomic Predicate instance defined in the z3 model
     * @param assertions it is the set of assertion of the result model
     * @param model it is the z3 model of the MaxSMT problem
     */
    public VerificationResult(Context ctx,Status result, NetContextAP nctx, BoolExpr[] assertions, Model model){
        this.ctx = ctx;
        this.result = result;
        this.model = model;
        this.nctxAP = nctx;
        this.assertions = assertions;
    }
    
	/**
     * Public constructor of VerificationResult, whose instances wrap all the information about the z3 simulation result
     * @param ctx it is the z3 Context instance
     * @param result it is the result status
     * @param nctx it is the NetContext Maximal Flows instance defined in the z3 model
     * @param assertions it is the set of assertion of the result model
     * @param model it is the z3 model of the MaxSMT problem
     */
    public VerificationResult(Context ctx,Status result, NetContextMF nctx, BoolExpr[] assertions, Model model){
        this.ctx = ctx;
        this.result = result;
        this.model = model;
        this.nctxMF = nctx;
        this.assertions = assertions;
    }
    
}