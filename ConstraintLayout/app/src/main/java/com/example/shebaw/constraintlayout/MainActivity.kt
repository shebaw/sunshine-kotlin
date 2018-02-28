package com.example.shebaw.constraintlayout

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.example.shebaw.constrainglayout.utilities.FakeDataUtils
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.flight_info.*
import kotlinx.android.synthetic.main.boarding_info.*
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Load a BordingPassInfo object with fake data using FakeDataUtils
        val fakeBordingInfo = FakeDataUtils.generateFakeBoardingPassInfo()
        displayBordingPassInfo(fakeBordingInfo)
    }

    private fun displayBordingPassInfo(info: BoardingPassInfo) {
        textViewPassengerName.text = info.passengerName
        textViewOriginAirport.text = info.originCode
        textViewFlightCode.text = info.flightCode
        textViewDestinationAirport.text = info.destCode

        val formatter = SimpleDateFormat(getString(R.string.timeFormat), Locale.getDefault())
        textViewBoardingTime.text = formatter.format(info.boardingTime)
        textViewDepartureTime.text = formatter.format(info.departureTime)
        textViewArrivalTime.text = formatter.format(info.arrivalTime)

        val totalMinutesUntilBoarding = info.getMinutesUntilBoarding()
        val hoursUntilBoarding = TimeUnit.MINUTES.toHours(totalMinutesUntilBoarding)
        val minutesLessHoursUntilBoarding =
                totalMinutesUntilBoarding - TimeUnit.HOURS.toMinutes(hoursUntilBoarding)

        val hoursAndMinutesUntilBoarding = getString(R.string.countDownFormat,
                hoursUntilBoarding,
                minutesLessHoursUntilBoarding)

        textViewBoardingInCountdown.text = hoursAndMinutesUntilBoarding
        textViewTerminal.text = info.departureTerminal
        textViewGate.text = info.departureGate
        textViewSeat.text = info.seatNumber
    }
}
