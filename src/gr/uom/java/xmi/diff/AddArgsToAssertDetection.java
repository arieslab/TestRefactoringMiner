package gr.uom.java.xmi.diff;

import gr.uom.java.xmi.decomposition.AbstractCall;
import gr.uom.java.xmi.decomposition.UMLOperationBodyMapper;
import gr.uom.java.xmi.decomposition.replacement.MethodInvocationReplacement;
import gr.uom.java.xmi.decomposition.replacement.Replacement;

import java.util.*;

public class AddArgsToAssertDetection {
    private final UMLOperationBodyMapper mapper;
    private AbstractCall operation2;
    private AbstractCall operation1;


    public AddArgsToAssertDetection(UMLOperationBodyMapper mapper) {
        this.mapper = mapper;
    }



    public ArrayList<AddArgsToAssertRefactoring> check() {
        ArrayList<AddArgsToAssertRefactoring> lista = new ArrayList<>();
        for (MethodInvocationReplacement replacement:
        mapper.getMethodInvocationRenameReplacements()) {
            if (replacement.getBefore().contains("assert") && replacement.getAfter().contains("assert") &&
                    replacement.getBefore() != null && replacement.getAfter() != null ) {
                if (replacement.getInvokedOperationBefore().getArguments().size() <
                        replacement.getInvokedOperationAfter().getArguments().size()) {
                    List rem = new ArrayList(replacement.getInvokedOperationAfter().getArguments());
                    rem.removeAll(replacement.getInvokedOperationBefore().getArguments());
                    if (rem.size() == 1 && rem.get(0).toString().contains("\"")) {
                        lista.add (new AddArgsToAssertRefactoring(replacement.getInvokedOperationBefore(), replacement.getInvokedOperationAfter()));
                    }
                }
            }
        }
        return lista;
    }
}