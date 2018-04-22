package it.polito.verifoo.rest.main;
import java.util.HashMap;

import com.microsoft.z3.Context;
import com.microsoft.z3.FuncDecl;
import com.microsoft.z3.IntExpr;
import com.microsoft.z3.Optimize;
import com.microsoft.z3.Params;
import com.microsoft.z3.Sort;
import com.microsoft.z3.Status;
import com.microsoft.z3.Z3Exception;

public class Wildcards {
	static Context ctx;
	public void resetZ3() throws Z3Exception {
		HashMap<String, String> cfg = new HashMap<String, String>();
		cfg.put("model", "true");
		ctx = new Context(cfg);
	}

	public static void main(String[] args) {
		ctx = new Context();
		newone(ctx);
	}
	private static void newone(Context ctx2) {
		Optimize mkOptimize = ctx.mkOptimize();
		
		IntExpr x1 = ctx.mkIntConst("ip1_1");
		IntExpr x2 = ctx.mkIntConst("ip1_2");
		IntExpr x3 = ctx.mkIntConst("ip1_3");
		IntExpr x4 = ctx.mkIntConst("ip1_4");
		
		IntExpr y1 = ctx.mkIntConst("ip2_1");
		IntExpr y2 = ctx.mkIntConst("ip2_2");
		IntExpr y3 = ctx.mkIntConst("ip2_3");
		IntExpr y4 = ctx.mkIntConst("ip2_4");
		
		IntExpr auto1 = ctx.mkIntConst("ipAuto_1");
		IntExpr auto2 = ctx.mkIntConst("ipAuto_2");
		IntExpr auto3 = ctx.mkIntConst("ipAuto_3");
		IntExpr auto4 = ctx.mkIntConst("ipAuto_4");
		
		mkOptimize.Add(ctx.mkEq(x1, ctx.mkInt(192)));
		mkOptimize.Add(ctx.mkEq(x2, ctx.mkInt(167)));
		mkOptimize.Add(ctx.mkEq(x3, ctx.mkInt(1)));
		mkOptimize.Add(ctx.mkEq(x4, ctx.mkInt(1)));
		
		mkOptimize.Add(ctx.mkEq(y1, ctx.mkInt(192)));
		mkOptimize.Add(ctx.mkEq(y2, ctx.mkInt(168)));
		mkOptimize.Add(ctx.mkEq(y3, ctx.mkInt(1)));
		mkOptimize.Add(ctx.mkEq(y4, ctx.mkInt(1)));
		
		
		mkOptimize.AssertSoft(ctx.mkAnd(ctx.mkEq(auto1, x1),ctx.mkEq(auto1, y1)), 1000, "auto");
		mkOptimize.AssertSoft(ctx.mkAnd(ctx.mkEq(auto2, x2),ctx.mkEq(auto2, y2)), 1000, "auto");
		mkOptimize.AssertSoft(ctx.mkAnd(ctx.mkEq(auto3, x3),ctx.mkEq(auto3, y3)), 1000, "auto");
		mkOptimize.AssertSoft(ctx.mkAnd(ctx.mkEq(auto4, x4),ctx.mkEq(auto4, y4)), 1000, "auto");
		
		mkOptimize.AssertSoft(ctx.mkEq(auto1, ctx.mkInt(-1)), 100, "auto");
		mkOptimize.AssertSoft(ctx.mkEq(auto2, ctx.mkInt(-1)), 100, "auto");
		mkOptimize.AssertSoft(ctx.mkEq(auto3, ctx.mkInt(-1)), 100, "auto");
		mkOptimize.AssertSoft(ctx.mkEq(auto4, ctx.mkInt(-1)), 100, "auto");
		
		
		FuncDecl drop = ctx.mkFuncDecl("drop_func", new Sort[]{ctx.mkIntSort(), ctx.mkIntSort(), ctx.mkIntSort(), ctx.mkIntSort()
																, ctx.mkIntSort(), ctx.mkIntSort(), ctx.mkIntSort(), ctx.mkIntSort()},ctx.mkBoolSort());
		
		mkOptimize.Add(ctx.mkAnd(
				ctx.mkEq(drop.apply(x1, x2, x3, x4, auto1, auto2, auto3, auto4), ctx.mkTrue()),
				ctx.mkEq(drop.apply(auto1, auto2, auto3, auto4, y1, y2, y3, y4), ctx.mkTrue())
				));
		
		Status result=mkOptimize.Check();
		
		if(result==Status.UNSATISFIABLE){
			System.out.println("UNSAT");
		}else{
			System.out.println("SAT");
			System.out.println(mkOptimize.getModel());
		}
		
		
	}

}
