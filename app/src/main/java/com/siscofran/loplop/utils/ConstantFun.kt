package com.siscofran.loplop.utils

import android.Manifest
import android.content.ContentValues
import android.content.Context
import android.content.pm.PackageManager
import android.content.res.Resources
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import java.io.*
import java.net.HttpURLConnection
import java.net.URL
import java.util.*


fun logi(msg: String){
    Log.i("==LOPLOP==", "logi: $msg")
}

fun loge(msg: String){
    Log.e("==LOPLOP==", "loge: $msg")
}

fun Context.toast(msg: String){
    Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
}

fun getAge(year: Int, month: Int, day: Int): Int {
    val dob = Calendar.getInstance()
    val today = Calendar.getInstance()

    dob.set(year, month, day)

    var age = today.get(Calendar.YEAR) - dob.get(Calendar.YEAR)
    if (today.get(Calendar.DAY_OF_YEAR) < dob.get(Calendar.DAY_OF_YEAR)) age--

    return age
}

fun Context.saveName(name: String, date: String, email: String) {
    val pref = this.getSharedPreferences("profile", Context.MODE_PRIVATE)
    pref.edit().putString("name", name).apply()
    pref.edit().putString("date", date).apply()
    pref.edit().putString("email", email).apply()
}

fun Context.saveGender(gender: String, interest: String) {
    val pref = this.getSharedPreferences("profile", Context.MODE_PRIVATE)
    pref.edit().putString("gender", gender).apply()
    pref.edit().putString("interest", interest).apply()
}

fun Context.saveImage(position: Int, image: String) {
    val pref = this.getSharedPreferences("profile", Context.MODE_PRIVATE)
    pref.edit().putString("image$position", image).apply()
}

fun Context.saveHobby(hobby: ArrayList<String>) {
    val pref = this.getSharedPreferences("profile", Context.MODE_PRIVATE)
    val mHobby: MutableSet<String> = HashSet()
    mHobby.addAll(hobby)
    pref.edit().putStringSet("hobby", mHobby).apply()
}

fun Context.saveLocationTracking(locationUpdate: String){
    val pref = this.getSharedPreferences("profile", Context.MODE_PRIVATE)
    pref.edit().putString("liveLocation", locationUpdate).apply()
}

fun Context.prefGetDate(): String{
    val pref = this.getSharedPreferences("profile", Context.MODE_PRIVATE)
    return pref.getString("date", "") ?: ""
}

fun Context.prefGetName(): String{
    val pref = this.getSharedPreferences("profile", Context.MODE_PRIVATE)
    return pref.getString("name", "") ?: ""
}

fun Context.prefGetGender(): String{
    val pref = this.getSharedPreferences("profile", Context.MODE_PRIVATE)
    return pref.getString("gender", "") ?: ""
}

fun Context.prefGetInterest(): String{
    val pref = this.getSharedPreferences("profile", Context.MODE_PRIVATE)
    return pref.getString("interest", "") ?: ""
}

fun Context.prefGetImage(position: Int): String{
    val pref = this.getSharedPreferences("profile", Context.MODE_PRIVATE)
    return pref.getString("image$position", "") ?: ""
}

fun Context.prefGetHobby(): MutableSet<String>? {
    val pref = this.getSharedPreferences("profile", Context.MODE_PRIVATE)
    return pref.getStringSet("hobby", null)
}

fun Context.prefGetEmail(): String {
    val pref = this.getSharedPreferences("profile", Context.MODE_PRIVATE)
    return pref.getString("email", "") ?: ""
}

fun Context.prefGetLocationTracking(): String{
    val pref = this.getSharedPreferences("profile", Context.MODE_PRIVATE)
    return pref.getString("liveLocation", "") ?: ""
}

interface CallbackData<T> {
    fun callback(data: T)
}

fun getPath(context: Context, uri: Uri?): String? {
    var cursor: Cursor? = null
    return try {
        val proj = arrayOf(MediaStore.Images.Media.DATA)
        cursor = uri?.let { context.contentResolver.query(it, proj, null, null, null) }
        val column_index = cursor!!.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
        cursor.moveToFirst()
        cursor.getString(column_index)
    } catch (e: Exception) {
        loge("getRealPathFromURI Exception : $e")
        ""
    } finally {
        cursor?.close()
    }
}

fun getAbsoluteFile(relativePath: String, context: Context): File {
    return if (Environment.MEDIA_MOUNTED == Environment.getExternalStorageState()) {
        File(context.getExternalFilesDir("null"), relativePath)
    } else {
        File(context.filesDir, relativePath)
    }
}

