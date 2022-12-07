package gr.uom.java.xmi.diff;

import gr.uom.java.xmi.UMLAttribute;
import gr.uom.java.xmi.UMLOperation;
import gr.uom.java.xmi.decomposition.*;
import org.refactoringminer.api.Refactoring;
import org.refactoringminer.api.RefactoringType;

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

    public ExceptionRuleToAssertThrowsRefactoring check() {
        try {
            expectedExceptionFieldDeclaration = removedAttributes.stream()
                    .filter(field -> field.getType().getClassType().equals("ExpectedException"))
                    .findAny()
                    .orElseThrow();
           expectInvocations = detectAddedExpectInvocations(removedStmts, expectedExceptionFieldDeclaration)
                    .collect(Collectors.toList());

            operationInvocation = getAssertThrows(operationAfter).stream()
                    .filter(i -> containsAtLeastOneLineInCommon(operationBefore, i.getArguments().get(1)))
                    .findAny()
                    .orElseThrow();
            lambda = operationAfter.getAllLambdas().stream()
                    .filter(lambda -> isEnclosedBy(lambda, operationInvocation))
                    .findAny()
                    .orElseThrow();
            var expectInvocation = expectInvocations.stream().filter(op ->
                    operationInvocation.actualString().contains(op.getArguments().get(0))).findAny().orElseThrow();

            return new ExceptionRuleToAssertThrowsRefactoring(operationBefore, operationAfter,
                    lambda, operationInvocation, expectedExceptionFieldDeclaration, expectInvocation);
        } catch (NoSuchElementException ex) {
            return null;
        }
    }



    private boolean isEnclosedBy(LambdaExpressionObject lambda, AbstractCall invocation) {
        var invocationRange = invocation.codeRange();
        var lambdaRange = lambda.codeRange();
        return invocationRange.getStartLine() <= lambdaRange.getStartLine() &&
                invocationRange.getEndLine() >= lambdaRange.getEndLine() &&
                invocationRange.getStartColumn() <= lambdaRange.getStartColumn() &&
                invocationRange.getEndColumn() >= lambdaRange.getEndColumn();
    }

    private boolean containsAtLeastOneLineInCommon(UMLOperation operation, String lambda) {
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

    private Optional<ModifyMethodAnnotationRefactoring> getRemovedExpectedAttributeFromTestAnnotation() {
        return refactorings.stream()
                .filter(r -> r.getRefactoringType().equals(RefactoringType.MODIFY_METHOD_ANNOTATION))
                .map(r -> (ModifyMethodAnnotationRefactoring) r)
                .filter(r -> r.getOperationBefore().equals(operationBefore))
                .filter(r -> r.getOperationAfter().equals(operationAfter))
                .filter(r -> hasExpectedException(r.getAnnotationBefore()))
                .filter(r -> !hasExpectedException(r.getAnnotationAfter()))
                .findAny();
    }

    private boolean hasExpectedException(gr.uom.java.xmi.UMLAnnotation before) {
        return before.isNormalAnnotation() && before.getTypeName().equals("Test") && before.getMemberValuePairs().containsKey("expected");
    }

    private List<AbstractCall> getAssertThrows(UMLOperation operation) {
        return operation.getAllOperationInvocations().stream()
                .filter((op) -> op.getName().equals("assertThrows") &&
                        (Objects.isNull(op.getExpression()) || op.getExpression().equals("Assert") || op.getExpression().equals("Assertions")))
                .collect(Collectors.toList());
    }


    /**
     * Rule and expected exception
     * */


    private static Stream<AbstractCall> detectAddedExpectInvocations(List<AbstractCodeFragment> addedStmts, UMLAttribute expectedExceptionRuleFieldDeclaration) {
        return extractMethodInvocationsStream(addedStmts)
                .filter(expectInvocation -> expectedExceptionRuleFieldDeclaration.getName().equals(expectInvocation.getExpression()))
                .filter(expectInvocation -> expectInvocation.getArguments().size() == 1);
    }

    private static Stream<AbstractCall> extractMethodInvocationsStream(List<AbstractCodeFragment> addedStmts) {
        return addedStmts.stream().flatMap(st -> st.getMethodInvocationMap().values().stream().flatMap(Collection::stream));
    }


    private static boolean isAnyArgumentPassedTo(List<String> arguments, AbstractCall invocation) {
        return arguments.contains(invocation.getArguments().get(0));
    }
}