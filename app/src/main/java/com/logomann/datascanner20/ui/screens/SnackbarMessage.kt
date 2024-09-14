package com.logomann.datascanner20.ui.screens

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.logomann.datascanner20.ui.theme.green
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun SnackbarMessage(
    message: String,
    scope: CoroutineScope = rememberCoroutineScope(),
    snackbarHostState: SnackbarHostState
) {
    LaunchedEffect(Unit) {
        scope.launch {
            snackbarHostState.showSnackbar(
                message = message,
                duration = SnackbarDuration.Short
            )
        }
    }
}

@Composable
fun CreateSnackbarHost(
    snackbarHostState: SnackbarHostState,
    isError: Boolean,
    modifier: Modifier
) {
    SnackbarHost(
        snackbarHostState,
        modifier = modifier
            .fillMaxWidth()
    ) { data ->
        Snackbar(
            containerColor = if (isError) MaterialTheme.colorScheme.error else green,
            modifier = Modifier.padding(16.dp),
            content = {
                Text(
                    text = data.visuals.message,
                    color = Color.White,
                    modifier = Modifier.clip(RoundedCornerShape(8.dp))
                )
            })
    }
}