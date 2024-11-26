@file:OptIn(ExperimentalFoundationApi::class)

package edu.farmingdale.draganddropanim_demo

import android.content.ClipData
import android.content.ClipDescription
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.animateIntOffsetAsState
import androidx.compose.animation.core.repeatable
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.draganddrop.dragAndDropSource
import androidx.compose.foundation.draganddrop.dragAndDropTarget
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Image
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp

@Composable
fun DragAndDropBoxes(modifier: Modifier = Modifier) {
    var isPlaying by remember { mutableStateOf(true) }
    var pOffset by remember { mutableStateOf(IntOffset(0, 0)) } // Position of the rect
    var rotationAngle by remember { mutableStateOf(0f) } // Rotation angle
    var dragBoxIndex by remember { mutableIntStateOf(0) } // Tracks which box is active

    Column(modifier = Modifier.fillMaxSize()) {
        // Row for the draggable boxes
        Row(
                modifier = modifier
                        .fillMaxWidth()
                        .weight(0.2f)
        ) {
            val boxCount = 4

            repeat(boxCount) { index ->
                Box(
                        modifier = Modifier
                                .weight(1f)
                                .fillMaxHeight()
                                .padding(10.dp)
                                .border(1.dp, Color.Black)
                                .dragAndDropTarget(
                                        shouldStartDragAndDrop = { event ->
                                            event.mimeTypes().contains(ClipDescription.MIMETYPE_TEXT_PLAIN)
                                        },
                                        target = remember {
                                            object : DragAndDropTarget {
                                                override fun onDrop(event: DragAndDropEvent): Boolean {
                                                    isPlaying = !isPlaying
                                                    dragBoxIndex = index
                                                    return true
                                                }
                                            }
                                        }
                                ),
                        contentAlignment = Alignment.Center
                ) {
                    // AnimatedVisibility for the "Right" icon/text inside the box
                    AnimatedVisibility(
                            visible = index == dragBoxIndex,
                            enter = scaleIn() + fadeIn(),
                            exit = scaleOut() + fadeOut()
                    ) {
                        // Replace the text with an Image from the drawable folder
                        Image(
                                painter = painterResource(id = R.drawable.right_pic), // Load the image from drawable
                                contentDescription = "Right Command",
                                modifier = Modifier.size(40.dp) // You can adjust the size here
                        )
                    }
                }
            }
        }

        // Animation for the rect's position (translation)
        val pOffsetAnim by animateIntOffsetAsState(
                targetValue = pOffset,
                animationSpec = tween(3000, easing = LinearEasing)
        )

        // Animation for rotation
        val rotationAnim by animateFloatAsState(
                targetValue = rotationAngle,
                animationSpec = repeatable(
                        iterations = if (isPlaying) 10 else 1,
                        tween(durationMillis = 3000, easing = LinearEasing),
                        repeatMode = RepeatMode.Restart
                )
        )

        // Box where the draggable rect (icon) will go
        Box(
                modifier = Modifier
                        .fillMaxWidth()
                        .weight(0.8f)
                        .background(Color.Red)
        ) {
            // Rectangle that moves and rotates based on the animations
            Box(
                    modifier = Modifier
                            .size(100.dp, 200.dp) // Rectangle size
                            .background(Color.Blue) // Rectangle color
                            .offset(pOffsetAnim.x.dp, pOffsetAnim.y.dp) // Apply position translation
                            .rotate(rotationAnim) // Apply rotation
                            .dragAndDropSource {
                                detectTapGestures(
                                        onLongPress = { offset ->
                                            startTransfer(
                                                    transferData = DragAndDropTransferData(
                                                            clipData = ClipData.newPlainText("text", "")
                                                    )
                                            )
                                        }
                                )
                            }
            )
        }

        // Button to reset the rect position to the center
        Button(
                onClick = {
                    pOffset = IntOffset(0, 0) // Reset position to center
                    rotationAngle = 0f // Reset rotation
                },
                modifier = Modifier.padding(16.dp)
        ) {
            Text("Reset to Center")
        }
    }
}
