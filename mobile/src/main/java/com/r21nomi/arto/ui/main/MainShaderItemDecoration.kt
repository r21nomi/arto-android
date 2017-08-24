package com.r21nomi.androidshaderviewer.ui.common.adapter.decoration

import android.content.Context
import android.graphics.Rect
import android.support.v7.widget.RecyclerView
import android.view.View
import com.r21nomi.arto.R

/**
 * Created by Ryota Niinomi on 2017/08/24.
 */
class MainShaderItemDecoration(context: Context) : RecyclerView.ItemDecoration() {

    private val horizontalSpace: Int = 0
    private val verticalSpace: Int = context.resources.getDimensionPixelSize(R.dimen.vertical_decoration_main_shader_viewholder)
    private val topOffset: Int = 0
    private val bottomOffset: Int = topOffset

    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        super.getItemOffsets(outRect, view, parent, state)

        val itemPosition = parent.getChildAdapterPosition(view)
        if (itemPosition == RecyclerView.NO_POSITION) {
            return
        }

        outRect.run {
            left = horizontalSpace
            right = horizontalSpace

            if (itemPosition == 0) {
                // first item
                top = topOffset
                bottom = verticalSpace

            } else if (itemPosition == state.itemCount - 1) {
                // last item
                bottom = bottomOffset

            } else {
                bottom = verticalSpace
            }
        }
    }
}