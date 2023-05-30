package com.hansung.capstone.mypage

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.database.Cursor
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.provider.OpenableColumns
import android.util.Log
import android.view.*
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.Description
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.formatter.ValueFormatter
import com.hansung.capstone.*
import com.hansung.capstone.board.BoardAdapterDecoration
import com.hansung.capstone.board.RePModifyProfileImage
import com.hansung.capstone.databinding.FragmentMypageBinding
import com.hansung.capstone.linechart.GetRecordData
import com.hansung.capstone.modify.ModifyMyInfo
import com.hansung.capstone.retrofit.ReqModifyProfileImage
import com.hansung.capstone.retrofit.RetrofitService
import com.hansung.capstone.retrofit.RidingData
import kotlinx.android.synthetic.main.activity_findpw.view.*
import kotlinx.android.synthetic.main.fragment_mypage.view.*
import kotlinx.android.synthetic.main.view_login.view.*
import kotlinx.android.synthetic.main.view_profile.*
import kotlinx.android.synthetic.main.view_profile.view.*
import okhttp3.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.io.FileOutputStream
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.concurrent.TimeUnit

class MyPageFragment : Fragment() {

    val api = RetrofitService.create()
    private lateinit var resultAllPost: RecyclerView
    private lateinit var adapter: RecordAdapter
    private val linearLayoutManager= LinearLayoutManager(activity)
    var requestX=0
    var requestY=0
    private val requestCode = 100
    lateinit var binding: FragmentMypageBinding
    private val defaultGalleryRequestCode = 0
    private var filePart: MultipartBody.Part? = null
    private var ridingDataList: MutableList<RidingData> = mutableListOf()
    private var monthRidingDataList: List<RidingData> = emptyList()
    private var flag=false
    val convertedDate: MutableList<String> = mutableListOf()
    var convertedTime : MutableList<String> = mutableListOf()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentMypageBinding.inflate(inflater, container, false)
        adapter= RecordAdapter()
        resultAllPost = binding.userContainer.resultAllPost
        resultAllPost.addItemDecoration(BoardAdapterDecoration())
        resultAllPost.adapter=adapter
        resultAllPost.layoutManager=linearLayoutManager
        if (MyApplication.prefs.getString("email", "") == "") {
            visibleLogin()
        } else {
            binding.modifyInfo.visibility=View.VISIBLE
            //visibleProfile()
        }
        return binding.root
    }

    @SuppressLint("SuspiciousIndentation", "SetTextI18n")
    private fun visibleProfile() {
        val noImage:Long=-1
        Log.d("position-1","@@@@@@@@@@@@@@@@")
        if(!flag){
            Log.d("position0","@@@@@@@@@@@@@@@@")
            requestData(requestX)
        }
        binding.userContainer.profile_container.visibility = View.VISIBLE
        binding.userContainer.login_container.visibility = View.GONE
        binding.modifyInfo.visibility=View.VISIBLE
        binding.modifyInfo.isEnabled=true
        binding.userContainer.profile_container.tv_nick.text =
            MyApplication.prefs.getString("nickname", "")
        binding.userContainer.profile_container.tv_email.text =
            MyApplication.prefs.getString("email", "")
        if (MyApplication.prefs.getLong("profileImageId", 0) == noImage) {
            binding.userContainer.profile_container.profileImage.setImageResource(R.drawable.user)
        } else {
            getProfileImage()
        }
        binding.userContainer.profile_container.profileImage.setOnClickListener {
            val intent = Intent(activity, ModifyMyInfo::class.java)
            startActivity(intent)
        }
        binding.modifyInfo.setOnClickListener {
            val intent = Intent(activity, ModifyMyInfo::class.java)
            startActivity(intent)
        }
        var isInitialSelection = true  //spinner selector
        val select_x = resources.getStringArray(R.array.select_x)
        //val adapterX = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, select_x)
        val adapterX = ArrayAdapter(requireContext(), R.layout.spinner_dropdown_item, select_x)
        binding.userContainer.select_x.adapter=adapterX
        binding.userContainer.select_x.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                Log.d("seleceted", "$position")
                if (isInitialSelection) {
                    isInitialSelection = false
                    return  // Skip initial selection
                } else {
                    when (position) {
                        0 -> {
                            requestX = 0
                        }
                        1 -> {
                            requestX = 1
                        }
                    }
                    Log.d("position1", "@@@@@@@@@@@@@@@@")
                    if(!flag)
                    requestData(requestX)
                }
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {
                // 아무것도 선택되지 않았을 때의 처리
                //isInitialSelection = true
            }
        }
        var isInitialSelectionY = true
        val select_y = resources.getStringArray(R.array.select_y)
       // val adapterY = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, select_y)
        val adapterY = ArrayAdapter(requireContext(), R.layout.spinner_dropdown_item, select_y)
        binding.userContainer.select_y.adapter=adapterY
        binding.userContainer.select_y.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                Log.d("position","$position")
                if (isInitialSelectionY) {
                    isInitialSelectionY = false
                    return  // Skip initial selection
                } else {
                    when (position) {
                        0 -> {
                            requestY = 0
                            // drawDistance(ridingDataList)
                        }
                        1 -> {
                            requestY = 1
                            // drawTime(ridingDataList)
                        }
                        2 -> {
                            requestY = 2
                            // drawCalorie(ridingDataList)
                        }
                    }
                    Log.d("position2", "@@@@@@@@@@@@@@@@")
                    if(!flag)
                    requestData(requestX)
                }
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {
                // 아무것도 선택되지 않았을 때의 처리
               // isInitialSelectionY = true
            }
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
        binding.userContainer.renewBt.setOnClickListener {
            Log.d("position3","@@@@@@@@@@@@@@@@")
            requestData(requestX)
        }
        GetRecordData().getRidingData(30) { result ->
            if (result.isNotEmpty()) {
                sumData(result)// 한달 누적 정보
                adapter.addData(result)
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
    private fun request(period:Int){
        GetRecordData().getRidingData(period) { result ->
            if (result.isNotEmpty()) {
                Log.d("repeat0", "@@@@@@@@@@@@@@@@")
                binding.userContainer.noResult.visibility = View.GONE
                this.ridingDataList = result
                val ridingTime=result.map{ridingData ->
                    ridingData.ridingTime
                }.toMutableList()
                convertedTime=Utility.convertMsList(ridingTime)
                val now = LocalDateTime.now()

                convertedDate.clear() // 기존 데이터 초기화

                for (i in 0..period) {
                    val value = now.minusDays((period - i).toLong()).format(DateTimeFormatter.ofPattern("MM-dd"))
                    convertedDate.add(i, value)
                }
                binding.userContainer.chart.clear()
                when (requestY) {
                    0 -> {
                        Log.d("repeat2", "@@@@@@@@@@@@@@@@")
                        drawDistance(ridingDataList)
                    }
                    1 -> drawTime(ridingDataList)
                    2 -> drawCalorie(ridingDataList)
                }
            } else {
                binding.userContainer.noResult.visibility = View.VISIBLE
            }

            flag = false
        }
    }
    private fun requestData(int: Int) { // record 요청
        var period = 6
        if (int == 1) period = 29

        if (!flag) {
            flag = true
            Log.d("period", "$period")
            if(Token().checkToken()){
                Token().issueNewToken {
                    request(period=period)
                }
            }else request(period=period)

        }
    }

    private fun drawCalorie(ridingDataList: MutableList<RidingData>) {
        val barChart=binding.userContainer.chart
        initBarChart(barChart)
        barChart.setScaleEnabled(false)
        val entries:ArrayList<BarEntry> = ArrayList()
        val title="칼로리"
        val zero=0
        var barEntry:BarEntry
        for(i in 0 until convertedDate.size){
            if(ridingDataList.isNotEmpty()){

                if(ridingDataList[0].createdDate.substring(5 until 10).equals(convertedDate[i])){
                    Log.d("repeat3","${convertedDate[i]}")
                    Log.d("repeat4","${ridingDataList[0].createdDate}")
                    barEntry = BarEntry(i.toFloat(), ridingDataList.removeAt(0).calorie.toFloat()
                    )
                }
                else{

                    barEntry = BarEntry(i.toFloat(), zero.toFloat())
                }

            }else  barEntry = BarEntry(i.toFloat(), zero.toFloat())
            entries.add(barEntry)
        }
        val barDataSet = BarDataSet(entries, title)
        // val xAxisLabels =ridingDataList. // x축의 레이블로 사용할 문자열 배열
        val xAxis = barChart.xAxis // barChart는 BarChart 객체입니다.
        // xAxis.valueFormatter = IndexAxisValueFormatter(xAxisLabels)
        val leftAxis: YAxis = barChart.getAxisLeft()
        // y축 값 변환 로직을 여기에 추가합니다
        leftAxis.valueFormatter = object : ValueFormatter() {
            override fun getFormattedValue(value: Float): String {
                // 원하는 값 변환 로직을 구현합니다
                // 예를 들어, 소수점 자리를 제한하거나 단위를 추가할 수 있습니다
                return String.format("%.0f kcal", value)
            }
        }
        val data = BarData(barDataSet)
        barChart.data = data
        // initBarDataSet(barDataSet)
        barChart.invalidate()
    }
    private fun drawTime(ridingDataList: MutableList<RidingData>) {
        val barChart=binding.userContainer.chart
        initBarChart(barChart)
        barChart.setScaleEnabled(false)
        val entries:ArrayList<BarEntry> = ArrayList()
        val title="시간"
        val zero=0
        var barEntry:BarEntry
        for(i in 0 until convertedDate.size){
            if(ridingDataList.isNotEmpty()){
                if(ridingDataList[0].createdDate.substring(5 until 10).equals(convertedDate[i])){
                    barEntry = BarEntry(i.toFloat(),ridingDataList.removeAt(0).ridingTime.toFloat()
                    )

                }
                else{
                    barEntry = BarEntry(i.toFloat(), zero.toFloat())
                }
            }else  barEntry = BarEntry(i.toFloat(), zero.toFloat())

            entries.add(barEntry)
        }

        val barDataSet = BarDataSet(entries, title)
        // val xAxisLabels =ridingDataList. // x축의 레이블로 사용할 문자열 배열

        val xAxis = barChart.xAxis // barChart는 BarChart 객체입니다.
        // xAxis.valueFormatter = IndexAxisValueFormatter(xAxisLabels)
        val leftAxis: YAxis = barChart.getAxisLeft()
       // leftAxis.valueFormatter = IndexAxisValueFormatter(convertedTime)
        binding.userContainer.horizontal_scroll_view.post{
            binding.userContainer.horizontal_scroll_view.scrollTo(
                binding.userContainer.chart.width,0
            )
        }
        leftAxis.valueFormatter = object : ValueFormatter() {
            override fun getFormattedValue(value: Float): String {
                val hours = (value / (1000 * 60 * 60)).toInt()
                val minutes = ((value / (1000 * 60)) % 60).toInt()
                return String.format("%dH%dM", hours, minutes)
            }
        }
        val valueFormatter = object : ValueFormatter() {
            override fun getFormattedValue(value: Float): String {
                return if(value!=0F){
                    val hours = (value / (1000 * 60 * 60)).toInt()
                    val minutes = ((value / (1000 * 60)) % 60).toInt()
                    String.format("%dH%dM", hours, minutes)
                }else "0"
            }
        }
        barDataSet.valueFormatter = valueFormatter
        val data = BarData(barDataSet)
        barChart.data = data

        barChart.invalidate()
    }
    @SuppressLint("ClickableViewAccessibility")
    private fun drawDistance(ridingDataList: MutableList<RidingData>) {
        val barChart=binding.userContainer.chart
        initBarChart(barChart)
        barChart.setScaleEnabled(false)
        val entries:ArrayList<BarEntry> = ArrayList()
        val title="거리"

        val zero=0
        var barEntry:BarEntry
        for(i in 0 until convertedDate.size){
            if(ridingDataList.isNotEmpty()){

                if(ridingDataList[0].createdDate.substring(5 until 10).equals(convertedDate[i])){
                    barEntry = BarEntry(i.toFloat(), ridingDataList.removeAt(0).ridingDistance)
                }
                else{

                    barEntry = BarEntry(i.toFloat(), zero.toFloat())
                }

            }else  barEntry = BarEntry(i.toFloat(), zero.toFloat())
            entries.add(barEntry)
        }

//        for (i in 0 until createdDate.size){
//            if(ridingDataList.isNotEmpty()){
//                if(ridingDataList[0].createdDate.format())
//            }
//            if ridingDataList 가 존재한다면:
//                if ridingDataList[0].createdDate.format() ("5/25") 가 createdDate[i]랑 같다면 {
//                    val barEntry = BarEntry(i.toFloat(), ridingDataList.remove(0).ridingDistance)
//                }
//                else: val barEntry = BarEntry(i.toFloat(), 0)
//            else: val barEntry = BarEntry(i.toFloat(), 0)
//            entries.add(barEntry)

 //       }
        val leftAxis: YAxis = barChart.getAxisLeft()

// y축 값 변환 로직을 여기에 추가합니다
        leftAxis.valueFormatter = object : ValueFormatter() {
            override fun getFormattedValue(value: Float): String {
                // 원하는 값 변환 로직을 구현합니다.
                // 예를 들어, 소수점 자리를 제한하거나 단위를 추가할 수 있습니다.
                return String.format("%.0f km", value)
            }
        }
        val barDataSet = BarDataSet(entries, title)
       // val xAxisLabels =ridingDataList. // x축의 레이블로 사용할 문자열 배열

        val xAxis = barChart.xAxis // barChart는 BarChart 객체입니다.
       // xAxis.valueFormatter = IndexAxisValueFormatter(xAxisLabels)

        val data = BarData(barDataSet)
        barChart.data = data
       // initBarDataSet(barDataSet)
        barChart.invalidate()
    }
    private fun initBarDataSet(barDataSet: BarDataSet) {
        //Changing the color of the bar
        barDataSet.color = Color.parseColor("#304567")
        //Setting the size of the form in the legend
        barDataSet.formSize = 15f
        //showing the value of the bar, default true if not set
        barDataSet.setDrawValues(false)
        //setting the text size of the value of the bar
        barDataSet.valueTextSize = 12f
    }
    private fun initBarChart(barChart: BarChart) {
        //hiding the grey background of the chart, default false if not set
        barChart.setDrawGridBackground(false)
        //remove the bar shadow, default false if not set
        barChart.setDrawBarShadow(false)
        //remove border of the chart, default false if not set
        barChart.setDrawBorders(false)
        barChart.setTouchEnabled(false)
        //remove the description label text located at the lower right corner
        val description = Description()
        description.setEnabled(false)
        barChart.setDescription(description)

        //X, Y 바의 애니메이션 효과
        barChart.animateY(1000)
        barChart.animateX(1000)
        //바텀 좌표 값
        val xAxis: XAxis = barChart.getXAxis()
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        xAxis.valueFormatter = IndexAxisValueFormatter(convertedDate)
        //set the horizontal distance of the grid line
        xAxis.granularity = 1f
        xAxis.textColor = Color.BLACK
        //hiding the x-axis line, default true if not set
        xAxis.setDrawAxisLine(false)
        //hiding the vertical grid lines, default true if not set
        xAxis.setDrawGridLines(false)
        //좌측 값 hiding the left y-axis line, default true if not set
        val leftAxis: YAxis = barChart.getAxisLeft()
        leftAxis.setDrawAxisLine(false)
        leftAxis.textColor = Color.BLACK


        //우측 값 hiding the right y-axis line, default true if not set
        val rightAxis: YAxis = barChart.getAxisRight()
        rightAxis.setDrawAxisLine(false)
        rightAxis.isEnabled=false
        //바차트의 타이틀
        val legend: Legend = barChart.getLegend()
        //setting the shape of the legend form to line, default square shape
        legend.form = Legend.LegendForm.LINE
        //setting the text size of the legend
        legend.textSize = 11f
        legend.textColor = Color.BLACK
        //setting the alignment of legend toward the chart
        legend.verticalAlignment = Legend.LegendVerticalAlignment.TOP
        legend.horizontalAlignment = Legend.LegendHorizontalAlignment.CENTER
        //setting the stacking direction of legend
        legend.orientation = Legend.LegendOrientation.HORIZONTAL
        //setting the location of legend outside the chart, default false if not set
        legend.setDrawInside(false)
    }
        private fun sumData(data:List<RidingData>){
            this.monthRidingDataList=data
            binding.userContainer.monthDistance.text= String.format("%.2f",monthRidingDataList.sumByDouble { it.ridingDistance.toDouble() })+"km"
            val totalTime=monthRidingDataList.sumByDouble { it.ridingTime.toDouble() }
            val changedValue=Utility.convertMs(totalTime.toLong())
            binding.userContainer.monthTime.text=changedValue
            binding.userContainer.monthC.text=String.format("%d",monthRidingDataList.sumBy{ it.calorie })+"calorie"
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

    }


    override fun onResume() {
        super.onResume()
       //  if(monthRidingDataList.isNotEmpty())
            //sumData(monthRidingDataList)
        if(MainActivity.getInstance()?.getLoginState()!!)
            commentLogin()
        if(MyApplication.prefs.getString("email", "")!="")
            visibleProfile()
        else  visibleLogin()
    }

    private fun commentLogin(){
        Toast.makeText(requireActivity(),"로그인 되었습니다.",Toast.LENGTH_SHORT).show()
        MainActivity.getInstance()!!.setLoginState(false)
    }

    override fun onDestroy() {
        super.onDestroy()
        adapter.remove()
    }

}