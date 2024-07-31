package com.example.stock.repositories

import com.example.stock.models.Stock
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class StockRepository @Inject constructor() {
    private val _stocks = MutableStateFlow<List<Stock>>(emptyList())
    val stocks: StateFlow<List<Stock>> = _stocks
    private var simulationJob: Job? = null
    init {
        loadDummyData()
    }

    private fun loadDummyData() {
        _stocks.value = listOf(
            Stock("VNM", "VanEck Vectors Vietnam ETF", 13.0, 0.0),
            Stock("Dow Jones", "Dow Jones Industrial Average", 60.0, 0.0),
            Stock("NKE", "NIKE, Inc.", 95.0, 0.0),
            Stock("AAPL", "Apple Inc.", 150.0, 0.0),
            Stock("SBUX", "Starbucks Corporation", 110.0, 0.0),
            Stock("BHP", "BHP Billiton Limited", 55.0, 0.0),
            Stock("THC","Tenet Healthcare Corporation", 60.0, 0.0),
        )
    }

    fun startSimulation() {
        if (simulationJob?.isActive == true) return
        simulationJob = CoroutineScope(Dispatchers.IO).launch {
            while (true) {
                delay(1000)
                _stocks.value = _stocks.value.map { stock ->
                    val change = (stock.currentPrice * (0.1 * (0.5 - Math.random()))).coerceIn(-stock.currentPrice * 0.1, stock.currentPrice * 0.1)
                    val newPrice = stock.currentPrice + change
                    stock.copy(
                        currentPrice = String.format(Locale.US, "%.2f", newPrice).toDouble(),
                        priceChange = String.format(Locale.US, "%.2f", change).toDouble()
                    )
                }
            }
        }
    }
    fun stopSimulation() {
        simulationJob?.cancel()
        simulationJob = null
    }
}