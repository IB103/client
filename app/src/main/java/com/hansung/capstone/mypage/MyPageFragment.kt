package com.hansung.capstone.mypage

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.provider.OpenableColumns
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.Toast
import com.bumptech.glide.Glide
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.DefaultValueFormatter
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.listener.OnChartValueSelectedListener
import com.hansung.capstone.*
import com.hansung.capstone.board.RePModifyProfileImage
import com.hansung.capstone.databinding.FragmentMypageBinding
import com.hansung.capstone.home.CustomMarkerView
import com.hansung.capstone.linechart.GetRecordData
import com.hansung.capstone.modify.ModifyMyInfo
import com.hansung.capstone.retrofit.ReqModifyProfileImage
import com.hansung.capstone.retrofit.RetrofitService
import com.hansung.capstone.retrofit.RidingData
import kotlinx.android.synthetic.main.fragment_mypage.view.*
import kotlinx.android.synthetic.main.view_login.view.*
import kotlinx.android.synthetic.main.view_profile.view.*
import okhttp3.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.io.FileOutputStream
import kotlin.math.roundToLong

class MyPageFragment : Fragment() {

    val api = RetrofitService.create()
    private val requestCode = 100
    lateinit var binding: FragmentMypageBinding
    private val defaultGalleryRequestCode = 0
    private var filePart: MultipartBody.Part? = null
    private var weekRidingDataList: List<RidingData> = emptyList()
    private var monthRidingDataList: List<RidingData> = emptyList()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentMypageBinding.inflate(inflater, container, false)
        if (MyApplication.prefs.getString("email", "") == "") {
            visibleLogin()
        } else {
            binding.modifyInfo.visibility=View.VISIBLE
            visibleProfile()
        }
        return binding.root
    }

    @SuppressLint("SuspiciousIndentation", "SetTextI18n")
    private fun visibleProfile() {
        val noImage:Long=-1
        binding.userContainer.profile_container.visibility = View.VISIBLE
        binding.userContainer.login_container.visibility = View.GONE
        binding.userContainer.profile_container.tv_nick.text =
            MyApplication.prefs.getString("nickname", "")
        binding.userContainer.profile_container.tv_email.text =
            MyApplication.prefs.getString("email", "")
        if (MyApplication.prefs.getLong("profileImageId", 0) == noImage) {
            binding.userContainer.profile_container.profileImage.setImageResource(R.drawable.user)
        } else {
            getProfileImage()
        }
        binding.modifyInfo.setOnClickListener {
            val intent = Intent(activity, ModifyMyInfo::class.java)
            startActivity(intent)
        }


        //내가 쓴 글
        binding.userContainer.profile_container.mystory_bt.setOnClickListener {
            val intent = Intent(activity, MyStory::class.java)
            startActivity(intent)
        }
        //내가 스크랩 글
        binding.userContainer.myscraplist_bt.setOnClickListener {
            val intent = Intent(activity, MyScrap::class.java)
            startActivity(intent)
        }
        GetRecordData().getRidingData(7) { result ->
            if (result.isNotEmpty()) {
                binding.userContainer.noResult.visibility=View.GONE
                this.weekRidingDataList=result
                draw(result)
                //adapter.setData(result)
            } else {
                binding.userContainer.noResult.visibility=View.VISIBLE
            }
        }
        GetRecordData().getRidingData(30) { result ->
            Log.d("getMonthlyData","$result")
            if (result.isNotEmpty()) {
                sumData(result)
            } else {
            }
        }
    }


    private fun visibleLogin() {
        binding.modifyInfo.visibility=View.GONE
        binding.userContainer.login_container.visibility = View.VISIBLE
        binding.userContainer.profile_container.visibility = View.GONE
        binding.userContainer.login_container.login_bt.setOnClickListener {
            val intent = Intent(activity, LoginActivity::class.java)
            @Suppress("DEPRECATION")
            startActivityForResult(intent, requestCode)
            // startForResult.launch(intent)
        }

    }
    @SuppressLint("ClickableViewAccessibility")
    private fun draw(ridingDataList: List<RidingData>) {
        binding.userContainer.chart.visibility = View.VISIBLE
        val entries = ridingDataList.map { Entry(it.ridingTime.toFloat(), it.ridingDistance) }
        val dataSet = LineDataSet(entries, "라이딩 기록")
        Log.d("ridinglist","$ridingDataList")

        val markerView = CustomMarkerView(requireContext(), R.layout.marker_layout)
        val lineData = LineData(dataSet)
        val lineChart = binding.userContainer.chart
        lineChart.marker = markerView
        val xAxis = lineChart.xAxis
        lineChart.data = lineData
        lineChart.invalidate()
        xAxis.apply {
            setDrawGridLines(false)
            setDrawAxisLine(true)
            setDrawLabels(true)
            position = XAxis.XAxisPosition.BOTTOM
            textColor = resources.getColor(R.color.black, null)
            textSize = 10f
            valueFormatter = DefaultValueFormatter(2)
            labelRotationAngle = 0f
            setLabelCount(10, true)
        }

        lineChart.apply {

            axisRight.isEnabled = false   //y축 사용여부
            axisLeft.isEnabled = true
            legend.isEnabled = false    //legend 사용여부
            description.isEnabled = false //주석
            isScaleYEnabled = false //y축 줌 사용여부
            isScaleXEnabled = false //x축 줌 사용여부
        }

        dataSet.apply {
            color = resources.getColor(R.color.maincolor, null)
            circleRadius = 3f
            lineWidth = 1f
            setCircleColor(resources.getColor(R.color.maincolor, null))
            setDrawHighlightIndicators(false)
            setDrawValues(false) // 숫자표시
            valueTextColor = resources.getColor(R.color.black, null)
            valueFormatter = DefaultValueFormatter(2)  // 소숫점 자릿수 설정
            valueTextSize = 10f

        }
        lineChart.setOnChartValueSelectedListener(object : OnChartValueSelectedListener {
            override fun onValueSelected(e: Entry?, h: Highlight?) {
                if (e != null) {
                    lineChart.highlightValue(h) // 하이라이트 표시
                }
            }

            override fun onNothingSelected() {
            }
        })

        lineChart.setOnTouchListener { v, event ->
            if (event.action == MotionEvent.ACTION_DOWN) {
                val highlight = lineChart.getHighlightByTouchPoint(event.x, event.y)
                if (highlight == null) { }
            }
            false
        }
    }
        private fun sumData(data:List<RidingData>){
            this.monthRidingDataList=data
            binding.userContainer.monthDistance.text= String.format("%.2f",monthRidingDataList.sumByDouble { it.ridingDistance.toDouble() })+"km"
            binding.userContainer.monthTime.text=String.format("%.2f",monthRidingDataList.sumByDouble { it.ridingTime.toDouble() })+"H"
            binding.userContainer.monthC.text=String.format("%.2f",monthRidingDataList.sumByDouble { it.calorie.toDouble() })+"C"
        }
    private fun getProfileImage() {
        val profileImageId = MyApplication.prefs.getLong("profileImageId", 0)
        Glide.with(requireActivity())
            .load("${MyApplication.getUrl()}profile-image/$profileImageId") // 불러올 이미지 url
            .override(200, 200)
            .centerCrop()
            .into(binding.userContainer.profile_container.profileImage)
    }


    @SuppressLint("Recycle")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        @Suppress("DEPRECATION")
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == this.requestCode) {
            if (resultCode == Activity.RESULT_OK || MyApplication.prefs.getString("email","")!="") {
                Log.d("resultCode","$resultCode")
                Log.d(">>>>>>>>>>>>>>cc", MyApplication.prefs.getString("email", ""))
                Log.d("FINISH",">>>>>>>>>>>>>>")
                visibleProfile()
            } else {
                Log.d("resultCode","$resultCode")
                Log.d("Fail",">>>>>>>>>>>>>>")
                visibleLogin()
            }
        }
        when (requestCode) {
            defaultGalleryRequestCode -> {
                if (resultCode == Activity.RESULT_OK && requestCode == defaultGalleryRequestCode) {
                    val photoUri: Uri = data?.data!!
                    val filename=getFileName(photoUri)
                    Log.d("filename","$filename")
                    // 선택한 이미지 list 추가 코드
                    val inputStream = requireActivity().contentResolver.openInputStream(photoUri)
                    val file = File(requireActivity().cacheDir, photoUri.lastPathSegment!!)
                    val outputStream = FileOutputStream(file)
                    inputStream?.copyTo(outputStream)
                    val requestBody = RequestBody.create(MediaType.parse(requireActivity().contentResolver.getType(photoUri)!!), file)
                    filePart = MultipartBody.Part.createFormData("imageList", filename, requestBody)
                    val image:MultipartBody.Part=filePart!!
                    Log.d("image","$image")
                    modifyImage(filePart!!)
                   // modifyImage(image)
                    //imageList.add(filePart!!)
                }
            }
        }
    }
    fun changed(){
        val noImage:Long=-1
        if (MyApplication.prefs.getLong("profileImageId", 0) == noImage) {
            binding.userContainer.profile_container.profileImage.setImageResource(R.drawable.user)
        } else {
            Log.d("profileImageId", "${MyApplication.prefs.getLong("profileImageId", 0)}")
            getProfileImage()
        }
        Toast.makeText(context, "프로필 사진이 변경됐습니다.", Toast.LENGTH_SHORT).show()
    }
    private fun modifyImage(image: MultipartBody.Part){
        val userId= MyApplication.prefs.getLong("userId",0)
        val profileImageId = MyApplication.prefs.getLong("profileImageId", 0)
        val putModifyProfileImage= ReqModifyProfileImage(userId, profileImageId = profileImageId)
        api.modifyProfileImage(putModifyProfileImage,image).enqueue(object : Callback<RePModifyProfileImage> {
            override fun onResponse(call: Call<RePModifyProfileImage>, response: Response<RePModifyProfileImage>) {
                if (response.isSuccessful) {
                    Log.d( "modifyProfile 성공"," $response")
                    MyApplication.prefs.setLong("profileImageId",  response.body()!!.data.profileImageId)
                    changed()
                   // MainActivity.getInstance()?.setChangedPostCheck(true)
                    }else
                    Log.d("ERR", "onResponse 실패 $response")
                }
            override fun onFailure(call: Call<RePModifyProfileImage>, t: Throwable) {
                Log.d("onFailure", "실패 $t")
            }
        })
    }

    override fun onResume() {
        super.onResume()
        if(weekRidingDataList.isNotEmpty())
           draw(weekRidingDataList)
        else if(monthRidingDataList.isNotEmpty())
            sumData(monthRidingDataList)
        if(MainActivity.getInstance()?.getLoginState()!!)
            commentLogin()
        if(MyApplication.prefs.getString("email", "")!="")
            visibleProfile()
        else  visibleLogin()
    }
@Suppress("NAME_SHADOWING")
@SuppressLint("Range")
fun getFileName(uri: Uri): String? {
    var result: String? = null
    if (uri.scheme == "content") {
        val cursor: Cursor? =requireActivity().contentResolver.query(uri, null, null, null, null)
        cursor.use { cursor ->
            if (cursor != null && cursor.moveToFirst()) {
                cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME))
                    .also { result = it }
            }
        }
    }
    if (result == null) {
        result = uri.lastPathSegment
    }
    return result
}
    private fun commentLogin(){
        Toast.makeText(requireActivity(),"로그인 되었습니다.",Toast.LENGTH_SHORT).show()
        MainActivity.getInstance()!!.setLoginState(false)
    }
}