package com.example.kmmkitchentmanagement.AppUtils

import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ListView

object Utils {
fun setListViewHeightBasedOnChildren(listView: ListView) {
    val listAdapter = listView.adapter as? BaseAdapter ?: return

            var totalHeight = 0
    for (i in 0 until listAdapter.count) {
        val listItem = listAdapter.getView(i, null, listView)
        listItem.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED)
        totalHeight += listItem.measuredHeight
    }

    val params = listView.layoutParams
    params.height = totalHeight + (listView.dividerHeight * (listAdapter.count - 1))
    listView.layoutParams = params
    listView.requestLayout()
}
}
