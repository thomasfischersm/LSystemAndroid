package com.playposse.thomas.lindenmayer.suite;

import com.playposse.thomas.lindenmayer.domain.DimensionTest;
import com.playposse.thomas.lindenmayer.logic.BruteForceAlgorithmTest;
import com.playposse.thomas.lindenmayer.logic.EffortEstimatorTest;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

/**
 * Test suite for all plain unit tests.
 */
@RunWith(Suite.class)
@Suite.SuiteClasses({
        DimensionTest.class,
        BruteForceAlgorithmTest.class,
        EffortEstimatorTest.class})
public class UnitTestSuite {
}
