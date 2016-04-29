package tests.examples.taormina;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import com.microsoft.z3.Context;
import com.microsoft.z3.FuncDecl;
import com.microsoft.z3.Model;
import com.microsoft.z3.Status;
import com.microsoft.z3.Z3Exception;
import mcnet.components.IsolationResult;
import tests.examples.taormina.Scenario_1;
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
        int k = 0;
        long t = 0;
        for(;k<10;k++){
            p.resetZ3();
            Scenario_1 model = new Scenario_1(p.ctx);
            Calendar cal = Calendar.getInstance();
            Date start_time = cal.getTime();
            IsolationResult ret =model.check.checkIsolationProperty(model.mailclient, model.mailserver);
            Calendar cal2 = Calendar.getInstance();
            t = t+(cal2.getTime().getTime() - start_time.getTime());
            if (ret.result == Status.UNSATISFIABLE){
                System.out.println("UNSAT");
            }else{
                System.out.println("SAT");
            			}
        }
        System.out.printf("Mean execution time mailclient -> mailserver: %.16f", ((float) t/(float)1000)/k);
    }
}

