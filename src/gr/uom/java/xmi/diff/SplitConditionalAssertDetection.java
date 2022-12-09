package gr.uom.java.xmi.diff;

import gr.uom.java.xmi.decomposition.AbstractCall;
import gr.uom.java.xmi.decomposition.UMLOperationBodyMapper;
import gr.uom.java.xmi.decomposition.replacement.MethodInvocationReplacement;

import java.util.ArrayList;
import java.util.Arrays;

public class SplitConditionalAssertDetection {
    private final UMLOperationBodyMapper mapper;


    public SplitConditionalAssertDetection(UMLOperationBodyMapper mapper) {
        this.mapper = mapper;
    }


    public ArrayList<SplitConditionalAssertRefactoring> check() {
        String[] match = {".equals", ".contains", "==", "<=", ">=", "!=", "<", ">"};
       ArrayList<SplitConditionalAssertRefactoring> lista = new ArrayList<>();
        for (MethodInvocationReplacement replacement:
                mapper.getMethodInvocationRenameReplacements()) {
            if(replacement.getInvokedOperationBefore().toString().contains("assert") && replacement.getInvokedOperationAfter().toString().contains("assert")) {
                if (checkAssertsWithOneArg(replacement.getInvokedOperationBefore()) &&
                        checkAssertsTwoArgs(replacement.getInvokedOperationAfter())){
                    for (int i = 0; i < replacement.getInvokedOperationBefore().getArguments().size(); i++) {
                        if (!replacement.getInvokedOperationBefore().getArguments().get(i).contains("\"")){
                            for(int j = 0; j < match.length; j++){
                                if(replacement.getInvokedOperationBefore().getArguments().get(i).contains(match[j])) {
                                    String inv = replacement.getInvokedOperationBefore().getArguments().get(i);
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
                                    if(replacement.getInvokedOperationAfter().getArguments().containsAll(Arrays.asList(splitParam))){
                                        lista.add(new SplitConditionalAssertRefactoring(replacement.getInvokedOperationBefore(),
                                                replacement.getInvokedOperationAfter()));
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        return lista;
    }

    private boolean checkAssertsTwoArgs(AbstractCall replacement) {
        return (replacement.getName().equals("assertEquals") ||
                replacement.getName().equals("assertNotEquals") ||
                replacement.getName().equals("assertSame") ||
                replacement.getName().equals("assertNotSame") ||
                replacement.getName().equals("assertThat") ||
                replacement.getName().equals("assertNotThat"));
    }

    private boolean checkAssertsWithOneArg(AbstractCall replacement) {
        return (replacement.getName().equals("assertTrue") ||
                replacement.getName().equals("assertFalse") ||
                replacement.getName().equals("assertNull") ||
                replacement.getName().equals("assertNotNull"));
    }
}
