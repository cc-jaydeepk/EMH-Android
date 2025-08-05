# EMH-Android Setup Guide

This project is designed to run in **Android Studio Electric Eel**.

## Prerequisites

- **Android Studio Electric Eel** (other versions may not be compatible without changes)
- Properly installed Android SDK (see recommended version in `build.gradle`)
- Required Gradle version (see `gradle/wrapper/gradle-wrapper.properties`)

## Project Structure & Dependencies

- The [Folio Reader](https://github.com/FolioReader/FolioReader-Android) library is included as a **project module**, not as a Gradle dependency.
- All other necessary configurations are present in the Gradle files.

## Setup Instructions

1. **Extract the provided zip file** to your desired location.
2. Open Android Studio Electric Eel.
3. Select **"Open an existing project"** and navigate to the extracted project directory.
4. Allow Gradle to sync. Ensure your Android SDK and Gradle version match those specified in the project files.
5. Build and run the app on your emulator or device.

> ⚠️ This project is **not compatible with the latest versions of Android Studio** without further modifications.

## Notes

- No additional library setup is required for Folio Reader; it’s already included as a module.
- Ensure all SDK and Gradle versions match for a smooth setup.