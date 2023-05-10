package com.hansung.capstone.course

import android.annotation.SuppressLint
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.BitmapFactory
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
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.hansung.capstone.*
import com.hansung.capstone.R
import com.hansung.capstone.databinding.ActivityCourseBinding
import com.hansung.capstone.Waypoint
import com.hansung.capstone.retrofit.RetrofitService
import com.naver.maps.geometry.LatLng
import com.naver.maps.geometry.LatLngBounds
import com.naver.maps.map.*
import com.naver.maps.map.overlay.Marker
import com.naver.maps.map.overlay.PathOverlay
import kotlinx.android.synthetic.main.activity_course.*
import kotlinx.android.synthetic.main.activity_post_detail.*
import kotlinx.android.synthetic.main.item_post_detail_recomments.view.*
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream
import java.util.*
import kotlin.math.round


class CourseActivity : AppCompatActivity(), OnMapReadyCallback {
    private val binding by lazy { ActivityCourseBinding.inflate(layoutInflater) }
    private var serverinfo = MyApplication.getUrl() //username password1 password2 email
    private var retrofit = Retrofit.Builder().baseUrl(serverinfo)
        .addConverterFactory(GsonConverterFactory.create()).build()
    private var service = retrofit.create(RetrofitService::class.java)
    private lateinit var naverMap: NaverMap // 네이버 맵 객체
    private lateinit var waypoints: MutableList<Waypoint>
    private lateinit var path: List<LatLng>
    var imageUriList=ArrayList<Uri>()
    private lateinit var imageList: ArrayList<MultipartBody.Part>
    private lateinit var courseImageHolder: CourseImageAdapter.CourseImageHolder
    private var position: Int? = 0
    private lateinit var selectedImageUri: Uri
//    private lateinit var getContent: ActivityResultLauncher<String>
        private lateinit var getContent: ActivityResultLauncher<Intent>
    val REQUEST_CODE = 100

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = ""

        val intent = intent
//        val waypoints = intent.getParcelableArrayListExtra<Waypoint>("waypoints",Waypoint::class.java)
        waypoints = intent.getParcelableArrayListExtra<Waypoint>("waypoints")!!
        path = intent.getStringExtra("path")?.let { decode(it) }!!
        Log.d("웨이포인트", waypoints.toString())
        Log.d("경로", path.toString())

//        imageList = ArrayList(waypoints.size)
        imageList = ArrayList(Collections.nCopies(waypoints.size, null))
        Log.d("imageList 초기화","${imageList.toString()} + ${waypoints.count()}")

        // registerForActivityResult 대신 getContent라는 ActivityResultLauncher를 생성합니다.
        getContent = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
//                val selectedImageUri: Uri? = result.data?.data
                selectedImageUri = result.data?.data!!
                Log.d("uri출력 in launch",selectedImageUri.toString())
                selectedImageUri.let { uri ->
                    courseImageHolder.binding.courseView.setImageURI(uri)
                    val filename=getFileName(uri)
                    Log.d("filename","$filename")
                    // 선택한 이미지를 imageList에 추가하는 코드
                    var filePart: MultipartBody.Part? = null
                    val inputStream = contentResolver.openInputStream(uri)
                    val file = File(cacheDir, uri.lastPathSegment)
                    val outputStream = FileOutputStream(file)
                    inputStream?.copyTo(outputStream)
                    val requestBody = RequestBody.create(MediaType.parse(contentResolver.getType(uri)), file)
                    filePart = MultipartBody.Part.createFormData("imageList", filename, requestBody)
                    imageList[position!!]=filePart!!
//                    imageList.add(filePart!!)
                    Log.d("imageList 값 변경",imageList.toString())
                }
            }
        }

//            registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
//                uri?.let {
//                    //                    imageView.setImageURI(uri)
//                }
//            }

        val fm = supportFragmentManager
        val mapFragment = fm.findFragmentById(R.id.map) as MapFragment?
            ?: MapFragment.newInstance().also {
                fm.beginTransaction().add(R.id.map, it).commit()
            }

        mapFragment.getMapAsync(this)

        binding.courseImageRecyclerview.adapter = waypoints?.let { CourseImageAdapter(this, it) }
//        binding.courseImageRecyclerview.addItemDecoration(
//            PostImageAdapterDecoration()
//        )
//        val waypoints = intent.getParcelableArrayListExtra<Waypoint>("waypoints")

        // RidingActivity intent 받기
        val byteArray = intent.getByteArrayExtra("bitmap")
        val bitmap = byteArray?.size?.let { BitmapFactory.decodeByteArray(byteArray, 0, it) }

        val file = bitmapToFile(bitmap)
