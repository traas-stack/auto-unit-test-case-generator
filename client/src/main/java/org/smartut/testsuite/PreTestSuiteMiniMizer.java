package org.smartut.testsuite;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.smartut.Properties;
import org.smartut.coverage.TestFitnessFactory;
import org.smartut.ga.ConstructionFailedException;
import org.smartut.setup.DependencyAnalysis;
import org.smartut.testcase.TestChromosome;
import org.smartut.testcase.TestFactory;
import org.smartut.testcase.TestFitnessFunction;
import org.smartut.testcase.statements.FunctionalMockStatement;
import org.smartut.testcase.statements.MethodStatement;
import org.smartut.testcase.statements.Statement;
import org.smartut.testcase.statements.reflection.PrivateFieldStatement;
import org.smartut.testcase.statements.reflection.PrivateMethodStatement;
import org.smartut.testcase.statements.reflection.ReflectionFactory;
import org.smartut.testcase.variable.VariableReference;
import org.smartut.testcase.variable.VariableReferenceImpl;

/**
 * Preprocess before minimize
 * test suite to delete unnecessary statements related to file
 * @date 2022/02/22
 */
public class PreTestSuiteMiniMizer {
    /**
     * Logger
     */
    private final static Logger logger = LoggerFactory.getLogger(PreTestSuiteMiniMizer.class);

    private final List<TestFitnessFactory<?>> testFitnessFactories = new ArrayList<>();

    private final Map<String, Set<String>> methodUsedFieldsPool = Collections.synchronizedMap(new HashMap<>());

    private final Set<String> allFields = new HashSet<>();

    private ReflectionFactory reflectionFactory;

    public PreTestSuiteMiniMizer(List<TestFitnessFactory<? extends TestFitnessFunction>> factories) {
        this.testFitnessFactories.addAll(factories);
        this.buildMethodFieldPool();
        this.buildAllFields();
    }

    public void minimize(TestSuiteChromosome suite) {
        long startTimestamp = System.currentTimeMillis();
        logger.warn("Before preMinimization {} total length of test suite", suite.totalLengthOfTestCases());
        suite.clearMutationHistory();
        minimizeTests(suite);
        logger.warn("After preMinimization {} total length of test suite", suite.totalLengthOfTestCases());
        long endTimestamp = System.currentTimeMillis();
        logger.warn("preMinimization cost: {} ms.", endTimestamp - startTimestamp);
    }

    private void minimizeTests(TestSuiteChromosome suite) {
        TestSuiteMinimizer.removeEmptyTestCases(suite);
        for (TestChromosome testChromosome : suite.tests) {
            minimizeTest(testChromosome, false);
        }
    }

    private void minimizeTest(TestChromosome testChromosome, boolean isVerifyFitnessEachDeletion) {
        logger.debug("Test before preMinimize: {}", testChromosome.size());
        //filter field type name unused
        Set<String> unusedFields = this.getMethodUnusedFields(testChromosome);
        TestChromosome originalTestChromosome = testChromosome.clone();
        int skipIndex = 1;
        // delete backward
        while (testChromosome.size() - skipIndex >= 0) {
            TestChromosome originalTestChromosomeTemp = testChromosome.clone();
            for (int i = testChromosome.size() - skipIndex; i >= 0; i--) {
                Statement statement = testChromosome.getTestCase().getStatement(i);
                if (statement instanceof PrivateFieldStatement) {
                    String fieldName = ((PrivateFieldStatement) statement).getFieldName();
                    if (unusedFields.contains(fieldName)) {
                        boolean modified;
                        try {
                            TestFactory testFactory = TestFactory.getInstance();
                            logger.debug("PreMinimizer deleting statement: {} index {}/{} from test",
                                testChromosome.getTestCase().getStatement(i).getCode(), i, testChromosome.size());
                            modified = testFactory.deleteStatementGracefully(testChromosome.getTestCase(), i);
                            if (modified){
                                // delete fields statement referenced by mock
                                modified = this.removeReferencesFieldStatement(testChromosome, (PrivateFieldStatement) statement, i);
                            }
                        } catch (ConstructionFailedException e) {
                            modified = false;
                            logger.debug("PreMinimizer deleting fail! statement: {} index: {}/{} from test",
                                testChromosome.getTestCase().getStatement(i).getCode(), i, testChromosome.size());
                        }
                        if (modified) {
                            if (isVerifyFitnessEachDeletion){
                                if (this.verifyFitnessIsWorse(originalTestChromosomeTemp, testChromosome)){
                                    logger.debug("fitness decrease after delete！！！！！");
                                    skipIndex ++;
                                }else {
                                    testChromosome.setChanged(true);
                                }
                            }else {
                                testChromosome.setChanged(true);
                            }
                            break;
                        } else {
                            testChromosome.setTestCase(originalTestChromosomeTemp.getTestCase());
                            logger.debug("PreMinimizer deleting failed");
                        }
                    }
                }
                skipIndex++;
            }
        }
        if (Properties.PRE_MINIMIZE_VERIFY_FITNESS){
            if (testChromosome.isChanged()) {
                // only execute once for fitness calculate after delete
                if (!isVerifyFitnessEachDeletion && verifyFitnessIsWorse(originalTestChromosome, testChromosome)){
                    testChromosome.setChanged(false);
                    logger.debug("fitness decrease，open delete verify！！！！！");
                    minimizeTest(testChromosome, true);
                }
            }
        }
        logger.debug("Test after preMinimize: {}", testChromosome.size());
    }

