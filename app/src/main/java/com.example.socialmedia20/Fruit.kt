package com.example.recycleview

import androidx.core.location.LocationRequestCompat
import kotlin.concurrent.fixedRateTimer
import kotlin.random.Random

data class Fruit(
    val name:String,
    val origin: String,
    val quanity: Int
) {
    companion object{
        @JvmField
        val Fruit_Name= arrayOf(
            "Banana", "Pineapple",
            "Peach","Avocado",
            "Melon","Lemon",
            "Lime","Orange")
        @JvmField
        val Origins= arrayOf(
            "Lucknow","Nagpur",
            "Srinagar","Malda",
            "Kolkata","Siliguri",
            "Bidhan Nagar","Haripur")
        @JvmStatic
        fun getRandomFruits(n:Int):ArrayList<Fruit>{
            val fruitArray=ArrayList<Fruit>(n)
            for (i in 1..n)
            {
                fruitArray.add(
                    Fruit(
                        Fruit_Name[Random.nextInt(8)],
                        Origins[Random.nextInt(8)],
                        Random.nextInt(10)*100
                    )
                )
            }
            return fruitArray
        }
    }
}