package com.travelplanner.irida.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Hotel
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.compose.SubcomposeAsyncImage
import coil.request.CachePolicy
import coil.request.ImageRequest
import com.travelplanner.irida.BuildConfig
import com.travelplanner.irida.ui.theme.NavyLight
import com.travelplanner.irida.ui.theme.NavyMid
import com.travelplanner.irida.ui.theme.TurquoisePrimary

// ── URL resolver ──────────────────────────────────────────────────────────────

/**
 * Converts a relative image path returned by the Hotels API (e.g. "/images/BCN01.png")
 * into an absolute URL using the configured [BuildConfig.HOTELS_API_URL] base.
 *
 * Already-absolute URLs (http / https) are returned unchanged.
 */
fun resolveHotelImageUrl(path: String): String {
    if (path.isBlank()) return ""
    if (path.startsWith("http://") || path.startsWith("https://")) return path
    val base       = BuildConfig.HOTELS_API_URL.trimEnd('/')
    val normalized = if (path.startsWith('/')) path else "/$path"
    return "$base$normalized"
}

// ── Shared composable ─────────────────────────────────────────────────────────

/**
 * Reusable hotel image composable backed by Coil.
 *
 * Features:
 * - Resolves relative API paths to absolute URLs via [resolveHotelImageUrl]
 * - Memory + disk caching enabled, 300 ms crossfade
 * - Spinner placeholder while loading; muted hotel-icon fallback on error
 */
@Composable
fun HotelAsyncImage(
    url: String,
    contentDescription: String? = null,
    modifier: Modifier = Modifier,
    contentScale: ContentScale = ContentScale.Crop
) {
    val resolvedUrl = remember(url) { resolveHotelImageUrl(url) }

    SubcomposeAsyncImage(
        model = ImageRequest.Builder(LocalContext.current)
            .data(resolvedUrl)
            .crossfade(300)
            .memoryCachePolicy(CachePolicy.ENABLED)
            .diskCachePolicy(CachePolicy.ENABLED)
            .build(),
        contentDescription = contentDescription,
        contentScale = contentScale,
        modifier = modifier,
        loading = {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(NavyMid),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(
                    color = TurquoisePrimary,
                    strokeWidth = 2.dp,
                    modifier = Modifier.size(24.dp)
                )
            }
        },
        error = {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.linearGradient(listOf(NavyMid, NavyLight))
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Filled.Hotel,
                    contentDescription = null,
                    tint = TurquoisePrimary.copy(alpha = 0.35f),
                    modifier = Modifier.size(40.dp)
                )
            }
        }
    )
}
