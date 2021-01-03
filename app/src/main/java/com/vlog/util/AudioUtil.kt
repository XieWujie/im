package com.vlog.util

import android.content.Context
import android.media.AudioManager


object AudioUtil {

    private var currVolume = 0

    fun openSpeaker(context: Context) {
        val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        try {
            audioManager.mode = AudioManager.MODE_IN_CALL
            // 获取当前通话音量
           currVolume = audioManager.getStreamVolume(AudioManager.STREAM_VOICE_CALL)
            if (!audioManager.isSpeakerphoneOn) {
                audioManager.isSpeakerphoneOn = true
                audioManager.setStreamVolume(
                    AudioManager.STREAM_VOICE_CALL,
                    audioManager.getStreamMaxVolume(AudioManager.STREAM_VOICE_CALL),
                    AudioManager.STREAM_VOICE_CALL
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun closeSpeaker(context: Context) {
        val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        try {
            if (audioManager.isSpeakerphoneOn) {
                audioManager.isSpeakerphoneOn = false
                audioManager.setStreamVolume(
                    AudioManager.STREAM_VOICE_CALL,
                    currVolume,
                    AudioManager.STREAM_VOICE_CALL
                )
            }
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }
}