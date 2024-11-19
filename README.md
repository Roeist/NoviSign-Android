# Project Name: NoviSign

## Description:

Novisign is a digital signage platform that allows users to create and manage playlists of media files. 
This Android application retrieves playlists from a remote API, downloads media files, and then plays them back using ExoPlayer. 
It supports handling both video and image files and manages network failures and caching for offline use.

## Features

Playlists Management: Retrieve and manage playlists from a remote server.
ExoPlayer Integration: Play media files (video, images) using ExoPlayer.
Network Resilience: Automatically handles cases when the network is unavailable, retrying when the network is restored.

## Tech Stack

Programming Language: Kotlin
Architecture: MVVM (Model-View-ViewModel) with Clean Architecture
Networking: Retrofit for API calls
Dependency Injection: Hilt
Media Player: ExoPlayer
Caching: File-based caching for media
Coroutines: For asynchronous tasks

## Requirements

Android SDK: 5.0 (Lollipop) or higher
Internet connection (for downloading playlists and media files)
Hilt for Dependency Injection
ExoPlayer for media playback

## Installation

Follow these steps to get this project up and running:

1. Clone the repository:

  git clone https://github.com/Roeist/NoviSign-Android

2. Open in Android Studio:

    Open Android Studio and choose Open an existing project.
    Select the cloned project directory.

3. Sync Gradle:
  
  Ensure that Gradle is synced and dependencies are resolved.

4. Run the Project:

  Connect a physical device or use the Android Emulator.
  Press Run in Android Studio to build and run the app.

## Usage

# Fetching Playlists
The app fetches playlists from a remote API using Retrofit. Once retrieved, the media files associated with the playlist are downloaded and saved locally for offline playback.

# Playing Media
The app uses ExoPlayer to play the media files. It supports both video and image files, which are played sequentially as part of a playlist.

# Handling Network Failures
The app detects network unavailability and displays an error message when there is no internet connection. When the network is restored, it automatically retries fetching playlists and media.

## API Endpoints

# Get Playlists:
  Endpoint: GET /PlayerBackend/screen/playlistItems/{screenKey}
  Description: Retrieves a list of playlists for the given screen key.

# Get Media Files:
  Endpoint: GET /PlayerBackend/creative/get/{fileKey}
  Description: Retrieves a media file (image or video) by its file key.


## Project Structure

app/
  ├── data/                # Repository classes and data models
  ├── domain/              # Contains business logic and domain models
  ├── reposotery/          # Data layer, handles the API calls and data fetching
  ├── ui/                  # UI components (activities, fragments)
  ├── utils/               # Helper classes (network utilities, etc.)
  ├── gradle/              # Gradle build files and configurations


## Unit Testing

The project includes unit tests to ensure the functionality and reliability of the app. Below are some of the tests implemented:

1. PlaylistResponseTest:
This test focuses on verifying the behavior of the MediaRepository when the ApiService fails to return the playlist due to network issues (simulated using RuntimeException).

2. MediaRepositoryTest:
This test checks the behavior of the MediaRepository when the ApiService fails to fetch playlists or media files.

## Contributing

We welcome contributions to this project! If you would like to contribute, please follow these steps:

1. Fork the repository on GitHub.
2. Create a new branch (git checkout -b feature-name).
3. Make your changes and commit them (git commit -am 'Add feature').
4. Push your branch to your fork (git push origin feature-name).
5. Open a pull request to the main repository.
6. Please ensure that your code follows the project's coding style and includes relevant unit tests.

## License

This project is licensed under the MIT License - see the LICENSE file for details.

## Acknowledgements
  
  ExoPlayer for media playback.
  Retrofit for networking.
  Hilt for dependency injection.
