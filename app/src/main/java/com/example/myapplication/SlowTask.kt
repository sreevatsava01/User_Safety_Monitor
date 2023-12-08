package com.example.myapplication
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.os.AsyncTask
import android.util.Log
import android.widget.TextView
import java.io.IOException

class SlowTask(
    private val context: Context,
    private val heartBeatRate: TextView, // Assuming you're passing the TextView to update
    private val uri: Uri // Assuming the URI of the video is passed to the task
) : AsyncTask<String, String, String>() {

    override fun onPostExecute(s: String) {
        Log.i("Video Starting Harindra", "$s 1,2,3,4")
        heartBeatRate.text = s
    }

    override fun doInBackground(vararg params: String?): String {
        val frameList = mutableListOf<Bitmap>()
        val retriever = MediaMetadataRetriever()
        var videoDuration = 0
        try {
            retriever.setDataSource(context, uri)
            val duration = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)
            videoDuration = duration?.toInt() ?: 0
            var i = 10
            while (i < videoDuration / 75) {
                retriever.getFrameAtIndex(i)?.let { bitmap ->
                    frameList.add(bitmap)
                }
                i += 5
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            try {
                retriever.release()
            } catch (e: IOException) {
                throw RuntimeException(e)
            }
            var redBucket: Long
            var pixelCount: Long
            val a = mutableListOf<Long>()
            frameList.forEach { frame ->
                redBucket = 0
                pixelCount = 0
                for (y in 550 until 650) {
                    for (x in 550 until 650) {
                        val color = frame.getPixel(x, y)
                        pixelCount++
                        redBucket += Color.red(color) + Color.blue(color) + Color.green(color)
                    }
                }
                a.add(redBucket)
            }
            val b = mutableListOf<Long>()
            for (i in 0 until a.size - 5) {
                val temp = (a[i] + a[i + 1] + a[i + 2] + a[i + 3] + a[i + 4]) / 4
                b.add(temp)
            }
            var x = b[0]
            var count = 0
            for (i in 1 until b.size) {
                val p = b[i]
                if ((p - x) > 1000) {
                    count++
                }
                x = b[i]
            }
            val rate = ((count * 60.0) / (videoDuration / 1000.0) / 2).toInt()
            return rate.toString()
        }
    }
}
