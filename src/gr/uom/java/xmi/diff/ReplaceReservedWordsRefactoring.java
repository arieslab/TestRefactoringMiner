package gr.uom.java.xmi.diff;

import gr.uom.java.xmi.decomposition.AbstractCall;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.refactoringminer.api.Refactoring;
import org.refactoringminer.api.RefactoringType;

import java.util.*;

public class ReplaceReservedWordsRefactoring implements Refactoring {
    private final AbstractCall operationBefore;
    private final AbstractCall operationAfter;


    public ReplaceReservedWordsRefactoring(AbstractCall operationBefore,
                                         AbstractCall operationAfter) {
        this.operationBefore = operationBefore;
        this.operationAfter = operationAfter;
    }

    @Override
    public String toString() {
        return getName() ;
    }

    private String getClassName() {
        return operationAfter.getName();
    }

    @Override
    public List<CodeRange> leftSide() {
        List<CodeRange> ranges = new ArrayList<>();
        ranges.add(operationBefore.codeRange()
                .setDescription("original assert method invocation")
                .setCodeElement(operationBefore.toString()));
        return ranges;
    }

    @Override
    public List<CodeRange> rightSide() {
        List<CodeRange> ranges = new ArrayList<>();
        ranges.add(operationAfter.codeRange()
                .setDescription("assert method invocation after replacing reserved words")
                .setCodeElement(operationAfter.toString()));
        return ranges;
    }

    @Override
    public RefactoringType getRefactoringType() {
        return RefactoringType.REPLACE_RESERVED_WORDS;
    }


    @Override
    public String getName() {
        return this.getRefactoringType().getDisplayName();
    }

    @Override
    public Set<ImmutablePair<String, String>> getInvolvedClassesBeforeRefactoring() {
        Set<ImmutablePair<String, String>> pairs = new LinkedHashSet<>();
        pairs.add(new ImmutablePair<>(operationBefore.getLocationInfo().getFilePath(), operationBefore.getName()));
        return pairs;
    }

    @Override
    public Set<ImmutablePair<String, String>> getInvolvedClassesAfterRefactoring() {
        Set<ImmutablePair<String, String>> pairs = new LinkedHashSet<>();
        pairs.add(new ImmutablePair<>(operationAfter.getLocationInfo().getFilePath(), operationAfter.getName()));
        return pairs;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        ReplaceReservedWordsRefactoring other = (ReplaceReservedWordsRefactoring) obj;
        return Objects.equals(operationAfter, other.operationAfter)
                && Objects.equals(operationBefore, other.operationBefore)
                ;
    }
}