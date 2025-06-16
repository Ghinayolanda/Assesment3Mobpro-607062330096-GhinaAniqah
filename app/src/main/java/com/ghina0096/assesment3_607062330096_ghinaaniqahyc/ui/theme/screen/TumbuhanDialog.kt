package com.ghina0096.assesment3_607062330096_ghinaaniqahyc.ui.theme.screen

import android.content.ContentResolver
import android.content.Context
import android.content.res.Configuration
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
//import androidx.compose.material.icons.filled.BrokenImage
//import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.canhub.cropper.CropImageContract
import com.canhub.cropper.CropImageContractOptions
import com.canhub.cropper.CropImageOptions
import com.canhub.cropper.CropImageView
import com.ghina0096.assesment3_607062330096_ghinaaniqahyc.R
import com.ghina0096.assesment3_607062330096_ghinaaniqahyc.model.Tumbuhan
import com.ghina0096.assesment3_607062330096_ghinaaniqahyc.network.TumbuhanApi // Import your TumbuhanApi
import com.ghina0096.assesment3_607062330096_ghinaaniqahyc.ui.theme.Assesment3_607062330096_GhinaAniqahYCTheme

@Composable
fun TumbuhanDialog(
    bitmap: Bitmap?, // This bitmap is for a NEW image selected within the dialog
    tumbuhan: Tumbuhan? = null, // The Tumbuhan object for editing
    onDismissRequest: () -> Unit,
    onConfirmation: (String, String, Bitmap?) -> Unit, // Changed to allow nullable bitmap for update
    onImageSelected: (Bitmap?) -> Unit // Callback to send selected bitmap back to MainScreen
) {
    var namaTumbuhan by remember(tumbuhan) { mutableStateOf(tumbuhan?.namaTumbuhan ?: "") }
    var namalatin by remember(tumbuhan) { mutableStateOf(tumbuhan?.namaLatin ?: "") }

    val context = LocalContext.current

    val launcher = rememberLauncherForActivityResult(CropImageContract()) { result ->
        val selectedBitmap = getCroppedImage(context.contentResolver, result)
        onImageSelected(selectedBitmap) // Send the new bitmap back to MainScreen
    }

    Dialog(onDismissRequest = { onDismissRequest() }) {
        Card(
            modifier = Modifier.padding(16.dp),
            shape = RoundedCornerShape(16.dp),
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                // Image display area with clickable icon for selection
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(1f)
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                        .clickable {
                            val options = CropImageContractOptions(
                                null, CropImageOptions(
                                    imageSourceIncludeGallery = true,
                                    imageSourceIncludeCamera = true,
                                    fixAspectRatio = true
                                )
                            )
                            launcher.launch(options)
                        },
                    contentAlignment = Alignment.Center
                ) {
                    // Display newly selected image if available
                    if (bitmap != null) {
                        Image(
                            bitmap = bitmap.asImageBitmap(),
                            contentDescription = null,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    } else if (tumbuhan?.imageId != null && tumbuhan.imageId.isNotEmpty()) {
                        // Display existing image if editing and no new image is selected
                        AsyncImage(
                            model = ImageRequest.Builder(LocalContext.current)
                                .data(TumbuhanApi.getTumbuhanImageUrl(tumbuhan.imageId))
                                .crossfade(true)
                                .build(),
                            contentDescription = null,
                            contentScale = ContentScale.Crop,
                            placeholder = painterResource(id = R.drawable.loading_img),
                            error = painterResource(id = R.drawable.broken_img),
                            modifier = Modifier.fillMaxSize()
                        )
                    } else {
                        // Placeholder icon if no image
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                imageVector = Icons.Default.CameraAlt,
                                contentDescription = stringResource(R.string.pilihGambar),
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.height(60.dp) // Make icon larger
                            )
                            Text(stringResource(R.string.pilihGambar))
                        }
                    }
                }


                OutlinedTextField(
                    value = namaTumbuhan,
                    onValueChange = { namaTumbuhan = it },
                    label = { Text(text = stringResource(id = R.string.nama)) },
                    maxLines = 1,
                    keyboardOptions = KeyboardOptions(
                        capitalization = KeyboardCapitalization.Words,
                        imeAction = ImeAction.Next
                    ),
                    modifier = Modifier.padding(top = 8.dp)
                )
                OutlinedTextField(
                    value = namalatin,
                    onValueChange = { namalatin = it },
                    label = { Text(text = stringResource(id = R.string.nama_latin)) },
                    maxLines = 1,
                    keyboardOptions = KeyboardOptions(
                        capitalization = KeyboardCapitalization.Sentences,
                        imeAction = ImeAction.Done
                    ),
                    modifier = Modifier.padding(top = 8.dp)
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp),
                    horizontalArrangement = Arrangement.Center
                ) {
                    OutlinedButton(
                        onClick = { onDismissRequest() },
                        modifier = Modifier.padding(8.dp)
                    ) {
                        Text(text = stringResource(R.string.batal))
                    }
                    OutlinedButton(
                        onClick = { onConfirmation(namaTumbuhan, namalatin, bitmap) }, // Pass bitmap to confirmation
                        enabled = namaTumbuhan.isNotEmpty() && namalatin.isNotEmpty() && (bitmap != null || tumbuhan != null), // Enable if fields are not empty AND (new bitmap or existing data)
                        modifier = Modifier.padding(8.dp)
                    ) {
                        Text(text = stringResource(R.string.simpan))
                    }
                }
            }
        }
    }
}

// Helper function to get cropped image (can be moved outside if already exists)
private fun getCroppedImage(
    resolver: ContentResolver,
    result: CropImageView.CropResult
): Bitmap? {
    if (!result.isSuccessful) {
        Log.e("IMAGE", "Error: ${result.error}")
        return null
    }

    val uri = result.uriContent ?: return null

    return if (Build.VERSION.SDK_INT < Build.VERSION_CODES.P) {
        MediaStore.Images.Media.getBitmap(resolver, uri)
    } else {
        val source = ImageDecoder.createSource(resolver, uri)
        ImageDecoder.decodeBitmap(source)
    }
}


@Preview(showBackground = true)
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES, showBackground = true)
@Composable
fun TumbuhanDialogPreview() {
    Assesment3_607062330096_GhinaAniqahYCTheme {
        TumbuhanDialog(
            bitmap = null,
            onDismissRequest = {},
            onConfirmation = { _, _, _ -> }, // Updated signature
            onImageSelected = {}
        )
    }
}