    // calculate fitness, if decrease, recovery
    private boolean verifyFitnessIsWorse(TestChromosome originalTestChromosome, TestChromosome testChromosome){
        testChromosome.clearCachedResults();
        testChromosome.getTestCase().clearCoveredGoals();
        List<TestFitnessFunction> goals = new ArrayList<>();
        for (TestFitnessFactory<?> ff : testFitnessFactories) {
            goals.addAll(ff.getCoverageGoals());
        }
        boolean isWorse = false;
        for (TestFitnessFunction goal : goals) {
            if (goal.getFitness(testChromosome) > originalTestChromosome.getFitness(goal)){
                isWorse = true;
                break;
            }
        }
        if (isWorse) {
            logger.debug("PreMinimize fail new fitness is greater than previous one, Keeping original version！！！");
            testChromosome.setTestCase(originalTestChromosome.getTestCase());
            testChromosome.setLastExecutionResult(originalTestChromosome.getLastExecutionResult());
        }
        return isWorse;
    }


    private boolean removeReferencesFieldStatement(TestChromosome testChromosome, PrivateFieldStatement statement, int position) {
        Set<VariableReference> variableReferences = statement.getVariableReferences();
        int refPosition = -1;
        int refPositionCount = 0;
        for (VariableReference variableReference : variableReferences) {
            try {
                if (variableReference instanceof VariableReferenceImpl) {
                    int stPosition = variableReference.getStPosition();
                    if (stPosition >= position || !(testChromosome.getTestCase().getStatement(stPosition) instanceof FunctionalMockStatement)) {
                        continue;
                    }
                    // statement.getFiledType() return null when using seed
                    String targetFieldTypeName = this.getFieldByFieldName(statement.getFieldName()).getType().getTypeName();
                    if (targetFieldTypeName.equals(variableReference.getType().getTypeName())) {
                        refPosition = variableReference.getStPosition();
                        refPositionCount++;
                    }
                }
            } catch (Throwable t) {
                logger.debug(t.getMessage());
            }
        }
        boolean modified = true;
        if (refPositionCount == 1) {
            try {
                TestFactory testFactory = TestFactory.getInstance();
                logger.debug("PreMinimizer Deleting statement: {} index: {}/{} from test",
                    testChromosome.getTestCase().getStatement(refPosition).getCode(), refPosition, testChromosome.size());
                modified = testFactory.deleteStatementGracefully(testChromosome.getTestCase(), refPosition);
            } catch (ConstructionFailedException e) {
                modified = false;
                logger.debug("PreMinimizer deleting fail! statement: {} index: {}/{} from test",
                    testChromosome.getTestCase().getStatement(refPosition).getCode(), refPosition, testChromosome.size());
            }
        }
        return modified;
    }

