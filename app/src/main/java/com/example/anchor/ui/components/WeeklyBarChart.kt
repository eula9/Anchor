package com.example.anchor.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

/**
 * 近 7 日完成数柱状图（简易版）。
 */
@Composable
fun WeeklyBarChart(
    labels: List<String>,
    fixedCounts: List<Int>,
    optionalCounts: List<Int>,
    modifier: Modifier = Modifier,
) {
    val maxValue = (fixedCounts + optionalCounts).maxOrNull()?.coerceAtLeast(1) ?: 1
    val chartHeight = 120.dp

    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Bottom,
    ) {
        labels.forEachIndexed { index, label ->
            val fixed = fixedCounts.getOrElse(index) { 0 }
            val optional = optionalCounts.getOrElse(index) { 0 }

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.weight(1f),
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(3.dp),
                    verticalAlignment = Alignment.Bottom,
                    modifier = Modifier.height(chartHeight),
                ) {
                    Box(
                        modifier = Modifier
                            .width(12.dp)
                            .height((chartHeight * fixed / maxValue).coerceAtLeast(2.dp))
                            .background(MaterialTheme.colorScheme.primary),
                    )
                    Box(
                        modifier = Modifier
                            .width(12.dp)
                            .height((chartHeight * optional / maxValue).coerceAtLeast(2.dp))
                            .background(MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f)),
                    )
                }
                Text(
                    text = label,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = 6.dp),
                )
            }
        }
    }
}
