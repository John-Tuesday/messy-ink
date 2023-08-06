package org.calamarfederal.messyink.common.presentation.format

import androidx.compose.ui.test.junit4.createComposeRule
import kotlinx.datetime.LocalDateTime
import org.junit.Rule
import org.junit.Test

class DateTimeFormatUnitTest {
    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun `Empty formatters return empty strings`() {
        val testDateTime = LocalDateTime(
            year = 2023,
            monthNumber = 4,
            dayOfMonth = 3,
            hour = 5,
            minute = 6,
        )

        var result = testDateTime.date.formatToString(DateFormat.Empty)
        assert("" == result) { "result = \"$result\"" }

        result = testDateTime.time.formatToString(TimeFormat.Empty)
        assert("" == result) { "result = \"$result\"" }

        result = testDateTime.formatToString(dateTimeFormat = DateTimeFormat.Empty)
        assert("" == result) { "result = \"$result\"" }
    }

    @Test
    fun `Empty time or date formatter is equal to just the non empty time or date`() {
        val testDateTime = LocalDateTime(
            year = 2023,
            monthNumber = 4,
            dayOfMonth = 3,
            hour = 5,
            minute = 6,
        )

        val dateFormat = DateFormat(
            year = YearFormat.FullDigit,
            month = MonthFormat.FullName,
            day = DayFormat.MinDigit
        )
        var expect = testDateTime.date.formatToString(dateFormat)
        var result = testDateTime.formatToString(
            DateTimeFormat(
                dateFormat = dateFormat,
                timeFormat = TimeFormat.Empty
            )
        )
        assert(result == expect) {
            "expected = \"$expect\"\nresult = \"$result\""
        }

        val timeFormat = TimeFormat(
            hour = HourFormat.MinDigit,
            minute = MinuteFormat.TwoDigit,
            second = SecondFormat.Omit,
            clockFormat = ClockFormat.AMPMLower,
        )
        expect = testDateTime.time.formatToString(timeFormat)
        result = testDateTime.formatToString(
            DateTimeFormat(
                dateFormat = DateFormat.Empty,
                timeFormat = timeFormat,
            )
        )
        assert(result == expect) {
            "expected = \"$expect\"\nresult = \"$result\""
        }
    }
}
