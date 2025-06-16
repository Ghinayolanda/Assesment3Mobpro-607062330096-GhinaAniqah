package com.ghina0096.assesment3_607062330096_ghinaaniqahyc.ui.theme.screen

import android.content.ContentResolver
import android.content.Context
import android.content.res.Configuration
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.os.Build
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.credentials.ClearCredentialStateRequest
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.GetCredentialResponse
import androidx.credentials.exceptions.ClearCredentialException
import androidx.credentials.exceptions.GetCredentialException
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.canhub.cropper.CropImageContract
import com.canhub.cropper.CropImageContractOptions
import com.canhub.cropper.CropImageOptions
import com.canhub.cropper.CropImageView
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenParsingException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import com.ghina0096.assesment3_607062330096_ghinaaniqahyc.BuildConfig
import com.ghina0096.assesment3_607062330096_ghinaaniqahyc.R
import com.ghina0096.assesment3_607062330096_ghinaaniqahyc.model.Tumbuhan
import com.ghina0096.assesment3_607062330096_ghinaaniqahyc.model.User
import com.ghina0096.assesment3_607062330096_ghinaaniqahyc.network.ApiStatus
import com.ghina0096.assesment3_607062330096_ghinaaniqahyc.network.TumbuhanApi
import com.ghina0096.assesment3_607062330096_ghinaaniqahyc.network.UserDataStore
import com.ghina0096.assesment3_607062330096_ghinaaniqahyc.ui.theme.Assesment3_607062330096_GhinaAniqahYCTheme


@Preview(showBackground = true)
@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun MainScreen() {
    val context = LocalContext.current
    val dataStore = UserDataStore(context)
    val user by dataStore.userFlow.collectAsState(User())

    val viewModel: MainViewModel = viewModel()
    val errorMessage by viewModel.errorMessage

    var showDialog by remember { mutableStateOf(false) }
    var showTumbuhanDialog by remember { mutableStateOf(false) }
    var showHapusDialog by remember { mutableStateOf(false) }
    var tumbuhanToDelete by remember { mutableStateOf<Tumbuhan?>(null) }
    var tumbuhanToEdit by remember { mutableStateOf<Tumbuhan?>(null) } // For editing

    var dialogBitmap: Bitmap? by remember { mutableStateOf(null) } // Bitmap to be displayed/used in the dialog

    // We no longer need the launcher in MainScreen, as it's moved to TumbuhanDialog
    // val launcher = rememberLauncherForActivityResult(CropImageContract()) { ... }


    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(text = stringResource(id = R.string.app_name))
                },
                colors = TopAppBarDefaults.mediumTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.primary
                ),
                actions = {
                    IconButton(onClick = {
                        if (user.email.isEmpty()) {
                            CoroutineScope(Dispatchers.IO).launch { signIn(context, dataStore) }
                        } else {
                            showDialog = true
                        }
                    }) {
                        Icon(
                            painter = painterResource(R.drawable.account_circle_24),
                            contentDescription = stringResource(R.string.profil),
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = {
                // For adding a new plant, clear any existing edit state and bitmap
                tumbuhanToEdit = null
                dialogBitmap = null
                showTumbuhanDialog = true // Show the dialog, which will handle image picking
            }) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = stringResource(id = R.string.tambah_tumbuhan)
                )
            }
        }
    ) { innerPadding ->
        ScreenContent(
            viewModel = viewModel,
            userId = user.email,
            currentUserId = user.email,
            onDeleteClick = { tumbuhan ->
                tumbuhanToDelete = tumbuhan
                showHapusDialog = true
            },
            onEditClick = { tumbuhan ->
                tumbuhanToEdit = tumbuhan
                dialogBitmap = null // Start with no new bitmap for edit, user can pick one
                showTumbuhanDialog = true // Show the dialog, which will handle image picking
            },
            modifier = Modifier.padding(innerPadding)
        )

        if (showDialog) {
            ProfilDialog(
                user = user,
                onDismissRequest = { showDialog = false }) {
                CoroutineScope(Dispatchers.IO).launch { signOut(context, dataStore) }
                showDialog = false
            }
        }

        if (showTumbuhanDialog) {
            TumbuhanDialog(
                bitmap = dialogBitmap, // Pass the bitmap from MainScreen state
                tumbuhan = tumbuhanToEdit, // Pass the Tumbuhan object for pre-filling text fields
                onDismissRequest = {
                    showTumbuhanDialog = false
                    tumbuhanToEdit = null // Clear edit state
                    dialogBitmap = null // Clear bitmap
                },
                onConfirmation = { namaTumbuhan, namaLatin, newBitmap -> // Receive newBitmap from dialog
                    if (tumbuhanToEdit == null) {
                        // This is an insert operation
                        if (newBitmap != null) { // Ensure bitmap is not null for insert
                            viewModel.saveData(user.email, namaTumbuhan, namaLatin, newBitmap)
                        } else {
                            Toast.makeText(context, R.string.pilihGambar, Toast.LENGTH_LONG).show()
                            return@TumbuhanDialog // Don't dismiss dialog if image is missing for new entry
                        }
                    } else {
                        // This is an update operation
                        viewModel.updateData(
                            tumbuhanToEdit!!.id,
                            user.email,
                            namaTumbuhan,
                            namaLatin,
                            newBitmap!! // Pass the new bitmap (can be null if not changed)
                        )
                    }
                    showTumbuhanDialog = false
                    tumbuhanToEdit = null // Clear edit state
                    dialogBitmap = null // Clear bitmap
                },
                onImageSelected = { selectedBitmap ->
                    dialogBitmap = selectedBitmap // Update the bitmap in MainScreen's state
                }
            )
        }

        if (showHapusDialog) {
            HapusDialog(
                onDismissRequest = {
                    showHapusDialog = false
                    tumbuhanToDelete = null
                },
                onConfirmation = {
                    tumbuhanToDelete?.let { tumbuhan ->
                        viewModel.deleteData(user.email, tumbuhan.id)
                    }
                    showHapusDialog = false
                    tumbuhanToDelete = null
                }
            )
        }

        if (errorMessage != null) {
            Toast.makeText(context, errorMessage, Toast.LENGTH_LONG).show()
            viewModel.clearMessage()
        }
    }
}

