package ru.fbear.btc_eth_converter

import java.io.File
import java.io.FileNotFoundException

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
}