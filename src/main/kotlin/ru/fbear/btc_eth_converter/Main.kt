package ru.fbear.btc_eth_converter

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

}