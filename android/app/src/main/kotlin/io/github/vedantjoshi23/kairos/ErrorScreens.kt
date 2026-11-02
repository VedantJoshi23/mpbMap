package io.github.vedantjoshi23.kairos

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/** Shown on the launch after a crash, so it is never silent. */
@Composable
fun CrashReportScreen(report: String, onDismiss: () -> Unit) {
    val context = LocalContext.current
    Column(Modifier.fillMaxSize().padding(20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text("Kairos stopped unexpectedly last time", style = MaterialTheme.typography.titleLarge, modifier = Modifier.semantics { heading() })
        Text("The details below contain no location or personal data. Copy them and send them to the developer.")
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Button(onClick = { copy(context, report) }) { Text("Copy details") }
            OutlinedButton(onClick = onDismiss) { Text("Continue") }
        }
        Text(
            report,
            fontFamily = FontFamily.Monospace,
            fontSize = 11.sp,
            modifier = Modifier.verticalScroll(rememberScrollState()),
        )
    }
}

/** A recoverable failure in one part of the app: explain it and offer a retry. */
@Composable
fun ErrorCard(title: String, detail: String, onRetry: (() -> Unit)?) {
    Column(Modifier.fillMaxSize().padding(24.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text(title, style = MaterialTheme.typography.titleLarge, modifier = Modifier.semantics { heading() })
        Text(detail, style = MaterialTheme.typography.bodyMedium)
        if (onRetry != null) Button(onClick = onRetry) { Text("Try again") }
    }
}

private fun copy(context: Context, text: String) {
    context.getSystemService(ClipboardManager::class.java)?.setPrimaryClip(ClipData.newPlainText("Kairos crash report", text))
}
