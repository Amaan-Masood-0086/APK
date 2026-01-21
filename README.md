# LinkUp - Local Wi-Fi Communication App

<div align="center">

![Android](https://img.shields.io/badge/Android-3DDC84?style=for-the-badge&logo=android&logoColor=white)
![Java](https://img.shields.io/badge/Java-ED8B00?style=for-the-badge&logo=java&logoColor=white)
![Room](https://img.shields.io/badge/Room-4285F4?style=for-the-badge&logo=google&logoColor=white)

**A local Wi-Fi based messaging application for Android that enables communication without internet**

[Features](#-features) • [Architecture](#-architecture) • [Setup](#-setup) • [Build](#-build) • [Documentation](#-documentation)

</div>

---

## 📱 About LinkUp

**LinkUp** is an Android application that enables local Wi-Fi communication between devices without requiring an internet connection. Users can send messages, share files, record voice messages, and create group chats - all over local Wi-Fi network using TCP sockets.

### Key Highlights
- ✅ **No Internet Required** - Works entirely on local Wi-Fi
- ✅ **QR Code Connection** - Easy device pairing via QR codes
- ✅ **Group Chats** - Create and manage group conversations
- ✅ **File Sharing** - Send images, PDFs, and other files
- ✅ **Voice Messages** - Record and send audio messages
- ✅ **Offline Queue** - Messages stored when recipient is offline
- ✅ **MVVM Architecture** - Clean, maintainable code structure

---

## ✨ Features

### Core Features
- **Local Wi-Fi Messaging**: Send text messages over local network
- **QR Code Identity**: Generate and scan QR codes for user identification
- **Group Chats**: Create groups, add members, manage admins
- **File Transfer**: Share images, PDFs, and other file types
- **Voice Messages**: Record and play audio messages
- **Message Queue**: Offline message handling with retry mechanism
- **Background Service**: Continuous network listening for incoming messages

### Technical Features
- **Room Database**: Local SQLite database for messages, users, and groups
- **MVVM Architecture**: Separation of concerns with ViewModels and Repositories
- **TCP Socket Communication**: Custom protocol for reliable message delivery
- **JSON Message Protocol**: Standardized message format with acknowledgments
- **Foreground Service**: Background message processing

---

## 🏗️ Architecture

### MVVM Pattern
```
View (Activities) 
    ↓
ViewModel (LiveData)
    ↓
Repository
    ↓
Database (Room) / Network (Sockets)
```

### Project Structure
```
app/src/main/java/com/example/p/
├── database/          # Room entities & DAOs
│   ├── User.java
│   ├── Message.java
│   ├── Group.java
│   ├── GroupMember.java
│   └── LinkUpDatabase.java
├── repository/        # Data repositories
│   ├── UserRepository.java
│   ├── MessageRepository.java
│   └── GroupRepository.java
├── viewmodel/        # ViewModels
│   ├── ChatViewModel.java
│   ├── ChatListViewModel.java
│   └── GroupViewModel.java
├── network/          # Socket communication
│   ├── EnhancedSocketServer.java
│   ├── ClientConnection.java
│   ├── FileTransferManager.java
│   └── GroupMessageBroadcaster.java
├── service/          # Background services
│   ├── LinkUpService.java
│   └── MessageQueueManager.java
├── utils/            # Utility classes
│   ├── MessageHandler.java
│   ├── ConnectionManager.java
│   └── FileStorageManager.java
├── audio/            # Audio handling
│   ├── AudioRecorder.java
│   ├── AudioPlayer.java
│   └── AudioFileManager.java
└── [Activities]      # UI screens
    ├── UserSetupActivity.java
    ├── ChatListActivity.java
    ├── ChatActivity.java
    ├── QrConnectActivity.java
    ├── CreateGroupActivity.java
    └── GroupInfoActivity.java
```

---

## 🛠️ Technology Stack

### Languages & Frameworks
- **Java 11** - Primary programming language
- **XML** - UI layouts
- **Kotlin DSL** - Build configuration (Gradle)

### Android Components
- **Min SDK**: 24 (Android 7.0)
- **Target SDK**: 35 (Android 15)
- **Compile SDK**: 36

### Libraries & Dependencies
- **Room Database** 2.6.1 - Local data persistence
- **Lifecycle Components** 2.8.6 - ViewModel & LiveData
- **ZXing** 3.5.1 - QR code generation & scanning
- **Material Design** 1.13.0 - UI components
- **AppCompat** 1.7.1 - Backward compatibility

### Networking
- **Java TCP Sockets** - Custom socket implementation
- **JSON** - Message protocol (org.json)

### Audio
- **MediaRecorder** - Voice message recording
- **MediaPlayer** - Audio playback

---

## 📋 Prerequisites

- **Android Studio** Hedgehog (2023.1.1) or later
- **JDK 11** or later
- **Android SDK** (API 24+)
- **Gradle** 8.13+ (included via wrapper)

---

## 🚀 Setup

### 1. Clone Repository
```bash
git clone https://github.com/Amaan-Masood-0086/APK.git
cd APK
```

### 2. Open in Android Studio
1. Open Android Studio
2. File → Open → Select project directory
3. Wait for Gradle sync to complete

### 3. Configure SDK (if needed)
If Android SDK is not auto-detected, create `local.properties`:
```properties
sdk.dir=C\:\\Users\\YOUR_USERNAME\\AppData\\Local\\Android\\Sdk
```

### 4. Sync Gradle
- Click "Sync Now" if prompted
- Wait for dependencies to download

---

## 🔨 Build

### Debug APK (Development)
```bash
# Command line
./gradlew assembleDebug

# Android Studio
Build → Build Bundle(s) / APK(s) → Build APK(s)
```

**Output:** `app/build/outputs/apk/debug/app-debug.apk`

### Release APK (Production)
```bash
# Command line
./gradlew assembleRelease

# Android Studio
Build → Generate Signed Bundle / APK
```

**Output:** `app/build/outputs/apk/release/app-release.apk`

### GitHub Actions (CI/CD)
The project includes GitHub Actions workflow for automated builds:
- **Workflow File**: `.github/workflows/build-apk.yml`
- **Trigger**: Push to main/master branch
- **Status**: Check [Actions](https://github.com/Amaan-Masood-0086/APK/actions)

---

## 📱 Usage

### First Time Setup
1. Launch the app
2. Enter your name and mobile number
3. Generate your QR code
4. Start connecting with other users

### Connecting Devices
1. Open **Chat List** screen
2. Tap **"+"** button to add new chat
3. Scan QR code of another user
4. Start messaging!

### Creating Groups
1. Go to **Chat List**
2. Tap **"Create Group"**
3. Enter group name and description
4. Add members via QR code
5. Start group chat!

### Sending Messages
- **Text**: Type and send
- **Voice**: Hold microphone button to record
- **Files**: Tap attachment icon to select file
- **Images**: Select from gallery

---

## 🗄️ Database Schema

### Tables

#### Users
- `userId` (Primary Key)
- `name`
- `mobileNumber`
- `deviceId`
- `ipAddress`
- `lastSeen`
- `isOnline`

#### Messages
- `id` (Primary Key)
- `messageId` (Unique)
- `chatId`
- `senderId`
- `senderName`
- `content`
- `messageType` (TEXT, VOICE, IMAGE, PDF)
- `filePath`
- `timestamp`
- `status` (PENDING, SENT, DELIVERED, FAILED)
- `isGroupMessage`

#### Groups
- `groupId` (Primary Key)
- `groupName`
- `description`
- `adminId`
- `adminOnlyMessages`
- `createdAt`
- `groupImagePath`

#### GroupMembers
- `groupId` (Foreign Key)
- `userId` (Foreign Key)
- `isAdmin`
- `joinedAt`

---

## 🔌 Network Protocol

### Message Format (JSON)
```json
{
  "type": "MESSAGE",
  "messageId": "unique-id",
  "chatId": "chat-id",
  "senderId": "user-id",
  "senderName": "User Name",
  "content": "Message content",
  "messageType": "TEXT|VOICE|IMAGE|PDF",
  "timestamp": 1234567890,
  "isGroupMessage": false
}
```

### Connection Flow
1. **Server Start**: Device starts TCP server on port 8888
2. **Client Connect**: Other device connects via IP address
3. **User Info Exchange**: Both devices exchange user information
4. **Message Exchange**: Messages sent/received via JSON protocol
5. **Acknowledgment**: Messages acknowledged for reliability

---

## 📝 Permissions

The app requires following permissions:
- `INTERNET` - Network communication
- `ACCESS_WIFI_STATE` - Check Wi-Fi connectivity
- `CHANGE_WIFI_STATE` - Manage Wi-Fi state
- `CAMERA` - QR code scanning
- `RECORD_AUDIO` - Voice messages
- `READ_EXTERNAL_STORAGE` - File access
- `WRITE_EXTERNAL_STORAGE` - File saving
- `FOREGROUND_SERVICE` - Background service
- `POST_NOTIFICATIONS` - Notifications

---

## 🧪 Testing

### Manual Testing Checklist
- [ ] User registration and QR code generation
- [ ] Device connection via QR code scan
- [ ] Text message sending/receiving
- [ ] Group creation and management
- [ ] File sharing (images, PDFs)
- [ ] Voice message recording/playback
- [ ] Offline message queue
- [ ] Background service functionality

### Unit Testing
```bash
./gradlew test
```

### Instrumented Testing
```bash
./gradlew connectedAndroidTest
```

---

## 🐛 Troubleshooting

### Build Issues
**Problem**: Gradle sync fails  
**Solution**: 
- Check internet connection
- File → Invalidate Caches / Restart
- Try: `./gradlew clean`

**Problem**: Room compilation error  
**Solution**:
- Ensure kapt plugin is applied
- Check `gradle/libs.versions.toml` has room version
- Clean and rebuild

### Runtime Issues
**Problem**: Cannot connect to other devices  
**Solution**:
- Ensure both devices on same Wi-Fi network
- Check firewall settings
- Verify IP address is correct

**Problem**: Messages not sending  
**Solution**:
- Check background service is running
- Verify network permissions
- Check message queue status

---

## 📚 Documentation

Detailed documentation available in `logs/` directory:
- `PROJECT_STATUS.md` - Implementation status
- `TECH_STACK.md` - Technology details
- `PROPOSAL_COMPARISON.md` - Proposal vs Implementation
- `BUILD_READY_CHECKLIST.md` - Build checklist
- `APK_BUILD_GUIDE.md` - APK build instructions
- `GITHUB_ACTIONS_SETUP.md` - CI/CD setup

---

## 🤝 Contributing

1. Fork the repository
2. Create feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to branch (`git push origin feature/AmazingFeature`)
5. Open Pull Request

---

## 📄 License

This project is part of an academic assignment (MAD LAB Project).

---

## 👥 Authors

- **Development Team** - LinkUp Project

---

## 🙏 Acknowledgments

- Android Development Community
- Room Database Documentation
- ZXing Library Contributors

---

## 📞 Support

For issues and questions:
- Open an issue on GitHub
- Check documentation in `logs/` directory

---

<div align="center">

**Made with ❤️ for Local Communication**

[⬆ Back to Top](#linkup---local-wi-fi-communication-app)

</div>
