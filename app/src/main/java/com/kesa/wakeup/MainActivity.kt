package com.kesa.wakeup

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.*
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import kotlin.math.pow
import kotlin.math.sqrt


class MainActivity : AppCompatActivity() {

    private val sensorThreshold = 5

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val sensorWork = SensorWorks()

        val showTime = findViewById<TextView>(R.id.textView)
        val x = findViewById<TextView>(R.id.x)
        val y = findViewById<TextView>(R.id.y)
        val z = findViewById<TextView>(R.id.z)

        val turnOnBtm = findViewById<Button>(R.id.recordOn)
        val turnOffBtn = findViewById<Button>(R.id.recordOff)

        val sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        val sensor: Sensor? = sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION)
        sensorManager.registerListener(sensorWork, sensor, SensorManager.SENSOR_DELAY_NORMAL)

        val mHideHandler = Handler(Looper.getMainLooper())
        mHideHandler.post {

        }
        mHideHandler.removeCallbacksAndMessages(null);

        val countDownTimer: CountDownTimer = object : CountDownTimer(2000 * 10, 10) {

            var localMax:Float = Float.MIN_VALUE
            override fun onTick(millisUntilFinished: Long) {
                //반복실행할 구문
                if (localMax < sensorWork.lTotal) localMax = sensorWork.lTotal

                showTime.text = millisUntilFinished.toString()
                x.text = sensorWork.lAccX.toString()
                y.text = sensorWork.lAccY.toString()
                z.text = sensorWork.lAccZ.toString()
            }

            override fun onFinish() {
                //마지막에 실행할 구문
                if (localMax > sensorThreshold) {
                    DoAsync{vibrationOn()}
                    showTime.text = localMax.toString()
                }
            }
        }

        turnOnBtm.setOnClickListener{
            countDownTimer.start() //CountDownTimer 실행
        }
        turnOffBtn.setOnClickListener{
            countDownTimer.cancel() // 타이머 종료
        }

    }

    private fun vibrationOn() {
        val vib = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator  // 진동
        vib.vibrate(VibrationEffect.createWaveform(longArrayOf(500, 1000, 500, 2000), 2))
    }

}

class SensorWorks : SensorEventListener{
    var lAccX: Float = 0.0f
    var lAccY: Float = 0.0f
    var lAccZ: Float = 0.0f

    var lTotal: Float = 0.0f

    override fun onSensorChanged(event: SensorEvent){
        lAccX = event.values[0]
        lAccY = event.values[1]
        lAccZ = event.values[2]

        lTotal = sqrt(lAccX.pow(2) + lAccY.pow(2) + lAccZ.pow(2))
    }

    override fun onAccuracyChanged(p0: Sensor?, p1: Int) {

    }
}

class DoAsync(val handler: () -> Unit) : AsyncTask<Void, Void, Void>() {    // 비동기
    override fun doInBackground(vararg params: Void?): Void? {
        handler()
        return null
    }
}