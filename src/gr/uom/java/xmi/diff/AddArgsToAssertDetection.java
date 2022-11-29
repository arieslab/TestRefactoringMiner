package gr.uom.java.xmi.diff;

import gr.uom.java.xmi.UMLOperation;
import gr.uom.java.xmi.decomposition.AbstractCall;
import gr.uom.java.xmi.decomposition.LambdaExpressionObject;
import gr.uom.java.xmi.decomposition.OperationInvocation;
import org.refactoringminer.api.Refactoring;
import org.refactoringminer.api.RefactoringType;

import java.util.*;
import java.util.stream.Collectors;

public class AddArgsToAssertDetection {
    private AbstractCall operationBefore = null;
    private AbstractCall operationAfter = null;
    private AbstractCall operation2;
    private AbstractCall operation1;

    public AddArgsToAssertDetection(UMLOperation operationBefore, UMLOperation operationAfter) {
        // this.operationBefore = operationBefore;
        //this.operationAfter = operationAfter;
    }

    public AddArgsToAssertDetection(AbstractCall operationBefore, AbstractCall operationAfter) {
        this.operationBefore = operationBefore;
        this.operationAfter = operationAfter;
    }


    public AddArgsToAssertRefactoring check() {

        if (operationBefore.toString().contains("assert") && operationAfter.toString().contains("assert")) {
            if (operationBefore != null && operationAfter != null) {
                if (operationBefore.getArguments().size() < operationAfter.getArguments().size()) {
                    List rem = new ArrayList(operationAfter.getArguments());
                    rem.removeAll(operationBefore.getArguments());
                    if (rem.size() == 1 && rem.get(0).toString().contains("\"")) {
                        operation1 = operationBefore;
                        operation2 = operationAfter;
                        return new AddArgsToAssertRefactoring(operation1, operation2);
                    }
                }
            }
        }
        return null;

    }
}