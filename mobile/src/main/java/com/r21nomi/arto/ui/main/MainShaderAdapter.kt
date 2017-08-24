package com.r21nomi.arto.ui.main

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.r21nomi.arto.GlideApp
import com.r21nomi.arto.R
import com.r21nomi.arto.data.shaderResponse.entity.PreviewShader


/**
 * Created by r21nomi on 2017/08/24.
 */
class MainShaderAdapter(private val dataSet: MutableList<PreviewShader>,
                        val listener: Listener)
    : RecyclerView.Adapter<MainShaderAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return LayoutInflater.from(parent.context)
                .inflate(R.layout.main_shader_viewholder, parent, false)
                .let {
                    ViewHolder(it)
                }
                .apply {
                    thumb.setOnClickListener {
                        listener.onThumbClick(dataSet[adapterPosition], it)
                    }
                }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val context = holder.itemView.context

        GlideApp.with(context)
                .load(dataSet[position].getUrl())
                .into(holder.thumb)
    }

    override fun getItemCount(): Int {
        return dataSet.size
    }

    fun setDataSet(dataSet: List<PreviewShader>) {
        this.dataSet.clear()
        this.dataSet.addAll(dataSet)
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val thumb: ImageView by lazy { view.findViewById<ImageView>(R.id.thumb) }
    }

    interface Listener {
        fun onThumbClick(previewShader: PreviewShader, view: View)
    }
}