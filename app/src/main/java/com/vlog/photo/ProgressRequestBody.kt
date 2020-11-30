package com.vlog.photo

import okhttp3.MediaType
import okhttp3.RequestBody
import okio.*
import java.io.File

class ProgressRequestBody(private val mediaType: MediaType?, private val file: File,private val listener:(Long,Long,Boolean)->Unit) :RequestBody(){

    override fun contentType(): MediaType? {
        return mediaType
    }

    override fun contentLength(): Long {
        return file.length()
    }

    override fun writeTo(sink: BufferedSink) {
        val source: Source
        try {
            source = file.source()
            //sink.writeAll(source);
            val buf = Buffer()
            var writeCount = 0L
            var contentLength= contentLength()
            var readCount: Long
            while (source.read(buf, 2048).also { readCount = it } != -1L) {
                sink.write(buf, readCount)
                writeCount+=readCount
                listener.invoke(
                    contentLength ,
                    writeCount,
                    writeCount==contentLength
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}