package com.andresuryana.metembangbali.helper

import android.app.Activity
import android.content.Intent
import com.github.dhaval2404.imagepicker.ImagePicker
import java.io.File

class IntentHelper(private val activity: Activity) {

    private var cameraIntent: Intent? = null
        set(value) {
            ImagePicker.with(activity)
                .cropSquare()
                .cameraOnly()
                .maxResultSize(400, 400)
                .compress(1024)
                .saveDir(File(activity.filesDir, "Metembang Bali"))
                .createIntent {
                    field = it
                }
        }

    private var galleryIntent: Intent? = null
        set(value) {
            ImagePicker.with(activity)
                .cropSquare()
                .galleryOnly()
                .galleryMimeTypes(arrayOf("image/jpg", "image/jpeg", "image/png"))
                .maxResultSize(400, 400)
                .compress(1024)
                .saveDir(File(activity.filesDir, "Metembang Bali"))
                .createIntent {
                    field = it
                }
        }

    val chooserImage: Intent = Intent.createChooser(cameraIntent, "Test").also {
        it.putExtra(Intent.EXTRA_INITIAL_INTENTS, arrayOf(galleryIntent))
    }
}