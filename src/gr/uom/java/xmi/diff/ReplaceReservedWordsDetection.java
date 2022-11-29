package gr.uom.java.xmi.diff;

import gr.uom.java.xmi.decomposition.AbstractCall;

import java.util.ArrayList;

public class ReplaceReservedWordsDetection {


    private AbstractCall operationBefore = null;
    private AbstractCall operationAfter = null;
    private AbstractCall operation2;
    private AbstractCall operation1;


    public ReplaceReservedWordsDetection(AbstractCall operationBefore, AbstractCall operationAfter) {
        this.operationBefore = operationBefore;
        this.operationAfter = operationAfter;
    }


    public ReplaceReservedWordsRefactoring check() {
        if(operationBefore.toString().contains("assert") && operationAfter.toString().contains("assert")) {
            if ((operationBefore.getName().equals("assertEquals") || operationBefore.getName().equals("assertNotEquals")) &&
                    (operationAfter.getName().equals("assertTrue") || operationAfter.getName().equals("assertFalse") || operationAfter.getName().equals("assertNull"))){
                ArrayList removeArgsEqual = new ArrayList(operationBefore.getArguments());
                removeArgsEqual.removeAll(operationAfter.getArguments());
                if(removeArgsEqual.size()==1 && (removeArgsEqual.get(0).equals("true") || removeArgsEqual.get(0).equals("false") || removeArgsEqual.get(0).equals("null"))){
                        operation1 = operationBefore;
                        operation2 = operationAfter;
                        return new ReplaceReservedWordsRefactoring(operation1, operation2);
                }
            }
        }
        return null;
    }
}