@Composable
fun ScreenContent(
    viewModel: MainViewModel,
    userId: String,
    currentUserId: String,
    onDeleteClick: (Tumbuhan) -> Unit,
    onEditClick: (Tumbuhan) -> Unit,
    modifier: Modifier = Modifier
) {
    val data by viewModel.data
    val status by viewModel.status.collectAsState()

    LaunchedEffect(userId) {
        viewModel.retrieveData(userId)
    }

    when (status) {
        ApiStatus.LOADING -> {
            Box(
                modifier = modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }

        ApiStatus.SUCCESS -> {
            LazyVerticalGrid(
                modifier = modifier
                    .fillMaxSize()
                    .padding(4.dp),
                columns = GridCells.Fixed(2),
                contentPadding = PaddingValues(bottom = 80.dp)
            ) {
                items(data) { tumbuhan ->
                    ListItem(
                        tumbuhan = tumbuhan,
                        isOwner = tumbuhan.Authorization == currentUserId && currentUserId.isNotEmpty(),
                        onDeleteClick = { onDeleteClick(tumbuhan) },
                        onEditClick = { onEditClick(tumbuhan) }
                    )

                    Log.d("DEBUG_DELETE", "Tumbuhan: ${tumbuhan.namaTumbuhan}, TumbuhanUserId: '${tumbuhan.Authorization}', CurrentUserId: '$currentUserId', IsOwner: ${tumbuhan.Authorization == currentUserId && currentUserId.isNotEmpty()}")
                }
            }
        }

        ApiStatus.FAILED -> {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(text = stringResource(id = R.string.error))
                Button(
                    onClick = { viewModel.retrieveData(userId) },
                    modifier = Modifier.padding(top = 16.dp),
                    contentPadding = PaddingValues(horizontal = 32.dp, vertical = 16.dp)
                ) {
                    Text(text = stringResource(id = R.string.try_again))
                }
            }
        }
    }
}

