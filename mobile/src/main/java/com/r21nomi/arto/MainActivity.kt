package com.r21nomi.arto

import android.content.ComponentName
import android.net.Uri
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.wearable.companion.WatchFaceCompanion
import android.util.Log
import android.view.View
import android.widget.TextView
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener
import com.google.android.gms.common.api.ResultCallback
import com.google.android.gms.wearable.*
import java.io.IOException
import java.io.InputStream

class MainActivity : AppCompatActivity() {

    private companion object {
        private val PATH_WITH_FEATURE = "/watch_face_config/gl"
        private val KEY_FRAGMENT_SHADER_PROGRAM = "fragment_shader_program"
    }

    private val peerId: String? by lazy {
        intent.getStringExtra(WatchFaceCompanion.EXTRA_PEER_ID)
    }

    private val connectionCallback: ConnectionCallbacks = object : ConnectionCallbacks {
        override fun onConnected(connectionHint: Bundle?) {
            Log.d(this@MainActivity.javaClass.name, "onConnected: " + connectionHint)

            Wearable.DataApi.addListener(googleApiClient, dataListener)

            if (peerId != null) {
                val builder = Uri.Builder()
                val uri = builder.scheme("wear").path(PATH_WITH_FEATURE).authority(peerId).build()
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val name: ComponentName? = intent.getParcelableExtra<ComponentName>(WatchFaceCompanion.EXTRA_WATCH_FACE_COMPONENT)

        findViewById<TextView>(R.id.componentName).text = name?.className?.let { "Connected to $it" } ?: "No Component"

        findViewById<View>(R.id.changeProgramButton).setOnClickListener {
            sendProgram(KEY_FRAGMENT_SHADER_PROGRAM, loadRawResource(R.raw.fragment_1))
        }
    }

    override fun onStart() {
        super.onStart()
        googleApiClient.connect()
    }

    override fun onStop() {
        if (googleApiClient.isConnected) {
            Wearable.DataApi.removeListener(googleApiClient, dataListener)
            googleApiClient.disconnect()
        }
        super.onStop()
    }

    private fun sendProgram(configKey: String, program: String) {
        if (peerId == null) return

//        DataMap().let { config ->
//            config.putString(configKey, program)
//            config.toByteArray().let { rawData ->
//                Wearable.MessageApi.sendMessage(googleApiClient, peerId, PATH_WITH_FEATURE, rawData)
//            }
//        }

        PutDataMapRequest.create(PATH_WITH_FEATURE).let { dataMap ->
            dataMap.dataMap.putString(configKey, program)
            dataMap.asPutDataRequest().let { request ->
                Wearable.DataApi.putDataItem(googleApiClient, request)
            }
        }

        Log.d(this.javaClass.name, "Sent watch face config message: $configKey -> $program")
    }

    @Throws(IOException::class)
    private fun loadRawResource(id: Int): String {
        val inputStream: InputStream = resources.openRawResource(id)
        val l = inputStream.available()
        val b = ByteArray(l)
        return if (inputStream.read(b) == l) String(b) else ""
    }
}
