package com.example.wordmark.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.wordmark.data.StudyRecord
import com.example.wordmark.data.StudyRecordRepository
import com.example.wordmark.data.UnitsLoader
import com.example.wordmark.data.WordMarkDatabase
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StudyRecordScreen(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val database = remember { WordMarkDatabase.getInstance(context) }
    val repository = remember { StudyRecordRepository(database.studyRecordDao()) }
    val records by repository.records.collectAsState(initial = emptyList())
    val unitNames = remember { UnitsLoader.load(context) }
    val formatter = remember { SimpleDateFormat("yyyy/MM/dd HH:mm", Locale.JAPAN) }
    val scope = rememberCoroutineScope()

    var selectedUnit by remember { mutableStateOf("") }
    var startText by remember { mutableStateOf("") }
    var endText by remember { mutableStateOf("") }
    var expanded by remember { mutableStateOf(false) }


    // 削除対象の記録（確認ダイアログ用）
    var recordToDelete by remember { mutableStateOf<StudyRecord?>(null) }

    val startNumber = startText.toIntOrNull()
    val endNumber = endText.toIntOrNull()
    val wordCount = if (startNumber != null && endNumber != null && endNumber >= startNumber) {
        endNumber - startNumber + 1
    } else {
        null
    }
    val isRangeInvalid = startNumber != null && endNumber != null && endNumber < startNumber
    val canSave = selectedUnit.isNotBlank() && wordCount != null

    Column(
        modifier = modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text("Study Records", style = MaterialTheme.typography.headlineSmall)

        if (unitNames.isEmpty()) {
            Text(
                "No unit list found. Create app/src/main/assets/units.properties.",
                style = MaterialTheme.typography.bodyMedium
            )
        } else {
            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = !expanded },
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedTextField(
                    value = selectedUnit,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Unit") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                    modifier = Modifier.fillMaxWidth().menuAnchor(),
                    colors = OutlinedTextFieldDefaults.colors()
                )
                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    unitNames.forEach { unit ->
                        DropdownMenuItem(
                            text = { Text(unit) },
                            onClick = {
                                selectedUnit = unit
                                expanded = false
                            }
                        )
                    }
                }
            }
        }

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            OutlinedTextField(
                value = startText,
                onValueChange = { startText = it },
                label = { Text("Start") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true,
                modifier = Modifier.weight(1f)
            )
            OutlinedTextField(
                value = endText,
                onValueChange = { endText = it },
                label = { Text("End") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true,
                modifier = Modifier.weight(1f)
            )
        }

        if (isRangeInvalid) {
            Text("End must be greater than or equal to Start.", color = MaterialTheme.colorScheme.error)
        } else {
            Text("Word count: ${wordCount ?: "-"}")
        }

        Button(
            onClick = {
                val now = System.currentTimeMillis()
                val start = startNumber ?: return@Button
                val end = endNumber ?: return@Button
                val count = wordCount ?: return@Button
                scope.launch {
                    repository.add(
                        StudyRecord(
                            timestamp = now,
                            unitName = selectedUnit,
                            startNumber = start,
                            endNumber = end,
                            wordCount = count
                        )
                    )
                    startText = ""
                    endText = ""
                }
            },
            enabled = canSave
        ) {
            Text("Save")
        }

        Spacer(modifier = Modifier.height(8.dp))
        Text("History", style = MaterialTheme.typography.titleMedium)

        if (records.isEmpty()) {
            Text("No records yet.", style = MaterialTheme.typography.bodyMedium)
        } else {
            LazyColumn(
                contentPadding = PaddingValues(vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(records, key = { it.id }) { record ->
                    val timestamp = formatter.format(Date(record.timestamp))
                    Text(
                        "$timestamp \"${record.unitName}\" ${record.startNumber} - ${record.endNumber}  Count ${record.wordCount}",
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                recordToDelete = record
                            }
                    )
                }
            }
        }
    }

    recordToDelete?.let { record ->
        val timestamp = formatter.format(Date(record.timestamp))
        AlertDialog(
            onDismissRequest = { recordToDelete = null },
            title = { Text("Delete record?") },
            text = {
                Text(
                    "$timestamp \"${record.unitName}\" ${record.startNumber} - ${record.endNumber}  Count ${record.wordCount}"
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        scope.launch {
                            repository.delete(record)
                        }
                        recordToDelete = null
                    }
                ) {
                    Text("Delete")
                }
            },
            dismissButton = {
                TextButton(onClick = { recordToDelete = null }) {
                    Text("Cancel")
                }
            }
        )
    }
}


