package com.hansung.capstone.course

import android.annotation.SuppressLint
import android.content.Intent
import android.database.Cursor
import android.graphics.Rect
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.provider.OpenableColumns
import android.util.Log
import android.view.MenuItem
import android.view.MotionEvent
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.hansung.capstone.*
import com.hansung.capstone.BuildConfig
import com.hansung.capstone.Constants.OPEN_BOARD_FRAGMENT
import com.hansung.capstone.databinding.ActivityCourseBinding
import com.hansung.capstone.Waypoint
import com.hansung.capstone.map.KakaoSearchAPI
import com.hansung.capstone.map.ResultGetAddress
import com.hansung.capstone.post.PostImageAdapterDecoration
import com.hansung.capstone.retrofit.ImageInfo
import com.hansung.capstone.retrofit.ReqCoursePost
import com.hansung.capstone.retrofit.RetrofitService
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.*
import kotlinx.android.synthetic.main.activity_course.*
import kotlinx.android.synthetic.main.activity_post_detail.*
import kotlinx.android.synthetic.main.item_location_search_results.*
import kotlinx.android.synthetic.main.item_post_detail_recomments.view.*
import kotlinx.coroutines.*
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.io.FileOutputStream
import java.util.*

class CourseActivity : AppCompatActivity() {
    private val binding by lazy { ActivityCourseBinding.inflate(layoutInflater) }
    private lateinit var waypoints: MutableList<Waypoint>
    private lateinit var encodedPath: String
    private lateinit var coordinates: List<LatLng>
    private lateinit var snapshotPath: String
    private lateinit var imageInfoMutableList: MutableList<ImageInfo>
    private lateinit var imageList: ArrayList<MultipartBody.Part>
    private lateinit var thumbnail: MultipartBody.Part
    private lateinit var courseImageHolder: CourseImageAdapter.CourseImageHolder
    private var position: Int? = 0
    private lateinit var selectedImageUri: Uri
    private lateinit var getImageLauncher: ActivityResultLauncher<Intent>
    private var origin: String? = null
    private var destination: String? = null
    private var originToDestination: String? = null
    private var region: String? = null
    private lateinit var api: KakaoSearchAPI
    private lateinit var courseImageAdapter: CourseImageAdapter

    @SuppressLint("Recycle")
    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

//        Log.d("LiveDataInCourseActivity", RidingService.timeLiveData.value!!.toString())

        // 인텐트 값 읽기
        val intent = intent
        @Suppress("DEPRECATION")
        waypoints = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
            intent.getParcelableArrayListExtra("waypoints", Waypoint::class.java)!!.toMutableList()
        else
            intent.getParcelableArrayListExtra<Waypoint>("waypoints")!!.toMutableList()
        encodedPath = intent.getStringExtra("coordinates").toString()
        coordinates = DataConverter.decode(encodedPath)
        Log.d("coordinates", "$coordinates")
        snapshotPath = intent.getStringExtra("snapshotPath")!!


        // 지도 스냅샷 출력
        val requestOptions = RequestOptions()
            .fitCenter()
            .override(binding.courseCardView.width)
        Glide.with(this)
            .load(snapshotPath)
            .apply(requestOptions)
            .into(binding.courseImage)

        // 빈 이미지 리스트 생성
        imageList = ArrayList(Collections.nCopies(waypoints.size, null))

        // 스냅샷 이미지 멀티파트
        val snapshotFile = File(snapshotPath)
        val requestFile = RequestBody.create(MediaType.parse("image/jpeg"), snapshotFile)
        thumbnail = MultipartBody.Part.createFormData("thumbnail", snapshotFile.name, requestFile)


