package com.example.myapplication.features.home

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.myapplication.data.model.SensorData
import com.example.myapplication.databinding.ActivityHomeBinding
import java.text.SimpleDateFormat
import java.util.*

class HomeActivity : AppCompatActivity(), SensorEventListener {
    private lateinit var binding: ActivityHomeBinding

    //Sensors
    private lateinit var sensorManager: SensorManager
    private lateinit var sensorGravity: Sensor
    private lateinit var sensorAccel: Sensor
    private lateinit var sensorGyro: Sensor

    //SensorData
    private var gravityData: SensorData? = null
    private var accelerationData: SensorData? = null
    private var gyroData: SensorData? = null

    //Gyrodata
    private var gyroX: Float = 0f
    private var gyroy: Float = 0f
    private var gyroz: Float = 0f


    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityHomeBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        initSensors()

        binding.tvTime.text = getCurrentDateTime().toString("HH:mm")
        binding.tvDate.text = getCurrentDateTime().toString("dd MMM yyy")

        binding.btnReset.setOnClickListener {
            reset()
            unregisterListener()
            binding.btnStart.isEnabled = true
            binding.btnStop.isEnabled = true
        }

        binding.btnStart.setOnClickListener {
            registerListener()
            binding.btnStart.isEnabled = false
            binding.btnStop.isEnabled = true
        }

        binding.btnStop.setOnClickListener {
            unregisterListener()
            binding.btnStart.isEnabled = true
            binding.btnStop.isEnabled = false
        }

    }

    fun Date.toString(format: String, locale: Locale = Locale.getDefault()): String {
        val formatter = SimpleDateFormat(format, locale)
        return formatter.format(this)
    }

    fun getCurrentDateTime(): Date {
        return Calendar.getInstance().time
    }

    private fun initSensors() {
        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        if (sensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY) != null) {
            sensorGravity = sensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY)
        }
        if (sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER) != null) {
            sensorAccel = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        }
        if (sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE) != null) {
            sensorGyro = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE)
        }
    }

    private fun registerListener(){
        if (sensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY) != null) {
            sensorManager.registerListener(this, sensorGravity, SensorManager.SENSOR_DELAY_NORMAL)
        }
        if (sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER) != null) {
            sensorManager.registerListener(this, sensorAccel, SensorManager.SENSOR_DELAY_NORMAL)
        }
        if (sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE) != null) {
            sensorManager.registerListener(this, sensorGyro, SensorManager.SENSOR_DELAY_NORMAL)
        }
    }

    private fun unregisterListener(){
        sensorManager.unregisterListener(this, sensorGravity)
        sensorManager.unregisterListener(this, sensorAccel)
        sensorManager.unregisterListener(this, sensorGyro)
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {

    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (event != null) {
            if(event.sensor.type == Sensor.TYPE_GRAVITY) {
                getGravityData(event)
            }
            else if(event.sensor.type == Sensor.TYPE_ACCELEROMETER) {
                getAccelerationData(event)
            }
            else if (event.sensor.type == Sensor.TYPE_GYROSCOPE) {
                getGyroData(event)
            }
        }
    }

    private fun getGravityData(e: SensorEvent?){
        if(gravityData == null){
            if (e != null) {
                gravityData = SensorData(e.values[0],e.values[1], e.values[2], e.timestamp)
            }
        }
        else {
            gravityData!!.x1 = e!!.values[0]
            gravityData!!.x2 = e!!.values[1]
            gravityData!!.x3 = e!!.values[2]
        }
        binding.tvGX.text = "x: ${"%.2f".format(gravityData!!.x1)} m/s²"
        binding.tvGY.text = "y: ${"%.2f".format(gravityData!!.x2)} m/s²"
        binding.tvGZ.text = "z: ${"%.2f".format(gravityData!!.x3)} m/s²"
    }

    private fun getAccelerationData(e: SensorEvent?){
        if(accelerationData == null){
            if (e != null) {
                accelerationData = SensorData(e.values[0],e.values[1], e.values[2], e.timestamp)
            }
        }
        else {
            accelerationData!!.x1 = e!!.values[0]
            accelerationData!!.x2 = e!!.values[1]
            accelerationData!!.x3 = e!!.values[2]
        }
        binding.tvAX.text = "x: ${"%.2f".format(accelerationData!!.x1)} m/s²"
        binding.tvAY.text = "y: ${"%.2f".format(accelerationData!!.x2)} m/s²"
        binding.tvAZ.text = "z: ${"%.2f".format(accelerationData!!.x3)} m/s²"
    }

    private fun getGyroData(e: SensorEvent?){
        if(gyroData == null){
            if (e != null) {
                gyroData = SensorData(e.values[0],e.values[1], e.values[2], e.timestamp)
            }
        }
        else {
            gyroData!!.x1 = e!!.values[0]
            gyroData!!.x2 = e!!.values[1]
            gyroData!!.x3 = e!!.values[2]
        }
        binding.tvGyX.text = "x: ${"%.2f".format(gyroData!!.x1)} m/s²"
        binding.tvGyY.text = "y: ${"%.2f".format(gyroData!!.x2)} m/s²"
        binding.tvGyZ.text = "z: ${"%.2f".format(gyroData!!.x3)} m/s²"
    }

    private fun reset(){
        binding.apply {
            tvGX.text = "x: 0 m/s²"
            tvGY.text = "y: 0 m/s²"
            tvGZ.text = "z: 0 m/s²"
            tvAX.text = "x: 0 m/s²"
            tvAY.text = "y: 0 m/s²"
            tvAZ.text = "z: 0 m/s²"
            tvGyX.text = "x: 0 m/s²"
            tvGyY.text = "y: 0 m/s²"
            tvGyZ.text = "z: 0 m/s²"
        }
    }

}