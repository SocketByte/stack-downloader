package pl.socketbyte.stackoffline

class TimeUtil {

    private fun appendTimeAndUnit(timeBuf: StringBuffer, time: Long, unit: String) {
        if (time < 1) {
            return
        }

        timeBuf.append(time)
        timeBuf.append(unit)
    }

    private fun prependTimeAndUnit(timeBuf: StringBuffer, time: Long, unit: String) {
        if (time < 1) {
            return
        }

        if (timeBuf.isNotEmpty()) {
            timeBuf.insert(0, " ")
        }

        timeBuf.insert(0, unit)
        timeBuf.insert(0, time)
    }

    /**
     * Provide the Millisecond time value in {year}y {day}d {hour}h {minute}m {second}s {millisecond}ms. <br></br>
     * Omitted if there is no value for that unit.
     *
     * @param timeInMillis
     * @return
     *
     * @since 2018. 1. 9.
     */
    fun toYYYYHHmmssS(timeInMillis: Long): String {

        if (timeInMillis < 1) {
            return "0 ms"
        }

        val timeBuf = StringBuffer()

        val millis = timeInMillis % 1000
        //appendTimeAndUnit(timeBuf, millis, "ms")

        // second (1000ms) & above
        var time = timeInMillis / 1000
        if (time < 1) {
            return timeBuf.toString()
        }

        val seconds = time % 60
        prependTimeAndUnit(timeBuf, seconds, "s")

        // minute(60s) & above
        time /= 60
        if (time < 1) {
            return timeBuf.toString()
        }

        val minutes = time % 60
        prependTimeAndUnit(timeBuf, minutes, "m")

        // hour(60m) & above
        time /= 60
        if (time < 1) {
            return timeBuf.toString()
        }

        val hours = time % 24
        prependTimeAndUnit(timeBuf, hours, "h")

        // day(24h) & above
        time /= 24
        if (time < 1) {
            return timeBuf.toString()
        }

        val day = time % 365
        prependTimeAndUnit(timeBuf, day, "d")

        // year(365d) ...
        time /= 365
        if (time < 1) {
            return timeBuf.toString()
        }

        prependTimeAndUnit(timeBuf, time, "y")

        return timeBuf.toString()
    }
}