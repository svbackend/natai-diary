package com.svbackend.natai.android.ui

import android.app.DatePickerDialog
import android.content.Context
import android.widget.DatePicker
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.svbackend.natai.android.R
import com.svbackend.natai.android.utils.LocalDateTimeFormatter
import java.time.LocalDate

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NTextField(
    modifier: Modifier = Modifier,
    value: TextFieldValue,
    label: String,
    onChange: (TextFieldValue) -> Unit,
    singleLine: Boolean = true,
    maxLines: Int = 1,
    trailingIcon: @Composable (() -> Unit)? = null,
    visualTransformation: VisualTransformation = VisualTransformation.None,
) {
    OutlinedTextField(
        value = value,
        label = @Composable { Text(label) },
        onValueChange = {
            onChange(it)
        },
        singleLine = singleLine,
        modifier = modifier
            .fillMaxWidth()
            .padding(bottom = 16.dp),
        maxLines = maxLines,
        trailingIcon = trailingIcon,
        visualTransformation = visualTransformation,
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppDateField(
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

@Composable
fun AppDateRow(
    actualDate: LocalDate,
    onDateChange: (LocalDate) -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        IconButton(
            onClick = {
                onDateChange(actualDate.minusDays(1))
            }
        ) {
            Icon(
                imageVector = Icons.Default.KeyboardArrowLeft,
                contentDescription = "select previous date"
            )
        }
        Text(
            text = LocalDateTimeFormatter.fullDate.format(actualDate),
            color = MaterialTheme.colorScheme.onSurface,
            fontSize = 16.sp,
            modifier = Modifier.padding(top = 12.dp)
        )
        IconButton(
            onClick = {
                onDateChange(actualDate.plusDays(1))
            },
            enabled = actualDate.isBefore(LocalDate.now())
        ) {
            Icon(
                imageVector = Icons.Filled.KeyboardArrowRight,
                contentDescription = "select next date"
            )
        }
    }
}

@Composable
fun NPasswordField(
    modifier: Modifier = Modifier,
    value: TextFieldValue,
    label: String,
    onChange: (TextFieldValue) -> Unit,
) {
    val passwordVisible = rememberSaveable { mutableStateOf(false) }

    NTextField(
        modifier = modifier,
        value = value,
        label = label,
        onChange = onChange,
        singleLine = true,
        maxLines = 1,
        visualTransformation = if (passwordVisible.value) VisualTransformation.None else PasswordVisualTransformation(),
        trailingIcon = @Composable {
            Icon(
                modifier = Modifier.clickable {
                    passwordVisible.value = !passwordVisible.value
                },
                painter = if (passwordVisible.value) {
                    painterResource(id = R.drawable.ic_baseline_visibility_off_24)
                } else {
                    painterResource(id = R.drawable.ic_baseline_visibility_24)
                },
                contentDescription = "show password"
            )
        },
    )
}
