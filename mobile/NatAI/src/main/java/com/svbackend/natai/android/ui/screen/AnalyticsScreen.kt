package com.svbackend.natai.android.ui.screen

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.svbackend.natai.android.R
import com.svbackend.natai.android.entity.LocalNote
import com.svbackend.natai.android.entity.Tag
import com.svbackend.natai.android.entity.TagEntityDto
import com.svbackend.natai.android.ui.NataiCustomColors
import com.svbackend.natai.android.ui.component.SpecialTagIcon
import com.svbackend.natai.android.utils.LocalDateTimeFormatter
import com.svbackend.natai.android.viewmodel.NoteViewModel
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.Period

val NB_OF_DAYS = 49

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

    val tags = Tag.getMostFrequentlyUsedTags(vm.notesState)

    var daysBefore by remember { mutableStateOf(NB_OF_DAYS) }

    val dateList = generateDates(daysBefore)
    val firstDate = dateList.first()
    val lastDate = dateList.last()
    val dateStrings = dateList.map { LocalDateTimeFormatter.fullDate.format(it) }

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

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    IconButton(
                        onClick = {
                            daysBefore += NB_OF_DAYS
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.KeyboardArrowLeft,
                            contentDescription = "show previous dates"
                        )
                    }
                    Text(
                        text = LocalDateTimeFormatter.fullDate.format(firstDate),
                        color = MaterialTheme.colorScheme.onSurface,
                        fontSize = 16.sp,
                        modifier = Modifier.padding(top = 12.dp)
                    )
                    Text(
                        text = "—",
                        color = MaterialTheme.colorScheme.onSurface,
                        fontSize = 16.sp,
                        modifier = Modifier.padding(top = 12.dp)
                    )
                    Text(
                        text = LocalDateTimeFormatter.fullDate.format(lastDate),
                        color = MaterialTheme.colorScheme.onSurface,
                        fontSize = 16.sp,
                        modifier = Modifier.padding(top = 12.dp)
                    )
                    IconButton(
                        onClick = {
                            daysBefore -= NB_OF_DAYS
                        },
                        enabled = daysBefore > NB_OF_DAYS
                    ) {
                        Icon(
                            imageVector = Icons.Filled.KeyboardArrowRight,
                            contentDescription = "show next dates"
                        )
                    }
                }
            }

            items(tags) { tag ->
                Contributions(
                    context = context,
                    tag = tag,
                    notesMap = notesMap,
                    dateList = dateStrings
                )
            }
        }
    }
}

// generate dates (last 3 months) for displaying squares of days with tags
// start always on Sunday, end always on Saturday
// if there is no tag for a day, it will be empty
private fun generateDates(daysBefore: Int = NB_OF_DAYS): List<LocalDate> {
    val calendar = LocalDate.now()

    val prependedDates = mutableListOf<LocalDate>()
    val appendedDates = mutableListOf<LocalDate>()
    val dateList = (daysBefore downTo (daysBefore - NB_OF_DAYS)).map {
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
        val nbDaysToAdd = if (lastDate.dayOfWeek == DayOfWeek.SUNDAY) {
            6
        } else {
            DayOfWeek.SATURDAY.value - lastDate.dayOfWeek.value
        }

        if (nbDaysToAdd > 0) {
            appendedDates.addAll(
                (1..nbDaysToAdd).map {
                    calendar.plus(Period.ofDays(it))
                }
            )
        }
    }

    return prependedDates
        .plus(dateList)
        .plus(appendedDates)
}

@Composable
fun Contributions(
    context: Context,
    tag: String,
    notesMap: MutableMap<String, MutableList<LocalNote>>,
    dateList: List<String>
) {
    val weekDayBaseTopPadding = 42.dp
    val weekDayFollowingPadding = 15.dp

    Text(
        text = "#${tag}",
        color = MaterialTheme.colorScheme.primary,
        fontSize = 18.sp,
        modifier = Modifier.padding(start = 16.dp)
    )
    Box(
        modifier = Modifier
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
        ) {
            for (week in dateList.chunked(7)) {
                Column {
                    for (day in week) {
                        when (tag) {
                            "mood" -> {
                                MoodSquare(
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
                            else -> {
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

            }
            Column(
                modifier = Modifier
                    .padding(top = 8.dp, start = 4.dp)
            ) {
                Text(
                    text = "Mon",
                    modifier = Modifier.padding(top = weekDayBaseTopPadding),
                    fontSize = 10.sp,
                )
                Text(
                    text = "Wed",
                    modifier = Modifier.padding(top = weekDayBaseTopPadding + weekDayFollowingPadding),
                    fontSize = 10.sp,
                )
                Text(
                    text = "Fri",
                    modifier = Modifier.padding(top = weekDayBaseTopPadding + weekDayFollowingPadding),
                    fontSize = 10.sp,
                )
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
            if (t.tag == tag) {
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
            .size(36.dp)
            .padding(horizontal = 2.dp, vertical = 2.dp)
            //.border(color = colors.border, width = 1.dp, shape = RoundedCornerShape(4.dp))
            .clip(shape = RoundedCornerShape(4.dp))
            .background(bg)
    )
}

@Composable
fun MoodSquare(
    day: String,
    tag: String,
    notes: List<LocalNote>,
    onClick: (String) -> Unit,
) {
    var tagDto: TagEntityDto? = null
    notes.forEach { localNote ->
        localNote.tags.forEach innerLoop@{ t ->
            if (t.tag == tag) {
                tagDto = t
                return@innerLoop
            }
        }

        if (tagDto != null) {
            return@forEach
        }
    }

    val colors = NataiCustomColors.get()
    Box(
        modifier = Modifier
            .clickable { onClick(day) }
            .size(36.dp)
            .padding(horizontal = 2.dp, vertical = 2.dp)
            //.border(color = colors.border, width = 1.dp, shape = RoundedCornerShape(4.dp))
            .clip(shape = RoundedCornerShape(4.dp))
            .background(colors.emptyContribution)
    ) {
        if (tagDto != null) {
            SpecialTagIcon(modifier = Modifier.size(36.dp), tag = tag, score = tagDto!!.score)
        }
    }
}

private fun getAlpha(score: Int?): Float {
    return if (score != null) {
        (score / 10f).coerceAtLeast(.1f)
    } else {
        .8f
    }
}