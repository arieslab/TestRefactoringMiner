package gr.uom.java.xmi.diff;

import gr.uom.java.xmi.UMLAttribute;
import gr.uom.java.xmi.UMLOperation;
import gr.uom.java.xmi.decomposition.UMLOperationBodyMapper;
import org.refactoringminer.api.Refactoring;

import java.util.*;

/**
 * @author victorgveloso
 * Compares two test methods and detect test-related refactorings
 */
public class TestOperationDiff {
    private final UMLOperation removed;
    private final UMLOperation added;
    private final Collection<Refactoring> refactorings = new LinkedHashSet<>();
    private List<UMLAttribute> addedAttributes;
    private List<UMLAttribute> removedAttributes;
    private UMLOperationBodyMapper mapper;

    public TestOperationDiff(UMLOperationBodyMapper mapper, UMLAbstractClassDiff classDiff, Collection<Refactoring> refactorings) {
        this(mapper.getOperation1(), mapper.getOperation2(), refactorings);
        addedAttributes = classDiff.addedAttributes;
        removedAttributes = classDiff.removedAttributes;
        this.mapper = mapper;
    }

    TestOperationDiff(UMLOperation removedOperation, UMLOperation addedOperation, Collection<Refactoring> refactorings) {
        this(removedOperation, addedOperation);
        this.refactorings.addAll(refactorings);
    }

    TestOperationDiff(UMLOperation removedOperation, UMLOperation addedOperation) {
        removed = removedOperation;
        added = addedOperation;
    }

    static boolean isTestOperation(UMLOperation operation) {
        return operation.hasTestAnnotation();
    }

    public TryWithFailToExpectedExceptionRuleRefactoring getJUnit3AssertFailToJUnit4ExpectedExceptionRefactoring() {
        TryWithFailToExpectedExceptionRuleDetection detector;
        detector = new TryWithFailToExpectedExceptionRuleDetection(mapper, addedAttributes);
        return detector.check();
    }

    public ExpectedAnnotationToAssertThrowsRefactoring getJUnit4ExpectedExceptionToJUnit5AssertThrowsRefactoring() {
        ExpectedAnnotationToAssertThrowsDetection detector = new ExpectedAnnotationToAssertThrowsDetection(removed, added, refactorings);
        return detector.check();
    }

    private ExceptionRuleToAssertThrowsRefactoring getExceptionToAssertThows() {
        ExceptionRuleToAssertThrowsDetection detector = new ExceptionRuleToAssertThrowsDetection(mapper, refactorings, removedAttributes);
        return detector.check();
    }

    private ArrayList<AddArgsToAssertRefactoring> getAddArgs() {
            AddArgsToAssertDetection detector1 = new AddArgsToAssertDetection(mapper);
        return detector1.check();
    }


    public Set<Refactoring> getRefactorings() {
        Set<Refactoring> refactorings = new LinkedHashSet<>();
        TryWithFailToExpectedExceptionRuleRefactoring jUnit3To4RuleBasedRefactoring = getJUnit3AssertFailToJUnit4ExpectedExceptionRefactoring();
        if (Objects.nonNull(jUnit3To4RuleBasedRefactoring)) {
            refactorings.add(jUnit3To4RuleBasedRefactoring);
        }
        ExpectedAnnotationToAssertThrowsRefactoring jUnit4To5Refactoring = getJUnit4ExpectedExceptionToJUnit5AssertThrowsRefactoring();
        if (Objects.nonNull(jUnit4To5Refactoring)) {
            refactorings.add(jUnit4To5Refactoring);
        }
        ExceptionRuleToAssertThrowsRefactoring exceptionToAssertThows = getExceptionToAssertThows();
        if (Objects.nonNull(exceptionToAssertThows)) {
            refactorings.add(exceptionToAssertThows);
        }
        ArrayList<AddArgsToAssertRefactoring> assertArg = getAddArgs();
        if(Objects.nonNull(assertArg)){
            refactorings.addAll(assertArg);
        }
        return refactorings;
        }


}
