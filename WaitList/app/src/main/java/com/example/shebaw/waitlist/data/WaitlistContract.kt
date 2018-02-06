package com.example.shebaw.waitlist.data

import android.provider.BaseColumns

class WaitlistContract private constructor() {
    class Contacts : BaseColumns {
        companion object {
            const val TABLE_NAME = "waitlist"
            const val COLUMN_GUEST_NAME = "guestName"
            const val COLUMN_PARTY_SIZE = "partySize"
            const val COLUMN_TIMESTAMP = "timestamp"
        }
    }
}