@Throws(IOException::class)
fun saveImage(context: Context, bitmap: Bitmap, folderName: String, fileName: String): Uri? {
    val fos: OutputStream?
    var imageFile: File? = null
    var imageUri: Uri? = null
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        val resolver = context.contentResolver
        val contentValues = ContentValues()
        contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME, fileName)
        contentValues.put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
        contentValues.put(
            MediaStore.MediaColumns.RELATIVE_PATH,
            Environment.DIRECTORY_DCIM + File.separator + folderName
        )
        imageUri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
        fos = resolver.openOutputStream(imageUri!!)
    } else {
        val imagesDir = Environment.getExternalStoragePublicDirectory(
            Environment.DIRECTORY_DCIM
        ).toString() + File.separator + folderName
        imageFile = File(imagesDir)
        if (!imageFile.exists()) {
            imageFile.mkdir()
        }
        imageFile = File(imagesDir, "$fileName.jpeg")
        fos = FileOutputStream(imageFile)
    }
    bitmap.compress(Bitmap.CompressFormat.PNG, 90, fos)
    fos?.flush()
    fos?.close()
    if (imageFile != null) // pre Q
    {
        MediaScannerConnection.scanFile(context, arrayOf(imageFile.toString()), null, null)
        imageUri = Uri.fromFile(imageFile)
    }
    return imageUri
}

@Throws(IOException::class)
fun downloadFromUrl(url: String?): Drawable {
    val x: Bitmap
    val connection: HttpURLConnection = URL(url).openConnection() as HttpURLConnection
    connection.connect()
    val input: InputStream = connection.inputStream
    x = BitmapFactory.decodeStream(input)
    return BitmapDrawable(Resources.getSystem(), x)
}

fun checkPermissionGranted(context: Context) : Boolean {
    return ActivityCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED //permission already granted
}

fun Bitmap.rotate(degrees: Float): Bitmap {
    val matrix = Matrix().apply { postRotate(degrees) }
    return Bitmap.createBitmap(this, 0, 0, width, height, matrix, true)
}

//fun getPathFromURI(context: Context, uri: Uri): String? {
//    val isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT
//
//    // DocumentProvider
//    if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
//        // ExternalStorageProvider
//        if (isExternalStorageDocument(uri)) {
//            val docId = DocumentsContract.getDocumentId(uri)
//            val split = docId.split(":".toRegex()).toTypedArray()
//            val type = split[0]
//            if ("primary".equals(type, ignoreCase = true)) {
//                return Environment.getExternalStorageDirectory().toString() + "/" + split[1]
//            }
//        } else if (isDownloadsDocument(uri)) {
//            val id = DocumentsContract.getDocumentId(uri)
//            val contentUri: Uri = ContentUris.withAppendedId(
//                Uri.parse("content://downloads/public_downloads"),
//                java.lang.Long.valueOf(id)
//            )
//            return getDataColumn(context, contentUri, null, null)
//        } else if (isMediaDocument(uri)) {
//            val docId = DocumentsContract.getDocumentId(uri)
//            val split = docId.split(":".toRegex()).toTypedArray()
//            val type = split[0]
//            var contentUri: Uri? = null
//            if ("image" == type) {
//                contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
//            } else if ("video" == type) {
//                contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI
//            } else if ("audio" == type) {
//                contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
//            }
//            val selection = "_id=?"
//            val selectionArgs = arrayOf(
//                split[1]
//            )
//            return getDataColumn(context, contentUri, selection, selectionArgs)
//        }
//    } else if ("content".equals(uri.scheme, ignoreCase = true)) {
//        return getDataColumn(context, uri, null, null)
//    } else if ("file".equals(uri.scheme, ignoreCase = true)) {
//        return uri.path
//    }
//    return null
//}
//
//fun getDataColumn(
//    context: Context, uri: Uri?, selection: String?,
//    selectionArgs: Array<String>?
//): String? {
//    var cursor: Cursor? = null
//    val column = "_data"
//    val projection = arrayOf(
//        column
//    )
//    try {
//        cursor = context.contentResolver.query(
//            uri!!, projection, selection, selectionArgs,
//            null
//        )
//        if (cursor != null && cursor.moveToFirst()) {
//            val columnIndex: Int = cursor.getColumnIndexOrThrow(column)
//            return cursor.getString(columnIndex)
//        }
//    } finally {
//        cursor?.close()
//    }
//    return null
//}
//
//fun isExternalStorageDocument(uri: Uri): Boolean {
//    return "com.android.externalstorage.documents" == uri.authority
//}
//
//fun isDownloadsDocument(uri: Uri): Boolean {
//    return "com.android.providers.downloads.documents" == uri.authority
//}
//
//fun isMediaDocument(uri: Uri): Boolean {
//    return "com.android.providers.media.documents" == uri.authority
//}