package com.arohaka.musictribegraph.activities

import android.graphics.Color
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.view.MenuItem
import android.view.animation.OvershootInterpolator
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.arohaka.musictribegraph.R
import com.arohaka.musictribegraph.databinding.ActivityGraphBinding
import com.github.mikephil.charting.components.Description
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.robinhood.ticker.TickerUtils
import java.util.*


class GraphActivity : AppCompatActivity(), SensorEventListener {

    private lateinit var mSensorManager: SensorManager
    private lateinit var mAccelerometer: Sensor
    private lateinit var mBinding: ActivityGraphBinding
    private var mIsAccelerometerEnabled = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_graph)
        initViews()
    }

    private fun initViews() {

        mBinding.tbGraph.title = "Sensor With Graph"
        setSupportActionBar(mBinding.tbGraph)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        mBinding.tickerView.setCharacterLists(TickerUtils.provideNumberList())
        mBinding.tickerView.animationInterpolator = OvershootInterpolator()


        mBinding.lavSwitch.setOnClickListener {
            if (!mIsAccelerometerEnabled) {
                mIsAccelerometerEnabled = true
                mBinding.lavSwitch.playAnimation()
            } else {
                mIsAccelerometerEnabled = false
                mBinding.lavSwitch.progress = 0F
            }
        }

        mSensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)

        mBinding.chart.setTouchEnabled(false)
        mBinding.chart.setPinchZoom(false)

        /* mBinding.chart.axisLeft.setDrawGridLines(false)
         mBinding.chart.axisRight.setDrawGridLines(false)
         mBinding.chart.xAxis.setDrawGridLines(false)

         mBinding.chart.setDrawGridBackground(false)
         mBinding.chart.xAxis.setDrawAxisLine(false)
         mBinding.chart.axisLeft.setDrawGridLines(false)
         mBinding.chart.axisLeft.setDrawAxisLine(false)
         mBinding.chart.axisRight.setDrawGridLines(false)
         mBinding.chart.axisRight.setDrawAxisLine(false)*/

        val description = Description()
        description.text = ""
        mBinding.chart.description = description

        mBinding.chart.description = description // Hide the description

        mBinding.chart.axisLeft.setDrawLabels(false)
        mBinding.chart.axisRight.setDrawLabels(false)
        mBinding.chart.xAxis.setDrawLabels(false)

        mBinding.chart.legend.isEnabled = false

        val values = ArrayList<Entry>()
        values.add(Entry(1.toFloat(), 50.toFloat()))
        values.add(Entry(2.toFloat(), 100.toFloat()))

        mBinding.chart.data = getBaseLineData()
    }

    override fun onResume() {
        super.onResume()
        mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_NORMAL)
    }

    override fun onPause() {
        super.onPause()
        mSensorManager.unregisterListener(this)
    }


    private fun getBaseLineData(): LineData {
        val arrayList: MutableList<Entry> = mutableListOf()
        for (i in 0..199) {
            arrayList.add(Entry(i.toFloat(), 0.0f))
        }
        val customLineDataSet = LineDataSet(arrayList, null as String?)
        customLineDataSet.setDrawCircles(false)
        customLineDataSet.setDrawValues(false)
        customLineDataSet.color = ContextCompat.getColor(this, android.R.color.holo_red_dark)
        customLineDataSet.lineWidth = 1.5f
        customLineDataSet.axisDependency = YAxis.AxisDependency.RIGHT
        return LineData(customLineDataSet)
    }

    override fun onSensorChanged(event: SensorEvent) {
        if (event.sensor.type == Sensor.TYPE_ACCELEROMETER) {
            if (mIsAccelerometerEnabled)
                addEntry(event)
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}

    private fun addEntry(event: SensorEvent) {
        val data = mBinding.chart.data
        if (data != null) {
            var set = data.getDataSetByIndex(0)
            if (set == null) {
                set = createSet()
                data.addDataSet(set)
            }
            data.addEntry(Entry(set.entryCount.toFloat(), event.values[0] + 5), 0)
            data.notifyDataChanged()
            mBinding.chart.notifyDataSetChanged()
            mBinding.chart.setVisibleXRangeMaximum(150f)
            mBinding.chart.moveViewToX(data.entryCount.toFloat())

            mBinding.tickerView.text = data.entryCount.toFloat().toString()

        }
    }

    private fun createSet(): LineDataSet {
        val set = LineDataSet(null, "Dynamic Data")
        set.axisDependency = YAxis.AxisDependency.LEFT
        set.lineWidth = 3f
        set.color = Color.MAGENTA
        set.isHighlightEnabled = false
        set.setDrawValues(false)
        set.setDrawCircles(false)
        set.mode = LineDataSet.Mode.CUBIC_BEZIER
        set.cubicIntensity = 0.2f
        return set
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home)
            finish()
        return true
    }

}