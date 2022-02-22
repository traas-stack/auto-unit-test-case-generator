package org.smartut.testcase.mutation;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.smartut.Properties;
import org.smartut.testcase.TestCase;
import org.smartut.testcase.TestFactory;
import org.smartut.testcase.statements.FunctionalMockStatement;
import org.smartut.testcase.statements.PrimitiveStatement;
import org.smartut.testcase.statements.Statement;
import org.smartut.testcase.variable.NullReference;
import org.smartut.testcase.variable.VariableReference;
import org.smartut.utils.ListUtil;
import org.smartut.utils.Randomness;

import static java.util.stream.Collectors.toList;

/**
 * An insertion strategy that allows for modification of test cases by inserting random statements.
 * @author ryan.zjf
 * @date 2022/02/21
 */
public class StructureInsertion implements InsertionStrategy {

    private static final Logger logger = LoggerFactory.getLogger(StructureInsertion.class);

    private boolean initInsert = true;

    public StructureInsertion(boolean initInsert) {
        this.initInsert = initInsert;
    }
    /**
     * insert statement for init test case
     * @param test             init test case
     * @param lastPosition     statement position
     * @return                 last position of inserted statements
     */
    private int insertInitCaseStatement(TestCase test, int lastPosition) {
        double r = Randomness.nextDouble();
        int oldSize = test.size();
        int position = 0;
        boolean success = false;
        int insertStartPos = lastPosition + 1;

        // for the first insertion statement，we need to insert private field
        // judge whether private fields have been inserted
        if(oldSize == 0 && TestFactory.getInstance().getPrivateFieldsSize() > 0) {
            success = TestFactory.getInstance().insertAllPrivateField(test, lastPosition + 1);
            if (test.size() - oldSize > 1) {
                position += (test.size() - oldSize - 1);
            }
        } else {
            position = test.size();
            success = TestFactory.getInstance().insertRandomCall(test, lastPosition + 1);

            // This can happen if insertion had side effect of adding further previous statements in the
            // test, e.g., to handle input parameters.
            if (test.size() - oldSize > 1) {
                position += (test.size() - oldSize - 1);
            }
        }

        // can NOT be deleted if statement is inserted during init process
        // calculate the count of insert statements
        int insertStmtSize = test.size() - oldSize;
        // calculate last insert statement position
        int lastInsertPos = insertStartPos + insertStmtSize - 1;
        // Exception condition：insert number is greater than the test case length or lastInsertPos >= test size
        if( test.size() < insertStmtSize || lastInsertPos >= test.size()) {
            logger.warn("Test size is smaller than statements inserted in init case statement, test size is {}, "
                + "insert statement size is {}, lastInsertPos is {}", test.size(), insertStmtSize, lastInsertPos);
            for(Statement stmt : test) {
                stmt.setCouldMutationDelete(false);
            }
        } else {
            int index = 0;
            for(Statement stmt: test) {
                if(index++ >= insertStartPos) {
                    stmt.setCouldMutationDelete(false);
                }
                if(index > lastInsertPos) {
                    break;
                }
            }
        }

        if (success) {
            return position;
        } else {
            return -1;
        }
    }

