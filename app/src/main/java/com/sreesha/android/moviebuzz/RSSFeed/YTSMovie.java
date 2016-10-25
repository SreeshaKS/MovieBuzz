package com.sreesha.android.moviebuzz.RSSFeed;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONArray;

/**
 * Created by Sreesha on 18-10-2016.
 */

public class YTSMovie implements Parcelable{
    private long id = 0;
    private String url = "defualtURL";
    private String imdb_code = "defultCode";
    private String title = "defaultTitle";
    private String title_english = "defaultTitle";
    private String title_long = "defaultTitle";
    private String slug = "defaultSlug";
    private int year = 0;
    private float rating = 0;
    private int runtime = 0;
    private String genreJSONArrayString = "defaultArray";
    private String summary = "defaultSummary";
    private String description_full = "fullDescription";
    private String synopsis = "default synopsis";
    private String yt_trailer_code = "defaultCode";
    private int page_number = 0;

    private YTSMovie(Parcel in) {
        id = in.readLong();
        url = in.readString();
        imdb_code = in.readString();
        title = in.readString();
        title_english = in.readString();
        title_long = in.readString();
        slug = in.readString();
        year = in.readInt();
        rating = in.readFloat();
        runtime = in.readInt();
        genreJSONArrayString = in.readString();
        summary = in.readString();
        description_full = in.readString();
        synopsis = in.readString();
        yt_trailer_code = in.readString();
        page_number = in.readInt();
        language = in.readString();
        mpa_rating = in.readString();
        background_image = in.readString();
        background_image_original = in.readString();
        small_cover_image = in.readString();
        medium_cover_image = in.readString();
        large_cover_image = in.readString();
        torrentArray = in.createTypedArray(YTSTorrent.CREATOR);
        date_uploaded = in.readString();
    }

    public static final Creator<YTSMovie> CREATOR = new Creator<YTSMovie>() {
        @Override
        public YTSMovie createFromParcel(Parcel in) {
            return new YTSMovie(in);
        }

        @Override
        public YTSMovie[] newArray(int size) {
            return new YTSMovie[size];
        }
    };

    public YTSMovie(long id, String url, String imdb_code
            , String title, String title_english, String title_long
            , String slug, int year, float rating
            , int runtime, String genreJSONArrayString, String summary
            , String description_full, String synopsis, String yt_trailer_code
            , int page_number, String language, String mpa_rating
            , String background_image, String background_image_original, String small_cover_image
            , String medium_cover_image, String large_cover_image, YTSTorrent[] torrentArray
            , String date_uploaded) {
        this.id = id;
        this.url = url;
        this.imdb_code = imdb_code;
        this.title = title;
        this.title_english = title_english;
        this.title_long = title_long;
        this.slug = slug;
        this.year = year;
        this.rating = rating;
        this.runtime = runtime;
        this.genreJSONArrayString = genreJSONArrayString;
        this.summary = summary;
        this.description_full = description_full;
        this.synopsis = synopsis;
        this.yt_trailer_code = yt_trailer_code;
        this.page_number = page_number;
        this.language = language;
        this.mpa_rating = mpa_rating;
        this.background_image = background_image;
        this.background_image_original = background_image_original;
        this.small_cover_image = small_cover_image;
        this.medium_cover_image = medium_cover_image;
        this.large_cover_image = large_cover_image;
        this.torrentArray = torrentArray;
        this.date_uploaded = date_uploaded;
    }
    public void setTorrentArray(YTSTorrent[] torrentArray) {
        this.torrentArray = torrentArray;
    }
    public int getPageNumber() {
        return page_number;
    }

    public String getLanguage() {
        return language;
    }

    public long getId() {
        return id;
    }

    public String getUrl() {
        return url;
    }

    public String getImdb_code() {
        return imdb_code;
    }

    public String getTitle() {
        return title;
    }

    public String getTitle_english() {
        return title_english;
    }

    public String getTitle_long() {
        return title_long;
    }

    public String getSlug() {
        return slug;
    }

    public int getYear() {
        return year;
    }

    public float getRating() {
        return rating;
    }

    public int getRuntime() {
        return runtime;
    }

    public String getGenreJSONArrayString() {
        return genreJSONArrayString;
    }

    public String getSummary() {
        return summary;
    }

    public String getDescription_full() {
        return description_full;
    }

    public String getSynopsis() {
        return synopsis;
    }

    public String getYt_trailer_code() {
        return yt_trailer_code;
    }

    public String getMpa_rating() {
        return mpa_rating;
    }

    public String getBackground_image() {
        return background_image;
    }

    public String getBackground_image_original() {
        return background_image_original;
    }

    public String getSmall_cover_image() {
        return small_cover_image;
    }

    public String getMedium_cover_image() {
        return medium_cover_image;
    }

    public String getLarge_cover_image() {
        return large_cover_image;
    }

    public YTSTorrent[] getTorrentArray() {
        return torrentArray;
    }

    public String getDate_uploaded() {
        return date_uploaded;
    }

    String language = "defaultLanguage";
    String mpa_rating = "defaultRating";
    String background_image = "";
    String background_image_original = "";
    String small_cover_image = "";
    String medium_cover_image = "";
    String large_cover_image = "";
    YTSTorrent[] torrentArray;
    String date_uploaded = "";

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(id);
        dest.writeString(url);
        dest.writeString(imdb_code);
        dest.writeString(title);
        dest.writeString(title_english);
        dest.writeString(title_long);
        dest.writeString(slug);
        dest.writeInt(year);
        dest.writeFloat(rating);
        dest.writeInt(runtime);
        dest.writeString(genreJSONArrayString);
        dest.writeString(summary);
        dest.writeString(description_full);
        dest.writeString(synopsis);
        dest.writeString(yt_trailer_code);
        dest.writeInt(page_number);
        dest.writeString(language);
        dest.writeString(mpa_rating);
        dest.writeString(background_image);
        dest.writeString(background_image_original);
        dest.writeString(small_cover_image);
        dest.writeString(medium_cover_image);
        dest.writeString(large_cover_image);
        dest.writeTypedArray(torrentArray, flags);
        dest.writeString(date_uploaded);
    }
}
