package com.example.stock.models

data class Stock(
    val symbol: String,
    val companyName: String,
    var currentPrice: Double,
    var priceChange: Double
)
