package com.example.shebaw.constrainglayout.utilities

/*
* Copyright (C) 2016 The Android Open Source Project
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*      http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/

import com.example.shebaw.constraintlayout.BoardingPassInfo
import com.example.shebaw.constraintlayout.R
import java.sql.Timestamp
import java.util.concurrent.TimeUnit

/**
 * This class is used to generate fake data that will be displayed in the boarding pass layout
 */
object FakeDataUtils {

    /**
     * Generates fake boarding pass data to be displayed.
     * @return fake boarding pass data
     */
    fun generateFakeBoardingPassInfo(): BoardingPassInfo {
        val now = System.currentTimeMillis()

        // Anything from 0 minutes up to (but not including) 30 minutes
        val randomMinutesUntilBoarding = (Math.random() * 30).toLong()
        // Standard 40 minute boarding time
        val totalBoardingMinutes: Long = 40
        // Anything from 1 hours up to (but not including) 6 hours
        val randomFlightLengthHours = (Math.random() * 5 + 1).toLong()

        val boardingMillis = now + minutesToMillis(randomMinutesUntilBoarding)
        val departure = boardingMillis + minutesToMillis(totalBoardingMinutes)
        val arrival = departure + hoursToMillis(randomFlightLengthHours)

        return BoardingPassInfo(
                "MR. ABDULKERIM",
                "FHVY 09",
                "ERTH",
                "MRS",
                Timestamp(boardingMillis),
                Timestamp(departure),
                Timestamp(arrival),
         "3A",
         "33C",
         "1A",
         //R.drawable.art_plane)
                42)
    }

    private fun minutesToMillis(minutes: Long): Long {
        return TimeUnit.MINUTES.toMillis(minutes)
    }

    private fun hoursToMillis(hours: Long): Long {
        return TimeUnit.HOURS.toMillis(hours)
    }
}