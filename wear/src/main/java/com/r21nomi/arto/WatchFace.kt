/*
 * Copyright (C) 2014 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.r21nomi.arto

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.*
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.support.v4.content.ContextCompat
import android.support.wearable.watchface.CanvasWatchFaceService
import android.support.wearable.watchface.WatchFaceService
import android.support.wearable.watchface.WatchFaceStyle
import android.view.SurfaceHolder
import android.view.WindowInsets
import android.widget.Toast
import java.lang.ref.WeakReference
import java.util.*
import java.util.concurrent.TimeUnit

class WatchFace : CanvasWatchFaceService() {

    companion object {
        private val NORMAL_TYPEFACE = Typeface.create(Typeface.SANS_SERIF, Typeface.NORMAL)

        /**
         * Update rate in milliseconds for interactive mode. We update once a second since seconds are
         * displayed in interactive mode.
         */
        private val INTERACTIVE_UPDATE_RATE_MS = TimeUnit.SECONDS.toMillis(1)

        /**
         * Handler message id for updating the time periodically in interactive mode.
         */
        private val MSG_UPDATE_TIME = 0
    }

    override fun onCreateEngine(): Engine {
        return Engine()
    }

    private class EngineHandler(reference: WatchFace.Engine) : Handler() {

        private val weakReference: WeakReference<WatchFace.Engine> = WeakReference(reference)

        override fun handleMessage(msg: Message) {
            val engine = weakReference.get() ?: return

            when (msg.what) {
                MSG_UPDATE_TIME -> engine.handleUpdateTimeMessage()
            }
        }
    }

    inner class Engine : CanvasWatchFaceService.Engine() {
        val updateTimeHandler: Handler = EngineHandler(this)
        var registeredTimeZoneReceiver = false
        val backgroundPaint: Paint by lazy { Paint() }
        val textPaint: Paint by lazy { createTextPaint(ContextCompat.getColor(applicationContext, R.color.time)) }
        var ambient: Boolean = false
        val calendar: Calendar by lazy { Calendar.getInstance() }
        val timeZoneReceiver: BroadcastReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                calendar.timeZone = TimeZone.getDefault()
                invalidate()
            }
        }
        var xOffset: Float = 0f
        var yOffset: Float = 0f

        /**
         * Whether the display supports fewer bits for each color in ambient mode. When true, we
         * disable anti-aliasing in ambient mode.
         */
        var lowBitAmbient: Boolean = false

        override fun onCreate(holder: SurfaceHolder?) {
            super.onCreate(holder)

            setWatchFaceStyle(WatchFaceStyle.Builder(this@WatchFace)
                    .setAcceptsTapEvents(true)
                    .build())

            yOffset = resources.getDimension(R.dimen.time_y_offset)
            backgroundPaint.color = ContextCompat.getColor(applicationContext, R.color.background)
        }

        override fun onDestroy() {
            updateTimeHandler.removeMessages(MSG_UPDATE_TIME)
            super.onDestroy()
        }

        override fun onVisibilityChanged(visible: Boolean) {
            super.onVisibilityChanged(visible)

            if (visible) {
                registerReceiver()

                // Update time zone in case it changed while we weren't visible.
                calendar.timeZone = TimeZone.getDefault()
                invalidate()
            } else {
                unregisterReceiver()
            }

            // Whether the timer should be running depends on whether we're visible (as well as
            // whether we're in ambient mode), so we may need to start or stop the timer.
            updateTimer()
        }

        override fun onApplyWindowInsets(insets: WindowInsets) {
            super.onApplyWindowInsets(insets)

            // Load resources that have alternate values for round watches.
            val resources = this@WatchFace.resources
            val isRound = insets.isRound
            xOffset = resources.getDimension(if (isRound)
                R.dimen.time_x_offset_round
            else
                R.dimen.time_x_offset)
            val textSize = resources.getDimension(if (isRound)
                R.dimen.time_text_size_round
            else
                R.dimen.time_text_size)

            textPaint.textSize = textSize
        }

        override fun onPropertiesChanged(properties: Bundle?) {
            super.onPropertiesChanged(properties)
            lowBitAmbient = properties!!.getBoolean(WatchFaceService.PROPERTY_LOW_BIT_AMBIENT, false)
        }

        override fun onTimeTick() {
            super.onTimeTick()
            invalidate()
        }

        override fun onAmbientModeChanged(inAmbientMode: Boolean) {
            super.onAmbientModeChanged(inAmbientMode)
            if (ambient != inAmbientMode) {
                ambient = inAmbientMode
                if (lowBitAmbient) {
                    textPaint.isAntiAlias = !inAmbientMode
                }
                invalidate()
            }

            // Whether the timer should be running depends on whether we're visible (as well as
            // whether we're in ambient mode), so we may need to start or stop the timer.
            updateTimer()
        }

        /**
         * Captures tap event (and tap type) and toggles the background color if the user finishes
         * a tap.
         */
        override fun onTapCommand(tapType: Int, x: Int, y: Int, eventTime: Long) {
            when (tapType) {
                WatchFaceService.TAP_TYPE_TOUCH -> {
                }
                WatchFaceService.TAP_TYPE_TOUCH_CANCEL -> {
                }
                WatchFaceService.TAP_TYPE_TAP ->
                    // The user has completed the tap gesture.
                    // TODO: Add code to handle the tap gesture.
                    Toast.makeText(applicationContext, R.string.message, Toast.LENGTH_SHORT).show()
            }// The user has started touching the screen.
            // The user has started a different gesture or otherwise cancelled the tap.
            invalidate()
        }

        override fun onDraw(canvas: Canvas?, bounds: Rect?) {
            if (canvas == null || bounds == null) return

            // Draw the background.
            if (isInAmbientMode) {
                canvas.drawColor(Color.BLACK)
            } else {
                canvas.drawRect(0f, 0f, bounds.width().toFloat(), bounds.height().toFloat(), backgroundPaint)
            }

            // Draw H:MM in ambient mode or H:MM:SS in interactive mode.
            val now = System.currentTimeMillis()
            calendar.timeInMillis = now

            val text = if (ambient) {
                "${calendar.get(Calendar.HOUR)}:${calendar.get(Calendar.MINUTE)}"
            } else {
                "${calendar.get(Calendar.HOUR)}:${calendar.get(Calendar.MINUTE)}:${calendar.get(Calendar.SECOND)}"
            }

            canvas.drawText(text, xOffset, yOffset, textPaint)
        }

        private fun createTextPaint(textColor: Int): Paint {
            val paint = Paint()
            paint.color = textColor
            paint.typeface = NORMAL_TYPEFACE
            paint.isAntiAlias = true
            return paint
        }

        private fun registerReceiver() {
            if (registeredTimeZoneReceiver) {
                return
            }
            registeredTimeZoneReceiver = true
            val filter = IntentFilter(Intent.ACTION_TIMEZONE_CHANGED)
            this@WatchFace.registerReceiver(timeZoneReceiver, filter)
        }

        private fun unregisterReceiver() {
            if (!registeredTimeZoneReceiver) {
                return
            }
            registeredTimeZoneReceiver = false
            this@WatchFace.unregisterReceiver(timeZoneReceiver)
        }

        /**
         * Starts the [.updateTimeHandler] timer if it should be running and isn't currently
         * or stops it if it shouldn't be running but currently is.
         */
        private fun updateTimer() {
            updateTimeHandler.removeMessages(MSG_UPDATE_TIME)

            if (shouldTimerBeRunning()) {
                updateTimeHandler.sendEmptyMessage(MSG_UPDATE_TIME)
            }
        }

        /**
         * Returns whether the [.updateTimeHandler] timer should be running. The timer should
         * only run when we're visible and in interactive mode.
         */
        private fun shouldTimerBeRunning(): Boolean {
            return isVisible && !isInAmbientMode
        }

        /**
         * Handle updating the time periodically in interactive mode.
         */
        fun handleUpdateTimeMessage() {
            invalidate()

            if (shouldTimerBeRunning()) {
                val timeMs = System.currentTimeMillis()
                val delayMs = INTERACTIVE_UPDATE_RATE_MS - timeMs % INTERACTIVE_UPDATE_RATE_MS
                updateTimeHandler.sendEmptyMessageDelayed(MSG_UPDATE_TIME, delayMs)
            }
        }
    }
}
