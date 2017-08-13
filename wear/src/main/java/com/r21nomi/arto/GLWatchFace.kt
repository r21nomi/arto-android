package com.r21nomi.arto

import android.content.Context
import android.opengl.GLES20
import android.os.Bundle
import android.support.wearable.watchface.Gles2WatchFaceService
import android.support.wearable.watchface.WatchFaceService
import android.view.SurfaceHolder
import java.io.IOException
import java.io.InputStream
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer

/**
 * Created by r21nomi on 2017/08/12.
 */
class GLWatchFace : Gles2WatchFaceService() {

    override fun onCreateEngine(): Gles2WatchFaceService.Engine {
        return Engine()
    }

    inner class Engine : Gles2WatchFaceService.Engine() {

        private val VERTICES: FloatArray = floatArrayOf(
                -1.0f, 1.0f, 0.0f, // ↖ left top︎
                -1.0f, -1.0f, 0.0f, // ↙︎ left bottom
                1.0f, 1.0f, 0.0f, // ↗︎ right top
                1.0f, -1.0f, 0.0f   // ↘︎ right bottom
        )

        private val TEXCOORD: FloatArray = floatArrayOf(
                0.0f, 0.0f, // ↖ left top︎
                0.0f, 1.0f, // ↙︎ left bottom
                1.0f, 0.0f, // ↗︎ right top
                1.0f, 1.0f  // ↘︎ right bottom
        )

        /**
         * Whether the display supports fewer bits for each color in ambient mode. When true, we
         * disable anti-aliasing in ambient mode.
         */
        private var lowBitAmbient: Boolean = false
        private var ambient: Boolean = false

        private var programId: Int = 0
        private var time: Float = 0f
        private var widthAndHeight: Pair<Float, Float> = Pair(0f, 0f)

        private var timeLoc: Int = 0
        private var resolutionLoc: Int = 0

        private var positionsLoc: Int = 0
        private var texcoordLoc: Int = 0

        private var positionsBuffer: FloatBuffer? = null
        private var texcoordBuffer: FloatBuffer? = null

        private var currentIndex: Int = 0
        private val fragmentShaderResArray = listOf(
                R.raw.fragment_1,
                R.raw.fragment_2
        )

        override fun onCreate(surfaceHolder: SurfaceHolder?) {
            super.onCreate(surfaceHolder)

            positionsBuffer = convert(VERTICES)
            texcoordBuffer = convert(TEXCOORD)
        }

        override fun onGlContextCreated() {
            super.onGlContextCreated()

            init()
        }

        override fun onGlSurfaceCreated(width: Int, height: Int) {
            super.onGlSurfaceCreated(width, height)

            GLES20.glClearColor(0f, 0f, 0f, 0f)
            GLES20.glViewport(0, 0, width, height)

            widthAndHeight = Pair(width.toFloat(), height.toFloat())
        }

        override fun onDraw() {
            super.onDraw()

            if (isInAmbientMode) return

            time += 0.01f

            GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT or GLES20.GL_DEPTH_BUFFER_BIT)

            // position
            GLES20.glVertexAttribPointer(positionsLoc, 3, GLES20.GL_FLOAT, false, 0, positionsBuffer)
            GLES20.glEnableVertexAttribArray(positionsLoc)

            // texcoord
            GLES20.glEnableVertexAttribArray(texcoordLoc)
            GLES20.glVertexAttribPointer(texcoordLoc, 2, GLES20.GL_FLOAT, false, 0, texcoordBuffer)

            // time
            GLES20.glUniform1f(timeLoc, time)

            // resolution
            GLES20.glUniform2f(resolutionLoc, widthAndHeight.first, widthAndHeight.second)

            GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4)

            GLES20.glDisableVertexAttribArray(positionsLoc)
            GLES20.glDisableVertexAttribArray(texcoordLoc)

            invalidate()
        }

        override fun onAmbientModeChanged(inAmbientMode: Boolean) {
            super.onAmbientModeChanged(inAmbientMode)

            if (ambient != inAmbientMode) {
                ambient = inAmbientMode

                if (!ambient) {
                    // TODO: Change shader from sender request.
                    init()
                }

                invalidate()
            }
        }

        override fun onPropertiesChanged(properties: Bundle) {
            super.onPropertiesChanged(properties)

            lowBitAmbient = properties.getBoolean(WatchFaceService.PROPERTY_LOW_BIT_AMBIENT, false)
        }

        private fun init() {
            programId = GLES20.glCreateProgram()

            GLES20.glCreateShader(GLES20.GL_VERTEX_SHADER).let { vertexShader ->
                GLES20.glShaderSource(vertexShader, loadRawResource(applicationContext, R.raw.vertex))
                GLES20.glCompileShader(vertexShader)
                GLES20.glAttachShader(programId, vertexShader)
            }

            GLES20.glCreateShader(GLES20.GL_FRAGMENT_SHADER).let { fragmentShader ->
                GLES20.glShaderSource(fragmentShader, loadRawResource(applicationContext, getFragmentShaderRes()))
                GLES20.glCompileShader(fragmentShader)
                GLES20.glAttachShader(programId, fragmentShader)
            }

            GLES20.glLinkProgram(programId)
            GLES20.glUseProgram(programId)

            positionsLoc = GLES20.glGetAttribLocation(programId, "position")
            texcoordLoc = GLES20.glGetAttribLocation(programId, "texcoord")

            timeLoc = GLES20.glGetUniformLocation(programId, "time")
            resolutionLoc = GLES20.glGetUniformLocation(programId, "resolution")
        }

        @Throws(IOException::class)
        private fun loadRawResource(context: Context, id: Int): String {
            val inputStream: InputStream = context.resources.openRawResource(id)
            val l = inputStream.available()
            val b = ByteArray(l)
            return if (inputStream.read(b) == l) String(b) else ""
        }

        private fun convert(data: FloatArray): FloatBuffer {
            val bb: ByteBuffer = ByteBuffer.allocateDirect(data.size * 4).apply {
                order(ByteOrder.nativeOrder())
            }
            return bb.asFloatBuffer().apply {
                put(data)
                position(0)
            }
        }

        private fun getFragmentShaderRes(): Int {
            return fragmentShaderResArray[currentIndex].apply {
                currentIndex++
                if (currentIndex > fragmentShaderResArray.size - 1) {
                    currentIndex = 0
                }
            }
        }
    }
}