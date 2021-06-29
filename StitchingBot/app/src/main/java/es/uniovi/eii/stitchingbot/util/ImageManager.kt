package es.uniovi.eii.stitchingbot.util

import android.annotation.SuppressLint
import android.app.Activity
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.net.Uri
import android.os.Environment
import androidx.exifinterface.media.ExifInterface
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*

class ImageManager() {

    fun getImageFromUri(imageUri: Uri?, activity: Activity): Bitmap? {
        var image: Bitmap
        if (imageUri != null) {
            activity.contentResolver.openFileDescriptor(imageUri, "r")
                .use { pfd ->
                    if (pfd != null) {
                        val matrix = Matrix()
                        matrix.postRotate(180F)
                        image = BitmapFactory.decodeFileDescriptor(pfd.fileDescriptor)
                        //Rotate bitmap
                        val ei = activity.contentResolver.openInputStream(imageUri)?.let {
                            ExifInterface(
                                it
                            )
                        }
                        val orientation: Int = ei!!.getAttributeInt(
                            ExifInterface.TAG_ORIENTATION,
                            ExifInterface.ORIENTATION_UNDEFINED
                        )

                        return when (orientation) {
                            ExifInterface.ORIENTATION_ROTATE_90 -> rotateImage(image, 90F)
                            ExifInterface.ORIENTATION_ROTATE_180 -> rotateImage(image, 180F)
                            ExifInterface.ORIENTATION_ROTATE_270 -> rotateImage(image, 270F)
                            ExifInterface.ORIENTATION_NORMAL -> image
                            else -> image
                        }
                    }
                }

        }
        return null
    }


    private fun rotateImage(source: Bitmap, angle: Float): Bitmap? {
        val matrix = Matrix()
        matrix.postRotate(angle)
        return Bitmap.createBitmap(
            source, 0, 0, source.width, source.height,
            matrix, true
        )
    }

    fun deleteImageFile(uri: Uri) {
        //TODO hacer mensaje de confirmación
        val file = File(uri.path)
        if (file.exists()) {
            file.delete()
        }
    }


    fun copyImage(bitmap: Bitmap?, createdImageFile: File) {
        val destination = FileOutputStream(createdImageFile)
        if (bitmap != null) {
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, destination)
            destination.flush()
        }
        destination.close()
    }


    @SuppressLint("SimpleDateFormat")
    fun createImageFile(activity: Activity): File {
        // Create an image file name
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val storageDir: File? =
            activity.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(
            "SEWMACH_${timeStamp}_", /* prefix */
            ".jpg", /* suffix */
            storageDir /* directory */
        )
//            .apply {
//            // Save a file: path for use with ACTION_VIEW intents
//            currentPhotoPath = absolutePath
//        }
    }
}