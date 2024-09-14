package com.logomann.datascanner20.ui.screens

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.constraintlayout.compose.ConstraintLayout
import com.logomann.datascanner20.ui.theme.DataScanner20Theme
import com.logomann.datascanner20.ui.theme.primaryLight


@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun LoadingScreen() {
    ConstraintLayout(modifier = Modifier.fillMaxSize()) {
        val indicator = createRef()
        CircularProgressIndicator(
            color = primaryLight,
            modifier = Modifier.constrainAs(indicator) {
                top.linkTo(parent.top)
                bottom.linkTo(parent.bottom)
                start.linkTo(parent.start)
                end.linkTo(parent.end)
            }
        )
    }
}

@Composable
@Preview
fun LoadingPreview() {
    DataScanner20Theme {
        LoadingScreen()
    }
}