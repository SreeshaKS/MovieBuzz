package com.sreesha.android.moviebuzz.MovieDataRenderingClasses.MovieDetailTabsView;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Sreesha on 11-03-2016.
 */
public class ParcelableBitmap implements Parcelable {
    public Bitmap getBitmap() {
        return mBitmap;
    }

    private Bitmap mBitmap;

    public ParcelableBitmap(Bitmap backDropBitmap) {
        this.mBitmap = backDropBitmap;
    }

    protected ParcelableBitmap(Parcel in) {
        mBitmap = in.readParcelable(Bitmap.class.getClassLoader());
    }

    public static final Creator<ParcelableBitmap> CREATOR = new Creator<ParcelableBitmap>() {
        @Override
        public ParcelableBitmap createFromParcel(Parcel in) {
            return new ParcelableBitmap(in);
        }

        @Override
        public ParcelableBitmap[] newArray(int size) {
            return new ParcelableBitmap[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(mBitmap, flags);
    }
}
