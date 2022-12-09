package gr.uom.java.xmi.diff;

import gr.uom.java.xmi.decomposition.AbstractCall;
import gr.uom.java.xmi.decomposition.UMLOperationBodyMapper;
import gr.uom.java.xmi.decomposition.replacement.MethodInvocationReplacement;

import java.util.*;
public class ReplaceNotOperatorDetection {
    private final UMLOperationBodyMapper mapper;

        public ReplaceNotOperatorDetection(UMLOperationBodyMapper mapper) {
            this.mapper = mapper;
        }


        public ArrayList<ReplaceNotOperatorRefactoring> check() {
            ArrayList<ReplaceNotOperatorRefactoring> lista = new ArrayList<>();
            for (MethodInvocationReplacement replacement:
                    mapper.getMethodInvocationRenameReplacements()) {
                if(replacement.getInvokedOperationBefore().toString().contains("assert") &&
                        replacement.getInvokedOperationAfter().toString().contains("assert")) {
                    if (checkOppositeAsserts(replacement.getInvokedOperationBefore(), replacement.getInvokedOperationAfter())) {
                        for (int i = 0; i < replacement.getInvokedOperationBefore().getArguments().size(); i++) {
                            if ((replacement.getInvokedOperationBefore().getArguments().get(i).contains("!") &&
                                    !replacement.getInvokedOperationAfter().getArguments().get(i).contains("!")) ||
                                    (!replacement.getInvokedOperationBefore().getArguments().get(i).contains("!") &&
                                            replacement.getInvokedOperationAfter().getArguments().get(i).contains("!"))) {
                                lista.add(new ReplaceNotOperatorRefactoring(replacement.getInvokedOperationBefore(), replacement.getInvokedOperationAfter()));
                            }
                        }
                    }
                }
            }
            return lista;
        }

    private boolean checkOppositeAsserts(AbstractCall operationBefore, AbstractCall operationAfter) {
        return  (operationBefore.getName().equals("assertEquals") && operationAfter.getName().equals("assertNotEquals")) ||
                (operationBefore.getName().equals("assertNotEquals") && operationAfter.getName().equals("assertEquals")) ||
                (operationBefore.getName().equals("assertTrue") && operationAfter.getName().equals("assertFalse")) ||
                (operationBefore.getName().equals("assertFalse") && operationAfter.getName().equals("assertTrue")) ||
                (operationBefore.getName().equals("assertNull") && operationAfter.getName().equals("assertNotNull")) ||
                (operationBefore.getName().equals("assertNotNull") && operationAfter.getName().equals("assertNull")) ||
                (operationBefore.getName().equals("assertSame") && operationAfter.getName().equals("assertNotSame")) ||
                (operationBefore.getName().equals("assertNotSame") && operationAfter.getName().equals("assertSame"));
        }


}
