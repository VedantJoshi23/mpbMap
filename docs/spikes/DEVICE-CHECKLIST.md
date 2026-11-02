# Kairos slice 1: on-device spike checklist (S1, S3)

These spikes need a real phone (KC-169). Install the debug APK from the
latest green CI run: GitHub, then **Actions**, then **ci**, then the run, then the
`kairos-debug-apk` artifact. Record the results in the issue or message the
agent, and they become evidence (an EVD entry) and tune `SafetyTiming`.

Write down: phone make, model, Android version, and whether battery
optimisation is on for Kairos.

## S3: alarm reliability (ADR-0005). Most important

| # | Do this | Expect | Result |
| --- | --- | --- | --- |
| 1 | Long-press a place 3–5 km away, choose Walk, set 2 km and 0.5 km alarms, Start trip | Location disclosure first, then permission prompts, then the "Trip to the pinned place" notification | |
| 2 | Walk or drive until within 2 km | Alarm sound **loops** on the alarm stream, phone **keeps vibrating**, full-screen alarm screen shows | |
| 3 | Tap **Snooze 2 minutes** | Silence, then the same alarm rings again after about 2 minutes | |
| 4 | Tap **Dismiss** | The 2 km alarm stops; the 0.5 km alarm is still armed (the notification remains) | |
| 5 | Turn on Do Not Disturb (alarms allowed), lock the screen, reach 0.5 km | Rings and vibrates over the lock screen | |
| 6 | Start a new trip, then **restart the phone** and do not unlock | "Trip alarm interrupted" rings **before unlock** and keeps repeating | |
| 7 | Start a trip, then swipe Kairos away and wait up to 15 minutes | "Trip alarm interrupted" rings (watchdog). Note how many minutes it took | |
| 8 | Start a trip, then Settings → Apps → Kairos → **Force stop** | Expected known limit (TD6): no alert. Reopen Kairos and note what you see | |
| 9 | Set alarm volume to 0, then press Start trip | A warning, but the trip starts, and vibration plus the screen still fire | |
| 10 | Turn off notifications for Kairos, then press Start trip | Start is **blocked**, with "allow notifications" | |
| 11 | Grant **approximate** location only, set a 0.5 km radius | Start is **blocked** (precision) | |
| 12 | With TalkBack on: arm a trip, then dismiss the alarm from the lock screen | Every control is announced and usable | |

## S1: map smoothness (STD-PERFORMANCE)

| # | Do this | Expect | Result |
| --- | --- | --- | --- |
| 1 | Pan and zoom around a city for 30 s | Feels smooth; note any stutter | |
| 2 | Find a coastline or a border region | **No border lines at any level** (Law 12) | |
| 3 | Check the bottom corner of the map | Attribution "i" is visible | |
| 4 | Turn on airplane mode after browsing an area | Labels still render in cached areas (bundled glyphs) | |
| 5 | Cold start (force stop, then open) | Note the seconds until the map is usable | |