        // 이미지 등록 런처
        getImageLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == RESULT_OK) {
                    selectedImageUri = result.data?.data!!
                    selectedImageUri.let { uri ->
                        Glide.with(this)
                            .load(uri) // 이미지 경로 (로컬 파일 경로, 웹 URL 등)
                            .centerCrop()
                            .into(courseImageHolder.binding.courseView)
                        val filename = getFileName(uri)
                        // 선택한 이미지 imageList 추가
                        val filePart: MultipartBody.Part?
                        val inputStream = contentResolver.openInputStream(uri)
                        val file = uri.lastPathSegment?.let { File(cacheDir, it) }
                        val outputStream = FileOutputStream(file)
                        inputStream?.copyTo(outputStream)
                        val requestBody =
                            RequestBody.create(
                                contentResolver.getType(uri)
                                    ?.let { MediaType.parse(it) }, file!!
                            )
                        filePart =
                            requestBody.let {
                                MultipartBody.Part.createFormData(
                                    "imageList", filename,
                                    it
                                )
                            }
                        imageList[position!!] = filePart!!
                        Log.d("CourseActivityImageList", imageList.toString())
                    }
                }
            }

        // RecyclerView 어댑터 연결
        courseImageAdapter = CourseImageAdapter(this, waypoints)
        binding.courseImageRecyclerview.adapter = courseImageAdapter
        binding.courseImageRecyclerview.addItemDecoration(PostImageAdapterDecoration())


        api = KakaoSearchAPI.create()
        // 첫 번째 주소
        searchAddress(waypoints[0]) { originAddress ->
            if (originAddress != null) {
                Log.d("getAddress", "Origin Address: $originAddress")
                origin = originAddress
                region = origin
                // 두 번째 주소
                searchAddress(waypoints.last()) { destinationAddress ->
                    if (destinationAddress != null) {
                        Log.d("getAddress", "Destination Address: $destinationAddress")
                        destination = destinationAddress
                        originToDestination = "$origin -> $destination"
                        // 글 등록 버튼 클릭
                        binding.writeButton.setOnClickListener {
                            if (binding.editTitle.text.toString().isEmpty()) {
                                Toast.makeText(this, "제목을 입력해주세요.", Toast.LENGTH_SHORT).show()
                            } else if (checkWaypoints(waypoints)) {
                                Toast.makeText(this, "장소 이름을 등록해주세요.", Toast.LENGTH_SHORT).show()
                            } else if (binding.editWriting.text.toString().isEmpty()) {
                                Toast.makeText(this, "내용을 입력해주세요.", Toast.LENGTH_SHORT).show()
                            } else {
                                imageInfoMutableList = mutableListOf()
                                for (i in waypoints) {
                                    Log.d("waypoints", "$i")
                                    val placeUrl = i.place_url ?: "" // url 없으면 빈 문자열
                                    imageInfoMutableList.add(
                                        ImageInfo(
                                            "${i.place_lat!!},${i.place_lng!!}",
                                            i.place_name!!,
                                            placeUrl
                                        )
                                    )
                                }
                                val imageInfoList: List<ImageInfo> = imageInfoMutableList
                                val title = binding.editTitle.text.toString()
                                val content = binding.editWriting.text.toString()
                                val userId = MyApplication.prefs.getLong("userId", 0)
                                Log.d("writeButtonClick", "$imageInfoList")
                                Log.d("writeButtonClick", "$imageList")
                                Log.d(
                                    "writeButtonClick",
                                    "$encodedPath $region $originToDestination $userId $title $content $imageInfoList"
                                )
                                val postReqCoursePost = ReqCoursePost(
                                    encodedPath,
                                    region!!,
                                    originToDestination!!,
                                    userId,
                                    category = "COURSE",
                                    title,
                                    content,
                                    imageInfoList
                                )
                                val service = RetrofitService.create()
                                service.coursePostCreate(
                                    requestDTO = postReqCoursePost,
                                    imageList,
                                    thumbnail
                                )
                                    .enqueue(object :
                                        Callback<ReqCoursePost> {
                                        override fun onResponse(
                                            call: Call<ReqCoursePost>,
                                            response: Response<ReqCoursePost>
                                        ) {
                                            if (response.isSuccessful) {
                                                val result: ReqCoursePost? = response.body()
                                                Log.d(
                                                    "onResponse######################################",
                                                    "onResponse: $result"
                                                )
                                            } else {
                                                Log.d(
                                                    "onResponse######################################",
                                                    "onResponse: error"
                                                )
                                            }
                                        }

                                        override fun onFailure(
                                            call: Call<ReqCoursePost>,
                                            t: Throwable
                                        ) {
                                            Log.d("onFailure", "onFailure")
                                        }
                                    })
                                // 게시판으로 이동하는 함수 필요
                                val moveToBoardFragment =
                                    Intent(this@CourseActivity, MainActivity::class.java)
                                moveToBoardFragment.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
                                moveToBoardFragment.putExtra(OPEN_BOARD_FRAGMENT, 3)
                                startActivity(moveToBoardFragment)
                                finish()
                            }
                        }
                    } else {
                        Log.d("getAddress", "Failed to get destination address")
                    }
                }
            } else {
                Log.d("getAddress", "Failed to get origin address")
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                finish()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        val focusView = currentFocus
        if (focusView != null && ev != null) {
            val rect = Rect()
            focusView.getGlobalVisibleRect(rect)
            val x = ev.x.toInt()
            val y = ev.y.toInt()

            if (!rect.contains(x, y)) {
                val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(focusView.windowToken, 0)
                focusView.clearFocus()
            }
        }
        return super.dispatchTouchEvent(ev)
    }

    @SuppressLint("IntentReset")
    fun openGallery(c: CourseImageAdapter.CourseImageHolder, position: Int) {
        this.courseImageHolder = c
        this.position = position
        val galleryIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        getImageLauncher.launch(galleryIntent)
    }

    @SuppressLint("Range")
    fun getFileName(uri: Uri): String? {
        var result: String? = null
        if (uri.scheme == "content") {
            val cursor: Cursor? = contentResolver.query(uri, null, null, null, null)
            cursor.use {
                if (it != null && it.moveToFirst()) {
                    result = it.getString(it.getColumnIndex(OpenableColumns.DISPLAY_NAME))
                }
            }
        }
        if (result == null) {
            result = uri.lastPathSegment
        }
        return result
    }

    private fun searchAddress(waypoint: Waypoint, callback: (String?) -> Unit) {
        val api = KakaoSearchAPI.create()
        api.getAddress(
            BuildConfig.KAKAO_REST_API_KEY,
            waypoint.place_lng.toString(),
            waypoint.place_lat.toString()
        ).enqueue(object : Callback<ResultGetAddress> {
            override fun onResponse(
                call: Call<ResultGetAddress>,
                response: Response<ResultGetAddress>
            ) {
                val body = response.body()
                if (body != null) {
                    Log.d("getAddress", "onResponse: $body")
                    val resultAddress =
                        "${body.documents[0].address.region_1depth_name} ${body.documents[0].address.region_2depth_name}"
                    callback(resultAddress)
                } else {
                    callback(null)
                }
            }

            override fun onFailure(
                call: Call<ResultGetAddress>,
                t: Throwable
            ) {
                Log.d("getAddress", "onFailure: $t")
                callback(null)
            }
        })
    }

    val searchLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                // 결과 처리
                val pos = result.data?.getIntExtra("position", -1)
                val placeName = result.data?.getStringExtra("place_name").toString()
                val placeUrl = result.data?.getStringExtra("place_url").toString()
                runOnUiThread {
                    courseImageAdapter.updateItemBySearch(
                        pos!!.toInt(),
                        placeName,
                        placeUrl
                    )
                }
            }
        }

    private fun checkWaypoints(waypoints: MutableList<Waypoint>): Boolean {
        for (w in waypoints) {
            if (w.place_name.isNullOrEmpty())
                return true
        }
        return false
    }
}
