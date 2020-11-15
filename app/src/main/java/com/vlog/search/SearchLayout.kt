package com.vlog.search

import android.content.Context
import android.util.AttributeSet
import android.widget.FrameLayout
import com.vlog.R

class SearchLayout :FrameLayout{

    constructor(context: Context):super(context)

    constructor(context: Context, attributeSet: AttributeSet):super(context,attributeSet)

    init {
        inflate(context, R.layout.search_layout,this)
    }
}