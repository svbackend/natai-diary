package com.svbackend.natai.android.ui.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.rounded.Menu
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.svbackend.natai.android.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddDrawerHeader(
    title: String,
    titleColor: Color = Color.Black,
) {
    Card(
        elevation = CardDefaults.cardElevation(),
        modifier = Modifier
            .fillMaxWidth(),
        border = BorderStroke(1.dp, color = Color.Gray),


        ) {
        Text(
            text = title,
            style = TextStyle(
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = titleColor
            ),
            modifier = Modifier.padding(14.dp)
        )

    }
}

@Composable
fun DrawerView() {
    val language = listOf("English ", "Hindi", "Arabic")
    val category = listOf("Cloth", "electronics", "fashion", "Food")
    LazyColumn {
        item {
            AddDrawerHeader(title = "Language")
        }
        items(language.size) { index ->

            AddDrawerContentView(
                title = language[index],
                selected = if (index == 1) true else false
            )
        }
        item {
            AddDrawerHeader(title = "Category")
        }

        items(category.size) { index ->

            AddDrawerContentView(
                title = category[index],
                selected = if (index == 2) true else false
            )
        }
    }

}

@Composable
fun AddDrawerContentView(title: String, selected: Boolean) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .clickable {}
            .padding(horizontal = 16.dp, vertical = 12.dp),


        ) {

        if (title.isNotEmpty()) {
            if (selected)
                Text(
                    text = title,
                    modifier = Modifier.weight(1f),
                    color = Color.Black,
                    style = TextStyle(
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = Color.Black
                    )
                )
            else
                Text(text = title, modifier = Modifier.weight(1f), fontSize = 12.sp)
        }

    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DefaultLayout(
    drawerState: DrawerState,
    scope: CoroutineScope,
    content: @Composable() () -> Unit
) {
    ModalNavigationDrawer(
        drawerState = drawerState,
        content = {
            Scaffold(
                bottomBar = { BottomBar() },
            ) {
                Surface(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(bottom = it.calculateBottomPadding()),
                    color = MaterialTheme.colorScheme.background
                ) {
                    content()
                }
            }
        },
        drawerContent = {
            DrawerView()
        },
    )
}

@Preview
@Composable
fun BottomBar() {
    BottomAppBar(
        icons = {
            IconButton(
                onClick = {},
                modifier = Modifier.fillMaxHeight(),
            ) {
                Icon(
                    imageVector = Icons.Rounded.Menu,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    contentDescription = "Open/Close Menu",
                )
            }
            IconButton(
                onClick = {},
                modifier = Modifier.fillMaxHeight(),
            ) {
                Icon(
                    imageVector = Icons.Rounded.Search,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    contentDescription = "Search",
                )
            }
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { /* do something */ },
                elevation = BottomAppBarDefaults.floatingActionButtonElevation()
            ) {
                Icon(Icons.Filled.Add, "Add Note")
            }
        }
    )
}



