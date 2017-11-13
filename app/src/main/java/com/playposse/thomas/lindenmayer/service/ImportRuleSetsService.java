package com.playposse.thomas.lindenmayer.service;

import android.app.IntentService;
import android.content.ContentValues;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import com.playposse.thomas.lindenmayer.R;
import com.playposse.thomas.lindenmayer.contentprovider.LindenmayerContentContract.RuleSetTable;
import com.playposse.thomas.lindenmayer.contentprovider.LindenmayerDatabaseHelper;
import com.playposse.thomas.lindenmayer.contentprovider.QueryHelper;
import com.playposse.thomas.lindenmayer.contentprovider.parser.RuleSetConverter;
import com.playposse.thomas.lindenmayer.data.AppPreferences;
import com.playposse.thomas.lindenmayer.domain.RuleSet;
import com.playposse.thomas.lindenmayer.util.DatabaseDumper;

import org.json.JSONException;

import java.io.IOException;
import java.util.List;

/**
 * An {@link IntentService} that reads the JSON files to import rule sets:
 * <ul>
 * <li>
 * The sample rule sets are embedded as a JSON file. It's easier to manage the JSON file.
 * </li>
 * <li>
 * In older app version, the users custom rule sets were stored in the file system. On an
 * upgrade run, those rule sets have to be imported.
 * </li>
 * </ul>
 */
public class ImportRuleSetsService extends IntentService {

    private static final String LOG_TAG = ImportRuleSetsService.class.getSimpleName();

    private static final String SERVICE_NAME = "ImportRuleSetsService";

    public ImportRuleSetsService() {
        super(SERVICE_NAME);
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        Log.i(LOG_TAG, "onHandleIntent: Start running ImportRuleSetsService.");
        long start = System.currentTimeMillis();

        try {
            importRuleSets(
                    DataReader.readSampleRuleSets(getResources()),
                    RuleSetTable.SAMPLE_TYPE);

            if (!AppPreferences.hasImportedUserRuleSets(getApplicationContext())) {
                Log.i(LOG_TAG, "onHandleIntent: Importing user rule sets.");
                importRuleSets(
                        DataReader.readUserRuleSets(getApplicationContext()),
                        RuleSetTable.PRIVATE_TYPE);

                AppPreferences.setHasImportedUserRuleSets(getApplicationContext(), true);
            } else {
                Log.i(LOG_TAG, "onHandleIntent: Skipping import of user rule sets");
            }
        } catch (IOException | JSONException ex) {
            Log.e(LOG_TAG, "onHandleIntent: Failed to import rule sets.", ex);
            showImportFailureToast();
        }

        long end = System.currentTimeMillis();
        Log.i(LOG_TAG, "onHandleIntent: Completed running ImportRuleSetsService in "
                + (end - start) + "ms.");

        LindenmayerDatabaseHelper mainDatabaseHelper =
                new LindenmayerDatabaseHelper(getApplicationContext());
        DatabaseDumper.dumpTables(mainDatabaseHelper);
        mainDatabaseHelper.close();
    }

    private void importRuleSets(List<RuleSet> ruleSets, int ruleSetType) {
        for (RuleSet ruleSet : ruleSets) {
            String name = ruleSet.getName();
            String json = RuleSetConverter.write(ruleSet);

            ContentValues values = new ContentValues();
            values.put(RuleSetTable.NAME_COLUMN, name);
            values.put(RuleSetTable.RULE_SET_COLUMN, json);
            values.put(RuleSetTable.TYPE_COLUMN, ruleSetType);

            Long existingRuleSetId = QueryHelper.doesRulSetExistByName(
                    getContentResolver(),
                    name,
                    ruleSetType);
            if (existingRuleSetId == null) {
                getContentResolver().insert(RuleSetTable.CONTENT_URI, values);
            } else {
                getContentResolver().update(
                        RuleSetTable.CONTENT_URI, values,
                        RuleSetTable.ID_COLUMN + "=?",
                        new String[]{existingRuleSetId.toString()});
            }
        }
    }

    private void showImportFailureToast() {
        Toast.makeText(getApplicationContext(), R.string.rule_set_import_failed, Toast.LENGTH_LONG)
                .show();
    }
}
