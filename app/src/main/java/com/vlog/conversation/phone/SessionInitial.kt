package com.vlog.conversation.phone

import android.content.Context
import android.util.Log
import com.dibus.DiBus
import com.google.gson.Gson
import com.vlog.connect.MessageSend
import com.vlog.database.Message
import org.webrtc.*
import org.webrtc.PeerConnection.IceServer
import org.webrtc.PeerConnection.RTCConfiguration
import org.webrtc.PeerConnectionFactory.InitializationOptions


private const val TAG = "SessionInitial"

class SessionInitial(private val fromType:Int,private val conversationId:Int,private val  context: Context) {

    private lateinit var remoteSurfaceView: SurfaceViewRenderer
    private lateinit var eglBase: EglBase
    private lateinit var peerConnectionFactory: PeerConnectionFactory

    private  var channel: DataChannel? = null
    private lateinit var videoTrack: VideoTrack
    private lateinit var audioTrack: AudioTrack
    private lateinit var peerConnection: PeerConnection
    private lateinit var streamList: List<String>
    private lateinit var observer: MySdpObserver

    private var videoStream:MediaStream? = null
    private var audioStream:MediaStream? = null
    private var mCameraVideoCapturer:CameraVideoCapturer? = null

    private lateinit var iceServers: ArrayList<IceServer>

    @Volatile
    private  var noAudio = false


     fun createPeerConnection() {
        //Initialising PeerConnectionFactory
        val initializationOptions = InitializationOptions.builder(context.applicationContext)
            .setEnableInternalTracer(true)
            .setFieldTrials("WebRTC-H264HighProfile/Enabled/")
            .createInitializationOptions()
        PeerConnectionFactory.initialize(initializationOptions)
        //创建EglBase对象
        eglBase = EglBase.create()
        val options = PeerConnectionFactory.Options()
        options.disableEncryption = true
        options.disableNetworkMonitor = true
        peerConnectionFactory = PeerConnectionFactory.builder()
            .setVideoDecoderFactory(DefaultVideoDecoderFactory(eglBase.eglBaseContext))
            .setVideoEncoderFactory(
                DefaultVideoEncoderFactory(
                    eglBase.eglBaseContext,
                    true,
                    true
                )
            )
            .setOptions(options)
            .createPeerConnectionFactory()
        // 配置STUN穿透服务器  转发服务器
        iceServers = ArrayList()
        val iceServer: IceServer = IceServer.builder(PhoneConstant.STUN).createIceServer()
        iceServers.add(iceServer)
        streamList = ArrayList()
        val configuration = RTCConfiguration(iceServers)
        val connectionObserver: PeerConnectionObserver = getObserver()
        peerConnection =
            peerConnectionFactory.
            createPeerConnection(configuration, connectionObserver)?:throw RuntimeException("can not create peerConnection")


        /*
        DataChannel.Init 可配参数说明：
        ordered：是否保证顺序传输；
        maxRetransmitTimeMs：重传允许的最长时间；
        maxRetransmits：重传允许的最大次数；
        */
        val init = DataChannel.Init()
        channel = peerConnection.createDataChannel(PhoneConstant.CHANNEL, init)
        val channelObserver = DateChannelObserver()
        connectionObserver.setObserver(channelObserver)
        initObserver()
    }



    fun setDes(description: SessionDescription){
        peerConnection.setRemoteDescription(observer,description)
        createAnswer()
    }


    private fun initObserver() {
        observer = object : MySdpObserver() {
            override fun onCreateSuccess(sessionDescription: SessionDescription?) {
                //将会话描述设置在本地
                sessionDescription ?: throw RuntimeException("sessionDescription == null")
                peerConnection.setLocalDescription(this, sessionDescription)
                val localDescription = peerConnection.localDescription
                val type = localDescription.type
                Log.e(TAG, "onCreateSuccess ==  type == $type")
                when (type) {
                    SessionDescription.Type.OFFER -> offer(sessionDescription)
                    SessionDescription.Type.ANSWER -> answer(sessionDescription)
                    SessionDescription.Type.PRANSWER -> {
                    }
                }
            }
        }
    }


