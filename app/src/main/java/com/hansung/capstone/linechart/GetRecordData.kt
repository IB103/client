package com.hansung.capstone.linechart

import android.util.Log
import com.hansung.capstone.MainActivity
import com.hansung.capstone.MyApplication
import com.hansung.capstone.Token
import com.hansung.capstone.retrofit.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class GetRecordData {
    var service=RetrofitService.create()
    internal fun getRidingData(int:Int,callback: (result: MutableList<RidingData>) -> Unit){

        val accessToken= MyApplication.prefs.getString("accessToken", "")

            val user_id=MyApplication.prefs.getLong("userId",0)
            service.getRecord(accessToken = "Bearer $accessToken",user_id ,int.toLong()).enqueue(object:Callback<RepGetRecord>{
                override fun onResponse(call: Call<RepGetRecord>, response: Response<RepGetRecord>) {
                    if(response.code()==200){
                        val result:RepGetRecord=response.body()!!
                        Log.d("result","${result.data}")
                        callback(result.data as MutableList<RidingData>)
                        // setData(result.data)
                    }
                }
                override fun onFailure(call: Call<RepGetRecord>, t: Throwable) {
                    Log.d("fail","${t.message}")
                    callback(emptyList<RidingData>().toMutableList())
                }
            })
    }
    fun getRankData(callback: (result: MutableList<RankData>) -> Unit){

            val accessToken=MyApplication.prefs.getString("accessToken","")
        Log.d("accessToken","$accessToken")
            service.getRank(accessToken = "Bearer $accessToken").enqueue(object:Callback<RepRank>{
                override fun onResponse(call: Call<RepRank>, response: Response<RepRank>) {
                    if(response.code()==200){
                        val result:RepRank=response.body()!!
                        Log.d("result","${result.data}")
                        callback(result.data as MutableList<RankData>)
                    }
                }
                override fun onFailure(call: Call<RepRank>, t: Throwable) {
                    Log.d("fail","${t.message}")
                    callback(emptyList<RankData>().toMutableList())
                }
            })


    }
}