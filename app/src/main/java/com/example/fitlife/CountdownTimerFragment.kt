
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.NumberPicker
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.fitlife.CountDownService
import com.example.fitlife.R
import com.google.android.material.floatingactionbutton.FloatingActionButton

class CountdownTimerFragment : Fragment() {
    private lateinit var textViewTimer: TextView
    private lateinit var hourPicker: NumberPicker
    private lateinit var minPicker: NumberPicker
    private lateinit var sekPicker: NumberPicker
    private var isTimerRunning = false
    private lateinit var countdownReceiver: CountdownReceiver

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_countdown_timer, container, false)
        textViewTimer = view.findViewById(R.id.textViewTimer)
        hourPicker = view.findViewById(R.id.hourPicker)
        hourPicker.maxValue = 23
        hourPicker.minValue = 0
        minPicker = view.findViewById(R.id.minPicker)
        minPicker.maxValue = 59
        minPicker.minValue = 0
        sekPicker = view.findViewById(R.id.sekPicker)
        sekPicker.maxValue = 59
        sekPicker.minValue = 0
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        countdownReceiver = CountdownReceiver(
            onUpdate = { timeLeft ->
                val hours = timeLeft / 1000 / 3600
                val minutes = (timeLeft / 1000 % 3600) / 60
                val seconds = (timeLeft / 1000 % 3600) % 60
                textViewTimer.text = String.format("%02d:%02d:%02d", hours, minutes, seconds)
            },
            onFinish = {
                textViewTimer.text = "00:00:00"
                isTimerRunning = false
            }
        )

        val filter = IntentFilter().apply {
            addAction(CountDownService.COUNTDOWN_UPDATED)
            addAction(CountDownService.COUNTDOWN_FINISHED)
        }
        requireActivity().registerReceiver(countdownReceiver, filter)

        view.findViewById<FloatingActionButton>(R.id.fabStart).setOnClickListener {
            if (!isTimerRunning) {
                startCountdown()
            }
        }
        view.findViewById<FloatingActionButton>(R.id.fabStop).setOnClickListener {
            stopCountdown()
        }
        view.findViewById<FloatingActionButton>(R.id.fabReset).setOnClickListener {
            resetCountdown()
        }
    }

    private fun startCountdown() {
        val hours = hourPicker.value
        val minutes = minPicker.value
        val seconds = sekPicker.value
        val durationInMillis: Long = (hours * 3600 + minutes * 60 + seconds) * 1000L
        val intent = Intent(requireContext(), CountDownService::class.java).apply {
            action = CountDownService.ACTION_START
            putExtra(CountDownService.EXTRA_TIME, durationInMillis)
        }
        requireActivity().startService(intent)
        isTimerRunning = true
    }

    private fun stopCountdown() {
        val intent = Intent(requireContext(), CountDownService::class.java).apply {
            action = CountDownService.ACTION_STOP
        }
        requireActivity().startService(intent)
        isTimerRunning = false
    }

    private fun resetCountdown() {
        val intent = Intent(requireContext(), CountDownService::class.java).apply {
            action = CountDownService.ACTION_RESET
        }
        requireActivity().startService(intent)
        textViewTimer.text = "00:00:00"
        isTimerRunning = false
    }

    override fun onDestroyView() {
        super.onDestroyView()
        requireActivity().unregisterReceiver(countdownReceiver)
    }
}