    private fun getObserver(): PeerConnectionObserver {
        return object : PeerConnectionObserver() {
            override fun onIceCandidate(iceCandidate: IceCandidate?) {
                super.onIceCandidate(iceCandidate)
                Log.d(TAG,"ice")
                if (iceCandidate != null) {
                    setIceCandidate(iceCandidate)
                }
            }

            override fun onAddStream(mediaStream: MediaStream) {
                super.onAddStream(mediaStream)
                Log.d(TAG, "onAddStream : $mediaStream")
                val videoTracks = mediaStream.videoTracks
                if (videoTracks != null && videoTracks.size > 0) {
                    val videoTrack = videoTracks[0]
                    videoTrack?.addSink(remoteSurfaceView)
                }
                val audioTracks = mediaStream.audioTracks
                if (audioTracks != null && audioTracks.size > 0) {
                    val audioTrack = audioTracks[0]
                    audioTrack?.setVolume(PhoneConstant.VOLUME.toDouble())
                    audioTrack?.setEnabled(noAudio)
                }
            }
        }
    }

    fun addIce(iceCandidate: IceCandidate){
        peerConnection.addIceCandidate(iceCandidate)
    }

    private fun createVideoCapturer(): VideoCapturer? {
        return if (Camera2Enumerator.isSupported(context)) {
            createCameraCapturer(Camera2Enumerator(context))
        } else {
            createCameraCapturer(Camera1Enumerator(true))
        }
    }

    private fun createCameraCapturer(enumerator: CameraEnumerator): VideoCapturer? {
        val deviceNames = enumerator.deviceNames


        for (deviceName in deviceNames) {
            if (enumerator.isFrontFacing(deviceName)) {

                mCameraVideoCapturer = enumerator.createCapturer(deviceName, null)
                if (mCameraVideoCapturer != null) {

                    return mCameraVideoCapturer
                }
            }
        }

        for (deviceName in deviceNames) {
            if (!enumerator.isFrontFacing(deviceName)) {

                val videoCapturer: VideoCapturer? = enumerator.createCapturer(deviceName, null)
                if (videoCapturer != null) {
                    return videoCapturer
                }
            }
        }
        return null
    }

    fun setIsVoice(voice: Boolean){
        noAudio = voice
    }

    fun attachView(local: SurfaceViewRenderer,remote:SurfaceViewRenderer){
        this.remoteSurfaceView = remote
        initSurfaceView(local)
        initSurfaceView(remote)
        startLocalAudioCapture()
        startLocalVideoCapture(local,createVideoCapturer()?:throw RuntimeException("找不到摄像头"))
    }

    private fun startLocalVideoCapture(localSurfaceView: SurfaceViewRenderer,videoCapturer: VideoCapturer) {
        val videoSource = peerConnectionFactory.createVideoSource(true)
        val surfaceTextureHelper = SurfaceTextureHelper.create(
            Thread.currentThread().name,
            eglBase.eglBaseContext
        )
        videoCapturer.initialize(surfaceTextureHelper, context, videoSource.capturerObserver)
        videoCapturer.startCapture(
            PhoneConstant.VIDEO_RESOLUTION_WIDTH,
            PhoneConstant.VIDEO_RESOLUTION_HEIGHT,
            PhoneConstant.VIDEO_FPS
        ) // width, height, frame per second
        videoTrack = peerConnectionFactory.createVideoTrack(
            PhoneConstant.VIDEO_TRACK_ID,
            videoSource
        )
        videoTrack.addSink(localSurfaceView)
        val localMediaStream =
            peerConnectionFactory.createLocalMediaStream(PhoneConstant.LOCAL_VIDEO_STREAM)
        localMediaStream.addTrack(videoTrack)
        peerConnection.addTrack(videoTrack, streamList)
        peerConnection.addStream(localMediaStream)
        videoStream = localMediaStream
    }

