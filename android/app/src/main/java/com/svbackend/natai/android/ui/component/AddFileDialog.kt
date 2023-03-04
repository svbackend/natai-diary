package com.svbackend.natai.android.ui.component

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.svbackend.natai.android.R
import com.svbackend.natai.android.ui.NPrimaryButton
import com.svbackend.natai.android.viewmodel.AddedFile

@Composable
fun AddFileDialog(
    onClose: () -> Unit,
    selectedFiles: List<AddedFile>,
    onAdd: (List<Uri>) -> Unit,
    onDelete: (AddedFile) -> Unit,
) {


    val pickFilesLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.PickMultipleVisualMedia()
    ) { imageUris ->
        onAdd(imageUris)
    }

    AlertDialog(
        onDismissRequest = onClose,
        confirmButton = {
            Button(onClick = {
                onClose()
            }) {
                Text(stringResource(R.string.done))
            }
        },
        text = {
            Column(modifier = Modifier.fillMaxSize()) {
                AddedFilesArea(selectedFiles, onDelete = onDelete)

                FilePickerArea {
                    pickFilesLauncher.launch(
                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                    )
                }
            }
        },
        title = {
            Text("Add File")
        },
        modifier = Modifier
            .padding(top = 32.dp, bottom = 32.dp)
    )
}

@Composable
fun FilePickerArea(onClick: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 8.dp)
            .clickable { onClick() }
    ) {
        NPrimaryButton(
            onClick = onClick,
        ) {
            Icon(
                painterResource(id = R.drawable.cloud_arrow_up),
                stringResource(R.string.addFile)
            )
            Text(
                text = stringResource(R.string.addFile),
                modifier = Modifier.padding(start = 8.dp)
            )
        }
    }
}

@Composable
fun AddedFilesArea(files: List<AddedFile>, onDelete: (AddedFile) -> Unit) {
    if (files.isEmpty()) {
        Row(
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier
                .fillMaxWidth()
        ) {
            Text("No files added")
        }
        return
    }

    LazyColumn {
        items(files.size) { index ->
            val f = files[index]
            AddedFile(f, onDelete = onDelete)
        }
    }
}


@Composable
fun AddedFile(f: AddedFile, onDelete: (AddedFile) -> Unit) {
    // Image, name, remove button
    Row(verticalAlignment = Alignment.CenterVertically) {
        AsyncImage(
            model = f.uri,
            contentDescription = null,
            modifier = Modifier
                .size(64.dp)
                .padding(8.dp)
        )
        Column(modifier = Modifier.weight(1f)) {
            Text(f.shortFilename, maxLines = 1)

            AddedFileStatus(f)

            LinearProgressIndicator(
                progress = .65f,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 2.dp)
                    .height(2.dp)
            )
        }
        IconButton(onClick = { onDelete(f) }, modifier = Modifier.fillMaxHeight()) {
            Icon(
                Icons.Filled.Delete,
                stringResource(R.string.deleteFile)
            )
        }
    }
}

@Composable
fun AddedFileStatus(f: AddedFile) {
    val size = MaterialTheme.typography.bodySmall
    if (f.isPending) {
        Text(text = "Pending", style = size)
    }

    if (f.error != null) {
        Text(text = "Error: ${f.error}", maxLines = 1, style = size)
    }

    if (f.isUploading) {
        Text(text = "Uploading", style = size)
    }

    if (f.isUploaded) {
        Text(text = "Uploaded", style = size)
    }
}
