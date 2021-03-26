package co.invoid.flutter.image_helper_adv_flutterdemo

import android.R.attr
import android.app.Activity.RESULT_OK
import android.content.*
import android.content.ContentValues.TAG
import android.os.BatteryManager
import android.os.Build.VERSION
import android.os.Build.VERSION_CODES
import android.util.Log
import androidx.annotation.NonNull
import co.invoid.inimagehelperadvance.ImageHelper
import co.invoid.inimagehelperadvance.ImageHelperOptions
import co.invoid.inimagehelperadvance.ImageHelperResult
import io.flutter.embedding.android.FlutterActivity
import io.flutter.embedding.engine.FlutterEngine
import io.flutter.plugin.common.MethodChannel
import co.invoid.inimagehelperadvance.utils.DocumentType



class MainActivity: FlutterActivity() {

    private val CHANNEL = "imagehelperadvance"
    private lateinit var result: MethodChannel.Result


    override fun configureFlutterEngine(@NonNull flutterEngine: FlutterEngine) {
        super.configureFlutterEngine(flutterEngine)
        MethodChannel(flutterEngine.dartExecutor.binaryMessenger, CHANNEL).setMethodCallHandler { call, result ->
            if (call.method == "startImageHelper") {
                val authKey: String = call.argument<String>("authKey") as String
                this.result = result

                val imageHelperOptions = ImageHelperOptions.Builder()
                        .setPhotoOption(ImageHelperOptions.PhotoOptions.SELFIE_WITH_DOC_PHOTO)
                        .setDocumentType(DocumentType.AADHAAR)
                        .setGalleryOption(ImageHelperOptions.GalleryOption.ALLOW_IN_DOC_ONLY)
                        .build()
                ImageHelper.with(this, authKey, imageHelperOptions).start()

            } else {
                result.notImplemented()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(requestCode == ImageHelper.PHOTO_HELPER_REQ_CODE) {

            val map = HashMap<String, Any>()

            if (resultCode == RESULT_OK) {
                val imageHelperResult: ImageHelperResult? = data?.getParcelableExtra(ImageHelper.IMAGE_RESULT)

                map["authorized"] = true
                map["status"] = 1

                val selfiePath = imageHelperResult?.selfieResult?.selfiePath ?: ""

                val docFrontPath = imageHelperResult?.documentResult?.docFrontPath ?: ""
                val docFrontFullPath = imageHelperResult?.documentResult?.docFullFrontPath ?: ""
                val isDocFrontClear = imageHelperResult?.documentResult?.isDocFrontClear ?: false
                val isGlareInFront = imageHelperResult?.documentResult?.isGlareInFront ?: false
                val isBlurInFront = imageHelperResult?.documentResult?.isBlurInFront ?: false

                val docBackPath = imageHelperResult?.documentResult?.docBackPath ?: ""
                val docBackFullPath = imageHelperResult?.documentResult?.docBackPath ?: ""
                val isDocBackClear = imageHelperResult?.documentResult?.isDocBackClear ?: false
                val isGlareInBack = imageHelperResult?.documentResult?.isGlareInBack ?: false
                val isBlurInBack = imageHelperResult?.documentResult?.isBlurInBack ?: false

                val isFaceInDoc = imageHelperResult?.documentResult?.isFaceInDoc ?: false

                map["selfiePath"] = selfiePath

                map["docFrontPath"] = docFrontPath
                map["docFrontFullPath"] = docFrontFullPath
                map["isGlareInFront"] = isGlareInFront
                map["isBlurInFront"] = isBlurInFront
                map["isDocFrontClear"] = isDocFrontClear

                map["docBackPath"] = docBackPath
                map["docBackFullPath"] = docBackFullPath
                map["isGlareInBack"] = isGlareInBack
                map["isBlurInBack"] = isBlurInBack
                map["isDocBackClear"] = isDocBackClear

                map["isFaceInDoc"] = isFaceInDoc

                result.success(map)

            } else if (resultCode == ImageHelper.AUTHORIZATION_RESULT_CODE) {
                val authorizationResult: Int = data!!.getIntExtra(ImageHelper.AUTHORIZATION_RESULT, -1)
                if (authorizationResult == ImageHelper.UNAUTHORIZED) {
                    map["authorized"] = false
                    map["status"] = 2
                    result.success(map)
                } else {
                    map["authorized"] = false
                    map["status"] = 3
                    result.success(map)
                }
            }

        }

    }

}
