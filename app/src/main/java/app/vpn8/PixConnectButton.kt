package com.pixvpn.gram

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.*
import androidx.compose.ui.graphics.vector.PathParser
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import kotlin.math.cos
import kotlin.math.sin

// SVG path data, rendered natively: no WebView, bitmap scaling or network asset.
private const val ROCKET_PATH = "M12 2 C7 5 6 11 7 16 L10 19 L14 19 L17 16 C18 11 17 5 12 2 Z M7 11 L3 16 L3 21 L8 18 Z M17 11 L21 16 L21 21 L16 18 Z"

@Composable
fun ConnectButton(
    phase: Phase,
    onClick: () -> Unit,
    onOptions: () -> Unit,
    optionsOpen: Boolean,
    failed: Boolean = false,
) {
    val live = phase == Phase.CONNECTED
    val launching = phase == Phase.CONNECTING || phase == Phase.RECONNECTING
    val label = stringResource(when {
        phase == Phase.STOPPING -> R.string.stopping
        live -> R.string.disconnect
        phase == Phase.RECONNECTING -> R.string.reconnecting
        launching -> R.string.connecting
        failed -> R.string.pix_retry
        else -> R.string.tap_to_connect
    })
    val progress by animateFloatAsState(
        if (live) 1f else if (launching) .58f else 0f,
        tween(if (live) 1100 else 800, easing = FastOutSlowInEasing), label = "rocketFlight")
    val debris = remember { Animatable(0f) }
    LaunchedEffect(failed) {
        debris.snapTo(0f)
        if (failed) debris.animateTo(1f, tween(650))
    }
    // Only the flame moves in flight; connected is a still, landed rocket.
    val flame = if (launching) {
        val transition = rememberInfiniteTransition(label = "engineFlame")
        val length by transition.animateFloat(.65f, 1f,
            infiniteRepeatable(tween(360), RepeatMode.Reverse), label = "flameLength")
        length
    } else 0f
    val rocket = remember { PathParser().parsePathString(ROCKET_PATH).toPath() }
    val palette = remember { listOf(Color(0xFFFFDF67), Color(0xFFFFAB65), Color(0xFF8BD9A4), Color(0xFF70BFFF)) }
    val ink = Color(0xFF152B3B)
    Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
        BoxWithConstraints(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
            val diameter = minOf(224.dp, maxWidth)
            Box(Modifier.size(diameter).clip(CircleShape)
                .background(Brush.linearGradient(palette))
                .border(2.dp, Color.White.copy(alpha = .30f), CircleShape)
                .clickable(enabled = phase != Phase.STOPPING, role = Role.Button, onClickLabel = label, onClick = onClick)
                .semantics(mergeDescendants = true) {
                    contentDescription = label
                    stateDescription = when {
                        failed -> "Connection failed"
                        live -> "Connected, rocket landed on the moon"
                        launching -> "Connecting, rocket launching"
                        else -> "Disconnected"
                    }
                }, contentAlignment = Alignment.Center) {
                Canvas(Modifier.fillMaxSize().padding(20.dp)) {
                    val unit = size.minDimension / 184f
                    scale(unit, pivot = Offset.Zero) {
                        // Subtle orbital ring and stars stay inside the fixed touch target.
                        drawCircle(ink.copy(alpha = .12f), 72f, Offset(92f, 92f), style = Stroke(1.5f))
                        listOf(Offset(28f, 46f), Offset(53f, 24f), Offset(154f, 114f), Offset(30f, 131f)).forEach {
                            drawCircle(ink.copy(alpha = .35f), 2f, it)
                        }
                        val moon = Offset(132f, 49f)
                        drawCircle(Color(0xFFFFF5CB), 25f, moon)
                        drawCircle(ink.copy(alpha = .10f), 6f, moon + Offset(7f, -6f))
                        drawCircle(ink.copy(alpha = .09f), 4f, moon + Offset(-9f, 8f))
                        val x = 55f + 62f * progress
                        val y = 120f - 54f * progress
                        if (failed) {
                            val blast = debris.value
                            repeat(9) { i ->
                                val angle = i * (Math.PI * 2 / 9)
                                val center = Offset(92f + cos(angle).toFloat() * (12f + blast * 39f), 98f + sin(angle).toFloat() * (12f + blast * 39f))
                                rotate(i * 37f + blast * 80f, center) {
                                    drawRect(if (i % 2 == 0) ink else Color(0xFFFF7043), center - Offset(3f, 6f), androidx.compose.ui.geometry.Size(6f, 12f))
                                }
                            }
                            drawCircle(ink, 13f, Offset(92f, 98f), style = Stroke(3f))
                            drawLine(ink, Offset(87f, 93f), Offset(97f, 103f), 3f)
                            drawLine(ink, Offset(97f, 93f), Offset(87f, 103f), 3f)
                        } else {
                            translate(x, y) {
                                rotate(38f, Offset.Zero) {
                                    scale(2.4f, pivot = Offset.Zero) {
                                        translate(-12f, -12f) {
                                            if (launching) {
                                                val fire = Path().apply { moveTo(9f, 19f); quadraticTo(12f, 23f + 5f * flame, 12f, 25f + 6f * flame); quadraticTo(15f, 23f, 15f, 19f); close() }
                                                drawPath(fire, Color(0xFFFFF5CB))
                                            }
                                            drawPath(rocket, ink)
                                            drawCircle(Color(0xFF8BD9E9), 2.5f, Offset(12f, 10f))
                                        }
                                    }
                                }
                            }
                        }
                        if (live) {
                            drawCircle(ink, 10f, Offset(145f, 148f))
                            drawLine(Color.White, Offset(140f, 148f), Offset(144f, 152f), 2f)
                            drawLine(Color.White, Offset(144f, 152f), Offset(151f, 144f), 2f)
                        }
                    }
                }
            }
        }
        Spacer(Modifier.height(12.dp))
        Text(label, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth())
        TextButton(onClick = onOptions, enabled = phase == Phase.IDLE,
            modifier = Modifier.heightIn(min = 48.dp)) {
            Text(stringResource(R.string.a11y_options))
            Icon8(Ico.CHEVRON, LocalContentColor.current, 20.dp,
                Modifier.rotate(if (optionsOpen) 180f else 0f))
        }
    }
}
