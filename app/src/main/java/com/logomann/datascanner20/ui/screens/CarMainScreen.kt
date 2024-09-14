package com.logomann.datascanner20.ui.screens

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.TabPosition
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.debugInspectorInfo
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.navigation.NavController
import com.logomann.datascanner20.R
import kotlinx.coroutines.launch

@Composable
fun CarMainScreen(
    navController: NavController
) {

    val tabs = listOf(
        stringResource(R.string.relocation),
        stringResource(R.string.search_by_VIN),
        stringResource(R.string.search_by_place),
        stringResource(R.string.lot_in_ladung),
        stringResource(R.string.car_lotting)
    )
    val pagerState = rememberPagerState(initialPage = 0) {
        tabs.size
    }

    ConstraintLayout(modifier = Modifier.fillMaxSize()) {
        val (tabRow, text) = createRefs()
        val density = LocalDensity.current
        val tabWidths = remember {
            val tabWidthStateList = mutableStateListOf<Dp>()
            repeat(tabs.size) {
                tabWidthStateList.add(0.dp)
            }
            tabWidthStateList
        }
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
                val scope = rememberCoroutineScope()
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
                    CarRelocationScreen(navController = navController)
                }

                1 -> {
                    CarSearchByVinScreen(navController = navController)
                }

                2 -> {
                    CarSearchByPlaceScreen()
                }

                3 -> {
                    CarLotInLadungScreen(navController = navController)
                }

                4 -> {
                    CarLottingScreen(navController = navController)
                }

                else -> {
                    Text(
                        text = "content$tabIndex",
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
        }

    }
}

fun Modifier.customTabIndicatorOffset(
    currentTabPosition: TabPosition,
    tabWidth: Dp
): Modifier = composed(
    inspectorInfo = debugInspectorInfo {
        name = "customTabIndicatorOffset"
        value = currentTabPosition
    }
) {
    val currentTabWidth by animateDpAsState(
        targetValue = tabWidth,
        animationSpec = tween(durationMillis = 250, easing = FastOutSlowInEasing)
    )
    val indicatorOffset by animateDpAsState(
        targetValue = ((currentTabPosition.left + currentTabPosition.right - tabWidth) / 2),
        animationSpec = tween(durationMillis = 250, easing = FastOutSlowInEasing)
    )
    fillMaxWidth()
        .wrapContentSize(Alignment.BottomStart)
        .offset(x = indicatorOffset)
        .width(currentTabWidth)
}


