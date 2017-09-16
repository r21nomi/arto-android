package com.r21nomi.arto.ui.shader_detail

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import com.r21nomi.arto.R
import com.r21nomi.arto.lib.Action
import com.r21nomi.arto.ui.BaseActivity
import com.r21nomi.arto.ui.shader_detail.di.DaggerShaderDetailComponent
import com.r21nomi.arto.ui.shader_detail.di.ShaderDetailComponent
import com.r21nomi.arto.ui.shader_detail.di.ShaderDetailModule
import io.reactivex.functions.Consumer
import javax.inject.Inject

/**
 * Created by r21nomi on 2017/09/06.
 */
class ShaderDetailActivity : BaseActivity<ShaderDetailComponent>() {

    companion object {
        private val SHADER_ID = "shader_id"

        fun createIntent(context: Context, shaderId: String): Intent {
            return Intent(context, ShaderDetailActivity::class.java).apply {
                putExtra(SHADER_ID, shaderId)
            }
        }
    }

    @Inject lateinit var shaderDetailStore: ShaderDetailStore
    @Inject lateinit var shaderDetailActionCreator: ShaderDetailActionCreator

    override fun buildComponent(): ShaderDetailComponent {
        return DaggerShaderDetailComponent.builder()
                .applicationComponent(getApplicationComponent())
                .shaderDetailModule(ShaderDetailModule())
                .build()
    }

    override fun injectDependency(component: ShaderDetailComponent) {
        component.inject(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.shader_detail_activity)
    }

    override fun onResume() {
        super.onResume()

        shaderDetailStore.observeOnMainThread(Consumer {
            when (it.key) {
                ShaderDetailAction.FETCH_SHADER_DETAIL -> showShaderDetail(it)
            }
        })
    }

    override fun onStart() {
        super.onStart()

        shaderDetailActionCreator.fetchShaderDetail(intent.getStringExtra(SHADER_ID))
    }

    private fun showShaderDetail(action: Action<Any>) {
        shaderDetailStore.shader?.content?.renderpass?.get(0)?.code?.let { text ->
            findViewById<TextView>(R.id.shaderProgramText).text = text
        }
    }
}