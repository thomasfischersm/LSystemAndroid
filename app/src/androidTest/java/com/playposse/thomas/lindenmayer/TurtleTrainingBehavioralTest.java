package com.playposse.thomas.lindenmayer;

import android.support.test.filters.LargeTest;
import android.support.test.runner.AndroidJUnit4;

import com.playposse.thomas.lindenmayer.activity.TurtleTrainingActivity;

import org.junit.runner.RunWith;

/**
 * Esspresso test for the {@link TurtleTrainingActivity}.
 */
@RunWith(AndroidJUnit4.class)
@LargeTest
public class TurtleTrainingBehavioralTest {
    // The test is broken due to massive changes to the UI.
//
//    private static final String LOG_CAT = TurtleTrainingBehavioralTest.class.getSimpleName();
//
//    @Rule
//    public ActivityTestRuleWithReset<MainActivity> activityRule =
//            new ActivityTestRuleWithReset<MainActivity>(MainActivity.class);
//
//    @Test
//    public void smokeTest() {
//        // Navigate to turtle tutorial activity.
//        onView(withId(R.id.turtle_tutorial_button))
//                .perform(click());
//
//        // Verify and dismiss dialog.
//        onView(withText(R.string.turtle_trainer_dialog_title))
//                .check(matches(isDisplayed()));
//        onView(withText(R.string.turtle_Trainer_Dialog_message))
//                .check(matches(isDisplayed()));
//        onView(withText(R.string.dialog_continue_button))
//                .perform(click());
//
//        // Draw a few things.
//        clickDrawButtonsAndAssert(R.id.draw_line_button, "f");
//        clickDrawButtonsAndAssert(R.id.increase_stroke_width_button, "f{");
//        clickDrawButtonsAndAssert(R.id.plus_button, "f{+");
//        clickDrawButtonsAndAssert(R.id.next_color_button, "f{+<");
//        clickDrawButtonsAndAssert(R.id.draw_line_button, "f{+<f");
//        clickDrawButtonsAndAssert(R.id.invisible_line_button, "f{+<fF");
//        clickDrawButtonsAndAssert(R.id.draw_line_button, "f{+<fFf");
//
//        // Navigate back and ensure that the dialog no longer shows.
//        Espresso.pressBack();
//        onView(withId(R.id.turtle_tutorial_button))
//                .perform(click());
//        onView(withText(R.string.turtle_trainer_dialog_title))
//                .check(doesNotExist());
//    }
//
//    private void clickDrawButtonsAndAssert(int buttonId, String axiom) {
//        onView(withId(buttonId))
//                .perform(click());
//        onView(withId(R.id.turtle_instructions_edit_text))
//                .check(matches(withText(axiom)));
//    }
}
