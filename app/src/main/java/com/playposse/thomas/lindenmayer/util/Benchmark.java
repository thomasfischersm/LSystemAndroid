package com.playposse.thomas.lindenmayer.util;

import android.util.Log;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * DO NOT INCLUDE IN PRODUCTION CODE!
 * <p>
 * <p>This class is used to benchmark critical pieces of the Java algorithm during development.</p>
 */
public class Benchmark {

    private static final String LOG_TAG = Benchmark.class.getSimpleName();

    private static final int REPETITION_COUNT = 10_000;
    private static final int STARTUP_DELAY_MS = 10_000;
    private static final int EXPECTED_RESULT = 30;

    private static String[] stringArray = new String[]{
            "apple",
            "banana",
            "monkey",
            "donkey",
            "trouble"};
    private static List<String> stringList = new ArrayList<>();

    public static void runBenchmark() {
        init();

        new Thread(new Runnable() {
            @Override
            public void run() {
                // Wait for the app to start and the CPU to quiet.
                try {
                    Thread.sleep(STARTUP_DELAY_MS);
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                }

                runBenchmark("plus char", new Runnable() {
                    @Override
                    public void run() {
                        if (!testPlusChar('c').equals("c")) {
                            Log.d(LOG_TAG, "run: Failed verification ");
                        }
                    }
                });

                runBenchmark("toString char", new Runnable() {
                    @Override
                    public void run() {
                        if (!testToStringChar('c').equals("c")) {
                            Log.d(LOG_TAG, "run: Failed verification ");
                        }
                    }
                });

                runBenchmark("valueOf char", new Runnable() {
                    @Override
                    public void run() {
                        if (!testValueOfChar('c').equals("c")) {
                            Log.d(LOG_TAG, "run: Failed verification ");
                        }
                    }
                });

                runBenchmark("foreach ArrayList", new Runnable() {
                    @Override
                    public void run() {
                        if (testArrayList() != EXPECTED_RESULT) {
                            Log.d(LOG_TAG, "run: Failed verification " + testArrayList());
                        }
                    }
                });

                runBenchmark("foreach array", new Runnable() {
                    @Override
                    public void run() {
                        if (testArray() != EXPECTED_RESULT) {
                            Log.d(LOG_TAG, "run: Failed verification " + testArray());
                        }
                    }
                });

                runBenchmark("for i ArrayList", new Runnable() {
                    @Override
                    public void run() {
                        if (testManualArrayList() != EXPECTED_RESULT) {
                            Log.d(LOG_TAG, "run: Failed verification " + testManualArrayList());
                        }
                    }
                });

                runBenchmark("for i array", new Runnable() {
                    @Override
                    public void run() {
                        if (testManualArray() != EXPECTED_RESULT) {
                            Log.d(LOG_TAG, "run: Failed verification " + testManualArray());
                        }
                    }
                });

                runBenchmark("foreach ArrayList2", new Runnable() {
                    @Override
                    public void run() {
                        if (!testArrayList2()) {
                            Log.d(LOG_TAG, "run: Failed verification " + testArrayList2());
                        }
                    }
                });

                runBenchmark("foreach arra2y", new Runnable() {
                    @Override
                    public void run() {
                        if (!testArray2()) {
                            Log.d(LOG_TAG, "run: Failed verification " + testArray2());
                        }
                    }
                });

                runBenchmark("for i ArrayList2", new Runnable() {
                    @Override
                    public void run() {
                        if (!testManualArrayList2()) {
                            Log.d(LOG_TAG, "run: Failed verification " + testManualArrayList2());
                        }
                    }
                });

                runBenchmark("for i array2", new Runnable() {
                    @Override
                    public void run() {
                        if (!testManualArray2()) {
                            Log.d(LOG_TAG, "run: Failed verification " + testManualArray2());
                        }
                    }
                });
            }
        }).start();

    }

    private static void init() {
        Collections.addAll(stringList, stringArray);
    }

    private static void runBenchmark(String label, Runnable runnable) {
        Log.d(LOG_TAG, "runBenchmark: Starting " + label);
        long start = System.currentTimeMillis();

        for (int i = 0; i < REPETITION_COUNT; i++) {
            runnable.run();
        }

        long end = System.currentTimeMillis();
        Log.d(LOG_TAG, "runBenchmark: " + label + " completed in " + (end - start) + "ms.");
    }

    private static int testArrayList() {
        StringBuilder sb = new StringBuilder();
        for (String str : stringList) {
            sb.append(str);
        }
        return sb.toString().length();
    }

    private static int testArray() {
        StringBuilder sb = new StringBuilder();
        for (String str : stringArray) {
            sb.append(str);
        }
        return sb.toString().length();
    }

    private static int testManualArrayList() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < stringList.size(); i++) {
            String str = stringList.get(i);
            sb.append(str);
        }
        return sb.toString().length();
    }

    private static int testManualArray() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < stringArray.length; i++) {
            String str = stringArray[i];
            sb.append(str);
        }
        return sb.toString().length();
    }

    private static boolean testArrayList2() {
        for (String str : stringList) {
            if (str.equals("never")) {
                return false;
            }
        }
        return true;
    }

    private static boolean testArray2() {
        for (String str : stringArray) {
            if (str.equals("never")) {
                return false;
            }
        }
        return true;
    }

    private static boolean testManualArrayList2() {
        for (int i = 0; i < stringList.size(); i++) {
            String str = stringList.get(i);
            if (str.equals("never")) {
                return false;
            }
        }
        return true;
    }

    private static boolean testManualArray2() {
        for (int i = 0; i < stringArray.length; i++) {
            String str = stringArray[i];
            if (str.equals("never")) {
                return false;
            }
        }
        return true;
    }

    private static String testPlusChar(char c) {
        return "" + c;
    }

    private static String testToStringChar(char c) {
        return Character.toString(c);
    }

    private static String testValueOfChar(char c) {
        return String.valueOf(c);
    }
}
