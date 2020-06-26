package com.eme.shakeit

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorManager
import android.os.Build
import android.os.Bundle
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private lateinit var accelerometer: Sensor

    private lateinit var sensorManager: SensorManager

    private lateinit var accelerometerListener: AccelerometerListener

    private lateinit var timeMaster: TimeMaster

    private var defaultTime = 15 * 1000L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        this.sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager

        timeMaster = TimeMaster(defaultTime, 1000)

        loadSensor()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            initListeners()
        } else {
            // TODO implement for older SDK
        }
    }


    private fun loadSensor() {
        sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)?.let {
            this.accelerometer = it
        }

        accelerometerListener = AccelerometerListener()
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    private fun initListeners() {
        button.setOnClickListener {
            accelerometerListener.reset()
            timeMaster.reset()
            accelerometerListener.startListening()
            tvTimer.background = getDrawable(R.drawable.countdown_ready_shape)
        }

        accelerometerListener.shakeCounter.observe(this, Observer {
            if (it == 1) { // First move, here we go
                timeMaster.start()
                tvTimer.background = getDrawable(R.drawable.countdown_running_shape)
            }
            tvMain.text = it.toString()
        })

        timeMaster.counter.observe(this, Observer {
            tvTimer.text = it.toString()
            if (it == 0) {
                tvTimer.background = getDrawable(R.drawable.countdown_stop_shape)
                accelerometerListener.stopListening()
            }
        })
    }

    override fun onResume() {
        super.onResume()
        sensorManager.registerListener(
            accelerometerListener,
            accelerometer,
            SensorManager.SENSOR_DELAY_NORMAL
        )
    }

    override fun onPause() {
        super.onPause()
        sensorManager.unregisterListener(accelerometerListener)
    }
}