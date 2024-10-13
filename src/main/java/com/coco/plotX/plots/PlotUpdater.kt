package com.coco.plotX.plots

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class PlotUpdater {
    private val scope = CoroutineScope(Dispatchers.Default)

    fun startUpdating() {
        scope.launch {
            while (true) {
                delay(1000)
            }
        }
    }
}