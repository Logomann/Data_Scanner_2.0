package com.logomann.datascanner20.ui.screens

import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.widget.LinearLayout
import androidx.camera.mlkit.vision.MlKitAnalyzer
import androidx.camera.view.CameraController.COORDINATE_SYSTEM_VIEW_REFERENCED
import androidx.camera.view.LifecycleCameraController
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.FlashOff
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.logomann.datascanner20.ui.camera.CameraScreenState
import com.logomann.datascanner20.ui.camera.view_model.CameraViewModel
import com.logomann.datascanner20.util.CAMERA_RESULT
import kotlinx.coroutines.delay
import org.koin.androidx.compose.koinViewModel


@Composable
fun Camera(
    navController: NavController,
    viewModel: CameraViewModel = koinViewModel()
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val state = viewModel.state.collectAsState()
    var hasFlash by remember {
        mutableStateOf(false)
    }
    var isTorchOn by remember {
        mutableStateOf(false)
    }
    val previewView = remember {
        PreviewView(context)
    }
    val cameraController = remember {
        LifecycleCameraController(context).apply {
            bindToLifecycle(lifecycleOwner)
            isTapToFocusEnabled = true
            val scanner = viewModel.getScanner()
            setImageAnalysisAnalyzer(
                ContextCompat.getMainExecutor(context),
                MlKitAnalyzer(
                    listOf(scanner),
                    COORDINATE_SYSTEM_VIEW_REFERENCED,
                    ContextCompat.getMainExecutor(context)
                ) { result: MlKitAnalyzer.Result? ->
                    val barcodeResults = result?.getValue(scanner)
                    if ((barcodeResults == null) ||
                        (barcodeResults.size == 0) ||
                        (barcodeResults.first() == null)
                    ) {
                        return@MlKitAnalyzer
                    }
                    viewModel.setResult(barcodeResults[0])
                    previewView.overlay.clear()
                }
            )
        }
    }
    LaunchedEffect(Unit) {
        previewView.apply {
            layoutParams = LinearLayout.LayoutParams(MATCH_PARENT, MATCH_PARENT)
            scaleType = PreviewView.ScaleType.FILL_START
            implementationMode = PreviewView.ImplementationMode.COMPATIBLE
            controller = cameraController
        }
        delay(100L)
        hasFlash = cameraController.cameraInfo?.hasFlashUnit() ?: false
    }

    when (val collectState = state.value) {
        CameraScreenState.Default -> {}
        is CameraScreenState.Result -> {
            navController.previousBackStackEntry?.savedStateHandle?.set(
                CAMERA_RESULT,
                collectState.result
            )
            navController.navigateUp()
        }
    }
    fun switchFlash() {
        if (hasFlash)
            isTorchOn = if (isTorchOn) {
                cameraController.cameraControl!!.enableTorch(false)
                false
            } else {
                cameraController.cameraControl!!.enableTorch(true)
                true
            }
    }
    ConstraintLayout(
        modifier = Modifier.fillMaxSize()
    ) {
        val (arrowBtn, flashBtn) = createRefs()
        AndroidView(
            modifier = Modifier.fillMaxSize(),
            factory = { _ ->
                previewView
            },
            onRelease = {
                cameraController.unbind()
            }
        )
        if (hasFlash) {
            IconButton(
                onClick = { switchFlash() },
                modifier = Modifier
                    .constrainAs(flashBtn) {
                        top.linkTo(parent.top)
                        end.linkTo(parent.end)
                    }
                    .size(height = 48.dp, width = 48.dp)
                    .padding(top = 12.dp)
            ) {
                Icon(
                    if (isTorchOn) Icons.Filled.FlashOff else Icons.Filled.FlashOn,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
        IconButton(
            onClick = { navController.navigateUp() },
            modifier = Modifier
                .size(height = 48.dp, width = 48.dp)
                .padding(top = 12.dp)
                .constrainAs(arrowBtn) {
                    top.linkTo(parent.top)
                    start.linkTo(parent.start)
                }
        ) {
            Icon(
                Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}

@Preview
@Composable
fun CameraPreview() {
    Camera(rememberNavController())
}

