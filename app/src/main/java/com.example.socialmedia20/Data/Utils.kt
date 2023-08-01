package com.example.socialmedia20.Data

import java.lang.Math.round

class Utils {

    companion object {
        private const val SECOND_MILLIS = 1000
        private const val MINUTE_MILLIS = 60 * SECOND_MILLIS
        private const val HOUR_MILLIS = 60 * MINUTE_MILLIS
        private const val DAY_MILLIS = 24 * HOUR_MILLIS
        private const val WEEK_MILLIS = 7 * DAY_MILLIS
        private const val K = 1000L
        private const val M = 1000000L

        fun formatCount(likeCount: Long): String {
            return when {
                likeCount >= M -> {
                    if (likeCount % M != 0L) {
                        val countK = likeCount.toFloat() / M
                        String.format("%.1fM", countK)
                    } else
                        (likeCount / M).toInt().toString()+"M"
                }
                likeCount >= K -> {
                    if (likeCount % K != 0L) {
                        val countK = likeCount.toFloat() / K
                        String.format("%.1fK", countK+100)
                    } else
                        ((100+likeCount / K).toInt()).toString()+"K"
                }
                else -> (likeCount+100).toString()
            }
        }

        fun getTimeAgo(time: Long): String? {
            val now: Long = System.currentTimeMillis()
            if (time > now || time <= 0) {
                return null
            }

            val diff = now - time
            return if (diff < MINUTE_MILLIS) {
                "just now"
            } else if (diff < 2 * MINUTE_MILLIS) {
                "a minute ago"
            } else if (diff < 50 * MINUTE_MILLIS) {
                (diff / MINUTE_MILLIS).toString() + " minutes ago"
            } else if (diff < 90 * MINUTE_MILLIS) {
                "an hour ago"
            } else if (diff < 24 * HOUR_MILLIS) {
                (diff / HOUR_MILLIS).toString() + " hours ago"
            } else if (diff < 48 * HOUR_MILLIS) {
                "yesterday"
            } else if (diff <= 7 * DAY_MILLIS) {
                (diff / DAY_MILLIS).toString() + " days ago"
            } else {
                (diff / WEEK_MILLIS).toString() + " weeks ago"
            }
        }


    }
}