package org.calamarfederal.messyink

import android.content.ContentResolver
import android.content.ContentValues
import android.graphics.Bitmap
import android.provider.MediaStore
import androidx.test.platform.app.InstrumentationRegistry

fun saveScreenshot(
    filename: String,
    bmp: Bitmap,
    format: Bitmap.CompressFormat = Bitmap.CompressFormat.PNG,
    quality: Int = 100,
    resolver: ContentResolver = InstrumentationRegistry.getInstrumentation().targetContext.contentResolver
) {
    val imageCollection = MediaStore.Images.Media.EXTERNAL_CONTENT_URI

    val imageDetails = ContentValues().apply {
        put(MediaStore.Images.Media.DISPLAY_NAME, filename)
        put(MediaStore.Images.Media.IS_PENDING, 1)
    }

    val uri = resolver.insert(imageCollection, imageDetails)!!

    resolver.openOutputStream(uri).use {
        it?.let {
            bmp.compress(format, quality, it)
            it.flush()
        }
    }

    imageDetails.clear()
    imageDetails.put(MediaStore.Images.Media.IS_PENDING, 0)
    resolver.update(uri, imageDetails, null, null)

    println("Saved screenshot $filename")
}
