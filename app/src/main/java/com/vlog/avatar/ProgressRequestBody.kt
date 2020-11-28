package com.vlog.avatar

import okhttp3.MediaType
import okhttp3.RequestBody
import okio.*
import java.io.File

class ProgressRequestBody(private val mediaType: MediaType,private val file:File) :RequestBody(){

    override fun contentType(): MediaType? {
        return mediaType
    }

    override fun writeTo(sink: BufferedSink) {
        val source = file.source().buffer()
        val contentLength = file.length()
        sink.w
    }
}