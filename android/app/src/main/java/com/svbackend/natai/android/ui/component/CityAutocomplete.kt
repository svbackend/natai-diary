package com.svbackend.natai.android.ui.component

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.KeyboardArrowDown
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.toSize
import com.svbackend.natai.android.http.dto.CityDto
import com.svbackend.natai.android.ui.NTextField

@Composable
fun CityAutocomplete(
    selectedCityId: Int?,
    onCitySelected: (Int) -> Unit,
    cities: List<CityDto>,
    onInputChanged: (String) -> Unit,
) {
    var city by remember {
        mutableStateOf("")
    }

    val heightTextFields by remember {
        mutableStateOf(55.dp)
    }

    var textFieldSize by remember {
        mutableStateOf(Size.Zero)
    }

    var expanded by remember {
        mutableStateOf(false)
    }
    val interactionSource = remember {
        MutableInteractionSource()
    }

    // Category Field
    Column(
        modifier = Modifier
            .padding(30.dp)
            .fillMaxWidth()
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = {
                    expanded = false
                }
            )
    ) {
        NTextField(
            modifier = Modifier.padding(start = 3.dp, bottom = 2.dp),
            value = TextFieldValue(city),
            label = "City",
            onChange = {
                city = it.text
                onInputChanged(it.text)
            },
            trailingIcon = {
                Icon(
                    modifier = Modifier.size(24.dp),
                    imageVector = Icons.Rounded.KeyboardArrowDown,
                    contentDescription = "arrow",
                    tint = Color.Black
                )
            },
        )

        Column(modifier = Modifier.fillMaxWidth()) {

            Row(modifier = Modifier.fillMaxWidth()) {
                NTextField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(heightTextFields)
                        .border(
                            width = 1.8.dp,
                            color = Color.Black,
                            shape = RoundedCornerShape(15.dp)
                        )
                        .onGloballyPositioned { coordinates ->
                            textFieldSize = coordinates.size.toSize()
                        },
                    value = TextFieldValue(city),
                    onChange = {
                        city = it.text
                        onInputChanged(it.text)
                        expanded = true
                    },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Text,
                        imeAction = ImeAction.Done
                    ),
                    singleLine = true,
                    label = "",
                    trailingIcon = {
                        IconButton(onClick = { expanded = !expanded }) {
                            Icon(
                                modifier = Modifier.size(24.dp),
                                imageVector = Icons.Rounded.KeyboardArrowDown,
                                contentDescription = "arrow",
                                tint = Color.Black
                            )
                        }
                    }
                )
            }

            AnimatedVisibility(visible = expanded) {
                Card(
                    modifier = Modifier
                        .padding(horizontal = 5.dp)
                        .width(textFieldSize.width.dp),
                    shape = RoundedCornerShape(10.dp)
                ) {

                    LazyColumn(
                        modifier = Modifier.heightIn(max = 150.dp),
                    ) {

                        if (cities.isNotEmpty()) {
                            cities.map {
                                item {
                                    CityItem(it, isSelected = it.id == selectedCityId) { id ->
                                        onCitySelected(id)
                                        expanded = false
                                    }
                                }
                            }
                        }

                    }

                }
            }

        }

    }


}

@Composable
fun CityItem(
    city: CityDto,
    isSelected: Boolean = false,
    onSelect: (Int) -> Unit,
) {
    val m = Modifier
        .fillMaxWidth()
        .clickable {
            onSelect(city.id)
        }
        .padding(10.dp)

    if (isSelected) {
        m.border(
            width = 1.8.dp,
            color = Color.Black,
            shape = RoundedCornerShape(15.dp)
        )
    }

    Row(
        modifier = m
    ) {
        Text(text = city.name, fontSize = 16.sp)
    }
}