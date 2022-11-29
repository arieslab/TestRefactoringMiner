package gr.uom.java.xmi.diff;

import gr.uom.java.xmi.decomposition.AbstractCall;

import java.util.*;
public class ReplaceNotOperatorDetection {


        private AbstractCall operationBefore = null;
        private AbstractCall operationAfter = null;
        private AbstractCall operation2;
        private AbstractCall operation1;


        public ReplaceNotOperatorDetection(AbstractCall operationBefore, AbstractCall operationAfter) {
            this.operationBefore = operationBefore;
            this.operationAfter = operationAfter;
        }


        public ReplaceNotOperatorRefactoring check() {
            if(operationBefore.toString().contains("assert") && operationAfter.toString().contains("assert")) {
                if ((operationBefore.getName().equals("assertEquals") && operationAfter.getName().equals("assertNotEquals")) ||
                        (operationBefore.getName().equals("assertNotEquals") && operationAfter.getName().equals("assertEquals")) ||
                        (operationBefore.getName().equals("assertTrue") && operationAfter.getName().equals("assertFalse")) ||
                        (operationBefore.getName().equals("assertFalse") && operationAfter.getName().equals("assertTrue")) ||
                        (operationBefore.getName().equals("assertNull") && operationAfter.getName().equals("assertNotNull")) ||
                        (operationBefore.getName().equals("assertNotNull") && operationAfter.getName().equals("assertNull")) ||
                        (operationBefore.getName().equals("assertSame") && operationAfter.getName().equals("assertNotSame")) ||
                        (operationBefore.getName().equals("assertNotSame") && operationAfter.getName().equals("assertSame"))) {

                    for (int i = 0; i < operationBefore.getArguments().size(); i++) {
                        if ((operationBefore.getArguments().get(i).contains("!") &&
                                !operationAfter.getArguments().get(i).contains("!")) ||
                                (!operationBefore.getArguments().get(i).contains("!") &&
                                        operationAfter.getArguments().get(i).contains("!"))) {
                            operation1 = operationBefore;
                            operation2 = operationAfter;
                            return new ReplaceNotOperatorRefactoring(operation1, operation2);
                        }
                    }
                }
            }
            return null;
        }
    }
