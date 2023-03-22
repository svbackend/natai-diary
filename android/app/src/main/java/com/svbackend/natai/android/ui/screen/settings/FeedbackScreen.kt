package com.svbackend.natai.android.ui.screen.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.svbackend.natai.android.viewmodel.FeedbackViewModel

@Composable
fun FeedbackScreen(
    vm: FeedbackViewModel = viewModel()
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Rating section
        Text(
            text = "How would you rate our app?",
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        RatingSection(vm)

        Spacer(modifier = Modifier.height(16.dp))

        if (vm.showThankYou.value) {
            Text(
                text = "Thank you for your feedback! We really appreciate it!",
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(bottom = 16.dp),
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(16.dp))
        }

        // Feedback section
        Text(
            text = "What feedback do you have for us?",
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        FeedbackSection(vm)

        Spacer(modifier = Modifier.height(16.dp))

        // Send feedback button
        Button(
            onClick = { vm.sendFeedback() },
            modifier = Modifier.align(Alignment.End)
        ) {
            Text(text = "Send Feedback")
        }
    }
}

@Composable
fun RatingSection(vm: FeedbackViewModel) {
    val selectedRating = vm.selectedRating.value ?: 0
    Row(
        horizontalArrangement = Arrangement.SpaceEvenly,
        modifier = Modifier.fillMaxWidth()
    ) {
        for (i in 1..5) {
            val image = Icons.Filled.Star
            val tint = if (i <= selectedRating) {
                MaterialTheme.colorScheme.primary
            } else {
                MaterialTheme.colorScheme.outline
            }
            Icon(
                imageVector = image,
                contentDescription = "Star",
                tint = tint,
                modifier = Modifier
                    .size(48.dp)
                    .clickable {
                        vm.setSelectedRating(i)
                    }
            )
        }
    }
}

@Composable
fun FeedbackSection(vm: FeedbackViewModel) {
    TextField(
        value = vm.feedbackContent.value,
        onValueChange = { vm.setFeedbackContent(it) },
        modifier = Modifier.fillMaxWidth(),
        placeholder = { Text(text = "Enter your feedback here") },
        maxLines = 5
    )
}