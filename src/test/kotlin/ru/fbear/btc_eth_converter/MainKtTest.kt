package ru.fbear.btc_eth_converter

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.io.File
import java.io.FileNotFoundException
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL
import kotlin.test.assertFailsWith
import kotlin.test.assertIs

internal class MainKtTest {

    @Test
    fun parseArgsTest() {
        val args1 = arrayOf("8000")
        val args2 = arrayOf("-1")
        val args3 = arrayOf("800000")
        val args4 = arrayOf("test", "test")
        val args5 = arrayOf("test")
        val args6 = emptyArray<String>()

        assertEquals(8000, parseArgs(args1))
        assertFailsWith(IllegalArgumentException::class, "Wrong args") { parseArgs(args2) }
        assertFailsWith(IllegalArgumentException::class, "Wrong port range") { parseArgs(args3) }
        assertFailsWith(IllegalArgumentException::class, "Wrong args") { parseArgs(args4) }
        assertFailsWith(IllegalArgumentException::class, "Wrong args") { parseArgs(args5) }
        assertFailsWith(IllegalArgumentException::class, "Wrong args") { parseArgs(args6) }
    }

    @Test
    fun readApiKeyFromFileTest() {
        val expected1 = "test"
        val expected2 = "testTEST"
        val fileName1 = "api_key_test.txt"
        val fileName2 = "test.txt"

        File(fileName1).apply {
            createNewFile()
            bufferedWriter().use {
                it.write(expected1)
            }
        }.deleteOnExit()
        File(fileName2).apply {
            createNewFile()
            bufferedWriter().use {
                it.write(expected2)
            }
        }.deleteOnExit()
        assertEquals(expected1, readApiKeyFromFile(fileName1))
        assertEquals(expected2, readApiKeyFromFile(fileName2))
        assertFailsWith<FileNotFoundException> { readApiKeyFromFile("FileNotFoundException") }
    }

    @Test
    fun splitQueryTest() {
        val query1 = null
        val query2 = "amount=15"
        val query3 = "amount=15&test=1"
        val query4 = "test=1&1=1"
        val query5 = "test=1&"
        val query6 = "amount=15.5"
        val query7 = "amount==test"

        val expected1 = emptyMap<String, String>()
        val expected2 = mapOf("amount" to "15")
        val expected3 = mapOf("amount" to "15", "test" to "1")
        val expected4 = mapOf("test" to "1", "1" to "1")
        val expected6 = mapOf("amount" to "15.5")


        assertEquals(expected1, splitQuery(query1))
        assertEquals(expected2, splitQuery(query2))
        assertEquals(expected3, splitQuery(query3))
        assertEquals(expected4, splitQuery(query4))
        assertFailsWith<IllegalArgumentException>("Wrong query") { splitQuery(query5) }
        assertEquals(expected6, splitQuery(query6))
        assertFailsWith<IllegalArgumentException>("Wrong query") { splitQuery(query7) }

    }

    @Test
    fun convertByApiTest() {
        val wrongApiKey = "test"
        val amount1 = "5"
        val amount2 = "8000"
        val apiKey = readApiKeyFromFile()

        assertFailsWith<IOException> { convertByApi(amount1, wrongApiKey) }
        assertIs<Double>(convertByApi(amount1, apiKey))
        assertIs<Double>(convertByApi(amount2, apiKey))
    }

    @Test
    fun mainTest() {
        main(arrayOf("8000"))

        var url = URL("http://localhost:8000/api/convert?amount=50")

        var con = url.openConnection() as HttpURLConnection

        assertEquals(200, con.responseCode)

        assertIs<Double>(con.inputStream.bufferedReader().readText().toDoubleOrNull())

        url = URL("http://localhost:8000/api/convert?amount=")

        con = url.openConnection() as HttpURLConnection

        assertEquals(400, con.responseCode)

    }
}