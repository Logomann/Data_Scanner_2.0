package com.logomann.datascanner20.ui.settings

import android.annotation.SuppressLint
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ModeEdit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.navigation.NavController
import com.logomann.datascanner20.R
import com.logomann.datascanner20.ui.settings.view_model.SettingsViewModel
import com.logomann.datascanner20.ui.theme.primaryLight
import kotlinx.coroutines.launch
import org.koin.compose.koinInject

const val MAXIMUM_TSD = 96
const val FIRST_AKULOVO_TSD = 1
const val LAST_AKULOVO_TSD = 39
const val FIRST_SPB_TSD = 40
const val LAST_SPB_TSD = 79
const val FIRST_GOLYTSINO_TSD = 80
const val LAST_GOLYTSINO_TSD = 89
const val FIRST_NF_TSD = 90
const val LAST_NF_TSD = 99

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Settings(
    navController: NavController,
    viewModel: SettingsViewModel = koinInject()
) {
    LaunchedEffect(Unit) {
        viewModel.compoundPicked = viewModel.compoundName
    }

    when (viewModel.pickedID) {
        in FIRST_AKULOVO_TSD..LAST_AKULOVO_TSD -> {
            viewModel.compoundName = stringResource(R.string.akulovo)
        }

        in FIRST_SPB_TSD..LAST_SPB_TSD -> {
            viewModel.compoundName = stringResource(R.string.beloostrov)
        }

        in FIRST_GOLYTSINO_TSD..LAST_GOLYTSINO_TSD -> {
            viewModel.compoundName = stringResource(R.string.golytsino)
        }

        in FIRST_NF_TSD..LAST_NF_TSD -> {
            viewModel.compoundName = stringResource(R.string.naro_fominsk)
        }
    }

    val scope = rememberCoroutineScope()
    if (viewModel.openDialog) {
        val lazyScrollState = rememberLazyListState()
        val middle = remember {
            derivedStateOf {
                if (lazyScrollState.firstVisibleItemIndex + 2 == viewModel.listOfTsd.size) {
                    viewModel.listOfTsd.size
                } else {
                    lazyScrollState.firstVisibleItemIndex + 1
                }
            }
        }
        LaunchedEffect(Unit) {
            scope.launch {
                val indexToScroll =
                    if (viewModel.tsdId + 2 == viewModel.listOfTsd.size) {
                        viewModel.listOfTsd.lastIndex
                    } else {
                        viewModel.tsdId + 1
                    }
                lazyScrollState.scrollToItem(indexToScroll, -200)
            }
        }
        AlertDialog(
            title = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(text = stringResource(R.string.tsd))
                    Text(text = viewModel.compoundName)
                }
            },
            onDismissRequest = { viewModel.openDialog = false },
            dismissButton = {
                Button(onClick = {
                    viewModel.pickedID = viewModel.tsdId
                    viewModel.openDialog = false
                }) {
                    Text(text = stringResource(R.string.cancel))

                }
            },
            confirmButton = {
                Button(onClick = {
                    viewModel.tsdId = viewModel.pickedID
                    viewModel.setScannerID()
                    viewModel.compoundPicked = viewModel.compoundName
                    viewModel.openDialog = false
                })
                {
                    Text(text = stringResource(id = R.string.ok))
                }
            },
            text = {

                LazyColumn(
                    state = lazyScrollState,
                    modifier = Modifier
                        .height(100.dp)
                ) {
                    itemsIndexed(viewModel.listOfTsd) { index, item ->
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            var isVisible = false
                            if (index == middle.value) {
                                isVisible = true
                                viewModel.pickedID = item.toInt()
                            }
                            if (isVisible) HorizontalDivider(
                                color = MaterialTheme.colorScheme.primary
                            )
                            Text(
                                text = item,
                                fontSize = if (isVisible) 40.sp else 28.sp,
                                color = if (isVisible) MaterialTheme.colorScheme.error
                                else MaterialTheme.colorScheme.onSurface
                            )
                            if (isVisible) HorizontalDivider(
                                color = MaterialTheme.colorScheme.primary
                            )
                        }

                    }
                }
            }
        )

    }
    ConstraintLayout(
        modifier = Modifier.fillMaxSize()
    ) {
        val (topBar, darkThemeRow, tsdTxt, tsdId) = createRefs()
        TopAppBar(
            modifier = Modifier.constrainAs(topBar) {
                start.linkTo(parent.start)
                end.linkTo(parent.end)
                top.linkTo(parent.top)
            },
            title = {
                Text(
                    text = stringResource(id = R.string.settings),
                    fontSize = 22.sp
                )
            },
            navigationIcon = {
                IconButton(onClick = {
                    navController.navigateUp()
                }) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = null
                    )
                }
            })

        val checkedState = remember { mutableStateOf(viewModel.getTheme()) }
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.constrainAs(darkThemeRow) {
                start.linkTo(parent.start)
                end.linkTo(parent.end)
                top.linkTo(topBar.bottom)
                width = Dimension.fillToConstraints
            }
        ) {
            Text(
                text = stringResource(id = R.string.dark_theme),
                fontSize = 16.sp,
                modifier = Modifier.padding(start = 12.dp)
            )
            Switch(
                checked = checkedState.value,
                onCheckedChange = {
                    viewModel.switchTheme()
                    checkedState.value = it
                },
                modifier = Modifier
                    .padding(end = 12.dp),
                colors = SwitchDefaults.colors(
                    checkedThumbColor = primaryLight
                )
            )
        }

        Text(
            text = stringResource(id = R.string.scanner_id) + " (${viewModel.compoundPicked})",
            fontSize = 16.sp,
            modifier = Modifier
                .constrainAs(tsdTxt) {
                    start.linkTo(parent.start, 12.dp)
                    top.linkTo(darkThemeRow.bottom)
                }
        )
        val lens = createRef()
        Text(
            text = viewModel.tsdId.toString(),
            modifier = Modifier
                .constrainAs(tsdId) {
                    top.linkTo(darkThemeRow.bottom)
                    end.linkTo(parent.end, 30.dp)
                },
            fontSize = 24.sp
        )

        Icon(Icons.Filled.ModeEdit,
            contentDescription = null,
            modifier = Modifier
                .size(width = 12.dp, height = 12.dp)
                .clickable { viewModel.openDialog = true }
                .constrainAs(lens) {
                    start.linkTo(tsdId.end)
                    top.linkTo(tsdId.top)
                }
        )
    }
}
