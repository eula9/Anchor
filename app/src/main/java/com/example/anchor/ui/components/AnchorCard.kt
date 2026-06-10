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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.anchor.R
import com.example.anchor.domain.model.IdentityAnchor
import com.example.anchor.ui.theme.anchorCardColors

/**
 * 身份锚点卡片。
 */
@Composable
fun AnchorCard(
    anchor: IdentityAnchor?,
    modifier: Modifier = Modifier,
) {
    val colors = anchorCardColors()

    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = colors.container),
        border = BorderStroke(1.dp, colors.border),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 24.dp),
        ) {
            Text(
                text = stringResource(R.string.anchor_card_title),
                style = MaterialTheme.typography.labelLarge,
                color = colors.title,
            )

            Text(
                text = anchor?.statement ?: stringResource(R.string.anchor_loading),
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.SemiBold,
                color = colors.body,
                modifier = Modifier.padding(top = 8.dp),
            )

            anchor?.let {
                Text(
                    text = if (it.isExpired) {
                        stringResource(R.string.anchor_expired_hint)
                    } else {
                        stringResource(
                            R.string.anchor_progress,
                            it.currentDay,
                            it.durationDays,
                        )
                    },
                    style = MaterialTheme.typography.bodyMedium,
                    color = colors.subtitle,
                    modifier = Modifier.padding(top = 12.dp),
                )
            }
        }
    }
}
