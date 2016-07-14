package com.sreesha.android.moviebuzz.MovieDataRenderingClasses.PeopleDisplay;

import com.sreesha.android.moviebuzz.Networking.PopularPeopleInstance;

/**
 * Created by Sreesha on 12-07-2016.
 */
public interface PersonClickListener {
    void onPersonClicked(PopularPeopleInstance instance);

    interface PersonClickNotifier {
        void OnPersonCLickDispatched(PopularPeopleInstance instance);
    }
}
