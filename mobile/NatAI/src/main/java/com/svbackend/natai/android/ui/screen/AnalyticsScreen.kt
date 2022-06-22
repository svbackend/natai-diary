package com.svbackend.natai.android.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.svbackend.natai.android.R
import com.svbackend.natai.android.ui.NataiCustomColors
import kotlin.random.Random

@Composable
fun AnalyticsScreen() {
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
                text = stringResource(R.string.analyticsTitle),
                color = MaterialTheme.colorScheme.primary,
                fontSize = 24.sp,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 24.dp)
            )

            Contributions()
        }
    }
}

@Preview(showBackground = false)
@Composable
fun Contributions() {
    Text(
        text = "#sex",
        color = MaterialTheme.colorScheme.primary,
        fontSize = 18.sp,
        modifier = Modifier.padding(start = 16.dp)
    )

            BoxWithConstraints(
                modifier = Modifier
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                LazyRow(
                    modifier = Modifier.fillMaxWidth(),
                    state = rememberLazyListState(initialFirstVisibleItemIndex = (365 / 7)-1)
                ) {
                    for (week in 0..365 / 7) {
                        item {
                            Column {
                                for (day in 0..6) {
                                    ContributionsSquare()
                                }
                            }
                        }
                    }
                    item {
                        Column(
                            modifier = Modifier
                                .padding(top = 8.dp, start = 4.dp)
                        ) {
                            Text(
                                text = "Mon",
                                modifier = Modifier.padding(top = 22.dp),
                                fontSize = 10.sp,
                            )
                            Text(
                                text = "Wed",
                                modifier = Modifier.padding(top = 34.dp),
                                fontSize = 10.sp,
                            )
                            Text(
                                text = "Fri",
                                modifier = Modifier.padding(top = 34.dp),
                                fontSize = 10.sp,
                            )
                        }
                    }
                }
            }
}

@Composable
fun ContributionsSquare() {
    val colors = NataiCustomColors.get()
    val bg = if (Random.nextInt(0, 10) % 10 == 0)
        colors.emptyContribution else colors.contribution
    Box(
        modifier = Modifier
            .size(24.dp)
            .padding(horizontal = 2.dp, vertical = 2.dp)
            .border(color = colors.border, width = 1.dp, shape = RoundedCornerShape(4.dp))
            .clip(shape = RoundedCornerShape(4.dp))
            .background(bg)
    )
}