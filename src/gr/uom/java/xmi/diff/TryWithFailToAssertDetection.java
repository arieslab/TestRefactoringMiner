package gr.uom.java.xmi.diff;

import gr.uom.java.xmi.UMLAttribute;
import gr.uom.java.xmi.UMLOperation;
import gr.uom.java.xmi.decomposition.*;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class TryWithFailToAssertDetection {
    private final UMLOperation operationBefore;
    private AbstractCall operationInvocation;
    private LambdaExpressionObject lambda;
    private final UMLOperation operationAfter;
    private final List<CompositeStatementObject> removedCompositeStmts;
    private final List<AbstractCodeFragment> addedStmts;
    private List<TryStatementObject> tryStatements;
    private List<String> capturedExceptions;
    private List<AbstractCall> assertFailInvocationsFound;

    public TryWithFailToAssertDetection(UMLOperationBodyMapper mapper) {
        this(mapper.getOperation1(), mapper.getOperation2(), mapper.getNonMappedInnerNodesT1(),
                mapper.getNonMappedLeavesT2());
    }

    public TryWithFailToAssertDetection(UMLOperation operationBefore, UMLOperation operationAfter, List<CompositeStatementObject> removedCompositeStmts, List<AbstractCodeFragment> addedStmts) {
        this.operationBefore = operationBefore;
        this.operationAfter = operationAfter;
        this.removedCompositeStmts = removedCompositeStmts;
        this.addedStmts = addedStmts;
    }

    public TryWithFailToAssertRefactoring check() {
        try {
            if (checkFromTryWithFail()) {
                return createRefactoring();
            }
            return null;
        }
        catch (NoSuchElementException exception) {
            return null;
        }
    }

    private TryWithFailToAssertRefactoring createRefactoring() {
        var tryStmt = tryStatements.get(0);
        var assertFailInvocation = assertFailInvocationsFound.get(0);

        List<AbstractCall> assertThrowsList = getAssertThrows(operationAfter);

        operationInvocation = getAssertThrows(operationAfter).stream()
                .filter(i -> capturedExceptions.contains(i.getArguments().get(0)))
                .findAny()
                .orElseThrow();

        lambda = operationAfter.getAllLambdas().stream()
                .filter(lambda -> operationInvocation.getArguments().contains(lambda.toString()))
                .findAny()
                .orElseThrow();
        return new TryWithFailToAssertRefactoring(operationBefore, operationAfter,
                tryStmt, assertFailInvocation, operationInvocation, lambda);
    }

    private boolean checkFromTryWithFail() {
        tryStatements = filterTryStatement(removedCompositeStmts).collect(Collectors.toList());
        capturedExceptions = tryStatements.stream()
                .filter(stmt -> detectAssertFailInvocationAtTheEndOf(stmt).findAny().isPresent())
                .flatMap(TryWithFailToAssertDetection::detectCatchExceptions)
                .map(exception -> exception.concat(".class"))
                .collect(Collectors.toList());
        assertFailInvocationsFound = tryStatements.stream()
                .flatMap(TryWithFailToAssertDetection::detectAssertFailInvocationAtTheEndOf)
                .collect(Collectors.toList());
        return assertFailInvocationsFound.size() > 0;
    }

    private static Stream<AbstractCall> detectAssertFailInvocationAtTheEndOf(TryStatementObject tryStatement) {
        List<AbstractCall> operationInvocationsInLastStatement;
        try {
            var lastStatement = tryStatement.getStatements().get(tryStatement.getStatements().size() - 1);
            operationInvocationsInLastStatement = new ArrayList<>(lastStatement.getMethodInvocationMap().values()).get(0);
        } catch (IndexOutOfBoundsException e) {
            return Stream.empty();
        }
        var nonNullInvocations = operationInvocationsInLastStatement.stream().filter(Objects::nonNull);
        var nonNullFailInvocations = nonNullInvocations.filter(invocation -> Objects.nonNull(invocation.getName()) && invocation.getName().equals("fail"));
        return nonNullFailInvocations.filter(invocation -> Objects.isNull(invocation.getExpression()) || invocation.getExpression().equals("Assert"));
    }

    private static Stream<String> detectCatchExceptions(TryStatementObject tryStatement) {
        return tryStatement.getCatchClauses().stream()
                .flatMap(clause -> clause.getVariableDeclarations().stream()
                        .map(variable -> variable.getType().getClassType()))
                .filter(classType -> classType.endsWith("Exception"));
    }

    private static Stream<TryStatementObject> filterTryStatement(List<CompositeStatementObject> stmts) {
        return stmts.stream()
                .filter(st->st instanceof TryStatementObject)
                .map(st -> (TryStatementObject)st);
    }

    private List<AbstractCall> getAssertThrows(UMLOperation operation) {
        return operation.getAllOperationInvocations().stream()
                .filter((op) -> op.getName().equals("assertThrows") &&
                        (Objects.isNull(op.getExpression()) || op.getExpression().equals("Assert") || op.getExpression().equals("Assertions")))
                .collect(Collectors.toList());
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
}