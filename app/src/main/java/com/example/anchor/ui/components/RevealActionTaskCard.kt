package com.example.anchor.ui.components

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.AnchoredDraggableState
import androidx.compose.foundation.gestures.DraggableAnchors
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.animateTo
import androidx.compose.foundation.gestures.anchoredDraggable
import androidx.compose.foundation.gestures.snapTo
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch

private enum class RevealAnchor {
    Closed,
    Open,
}

private val RevealSnapSpec = spring<Float>(
    dampingRatio = Spring.DampingRatioNoBouncy,
    stiffness = Spring.StiffnessMediumLow,
)

/**
 * 左滑露出一小块操作区，可滑回；点击操作按钮后执行并收起。
 * 外层统一圆角裁剪，前景与操作区无缝贴合。
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun RevealActionTaskCard(
    colors: TaskListSectionColors,
    actionLabel: String,
    actionEnabled: Boolean,
    revealWidth: Dp,
    onActionClick: () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    val density = LocalDensity.current
    val revealWidthPx = with(density) { revealWidth.toPx() }
    val scope = rememberCoroutineScope()

    val dragState = remember(revealWidthPx, actionEnabled) {
        AnchoredDraggableState(
            initialValue = RevealAnchor.Closed,
            anchors = DraggableAnchors {
                RevealAnchor.Closed at 0f
                if (actionEnabled) {
                    RevealAnchor.Open at -revealWidthPx
                }
            },
        )
    }

    LaunchedEffect(actionEnabled) {
        if (!actionEnabled) {
            dragState.snapTo(RevealAnchor.Closed)
        }
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clip(TaskCardShape)
            .border(1.dp, colors.cardBorder, TaskCardShape),
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    if (actionEnabled) colors.swipeActionBackground else colors.cardBorder,
                ),
        ) {
            TextButton(
                onClick = {
                    if (actionEnabled) {
                        onActionClick()
                        scope.launch {
                            dragState.animateTo(
                                targetValue = RevealAnchor.Closed,
                                animationSpec = RevealSnapSpec,
                            )
                        }
                    }
                },
                enabled = actionEnabled,
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .width(revealWidth)
                    .fillMaxHeight(),
                contentPadding = PaddingValues(horizontal = 4.dp),
            ) {
                Text(
                    text = actionLabel,
                    style = MaterialTheme.typography.labelMedium,
                    color = colors.swipeActionText,
                    textAlign = TextAlign.Center,
                )
            }
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .graphicsLayer {
                    val offsetX = dragState.offset
                    translationX = if (offsetX.isNaN()) 0f else offsetX
                }
                .anchoredDraggable(
                    state = dragState,
                    orientation = Orientation.Horizontal,
                    enabled = actionEnabled,
                )
                .background(colors.cardContainer),
        ) {
            content()
        }
    }
}
