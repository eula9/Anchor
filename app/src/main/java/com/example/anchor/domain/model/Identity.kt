package com.example.anchor.domain.model

/**
 * 今日身份领域模型。
 *
 * @property statement 身份语句文本，例如「我是一个行动的人」
 * @property date 身份生效日期（ISO 格式 yyyy-MM-dd）
 */
data class Identity(
    val statement: String,
    val date: String,
)
