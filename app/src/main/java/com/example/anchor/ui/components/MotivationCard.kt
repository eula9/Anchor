package com.example.anchor.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.anchor.R
import com.example.anchor.ui.theme.motivationCardColors

/**
 * 每日激励语卡片（显示在身份锚点下方）。
 */
@Composable
fun MotivationCard(
    quote: String,
    modifier: Modifier = Modifier,
) {
    val colors = motivationCardColors()

    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = colors.container),
        border = BorderStroke(1.dp, colors.border),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 18.dp),
        ) {
            Text(
                text = stringResource(R.string.motivation_card_title),
                style = MaterialTheme.typography.labelLarge,
                color = colors.title,
            )

            Text(
                text = quote.ifBlank { stringResource(R.string.motivation_loading) },
                style = MaterialTheme.typography.bodyLarge,
                fontStyle = FontStyle.Italic,
                color = colors.body,
                textAlign = TextAlign.Start,
                modifier = Modifier.padding(top = 10.dp),
            )
        }
    }
}
