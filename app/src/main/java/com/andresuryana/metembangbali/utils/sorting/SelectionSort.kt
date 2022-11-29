package com.andresuryana.metembangbali.utils.sorting

import com.andresuryana.metembangbali.data.model.Tembang
import com.andresuryana.metembangbali.utils.Ext.toTimeInMillis
import com.andresuryana.metembangbali.utils.sorting.SelectionSort.Method.ASC
import com.andresuryana.metembangbali.utils.sorting.SelectionSort.Method.DESC

/**
 * Selection Sort Algorithm
 */
class SelectionSort(private val list: ArrayList<Tembang>) {

    fun sortByTitle(method: Method): ArrayList<Tembang> {
        // First boundary loop
        for (i in 0 until list.size) {
            // Set current title index & element
            var currentIndex = i
            var currentTitle = list[i].title

            // Second boundary loop
            for (j in i + 1 until (list.size)) {
                // Compare string title
                when (method) {
                    ASC -> {
                        if (list[j].title < currentTitle) { /* ASC */
                            // If first string is smaller than second string (currentTitle),
                            // then update currentTitle to current element in second boundary loop
                            currentIndex = j
                            currentTitle = list[j].title
                        }
                    }
                    DESC -> {
                        if (list[j].title > currentTitle) { /* DESC */
                            // If first string is greater than second string (currentTitle),
                            // then update currentTitle to current element in second boundary loop
                            currentIndex = j
                            currentTitle = list[j].title
                        }
                    }
                }

                // Swap currentTitle that has been found,
                // then swap it if currentIndex not same as current iterator (i)
                if (currentIndex != i) {
                    val temp = list[currentIndex]
                    list[currentIndex] = list[i]
                    list[i] = temp
                }
            }
        }

        return list
    }

    fun sortByDate(method: Method): ArrayList<Tembang> {
        // First boundary loop
        for (i in 0 until (list.size)) {
            // Set current title index & element
            var currentIndex = i
            var currentDate = list[i].createdAt.toTimeInMillis()

            // Second boundary loop
            for (j in i + 1 until (list.size)) {
                // Compare string createdAt?.time?.toTimeInMillis()
                when (method) {
                    ASC -> {
                        if (list[j].createdAt.toTimeInMillis() < currentDate) { /* ASC */
                            // If first string is smaller than second string (currentDate),
                            // then update currentDate to current element in second boundary loop
                            currentIndex = j
                            currentDate = list[j].createdAt.toTimeInMillis()
                        }
                    }
                    DESC -> {
                        if (list[j].createdAt.toTimeInMillis() > currentDate) { /* DESC */
                            // If first string is greater than second string (currentDate),
                            // then update currentDate to current element in second boundary loop
                            currentIndex = j
                            currentDate = list[j].createdAt.toTimeInMillis()
                        }
                    }
                }

                // Swap currentTitle that has been found,
                // then swap it if currentIndex not same as current iterator (i)
                if (currentIndex != i) {
                    val temp = list[currentIndex]
                    list[currentIndex] = list[i]
                    list[i] = temp
                }
            }
        }

        return list
    }

    enum class Method {
        ASC,
        DESC
    }
}