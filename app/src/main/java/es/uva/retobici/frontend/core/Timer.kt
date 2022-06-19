package es.uva.retobici.frontend.core

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.*

class Timer {

    private val job = SupervisorJob()
    private val scope = CoroutineScope(Dispatchers.Default + job)
    val seconds = MutableLiveData<Int>(0)

    private fun startCoroutineTimer(delayMillis: Long = 0, repeatMillis: Long = 0, action: () -> Unit) = scope.launch(Dispatchers.IO) {
        seconds.postValue(0)
        delay(delayMillis)
        if (repeatMillis > 0) {
            while (true) {
                action()
                delay(repeatMillis)
            }
        } else {
            action()
        }
    }

    private val timer: Job = startCoroutineTimer(delayMillis = 0, repeatMillis = 1000) {
        seconds.postValue(seconds.value!!.plus(1))
        //Log.d("Timer", "Background - tick $seconds")
        //doSomethingBackground()
        /*
        scope.launch(Dispatchers.Main) {
            //Log.d("Timer", "Main thread - tick")
            //doSomethingMainThread()
        }
        */
    }

    fun startTimer() {
        timer.start()
    }

    fun cancelTimer() {
        timer.cancel()
    }
}