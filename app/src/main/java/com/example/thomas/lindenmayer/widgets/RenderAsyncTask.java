package com.example.thomas.lindenmayer.widgets;

import android.os.AsyncTask;
import android.util.Log;

import com.example.thomas.lindenmayer.domain.Fragment;
import com.example.thomas.lindenmayer.domain.RuleSet;
import com.example.thomas.lindenmayer.logic.RuleProcessor;

/**
 * Created by Thomas on 5/20/2016.
 */
public class RenderAsyncTask extends AsyncTask<Void, Void, Fragment> {

    private static final String LOG_CAT = RenderAsyncTask.class.getSimpleName();

    private final RuleSet ruleSet;
    private final FractalView fractalView;
    private final int iterationCount;

    public RenderAsyncTask(RuleSet ruleSet, FractalView fractalView, int iterationCount) {
        this.ruleSet = ruleSet;
        this.fractalView = fractalView;
        this.iterationCount = iterationCount;
    }

    @Override
    protected Fragment doInBackground(Void... params) {
        Log.i(LOG_CAT, "Start computing fragment.");
        Fragment fragment =  RuleProcessor.runIterations(ruleSet, iterationCount);
        Log.i(LOG_CAT, "Done computing fragment.");
        return fragment;
    }

    @Override
    protected void onPostExecute(Fragment fragment) {
        fractalView.assignFragment(fragment, ruleSet.getDirectionIncrement());
        fractalView.invalidate();
        Log.i(LOG_CAT, "Caused view to redraw.");
    }
}
