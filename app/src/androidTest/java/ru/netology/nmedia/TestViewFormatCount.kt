package ru.netology.nmedia

import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class TestViewFormatCount {

    @Test
    fun testSimpleNumber() {
        val count = 999
        val assertValue = formatCount(count)
        val expectedValue = "999"
        Assert.assertEquals(expectedValue, assertValue)
    }

    @Test
    fun testSimpleThousand() {
        val count = 1_000
        val assertValue = formatCount(count)
        val expectedValue = "1K"
        Assert.assertEquals(expectedValue, assertValue)
    }

    @Test
    fun testThousandAndSomeDigitValue() {
        val count = 1_050
        val assertValue = formatCount(count)
        val expectedValue = "1K"
        Assert.assertEquals(expectedValue, assertValue)
    }

    @Test
    fun testThousandAndHundredValue() {
        val count = 1_100
        val assertValue = formatCount(count)
        val expectedValue = "1.1K"
        Assert.assertEquals(expectedValue, assertValue)
    }

    @Test
    fun testThousandAndHundredSomeDigitValue() {
        val count = 1_150
        val assertValue = formatCount(count)
        val expectedValue = "1.1K"
        Assert.assertEquals(expectedValue, assertValue)
    }

    @Test
    fun testTenThousandValue() {
        val count = 10_000
        val assertValue = formatCount(count)
        val expectedValue = "10K"
        Assert.assertEquals(expectedValue, assertValue)
    }

    @Test
    fun testMillionValue() {
        val count = 1_000_000
        val assertValue = formatCount(count)
        val expectedValue = "1M"
        Assert.assertEquals(expectedValue, assertValue)
    }

    @Test
    fun testMillionAndHundredValue() {
        val count = 1_100_000
        val assertValue = formatCount(count)
        val expectedValue = "1.1M"
        Assert.assertEquals(expectedValue, assertValue)
    }
}