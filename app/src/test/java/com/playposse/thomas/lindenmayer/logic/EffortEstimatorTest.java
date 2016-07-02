package com.playposse.thomas.lindenmayer.logic;

import com.playposse.thomas.lindenmayer.domain.RuleSet;
import com.playposse.thomas.lindenmayer.domain.test.RuleSetTestUtil;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class EffortEstimatorTest {

    @Test
    public void estimateIterations() {
        estimateIterations(3, "-f-", "f", "f+f", 0, "-f-");
        estimateIterations(5, "-f-", "f", "f+f", 1, "-f+f-");
        estimateIterations(9, "-f-", "f", "f+f", 2, "-f+f+f+f-");
        estimateIterations(17, "-f-", "f", "f+f", 3, "-f+f+f+f+f+f+f+f-");
        estimateIterations(33, "-f-", "f", "f+f", 4, "-f+f+f+f+f+f+f+f+f+f+f+f+f+f+f+f-");
    }

    private void estimateIterations(
            int effort,
            String axiom,
            String match,
            String replacement,
            int iterationCount,
            String expectedResultString) {

        RuleSet ruleSet = RuleSetTestUtil.createSampleRuleSet(axiom, 90, match, replacement);

        // This isn't the target of the test. It simply validates that the expectation of the
        // test author is correct.
        assertEquals(expectedResultString, BruteForceAlgorithm.iterate(ruleSet, iterationCount));

        assertEquals(effort, EffortEstimator.estimateIterations(ruleSet, iterationCount));
    }
}
