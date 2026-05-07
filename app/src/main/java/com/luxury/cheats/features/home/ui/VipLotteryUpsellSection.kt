package com.luxury.cheats.features.home.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.luxury.cheats.R

@Composable
fun VipLotteryUpsellSection(
    onInfoClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val randomId = remember { (100000..999999).random() }
    
    val newsreaderFont = FontFamily(
        Font(com.luxury.cheats.R.font.newsreader_italic)
    )

    val titleText = buildAnnotatedString {
        append("Luxury ")
        withStyle(
            style = SpanStyle(
                fontFamily = newsreaderFont, 
                color = MaterialTheme.colorScheme.primary, 
                fontWeight = FontWeight.Bold
            )
        ) {
            append("Sorteo's")
        }
    }

    val descText = buildAnnotatedString {
        append("¡Participa diariamente por 999 diamantes en nuestros sorteos exclusivos para miembros ")
        withStyle(
            style = SpanStyle(
                fontFamily = newsreaderFont, 
                color = MaterialTheme.colorScheme.primary, 
                fontWeight = FontWeight.Bold
            )
        ) {
            append("VIPs")
        }
        append("!")
    }

    Box(
        modifier = modifier
            .width(341.dp)
            .clip(RoundedCornerShape(30.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f),
                shape = RoundedCornerShape(30.dp),
            )
            .clickable { onInfoClick() }
            .padding(vertical = 14.dp, horizontal = 16.dp),
        contentAlignment = Alignment.TopCenter
    ) {
        IconButton(
            onClick = onInfoClick,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(end = 4.dp)
                .background(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.05f), CircleShape)
                .size(24.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Info,
                contentDescription = "Detalles",
                tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                modifier = Modifier.size(14.dp)
            )
        }

        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = titleText,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            
            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = descText,
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f),
                textAlign = TextAlign.Center,
                lineHeight = 14.sp,
                modifier = Modifier.padding(horizontal = 12.dp)
            )

            Spacer(modifier = Modifier.height(12.dp))

            Box(
                modifier = Modifier
                    .width(259.dp)
                    .height(90.dp)
                    .clip(RoundedCornerShape(10.dp))
            ) {
                Image(
                    painter = painterResource(id = R.drawable.diamantes),
                    contentDescription = "Sorteo de Diamantes",
                    modifier = Modifier.fillMaxWidth(),
                    contentScale = ContentScale.FillWidth
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Box(
                modifier = Modifier
                    .width(259.dp)
                    .height(32.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.06f)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Ganador: ID: $randomId Ganó +999 diamantes",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

// Fin de VipLotteryUpsellSection

