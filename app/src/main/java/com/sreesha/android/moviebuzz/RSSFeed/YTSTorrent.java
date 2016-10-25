package com.sreesha.android.moviebuzz.RSSFeed;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Sreesha on 18-10-2016.
 */

public class YTSTorrent implements Parcelable{

    private String url = "defultURL";
    private String hash = "defaultHash";
    private int seeds = 0;
    private int peers = 0;
    private String size = "0MB";
    private long size_bytes = 0;
    private String date_upload = "defaultDate";
    private  long date_uploaded_unix = 0;

    public YTSTorrent(String url, String hash, int seeds
            , int peers, String size, long size_bytes
            , String date_upload, long date_uploaded_unix) {
        this.url = url;
        this.hash = hash;
        this.seeds = seeds;
        this.peers = peers;
        this.size = size;
        this.size_bytes = size_bytes;
        this.date_upload = date_upload;
        this.date_uploaded_unix = date_uploaded_unix;
    }


    public YTSTorrent(Parcel in) {
        url = in.readString();
        hash = in.readString();
        seeds = in.readInt();
        peers = in.readInt();
        size = in.readString();
        size_bytes = in.readLong();
        date_upload = in.readString();
        date_uploaded_unix = in.readLong();
    }

    public static final Creator<YTSTorrent> CREATOR = new Creator<YTSTorrent>() {
        @Override
        public YTSTorrent createFromParcel(Parcel in) {
            return new YTSTorrent(in);
        }

        @Override
        public YTSTorrent[] newArray(int size) {
            return new YTSTorrent[size];
        }
    };

    public long getSize_bytes() {
        return size_bytes;
    }

    public String getUrl() {
        return url;
    }

    public String getHash() {
        return hash;
    }

    public int getSeeds() {
        return seeds;
    }

    public int getPeers() {
        return peers;
    }

    public String getSize() {
        return size;
    }

    public String getDate_upload() {
        return date_upload;
    }

    public long getDate_uploaded_unix() {
        return date_uploaded_unix;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(url);
        dest.writeString(hash);
        dest.writeInt(seeds);
        dest.writeInt(peers);
        dest.writeString(size);
        dest.writeLong(size_bytes);
        dest.writeString(date_upload);
        dest.writeLong(date_uploaded_unix);
    }
}
