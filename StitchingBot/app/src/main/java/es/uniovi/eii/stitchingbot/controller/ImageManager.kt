package es.uniovi.eii.stitchingbot.controller

import android.annotation.SuppressLint
import android.app.Activity
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.net.Uri
import android.os.Environment
import android.util.Log
import androidx.exifinterface.media.ExifInterface
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class ImageManager {

    fun getImageFromUri(url: String?, activity: Activity): Bitmap? {
        var image: Bitmap
        val selectedUri=Uri.parse(url)
        if (selectedUri != null) {
            activity.contentResolver.openFileDescriptor(selectedUri, "r")
                .use { pfd ->
                    if (pfd != null) {
                        val matrix = Matrix()
                        matrix.postRotate(180F)
                        image = BitmapFactory.decodeFileDescriptor(pfd.fileDescriptor)
                        //Rotate bitmap
                        val ei = activity.contentResolver.openInputStream(selectedUri)?.let {
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

    fun deleteImageIfSaved(url: String?){
        if(!url.isNullOrEmpty()){
            deleteImageFile(url)
        }
    }


    fun deleteImageFile(url: String?) {
        //TODO hacer mensaje de confirmación
        val file = File(Uri.parse(url).path!!)
        if (file.exists()) {
            file.delete()
        }
    }


    fun saveImageReturningUri(bitmap: Bitmap?, activity: Activity):Uri {
        val file = createImageFile(activity)
        saveImageToFile(bitmap, file)
        return Uri.fromFile(file)
    }

    fun copyImage(bitmap: Bitmap?, url:String?) {
        val file = File(Uri.parse(url).path!!)
        saveImageToFile(bitmap, file)
    }

    fun copyImageFromGallery(selectedImage: String, file: File, activity: Activity){
        val bitmap = getImageFromUri(
            selectedImage,
            activity
        )
        saveImageToFile(bitmap, file)
    }


    private fun saveImageToFile(bitmap: Bitmap?, file: File) {
        val destination = FileOutputStream(file)
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
    }

    fun createPhotoFile(activity: Activity): File? {
        // Create the File where the photo should go
        val photoFile: File? = try {
            createImageFile(activity)
        } catch (ex: IOException) {
            // Error occurred while creating the File
            Log.i("ImageManager", "Error creando archivo")
            null
        }

        return photoFile
    }


}