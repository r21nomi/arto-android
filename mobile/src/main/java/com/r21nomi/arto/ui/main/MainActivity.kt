package com.r21nomi.arto.ui.main

import android.content.ComponentName
import android.net.Uri
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.wearable.companion.WatchFaceCompanion
import android.util.Log
import android.view.View
import android.widget.TextView
import android.widget.Toast
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener
import com.google.android.gms.common.api.ResultCallback
import com.google.android.gms.wearable.DataApi
import com.google.android.gms.wearable.DataEventBuffer
import com.google.android.gms.wearable.PutDataMapRequest
import com.google.android.gms.wearable.Wearable
import com.r21nomi.androidshaderviewer.ui.common.adapter.decoration.MainShaderItemDecoration
import com.r21nomi.arto.R
import com.r21nomi.arto.data.shaderResponse.entity.PreviewShader
import com.r21nomi.arto.lib.Action
import com.r21nomi.arto.ui.BaseActivity
import com.r21nomi.arto.ui.main.di.DaggerMainComponent
import com.r21nomi.arto.ui.main.di.MainComponent
import com.r21nomi.arto.ui.main.di.MainModule
import com.r21nomi.arto.ui.shader_detail.ShaderDetailActivity
import io.reactivex.functions.Consumer
import java.io.IOException
import java.io.InputStream
import javax.inject.Inject

class MainActivity : BaseActivity<MainComponent>() {

    private companion object {
        private val SCHEME = "wear"
        private val PATH_WITH_FEATURE = "/watch_face_config/gl"
        private val KEY_FRAGMENT_SHADER_PROGRAM = "fragment_shader_program"
    }

    @Inject lateinit var mainActionCreator: MainActionCreator
    @Inject lateinit var mainStore: MainStore

    private var currentIndex: Int = 0
    private val fragmentShaderResArray = listOf(
            R.raw.fragment_1,
            R.raw.fragment_2,
            R.raw.fragment_3,
            R.raw.fragment_4,
            R.raw.fragment_5
    )

    private val recyclerView: RecyclerView by lazy { findViewById<RecyclerView>(R.id.recyclerView) }
    private val mainShaderAdapter: MainShaderAdapter = MainShaderAdapter(mutableListOf(), object : MainShaderAdapter.Listener {
        override fun onThumbClick(previewShader: PreviewShader, view: View) {
//            mainActionCreator.changeShader(previewShader.id)

            startActivity(ShaderDetailActivity.createIntent(this@MainActivity, previewShader.id))
        }
    })

    private val peerId: String? by lazy {
        intent.getStringExtra(WatchFaceCompanion.EXTRA_PEER_ID)
    }

    private val connectionCallback: ConnectionCallbacks = object : ConnectionCallbacks {
        override fun onConnected(connectionHint: Bundle?) {
            Log.d(this@MainActivity.javaClass.name, "onConnected: " + connectionHint)

            Wearable.DataApi.addListener(googleApiClient, dataListener)

            if (isWearConnected()) {
                val builder = Uri.Builder()
                val uri = builder.scheme(SCHEME).path(PATH_WITH_FEATURE).authority(peerId).build()
                Wearable.DataApi.getDataItem(googleApiClient, uri).setResultCallback(resultCallback)
            }
        }

        override fun onConnectionSuspended(cause: Int) {
            Log.d(this@MainActivity.javaClass.name, "onConnectionSuspended")
        }
    }

    private val connectionFailedListener: OnConnectionFailedListener = OnConnectionFailedListener { result: ConnectionResult ->
        Log.d(this@MainActivity.javaClass.name, "onConnectionFailed")
    }

    private val resultCallback: ResultCallback<DataApi.DataItemResult> = ResultCallback { dataItemResult ->
        Log.d(this@MainActivity.javaClass.name, "onResult")

//        if (dataItemResult.status.isSuccess && dataItemResult.dataItem != null) {
//            val configDataItem = dataItemResult.dataItem
//            val dataMapItem = DataMapItem.fromDataItem(configDataItem)
//            val config = dataMapItem.dataMap
//        }
    }

