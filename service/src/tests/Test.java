package tests;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import com.microsoft.z3.Context;
import com.microsoft.z3.FuncDecl;
import com.microsoft.z3.Model;
import com.microsoft.z3.Status;
import com.microsoft.z3.Z3Exception;

import mcnet.components.DataIsolationResult;
import mcnet.components.IsolationResult;
import tests.examples.Test_1;
public class Test{
    Context ctx;
    public void resetZ3() throws Z3Exception{
        HashMap<String, String> cfg = new HashMap<String, String>();
        cfg.put("model", "true");
        ctx = new Context(cfg);
        
	}
    public void printVector (Object[] array){
        int i=0;
        System.out.println( "*** Printing vector ***");
        for (Object a : array){
            i+=1;
            System.out.println( "#"+i);
            System.out.println(a);
            System.out.println(  "*** "+ i+ " elements printed! ***");
        }
        
	}
    public void printModel (Model model) throws Z3Exception{
        for (FuncDecl d : model.getFuncDecls()){
            System.out.println(d.getName() +" = "+ d.toString());
              System.out.println("");
        }
        
	}
    public static void main(String[] args) throws Z3Exception{
    	 Test p = new Test();
         p.resetZ3();
         Test_1 model = new Test_1(p.ctx);
         IsolationResult ret =model.check.checkIsolationProperty(model.webserver, model.firewall);
         if (ret.result == Status.UNSATISFIABLE){
             System.out.println("UNSAT");
//             return -1;
         }else if (ret.result == Status.SATISFIABLE){
             System.out.println("SAT");
//             return 0;
         }else{
             System.out.println("UNKNOWN");
//             return -2;
         
 		}
    }
}

