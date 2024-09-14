package com.logomann.datascanner20.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.logomann.datascanner20.R
//import com.logomann.datascanner20.ui.theme.errorLight
import com.logomann.datascanner20.ui.theme.primaryLight


@Composable
fun CreateCameraButton(navController: NavController, modifier: Modifier) {
    IconButton(
        onClick = {
            navController.navigate(Screen.Camera.route)
        },
        modifier = modifier
            .padding(top = 52.dp, end = 18.dp)
    ) {
        Icon(
            painter = painterResource(id = R.drawable.camera),
            contentDescription = null,
            modifier = Modifier.fillMaxSize()
        )
    }
}

@Composable
fun CreateButtonsRow(
    modifier: Modifier,
    onClickOk: () -> Unit,
    onClickClear: () -> Unit,
    okBtnName: String = stringResource(id = R.string.ok)
) {
    Row(
        horizontalArrangement = Arrangement.SpaceAround,
        modifier = modifier
            .fillMaxWidth()
            .padding(top = 24.dp)
    ) {
        Button(
            onClick = onClickOk

        ) {
            Text(
                text = okBtnName,
                fontSize = 20.sp
            )
        }
        Button(
            onClick = onClickClear,
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.error
            )
        ) {
            Text(
                text = stringResource(id = R.string.clear),
                fontSize = 20.sp
            )
        }
    }
}