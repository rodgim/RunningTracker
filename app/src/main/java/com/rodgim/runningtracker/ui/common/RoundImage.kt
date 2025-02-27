package com.rodgim.runningtracker.ui.common

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.rodgim.runningtracker.R
import com.rodgim.runningtracker.ui.theme.RunningTrackerTheme

@Composable
fun RoundImage(
    image: Painter,
    modifier: Modifier
) {
    Image(
        painter = image,
        contentDescription = null,
        modifier = modifier
            .aspectRatio(1f, matchHeightConstraintsFirst = true)
            .border(
                width = 1.dp,
                color = Color.LightGray,
                shape = CircleShape
            )
            .padding(3.dp)
            .clip(CircleShape)
    )
}

@Preview
@Composable
fun RoundImagePreview() {
    RunningTrackerTheme {
        RoundImage(
            image = painterResource(R.drawable.ic_launcher_foreground),
            modifier = Modifier
                .size(100.dp)
        )
    }
}