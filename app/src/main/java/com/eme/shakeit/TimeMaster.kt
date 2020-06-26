package com.eme.shakeit

import android.os.CountDownTimer
import android.util.Log
import androidx.lifecycle.MutableLiveData
import java.util.concurrent.TimeUnit

class TimeMaster(var millisInFuture: Long, countDownInterval: Long) : CountDownTimer(
    millisInFuture,
    countDownInterval
) {
    private var tag = "TimeMaster"

    val counter = MutableLiveData<Int>()

    init {
        counter.value = getSeconds(millisInFuture)
    }

    override fun onFinish() {

    }

    override fun onTick(millisUntilFinished: Long) {
        Log.d(tag, "time ${counter.value}")
        counter.value = getSeconds(millisUntilFinished)
    }

    fun reset() {
        counter.value = getSeconds(millisInFuture)
    }

    private fun getSeconds(millis: Long): Int {
        return TimeUnit.MILLISECONDS.toSeconds(millis).toInt()
    }
}