    /**
     * insert statement for case mutation period
     * @param test              case needed to insert statement
     * @param lastPosition      statement position
     * @return
     */
    private int insertMutateCaseStatement(TestCase test, int lastPosition) {
        double r = Randomness.nextDouble();
        int oldSize = test.size();
        int position = 0;
        boolean success = false;

        // Insert a call to a variable (one that is used as a parameter for some function call
        // in the test case). The idea is to mutate the parameter so that new program states
        // can be reached in the function call.
        VariableReference var = selectRandomVariableForCall(test, lastPosition);
        if (var != null) {
            // find the last position where the selected variable is used in the test case
            final int lastUsage = test.getReferences(var).stream()
                .mapToInt(VariableReference::getStPosition)
                .max().orElse(var.getStPosition());

            if (lastUsage > var.getStPosition() + 1) {
                // If there is more than 1 statement where it is used, we randomly choose a position
                // Be notice, if test case has private field setting statement,
                // following statement should insert after that
                // for the private field has been successfully init in method body
                int privateFieldLastPos = test.getPrivateFieldLastPosition();
                int randomStart = var.getStPosition() + 1;
                // 如果privateFieldLastPos大于var的pos + 1，并且 privateFieldLastPos + 1 < lastUsage
                if(privateFieldLastPos > (var.getStPosition() + 1) && privateFieldLastPos + 1 < lastUsage) {
                    randomStart = privateFieldLastPos + 1;
                }

                if(privateFieldLastPos == lastUsage) {
                    position = privateFieldLastPos + 1;
                } else {
                    position = Randomness.nextInt(randomStart, // call has to be after the object is created
                        lastUsage                // but before the last usage
                    );
                }
            } else if(lastUsage == var.getStPosition()) {
                // The variable isn't used
                int privateFieldLastPos = test.getPrivateFieldLastPosition();
                position = lastUsage + 1;
                if(privateFieldLastPos > lastUsage) {
                    position = privateFieldLastPos + 1;
                }
            } else {
                // The variable is used at only one position, we insert at exactly that position
                position = lastUsage;
            }

            if(logger.isDebugEnabled()) {
                logger.debug("Inserting call at position " + position + ", chosen var: "
                    + var.getName() + ", distance: " + var.getDistance() + ", class: "
                    + var.getClassName());
            }

            success = TestFactory.getInstance().insertRandomCallOnObjectAtForMutate(test, var, position);

            // Adding new call on UUT because var was null?
        }

        if (test.size() - oldSize > 1) {
            position += (test.size() - oldSize - 1);
        }

        if (success) {
            return position;
        } else {
            return -1;
        }
    }

    @Override
    public int insertStatement(TestCase test, int lastPosition) {
        return initInsert ? insertInitCaseStatement(test, lastPosition)
            : insertMutateCaseStatement(test, lastPosition);
    }

    /**
     * In the given test case {@code test}, returns a random variable up to the specified {@code
     * position} for a subsequent call. If the test case is empty or the position is {@code 0},
     * {@code null} is returned.
     *
     * @param test the test case from which to select the variable
     * @param position the position in the test case up to which a variable shoulb be selected
     * @return the selected variable or {@code null} (see above)
     */
    private VariableReference selectRandomVariableForCall(TestCase test, int position) {
        if (test.isEmpty() || position == 0)
            return null;

        List<VariableReference> allVariables = test.getObjects(position);
        List<VariableReference> candidateVariables = new ArrayList<>();

        for(VariableReference var : allVariables) {

            if (!(var instanceof NullReference) &&
                !var.isVoid() &&
                !var.getGenericClass().isObject() &&
                !(test.getStatement(var.getStPosition()) instanceof PrimitiveStatement) &&
                !var.isPrimitive() &&
                !var.isWrapperType() &&
                !var.isString() &&
                (test.hasReferences(var) || var.getVariableClass().equals(Properties.getInitializedTargetClass()))&&
					/* Note: this check has been added only recently,
						to avoid having added calls to UUT in the middle of the test
					 */
					/*
					   Commented this out again, as it would mean that methods of the SUT class
					   that are declared in a superclass would not be inserted at all, but now
					   this may break some constraints.
					 */
                ///**
                // * Notice: CUT variable would not be chosen, superclass method would not be inserted
                // */
                // !var.getVariableClass().getName().equals(Properties.TARGET_CLASS) &&
                //do not directly call methods on mock objects
                ! (test.getStatement(var.getStPosition()) instanceof FunctionalMockStatement) ){

                candidateVariables.add(var);
            }
        }

        if(candidateVariables.isEmpty()) {
            return null;
        } else if(Properties.SORT_OBJECTS) {
            candidateVariables = candidateVariables.stream()
                .sorted(Comparator.comparingInt(VariableReference::getDistance))
                .collect(toList());
            return ListUtil.selectRankBiased(candidateVariables);
        } else {
            return Randomness.choice(candidateVariables);
        }
    }

}
