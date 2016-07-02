package com.playposse.thomas.lindenmayer.bruteforce;


import com.playposse.thomas.lindenmayer.domain.Fragment;
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
        assertDimension(0, 0, 0, 1, 100, "f");
        assertDimension(0, 0, 0, 2, 50, "ff");
        assertDimension(0, 0, 0, 2, 50, "ff+");
        assertDimension(0, 1, 0, 2, 50, "ff+f");
        assertDimension(0, 2, 0, 2, 50, "ff+ff");
        assertDimension(-1, 2, 0, 2, 100/3.0, "ff+ff+f+fff");
        assertDimension(-1, 2, 0, 2, 100/3.0, "ff+ffgh+f+fffgh");
        assertDimension(-1, 1, -1, 1, 50, "[f]+[f]+[f]+[f]");
        assertDimension(-1, 1, -1, 1, 50, "[F]-[F]-[F]-[F]");
    }

    private static void assertDimension(
            double minX,
            double maxX,
            double minY,
            double maxY,
            double scaleFactor,
            String str) {

        Fragment.Dimension dimension = BruteForceAlgorithm.computeDimension(str, 100, 100, 90);

        assertEquals("minX is wrong for " + str, minX, dimension.getMinX());
        assertEquals("maxX is wrong for " + str, maxX, dimension.getMaxX());
        assertEquals("minY is wrong for " + str, minY, dimension.getMinY());
        assertEquals("maxY is wrong for " + str, maxY, dimension.getMaxY());
        assertEquals("scaleFactor is wrong for " + str, scaleFactor, dimension.getScaleFactor());
    }
}
