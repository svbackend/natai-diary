package com.svbackend.natai.android.ui

import android.app.DatePickerDialog
import android.content.Context
import android.widget.DatePicker
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import com.svbackend.natai.android.R
import com.svbackend.natai.android.utils.LocalDateTimeFormatter
import java.time.LocalDate

@Composable
fun NTextField(
    modifier: Modifier = Modifier,
    value: TextFieldValue,
    label: String,
    onChange: (TextFieldValue) -> Unit,
    singleLine: Boolean = true,
    maxLines: Int = 1
) {
    TextField(
        value = value,
        label = @Composable { Text(label) },
        onValueChange = {
            onChange(it)
        },
        singleLine = singleLine,
        modifier = modifier
            .fillMaxWidth()
            .padding(bottom = 16.dp),
        maxLines = maxLines
    )
}

@Composable
fun NTextarea(
    value: TextFieldValue,
    label: String,
    onChange: (TextFieldValue) -> Unit
) {
    NTextField(
        value = value,
        label = label,
        onChange = onChange,
        singleLine = false,
        maxLines = 6,
        modifier = Modifier.height(200.dp)
    )
}

@Composable
fun NDateField(
    context: Context,
    modifier: Modifier = Modifier,
    value: LocalDate,
    onChange: (LocalDate) -> Unit,
) {
    val year = value.year
    val month = value.monthValue
    val day = value.dayOfMonth

    val onDateChange = { _: DatePicker, y: Int, m: Int, d: Int ->
        onChange(LocalDate.of(y, m, d))
    }

    val mDatePickerDialog = DatePickerDialog(
        context, onDateChange, year, month, day
    )
    val dateString = LocalDateTimeFormatter.fullDate.format(value)

    TextField(
        value = TextFieldValue(dateString),
        label = @Composable { Text(stringResource(R.string.date)) },
        readOnly = true,
        enabled = false,
        onValueChange = {
        },
        singleLine = true,
        modifier = modifier
            .clickable { mDatePickerDialog.show() }
            .fillMaxWidth()
            .padding(bottom = 16.dp),
    )
}