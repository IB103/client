package com.hansung.capstone.map

import android.Manifest
import android.Manifest.permission.ACCESS_COARSE_LOCATION
import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.content.ContentValues
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.location.LocationManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.UiThread
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat.checkSelfPermission
import androidx.fragment.app.Fragment
import com.google.android.gms.maps.GoogleMap
import com.hansung.capstone.R
import com.hansung.capstone.databinding.FragmentMapBinding
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.*
import com.naver.maps.map.MapFragment
import com.naver.maps.map.overlay.Marker
import com.naver.maps.map.overlay.PathOverlay
import com.naver.maps.map.util.FusedLocationSource
import kotlinx.android.synthetic.main.fragment_map.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream
import kotlin.math.round

class MapFragment : Fragment(), OnMapReadyCallback, NaverMap.SnapshotReadyCallback {
    private lateinit var naverMap: NaverMap
    private lateinit var googleMap: GoogleMap
    private lateinit var locationSource: FusedLocationSource
    private lateinit var binding: FragmentMapBinding

    // 추가
    val permissions = arrayOf(
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION,
    )

    companion object {
        val path = PathOverlay()
        val staticMarker = Marker()
        var markers = arrayListOf<Marker>()
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1000

//        private val PERMISSIONS = arrayOf(
//            Manifest.permission.ACCESS_FINE_LOCATION,
//            Manifest.permission.ACCESS_COARSE_LOCATION
//        )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMapBinding.inflate(inflater, container, false)
        return binding.root
//        return inflater.inflate(R.layout.fragment_map, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 권한 요청
        val contract = ActivityResultContracts.RequestMultiplePermissions()
        val activityResultLauncher  = registerForActivityResult(contract) {
                resultMap ->
            val isAllGranted = permissions.all { e -> resultMap[e] == true }

            if (isAllGranted) {
                // 모든 권한이 필요한 작업 수행
                Log.d("허용","ㅇㅇ")
            }

//            if (resultMap[Manifest.permission.RECORD_AUDIO] == true) {
//                // 일부 권한만 필요한 작업 수행
//            }
        }

//        LocationManager

        activityResultLauncher.launch(permissions)
        // 위치소스 권한 설정
        locationSource =
            activity?.let { FusedLocationSource(it, LOCATION_PERMISSION_REQUEST_CODE) }!!

        // 초기 옵션대로 생성
        val fm = childFragmentManager
        val mapFragment = fm.findFragmentById(R.id.mapView) as MapFragment?
            ?: MapFragment.newInstance().also {
                fm.beginTransaction().add(R.id.mapView, it).commit()
            }
        mapFragment.getMapAsync(this)

        val decodeString = "wiytfA_dbgqFQ?k@nJaHBDrq@{IxA@um@@uQk@i`@A_NKgFKkKmDoq@vR\\jNTnY[lE}Qb@iB^wQaCcLmN}]oR{\\_MmWwBeJfKyVlLmWMaZYoWlA{J|ByGbEmFtDcD`GoBp\\o@zH{@|EaAjNwI`JaIzGbHvFb@vHj@bBz@ZZhCgAdKmBzEqCbJpN\\^bAhAlD|E|EpG|EtF|FxKpAdC|AxCpHVjJwAlGmFjAaAxF_B|JoA|JiAnCYnFYnAR~OeJtAy@bJm@~LoAfGeAdGi@tH{@~Us@dUFZ~ONTxEtH`HzK`BbLJzD\\tP~NLhg@k@`Xh@n]r@bS`@lDMNAjBI~Qo@v\\mAjHQzYiAzPm@xBqJbCge@N_Dn@{RUmMyB}SkB_^]oNN}EoFyI}C{DnLeJbAy@~@q@h@i@vNuLjAaAn@e@dIeGxFyEbB_ApG}AjJcAvJEzJy@tK{AvFjEHH~CtBlD~AtIh@dCG`BE~HaAlFiAbB]~GwArEmCfC}AbAg@vC@fMBjNB`NDpC@bICN?pNCxLCrMCdD?l@AjRCnAAdTCpCYjDOvEQzXeAhPDbPOrWSpFs@zi@tV`E~B`GpDxp@~\\hLpFhk@hXpNtGn^uh@v[}d@|Z_d@bCmDnQmW`JcBnJwA~GvFd@`Iw@pLpBnKrEvBvYu@dFtA~AzDRfPdFnRpB_AjgA_g@tF_CxMqFZKXGfGgAtCg@vI[vJ_@fO?jW?|YTtKFjLI`Kk@tJs@|JeDhBwA`CkBdE_EfG}F~DoDtCeBx@U`V{@hZcAvLk@nEu@bCoArFuCjHsFpAmCzEeMdAmCnDgKXy@xBdChAv@x@l@n@d@jFlCpBb@pDd@nF^rFl@lCTvNNpJHpF^v@YfBm@tJmDtLJdB@tASjBcB`CkC`F{B~QpGnAnAfCd@tD|AtB^nNc@`NAbD`CvAvBvJEzDeBpF~KtBhEfCjFbSbb@vCfHfDbIfNnYr@zA`DjGpNzXdIzMlb@nh@n@dAbHpJfOfTxCfFhIvLr@jApHmCzGjEnElBzF^v^eArKe@~d@qB~XkEzB]dd@_HxZoHdVwI~n@}Zzb@mVp@hBhAnCTh@`@x@hBnCpFdIlCdDhEpFVVfAr@jFjDlAz@rBpBnArArEpF|BpCjEpElGxGzDbEhAjApCzBtA~@hAn@|GdDlI|E`H~D~BzAfAx@pJpGvEvCnAz@rK~F|QlKxOlJjI`FtQbK~L|Gd[lPlQzIpe@xWtEpBdA`@vAXn@RjDpAhEvBhNdHzYzP|ObJfEdCbEjBhExAxBn@~D~ArC`BzEdDbEpCb@VfDhBjHlD|SxJrCpA|CvAfGvCbDbBxCjBnCzAdBdAbDnBpA|@vAbAbDfC|BfBbDnCvHnGjCrBxBfBt@n@pCtClGjG|BrBx@t@rAlArAvAfCrCdCnCnAtAzAbB`C|Cz@dAp@bAhAbB`BdCfEpG`@n@bChETb@b@hAl@lBPh@hAbCv@~AlAhBtB|CtB`D`A|AnAbC`@~@dA`CfBpErAzDfAdD`AvDfBnGfFhQvB~G|AjFtA`EfVfr@hG~QrC`JnA~CnAhCpAfCzFbJdMfRnEvHrDzEdJ`MnHdJvF`HxHpIzIdInDbEhKxNpF|HnJrN`E|GrC`GrJvRfAzBvAbE~BxIz@lCv@xAnAnB|@hAxFfGpg@ng@|H`F~MtH`PjHpRzJxI|DfDjAfH|AhNjCnAb@tH`D|GbE~HjElFlD`BjAnIvIjBrBt@tAnA`Ch@p@`DpDlAfBn@~@\\\\xBbBtBdBpA`A\\Zz@pAxBzDvAvCjAzDp@xB\\r@fBvDPZvCnErA|Ar@x@RRLVhBtDlBbEzBhGbClHnBjGrBtGpBlGb@lAxB|FvAxDnAbEnBtG`B~FjB|FtAxDtBpGbHxSnCnJ|DdNzExQj@lBnXteAvFtR|BbIpAfD|A`EvIjStEbQ|B`J`DpL`@lAf@zAt@xA~AlBdC`BvCvAvGbEd]cNt[aMxeAaa@nvBsx@rcBoo@nyCshAnDoAZ~@p@hAlAhAvBh@xBSjBoA^k@Xq@`@mBDwBWqBzq@cZbb@oN|PiGbKuD|GeCzUwE`WqPbX{JjuBqu@z[kOlYcRt^qXjr@sh@pNsL|O}LzX_TfSkK`HgB~I_CjHmBx^iGp~@gLzAOjJcAvPeB|RwChKaC|KiCzMoEpDsAnEeBfQcHnZqJxHA`Ez@hAjArN|Nx@|@bDrEv_@|fBjCzClAtArDdE|FxGpStUfy@mJfDvAfD_BbGcDhAo@tWFxA@nNdGxP?tQH|AuCN]nHmKvD}E~EmGzG_JfEoFtBuBhEiEnG{GrKcLtQmRxGeH`j@yV~GkCzL_Br]sExd@oBlo@~BbIt@hYfC|RfBzSjBzNrAjPxAzr@lG|_@lDKnHzEtc@pE~a@tC?l\\iGvCqAbAyApu@qPbIxu@zC^|]jEf\\tClNnAnFmCdu@cx@~`@zTfI{M|WZ~_@{Tt[_RfVkNbXsObXaRfUxr@|s@ce@pe@`aBj^`pA|g@wYtC~HxF}DzAgAdExJrJsMnGaKHcP|JaL~FwKvBwUnGoT`DyTpHs@jEmJlFaJzHkKnGwGtJkJrHwQlFaGbIqG~MqFdJkDvD_EfC?fCKfUmPrElNlBfJO|Nv@vDl@tJbEtCzBk@~MkNzJ{HlIwAxIoE~Fh@pFlDdUgDrD~@jDhBxP~`@hG~KpM~GtVvK|GDzSg@h]`I~N_AvOwNb^mVzLaEvWaGxDuAdBaIr@wOlJmPxTwOpNi@`PbAzGvDnVdLtHnF~O|JrJpBtXrF~Ee@NfEzL[dAh@bFlAtEmA`Fa@lHdAvBGxDqCxAiBrBiBr@ThAv@fDzGtBtGlDzJrC~EnBhCbEfDnCpDlFlI`FvDtDlJbBrCfIdFrHjEpTvDpN|BtFfCdKw@|FqBfIbArHhCzLrHrLJnGrC|LeBnGeAbBUnE?fGv@nCjDpC?lQfBnGhIhAxJf@hJjC~PpA`OGxTo@rDdAE`FFp^|DtN~AtR}Dpr@eKvScCnBpd@nPoAfKeEr_@uDnl@{GhRyBpY}DrQaExu@gh@jrAy}@|Q{L`iBkuAvdBoeAxR{MlDaCzViPf`@aWh~@kj@lHmE|KyCpJwArj@yA~qAuFI}HnBGbcAwDzv@yAb@zGzo@mAdJBpLtA~MtHzcAhl@lm@t]lIrExWjO`d@zVtGpDvSbLlo@t]xJv@xG}A~DiFzFmSjh@m~A|NkAhRkZtTeRjQwYpBgDdImMnReQ`GiDbHoBpTJpMWpFzHbS`MbOjDtHjIjGlOxZlPvD`GUtB`ItGpJV|FcDjNcYjKod@f]mEb[w_@fCoIJoHlEwZbDwXrFiKdOkD|J{RfZuYpDoD~c@mU`SwHhk@_]zSuHdf@zRxZvEpIuDdXaBhNw@zSoTpXaOjS{Ht@uAvE_JfGmLjDuIpBaFlGeHtAv@x@uAhESrBv@bENvAt@n@[xCdBbBCvAlAnAFn@rAx@[p@V`Bs@vBr@dHb@|JvJdCx@jET`P~GvBfBvF`BxBvC`G~@x@vAqAr@bBrEnQ{BtIyInMjNdUdF|XwEh`@aLn\\_FtDuB^uC~Cu@`EaK`HKuAuFhAaClIuBwGcYtBcWlLmR`IaEjEwKjNuJbKwCaAkEvCkOhDwG`C_@hCwPjFu@|FHbAgd@zEiRpOuPfFmT`Kut@rDmd@bIeo@xFqTrDcR\\aZgCw|@h@g`@[a^u@wf@tCyYi@kSh@aOjEkKxHgYfFgXMyRqDqNg@io@M_]wB_H"

        Log.d("decodeString","$decodeString")
//        val r = decodeString.replace("\\\\","")
//        Log.d("r","$r")
//        val addPath: List<com.google.android.gms.maps.model.LatLng> = PolyUtil.decode(r)
//        val addPath: List<com.google.android.gms.maps.model.LatLng> = PolyUtil.decode(decodeString)
        val addPath: List<com.google.android.gms.maps.model.LatLng> = decode(decodeString)
        Log.d("경로 디코드", addPath.toString())
        Log.d("경로 카운트", addPath.size.toString())
//        val afterPath: String? = PolyUtil.encode(addPath)
        val afterPath: String? = encode(addPath)
        if (afterPath != null) {
            Log.d("경로 인코드", afterPath)
        }
        Log.d("코드비교", (decodeString == afterPath).toString())

        // 검색창에 입력 후 엔터 시 동작
        binding.locationSearch.setOnEditorActionListener { _, id, _ ->
            if ((id == EditorInfo.IME_ACTION_SEARCH) && (binding.locationSearch.text.toString()
                    .isNotBlank())
            ) {
                val imm =
                    activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(binding.locationSearch.windowToken, 0)
                locationSearch()
            } else {
                binding.locationSearch.requestFocus()
                val manager: InputMethodManager =
                    activity?.getSystemService(AppCompatActivity.INPUT_METHOD_SERVICE) as InputMethodManager
                manager.showSoftInput(binding.locationSearch, InputMethodManager.SHOW_IMPLICIT)
                Toast.makeText(activity, "검색어를 입력해주세요", Toast.LENGTH_SHORT).show()
            }
            true
        }
    }


