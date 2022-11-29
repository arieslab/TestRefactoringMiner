package gr.uom.java.xmi.diff;

import gr.uom.java.xmi.decomposition.AbstractCall;

import java.util.ArrayList;
import java.util.Arrays;

public class SplitConditionalAssertDetection {
    private AbstractCall operationBefore = null;
    private AbstractCall operationAfter = null;
    private AbstractCall operation2;
    private AbstractCall operation1;


    public SplitConditionalAssertDetection(AbstractCall operationBefore, AbstractCall operationAfter) {
        this.operationBefore = operationBefore;
        this.operationAfter = operationAfter;
    }


    public SplitConditionalAssertRefactoring check() {
        if(operationBefore.toString().contains("assert") && operationAfter.toString().contains("assert")) {
            if ((operationBefore.getName().equals("assertTrue") || operationBefore.getName().equals("assertFalse") || operationBefore.getName().equals("assertNull") || operationBefore.getName().equals("assertNotNull")) &&
                    (operationAfter.getName().equals("assertEquals") || operationAfter.getName().equals("assertNotEquals") || operationAfter.getName().equals("assertSame") || operationAfter.getName().equals("assertNotSame") || operationAfter.getName().equals("assertThat") || operationAfter.getName().equals("assertNotThat"))){
                String[] match = {".equals", ".contains", "==", "<=", ">=", "!=", "<", ">"};
                for (int i = 0; i < operationBefore.getArguments().size(); i++) {
                    if (!operationBefore.getArguments().get(i).contains("\"")){
                        for(int j = 0; j < match.length; j++){
                            if(operationBefore.getArguments().get(i).contains(match[j])) {
                                String inv = operationBefore.getArguments().get(i);
                                String[] splitParam = inv.split(match[j]);
                                if(splitParam[1].contains(")")) {
                                    splitParam[1] = splitParam[1].replace(")", "");
                                }
                                if(splitParam[1].contains("(")) {
                                    splitParam[1] = splitParam[1].replace("(", "");
                                }
                                if(splitParam[1].contains("\s")) {
                                    splitParam[1] = splitParam[1].replace("\s", "");
                                }
                                if(splitParam[0].contains("\s")) {
                                    splitParam[0] = splitParam[0].replace("\s", "");
                                }
                                if(operationAfter.getArguments().containsAll(Arrays.asList(splitParam))){
                                    operation1 = operationBefore;
                                    operation2 = operationAfter;
                                    return new SplitConditionalAssertRefactoring(operation1, operation2);
                                }
                            }
                        }
                    }
                }
            }
        }
        return null;
    }
}
