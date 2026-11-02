#!/usr/bin/env bash
# Runs instrumented tests on the CI emulator and always captures logcat for diagnosis.
set -u
adb logcat -c || true
./gradlew connectedDebugAndroidTest --console=plain
status=$?
adb logcat -d > ../logcat.txt 2>/dev/null || true
echo "---- crash excerpts ----"
grep -E "FATAL EXCEPTION|AndroidRuntime|KairosTrip|KairosWatchdog|MapLibre|libc" -A 30 ../logcat.txt | head -300 || true
exit $status
