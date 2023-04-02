package com.chelz.policesimulation

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.chelz.policesimulation.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

	private lateinit var binding: ActivityMainBinding
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		binding = ActivityMainBinding.inflate(layoutInflater)
		setContentView(binding.root)


		binding.confirmButton.setOnClickListener {

			val populationGrowth = binding.edPopulationGrowth.text.toString().toInt()
			val populationDecline = binding.edPopulationDecline.text.toString().toInt()
			val murderRate = binding.edKillerPercent.text.toString().toDouble()
			val crimeProb = binding.edCrimeCoef.text.toString().toDouble()
			val reportReaction = binding.edReactionDayTime.text.toString().toInt()
			val income = binding.edIncome.text.toString().toDouble()
			val education = binding.edEducation.text.toString().toDouble()
			val healthcare = binding.edHealthcare.text.toString().toDouble()
			val environment = binding.edEnvironmentQuality.text.toString().toDouble()
			val payoff = binding.edPayoff.text.toString().toDouble()

			var flag = true
			binding.lPopulationGrowth.error = if (populationGrowth <= 0) {
				flag = false
				"Прирост должен быть положительный"
			} else {
				null
			}
			binding.lPopulationDecline.error = if (populationDecline <= 0) {
				flag = false
				"Убыль должна быть положительной"
			} else {
				null
			}

			binding.lKillerPercent.error = if (murderRate !in 0.0..1.0) {
				flag = false

				"Диапазон значений [0,1]"
			} else {
				null
			}

			binding.lCrimeCoef.error = if (crimeProb !in 0.0..1.0) {
				flag = false

				"Диапазон значений [0,1]"
			} else {
				null
			}

			binding.lReactionDayTime.error = if (reportReaction <= 0) {
				flag = false

				"Реакция должна быть положительной"
			} else {
				null
			}

			binding.lIncome.error = if (income !in 0.0..1.0) {
				flag = false
				"Диапазон значений [0,1]"
			} else {
				null
			}

			binding.lEducation.error = if (education !in 0.0..1.0) {
				flag = false
				"Диапазон значений [0,1]"
			} else {
				null
			}

			binding.lEnvironmentQuality.error = if (environment !in 0.0..1.0) {
				flag = false
				"Диапазон значений [0,1]"
			} else {
				null
			}

			binding.lHealthcare.error = if (healthcare !in 0.0..1.0) {
				flag = false
				"Диапазон значений [0,1]"
			} else {
				null
			}

			binding.lPayoff.error = if (payoff !in 0.0..1.0) {
				flag = false
				"Диапазон значений [0,1]"
			} else {
				null
			}

			if (flag) {
				val model = Model(
					delay = 5,
					basePopulationGrowth = populationGrowth,
					basePopulationDecline = populationDecline,
					murder = murderRate,
					crimeCommittingProbability = crimeProb,
					policeReportReaction = reportReaction,
					income = income,
					education = education,
					healthcare = healthcare,
					environment = environment,
					payoff = payoff)

				intent = Intent(this, PlotActivity::class.java).putExtra("model", model)
				startActivity(intent)
			}

		}
	}
}