    @Deprecated("Deprecated in Java")
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        if (locationSource.onRequestPermissionsResult(
                requestCode, permissions,
                grantResults
            )
        ) {
            if (!locationSource.isActivated) { // 권한 거부됨
                naverMap.locationTrackingMode = LocationTrackingMode.None
            }
            return
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    private fun locationSearch() {
        // 전에 마커 비우기
        for (i in markers) {
            i.map = null
        }
        markers.clear() // 리스트 비우기
        staticMarker.map = null
        path.map = null
        val api = KakaoSearchAPI.create()

        api.getSearchKeyword(
            com.hansung.capstone.BuildConfig.KAKAO_SEARCH_API_KEY,
            binding.locationSearch.text.toString()
        )
            .enqueue(object : Callback<ResultSearchKeyword> {
                override fun onResponse(
                    call: Call<ResultSearchKeyword>,
                    response: Response<ResultSearchKeyword>
                ) {
                    val body = response.body()
                    Log.d("검색 결과", "Body: ${response.body()}")
                    if (body != null) {
                        if(body.documents.isNotEmpty()) {
                            val resultList = binding.bottomSheet.resultList
                            activity?.runOnUiThread {
                                resultList.adapter =
                                    SearchLocationAdapter(body, naverMap)
                            }

                            val cameraUpdate = CameraUpdate.scrollTo(
                                LatLng(
                                    body.documents[0].y.toDouble(),
                                    body.documents[0].x.toDouble()
                                )
                            )
                                .animate(CameraAnimation.Fly, 1000)
                            naverMap.moveCamera(cameraUpdate)

                            for (item in body.documents) {
                                val marker = Marker()
                                markers.add(marker)
                                marker.position = LatLng(item.y.toDouble(), item.x.toDouble())
                                marker.map = naverMap
                            }
                        }
                    }
                }

                override fun onFailure(call: Call<ResultSearchKeyword>, t: Throwable) {
                    Log.d("결과:", "실패 : $t")
                }
            })

    }

    override fun onSnapshotReady(bitmap: Bitmap) {
        // 여기서 저장
        // Generating a file name
        val filename = "${System.currentTimeMillis()}.jpg"

        // Output stream
        var fos: OutputStream? = null

        // For devices running android >= Q
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            // getting the contentResolver
            activity?.contentResolver?.also { resolver ->

                // Content resolver will process the contentvalues
                val contentValues = ContentValues().apply {

                    // putting file information in content values
                    put(MediaStore.MediaColumns.DISPLAY_NAME, filename)
                    put(MediaStore.MediaColumns.MIME_TYPE, "image/jpg")
                    put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES)
                }

                // Inserting the contentValues to
                // contentResolver and getting the Uri
                val imageUri: Uri? =
                    resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)

                // Opening an outputstream with the Uri that we got
                fos = imageUri?.let { resolver.openOutputStream(it) }
            }
        } else {
            // These for devices running on android < Q
            val imagesDir =
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
            val image = File(imagesDir, filename)
            fos = FileOutputStream(image)
        }

        fos?.use {
            // Finally writing the bitmap to the output stream that we opened
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, it)
            Log.d("비트맵 저장하기", "눌렀음")
