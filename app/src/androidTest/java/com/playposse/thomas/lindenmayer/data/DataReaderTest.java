package com.playposse.thomas.lindenmayer.data;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.filters.SmallTest;
import android.support.test.runner.AndroidJUnit4;

import com.playposse.thomas.lindenmayer.domain.RuleSet;
import com.playposse.thomas.lindenmayer.domain.test.RuleSetTestUtil;

import junit.framework.Assert;

import org.json.JSONException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.util.List;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

@RunWith(AndroidJUnit4.class)
@SmallTest
public class DataReaderTest {

    private static String RULE_SET_NAME = "Sample Rule";
    private static String AXIOM = "f++f++f";
    private static int DIRECTION_INCREMENT = 60;
    private static String MATCH = "f";
    private static String REPLACEMENT = "f-f++f-f";
    private static RuleSet RULE_SET =
            RuleSetTestUtil.createSampleRuleSet(AXIOM, DIRECTION_INCREMENT, MATCH, REPLACEMENT);

    @Before
    public void setUp() {
        // Ensure that no file with user defined rules is left over.
        Context context = InstrumentationRegistry.getTargetContext();
        context.deleteFile("userDefinedRuleSets.json");

        RULE_SET.setName(RULE_SET_NAME);
    }

    @Test
    public void testUserRuleSets() throws IOException, JSONException {
        // Ensure that no rule set exists.
        Context context = InstrumentationRegistry.getTargetContext();
        assertEquals(0, DataReader.readUserRuleSets(context).size());

        // Save a rule set.
        DataReader.saveUserRuleSets(context, RULE_SET);

        // Load the rule set back.
        List<RuleSet> ruleSets = DataReader.readUserRuleSets(context);
        assertEquals(1, ruleSets.size());
        RuleSet ruleSet = ruleSets.get(0);

        // Verify the contents of the rule set.
        assertEquals(AXIOM, ruleSet.getAxiom());
        assertEquals(DIRECTION_INCREMENT, ruleSet.getDirectionIncrement());
        assertEquals(1, ruleSet.getRules().size());
        assertEquals(MATCH, ruleSet.getRules().get(0).getMatch());
        assertEquals(REPLACEMENT, ruleSet.getRules().get(0).getReplacement());

        // Test #doesUserRuleSetExist().
        assertTrue(DataReader.doesUserRuleSetExist(context, RULE_SET_NAME));
        assertFalse(DataReader.doesUserRuleSetExist(context, "does not exist"));

        // Remove the rule set.
        DataReader.deleteUserRuleSet(context, RULE_SET_NAME);
        assertEquals(0, DataReader.readUserRuleSets(context).size());
    }

    @Test
    public void testSampleRuleSets() throws IOException, JSONException {
        // Ensure that all samples are installed.
        Context context = InstrumentationRegistry.getTargetContext();
        List<RuleSet> ruleSets = DataReader.readSampleRuleSets(context.getResources());
        assertEquals(13, ruleSets.size());
        Assert.assertEquals("Box", ruleSets.get(0).getName());
        Assert.assertEquals("Box fractal", ruleSets.get(1).getName());
        Assert.assertEquals("Crystal", ruleSets.get(2).getName());
        Assert.assertEquals("Dragon Curve", ruleSets.get(3).getName());
        Assert.assertEquals("Fractal plant", ruleSets.get(4).getName());
        Assert.assertEquals("Hexagonal gosper", ruleSets.get(5).getName());
        Assert.assertEquals("Hilbert curve I", ruleSets.get(6).getName());
        Assert.assertEquals("Hilbert curve II", ruleSets.get(7).getName());
        Assert.assertEquals("Koch Curve", ruleSets.get(8).getName());
        Assert.assertEquals("Penrose", ruleSets.get(9).getName());
        Assert.assertEquals("Random", ruleSets.get(10).getName());
        Assert.assertEquals("Rings", ruleSets.get(11).getName());
        Assert.assertEquals("Sickness", ruleSets.get(12).getName());
    }
}
