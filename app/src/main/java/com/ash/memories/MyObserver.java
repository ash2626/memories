package com.ash.memories;

import android.content.ContentValues;
import android.content.Context;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by ash on 05/06/15.
 */
public class MyObserver extends ContentObserver {

    private Context myContext;
    File mediaStorageDir;
    File inputFilename;
    File outputFilename;

    /**
     * Creates a content observer.
     *
     */

    public MyObserver(Context myCon) {
        super(null);

        this.myContext = myCon;
    }

    @Override
    public void onChange(boolean selfChange, Uri uri) {
        super.onChange(selfChange, uri);
    }

    @Override
    public void onChange(boolean selfChange) {
        super.onChange(selfChange);

        //create output directory if it doesn't exist
        mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "Memories");

        if (!mediaStorageDir.exists())
        {
            mediaStorageDir.mkdirs();
            Log.d("MemoriesApp", "Memories directory created");
        }

        inputFilename = getInputFilename(myContext,MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        outputFilename = getOutputFilename();

        if (!inputFilename.getPath().toLowerCase().contains("memories")) {

            // Move File
            Log.d("MemoriesApp", "Media Taken");
            inputFilename.renameTo(outputFilename);

            //Update MediaStore reference to new file location
            int rows = updateMediaStoreRecord(inputFilename.getPath(), outputFilename.getPath());

            if (rows == 1) {
                Log.d("MemoriesApp", "Media Store updated. " + rows + " updated");
            } else {
                Log.d("MemoriesApp", "Media Store update failed. " + rows + " updated");
            }
        }
    }

    private File getOutputFilename()
    {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        return new File(mediaStorageDir.getPath() + File.separator + "Mem_"+ timeStamp + ".jpg");
    }

    private File getInputFilename(Context context,Uri uri)
    {
        //Find last added file to MediaStore following picture being taken
        int dataColumn = 0;
        Cursor cursor = context.getContentResolver().query(uri, null, null, null, "date_added DESC");
        if (cursor.moveToNext()) {
            dataColumn = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
        }
        Log.d("MemoriesApp", "Filename found");
        return new File(cursor.getString(dataColumn));
    }

    private int updateMediaStoreRecord(String oldPath,String newPath)
    {
        //update MediaStore location with new filepath
        ContentValues values = new ContentValues();
        values.put(MediaStore.MediaColumns.DATA, newPath);
        return myContext.getContentResolver().update(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,values,MediaStore.MediaColumns.DATA + "='" + oldPath + "'", null);
    }
}