//            Toast.makeText(this , "Captured View and saved to Gallery" , Toast.LENGTH_SHORT).show()
        }
    }

    fun viewToBitmap(view: View): Bitmap {
        val bitmap = Bitmap.createBitmap(view.width, view.height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        view.draw(canvas)
        Log.d("비트맵 만들기", "눌렀음")
        return bitmap
    }

    @UiThread
    override fun onMapReady(naverMap: NaverMap) {

        // 메인의 객체와 연결
        this.naverMap = naverMap

        naverMap.locationSource = locationSource

        val uiSettings = naverMap.uiSettings
        uiSettings.isScaleBarEnabled = true
        uiSettings.isCompassEnabled = false
        uiSettings.isZoomControlEnabled = false
        uiSettings.isLocationButtonEnabled = true


        // 가리기
        path.isHideCollidedSymbols = true
        path.isHideCollidedCaptions = true

        // 맵 타입 Basic
        CameraPosition(LatLng(37.5666102, 126.9783881), 100.0)
        naverMap.mapType = NaverMap.MapType.Basic
//        naverMap.mapType = NaverMap.MapType.Navi

        // 지도에 표시할 정보 -> 자전거 도로
        naverMap.setLayerGroupEnabled(NaverMap.LAYER_GROUP_BUILDING, false)
        naverMap.setLayerGroupEnabled(NaverMap.LAYER_GROUP_TRANSIT, false)
        naverMap.setLayerGroupEnabled(NaverMap.LAYER_GROUP_MOUNTAIN, false)
        naverMap.setLayerGroupEnabled(NaverMap.LAYER_GROUP_CADASTRAL, false)
//        naverMap.setLayerGroupEnabled(NaverMap.LAYER_GROUP_BICYCLE, true)
//        naverMap.symbolScale = 1f
//        naverMap.isIndoorEnabled = true

//        binding.snapshotB.setOnClickListener {
//            val bitmapImage: Bitmap = viewToBitmap(mapView)
//            naverMap.takeSnapshot {
//                onSnapshotReady(it)
//            }
//        }
    }

}

