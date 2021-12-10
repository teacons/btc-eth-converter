package ru.fbear.btc_eth_converter

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty

@JsonIgnoreProperties(ignoreUnknown = true)
data class Response(
    val data: Data
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class Data(
    val quote: Quote
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class Quote(
    @JsonProperty("ETH") val eth: ETH
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class ETH(
    val price: Double
)