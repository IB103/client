package com.hansung.capstone.linechart

import android.util.Log
import com.hansung.capstone.MyApplication
import com.hansung.capstone.retrofit.RepGetRecord
import com.hansung.capstone.retrofit.RetrofitService
import com.hansung.capstone.retrofit.RidingData
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class GetRecordData {
    var service=RetrofitService.create()
    internal fun getRidingData(int:Int,callback: (result: List<RidingData>) -> Unit){
        //MyApplication.prefs.getLong("userId", 0
        service.getRecord(3 ,7).enqueue(object:Callback<RepGetRecord>{
            override fun onResponse(call: Call<RepGetRecord>, response: Response<RepGetRecord>) {
                if(response.code()==200){
                    val result:RepGetRecord=response.body()!!
                    Log.d("result","${result.data}")
                    callback(result.data)
                   // setData(result.data)
                }
            }
            override fun onFailure(call: Call<RepGetRecord>, t: Throwable) {
                Log.d("fail","${t.message}")
                callback(emptyList())
            }
        })

    }
}