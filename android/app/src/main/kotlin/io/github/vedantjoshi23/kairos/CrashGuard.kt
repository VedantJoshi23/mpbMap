package io.github.vedantjoshi23.kairos

import android.content.Context
import android.os.Build
import java.io.File
import java.io.PrintWriter
import java.io.StringWriter

/**
 * Records an uncaught crash to app-private CE storage so the next launch can show it
 * (no silent crashes). The report holds the stack trace and build info only: no
 * coordinates, no user data (Law 8; STD-CODE rule 5 keeps messages coordinate-free).
 * Interim until ACRA-to-backend ships (ADR-0009, slice 3).
 */
object CrashGuard {
    private const val FILE = "last_crash.txt"

    fun install(context: Context) {
        val app = context.applicationContext
        val previous = Thread.getDefaultUncaughtExceptionHandler()
        Thread.setDefaultUncaughtExceptionHandler { thread, error ->
            runCatching { write(app, thread.name, error) }
            previous?.uncaughtException(thread, error)
        }
    }

    fun record(context: Context, where: String, error: Throwable) {
        runCatching { write(context.applicationContext, where, error) }
    }

    /** Before the first unlock CE storage is unavailable; then nothing is written. */
    private fun write(context: Context, where: String, error: Throwable) {
        val trace = StringWriter().also { error.printStackTrace(PrintWriter(it)) }.toString()
        val version = runCatching { context.packageManager.getPackageInfo(context.packageName, 0).versionName }.getOrNull()
        val text = buildString {
            appendLine("Kairos $version, Android ${Build.VERSION.RELEASE} (API ${Build.VERSION.SDK_INT}), ${Build.MANUFACTURER} ${Build.MODEL}")
            appendLine("Where: $where")
            appendLine(trace.take(12_000))
        }
        File(context.noBackupFilesDir, FILE).writeText(text)
    }

    fun lastCrash(context: Context): String? =
        runCatching { File(context.noBackupFilesDir, FILE).takeIf { it.exists() }?.readText() }.getOrNull()

    fun clear(context: Context) {
        runCatching { File(context.noBackupFilesDir, FILE).delete() }
    }
}
