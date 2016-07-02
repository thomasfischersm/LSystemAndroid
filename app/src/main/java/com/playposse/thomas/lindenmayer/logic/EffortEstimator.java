package com.playposse.thomas.lindenmayer.logic;

import com.playposse.thomas.lindenmayer.domain.RuleSet;

/**
 * Helper class that estimates the number of letters at a certain iteration.
 *
 * <p>The algorithm is imprecise because it's more important to be fast than fully correct.
 */
public class EffortEstimator {

    public static int estimateIterations(RuleSet ruleSet, int iterations) {
        if (iterations == 0) {
            return ruleSet.getAxiom().length();
        }

        int axiomLength = ruleSet.getAxiom().length();
        int count = axiomLength;
        for (RuleSet.Rule rule : ruleSet.getRules()) {
            int matchInAxiomCount = countOccurrences(ruleSet.getAxiom(), rule.getMatch().charAt(0));
            int matchInRuleCount = countOccurrences(rule.getReplacement(), rule.getMatch().charAt(0));
            int singleSubstitutionGrowth = rule.getReplacement().length() - 1;
            int axiomGrowth = matchInAxiomCount * singleSubstitutionGrowth;
            int ruleGrowth = matchInRuleCount * singleSubstitutionGrowth;

            count += axiomGrowth;
            if (iterations > 1) {
                for (int i = 1; i < iterations; i++) {
                    count += singleSubstitutionGrowth * Math.pow(matchInRuleCount, i);
                }
            }
        }

        return count;

        // An improvement would be to include the computation of how rules affect each other.
    }

    private static int countOccurrences(String str, char c) {
        int count = 0;
        for (int i = 0; i < str.length(); i++) {
            if (str.charAt(i) == c) {
                count++;
            }
        }
        return count;
    }
}
