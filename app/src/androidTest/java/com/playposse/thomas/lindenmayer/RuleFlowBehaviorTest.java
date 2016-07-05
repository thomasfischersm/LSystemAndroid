package com.playposse.thomas.lindenmayer;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.Espresso;
import android.support.test.filters.LargeTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.playposse.thomas.lindenmayer.data.AppPreferences;

import org.junit.After;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.Espresso.openActionBarOverflowOrOptionsMenu;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.scrollTo;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withContentDescription;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class RuleFlowBehaviorTest {

    private static String RULE_SET_NAME = "Sample Rule";
    private static String AXIOM = "f++f++f";
    private static int DIRECTION_INCREMENT = 60;
    private static String MATCH = "f";
    private static String REPLACEMENT = "f-f++f-f";

    @Rule
    public ActivityTestRule<MainActivity> activityRule =
            new ActivityTestRule<MainActivity>(MainActivity.class) {
                @Override
                protected void beforeActivityLaunched() {
                    // Ensure that no file with user defined rules is left over.
                    Context context = InstrumentationRegistry.getTargetContext();
                    context.deleteFile("userDefinedRuleSets.json");

                    // Reset shared preferences.
                    InstrumentationRegistry.getContext().getSharedPreferences(
                            AppPreferences.SHARED_PREFERENCES_NAME,
                            Context.MODE_PRIVATE)
                            .edit()
                            .clear()
                            .commit();

                    super.beforeActivityLaunched();
                }
            };

    @After
    public void cleanup() {
        // Ensure that no file with user defined rules is left over.
        Context context = InstrumentationRegistry.getTargetContext();
        context.deleteFile("userDefinedRuleSets.json");
    }

    @Test
    public void createAndSaveRule() {
        // No user defined rules should exist because we should have a clean setup.
//        onView(withId(R.id.userDefinedLabel))
//                .check(matches(not(isDisplayed())));

        // Navigate to RuleActivity.
        onView(withId(R.id.newButton))
                .perform(click());

        // Create a minimal rule.
        onView(withId(R.id.axiomText))
                .perform(typeText(AXIOM));
        onView(withId(R.id.directionIncrementText))
                .perform(typeText("" + DIRECTION_INCREMENT));
        onView(withContentDescription(R.string.rules_activity_match_content_description))
                .perform((typeText(MATCH)));
//        onView(withContentDescription(R.string.rules_activity_replacement_content_description))
//                .perform((typeText(REPLACEMENT)));

        // Save the rule.
        onView(withId(R.id.action_save))
                .perform(click());
        onView(withId(R.id.fileNameText))
                .perform(typeText(RULE_SET_NAME));
        onView(withId(R.id.saveButton))
                .perform(click());

        // Verify the new rule on the MainActivity.
        Espresso.pressBack();
//        onView(withId(R.id.userDefinedLabel))
//                .check(matches(isDisplayed()));
        onView(withText(RULE_SET_NAME))
                .perform(click());
        onView(withId(R.id.axiomText))
                .check(matches(withText(AXIOM)));
        onView(withId(R.id.directionIncrementText))
                .check(matches(withText("" + DIRECTION_INCREMENT)));
    }

    @Test
    public void ensureHelpAccessible() {
        // Check help accessible in MainActivity.
        openHelpActivityAndReturn();

        // Check help accessible in RulesActivity.
        onView(withText("Crystal"))
                .perform(scrollTo(), click());
        openHelpActivityAndReturn();

        // Check help accessible in RenderingActivity.
        onView(withId(R.id.goButton))
                .perform(click());
        openHelpActivityAndReturn();
    }

    private void openHelpActivityAndReturn() {
        openActionBarOverflowOrOptionsMenu(InstrumentationRegistry.getTargetContext());
        onView(withText(R.string.help_menu_title))
                .perform(click());
        onView(withText(R.string.help_about_heading))
                .check(matches(isDisplayed()));
        Espresso.pressBack();
    }

    @Test
    public void renderSampleRuleSet() {
        onView(withText("Crystal"))
                .perform(scrollTo(), click());
        onView(withId(R.id.goButton))
                .perform(click());
        onView(withId(R.id.incrementIterationButton))
                .perform(click());
    }
}