//
        val requestFile = RequestBody.create(MediaType.parse("image/jpeg"), file)
        val body = MultipartBody.Part.createFormData("imageList", file.name, requestFile)


        // 글 등록 버튼 클릭
        binding.writebutton.setOnClickListener {
//            Log.d("click", "눌렀다")
//            val title = binding.editTitle.text.toString()
//            val content = binding.editWriting.text.toString()
//            val userId = MyApplication.prefs.getInt("userId", 0)
//            val postReqCoursePost = ReqCoursePost("경로인코드스트링", "서울", "출발->목적지",userId, category = "COURSE", title,content)//FREE category,수정해야함
//            Log.d("postReqCoursePost", postReqCoursePost.toString())
//            // 이미지 리스트
//            Log.d("image", body.toString())
//            service.coursePostCreate(requestDTO = postReqCoursePost, listOf(body)).enqueue(object :
//                Callback<ReqCoursePost> {
//                //  @SuppressLint("Range")
//                override fun onResponse(call: Call<ReqCoursePost>, response: Response<ReqCoursePost>) {
//                    if (response.isSuccessful) {
//                        Log.d("req보냈니", "OK")
//                        val result: ReqCoursePost? = response.body()
//                        Log.d("결과",result.toString())
////                        if (response.code() == 201) { //수정해야함
////                            if (result?.code == 100) {
////                                Log.d("게시글작성", "성공: $title")
////                                MainActivity.getInstance()?.writeCheck(true)
////                                finish()
////                            } else {
////                                Log.d("ERR", "실패: " + result?.toString())
////                            }
////                        }
//                    } else {
//                        Log.d("ERR", "onResponse 실패")
//                    }
//                }
//
//                override fun onFailure(call: Call<ReqCoursePost>, t: Throwable) {
//                    Log.d("onFailure", "실패 ")
//                }
//            })


//            val intent = Intent(this, CourseActivity::class.java)
//            RidingService.ridingTimer.observe(this) { time ->
//                intent.putExtra("ridingTime", time) // 시간
//            }
//            intent.putExtra("bitmap", byteArray) // 스냅샷
////        intent.putExtra("courseName", "Android Development") // 거리
////        intent.putExtra("courseName", "Android Development") // 좌표
//            startActivity(intent)

            val intent = Intent(this, MainActivity::class.java)
            intent.putExtra("openBoardFragment", "openBoard")
            startActivity(intent)
//            requireActivity().supportFragmentManager.beginTransaction()
//                .replace(R.id.fragmentLayout, MyPageFragment())
//                .commit()
//            MainActivity.getInstance()?.transfer()
        }
    }

    override fun onMapReady(naverMap: NaverMap) {
        this.naverMap = naverMap
        naverMap.lightness = 0.3f
        for (w in waypoints) {
            val marker = Marker()
            marker.position = LatLng(w.place_lat!!.toDouble(), w.place_lng!!.toDouble())
            marker.map = naverMap
        }
        val pathOverlay = PathOverlay()
        pathOverlay.coords = path
        pathOverlay.map = naverMap
        zoomToSeeWholeTrack(path)
    }

        fun bitmapToFile(bitmap: Bitmap?): File {
        val wrapper = ContextWrapper(applicationContext)
        var file = wrapper.getDir("images", Context.MODE_PRIVATE)
        file = File(file, "${UUID.randomUUID()}.jpg")

        try {
            val stream: OutputStream = FileOutputStream(file)
            bitmap?.compress(Bitmap.CompressFormat.JPEG, 100, stream)
            stream.flush()
            stream.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }

        return file
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

    fun decode(encodedPath: String): List<LatLng> {
        val len = encodedPath.length
        val path: MutableList<LatLng> = ArrayList()
        var index = 0
        var lat = 0
        var lng = 0
        while (index < len) {
            var result = 1
            var shift = 0
            var b: Int
            do {
                b = encodedPath[index++].code - 63 - 1
                result += b shl shift
                shift += 5
            } while (b >= 0x1f)
            lat += if (result and 1 != 0) (result shr 1).inv() else result shr 1
            result = 1
            shift = 0
            do {
                b = encodedPath[index++].code - 63 - 1
                result += b shl shift
                shift += 5
            } while (b >= 0x1f)
            lng += if (result and 1 != 0) (result shr 1).inv() else result shr 1
            path.add(
                LatLng(
                    round(lat * 1e-6 * 10000000) / 10000000,
                    round(lng * 1e-6 * 10000000) / 10000000
                )
            )
        }
        return path
    }

    private fun zoomToSeeWholeTrack(routeLatLng: List<LatLng>) {
        val bounds = LatLngBounds.Builder()

        for (path in routeLatLng) {
            bounds.include(path)
        }

        naverMap.moveCamera(
            CameraUpdate.fitBounds(bounds.build(), 300).animate(CameraAnimation.Fly)
        )
    }

    @SuppressLint("IntentReset")
//    fun openGallery(c:CourseImageAdapter.CourseImageHolder,position: Int): Uri? {
    fun openGallery(c:CourseImageAdapter.CourseImageHolder,position: Int) {
        this.courseImageHolder = c
        this.position = position
        val galleryIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        getContent.launch(galleryIntent)
//        Log.d("uri출력2 in function",selectedImageUri.toString())
//        return selectedImageUri

    }

    @SuppressLint("Range")
    fun getFileName(uri: Uri): String? {
        var result: String? = null
        if (uri.scheme == "content") {
            val cursor: Cursor? = contentResolver.query(uri, null, null, null, null)
            try {
                if (cursor != null && cursor.moveToFirst()) {
                    result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME))
                }
            } finally {
                if (cursor != null) {
                    cursor.close()
                }
            }
        }
        if (result == null) {
            result = uri.lastPathSegment
        }
        return result
    }

}