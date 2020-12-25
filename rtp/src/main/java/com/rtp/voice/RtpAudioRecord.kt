package com.rtp.voice

import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder

class RtpAudioRecord {

    private val audioRecord:AudioRecord

    init {
        val bufSize = AudioRecord.getMinBufferSize(8000,
            AudioFormat.CHANNEL_IN_MONO,AudioFormat.ENCODING_PCM_16BIT)
        audioRecord = AudioRecord(MediaRecorder.AudioSource.MIC,
        8000,
        AudioFormat.CHANNEL_IN_MONO,
        AudioFormat.ENCODING_PCM_16BIT,
        bufSize)
    }

    fun read(byteArray: ByteArray){
        audioRecord.read(byteArray,0,byteArray.size)
    }
}