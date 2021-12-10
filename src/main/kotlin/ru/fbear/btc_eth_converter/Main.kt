package ru.fbear.btc_eth_converter

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import java.io.File
import java.io.FileNotFoundException
import java.net.HttpURLConnection
import java.net.URL


const val api_url = "https://pro-api.coinmarketcap.com/v1/tools/price-conversion"

const val coin_from = "BTC"

const val coin_to = "ETH"


fun main(args: Array<String>) {
    if (args.isEmpty()) {
        println("Usage: java -jar btc-eth-converter.jar number_of_coins")
        return
    }

    val numOfCoin = args[0].toIntOrNull()

    if (numOfCoin == null) {
        println("Usage: java -jar btc-eth-converter.jar number_of_coins")
        return
    }

    val apiKey =
        try {
            File("api_key.txt").bufferedReader().readText()
        } catch (e: FileNotFoundException) {
            println("File \"api_key.txt\" not found")
            return
        }

    val url = URL("$api_url?amount=$numOfCoin&symbol=$coin_from&convert=$coin_to")

    val con = url.openConnection() as HttpURLConnection

    con.setRequestProperty("accept", "application/json")

    con.setRequestProperty("X-CMC_PRO_API_KEY", apiKey)

    con.requestMethod = "GET"

    val response: Response = jacksonObjectMapper().readValue(con.inputStream)

    println("Price: ${response.data.quote.eth.price}")
}