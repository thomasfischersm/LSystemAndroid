package com.playposse.thomas.lindenmayer.logic;


import com.playposse.thomas.lindenmayer.domain.Dimension;
import com.playposse.thomas.lindenmayer.domain.RuleSet;
import com.playposse.thomas.lindenmayer.domain.test.RuleSetTestUtil;

import org.junit.Test;

import static junit.framework.Assert.assertEquals;

public class BruteForceAlgorithmTest {

    @Test
    public void iterate_simpleRules() {
        RuleSet ruleSet = RuleSetTestUtil.createSampleRuleSet("f", 90, "f", "f+f");
        assertEquals("f", BruteForceAlgorithm.iterate(ruleSet, 0));
        assertEquals("f+f", BruteForceAlgorithm.iterate(ruleSet, 1));
        assertEquals("f+f+f+f", BruteForceAlgorithm.iterate(ruleSet, 2));
        assertEquals("f+f+f+f+f+f+f+f", BruteForceAlgorithm.iterate(ruleSet, 3));
    }

    @Test
    public void iterate_multipleRules() {
        RuleSet ruleSet = RuleSetTestUtil.createSampleRuleSet("ab", 90, "a", "bab", "b", "bbb");
        assertEquals("ab", BruteForceAlgorithm.iterate(ruleSet, 0));
        assertEquals("babbbb", BruteForceAlgorithm.iterate(ruleSet, 1));
        assertEquals("bbbbabbbbbbbbbbbbb", BruteForceAlgorithm.iterate(ruleSet, 2));
    }

    @Test
    public void computeDimension() {
        assertDimension(0, 0, 100, "f");
        assertDimension(0, 0, 50, "ff");
        assertDimension(0, 0, 50, "ff+");
        assertDimension(0, 0, 50, "ff+f");
        assertDimension(0, 0, 50, "ff+ff");
        assertDimension(100/3.0, 0, 100/3.0, "ff+ff+f+fff");
        assertDimension(100/3.0, 0, 100/3.0, "ff+ffgh+f+fffgh");
        assertDimension(50, 50, 50, "[f]+[f]+[f]+[f]");
        assertDimension(50, 50, 50, "[F]-[F]-[F]-[F]");
    }

    private static void assertDimension(
            double startX,
            double startY,
            double scaleFactor,
            String str) {

        Dimension dimension = BruteForceAlgorithm.computeDimension(str, 100, 100, 90);

        assertEquals("startX is wrong for " + str, startX, dimension.getStartX());
        assertEquals("startY is wrong for " + str, startY, dimension.getStartY());
        assertEquals("scaleFactor is wrong for " + str, scaleFactor, dimension.getScaleFactor());
    }
}
