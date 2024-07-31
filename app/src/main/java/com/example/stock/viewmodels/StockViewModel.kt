package com.example.stock.viewmodels

import android.util.Log
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModel
import com.example.stock.repositories.StockRepository
import com.example.stock.models.Stock
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class StockViewModel @Inject constructor(
    private val repository: StockRepository
) : ViewModel(), DefaultLifecycleObserver {
    private val TAG: String = "StockViewModel"
    val stocks: StateFlow<List<Stock>> = repository.stocks

    override fun onStart(owner: LifecycleOwner) {
        super.onStart(owner)
        Log.d(TAG,  "startSimulation")
        repository.startSimulation()
    }

    override fun onStop(owner: LifecycleOwner) {
        super.onStop(owner)
        Log.d(TAG,  "stopSimulation")
        repository.stopSimulation()
    }
}