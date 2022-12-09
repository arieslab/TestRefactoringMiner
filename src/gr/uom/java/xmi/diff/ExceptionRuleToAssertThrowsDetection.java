package gr.uom.java.xmi.diff;

import gr.uom.java.xmi.UMLAttribute;
import gr.uom.java.xmi.UMLOperation;
import gr.uom.java.xmi.decomposition.*;
import org.refactoringminer.api.Refactoring;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ExceptionRuleToAssertThrowsDetection {
    private final Collection<Refactoring> refactorings;
    private AbstractCall operationInvocation;
    private LambdaExpressionObject lambda;
    private final UMLOperation operationBefore;
    private final UMLOperation operationAfter;

    private final List<UMLAttribute> removedAttributes;
    private final List<AbstractCodeFragment> removedStmts;
    private   UMLAttribute expectedExceptionFieldDeclaration;
    private  List<AbstractCall> expectInvocations;
    



    public ExceptionRuleToAssertThrowsDetection(UMLOperationBodyMapper mapper,
                                                Collection<Refactoring> refactorings,
                                                List<UMLAttribute> removedAttributes) {
        this.operationBefore = mapper.getOperation1();
        this.operationAfter = mapper.getOperation2();
        this.removedStmts = mapper.getNonMappedLeavesT1();
        this.removedAttributes = removedAttributes;
        this.refactorings = refactorings;
    }

    public ArrayList<ExceptionRuleToAssertThrowsRefactoring> check() {
        ArrayList<ExceptionRuleToAssertThrowsRefactoring> lista = new ArrayList<>();
        try {
            expectedExceptionFieldDeclaration = removedAttributes.stream()
                    .filter(field -> field.getType().getClassType().equals("ExpectedException"))
                    .findAny()
                    .orElseThrow();
           expectInvocations = detectAddedExpectInvocations(removedStmts, expectedExceptionFieldDeclaration)
                    .collect(Collectors.toList());

            operationInvocation = getAssertThrows(operationAfter).stream()
                    .filter(i -> containsAtLeastOneLineInCommon(operationBefore, i.getArguments()))
                    .findAny()
                    .orElseThrow();

            lambda = operationAfter.getAllLambdas().stream()
                    .filter(lambda -> isEnclosedBy(lambda, operationInvocation))
                    .findAny()
                    .orElseThrow();
            var expectInvocation = expectInvocations.stream().filter(op ->
                    operationInvocation.actualString().contains(op.getArguments().get(0))).findAny().orElseThrow();

            lista.add( new ExceptionRuleToAssertThrowsRefactoring(operationBefore, operationAfter,
                    lambda, operationInvocation, expectedExceptionFieldDeclaration, expectInvocation));
        } catch (NoSuchElementException ex) {
            return null;
        }
        return lista;
    }



    private boolean isEnclosedBy(LambdaExpressionObject lambda, AbstractCall invocation) {
        var invocationRange = invocation.codeRange();
        var lambdaRange = lambda.codeRange();
        return invocationRange.getStartLine() <= lambdaRange.getStartLine() &&
                invocationRange.getEndLine() >= lambdaRange.getEndLine() &&
                invocationRange.getStartColumn() <= lambdaRange.getStartColumn() &&
                invocationRange.getEndColumn() >= lambdaRange.getEndColumn();
    }

    private boolean containsAtLeastOneLineInCommon(UMLOperation operation, List<String> lambdaActual) {
        String lambda = lambdaActual.get(1);
        if (lambdaActual.get(0).contains("\"")){
            lambda = lambdaActual.get(2);
        }
        return lambda
                .lines()
                .map(String::strip)
                .map(line -> lambdaBodyIsExpression(line) ? extractExpressionAndConvertToStatement(line) : line)
                .filter(line -> line.length() > 1) // Ignore "{" and "}" lines
                .anyMatch(lambdaLine -> operationContainsLine(operation, lambdaLine));
    }

    private String extractExpressionAndConvertToStatement(String line) {
        return line.replaceFirst("\\(\\) -> ", "") + ";";
    }

    private boolean lambdaBodyIsExpression(String line) {
        return line.endsWith(")");
    }

    private boolean operationContainsLine(UMLOperation operation, String line) {
        return operation.getBody().stringRepresentation().stream()
                .map(String::strip)
                .filter(s -> s.length() > 1) // Ignore "{" and "}" lines
                .anyMatch(operationBodyLine -> operationBodyLine.equals(line));
    }

    private List<AbstractCall> getAssertThrows(UMLOperation operation) {
        var a = operation.getAllOperationInvocations().stream()
                .filter((op) -> op.getName().equals("assertThrows") &&
                        (Objects.isNull(op.getExpression()) || op.getExpression().equals("Assert") || op.getExpression().equals("Assertions")))
                .collect(Collectors.toList());
//        for (int i = 0; i < a.size(); i++) {
//            System.out.println(a.get(i).actualString());
//
//        }
        return  a;
    }

    private static Stream<AbstractCall> detectAddedExpectInvocations(List<AbstractCodeFragment> addedStmts, UMLAttribute expectedExceptionRuleFieldDeclaration) {
        return extractMethodInvocationsStream(addedStmts)
                .filter(expectInvocation -> expectedExceptionRuleFieldDeclaration.getName().equals(expectInvocation.getExpression()))
                .filter(expectInvocation -> expectInvocation.getArguments().size() == 1);
    }

    private static Stream<AbstractCall> extractMethodInvocationsStream(List<AbstractCodeFragment> addedStmts) {
        return addedStmts.stream().flatMap(st -> st.getMethodInvocationMap().values().stream().flatMap(Collection::stream));
    }

}