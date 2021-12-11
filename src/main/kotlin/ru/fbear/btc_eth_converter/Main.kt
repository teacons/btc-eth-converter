package ru.fbear.btc_eth_converter

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.sun.net.httpserver.HttpServer
import java.io.File
import java.io.FileNotFoundException
import java.net.HttpURLConnection
import java.net.InetSocketAddress
import java.net.URL


const val api_url = "https://pro-api.coinmarketcap.com/v1/tools/price-conversion"

const val coin_from = "BTC"

const val coin_to = "ETH"

var apiKey = ""


fun main(args: Array<String>) {
    if (args.isEmpty()) {
        println("Usage: java -jar btc-eth-converter.jar port")
        return
    }

    val port = args[0].toIntOrNull()

    if (port == null) {
        println("Usage: java -jar btc-eth-converter.jar port")
        return
    }

    apiKey =
        try {
            File("api_key.txt").bufferedReader().readText()
        } catch (e: FileNotFoundException) {
            println("File \"api_key.txt\" not found")
            return
        }

    val server = HttpServer.create(InetSocketAddress(port), 0)

    server.createContext("/api/convert") {
        val params =
            if (it.requestURI.query != null)
                it.requestURI.query.split('&')
            else emptyList()

        if (params.size != 1) {
            it.sendResponseHeaders(400, -1)
            it.close()
            return@createContext
        }

        val param = params.first()

        val paramName = param.split('=').first()

        if (paramName != "amount") {
            it.sendResponseHeaders(400, -1)
            it.close()
            return@createContext
        }

        val paramValue = param.split('=').last()

        val amount = convertByApi(paramValue)

        val respText = amount.toString()

        it.sendResponseHeaders(200, respText.length.toLong())

        it.responseBody.apply {
            write(respText.toByteArray(Charsets.UTF_8))
            flush()
        }
    }

    server.executor = null

    server.start()


}

fun convertByApi(amount: String): Double {
    val url = URL("$api_url?amount=$amount&symbol=$coin_from&convert=$coin_to")

    val con = url.openConnection() as HttpURLConnection

    con.setRequestProperty("accept", "application/json")

    con.setRequestProperty("X-CMC_PRO_API_KEY", apiKey)

    con.requestMethod = "GET"

    val response: Response = jacksonObjectMapper().readValue(con.inputStream)

    return response.data.quote.eth.price
}