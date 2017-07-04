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

import sys, string

class CodeGeneratorBackend:

    def begin(self, tab="\t"):
        self.code = []
        self.tab = tab
        self.level = 0

    def end(self):
        return string.join(self.code, "")

    def write(self, string):
        self.code.append(self.tab * self.level + string)

    def writeln(self, string):
        self.code.append(self.tab * self.level + string + "\n")
        
    def append(self, string):
        self.code.append(string)

    def indent(self):
        self.level = self.level + 1

    def dedent(self):
        if self.level == 0:
            raise SyntaxError, "internal error in code generator"
        self.level = self.level - 1

    def write_list(self, data, delimiter=True, wrapper="'"):
        if delimiter == True:
            self.code.append("{")
        first = True
        for element in data:
            if (first == False):
                self.code.append(", ")
            else:
                first = False
            if wrapper == "'":
                self.code.append("'" + str(element) + "'")
            elif wrapper == "\"":
                self.code.append("\"" + str(element) + "\"")
            elif wrapper == "b":
                self.code.append("(" + str(element) + ")")
            else:
                self.code.append(str(element))
        if delimiter == True:
            self.code.append("}")