@Composable
fun ListItem(
    tumbuhan: Tumbuhan,
    isOwner: Boolean,
    onDeleteClick: () -> Unit,
    onEditClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .padding(4.dp)
            .border(1.dp, Color.Gray)
    ) {
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(TumbuhanApi.getTumbuhanImageUrl(tumbuhan.imageId))
                .crossfade(true)
                .build(),
            contentDescription = stringResource(R.string.gambar, tumbuhan.namaTumbuhan),
            contentScale = ContentScale.Crop,
            placeholder = painterResource(id = R.drawable.loading_img),
            error = painterResource(id = R.drawable.broken_img),
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f)
                .padding(4.dp)
        )

        // Delete button for owner's plants only
        if (isOwner) {
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(8.dp)
                    .size(32.dp)
                    .background(
                        Color.White.copy(alpha = 0.9f),
                        shape = CircleShape
                    )
                    .clickable { onDeleteClick() },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = stringResource(R.string.hapus),
                    tint = Color.Red,
                    modifier = Modifier.size(20.dp)
                )
            }

            // Edit button for owner's plants only
            Box(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(8.dp)
                    .size(32.dp)
                    .background(
                        Color.White.copy(alpha = 0.9f),
                        shape = CircleShape
                    )
                    .clickable { onEditClick() },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = stringResource(R.string.edit),
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(20.dp)
                )
            }
        }

        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .background(
                    Color.Black.copy(alpha = 0.6f)
                )
                .padding(8.dp)
        ) {
            Text(
                text = tumbuhan.namaTumbuhan,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            Text(
                text = tumbuhan.namaLatin,
                fontStyle = FontStyle.Italic,
                fontSize = 14.sp,
                color = Color.White
            )
        }
    }
}


private suspend fun signIn(context: Context, dataStore: UserDataStore) {
    val googleIdOption: GetGoogleIdOption = GetGoogleIdOption.Builder()
        .setFilterByAuthorizedAccounts(false)
        .setServerClientId(BuildConfig.API_KEY)
        .build()

    val request: GetCredentialRequest = GetCredentialRequest.Builder()
        .addCredentialOption(googleIdOption)
        .build()
    try {
        val credentialManager = CredentialManager.create(context)
        val result = credentialManager.getCredential(context, request)
        handleSignIn(result, dataStore)
    } catch (e: GetCredentialException) {
        Log.e("SIGN-IN", "Error: ${e.errorMessage}")
    }
}

private suspend fun handleSignIn(
    result: GetCredentialResponse,
    dataStore: UserDataStore
) {
    val credential = result.credential
    if (credential is CustomCredential && credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
        try {
            val googleId = GoogleIdTokenCredential.createFrom(credential.data)
            val nama = googleId.displayName ?: ""
            val email = googleId.id
            val photoUrl = googleId.profilePictureUri.toString()
            dataStore.saveData(User(nama, email, photoUrl))
        } catch (e: GoogleIdTokenParsingException) {
            Log.e("SIGN-IN", "Error: ${e.message}")
        }
    } else {
        Log.e("SIGN-IN", "Error: unrecognized custom credential type.")
    }
}

private suspend fun signOut(context: Context, dataStore: UserDataStore) {
    try {
        val credentialManager = CredentialManager.create(context)
        credentialManager.clearCredentialState(
            ClearCredentialStateRequest()
        )
        dataStore.saveData(User())
    } catch (e: ClearCredentialException) {
        Log.e("SIGN-OUT", "Error: ${e.message}")
    }
}


@Preview(showBackground = true)
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES, showBackground = true)
@Composable
fun MainScreenPreview() {
    Assesment3_607062330096_GhinaAniqahYCTheme {
        MainScreen()
    }
}