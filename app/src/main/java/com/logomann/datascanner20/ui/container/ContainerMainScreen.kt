package com.logomann.datascanner20.ui.container

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import com.logomann.datascanner20.R
import com.logomann.datascanner20.ui.car.customTabIndicatorOffset
import kotlinx.coroutines.launch

@Composable
fun ContainerMainScreen(){
    val tabs = listOf(
        stringResource(R.string.container_arrival),
        stringResource(R.string.container_in_carriage)
    )
    val pagerState = rememberPagerState(initialPage = 0) {
        tabs.size
    }
    val scope = rememberCoroutineScope()
    val density = LocalDensity.current
    val tabWidths = remember {
        val tabWidthStateList = mutableStateListOf<Dp>()
        repeat(tabs.size) {
            tabWidthStateList.add(0.dp)
        }
        tabWidthStateList
    }

    ConstraintLayout(modifier = Modifier.fillMaxSize()) {
        val (tabRow, text) = createRefs()
        ScrollableTabRow(
            divider = {},
            edgePadding = 0.dp,
            selectedTabIndex = pagerState.currentPage,
            modifier = Modifier
                .constrainAs(tabRow) {
                    top.linkTo(parent.top)
                },
            indicator = { tabPositions ->
                TabRowDefaults.SecondaryIndicator(
                    modifier = Modifier
                        .customTabIndicatorOffset(
                            tabPositions[pagerState.currentPage],
                            tabWidths[pagerState.currentPage]
                        ),
                    color = MaterialTheme.colorScheme.primary
                )
            }) {
            tabs.forEachIndexed { index, tab ->
                Tab(
                    selected = pagerState.currentPage == index,
                    onClick = { scope.launch { pagerState.scrollToPage(index) } },
                    text = {
                        Text(text = tab, color = MaterialTheme.colorScheme.primary,
                            onTextLayout = { textLayoutResult ->
                                tabWidths[index] = with(density) {
                                    textLayoutResult.size.width.toDp()
                                }
                            })
                    })
            }
        }
        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .constrainAs(text) {
                    top.linkTo(tabRow.bottom)
                    bottom.linkTo(parent.bottom)
                    height = Dimension.fillToConstraints
                }
        ) { tabIndex ->
            when (tabIndex) {
                0 -> {
                    ContainerArrivalScreen()
                }

                1 -> {
                    ContainerInCarriage()
                }
            }
        }

    }
}