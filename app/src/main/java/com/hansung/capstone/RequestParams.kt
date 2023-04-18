package com.hansung.capstone


class RequestParams {
    private val params: MutableMap<String, Any> = mutableMapOf()

    fun put(key: String, value: Any) {
        params[key] = value
    }
    fun  getValue(key:String):Any?{
        return params[key]
    }
    fun getParams(): Map<String, Any> {
        return params.toMap()
    }
}