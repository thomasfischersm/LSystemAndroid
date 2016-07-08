package com.playposse.thomas.lindenmayer.suite;

import com.playposse.thomas.lindenmayer.ApplicationTest;
import com.playposse.thomas.lindenmayer.RuleFlowBehaviorTest;
import com.playposse.thomas.lindenmayer.TurtleTrainingBehavioralTest;
import com.playposse.thomas.lindenmayer.data.DataReaderTest;
import com.playposse.thomas.lindenmayer.logic.BruteForceAlgorithmInstrumentedTest;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

/**
 * Test suite for all instrumented tests.
 */
@RunWith(Suite.class)
@Suite.SuiteClasses({
        BruteForceAlgorithmInstrumentedTest.class,
        DataReaderTest.class,
        ApplicationTest.class,
        RuleFlowBehaviorTest.class,
        TurtleTrainingBehavioralTest.class})
public class AndroidTestSuite {
}
