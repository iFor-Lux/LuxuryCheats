package com.luxury.cheats.features.update.ui

import android.view.ViewGroup
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Update
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.luxury.cheats.core.theme.LuxuryCheatsTheme
import com.luxury.cheats.core.ui.LogoWebViewManager

private val GREEN_STATUS_HEX = Color(0xFF00FF00)
private const val NEWS_REPEAT_COUNT = 4

/**
 * Pantalla completa de descarga de actualización.
 */
@Composable
fun DownloadUpdateScreen(
    modifier: Modifier = Modifier,
    onBackClick: () -> Unit = {},
    onDownloadClick: () -> Unit = {}
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .statusBarsPadding()
            .verticalScroll(scrollState)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        DownloadHeader(onBackClick)
        Spacer(modifier = Modifier.height(20.dp))
        DownloadMainCard(onDownloadClick)
        Spacer(modifier = Modifier.height(32.dp))
        DownloadNewsSection()
        Spacer(modifier = Modifier.height(32.dp))
    }
}

@Composable
private fun DownloadHeader(onBackClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight(),
        contentAlignment = Alignment.CenterStart
    ) {
        Box(
            modifier = Modifier
                .size(50.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.surfaceContainer)
                .clickable { onBackClick() },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.ArrowBack,
                contentDescription = "Volver",
                tint = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}

@Composable
private fun DownloadMainCard(onDownloadClick: () -> Unit) {
    Box(
        modifier = Modifier
            .size(width = 350.dp, height = 521.dp)
            .clip(RoundedCornerShape(50.dp))
            .background(MaterialTheme.colorScheme.surfaceContainer)
            .border(
                width = 1.dp,
                color = Color.White.copy(alpha = 0.46f),
                shape = RoundedCornerShape(50.dp)
            )
            .padding(24.dp),
        contentAlignment = Alignment.TopCenter
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            DownloadWebLogo()
            Spacer(modifier = Modifier.height(24.dp))
            DownloadVersionInfo()
            Spacer(modifier = Modifier.height(24.dp))
            DownloadDivider()
            Spacer(modifier = Modifier.height(24.dp))
            DownloadStatusInfo()
            Spacer(modifier = Modifier.weight(1f))
            DownloadActionButton(onDownloadClick)
        }
    }
}

@Composable
private fun DownloadWebLogo() {
    Box(
        modifier = Modifier
            .size(100.dp)
            .clip(CircleShape)
            .background(Color.Black)
    ) {
        AndroidView(
            factory = { ctx ->
                LogoWebViewManager.getOrCreateWebView(ctx).apply {
                    (parent as? ViewGroup)?.removeView(this)
                    layoutParams = ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT
                    )
                    setBackgroundColor(0x00000000)
                }
            },
            modifier = Modifier.fillMaxSize()
        )
    }
}

@Composable
private fun DownloadVersionInfo() {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = "v1.2.0",
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )
        Text(
            text = "Release 2025-12-06",
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun DownloadDivider() {
    HorizontalDivider(
        modifier = Modifier.width(300.dp),
        thickness = 1.dp,
        color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)
    )
}

@Composable
private fun DownloadStatusInfo() {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Icon(
            imageVector = Icons.Default.Update,
            contentDescription = null,
            modifier = Modifier.size(30.dp),
            tint = GREEN_STATUS_HEX
        )
        Text(
            text = "Actualizacion disponible",
            color = GREEN_STATUS_HEX,
            fontSize = 24.sp,
            fontWeight = FontWeight.SemiBold
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Version 2.0.0",
            color = Color.White,
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium
        )
        Text(
            text = "Release 2026-01-02",
            color = Color.White,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
private fun DownloadActionButton(onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier.size(width = 300.dp, height = 60.dp),
        shape = RoundedCornerShape(25.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer,
            contentColor = MaterialTheme.colorScheme.onSecondaryContainer
        )
    ) {
        Text(
            text = "DESCARGAR",
            fontSize = 24.sp,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
private fun DownloadNewsSection() {
    Column(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 4.dp),
        horizontalAlignment = Alignment.Start
    ) {
        Text(
            text = "Novedades",
            fontSize = 16.sp,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.padding(bottom = 12.dp, start = 8.dp)
        )
        Box(
            modifier = Modifier
                .size(width = 360.dp, height = 100.dp)
                .clip(RoundedCornerShape(20.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant)
                .padding(16.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                repeat(NEWS_REPEAT_COUNT) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(4.dp)
                                .background(MaterialTheme.colorScheme.primary, CircleShape)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Mejoras",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}

/** Preview de la pantalla de descarga en tema oscuro. */
@Preview(name = "Download Update Dark", showSystemUi = true)
@Composable
fun DownloadUpdateScreenPreviewDark() {
    LuxuryCheatsTheme(darkTheme = true) {
        DownloadUpdateScreen()
    }
}

/** Preview de la pantalla de descarga en tema claro. */
@Preview(name = "Download Update Light", showSystemUi = true)
@Composable
fun DownloadUpdateScreenPreviewLight() {
    LuxuryCheatsTheme(darkTheme = false) {
        DownloadUpdateScreen()
    }
}
