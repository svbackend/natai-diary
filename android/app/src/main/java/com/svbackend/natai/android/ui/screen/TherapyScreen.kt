package com.svbackend.natai.android.ui.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.svbackend.natai.android.R
import com.svbackend.natai.android.entity.TagEntityDto
import com.svbackend.natai.android.http.dto.CloudSuggestionDto
import com.svbackend.natai.android.ui.component.SuggestionLinks
import com.svbackend.natai.android.ui.component.TagPreviewBadge
import com.svbackend.natai.android.viewmodel.TherapyViewModel

// or SuggestionsScreen ?
@Composable
fun TherapyScreen(
    vm: TherapyViewModel = viewModel(),
    onClickCreateAccount: () -> Unit,
) {
    Surface(
        modifier = Modifier
            .fillMaxSize(),
        color = MaterialTheme.colorScheme.surface
    ) {
        Column(
            Modifier
                .verticalScroll(rememberScrollState())
        ) {
            Text(
                text = stringResource(R.string.therapyTitle),
                color = MaterialTheme.colorScheme.primary,
                fontSize = 24.sp,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 24.dp)
            )

            if (vm.isLoading.value) {
                LoadingState()
            } else {
                if (vm.suggestions.value.isEmpty()) {
                    EmptyState(vm.isUserLoggedIn, onClickCreateAccount)
                } else {
                    SuggestionsList(vm)
                }
            }
        }
    }

    if (vm.selectedSuggestion.value != null) {
        SuggestionDetailsDialog(vm)
    }
}

@Composable
fun SuggestionsList(
    vm: TherapyViewModel,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        vm.suggestions.value.forEach { suggestion ->
            SuggestionRow(vm, suggestion)
        }
    }
}

@Composable
fun SuggestionRow(
    vm: TherapyViewModel,
    suggestion: CloudSuggestionDto
) {
    Card(
        shape = RoundedCornerShape(8.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = vm.periodToString(suggestion.period),
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
            if (!suggestion.isReceived) {
                Text(
                    text = "new",
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.error
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = vm.preview(suggestion.suggestion),
                fontSize = 14.sp
            )
            Spacer(modifier = Modifier.height(8.dp))
            Button(
                onClick = { vm.selectSuggestion(suggestion) },
                colors = ButtonDefaults.textButtonColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    contentColor = MaterialTheme.colorScheme.primary
                )
            ) {
                Text(
                    text = "Read more",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
fun EmptyState(
    isUserLoggedIn: Boolean,
    onClickCreateAccount: () -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                painter = painterResource(id = R.drawable.baseline_assistant_24),
                contentDescription = "Assistant icon",
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(128.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Write couple of notes, receive personalized suggestions",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )

            if (!isUserLoggedIn) {
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "(You need to have an account and be logged in to receive suggestions)",
                    fontSize = 14.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Button(
                        modifier = Modifier
                            .padding(top = 12.dp),
                        onClick = onClickCreateAccount,
                    ) {
                        Text(
                            text = stringResource(R.string.createAccount),
                            textAlign = TextAlign.Center,
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun SuggestionDetailsDialog(vm: TherapyViewModel) {
    val suggestion = vm.selectedSuggestion.value!!
    val scroll = rememberScrollState()

    val onClickLearnMore = {
        // todo call parent function and redirect user to new screen
    }

    AlertDialog(
        onDismissRequest = { vm.selectSuggestion(null) },
        title = {
            Text(
                text = vm.periodToString(suggestion.period),
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(Modifier.verticalScroll(scroll)) {
                Image(
                    modifier = Modifier
                        .fillMaxWidth(),
                    painter = painterResource(id = R.drawable.therapy_session_sm),
                    contentDescription = stringResource(id = R.string.therapySessionImage),
                    contentScale = ContentScale.FillWidth,
                )
                Text(
                    text = "Natai Diary • AI Therapy Session",
                    fontSize = 12.sp,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier
                        .fillMaxWidth(),
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = suggestion.suggestion,
                    fontSize = 14.sp
                )
                SuggestionLinks(
                    links = vm.suggestionLinks.value,
                    error = vm.suggestionLinksError.value,
                    onClickGetAccess = { vm.onClickGetAccess() },
                    onClickLearnMore = onClickLearnMore,
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { vm.sendFeedbackAndClose(suggestion, 5) },
                colors = ButtonDefaults.textButtonColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            ) {
                Text(
                    text = "Thanks ❤",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        },
        dismissButton = {
            Button(
                onClick = { vm.sendFeedbackAndClose(suggestion, 1) },
                colors = ButtonDefaults.textButtonColors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer,
                    contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                )
            ) {
                Text(
                    text = "Not helpful",
                    fontSize = 13.sp
                )
            }
        }
    )
}

@Composable
fun LoadingState() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(
            color = MaterialTheme.colorScheme.primary
        )
    }
}