# LinkUp Project - Complete Documentation

## 📑 Table of Contents

1. [Project Overview](#project-overview)
2. [System Architecture](#system-architecture)
3. [Database Design](#database-design)
4. [Network Protocol](#network-protocol)
5. [Features Implementation](#features-implementation)
6. [Code Structure](#code-structure)
7. [Build & Deployment](#build--deployment)
8. [API Reference](#api-reference)
9. [Troubleshooting](#troubleshooting)

---

## 📱 Project Overview

### Purpose
LinkUp is a local Wi-Fi based messaging application that enables communication between Android devices without requiring internet connectivity. It uses TCP sockets for peer-to-peer communication over local network.

### Key Objectives
- Enable local Wi-Fi messaging
- QR code based user identification
- Group chat functionality
- File and voice message sharing
- Offline message queue
- Background service for continuous listening

### Technology Stack
- **Language**: Java 11
- **UI**: XML Layouts
- **Database**: Room (SQLite)
- **Architecture**: MVVM
- **Networking**: TCP Sockets
- **QR Code**: ZXing Library
- **Audio**: MediaRecorder/MediaPlayer

---

## 🏗️ System Architecture

### MVVM Architecture Pattern

```
┌─────────────────┐
│   View Layer    │  Activities (UI)
│  (Activities)    │
└────────┬────────┘
         │ Observes
         ↓
┌─────────────────┐
│  ViewModel      │  LiveData, Business Logic
│  (ViewModels)   │
└────────┬────────┘
         │ Uses
         ↓
┌─────────────────┐
│  Repository     │  Data Abstraction
│  (Repositories) │
└────────┬────────┘
         │ Accesses
         ↓
┌─────────────────┐
│  Data Sources   │  Database / Network
│  (Room/Sockets) │
└─────────────────┘
```

### Component Layers

#### 1. Presentation Layer
- **Activities**: UI screens
- **Adapters**: RecyclerView adapters
- **Layouts**: XML UI definitions

#### 2. Business Logic Layer
- **ViewModels**: UI logic and LiveData
- **Repositories**: Data access abstraction

#### 3. Data Layer
- **Database**: Room entities and DAOs
- **Network**: Socket clients and servers
- **File System**: File storage management

#### 4. Service Layer
- **Foreground Service**: Background message processing
- **Message Queue**: Offline message handling

---

## 🗄️ Database Design

### Entity Relationship Diagram

```
┌──────────┐         ┌──────────┐
│   User   │         │  Group   │
│          │         │          │
│ userId   │         │ groupId  │
│ name     │         │ groupName│
│ mobile   │         │ adminId  │
│ deviceId │         └────┬─────┘
│ ipAddress│              │
└────┬─────┘              │
     │                    │
     │                    │
     ↓                    ↓
┌─────────────────────────────┐
│      GroupMember            │
│                             │
│  groupId (FK)              │
│  userId (FK)               │
│  isAdmin                   │
└─────────────────────────────┘

┌──────────┐
│ Message  │
│          │
│ messageId│
│ chatId   │
│ senderId │
│ content  │
│ type     │
│ status   │
└──────────┘
```

### Database Tables

#### 1. User Table
```sql
CREATE TABLE users (
    userId TEXT PRIMARY KEY,
    name TEXT NOT NULL,
    mobileNumber TEXT,
    deviceId TEXT UNIQUE,
    ipAddress TEXT,
    lastSeen INTEGER,
    isOnline INTEGER DEFAULT 0
);
```

**Indexes:**
- `deviceId` (UNIQUE)
- `ipAddress`

#### 2. Message Table
```sql
CREATE TABLE messages (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    messageId TEXT UNIQUE NOT NULL,
    chatId TEXT NOT NULL,
    senderId TEXT NOT NULL,
    senderName TEXT,
    content TEXT,
    messageType TEXT DEFAULT 'TEXT',
    filePath TEXT,
    timestamp INTEGER NOT NULL,
    status TEXT DEFAULT 'PENDING',
    isGroupMessage INTEGER DEFAULT 0,
    createdAt INTEGER DEFAULT (strftime('%s', 'now'))
);
```

**Indexes:**
- `chatId`
- `senderId`
- `messageId` (UNIQUE)
- `timestamp`

**Message Types:**
- `TEXT` - Plain text message
- `VOICE` - Audio message
- `IMAGE` - Image file
- `PDF` - PDF document

**Status Values:**
- `PENDING` - Not yet sent
- `SENT` - Sent but not delivered
- `DELIVERED` - Delivered to recipient
- `FAILED` - Failed to send

#### 3. Group Table
```sql
CREATE TABLE groups (
    groupId TEXT PRIMARY KEY,
    groupName TEXT NOT NULL,
    description TEXT,
    adminId TEXT NOT NULL,
    adminOnlyMessages INTEGER DEFAULT 0,
    createdAt INTEGER DEFAULT (strftime('%s', 'now')),
    groupImagePath TEXT
);
```

**Foreign Keys:**
- `adminId` → `users.userId`

#### 4. GroupMember Table
```sql
CREATE TABLE group_members (
    groupId TEXT NOT NULL,
    userId TEXT NOT NULL,
    isAdmin INTEGER DEFAULT 0,
    joinedAt INTEGER DEFAULT (strftime('%s', 'now')),
    PRIMARY KEY (groupId, userId),
    FOREIGN KEY (groupId) REFERENCES groups(groupId),
    FOREIGN KEY (userId) REFERENCES users(userId)
);
```

**Indexes:**
- `groupId`
- `userId`
- Composite Primary Key: `(groupId, userId)`

---

## 🔌 Network Protocol

### TCP Socket Communication

#### Port Configuration
- **Default Port**: 8888
- **Protocol**: TCP/IP
- **Network**: Local Wi-Fi

#### Connection Flow

```
Device A (Server)                    Device B (Client)
     │                                    │
     │  Start Server (Port 8888)         │
     │◄──────────────────────────────────│
     │                                    │
     │  Generate QR Code                 │
     │  (Contains IP + User Info)        │
     │                                    │
     │                                    │  Scan QR Code
     │                                    │  Extract IP
     │                                    │
     │                                    │  Connect to IP:8888
     │◄──────────────────────────────────│
     │                                    │
     │  Accept Connection                │
     │  Create ClientConnection          │
     │                                    │
     │  Exchange User Info               │
     │◄──────────────────────────────────│
     │──────────────────────────────────►│
     │                                    │
     │  Ready for Messages               │
     │                                    │
```

### Message Protocol (JSON)

#### Message Format
```json
{
  "type": "MESSAGE",
  "messageId": "unique-message-id",
  "chatId": "chat-identifier",
  "senderId": "user-id",
  "senderName": "Display Name",
  "content": "Message content or base64 encoded data",
  "messageType": "TEXT|VOICE|IMAGE|PDF",
  "timestamp": 1234567890,
  "isGroupMessage": false
}
```

#### User Info Exchange
```json
{
  "type": "USER_INFO",
  "userId": "user-id",
  "name": "User Name",
  "mobileNumber": "1234567890",
  "deviceId": "device-identifier",
  "ipAddress": "192.168.1.100"
}
```

#### Acknowledgment
```json
{
  "type": "ACK",
  "messageId": "message-id-to-acknowledge",
  "status": "DELIVERED|FAILED"
}
```

### File Transfer Protocol

#### File Upload Flow
1. **Initiate**: Send file metadata (name, size, type)
2. **Chunk Transfer**: Send file in chunks (e.g., 8KB each)
3. **Verification**: Send checksum for verification
4. **Completion**: Receive acknowledgment

#### File Message Format
```json
{
  "type": "FILE_TRANSFER",
  "messageId": "unique-id",
  "fileName": "example.pdf",
  "fileSize": 1024000,
  "fileType": "PDF",
  "chunks": 128,
  "chunkSize": 8192
}
```

---

## ✨ Features Implementation

### 1. User Identity System

#### Components
- `UserSetupActivity` - Initial registration screen
- `UserPreferences` - SharedPreferences wrapper
- `DeviceUtils` - Unique device ID generation
- QR Code generation for user identity

#### Flow
1. User enters name and mobile number
2. Generate unique device ID
3. Create user profile in database
4. Generate QR code with user info
5. Store preferences locally

### 2. QR Code Connection

#### Components
- `QrConnectActivity` - QR scanning screen
- `CaptureActivityPortrait` - Camera capture
- ZXing library integration

#### Flow
1. User taps "Add Chat"
2. Open QR scanner
3. Scan other user's QR code
4. Extract user information
5. Connect via TCP socket
6. Exchange user info
7. Navigate to chat screen

### 3. Messaging System

#### Components
- `ChatActivity` - Chat screen
- `MessageAdapter` - Message list adapter
- `MessageHandler` - Message parsing
- `MessageRepository` - Message data access

#### Features
- Real-time message display
- Message status tracking
- Message alignment (sent/received)
- Timestamp display
- Message type handling

### 4. Group Chat System

#### Components
- `CreateGroupActivity` - Group creation
- `GroupInfoActivity` - Group management
- `AddMemberActivity` - Add members
- `GroupMessageBroadcaster` - Broadcast messages

#### Features
- Create groups with name and description
- Add/remove members
- Admin permissions
- Group message broadcasting
- Member list display

### 5. File Sharing

#### Components
- `FileTransferManager` - File transfer handling
- `FileStorageManager` - File storage management
- File picker integration

#### Supported Types
- Images (JPG, PNG, GIF)
- PDF documents
- Other file types

#### Flow
1. User selects file
2. File converted to base64 or chunks
3. Sent via socket with metadata
4. Receiver saves file
5. File path stored in message

### 6. Voice Messages

#### Components
- `AudioRecorder` - Audio recording
- `AudioPlayer` - Audio playback
- `AudioFileManager` - Audio file management

#### Flow
1. User holds microphone button
2. Start recording
3. Release button to stop
4. Save audio file
5. Send as message with type "VOICE"
6. Receiver plays audio

### 7. Background Service

#### Components
- `LinkUpService` - Foreground service
- `MessageQueueManager` - Offline queue
- `EnhancedSocketServer` - Multi-client server

#### Features
- Continuous network listening
- Incoming message processing
- Offline message queue
- Retry mechanism
- Connection management

### 8. Offline Message Queue

#### Components
- `MessageQueueManager` - Queue management
- Status tracking (PENDING, SENT, DELIVERED, FAILED)
- Retry logic

#### Flow
1. Message created with status PENDING
2. Attempt to send
3. If successful → status SENT
4. If failed → status FAILED, retry later
5. On delivery → status DELIVERED

---

## 📁 Code Structure

### Package Organization

```
com.example.p
│
├── database/              # Room Database
│   ├── User.java
│   ├── Message.java
│   ├── Group.java
│   ├── GroupMember.java
│   ├── Converters.java
│   ├── UserDao.java
│   ├── MessageDao.java
│   ├── GroupDao.java
│   ├── GroupMemberDao.java
│   └── LinkUpDatabase.java
│
├── repository/            # Data Repositories
│   ├── UserRepository.java
│   ├── MessageRepository.java
│   └── GroupRepository.java
│
├── viewmodel/             # ViewModels
│   ├── ChatViewModel.java
│   ├── ChatListViewModel.java
│   └── GroupViewModel.java
│
├── network/               # Network Layer
│   ├── EnhancedSocketServer.java
│   ├── ClientConnection.java
│   ├── FileTransferManager.java
│   ├── GroupMessageBroadcaster.java
│   └── MessageProtocol.java
│
├── service/               # Background Services
│   ├── LinkUpService.java
│   └── MessageQueueManager.java
│
├── utils/                 # Utilities
│   ├── MessageHandler.java
│   ├── ConnectionManager.java
│   ├── FileStorageManager.java
│   ├── UserPreferences.java
│   └── DeviceUtils.java
│
├── audio/                 # Audio Handling
│   ├── AudioRecorder.java
│   ├── AudioPlayer.java
│   └── AudioFileManager.java
│
└── [Activities]          # UI Screens
    ├── UserSetupActivity.java
    ├── ChatListActivity.java
    ├── ChatActivity.java
    ├── QrConnectActivity.java
    ├── CreateGroupActivity.java
    ├── GroupInfoActivity.java
    └── AddMemberActivity.java
```

### Key Classes

#### Database Layer
- **LinkUpDatabase**: Main database class, Room database instance
- **User**: User entity with device info
- **Message**: Message entity with types and status
- **Group**: Group entity with admin info
- **GroupMember**: Group membership entity

#### Repository Layer
- **UserRepository**: User data operations
- **MessageRepository**: Message CRUD operations
- **GroupRepository**: Group management operations

#### ViewModel Layer
- **ChatViewModel**: Chat screen logic
- **ChatListViewModel**: Chat list logic
- **GroupViewModel**: Group operations logic

#### Network Layer
- **EnhancedSocketServer**: Multi-client TCP server
- **ClientConnection**: Single client connection handler
- **FileTransferManager**: File transfer implementation
- **GroupMessageBroadcaster**: Group message broadcasting

#### Service Layer
- **LinkUpService**: Foreground service for background operations
- **MessageQueueManager**: Offline message queue management

---

## 🔨 Build & Deployment

### Build Configuration

#### Gradle Files
- `build.gradle.kts` - Project-level build config
- `app/build.gradle.kts` - App-level build config
- `gradle/libs.versions.toml` - Dependency versions
- `settings.gradle.kts` - Project settings

#### Key Configurations
```kotlin
compileSdk = 36
minSdk = 24
targetSdk = 35
javaVersion = 11
```

### Build Commands

#### Debug Build
```bash
./gradlew assembleDebug
```

#### Release Build
```bash
./gradlew assembleRelease
```

#### Clean Build
```bash
./gradlew clean assembleDebug
```

### APK Locations
- **Debug**: `app/build/outputs/apk/debug/app-debug.apk`
- **Release**: `app/build/outputs/apk/release/app-release.apk`

### GitHub Actions

#### Workflow File
`.github/workflows/build-apk.yml`

#### Features
- Automatic build on push
- Manual trigger support
- APK artifact upload
- 30-day retention

---

## 📖 API Reference

### Repository APIs

#### UserRepository
```java
// Get all users
LiveData<List<User>> getAllUsers();

// Get user by ID
LiveData<User> getUserById(String userId);

// Insert user
void insertUser(User user);

// Update user
void updateUser(User user);
```

#### MessageRepository
```java
// Get messages for chat
LiveData<List<Message>> getMessagesForChat(String chatId);

// Insert message
void insertMessage(Message message);

// Update message status
void updateMessageStatus(String messageId, String status);
```

#### GroupRepository
```java
// Get all groups
LiveData<List<Group>> getAllGroups();

// Get group by ID
LiveData<Group> getGroupById(String groupId);

// Create group
void createGroup(Group group);
```

### ViewModel APIs

#### ChatViewModel
```java
// Get messages
LiveData<List<Message>> getMessages();

// Send message
void sendMessage(String content, String messageType);

// Load chat
void loadChat(String chatId);
```

### Network APIs

#### MessageHandler
```java
// Create message JSON
static String createMessageJson(...);

// Parse message
static Message parseMessage(String json);

// Handle incoming message
static void handleIncomingMessage(String json, Context context);
```

#### FileTransferManager
```java
// Send file
void sendFile(String filePath, String recipientIp);

// Receive file
void receiveFile(InputStream inputStream, String fileName);
```

---

## 🐛 Troubleshooting

### Common Issues

#### 1. Build Failures
**Problem**: Gradle sync fails  
**Solution**:
- Check internet connection
- Invalidate caches: File → Invalidate Caches / Restart
- Clean build: `./gradlew clean`

**Problem**: Room compilation error  
**Solution**:
- Verify kapt plugin is applied
- Check Room version in `libs.versions.toml`
- Clean and rebuild

#### 2. Runtime Issues
**Problem**: Cannot connect to other devices  
**Solution**:
- Ensure same Wi-Fi network
- Check firewall settings
- Verify IP address
- Check permissions (INTERNET, ACCESS_WIFI_STATE)

**Problem**: Messages not sending  
**Solution**:
- Check background service is running
- Verify network permissions
- Check message queue status
- Review socket connection logs

**Problem**: QR code not scanning  
**Solution**:
- Grant camera permission
- Check camera hardware availability
- Verify ZXing library is included

#### 3. Database Issues
**Problem**: Database migration errors  
**Solution**:
- Increment database version
- Add migration strategy
- Clear app data if needed

### Debug Tips

#### Enable Logging
```java
// In Application class
if (BuildConfig.DEBUG) {
    // Enable verbose logging
}
```

#### Check Network Status
```java
// Check Wi-Fi connectivity
WifiManager wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
boolean isWifiEnabled = wifiManager.isWifiEnabled();
```

#### Monitor Socket Connections
```java
// Log socket connections
Log.d("Socket", "Server started on port: " + port);
Log.d("Socket", "Client connected: " + clientIp);
```

---

## 📊 Performance Considerations

### Database Optimization
- Use indexes on frequently queried columns
- Limit query results with pagination
- Use background threads for database operations

### Network Optimization
- Implement message batching
- Use connection pooling
- Implement timeout handling

### Memory Management
- Release MediaPlayer resources
- Clear image caches
- Limit message history

---

## 🔒 Security Considerations

### Data Protection
- Store sensitive data in encrypted SharedPreferences
- Use HTTPS for any external communication
- Implement input validation

### Network Security
- Validate incoming messages
- Implement message authentication
- Rate limiting for connections

### Permissions
- Request permissions at runtime
- Explain permission usage
- Handle permission denials gracefully

---

## 📈 Future Enhancements

### Potential Features
- End-to-end encryption
- Message search functionality
- Media gallery view
- Custom themes
- Message reactions
- Read receipts
- Typing indicators

### Technical Improvements
- Migrate to Kotlin
- Implement dependency injection (Hilt)
- Add unit tests
- Add UI tests
- Performance optimization
- Code refactoring

---

## 📞 Support & Contact

### Resources
- **GitHub Repository**: https://github.com/Amaan-Masood-0086/APK
- **Documentation**: Check `logs/` directory
- **Issues**: Open issue on GitHub

---

**Last Updated**: Current Session  
**Version**: 1.0  
**Status**: Production Ready
