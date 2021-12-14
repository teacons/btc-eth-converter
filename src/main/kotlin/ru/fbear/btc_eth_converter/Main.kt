package ru.fbear.btc_eth_converter

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.sun.net.httpserver.HttpServer
import java.io.File
import java.io.FileNotFoundException
import java.io.IOException
import java.net.BindException
import java.net.HttpURLConnection
import java.net.InetSocketAddress
import java.net.URL


const val api_url = "https://pro-api.coinmarketcap.com/v1/tools/price-conversion"

const val coin_from = "BTC"

const val coin_to = "ETH"


fun main(args: Array<String>) {

    val port = try {
        parseArgs(args)
    } catch (e: IllegalArgumentException) {
        if (e.message != null) println(e.message)
        println("Usage: java -jar btc-eth-converter.jar port")
        return
    }

    val apiKey =
        try {
            readApiKeyFromFile()
        } catch (e: FileNotFoundException) {
            println("File \"api_key.txt\" not found")
            return
        }

    val server = try {
        HttpServer.create(InetSocketAddress(port), 0)
    } catch (e: BindException) {
        println("Port is already in use")
        return
    }

    server.createContext("/api/convert") {
        try {
            val params = splitQuery(it.requestURI.query)

            if (params.size != 1 || (params.keys.contains("amount") && params["amount"]!!.toDoubleOrNull() == null))
                throw IllegalArgumentException("Wrong query")

            val amount = convertByApi(params["amount"]!!, apiKey)

            with(amount.toString()) {
                it.sendResponseHeaders(200, this.length.toLong())

                it.responseBody.apply {
                    write(this@with.toByteArray(Charsets.UTF_8))
                    flush()
                    close()
                }
            }
        } catch (e: IllegalArgumentException) {
            with(e.message) {
                if (this == null)
                    it.sendResponseHeaders(400, -1)
                else {
                    it.sendResponseHeaders(400, this.length.toLong())
                    it.responseBody.apply {
                        write(this@with.toByteArray(Charsets.UTF_8))
                        flush()
                        close()
                    }
                }
            }
        } catch (e: IOException) {
            with("Server API error") {
                it.sendResponseHeaders(500, this.length.toLong())
                it.responseBody.apply {
                    write(this@with.toByteArray(Charsets.UTF_8))
                    flush()
                    close()
                }
            }
        } finally {
            it.close()
        }
    }

    server.executor = null

    server.start()

}

fun parseArgs(args: Array<String>): Int {
    if (args.isEmpty()) throw IllegalArgumentException("Wrong args")
    with(args.first().toIntOrNull()) {
        if (args.size != 1) throw IllegalArgumentException("Wrong args")
        if (this == null) throw IllegalArgumentException("Wrong args")
        if (this !in 1..65535) throw IllegalArgumentException("Wrong port range")
        return this
    }
}

fun readApiKeyFromFile(fileName: String = "api_key.txt"): String {
    val key: String
    File(fileName).bufferedReader().use {
        key = it.readText()
    }
    return key
}

fun splitQuery(query: String?): Map<String, String> {
    val queryMap = mutableMapOf<String, String>()

    val params = query?.split('&') ?: emptyList()

    params.forEach {
        with(it.split('=')) {
            if (this.size != 2) throw IllegalArgumentException("Wrong query")
            queryMap[this.first()] = this.last()
        }
    }

    return queryMap
}

fun convertByApi(amount: String, apiKey: String): Double {
    val url = URL("$api_url?amount=$amount&symbol=$coin_from&convert=$coin_to")

    val con = url.openConnection() as HttpURLConnection

    con.setRequestProperty("accept", "application/json")

    con.setRequestProperty("X-CMC_PRO_API_KEY", apiKey)

    con.requestMethod = "GET"

    val response: Response = jacksonObjectMapper().readValue(con.inputStream)

    return response.data.quote.eth.price
}