#!/usr/bin/python

#
#  Copyright 2016 Politecnico di Torino
#  Authors:
#  Project Supervisor and Contact: Riccardo Sisto (riccardo.sisto@polito.it)
#  
#  This file is part of Verigraph.
#  
#  Verigraph is free software: you can redistribute it and/or modify
#  it under the terms of the GNU Affero General Public License as
#  published by the Free Software Foundation, either version 3 of
#  the License, or (at your option) any later version.
#  
#  Verigraph is distributed in the hope that it will be useful,
#  but WITHOUT ANY WARRANTY; without even the implied warranty of
#  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
#  GNU Affero General Public License for more details.
#  
#  You should have received a copy of the GNU Affero General Public
#  License along with Verigraph.  If not, see
#  <http://www.gnu.org/licenses/>.
#

from pprint import pprint
from code_generator import CodeGeneratorBackend
import sys, getopt
import contextlib
import os
from utility import *
import logging

def main(argv):
    if len(argv) < 8:
        print 'test_generator.py -i <inputfile> -o <outputfile> -s <source> -d <destination>'
        sys.exit(2)
    #initialize command line arguments values
    inputfile = ''
    outputfile = ''
    source = ''
    destination = ''
    #parse command line arguments and exit if there is an error
    try:
        opts, args = getopt.getopt(argv,"hi:o:s:d:",["ifile=","ofile=","source=","destination="])
    except getopt.GetoptError:
        print 'test_generator.py -i <inputfile> -o <outputfile> -s <source> -d <destination>'
        sys.exit(2)
    for opt, arg in opts:
        if opt == '-h':
            print 'test_generator.py -i <inputfile> -o <outputfile> -s <source> -d <destination>'
            sys.exit()
        elif opt in ("-i", "--ifile"):
            inputfile = arg
        elif opt in ("-o", "--ofile"):
            outputfile = arg
        elif opt in ("-s", "--source"):
            source = arg
        elif opt in ("-d", "--destination"):
            destination = arg
    #set logging
    logging.basicConfig(stream=sys.stderr, level=logging.INFO)
    #capitalize ouput filename
    dirname = os.path.dirname(outputfile)
    basename = os.path.basename(outputfile)
    basename = os.path.splitext(basename)[0]
    basename = basename[0].upper() + basename[1:]

    #print arguments    
    logging.debug('Input file is', inputfile)
    logging.debug('Output file is', dirname + "/" + basename)
    logging.debug('Source node is', source)
    logging.debug('Destination node is', destination)
    

    #begin file generation
    with smart_open(dirname + "/" + basename + ".java") as f:
        c = CodeGeneratorBackend()
        c.begin(tab="    ")
        
        c.writeln("package tests;")
        c.writeln("import java.util.Calendar;")
        c.writeln("import java.util.Date;")
        c.writeln("import java.util.HashMap;")

        c.writeln("import com.microsoft.z3.Context;")
        c.writeln("import com.microsoft.z3.FuncDecl;")
        c.writeln("import com.microsoft.z3.Model;")
        c.writeln("import com.microsoft.z3.Status;")
        c.writeln("import com.microsoft.z3.Z3Exception;")
        c.writeln("import mcnet.components.IsolationResult;")


        inputfile = os.path.basename(inputfile)
        c.writeln("import tests.scenarios." + os.path.splitext(inputfile)[0] + ";")
        
        c.writeln("public class " + basename + "{")

        c.indent()
        c.writeln("Context ctx;")

        c.write("public void resetZ3() throws Z3Exception{\n\
        HashMap<String, String> cfg = new HashMap<String, String>();\n\
        cfg.put(\"model\", \"true\");\n\
        ctx = new Context(cfg);\n\
        \r\t}\n")

        
        c.write("public void printVector (Object[] array){\n\
        int i=0;\n\
        System.out.println( \"*** Printing vector ***\");\n\
        for (Object a : array){\n\
            i+=1;\n\
            System.out.println( \"#\"+i);\n\
            System.out.println(a);\n\
            System.out.println(  \"*** \"+ i+ \" elements printed! ***\");\n\
        }\n\
        \r\t}\n")
        
        c.write("public void printModel (Model model) throws Z3Exception{\n\
        for (FuncDecl d : model.getFuncDecls()){\n\
            System.out.println(d.getName() +\" = \"+ d.toString());\n\
              System.out.println(\"\");\n\
        }\n\
        \r\t}\n")

        c.writeln("public int run() throws Z3Exception{")
        c.indent()

        c.writeln(basename + " p = new " + basename + "();")

        #adding time estimation
        #c.writeln("int k = 0;")
        #c.writeln("long t = 0;")
        
        #c.writeln("for(;k<1;k++){")
        #c.indent()
        
        c.writeln("p.resetZ3();")

        c.write(os.path.splitext(inputfile)[0] + " model = new " + os.path.splitext(inputfile)[0] + "(p.ctx);\n")
        
        #c.writeln("Calendar cal = Calendar.getInstance();")
        #c.writeln("Date start_time = cal.getTime();")

        c.write("IsolationResult ret =model.check.checkIsolationProperty(model.")
        c.append(source + ", model." + destination + ");\n")
        
        #c.writeln("Calendar cal2 = Calendar.getInstance();")
        #c.writeln("t = t+(cal2.getTime().getTime() - start_time.getTime());")

        c.writeln("if (ret.result == Status.UNSATISFIABLE){\n\
            System.out.println(\"UNSAT\");\n\
            return -1;\n\
        }else if (ret.result == Status.SATISFIABLE){\n\
            System.out.println(\"SAT\");\n\
            return 0;\n\
        }else{\n\
            System.out.println(\"UNKNOWN\");\n\
            return -2;\n\
        \r\t\t}")

        #c.dedent()
        #c.writeln("}")

        #c.writeln("")
        #c.writeln("System.out.printf(\"Mean execution time " + source + " -> " + destination + ": %.16f\", ((float) t/(float)1000)/k);")

        c.dedent()
        c.writeln("}")

        c.dedent()
        c.writeln("}")

        print >>f, c.end()
    logging.debug("File " + os.path.abspath(dirname + "/" + basename + ".java") + " has been successfully generated!!")
               

if __name__ == "__main__":
    main(sys.argv[1:])
