package com.chelz.policesimulation

import java.io.Serializable
import java.util.LinkedList
import java.util.Queue
import kotlin.math.max
import kotlin.math.pow
import kotlin.math.round
import kotlin.random.Random

class Model(
	private val delay: Int = 5,
	private val basePopulationGrowth: Int,
	private val basePopulationDecline: Int,
	private val murder: Double,
	private val crimeCommittingProbability: Double,
	private val policeReportReaction: Int,
	private val income: Double,
	private val education: Double,
	private val healthcare: Double,
	private val environment: Double,
	private val payoff: Double,
	initBribe: Double = 0.5,
	initPoliceEfficiency: Double = 0.5,
) : Serializable {

	private val n = 0.05
	var iteration = 0

	//var population = basePopulationGrowth * 10000
	var population = 500000
	private var bribe = initBribe
	var policeEfficiency = initPoliceEfficiency

	private var bribeDelay: Queue<Double> = LinkedList()
	private var policeEfficiencyDelay: Queue<Double> = LinkedList()

	init {
		bribeDelay.add(bribe)
		bribeDelay.add(bribe)
		bribeDelay.add(bribe)
		policeEfficiencyDelay.add(policeEfficiency)
		policeEfficiencyDelay.add(policeEfficiency)
		policeEfficiencyDelay.add(policeEfficiency)
		policeEfficiencyDelay.add(policeEfficiency)
	}

	var trust = delayedPoliceEfficiency()
	private var reportLevel = f7()

	private var crimeDisclosure = f4()
	var lifeLevel = f5()
	var crimeCount = f2()
	private var populationDecline = f3()

	fun tick() {
		++iteration
		lifeLevel = f5()
		crimeCount = f2()
		populationDecline = f3()
		population += basePopulationGrowth - populationDecline
		crimeDisclosure = f4()
		trust = delayedPoliceEfficiency()
		reportLevel = f7()
		bribe = f6()
		bribeDelay.add(bribe)
		policeEfficiency = f1()
		policeEfficiencyDelay.add(policeEfficiency)
	}

	private fun delayedPoliceEfficiency(): Double {
		return policeEfficiencyDelay.poll()
	}

	private fun delayedBribe(): Double {
		return bribeDelay.poll()
	}

	private fun f1(): Double {
		return ((1.0 - (policeReportReaction / (policeReportReaction + delay))) + reportLevel + crimeDisclosure) / 3.0
	}

	private fun f2(): Int {
		return round((population * Random.nextDouble(0.8, 1.2) * crimeCommittingProbability) / (lifeLevel + policeEfficiency)).toInt()
	}

	private fun f3(): Int {
		return (basePopulationDecline + round(murder * crimeCount).toInt())
	}

	private fun f4(): Double {
		return (1 - Random.nextDouble(0.9, 1.1) * bribe * payoff)
	}

	private fun f5(): Double {
		return ((1.0 - delayedBribe()) + income + education + healthcare + environment) / 6.0
	}

	private fun f6(): Double {
		return max(bribe - (policeEfficiency - n).pow(3) / 5.0, 0.01)
	}

	private fun f7(): Double {
		return Random.nextDouble(0.9, 1.1) * trust
	}
}