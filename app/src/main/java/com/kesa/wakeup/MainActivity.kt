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
import kotlin.math.absoluteValue
import kotlin.math.pow
import kotlin.math.sqrt


class MainActivity : AppCompatActivity() {
    private val timeInterval : Long = 50 * 100
    private val sensorThreshold = 0.3
    lateinit var countDownTimer:CountDownTimer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val sensorWork = SensorWorks()

        val showTime = findViewById<TextView>(R.id.TimeLeftByChecking)
        val x = findViewById<TextView>(R.id.AccX)
        val y = findViewById<TextView>(R.id.AccY)
        val z = findViewById<TextView>(R.id.AccZ)
        val tot = findViewById<TextView>(R.id.AccTotal)

        val turnOnBtm = findViewById<Button>(R.id.recordOn)
        val turnOffBtn = findViewById<Button>(R.id.recordOff)

        val sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        val sensor: Sensor? = sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION)
        sensorManager.registerListener(sensorWork, sensor, SensorManager.SENSOR_DELAY_NORMAL)


        val handler = Handler()
        var trigger = true
        val obj = Runnable {
            if (trigger) {
                sensorWork.max = Float.MIN_VALUE
                countDownTimer.start()
            }
        }
        countDownTimer = object : CountDownTimer(timeInterval, 10) {

            override fun onTick(millisUntilFinished: Long) {
                //반복실행할 구문

                showTime.text = millisUntilFinished.toString()
                x.text = sensorWork.lAccX.toString()
                y.text = sensorWork.lAccY.toString()
                z.text = sensorWork.lAccZ.toString()
                tot.text = sensorWork.lTotal.toString()
            }

            override fun onFinish() {
                //마지막에 실행할 구문
                if (sensorWork.max< sensorThreshold) {
                    vibrationOn()
                    showTime.text = sensorWork.max.toString()
                }

                handler.post(obj)
            }
        }


        turnOnBtm.setOnClickListener{
            trigger = true
            handler.post(obj)
//            countDownTimer.start() //CountDownTimer 실행
        }
        turnOffBtn.setOnClickListener{
            trigger = false
//            countDownTimer.cancel() // 타이머 종료
        }

    }

    private fun vibrationOn() {
        val vib = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator  // 진동
        vib.vibrate(VibrationEffect.createWaveform(longArrayOf(0, 100, 100, 100, 100), intArrayOf(0, 100, 0, 100, 0), -1))
    }

}

class SensorWorks : SensorEventListener{
    var preX: Float = 0.0f
    var preY: Float = 0.0f
    var preZ: Float = 0.0f

    var lAccX: Float = 0.0f
    var lAccY: Float = 0.0f
    var lAccZ: Float = 0.0f

    var lTotal: Float = 0.0f

    var max = Float.MIN_VALUE

    override fun onSensorChanged(event: SensorEvent){
        lAccX = event.values[0].absoluteValue - preX
        lAccY = event.values[1].absoluteValue - preY
        lAccZ = event.values[2].absoluteValue - preZ

        preX = event.values[0].absoluteValue
        preY = event.values[1].absoluteValue
        preZ = event.values[2].absoluteValue

        lTotal = sqrt(lAccX.pow(2) + lAccY.pow(2) + lAccZ.pow(2))

        if (max < lTotal) max = lTotal
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