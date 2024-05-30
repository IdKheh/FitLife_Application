package com.example.fitlife
import android.app.Service
import android.content.Intent
import android.os.CountDownTimer
import android.os.IBinder

class CountDownService : Service() {

    private var timer: CountDownTimer? = null
    private var remainingTime: Long = 0L

    companion object {
        const val ACTION_START = "com.example.fitlife.START"
        const val ACTION_STOP = "com.example.fitlife.STOP"
        const val ACTION_RESET = "com.example.fitlife.RESET"
        const val COUNTDOWN_UPDATED = "com.example.fitlife.COUNTDOWN_UPDATED"
        const val COUNTDOWN_FINISHED = "com.example.fitlife.COUNTDOWN_FINISHED"
        const val EXTRA_TIME = "com.example.fitlife.EXTRA_TIME"
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_START -> {
                val duration = intent.getLongExtra(EXTRA_TIME, 0L)
                startCountdown(duration)
            }
            ACTION_STOP -> stopCountdown()
            ACTION_RESET -> resetCountdown()
        }
        return START_STICKY
    }

    private fun startCountdown(duration: Long) {
        timer?.cancel()
        timer = object : CountDownTimer(duration, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                remainingTime = millisUntilFinished
                val intent = Intent(COUNTDOWN_UPDATED)
                intent.putExtra(EXTRA_TIME, millisUntilFinished)
                sendBroadcast(intent)
            }

            override fun onFinish() {
                val intent = Intent(COUNTDOWN_FINISHED)
                sendBroadcast(intent)
                stopSelf()
            }
        }
        timer?.start()
    }

    private fun stopCountdown() {
        timer?.cancel()
        remainingTime = 0L
        stopSelf()
    }

    private fun resetCountdown() {
        timer?.cancel()
        remainingTime = 0L
        val intent = Intent(COUNTDOWN_UPDATED)
        intent.putExtra(EXTRA_TIME, 0L)
        sendBroadcast(intent)
        stopSelf()
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onDestroy() {
        timer?.cancel()
        super.onDestroy()
    }
}
