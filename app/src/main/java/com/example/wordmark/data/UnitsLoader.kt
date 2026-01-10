package com.example.wordmark.data

import android.content.Context

object UnitsLoader {
    fun load(context: Context): List<String> {
        return try {
            context.assets.open("units.properties").bufferedReader(Charsets.UTF_8).useLines { lines ->
                lines.mapNotNull { line ->
                    val trimmed = line.trim()
                    if (trimmed.isEmpty() || trimmed.startsWith("#") || trimmed.startsWith("!")) {
                        return@mapNotNull null
                    }
                    val separatorIndex = trimmed.indexOfFirst { it == '=' || it == ':' }
                    val rawValue = if (separatorIndex >= 0) {
                        trimmed.substring(separatorIndex + 1)
                    } else {
                        trimmed
                    }
                    rawValue.trim().trimStart('\uFEFF').ifBlank { null }
                }.toList()
            }
        } catch (_: Exception) {
            emptyList()
        }
    }
}
