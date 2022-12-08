package gr.uom.java.xmi.diff;

import gr.uom.java.xmi.UMLAttribute;
import gr.uom.java.xmi.UMLOperation;
import gr.uom.java.xmi.decomposition.AbstractCall;
import gr.uom.java.xmi.decomposition.LambdaExpressionObject;
import gr.uom.java.xmi.decomposition.TryStatementObject;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.refactoringminer.api.Refactoring;
import org.refactoringminer.api.RefactoringType;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class TryWithFailToAssertRefactoring implements Refactoring {

    private final UMLOperation operationBefore;
    private final UMLOperation operationAfter;
    private final TryStatementObject tryStatement;
    private final AbstractCall assertFailInvocation;
    private final AbstractCall assertThrows;
    private final LambdaExpressionObject lambda;

    public TryWithFailToAssertRefactoring(UMLOperation operationBefore, UMLOperation operationAfter,
                                          TryStatementObject tryStmt, AbstractCall assertFailInvocation,
                                          AbstractCall operationInvocation, LambdaExpressionObject lambda) {
        this.operationBefore = operationBefore;
        this.operationAfter = operationAfter;
        this.tryStatement = tryStmt;
        this.assertFailInvocation = assertFailInvocation;
        this.assertThrows = operationInvocation;
        this.lambda = lambda;
    }

    @Override
    public List<CodeRange> leftSide() {
        List<CodeRange> ranges = new ArrayList<>();
        ranges.add(operationBefore.codeRange()
                .setDescription("source method declaration before migration")
                .setCodeElement(operationBefore.toString()));
        ranges.add(tryStatement.codeRange()
                .setDescription("source method's try-statement")
                .setCodeElement(tryStatement.toString()));
        var catchClause = tryStatement.getCatchClauses().stream().filter(clause -> clause.getVariableDeclarations().stream().anyMatch(v->v.getType().getClassType().equals("IllegalArgumentException"))).findAny();
        if (catchClause.isPresent()) {
            var clause = catchClause.get();
            ranges.add(clause.codeRange()
                    .setDescription("source method's catch clause capturing the expected exception")
                    .setCodeElement(clause.toString()));
        }
        ranges.add(assertFailInvocation.codeRange()
                .setDescription("source method's assertFail invocation from the try-statement before migration")
                .setCodeElement(assertFailInvocation.toString()));
        return ranges;
    }

    @Override
    public List<CodeRange> rightSide() {
        List<CodeRange> ranges = new ArrayList<>();
        ranges.add(operationAfter.codeRange()
                .setDescription("method declaration after migration")
                .setCodeElement(operationAfter.toString()));
       ranges.add(assertThrows.codeRange()
                .setDescription("added Assert.assertThrows call")
                .setCodeElement(assertThrows.toString()));
        ranges.add(lambda.codeRange()
                .setDescription("extracted lambda from method's body")
                .setCodeElement(lambda.toString()));
        return ranges;
    }

    @Override
    public String toString() {
        return getName() +
                " from method " +
                operationBefore +
                " in class " +
                getClassName();
    }

    private String getClassName() {
        return operationAfter.getClassName();
    }

    @Override
    public RefactoringType getRefactoringType() {
        return RefactoringType.REPLACE_TRY_FAIL_WITH_ASSERT;
    }

    @Override
    public String getName() {
        return getRefactoringType().getDisplayName();
    }

    @Override
    public Set<ImmutablePair<String, String>> getInvolvedClassesBeforeRefactoring() {
        Set<ImmutablePair<String, String>> pairs = new LinkedHashSet<>();
        pairs.add(new ImmutablePair<>(operationBefore.getLocationInfo().getFilePath(), operationBefore.getClassName()));
        return pairs;
    }

    @Override
    public Set<ImmutablePair<String, String>> getInvolvedClassesAfterRefactoring() {
        Set<ImmutablePair<String, String>> pairs = new LinkedHashSet<>();
        pairs.add(new ImmutablePair<>(operationAfter.getLocationInfo().getFilePath(), operationAfter.getClassName()));
        return pairs;
    }

}
