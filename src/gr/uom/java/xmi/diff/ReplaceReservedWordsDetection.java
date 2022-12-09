package gr.uom.java.xmi.diff;

import gr.uom.java.xmi.decomposition.AbstractCall;
import gr.uom.java.xmi.decomposition.UMLOperationBodyMapper;
import gr.uom.java.xmi.decomposition.replacement.MethodInvocationReplacement;

import java.util.ArrayList;

public class ReplaceReservedWordsDetection {


    private final UMLOperationBodyMapper mapper;

    public ReplaceReservedWordsDetection(UMLOperationBodyMapper mapper) {
        this.mapper = mapper;
    }


    public ArrayList<ReplaceReservedWordsRefactoring> check() {
        ArrayList<ReplaceReservedWordsRefactoring> lista = new ArrayList<>();
        for (MethodInvocationReplacement replacement:
                mapper.getMethodInvocationRenameReplacements()) {
            if(replacement.getInvokedOperationBefore().toString().contains("assert") && replacement.getInvokedOperationAfter().toString().contains("assert")) {
           //     System.out.println(replacement.getInvokedOperationBefore().getName()+"     "+replacement.getInvokedOperationAfter());
                if ((replacement.getInvokedOperationBefore().getName().equals("assertEquals") || replacement.getInvokedOperationBefore().getName().equals("assertNotEquals")) &&
                        (replacement.getInvokedOperationAfter().getName().equals("assertTrue") || replacement.getInvokedOperationAfter().getName().equals("assertFalse") || replacement.getInvokedOperationAfter().getName().equals("assertNull"))){
                    ArrayList removeArgsEqual = new ArrayList(replacement.getInvokedOperationBefore().getArguments());
                    removeArgsEqual.removeAll(replacement.getInvokedOperationAfter().getArguments());
                    if(removeArgsEqual.size()==1 && (removeArgsEqual.get(0).equals("true") || removeArgsEqual.get(0).equals("false") || removeArgsEqual.get(0).equals("null"))){
                        lista.add(new ReplaceReservedWordsRefactoring(replacement.getInvokedOperationBefore(),
                                replacement.getInvokedOperationAfter()));
                    }
                }
            }
        }
        return lista;
    }
}
