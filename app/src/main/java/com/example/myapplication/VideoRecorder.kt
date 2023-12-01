package com.example.myapplication

import android.content.ContentValues
import android.content.Context
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.video.*
import androidx.core.content.ContextCompat
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.ExecutionException
import androidx.camera.view.PreviewView
import androidx.lifecycle.LifecycleOwner

class VideoRecorder(private val context: Context, private val cameraFloatingWindow: PreviewView) {
    private var recording: Recording? = null
    private var vC: VideoCapture<Recorder>? = null
    private var cameraFacing = CameraSelector.LENS_FACING_BACK
    private var uri: Uri? = null

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    fun startVideoCapturing() {
        val rec = recording
        if (rec != null) {
            rec.stop()
            recording = null
            return
        }
        val name = SimpleDateFormat(
            "yyyy-MM-dd-HH-mm-ss-SSS",
            Locale.getDefault()
        ).format(System.currentTimeMillis())
        val cV = ContentValues()
        cV.put(MediaStore.MediaColumns.DISPLAY_NAME, name)
        cV.put(MediaStore.MediaColumns.MIME_TYPE, "video/mp4")
        cV.put(MediaStore.Video.Media.RELATIVE_PATH, "Movies/CameraX-Video")

        val opt = MediaStoreOutputOptions.Builder(
            context.contentResolver,
            MediaStore.Video.Media.EXTERNAL_CONTENT_URI
        ).setContentValues(cV).build()

        val pCP = ProcessCameraProvider.getInstance(context)

        var cP: ProcessCameraProvider? = null
        cP = try {
            pCP.get()
        } catch (e: ExecutionException) {
            throw RuntimeException(e)
        } catch (e: InterruptedException) {
            throw RuntimeException(e)
        }
        val pre = Preview.Builder().build()
        pre.setSurfaceProvider(cameraFloatingWindow.surfaceProvider)

        cP?.unbindAll()

        var cS: CameraSelector? = null
        cS = CameraSelector.Builder()
            .requireLensFacing(cameraFacing).build()
        val cam = cP?.bindToLifecycle(context as LifecycleOwner, cS, pre, vC)
        if (cam != null) {
            if (cam.cameraInfo.hasFlashUnit()) {
                if (cam.cameraInfo.torchState.value == 0) {
                    cam.cameraControl.enableTorch(true)
                }
            }
        }
        recording = vC!!.output.prepareRecording(context, opt).start(
            ContextCompat.getMainExecutor(context)
        ) { videoRecordEvent: VideoRecordEvent ->
            if (videoRecordEvent is VideoRecordEvent.Start) {
                Log.i("Video Recording started", videoRecordEvent.toString())
            } else if (videoRecordEvent is VideoRecordEvent.Finalize) {
                if (!(videoRecordEvent as VideoRecordEvent.Finalize).hasError()) {
                    uri = (videoRecordEvent as VideoRecordEvent.Finalize).outputResults
                        .outputUri
                    val msg = "Video is Captured and stored in : $uri"
                    if (cam != null) {
                        cam.cameraControl.enableTorch(false)
                    }
                    recording = null
                    vC = null

                    // Once the recording is done, the recorded URI will be passed into an asynchronous task that
                    // performs the video processing to get the Heart Rate. This is part of another component
                    // implemented by Vikas Kamineni.
                    Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                } else {
                    recording!!.close()
                    recording = null
                    val msg =
                        "Error Message: " + (videoRecordEvent as VideoRecordEvent.Finalize).error
                    Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    fun configureCamera() {
        val recorder = Recorder.Builder()
            .setQualitySelector(QualitySelector.from(Quality.HIGHEST))
            .build()
        vC = VideoCapture.withOutput(recorder)
    }
}