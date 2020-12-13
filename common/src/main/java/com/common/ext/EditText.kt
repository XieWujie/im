package com.common.ext

import android.text.Editable
import android.text.Spannable
import android.text.SpannableString
import android.text.TextWatcher
import android.text.style.ImageSpan
import android.util.Log
import android.widget.EditText
import android.widget.TextView
import com.common.customer.GlideImageGetter
import com.common.pushExecutors
import com.common.util.Util

fun EditText.afterTextChanged(afterTextChanged: (String) -> Unit) {
    this.addTextChangedListener(object : TextWatcher {
        override fun afterTextChanged(editable: Editable?) {
            afterTextChanged.invoke(editable.toString())
        }

        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
    })
}

data class Emotion(val source: String, val start: Int, val end: Int)


fun TextView.setEmotionText(text: String){

    pushExecutors {
        val pat = Regex("""\[[^\[\]]+.[png|gif]]""")
        val ms = pat.findAll(text)
        val emotions = ms.map {
            val value = it.value
            val source = value.substring(1, value.length - 1)
            Emotion(source, it.range.first, it.range.last+1)
        }

        val spanString = SpannableString(text)
        for (emo in emotions) {

            val drawable = try {
                GlideImageGetter(context, this).getDrawable(emo.source)
            }catch (e:Exception){
                continue
            }
            val span = ImageSpan(drawable)
            spanString.setSpan(span, emo.start, emo.end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        }
        this.post {
            setText(spanString)
            if(this is EditText){
                setSelection(length())
            }
        }
    }
    }