package com.playposse.thomas.lindenmayer.logic;

import com.playposse.thomas.lindenmayer.domain.CompositeFragment;
import com.playposse.thomas.lindenmayer.domain.Fragment;
import com.playposse.thomas.lindenmayer.domain.RuleSet;
import com.playposse.thomas.lindenmayer.domain.StringFragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Thomas on 5/19/2016.
 */
public class RuleProcessor {

    private static Fragment createInitialFragment(RuleSet ruleSet) {
        return new StringFragment(ruleSet.getAxiom());
    }

    private static Fragment runFirstIteration(Fragment fragment, RuleSet ruleSet, RuleLibrary ruleLibrary) {
        List<Fragment> result = new ArrayList<>();

        String str = fragment.getString();
        for (int i = 0; i < str.length(); i++) {
            String c = "" + str.charAt(i);
            if (ruleSet.hasRule(c)) {
                Fragment f = ruleLibrary.getFragment(c);
                if (f != null) {
                    result.add(f);
                } else {
                    f = new StringFragment(ruleSet.getRule(c).getReplacement());
                    result.add(f);
                    ruleLibrary.addTranslation(c, f);
                }
            } else {
                result.add(new StringFragment(c));
            }
        }

        return new CompositeFragment(result);
    }

    private static RuleLibrary createLibraryForEachRule(RuleSet ruleSet) {
        RuleLibrary ruleLibrary = new RuleLibrary();

        for (RuleSet.Rule rule : ruleSet.getRules()) {
            String c = rule.getMatch();
            ruleLibrary.addTranslation(c, new StringFragment(rule.getReplacement()));
        }

        return ruleLibrary;
    }

    private static RuleLibrary createLibraryForEachRule(RuleSet ruleSet, RuleLibrary oldRuleLibrary) {
        RuleLibrary newRuleLibrary = new RuleLibrary();

        for (RuleSet.Rule rule : ruleSet.getRules()) {
            String match = rule.getMatch();
            String replacement = rule.getReplacement();
            List<Fragment> fragments = new ArrayList<>();

            for (int i = 0; i < replacement.length(); i++) {
                String c = "" + replacement.charAt(i);
                Fragment oldFragment = oldRuleLibrary.getFragment(c);
                if (oldFragment != null) {
                    fragments.add(oldFragment);
                } else {
                    fragments.add(new StringFragment(c));
                }
            }

            newRuleLibrary.addTranslation(match, new CompositeFragment(fragments));
        }

        return newRuleLibrary;
    }

    private static Fragment runFinalIteration(String axiom, RuleLibrary ruleLibrary) {
        List<Fragment> result = new ArrayList<>();

        for (int i = 0; i < axiom.length(); i++) {
            String c = "" + axiom.charAt(i);
            Fragment fragment = ruleLibrary.getFragment(c);
            if (fragment != null) {
                result.add(fragment);
            } else {
                result.add(new StringFragment(c));
            }
        }

        return new CompositeFragment(result);
    }

    public static Fragment runIterations(RuleSet ruleSet, int iterationCount) {
        RuleLibrary ruleLibrary = createLibraryForEachRule(ruleSet);

        if (iterationCount > 1) {
            for (int i = 0; i < iterationCount - 1; i++) {
                ruleLibrary = createLibraryForEachRule(ruleSet, ruleLibrary);
            }
        }

        return runFinalIteration(ruleSet.getAxiom(), ruleLibrary);
    }

    private static class RuleLibrary {
        Map<String, Fragment> translationCache = new HashMap<>();

        private Fragment getFragment(String matchString) {
            return translationCache.get(matchString);
        }

        private void addTranslation(String matchString, Fragment fragment) {
            translationCache.put(matchString, fragment);
        }
    }
}
