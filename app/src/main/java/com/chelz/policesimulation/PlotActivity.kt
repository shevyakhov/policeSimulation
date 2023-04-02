package com.chelz.policesimulation

import android.graphics.Color
import android.os.Bundle
import android.widget.TableLayout
import android.widget.TableRow
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.chelz.policesimulation.databinding.ActivityPlotBinding
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.math.RoundingMode
import java.text.DecimalFormat
import java.util.Random

class PlotActivity : AppCompatActivity() {

	val df = DecimalFormat("#.##")
	private lateinit var chart: LineChart
	private lateinit var model: Model
	private lateinit var binding: ActivityPlotBinding
	private lateinit var job: Job
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		df.roundingMode = RoundingMode.FLOOR
		binding = ActivityPlotBinding.inflate(layoutInflater)
		setContentView(binding.root)
		chart = binding.chart
		model = intent.getSerializableExtra("model") as Model
		var xAxis: XAxis
		run {
			xAxis = binding.chart.xAxis
			xAxis.mAxisMinimum = 0f
			xAxis.enableGridDashedLine(10f, 10f, 0f)
		}
		job = Job()
		var isActive = false
		binding.start.setOnClickListener {
			if (!isActive) {
				isActive = true
				CoroutineScope(Dispatchers.IO).launch {
					tick()
				}
			} else {
				isActive = false
				job.cancel()
			}
		}

	}

	private suspend fun tick() = coroutineScope {
		val delay = binding.edDelay.text.toString().toLong()
		val month = binding.edMonth.text.toString().toInt()
		job = launch {
			initialBind()
			for (i in 0..month) {
				CoroutineScope(Dispatchers.Main).launch {
					model.tick()
					chart.nextMonth()
					addRow(
						model.iteration,
						model.trust,
						model.policeEfficiency,
						model.lifeLevel
					)
				}.join()
				delay(delay)
			}
		}
	}

	private fun LineChart.nextMonth() {

		this.data.dataSets.first().addEntry(Entry(model.iteration.toFloat(), model.population.toFloat()))
		this.data.dataSets.last().addEntry(Entry(model.iteration.toFloat(), model.crimeCount.toFloat()))
		this.data = LineData(this.data.dataSets)
		animateXY(0, 0)
	}

	private fun initialBind() {
		val random = Random()
		val peopleDataSet = LineDataSet(mutableListOf(Entry(model.iteration.toFloat(), model.population.toFloat())), "Кол-во людей").apply {
			val color: Int = Color.argb(255, random.nextInt(256), random.nextInt(256), random.nextInt(256))
			this.color = color
			setCircleColor(color)
			circleRadius = 1f
			lineWidth = 3F
		}
		val crimeDataSet = LineDataSet(mutableListOf(Entry(model.iteration.toFloat(), model.crimeCount.toFloat())), "Кол-во преступлений").apply {
			val color: Int = Color.argb(255, random.nextInt(256), random.nextInt(256), random.nextInt(256))
			this.color = color
			setCircleColor(color)
			circleRadius = 1f
			lineWidth = 3F
		}
		binding.chart.data = LineData(listOf(peopleDataSet, crimeDataSet))
		CoroutineScope(Dispatchers.Main).launch {
			addRow(
				model.iteration,
				model.trust,
				model.policeEfficiency,
				model.lifeLevel
			)
		}
	}

	private fun addRow(_num: Int, _trust: Double, _efficiency: Double, _lifeLevel: Double) {

		val tl = binding.table
		val tr = TableRow(this)
		tr.layoutParams = TableLayout.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT)

		val num = TextView(this)
		val trust = TextView(this)
		val efficiency = TextView(this)
		val lifeLevel = TextView(this)
		num.text = df.format(_num)
		trust.text = df.format(_trust)
		efficiency.text = df.format(_efficiency)
		lifeLevel.text = df.format(_lifeLevel)
		tr.addView(num)
		tr.addView(trust)
		tr.addView(efficiency)
		tr.addView(lifeLevel)
		tl.addView(tr, TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT, TableLayout.LayoutParams.WRAP_CONTENT))
	}
}