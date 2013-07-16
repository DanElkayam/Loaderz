package com.zenithed.loaderz.tests;

import android.app.Fragment;
import android.test.ActivityInstrumentationTestCase2;
import android.test.suitebuilder.annotation.MediumTest;
import android.widget.TextView;

import com.zenithed.loaderz.MainActivity;
import com.zenithed.loaderz.R;

import junit.framework.TestResult;

public class MainActivityTest extends ActivityInstrumentationTestCase2<MainActivity> {

    private TextView mTextView;

    /**
     * Creates an {@link android.test.ActivityInstrumentationTestCase2} that tests the {@link MainActivity} activity.
     */
    public MainActivityTest() {
        super(MainActivity.class);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        final MainActivity a = getActivity();
        // ensure a valid handle to the activity has been returned
        assertNotNull(a);
    }

    /**
     * The name 'test preconditions' is a convention to signal that if this
     * test doesn't pass, the test case was not set up properly and it might
     * explain any and all failures in other tests.  This is not guaranteed
     * to run before other tests, as junit uses reflection to find the tests.
     */
    @MediumTest
    public void testPreconditions() {
        Fragment fragment = getActivity().getFragmentManager().findFragmentById(R.id.fragment_container);
        assertNotNull(fragment);
    }

}

