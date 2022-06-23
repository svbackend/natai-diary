package com.svbackend.natai.android.ui.screen

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.svbackend.natai.android.R
import com.svbackend.natai.android.entity.LocalNote
import com.svbackend.natai.android.entity.TagEntityDto
import com.svbackend.natai.android.ui.NataiCustomColors
import com.svbackend.natai.android.utils.LocalDateTimeFormatter
import com.svbackend.natai.android.viewmodel.NoteViewModel
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.Period

@Composable
fun AnalyticsScreen(vm: NoteViewModel) {
    val context = LocalContext.current
    val notesMap: MutableMap<String, MutableList<LocalNote>> = mutableMapOf()
    vm.notesState.forEach {
        val date = LocalDateTimeFormatter.fullDate.format(it.actualDate)
        if (notesMap.containsKey(date)) {
            notesMap[date]!!.add(it)
        } else {
            notesMap[date] = mutableListOf(it)
        }
    }

    val tags = getMostFrequentUsedTags(vm.notesState)

    val dateList = generateDates()

    Surface(
        modifier = Modifier
            .fillMaxSize(),
        color = MaterialTheme.colorScheme.surface
    ) {
        LazyColumn {
            item {
                Text(
                    text = stringResource(R.string.analyticsTitle),
                    color = MaterialTheme.colorScheme.primary,
                    fontSize = 24.sp,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 24.dp)
                )
            }

            items(tags) { tag ->
                Contributions(
                    context = context,
                    tag = tag,
                    notesMap = notesMap,
                    dateList = dateList
                )
            }
        }
    }
}

private fun getMostFrequentUsedTags(notes: List<LocalNote>): List<String> {
    val tagsMap: MutableMap<String, Int> = mutableMapOf()
    notes.forEach { localNote ->
        localNote.tags.forEach {
            if (tagsMap.containsKey(it.name)) {
                tagsMap[it.name] = tagsMap[it.name]!! + 1
            } else {
                tagsMap[it.name] = 1
            }
        }
    }
    val sortedTags = tagsMap.toList().sortedByDescending { it.second }
    return sortedTags.map { it.first }
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
    context: Context,
    tag: String,
    notesMap: MutableMap<String, MutableList<LocalNote>>,
    dateList: List<String>
) {
    Text(
        text = "#${tag}",
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
                                day = day,
                                tag = tag,
                                notes = notesMap[day] ?: emptyList(),
                                onClick = {
                                    Toast
                                        .makeText(context, it, Toast.LENGTH_SHORT)
                                        .show()
                                }
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
    day: String,
    tag: String,
    notes: List<LocalNote>,
    onClick: (String) -> Unit,
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
            .clickable { onClick(day) }
            .size(24.dp)
            .padding(horizontal = 2.dp, vertical = 2.dp)
            .border(color = colors.border, width = 1.dp, shape = RoundedCornerShape(4.dp))
            .clip(shape = RoundedCornerShape(4.dp))
            .background(bg)
    )
}

private fun getAlpha(score: Int?): Float {
    return if (score != null) {
        (score / 10f).coerceAtLeast(.1f)
    } else {
        .8f
    }
}