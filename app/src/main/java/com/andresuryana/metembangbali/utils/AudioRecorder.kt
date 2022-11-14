package com.andresuryana.metembangbali.utils

import android.content.Context
import com.andresuryana.metembangbali.helper.Helpers.generateFilename
import com.github.squti.androidwaverecorder.RecorderState
import com.github.squti.androidwaverecorder.WaveRecorder
import java.io.File

class AudioRecorder(context: Context) {

    private val mediaDir = File(context.filesDir, "Metembang Bali")
    private val filename = generateFilename("AUDIO", "wav")
    private val filePath = mediaDir.absolutePath + filename

    private val waveRecorder = WaveRecorder(filePath)

    fun setOnStateChangeListener(onStateChangeListener: (state: RecorderState) -> Unit) {
        waveRecorder.onStateChangeListener = onStateChangeListener
    }

    fun setOnAmplitudeListener(onAmplitudeListener: (amplitude: Int) -> Unit) {
        waveRecorder.onAmplitudeListener = onAmplitudeListener
    }

    fun startRecording() {
        waveRecorder.startRecording()
    }

    fun stopRecordingForResult(result: (audio: File) -> Unit) {
        waveRecorder.stopRecording()
        result.invoke(File(filePath))
    }
}