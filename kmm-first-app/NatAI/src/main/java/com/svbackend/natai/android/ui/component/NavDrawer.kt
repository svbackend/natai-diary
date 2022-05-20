package com.svbackend.natai.android.ui.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.outlined.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.svbackend.natai.android.viewmodel.NoteViewModel

@Composable
fun NavDrawer(
    modifier: Modifier = Modifier,
    onLogin: () -> Unit,
    vm: NoteViewModel
) {
    val cnt = vm.notes.collectAsState(initial = emptyList()).value.size

    LazyColumn(modifier = modifier) {
        // use `item` for separate elements like headers
        // and `items` for lists of identical elements
        item {
            Text(
                text = "NATAI",
                color = MaterialTheme.colorScheme.primary,
                fontSize = 24.sp,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 24.dp)
            )
        }

        item { Divider(thickness = 0.3.dp) }

        item { AccountItem(onLogin, vm) }

        item { Spacer(modifier.padding(top = 8.dp)) }
        item { DrawerItem(icon = Icons.Filled.Email, title = "All Notes", "$cnt") }
        item { Spacer(modifier.padding(top = 8.dp)) }
        item { Divider(thickness = 0.3.dp) }

        item { Spacer(modifier.padding(top = 8.dp)) }
        item { DrawerItem(icon = Icons.Outlined.AccountBox, title = "Account") }
        item { DrawerItem(icon = Icons.Outlined.Build, title = "Settings") }

//        item { DrawerCategory(title = "RECENT LABELS") }
//        item { DrawerItem(icon = Icons.Outlined.Send, title = "[Imap]/Trash") }
//        item { DrawerItem(icon = Icons.Outlined.Close, title = "facebook") }
//
//        item { DrawerCategory(title = "ALL LABELS") }
//        item { DrawerItem(icon = Icons.Outlined.DateRange, title = "Starred") }
//        item { DrawerItem(icon = Icons.Outlined.Done, title = "Snoozed") }
//        item { DrawerItem(icon = Icons.Outlined.Email, title = "Important", "99+") }
//        item { DrawerItem(icon = Icons.Outlined.Send, title = "Sent", "99+") }
//        item { DrawerItem(icon = Icons.Outlined.Info, title = "Scheduled", "99+") }
//        item { DrawerItem(icon = Icons.Outlined.MailOutline, title = "Outbox", "10") }

    }

}

@Composable
fun DrawerItem(icon: ImageVector, title: String, msgCount: String = "") {

    Row {
        Icon(imageVector = icon, modifier = Modifier.padding(16.dp), contentDescription = null)
        Text(
            modifier = Modifier
                .weight(1f)
                .align(Alignment.CenterVertically)
                .padding(start = 8.dp),
            text = title,
            fontFamily = FontFamily.SansSerif,
            fontWeight = FontWeight.SemiBold,
            fontSize = 14.sp,
            textAlign = TextAlign.Start
        )

        if (msgCount.isNotEmpty()) {
            Text(
                modifier = Modifier
                    .align(Alignment.CenterVertically)
                    .padding(16.dp),
                text = msgCount,
                style = MaterialTheme.typography.labelSmall,
                textAlign = TextAlign.Start
            )
        }

    }

}

@Composable
fun DrawerCategory(title: String) {

    Text(
        text = title,
        letterSpacing = 0.7.sp,
        color = MaterialTheme.colorScheme.onBackground,
        fontSize = 12.sp,
        modifier = Modifier.padding(16.dp)
    )

}

@Composable
fun AccountItem(
    onLogin: () -> Unit,
    vm: NoteViewModel
) {
    val isLoggedIn = vm.isLoggedIn.collectAsState(initial = false).value

    if (isLoggedIn) {
        Text(text = "Logged!")
    } else {
        Row(
            Modifier.clickable(onClick = onLogin)
        ) {
            Icon(
                imageVector = Icons.Filled.AccountCircle,
                modifier = Modifier.padding(16.dp),
                contentDescription = ""
            )
            Text(
                modifier = Modifier
                    .weight(1f)
                    .align(Alignment.CenterVertically)
                    .padding(start = 8.dp),
                text = "Account",
                fontFamily = FontFamily.SansSerif,
                fontWeight = FontWeight.SemiBold,
                fontSize = 14.sp,
                textAlign = TextAlign.Start
            )

            Icon(
                imageVector = Icons.Filled.ArrowForward,
                modifier = Modifier
                    .align(Alignment.CenterVertically)
                    .padding(16.dp),
                contentDescription = ""
            )
        }
    }
}