    private val dataListener = DataApi.DataListener { dataEvents: DataEventBuffer ->
    }

    private val googleApiClient: GoogleApiClient by lazy {
        GoogleApiClient.Builder(this)
                .addConnectionCallbacks(connectionCallback)
                .addOnConnectionFailedListener(connectionFailedListener)
                .addApi(Wearable.API)
                .build()
    }

    override fun buildComponent(): MainComponent {
        return DaggerMainComponent.builder()
                .applicationComponent(getApplicationComponent())
                .mainModule(MainModule())
                .build()
    }

    override fun injectDependency(component: MainComponent) {
        component.inject(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)

        // TODO: Delete
        intent.getParcelableExtra<ComponentName>(WatchFaceCompanion.EXTRA_WATCH_FACE_COMPONENT)
                .let { name ->
                    findViewById<TextView>(R.id.componentName).text = name?.className?.let {
                        "Connected to $it"
                    } ?: "No Component"
                }

        // TODO: Delete
        findViewById<View>(R.id.changeProgramButton).setOnClickListener {
            mainActionCreator.changeShader(loadRawResource())
        }

        recyclerView.run {
            setHasFixedSize(true)
            addItemDecoration(MainShaderItemDecoration(this@MainActivity))
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = mainShaderAdapter
        }
    }

    override fun onResume() {
        super.onResume()

        mainStore.observeOnMainThread(Consumer {
            when (it.key) {
                MainAction.STORE_INITIALIZED -> updateUI(it)
                MainAction.CHANGE_SHADER -> changeShader()
            }
        })
    }

    override fun onStart() {
        super.onStart()
        googleApiClient.connect()

        if (!mainStore.initialized) {
            mainActionCreator.init()
        }
    }

    override fun onStop() {
        if (googleApiClient.isConnected) {
            Wearable.DataApi.removeListener(googleApiClient, dataListener)
            googleApiClient.disconnect()
        }
        super.onStop()
    }

    private fun isWearConnected(): Boolean {
        return peerId != null
    }

    private fun sendProgram(configKey: String, program: String) {
        if (!isWearConnected()) return

        PutDataMapRequest.create(PATH_WITH_FEATURE).let { dataMap ->
            dataMap.dataMap.putString(configKey, program)
            dataMap.asPutDataRequest().let { request ->
                Wearable.DataApi.putDataItem(googleApiClient, request)
            }
        }

        Log.d(this.javaClass.name, "Sent watch face config message: $configKey -> $program")
    }

    @Throws(IOException::class)
    private fun loadRawResource(): String {
        val inputStream: InputStream = resources.openRawResource(getFragmentShaderRes())
        val l = inputStream.available()
        val b = ByteArray(l)
        return if (inputStream.read(b) == l) String(b) else ""
    }

    private fun getFragmentShaderRes(): Int {
        return fragmentShaderResArray[currentIndex].apply {
            currentIndex++
            if (currentIndex > fragmentShaderResArray.size - 1) {
                currentIndex = 0
            }
        }
    }

    private fun updateUI(action: Action<Any>) {
        if (mainStore.shaderList.isEmpty()) return

        mainShaderAdapter.run {
            setDataSet(mainStore.shaderList)
            notifyDataSetChanged()
        }
    }

    private fun changeShader() {
        mainStore.shader?.content?.renderpass?.first()?.code?.let {
            sendProgram(KEY_FRAGMENT_SHADER_PROGRAM, loadRawResource())
            // Cannot show shader since shadertoy's one is too heavy for GPU.
//            sendProgram(KEY_FRAGMENT_SHADER_PROGRAM, it)
            Toast.makeText(this, "Shader is changed", Toast.LENGTH_SHORT).show()
        }
    }
}
