package com.noble.sync

import java.util.Calendar

class Constants {
    companion object {
        val EMAIL_REGEX = Regex("[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}")
        val MIN_YEAR = Calendar.getInstance().get(Calendar.YEAR) - 14
        val MAX_YEAR = Calendar.getInstance().get(Calendar.YEAR) - 55
    }
}