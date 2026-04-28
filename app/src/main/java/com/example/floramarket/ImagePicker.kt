package com.example.floramarket

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import java.io.File
import java.io.FileOutputStream

@Composable
fun rememberImagePicker(
    showPicker: Boolean,
    onImageSelected: (Uri) -> Unit,
    onPickerDismissed: () -> Unit
) {
    val context = LocalContext.current

    val saveImageToInternal: (Uri) -> String? = remember { { uri ->
        try {
            val inputStream = context.contentResolver.openInputStream(uri) ?: return@remember null
            val fileName = "bouquet_${System.currentTimeMillis()}.jpg"
            val file = File(context.filesDir, fileName)
            FileOutputStream(file).use { outputStream ->
                inputStream.copyTo(outputStream)
            }
            inputStream.close()
            "file://${file.absolutePath}"
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    } }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        uri?.let {
            val localPath = saveImageToInternal(it)
            localPath?.let { path ->
                onImageSelected(Uri.parse(path))
            }
        }
        onPickerDismissed()
    }

    LaunchedEffect(showPicker) {
        if (showPicker){
            launcher.launch(PickVisualMediaRequest())
        }
    }
}