fun decode(encodedPath: String): List<com.google.android.gms.maps.model.LatLng> {
    val len = encodedPath.length

    // For speed we preallocate to an upper bound on the final length, then
    // truncate the array before returning.
    val path: MutableList<com.google.android.gms.maps.model.LatLng> = ArrayList()
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
        path.add(com.google.android.gms.maps.model.LatLng(round(lat * 1e-6*10000000)/10000000, round(lng * 1e-6 *10000000)/10000000))
    }
    return path
}

fun encode(path: List<com.google.android.gms.maps.model.LatLng>): String? {
    var lastLat: Long = 0
    var lastLng: Long = 0
    val result = StringBuffer()
    for (point in path) {
        val lat = Math.round(point.latitude * 1e6)
        val lng = Math.round(point.longitude * 1e6)
        val dLat = lat - lastLat
        val dLng = lng - lastLng
        encode(dLat, result)
        encode(dLng, result)
        lastLat = lat
        lastLng = lng
    }
    return result.toString()
}

private fun encode(v: Long, result: StringBuffer) {
    var v = v
    v = if (v < 0) (v shl 1).inv() else v shl 1
    while (v >= 0x20) {
        result.append(Character.toChars((0x20 or (v and 0x1f).toInt()) + 63))
        v = v shr 5
    }
    result.append(Character.toChars((v + 63).toInt()))
}

