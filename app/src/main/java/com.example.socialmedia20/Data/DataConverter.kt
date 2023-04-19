package com.example.socialmedia20.Data

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.util.Objects

class DataConverter {

    @TypeConverter
    fun fromArrayList(list : ArrayList<String?>): String {
        return Gson().toJson(list)
    }

    @TypeConverter
    fun fromString(dataString: String?): ArrayList<String> {
        val listType= object : TypeToken<ArrayList<String>>(){}.type
        return Gson().fromJson(dataString,listType)
    }

    @TypeConverter
    fun fromPost(user : User?): String {
        return Gson().toJson(user)
    }
    @TypeConverter
    fun fromStringToPst(dataString : String?): User {
        val listType= object : TypeToken<User>(){}.type
        return Gson().fromJson(dataString,listType)
    }
}
