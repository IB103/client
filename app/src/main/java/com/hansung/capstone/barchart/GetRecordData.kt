package com.hansung.capstone.barchart

import android.util.Log
import com.hansung.capstone.MyApplication
import com.hansung.capstone.retrofit.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class GetRecordData {
    var service=RetrofitService.create()
    internal fun getRidingData(int:Int,callback: (result: MutableList<RidingData>) -> Unit){
    println("get Riding Data start")
        val accessToken= MyApplication.prefs.getString("accessToken", "")
            val userid=MyApplication.prefs.getLong("userId",0)
            service.getRecord(accessToken = "Bearer $accessToken",userid ,int.toLong()).enqueue(object:Callback<RepGetRecord>{
                override fun onResponse(call: Call<RepGetRecord>, response: Response<RepGetRecord>) {
                    if(response.isSuccessful){
                        val result:RepGetRecord=response.body()!!
                        Log.d("record Data","${result.data}")
                        callback(result.data as MutableList<RidingData>)
                        // setData(result.data)
                    }else{

                    }
                }
                override fun onFailure(call: Call<RepGetRecord>, t: Throwable) {
                    Log.d("fail","${t.message}")
                    callback(emptyList<RidingData>().toMutableList())
                }
            })
    }
    fun getRankData(callback: (result: MutableList<RankData>) -> Unit){
            service.getRank().enqueue(object:Callback<RepRank>{
                override fun onResponse(call: Call<RepRank>, response: Response<RepRank>) {
                    Log.d("checkRankData","###########")
                    Log.d("result rankdata","${response.body()}")
                    if(response.isSuccessful){
                    if(response.code()==200){
                        val result:RepRank=response.body()!!
                        callback(result.data as MutableList<RankData>)
                    }}
                }
                override fun onFailure(call: Call<RepRank>, t: Throwable) {
                    Log.d("fail","${t.message}")
                    callback(emptyList<RankData>().toMutableList())
                }
            })


    }
}