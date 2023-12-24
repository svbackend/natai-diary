package com.svbackend.natai.android.ui.component

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.KeyboardArrowDown
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.svbackend.natai.android.http.dto.CityDto
import com.svbackend.natai.android.ui.NTextField

@Composable
fun CityAutocomplete(
    input: TextFieldValue,
    selectedCityId: Int?,
    onCitySelected: (Int) -> Unit,
    cities: List<CityDto>,
    onInputChanged: (TextFieldValue) -> Unit,
    isLoadingAutocomplete: Boolean = false,
    expanded: Boolean = false,
    onToggleListVisibility: () -> Unit,
) {
    val interactionSource = remember {
        MutableInteractionSource()
    }

    // Category Field
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = {
                    if (expanded) {
                        onToggleListVisibility()
                    }
                }
            )
            .padding(top = 10.dp)
    ) {
        NTextField(
            value = input,
            label = "Location",
            onChange = {
                onInputChanged(it)
            },
            trailingIcon = {
                if (isLoadingAutocomplete) {
                    CircularProgressIndicator(modifier = Modifier.size(16.dp))
                } else {
                    Icon(
                        modifier = Modifier
                            .size(24.dp)
                            .clickable { onToggleListVisibility() },
                        imageVector = Icons.Rounded.KeyboardArrowDown,
                        contentDescription = "arrow",
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                }
            },
        )

        Column(modifier = Modifier.fillMaxWidth()) {
            AnimatedVisibility(visible = expanded) {
                Card(
                    modifier = Modifier
                        .padding(horizontal = 5.dp),
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