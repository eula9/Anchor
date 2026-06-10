package com.example.anchor.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.anchor.R
import com.example.anchor.domain.model.StreakInfo
import com.example.anchor.ui.theme.streakCardColors

/**
 * 连续完成固定任务天数卡片。
 */
@Composable
fun StreakCard(
    streakInfo: StreakInfo,
    modifier: Modifier = Modifier,
) {
    val colors = streakCardColors()

    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = colors.container),
        border = BorderStroke(1.dp, colors.border),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 20.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = stringResource(R.string.streak_card_title),
                    style = MaterialTheme.typography.labelLarge,
                    color = colors.title,
                )
                Text(
                    text = streakSubtitle(streakInfo),
                    style = MaterialTheme.typography.bodyMedium,
                    color = colors.body,
                    modifier = Modifier.padding(top = 4.dp),
                )
                if (streakInfo.longestStreak > 0) {
                    Text(
                        text = stringResource(
                            R.string.streak_longest_record,
                            streakInfo.longestStreak,
                        ),
                        style = MaterialTheme.typography.labelMedium,
                        color = colors.body.copy(alpha = 0.7f),
                        modifier = Modifier.padding(top = 8.dp),
                    )
                }
            }

            Text(
                text = streakInfo.currentStreak.toString(),
                style = MaterialTheme.typography.displaySmall,
                fontWeight = FontWeight.Bold,
                color = colors.accent,
            )
        }
    }
}

@Composable
private fun streakSubtitle(streakInfo: StreakInfo): String {
    return when {
        streakInfo.actionTakenToday -> stringResource(R.string.streak_status_done_today)
        streakInfo.currentStreak > 0 -> stringResource(R.string.streak_status_keep_going)
        else -> stringResource(R.string.streak_status_start)
    }
}
