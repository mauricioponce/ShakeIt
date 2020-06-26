package com.eme.shakeit

import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.util.Log
import androidx.lifecycle.MutableLiveData
import kotlin.math.sqrt

class AccelerometerListener : SensorEventListener {

    private val tag = "AccelerometerListener"

    private var mAccel = 0f
    private var lastTimeShakeDetected = System.currentTimeMillis()
    private var mAccelCurrent = SensorManager.GRAVITY_EARTH
    private val threshold = 3f
    private val timeBeforeDeclaringShakeStopped: Long = 500 // in millis
    private var isShaking = false
    private var listening = true
    var shakeCounter = MutableLiveData<Int>()

    init {
        shakeCounter.value = 0
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if(event?.sensor?.type == Sensor.TYPE_ACCELEROMETER && listening) {
            process(event)
        }
    }

    /**
     * https://github.com/nisrulz/sensey/blob/master/sensey/src/main/java/com/github/nisrulz/sensey/ShakeDetector.java
     */
    private fun process(event: SensorEvent) {
        // use the event timestamp as reference
        // so the manager precision won't depends
        // on the AccelerometerListener implementation
        // processing time
        val now = event.timestamp

        val x = event.values[0]
        val y = event.values[1]
        val z = event.values[2]

        val mAccelLast: Float = mAccelCurrent
        mAccelCurrent = sqrt(x * x + y * y + (z * z).toDouble()).toFloat()
        val delta: Float = mAccelCurrent - mAccelLast
        mAccel = mAccel * 0.9f + delta

        // Make this higher or lower according to how much
        // motion you want to detect
        if (mAccel > threshold) {
            Log.d(tag, "shake detected")
            lastTimeShakeDetected = now
            isShaking = true
            shakeCounter.value = shakeCounter.value?.inc()
        } else {
            val timeDelta: Long = now - lastTimeShakeDetected
            if (timeDelta > timeBeforeDeclaringShakeStopped && isShaking) {
                isShaking = false
                Log.d(tag, "shake stopped")
            }
        }
    }

    fun reset() {
        shakeCounter.value = 0
    }

    fun startListening() {
        listening = true
    }

    fun stopListening() {
        listening = false
    }
}