//private fun requestMultiplePermission(perms: Array<String>) {
//    val requestPerms = perms.filter { checkSelfPermission(it) != PackageManager.PERMISSION_GRANTED } // 여러 권한 확인
//    if (requestPerms.isEmpty())
//        return
//    val requestPermLauncher = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) {
//        val noPerms = it.filter { item -> item.value == false }.keys
//        if (noPerms.isNotEmpty()) { // there is a permission which is not granted!
//            AlertDialog.Builder(this).apply {
//                setTitle("Warning")
//                setMessage(getString(R.string.no_permission, noPerms.toString()))
//            }.show()
//        }
//    }
//    val showRationalePerms = requestPerms.filter {shouldShowRequestPermissionRationale(it)}
//    if (showRationalePerms.isNotEmpty()) {
//// you should explain the reason why this app needs the permission.
////        AlertDialog.Builder(this).apply {
////            setTitle("Reason")
////            setMessage(getString(R.string.req_permission_reason, requestPerms))
////            setPositiveButton("Allow") { _, _ -> requestPermLauncher.launch(requestPerms.toTypedArray()) }
////            setNegativeButton("Deny") { _, _ -> }
////        }.show()
//    } else {
//// should be called in onCreate()
//        requestPermLauncher.launch(requestPerms.toTypedArray())
//    }
//}

