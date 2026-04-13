package me.digitalby.emojipicker.internal

internal fun humanizeGroupId(raw: String): String = raw
    .split('_')
    .joinToString(separator = " & ") { token ->
        token.replaceFirstChar { it.titlecase() }
    }

internal fun resolveGroupLabel(
    rawGroupId: String,
    localized: Map<String, String>?,
): String = localized?.get(rawGroupId) ?: humanizeGroupId(rawGroupId)
