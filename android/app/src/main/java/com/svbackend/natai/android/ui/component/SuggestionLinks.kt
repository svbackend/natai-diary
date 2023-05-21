package com.svbackend.natai.android.ui.component

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat.startActivity
import coil.compose.AsyncImage
import com.svbackend.natai.android.R
import com.svbackend.natai.android.http.dto.CloudSuggestionLinkDto

@Composable
fun SuggestionLinks(links: List<CloudSuggestionLinkDto>, error: String?) {
    Column(modifier = Modifier.padding(top = 16.dp)) {
        Text(text = "Additional Resources", fontWeight = FontWeight.Bold, fontSize = 24.sp)

        if (error != null) {
            Text(
                text = "Error: $error",
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                modifier = Modifier.padding(bottom = 16.dp)
            )
        }

        if (links.isNotEmpty()) {
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp)
            ) {
                items(links) { link ->
                    SuggestionLinkCard(link)
                }
            }
        }
    }
}

@Composable
fun SuggestionLinkCard(link: CloudSuggestionLinkDto) {
    val context = androidx.compose.ui.platform.LocalContext.current

    fun onOpenLinkInBrowser(url: String) {
        val intent = Intent(Intent.ACTION_VIEW)
        intent.data = Uri.parse(url)
        startActivity(context, intent, null)
    }

    Card(
        modifier = Modifier
            .width(200.dp)
            .padding(end = 16.dp),
        shape = RoundedCornerShape(8.dp)
    ) {
        Column {
            if (link.image != null) {
                // ContentScale.Crop
                AsyncImage(
                    modifier = Modifier
                        .fillMaxWidth(),
                    model = link.image,
                    contentDescription = "Suggestion Link Image",
                    contentScale = ContentScale.FillWidth,
                )
            } else {
                // Image placeholder
                Image(
                    modifier = Modifier
                        .fillMaxWidth(),
                    painter = painterResource(id = R.drawable.therapy_session_sm),
                    contentDescription = stringResource(id = R.string.therapySessionImage),
                    contentScale = ContentScale.FillWidth,
                )
            }

            Column(
                modifier = Modifier
                    .padding(vertical = 12.dp, horizontal = 12.dp)
            ) {
                Text(
                    text = link.title,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                Text(
                    text = link.description,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                OutlinedButton(
                    onClick = { onOpenLinkInBrowser(link.url) },
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "Open",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.White,
                            modifier = Modifier.padding(start = 4.dp)
                        )
                    }
                }
            }
        }

    }
}

@Composable
fun SuggestionLinksBlurred() {

}