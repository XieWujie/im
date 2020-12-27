package com.rtp.voice

import android.content.Context
import org.webrtc.*


class SessionInitial {

    fun init(context: Context) {
        PeerConnectionFactory.initialize(
            PeerConnectionFactory.InitializationOptions.builder(context.applicationContext)
                .createInitializationOptions()
        )

        val options = PeerConnectionFactory.Options()
        val mPeerConnectionFactory =
            PeerConnectionFactory.builder().setOptions(options).createPeerConnectionFactory()

        //视频源
        val mVideoCapturer: CameraVideoCapturer = createVideoCapture(context)
        val videoSource: VideoSource = mPeerConnectionFactory.createVideoSource(mVideoCapturer)

        val mVideoTrack: VideoTrack =
            mPeerConnectionFactory.createVideoTrack("videtrack", videoSource)


        //音频
        val audioConstraints = MediaConstraints()
        audioConstraints.mandatory.apply {
            add(MediaConstraints.KeyValuePair("googEchoCancellation", "true"))
            add(MediaConstraints.KeyValuePair("googAutoGainControl", "true"))
            add(MediaConstraints.KeyValuePair("googHighpassFilter", "true"))
            add(MediaConstraints.KeyValuePair("googNoiseSuppression", "true"))
        }

        val audioSource: AudioSource = mPeerConnectionFactory.createAudioSource(audioConstraints)
        val mAudioTrack: AudioTrack =
            mPeerConnectionFactory.createAudioTrack("audiotrack", audioSource)


        val mMediaStream = mPeerConnectionFactory.createLocalMediaStream("localstream")
        mMediaStream.addTrack(mVideoTrack)
        mMediaStream.addTrack(mAudioTrack)

        val peerConnection: PeerConnection =
            mPeerConnectionFactory.createPeerConnection(iceServers, pcConstraints, this)
        peerConnection.addStream(mMediaStream)

    }

    private fun createVideoCapture(context: Context): CameraVideoCapturer {

    }

}

