package com.hansung.capstone

import android.content.Context
import android.content.SharedPreferences

class Preference(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("other2", 0)

    fun getString(key:String,defValue:String): String {


        return prefs.getString(key,defValue).toString()
    }
    fun setString(key:String,str:String){
        prefs.edit().putString(key,str).apply()
    }
    fun remove(){
        prefs.edit().remove("id").apply()
        prefs.edit().remove("nickname").apply()
    }
    /*var token:String?
        get() = prefs.getString("token",null)
        set(value){
            prefs.edit().putString("token",value).apply()
        }*/
}