package com.sreesha.android.moviebuzz.Settings;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;

import com.sreesha.android.moviebuzz.DataHandlerClasses.MovieContract;
import com.sreesha.android.moviebuzz.DataHandlerClasses.MovieDataDBHelper;
import com.sreesha.android.moviebuzz.R;

public class SettingsActivity extends AppCompatActivity {

    /**
     * A preference value change listener that updates the preference's summary
     * to reflect its new value.
     */
    private static Preference.OnPreferenceChangeListener sBindPreferenceSummaryToValueListener
            = new Preference.OnPreferenceChangeListener() {
        @Override
        public boolean onPreferenceChange(Preference preference, Object value) {
            String stringValue = value.toString();

            if (preference instanceof ListPreference) {
                // For list preferences, look up the correct display value in
                // the preference's 'entries' list.
                ListPreference listPreference = (ListPreference) preference;
                int index = listPreference.findIndexOfValue(stringValue);
                // Set the summary to reflect the new value.
                preference.setSummary(
                        index >= 0
                                ? listPreference.getEntries()[index]
                                : null);

            } else if (preference instanceof CheckBoxPreference) {
                switch (preference.getKey()) {
                    case "favouritesDisplayPreferenceKey":
                        if ((boolean) value) {
                            ((CheckBoxPreference) preference).setSummary(
                                    preference.getContext().getString(R.string.checkBox_preference_displayFavourites_summary)
                            );
                        } else {
                            ((CheckBoxPreference) preference).setSummary(
                                    preference.getContext().getString(R.string.checkBox_preference_displayAll_summary)
                            );
                        }
                        break;
                }
            }
            return true;
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        android.support.v7.app.ActionBar actionBar = getSupportActionBar();

        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(false);
            actionBar.setHomeButtonEnabled(false);
        }

        getFragmentManager().beginTransaction()
                .replace(android.R.id.content, new SettingsFragment())
                .commit();
    }

    private static void bindPreferenceSummaryToValue(Preference preference) {
        // Set the listener to watch for value changes.
        preference.setOnPreferenceChangeListener(sBindPreferenceSummaryToValueListener);

        // Trigger the listener immediately with the preference's
        // current value.
        if (preference instanceof ListPreference) {
            switch (preference.getKey()) {
                case "sort_preference":
                    sBindPreferenceSummaryToValueListener.onPreferenceChange(preference,
                            PreferenceManager
                                    .getDefaultSharedPreferences(preference.getContext())
                                    .getString(preference.getKey(), ""));
                    break;
                case "userPreferredLanguageKey":
                    MovieBuzzApplication.languagePreferenceChanged = true;
                    sBindPreferenceSummaryToValueListener.onPreferenceChange(preference,
                            PreferenceManager
                                    .getDefaultSharedPreferences(preference.getContext())
                                    .getString(preference.getKey(), "en"));
                    deleteMovieData();

                    break;
            }
        } else if (preference instanceof CheckBoxPreference) {
            switch (preference.getKey()) {
                case "imageStoragePreferenceKey":
                    sBindPreferenceSummaryToValueListener.onPreferenceChange(preference,
                            PreferenceManager
                                    .getDefaultSharedPreferences(preference.getContext())
                                    .getBoolean(preference.getKey(), true));
                case "favouritesDisplayPreferenceKey":
                    sBindPreferenceSummaryToValueListener.onPreferenceChange(preference,
                            PreferenceManager
                                    .getDefaultSharedPreferences(preference.getContext())
                                    .getBoolean(preference.getKey(), false));
                    break;
            }
        }
    }

    static void deleteMovieData() {
        MovieDataDBHelper mMovieDataDBHelper;
        SQLiteDatabase db;
        mMovieDataDBHelper = new MovieDataDBHelper(MovieBuzzApplication.getAppContext());
        db = mMovieDataDBHelper.getWritableDatabase();
        if (!db.isOpen()) {
            db = mMovieDataDBHelper.getWritableDatabase();
        }
        db.delete(MovieContract.MovieData.TABLE_MOVIE_DATA, null
                , null);
        db.delete(MovieContract.MovieReviews.TABLE_MOVIE_REVIEWS, null
                , null);
        db.delete(MovieContract.MovieTrailers.TABLE_MOVIE_TRAILERS, null
                , null);
        db.delete(MovieContract.CastData.TABLE_CAST_DATA, null
                , null);
        db.delete(MovieContract.CrewData.TABLE_CREW_DATA, null
                , null);
    }

    public static class SettingsFragment extends PreferenceFragment {

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.preferences);

//            bindPreferenceSummaryToValue(findPreference(getString(R.string.sort_options_list_key)));
//            bindPreferenceSummaryToValue(findPreference(getString(R.string.favourites_checkbox_preference_key)));
//            bindPreferenceSummaryToValue(findPreference(getString(R.string.poster_storage_checkbox_preference_key)));
            bindPreferenceSummaryToValue(findPreference(getString(R.string.preferences_user_language_key)));
        }

        @Override
        public void onPause() {
            super.onPause();
        }
    }

    @Override
    public void onBackPressed() {
        if (MovieBuzzApplication.languagePreferenceChanged) {
            startActivity(new Intent(SettingsActivity.this, LoginActivity.class));
            finish();
        } else {
            super.onBackPressed();
        }

    }
}