    private Set<String> getMethodUnusedFields(TestChromosome testChromosome) {
        Set<String> methodUnusedFields = new HashSet<>(allFields);
        for (int i = testChromosome.size() - 1; i >= 0; i--) {
            Statement statement = testChromosome.getTestCase().getStatement(i);
            if (!(statement instanceof PrivateFieldStatement) && statement instanceof MethodStatement) {
                String methodName;
                if (statement instanceof PrivateMethodStatement) {
                    methodName = ((PrivateMethodStatement) statement).getReflectedMethod().getMethod().getName();
                } else {
                    methodName = ((MethodStatement) statement).getMethod().getName();
                }
                if (methodUsedFieldsPool.get(methodName) != null) {
                    methodUnusedFields.removeAll(methodUsedFieldsPool.get(methodName));
                }
            }
        }
        return methodUnusedFields;
    }

    private void buildMethodFieldPool() {
        Class<?> targetClass = Properties.getTargetClassAndDontInitialise();
        ClassNode targetClassNode = DependencyAnalysis.getClassNode(targetClass.getName());
        if (targetClassNode.methods == null) {
            return;
        }
        // cache for method under test invoke cycling
        Set<MethodNode> methodNodeCashPool = new HashSet<>();
        for (MethodNode methodNode : targetClassNode.methods) {
            this.buildMethodUsedFieldsSeparate(targetClassNode, methodNodeCashPool, methodNode);
        }
    }


    private void buildMethodUsedFieldsSeparate(ClassNode targetClassNode, Set<MethodNode> cashMethodNode, MethodNode methodNode) {
        if (cashMethodNode.contains(methodNode)){
            return;
        }
        cashMethodNode.add(methodNode);
        if ("<clinit>".equals(methodNode.name) || "init".equals(methodNode.name) || "<init>".equals(methodNode.name)) {
            return;
        }
        InsnList insns = methodNode.instructions;
        Iterator<AbstractInsnNode> j = insns.iterator();
        Set<String> methodFields = new HashSet<>();
        while (j.hasNext()) {
            AbstractInsnNode insn = j.next();
            if (insn instanceof FieldInsnNode) {
                methodFields.add(((FieldInsnNode) insn).name);
            }else if (insn instanceof MethodInsnNode){
                String owner = ((MethodInsnNode) insn).owner;
                if (!owner.equals(targetClassNode.name)){
                    continue;
                }
                String tempMethodName = ((MethodInsnNode) insn).name;
                if (!this.methodUsedFieldsPool.containsKey(tempMethodName)){
                    this.buildMethodUsedFieldsSeparate(targetClassNode, cashMethodNode, tempMethodName);
                }
                // invoke again after analyzing inner invoke in a method
                if(this.methodUsedFieldsPool.containsKey(tempMethodName)){
                    methodFields.addAll(this.methodUsedFieldsPool.get(tempMethodName));
                }
            }
        }
        // do Not distinguish overload method
        if (!methodFields.isEmpty()) {
            if (this.methodUsedFieldsPool.containsKey(methodNode.name))
                this.methodUsedFieldsPool.get(methodNode.name).addAll(methodFields);
            else {
                this.methodUsedFieldsPool.put(methodNode.name, methodFields);
            }
        }
    }

    private void buildMethodUsedFieldsSeparate(ClassNode targetClassNode, Set<MethodNode> cashMethodNode, String methodName) {
        for (MethodNode methodNode : targetClassNode.methods) {
            if (methodNode.name.equals(methodName)){
                this.buildMethodUsedFieldsSeparate(targetClassNode, cashMethodNode, methodNode);
            }
        }
    }

    private List<Field> getAllFields() {
        if (reflectionFactory == null) {
            final Class<?> targetClass = Properties.getTargetClassAndDontInitialise();
            reflectionFactory = new ReflectionFactory(targetClass);
        }
        return reflectionFactory.getFields();
    }

    private void buildAllFields() {
        List<Field> fields = getAllFields();
        for (Field field : fields) {
            this.allFields.add(field.getName());
        }
    }

    private Field getFieldByFieldName(String fieldName) {
        List<Field> fields = getAllFields();
        for (Field field : fields) {
            if (field.getName().equals(fieldName)) {
                return field;
            }
        }
        return null;
    }
}
