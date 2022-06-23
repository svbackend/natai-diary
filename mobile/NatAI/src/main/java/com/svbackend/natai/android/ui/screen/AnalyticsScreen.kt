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
import com.svbackend.natai.android.entity.LocalNote
import com.svbackend.natai.android.entity.TagEntityDto
import com.svbackend.natai.android.ui.NataiCustomColors
import com.svbackend.natai.android.utils.LocalDateTimeFormatter
import com.svbackend.natai.android.viewmodel.NoteViewModel
import java.time.DayOfWeek
import java.time.Instant
import java.time.LocalDate
import java.time.Period
import java.util.*
import kotlin.random.Random

@Composable
fun AnalyticsScreen(vm: NoteViewModel) {
    val notesMap: MutableMap<String, MutableList<LocalNote>> = mutableMapOf()
    vm.notesState.forEach {
        val date = LocalDateTimeFormatter.fullDate.format(it.actualDate)
        if (notesMap.containsKey(date)) {
            notesMap[date]!!.add(it)
        } else {
            notesMap[date] = mutableListOf(it)
        }
    }

    val dateList = generateDates()

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

            Contributions(
                notesMap,
                dateList
            )
        }
    }
}

// generate dates (last 3 months) for displaying squares of days with tags
// start always on Sunday, end always on Saturday
// if there is no tag for a day, it will be empty
private fun generateDates(): List<String> {
    val calendar = LocalDate.now()

    val daysBefore = 90

    val prependedDates = mutableListOf<LocalDate>()
    val appendedDates = mutableListOf<LocalDate>()
    val dateList = (daysBefore downTo 0).map {
        calendar.minus(Period.ofDays(it))
    }.toMutableList()

    val firstDate = dateList.first()
    val lastDate = dateList.last()

    if (firstDate.dayOfWeek != DayOfWeek.SUNDAY) {
        val nbDaysToAdd = firstDate.dayOfWeek.value
        prependedDates.addAll(
            (daysBefore + nbDaysToAdd downTo daysBefore + 1).map {
                calendar.minus(Period.ofDays(it))
            }
        )
    }

    if (lastDate.dayOfWeek != DayOfWeek.SATURDAY) {
        val nbDaysToAdd = DayOfWeek.SATURDAY.value - lastDate.dayOfWeek.value
        appendedDates.addAll(
            (1..nbDaysToAdd).map {
                calendar.plus(Period.ofDays(it))
            }
        )
    }

    return prependedDates
        .plus(dateList)
        .plus(appendedDates)
        .map {
            LocalDateTimeFormatter.fullDate.format(it)
        }
}

@Composable
fun Contributions(
    notesMap: MutableMap<String, MutableList<LocalNote>>,
    dateList: List<String>
) {
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
            state = rememberLazyListState(initialFirstVisibleItemIndex = (365 / 7) - 1)
        ) {
            for (week in dateList.chunked(7)) {
                item {
                    Column {
                        for (day in week) {
                            ContributionsSquare(
                                tag = "sex",
                                notes = notesMap[day] ?: emptyList(),
                            )
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
fun ContributionsSquare(
    tag: String,
    notes: List<LocalNote>,
) {
    var tagDto: TagEntityDto? = null
    notes.forEach { localNote ->
        localNote.tags.forEach innerLoop@{ t ->
            if (t.name == tag) {
                tagDto = t
                return@innerLoop
            }
        }

        if (tagDto != null) {
            return@forEach
        }
    }

    val colors = NataiCustomColors.get()
    val bg = if (tagDto != null) {
        val a = getAlpha(tagDto!!.score)
        colors.contribution.copy(alpha = a)
    } else colors.emptyContribution
    Box(
        modifier = Modifier
            .size(24.dp)
            .padding(horizontal = 2.dp, vertical = 2.dp)
            .border(color = colors.border, width = 1.dp, shape = RoundedCornerShape(4.dp))
            .clip(shape = RoundedCornerShape(4.dp))
            .background(bg)
    )
}

private fun getAlpha(score: Int?): Float {
    return if (score != null) {
        (score / 100f).coerceAtLeast(.1f)
    } else {
        .8f
    }
}