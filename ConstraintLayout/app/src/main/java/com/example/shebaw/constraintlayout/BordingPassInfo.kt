package com.example.shebaw.constraintlayout

import java.sql.Timestamp
import java.util.concurrent.TimeUnit

data class BoardingPassInfo(val passengerName: String,
                            val flightCode: String,
                            val originCode: String,
                            val destCode: String,

                            val boardingTime: Timestamp,
                            val departureTime: Timestamp,
                            val arrivalTime: Timestamp,

                            val departureTerminal: String,
                            val departureGate: String,
                            val seatNumber: String,

                            val barCodeImageResource: Int) {
    fun getMinutesUntilBoarding(): Long {
        val millisUntilBoarding = boardingTime.time - System.currentTimeMillis()
        return TimeUnit.MILLISECONDS.toMinutes(millisUntilBoarding)
    }
}