    private fun startLocalAudioCapture() {
        //语音
        val audioConstraints = MediaConstraints()
        //回声消除
        audioConstraints.mandatory.add(
            MediaConstraints.KeyValuePair(
                "googEchoCancellation",
                "true"
            )
        )
        //自动增益
        audioConstraints.mandatory.add(MediaConstraints.KeyValuePair("googAutoGainControl", "true"))
        //高音过滤
        audioConstraints.mandatory.add(MediaConstraints.KeyValuePair("googHighpassFilter", "true"))
        //噪音处理
        audioConstraints.mandatory.add(MediaConstraints.KeyValuePair("googNoiseSuppression", "true"))
        val audioSource = peerConnectionFactory.createAudioSource(audioConstraints)
        audioTrack = peerConnectionFactory.createAudioTrack(
            PhoneConstant.AUDIO_TRACK_ID,
            audioSource
        )
        val localMediaStream =
            peerConnectionFactory.createLocalMediaStream(PhoneConstant.LOCAL_AUDIO_STREAM)
        localMediaStream.addTrack(audioTrack)
        audioTrack.setVolume(PhoneConstant.VOLUME.toDouble())
        peerConnection.addTrack(audioTrack, streamList)
        peerConnection.addStream(localMediaStream)
        audioStream = localMediaStream
    }

    private fun initSurfaceView(surfaceView: SurfaceViewRenderer) {
        surfaceView.apply {
            try {
                init(eglBase.eglBaseContext, null)
            }catch (e:Exception){
                e.printStackTrace()
            }
            setMirror(true)
            setScalingType(RendererCommon.ScalingType.SCALE_ASPECT_FILL)
            keepScreenOn = true
            setZOrderMediaOverlay(true)
            setEnableHardwareScaler(false)
        }
    }

    fun createOffer() {
        val mediaConstraints = MediaConstraints()
        mediaConstraints.mandatory.add(MediaConstraints.KeyValuePair("OfferToReceiveVideo", "true"))
        peerConnection.createOffer(observer, mediaConstraints)
    }

     private fun createAnswer() {
        val mediaConstraints = MediaConstraints()
        mediaConstraints.mandatory.add(MediaConstraints.KeyValuePair("OfferToReceiveVideo", "true"))
        peerConnection.createAnswer(observer, mediaConstraints)
    }

    /**
     * 应答
     *
     * @param sdpDescription
     */
    private fun answer(sdpDescription: SessionDescription) {
        val g:Gson = DiBus.load()
        val message = Message.obtain(conversationId,Message.RTC_DES_OFFER,g.toJson(sdpDescription),fromType)
        DiBus.postEvent(message,MessageSend{

        })
    }

    /**
     * 呼叫
     *
     * @param sdpDescription
     */
    private fun offer(sdpDescription: SessionDescription) {
        val g:Gson = DiBus.load()
        val message = Message.obtain(conversationId,Message.RTC_DES_OFFER,g.toJson(sdpDescription),fromType)
        DiBus.postEvent(message,MessageSend{

        })
    }

    /**
     * 呼叫
     *
     * @param iceCandidate
     */
    private fun setIceCandidate(iceCandidate: IceCandidate) {
        val g:Gson = DiBus.load()
        val message = Message.obtain(conversationId,Message.RTC_ICE_OFFER,g.toJson(iceCandidate),fromType)
        DiBus.postEvent(message,MessageSend{

        })
    }

    /**
     * 切换前后摄像头
     */
    fun changeVideoCapturer() {
        val cameraVideoCapturer: CameraVideoCapturer = mCameraVideoCapturer?:return
        cameraVideoCapturer.switchCamera(null)
    }

    /**
     * 关闭通话
     */
    fun closeMediaCapturer() {

        videoStream?.dispose()
        if (mCameraVideoCapturer != null) {
            mCameraVideoCapturer?.dispose()
        }
    }

    /**
     * 视频转语音
     */
    fun setVideoOrVoice(video: Boolean) {
        val mediaStream = videoStream?:return
        if(mediaStream.videoTracks.isEmpty())return
        if (video) {
            val currentTrack: VideoTrack = mediaStream.videoTracks[0]
            currentTrack.setEnabled(false)
        } else {
            val currentTrack: VideoTrack = mediaStream.videoTracks[0]
            currentTrack.setEnabled(true)
        }
    }

    /**
     * 静音切换
     */
    fun setMic(mic: Boolean) {
        val mediaStream = audioStream?:return
        Log.d("voice",mediaStream.audioTracks.size.toString())
        if(mediaStream.audioTracks.isEmpty())return
        if (mic) {
            val currentTrack: AudioTrack = mediaStream.audioTracks[0]
            currentTrack.setEnabled(false)
        } else {
            val currentTrack: AudioTrack = mediaStream.audioTracks[0]
            currentTrack.setEnabled(true)
        }
    }


}