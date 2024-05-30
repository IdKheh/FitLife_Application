import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.fitlife.CountDownService

class CountdownReceiver(private val onUpdate: (Long) -> Unit, private val onFinish: () -> Unit) : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        when (intent?.action) {
            CountDownService.COUNTDOWN_UPDATED -> {
                val timeLeft = intent.getLongExtra(CountDownService.EXTRA_TIME, 0)
                onUpdate(timeLeft)
            }
            CountDownService.COUNTDOWN_FINISHED -> {
                onFinish()
            }
        }
    }
}
