# CodeVault

CodeVault is a modern mobile text editor developed using **Kotlin** and **Jetpack Compose** for Android. The application provides powerful text editing capabilities along with advanced productivity features such as search and replace, syntax highlighting, version history, crash recovery, word wrapping, and read-only viewing.

---

## Project Information

**Project Name:** CodeVault

**Group Name:** CodeCrafters

**Module:** Mobile Application Design and Development

**Platform:** Android

**Development Environment:** Android Studio

---

## Team Members

### Member 1 - B.L.M Galwatta
- Home Screen
- Editor UI
- New File
- Open File
- Save
- Save As
- Search & Replace
- Kotlin Syntax Highlighting

### Member 2
- Recent Files
- Word Wrap
- Read-Only Mode
- File Management Enhancements
- User Experience Improvements

### Member 3
- Version History
- Historical Version Viewer
- Crash Recovery
- Data Persistence
- Testing and Integration

---

## Project Overview

CodeVault was developed to provide users with a lightweight and efficient mobile text editing environment. The application supports document creation, editing, organization, and version management while maintaining a simple and user-friendly interface.

The system enables users to safely manage text documents with features commonly found in modern code editors and note-taking applications.

---

## Features

### File Management
- Create New Files
- Open Existing Files
- Save Files
- Save As
- Recent Files Support

### Text Editing
- Rich Text Editing Interface
- Search Functionality
- Replace Functionality
- Replace All Functionality
- Undo / Redo Support

### Developer Features
- Kotlin Syntax Highlighting
- Word Wrap Toggle
- Read-Only Viewing Mode

### Version Control
- Create Version Snapshots
- Version History Viewer
- Historical Version Reconstruction
- Read-Only Historical Snapshots

### Reliability Features
- Crash Recovery
- Persistent Data Storage
- Automatic State Preservation

---

## Technologies Used

- Kotlin
- Android Studio
- Jetpack Compose
- Material Design 3
- SharedPreferences
- Android Storage Access Framework (SAF)

---

## Algorithms and Technical Implementations

### Incremental Delta Version Control

Instead of storing complete copies of files for every version, CodeVault stores only the differences between versions.

Benefits:

- Reduced storage consumption
- Faster version creation
- Efficient version management

### Version Reconstruction Algorithm

Historical versions are reconstructed by:

1. Loading the base version
2. Sequentially applying stored deltas
3. Rebuilding the requested snapshot

### Search and Replace Algorithm

The editor supports:

- Find
- Next Match
- Previous Match
- Replace
- Replace All

allowing efficient navigation and modification of large documents.

### Kotlin Syntax Highlighting

Implemented using Jetpack Compose Visual Transformation APIs to improve source code readability.

### Undo / Redo Mechanism

Uses state-based editing history to provide efficient document recovery and editing flexibility.

---

## User Interface

### Home Screen
Provides quick access to:
- New File
- Open File

### Editor Screen
Provides:
- Editing Area
- File Operations
- Search & Replace
- Version Control Features
- Word Wrap Control

### Version History Screen
Allows users to:
- View saved versions
- Inspect historical snapshots
- Open reconstructed document states

---

## Installation

### Requirements

- Android Studio
- Android SDK 26 or above
- Android Device or Emulator

### Steps

1. Clone the repository

```bash
git clone https://github.com/your-repository-url.git
```

2. Open the project in Android Studio

3. Sync Gradle files

4. Run the application on an Android device or emulator

---

## Testing

The application has been tested for:

- File Creation
- File Saving
- File Opening
- Search and Replace
- Kotlin Syntax Highlighting
- Version History Creation
- Historical Version Reconstruction
- Crash Recovery
- Read-Only Mode

---

## Future Improvements

Potential enhancements include:

- Cloud Synchronization
- Multi-language Syntax Highlighting
- Dark Mode Support
- Export to PDF
- Collaborative Editing
- Git Integration

---

## Repository

GitHub Repository:

[Insert Repository Link Here]

---

## Acknowledgements

This project was developed as part of the Mobile Application Design and Development module.

Special thanks to the module lecturers and supervisors for their guidance and support throughout the development process.

---

## Version

Current Release: **Version 1.0**
