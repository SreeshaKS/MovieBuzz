package com.sreesha.android.moviebuzz.MovieDataRenderingClasses.DetailView;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;
import android.widget.ImageView;

import com.sreesha.android.moviebuzz.Networking.MovieDataInstance;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by Sreesha on 21-03-2016.
 */
public class AsyncMediaStorageClass extends AsyncTask<Void, Void, Void> {

    public static final String BITMAP_STORAGE_TASK = "bitmapStorageTask";
    public static final String DOWNLOAD_BITMAP_STORE_TASK = "downloadAndStoreBitmapTask";
    public static final String SET_IMAGE_VIEW_BITMAP_TASK = "setImageViewWithBitmapRetrievalTask";

    public static final String RESULT_SUCCESS = "Success";
    public static final String TYPE_POSTER = "moviePoster";
    public static final String TYPE_BACKDROP = "movieBackDrop";
    public static final String ID_POSTER_TYPE_SEPARATOR = "^";

    private OnBitmapRenderedListener mOnBitmapRenderedListener;
    private OnImageStoredListener mOnImageStoredListener;
    private String mPosterPath;
    private static final String MEDIA_STORAGE_DIRECTORY = "PopularMovies";
    private static final String POSTER_STORAGE_DIRECTORY_NAME = "MoviePosters";
    private static final String MEDIA_STORAGE_DIRECTORY_PATH = Environment
            .getExternalStorageDirectory()
            .getAbsolutePath() + File.separator + MEDIA_STORAGE_DIRECTORY;
    private static final String POSTERS_STORAGE_DIRECTORY_PATH = Environment
            .getExternalStorageDirectory()
            .getAbsolutePath()
            + File.separator
            + MEDIA_STORAGE_DIRECTORY
            + File.separator
            + POSTER_STORAGE_DIRECTORY_NAME;
    private String mFileName;
    private String mCurrentTaskType;
    private Bitmap mRetrievedBitmap;
    private String mErrorString = null;
    private Bitmap mPosterBitmap;
    private ImageView mImageView;
    MovieDataInstance instance;
    public AsyncMediaStorageClass(String FILE_NAME, String TASK_TYPE, Bitmap POSTER_BITMAP, ImageView imageView) {
        mFileName = FILE_NAME;
        mCurrentTaskType = TASK_TYPE;
        mPosterBitmap = POSTER_BITMAP;
    }
    public AsyncMediaStorageClass(MovieDataInstance instance, String TASK_TYPE) {
        this.instance = instance;
        mCurrentTaskType = TASK_TYPE;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected Void doInBackground(Void... params) {
        if (isMediaDirectoryPresent()) {
            Log.e("StorageDebug", "Directory Already Created");
            if (isPosterDirectoryPresent()) {
                executeTask();
            } else {
                File posterFile = new File(POSTERS_STORAGE_DIRECTORY_PATH);
                if (!posterFile.mkdir()) {
                    mErrorString = "PosterDIRCreationError";
                } else {
                    executeTask();
                }
            }
        } else {
            Log.e("StorageDebug", "Creating Directory");
            File mediaDirectory = new File(MEDIA_STORAGE_DIRECTORY_PATH);
            if (!mediaDirectory.mkdir()) {
                Log.e("StorageDebug", "Error in Creating Directory");
                mErrorString = "MediaDIRCreationError";
            } else {
                Log.e("StorageDebug", "Directory Created");
                File posterFile = new File(POSTERS_STORAGE_DIRECTORY_PATH);
                if (!posterFile.mkdir()) {
                    Log.e("StorageDebug", "Error in Creating Poster Directory");
                    mErrorString = "PosterDIRCreationError";
                } else {
                    Log.e("StorageDebug", "Poster Directory Created");
                    executeTask();
                }
            }
        }
        return null;
    }

    private void executeTask() {
        switch (mCurrentTaskType) {
            case BITMAP_STORAGE_TASK: {
                Log.e("StorageDebug", "BitmapStorageExecuting");
                mPosterPath = POSTERS_STORAGE_DIRECTORY_PATH
                        + File.separator + mFileName + ".jpeg";
                if (!new File(mPosterPath).exists()) {
                    if (storeBitMapToSDCard()) {
                        onProgressUpdate();
                    } else {
                        Log.e("StorageDebug", "BitmapStorageExecuting Error");
                        mErrorString = "UnknownError";
                        onProgressUpdate();
                    }
                }
            }
            break;
            case SET_IMAGE_VIEW_BITMAP_TASK: {
                Log.e("StorageDebug", "Image View Bitmap Executing");
                if (retrieveBitmapFromSDCard()) {
                    mErrorString = RESULT_SUCCESS;
                    onProgressUpdate();
                } else {
                    Log.e("StorageDebug", "Image View Bitmap Executing Error");
                    mErrorString = "BitMapDoesNotExist";
                    onProgressUpdate();
                }
            }
            case DOWNLOAD_BITMAP_STORE_TASK:{

            }
            break;
        }
    }

    private boolean retrieveBitmapFromSDCard() {
        try {
            mRetrievedBitmap = BitmapFactory.decodeFile(
                    POSTERS_STORAGE_DIRECTORY_PATH + File.separator + mFileName + ".jpeg"
            );
            mErrorString = RESULT_SUCCESS;
        } catch (OutOfMemoryError e) {
            mErrorString = e.getMessage();
        }
        return mRetrievedBitmap != null && mErrorString.equals(RESULT_SUCCESS);
    }

    private boolean storeBitMapToSDCard() {
        FileOutputStream outStream = null;
        try {
            File poster = new File(mPosterPath);

            Log.e("StorageDebug", "ImageFile Path : " + poster.getAbsolutePath());
            outStream = new FileOutputStream(poster);
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            if (mPosterBitmap != null) {
                mPosterBitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
                poster.createNewFile();
                outStream.write(byteArrayOutputStream.toByteArray());
                mErrorString = RESULT_SUCCESS;
                Log.e("StorageDebug", "Image Created");
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            mErrorString = e.getMessage();
        } catch (IOException e) {
            e.printStackTrace();
            mErrorString = e.getMessage();
        } finally {
            if (outStream != null) {
                try {
                    outStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                    mErrorString = e.getMessage();
                }
            }
        }
        return mErrorString.equals(RESULT_SUCCESS);
    }

    @Override
    protected void onProgressUpdate(Void... values) {
        super.onProgressUpdate(values);
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        switch (mCurrentTaskType) {
            case BITMAP_STORAGE_TASK: {
                if (mOnImageStoredListener != null) {
                    mOnImageStoredListener.onImageStored(mErrorString, mPosterPath);
                }
            }
            break;
            case SET_IMAGE_VIEW_BITMAP_TASK: {
                if (mOnBitmapRenderedListener != null) {
                    mOnBitmapRenderedListener.onBitmapRendered(mErrorString, mRetrievedBitmap);
                }
            }
            break;
        }
        this.cancel(true);
        super.onPostExecute(aVoid);
    }

    public boolean isMediaDirectoryPresent() {
        return new File(MEDIA_STORAGE_DIRECTORY_PATH).exists();
    }

    public boolean isPosterDirectoryPresent() {
        return new File(POSTERS_STORAGE_DIRECTORY_PATH).exists();
    }

    public static String getFormattedPosterFileName(long movieID, String posterType) {
        return String.valueOf(movieID) + ID_POSTER_TYPE_SEPARATOR + posterType;
    }

    public AsyncMediaStorageClass setOnBitmapRenderedListener(OnBitmapRenderedListener listener) {
        mOnBitmapRenderedListener = listener;
        return this;
    }

    public AsyncMediaStorageClass setOnImageStoredListener(OnImageStoredListener listener) {
        mOnImageStoredListener = listener;
        return this;
    }

    public interface OnBitmapRenderedListener {
        void onBitmapRendered(String errorMessage, Bitmap bitmap);
    }

    public interface OnImageStoredListener {
        void onImageStored(String errorMessage, String imagePath);
    }

    public static String getPosterFileAbsolutePath(String fileName) {
        return POSTERS_STORAGE_DIRECTORY_PATH
                +File.separator
                +fileName;
    }
}
