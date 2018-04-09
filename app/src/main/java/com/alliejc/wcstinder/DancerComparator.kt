package com.alliejc.wcstinder

import com.alliejc.wcstinder.trackmyswing.Dancer


class DancerComparator : Comparator<Dancer> {
    override fun compare(o1: Dancer?, o2: Dancer?): Int {
        return if (o1?.relevance === o2?.relevance) {
            0
        } else if (o1!!.relevance > o2!!.relevance) {
            1
        } else {
            -1
        }
    }
}