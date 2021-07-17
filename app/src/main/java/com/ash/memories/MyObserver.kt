package com.ash.memories

import android.database.ContentObserver
import android.provider.MediaStore
import android.content.ContentValues
import android.content.Context
import android.net.Uri
import android.os.Environment
import android.util.Log
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by ash on 05/06/15.
 */
class MyObserver (private val myContext: Context) : ContentObserver(null) {
    private var mediaStorageDir: File? = null
    private var inputFilename: File? = null
    private var outputFilename: File? = null

    override fun onChange(selfChange: Boolean, uri: Uri) {
        super.onChange(selfChange, uri)
    }

    override fun onChange(selfChange: Boolean) {
        super.onChange(selfChange)

        //create output directory if it doesn't exist
        mediaStorageDir = File(
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
            "Memories"
        )
        if (!mediaStorageDir!!.exists()) {
            mediaStorageDir!!.mkdirs()
            Log.d("MemoriesApp", "Memories directory created")
        }
        inputFilename = getInputFilename(myContext, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        outputFilename = retrieveOutputFilename()
        if (!inputFilename!!.path.lowercase().contains("memories")) {

            // Move File
            Log.d("MemoriesApp", "Media Taken")
            inputFilename!!.renameTo(outputFilename)

            //Update MediaStore reference to new file location
            val rows = updateMediaStoreRecord(inputFilename!!.path, outputFilename!!.path)
            if (rows == 1) {
                Log.d("MemoriesApp", "Media Store updated. $rows updated")
            } else {
                Log.d("MemoriesApp", "Media Store update failed. $rows updated")
            }
        }
    }

    private fun retrieveOutputFilename(): File {
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        return File(mediaStorageDir!!.path + File.separator + "Mem_" + timeStamp + ".jpg")
    }

    private fun getInputFilename(context: Context, uri: Uri): File {
        //Find last added file to MediaStore following picture being taken
        var dataColumn = 0
        val cursor = context.contentResolver.query(uri, null, null, null, "date_added DESC")
        if (cursor.moveToNext()) {
            dataColumn = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA)
        }
        Log.d("MemoriesApp", "Filename found")
        return File(cursor.getString(dataColumn))
    }

    private fun updateMediaStoreRecord(oldPath: String, newPath: String): Int {
        //update MediaStore location with new filepath
        val values = ContentValues()
        values.put(MediaStore.MediaColumns.DATA, newPath)
        return myContext.contentResolver.update(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            values,
            MediaStore.MediaColumns.DATA + "='" + oldPath + "'",
            null
        )
    }
}