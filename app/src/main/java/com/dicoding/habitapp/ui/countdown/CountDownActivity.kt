package com.dicoding.habitapp.ui.countdown

import android.app.Notification
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.IntentCompat.getParcelableExtra
import androidx.lifecycle.ViewModelProvider
import androidx.work.Constraints
import androidx.work.Data
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.dicoding.habitapp.R
import com.dicoding.habitapp.data.Habit
import com.dicoding.habitapp.notification.NotificationWorker
import com.dicoding.habitapp.utils.HABIT
import com.dicoding.habitapp.utils.HABIT_ID
import com.dicoding.habitapp.utils.HABIT_TITLE
import com.dicoding.habitapp.utils.NOTIF_UNIQUE_WORK
import java.util.concurrent.TimeUnit

class CountDownActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_count_down)
        supportActionBar?.title = "Count Down"

        val habit = getParcelableExtra(intent, HABIT, Habit::class.java)

        if (habit != null) {
            findViewById<TextView>(R.id.tv_count_down_title).text = habit.title

            val viewModel = ViewModelProvider(this).get(CountDownViewModel::class.java)

            //TODO 10 : Set initial time and observe current time. Update button state when countdown is finished
            viewModel.setInitialTime(habit.minutesFocus)
            viewModel.currentTimeString.observe(this) {
                findViewById<TextView>(R.id.tv_count_down_title).text = it
            }

            viewModel.eventCountDownFinish.observe(this) {
                if (it) {
                    updateButtonState(false)
                }
            }

            //TODO 13 : Start and cancel One Time Request WorkManager to notify when time is up.
            val workManager = WorkManager.getInstance(applicationContext)
            val contraints = Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build()
            val inputData = Data.Builder()
                .putInt(HABIT_ID, habit.id)
                .putString(HABIT_TITLE, habit.title)
                .build()
            val notificationRequest =
                OneTimeWorkRequestBuilder<NotificationWorker>()
                    .setInitialDelay(habit.minutesFocus, TimeUnit.MINUTES)
                    .setConstraints(constraints = contraints)
                    .setInputData(inputData)
                    .build()
            val notificationWorkTag = NOTIF_UNIQUE_WORK
            findViewById<Button>(R.id.btn_start).setOnClickListener {
                workManager.enqueueUniqueWork(
                    notificationWorkTag,
                    ExistingWorkPolicy.REPLACE,
                    notificationRequest
                )
                viewModel.startTimer()
            }

            findViewById<Button>(R.id.btn_stop).setOnClickListener {
                workManager.cancelUniqueWork(notificationWorkTag)
                viewModel.resetTimer()
            }
        }

    }

    private fun updateButtonState(isRunning: Boolean) {
        findViewById<Button>(R.id.btn_start).isEnabled = !isRunning
        findViewById<Button>(R.id.btn_stop).isEnabled = isRunning
    }
}