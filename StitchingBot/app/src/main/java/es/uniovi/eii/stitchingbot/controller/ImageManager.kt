package es.uniovi.eii.stitchingbot.controller

import android.app.Activity
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.net.Uri
import android.os.Environment
import androidx.exifinterface.media.ExifInterface
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class ImageManager {

    /**
     * Obtiene una imagen de una url dada
     *
     * Abre el archivo especificado en la url y lo rota para que la imagen siempre se vea
     * correctamente
     *
     * @param url Url del archivo que se desea abrir
     * @param activity Actividad desde la que se llama a la función
     * @return Bitmap con la imagen cargada y rotada, null si el archivo no existe
     */
    fun getImageFromUri(url: String?, activity: Activity): Bitmap? {
        var image: Bitmap
        val selectedUri = Uri.parse(url)
        if (selectedUri != null && !url.isNullOrEmpty()) {
            activity.contentResolver.openFileDescriptor(selectedUri, "r")
                .use { pfd ->
                    if (pfd != null) {
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

    /**
     * Rota una imagen
     *
     * @param source Bitmap con la imagen que se desea rotar
     * @param angle Ángulo que se desea rotar la imagen
     * @return Bitmap con la imagen rotada
     */
    private fun rotateImage(source: Bitmap, angle: Float): Bitmap? {
        val matrix = Matrix()
        matrix.postRotate(angle)
        return Bitmap.createBitmap(
            source, 0, 0, source.width, source.height,
            matrix, true
        )
    }

    /**
     * Elimina una imagen si ya esta guardada en el dispositivo
     *
     * @param url de la imagen.
     */
    fun deleteImageIfSaved(url: String?) {
        if (!url.isNullOrEmpty()) {
            deleteImageFile(url)
        }
    }

    /**
     * Elimina una imagen guardada en una url
     *
     * @param url url del archivo que se desea eliminar
     */
    fun deleteImageFile(url: String?) {
        val file = File(Uri.parse(url).path!!)
        if (file.exists()) {
            file.delete()
        }
    }

    /**
     * Guarda una imagen en un archivo y devuelve la dirección de este archivo
     *
     * @param bitmap Bitmap con la imagen que se desea guardar
     * @param activity actividad desde la que se llama a la función
     *
     * @return Uri del archivo creado
     */
    fun saveImageReturningUri(bitmap: Bitmap?, activity: Activity): Uri {
        val file = createImageFile(activity)
        saveImageToFile(bitmap, file!!)
        return Uri.fromFile(file)
    }

    /**
     * Copia una imagen en un archivo ya creado
     *
     * @param bitmap Bitmap con la imagen que se desea guardar
     * @param url Url del archivo de la imagen
     */
    fun copyImage(bitmap: Bitmap?, url: String?) {
        val file = File(Uri.parse(url).path!!)
        saveImageToFile(bitmap, file)
    }

    /**
     * Copia una imagen obtenida de la galeria del usuario
     *
     * @param selectedImage url de la imagen seleccionada
     * @param file archivo al que se quiere copiar la imagen
     * @param activity Actividad desde la que se llama a esta función
     */
    fun copyImageFromGallery(selectedImage: String, file: File, activity: Activity) {
        val bitmap = getImageFromUri(
            selectedImage,
            activity
        )
        saveImageToFile(bitmap, file)
    }

    /**
     * Guarda una imagen en un archivo
     *
     * @param bitmap Bitmap de la imagen que se desea guardar
     * @param file archivo en el que se quiere guardar la imagen
     */
    private fun saveImageToFile(bitmap: Bitmap?, file: File) {
        val destination = FileOutputStream(file)
        if (bitmap != null) {
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, destination)
            destination.flush()
        }
        destination.close()
    }

    /**
     * Crea un archivo para guardar la imagen
     *
     * Se crea un nombre para el archivo basado en la fecha y hora en el que se genera,
     * con el sufijo '.jpg'
     *
     * @param activity actividad desde la que se llama al método
     * @return File generado
     */
    fun createImageFile(activity: Activity): File? {
        val photoFile: File? = try {
            val timeStamp: String =
                SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
            val storageDir: File? =
                activity.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
            return File.createTempFile(
                "STITCHING_${timeStamp}_",
                ".jpg",
                storageDir
            )
        } catch (ex: IOException) {
            null
        }
        return photoFile
    }
}