# Prompt for Antigravity Agent - LinkUp Project

## 🎯 YOUR MISSION

You are working on **LinkUp** - a local Wi-Fi based communication app. Your role is to implement **Network & Background Services** features.

---

## 📋 STEP 1: ANALYZE THE PROJECT

### First, read these files to understand the project:

1. **Project Status:**
   - `logs/PROJECT_STATUS.md` - Overall project status
   - `logs/AGENT_DIVISION.md` - Work division between agents
   - `logs/AGENT1_COMPLETION_REPORT.md` - What's already done

2. **Integration Details:**
   - `logs/INTEGRATION_LOG.md` - How socket-database integration works

3. **Key Code Files:**
   - `app/src/main/java/com/example/p/utils/MessageHandler.java` - JSON message format
   - `app/src/main/java/com/example/p/SocketServer.java` - Current socket server
   - `app/src/main/java/com/example/p/SocketClient.java` - Current socket client
   - `app/src/main/java/com/example/p/database/` - Database structure
   - `app/src/main/java/com/example/p/repository/` - Repositories

---

## 🎯 YOUR ASSIGNED TASKS

### Task 1: Enhanced Socket Communication (Priority: 🔥 HIGH)

**What to do:**
1. **Create JSON Message Protocol**
   - File: `app/src/main/java/com/example/p/network/MessageProtocol.java`
   - Standardize message format (already started in MessageHandler)
   - Add message acknowledgment system
   - Handle different message types (TEXT, VOICE, IMAGE, PDF)

2. **Improve SocketServer.java**
   - Handle multiple client connections (currently only one)
   - Support group message broadcasting
   - Better error handling
   - Connection retry logic

3. **Improve SocketClient.java**
   - Better error handling
   - Connection retry logic
   - Handle disconnections gracefully

4. **Create ConnectionManager**
   - File: `app/src/main/java/com/example/p/network/ConnectionManager.java` (already exists in utils, enhance it)
   - Manage multiple connections
   - Track connection status
   - Handle reconnections

**Expected Output:**
- Messages sent as JSON with proper structure
- Message acknowledgment system working
- Multiple clients can connect to server
- Group messages can be broadcasted

---

### Task 2: File Transfer System (Priority: 🔥 HIGH)

**What to do:**
1. **Create File Transfer Manager**
   - File: `app/src/main/java/com/example/p/network/FileTransferManager.java`
   - Handle file upload via TCP socket
   - Handle file download via socket
   - File chunking for large files (>1MB)
   - Progress tracking

2. **Create File Storage Manager**
   - File: `app/src/main/java/com/example/p/utils/FileStorageManager.java`
   - Save files to app directory (`getFilesDir()`)
   - Manage file paths
   - File validation (size, type)
   - Clean up old files

3. **Update MessageHandler**
   - Add file message handling
   - Save file path to database
   - Update message status for file transfers

**Expected Output:**
- PDF files can be sent/received
- Images can be sent/received
- File progress tracked
- Files saved to app storage

---

### Task 3: Voice Messages - Recording & Playback (Priority: ⚠️ MEDIUM)

**What to do:**
1. **Create Audio Recorder**
   - File: `app/src/main/java/com/example/p/audio/AudioRecorder.java`
   - Use MediaRecorder API
   - Record in 3GP or MP4 format
   - Save audio files
   - Handle recording errors

2. **Create Audio Player**
   - File: `app/src/main/java/com/example/p/audio/AudioPlayer.java`
   - Use MediaPlayer API
   - Play audio files
   - Pause/resume functionality
   - Handle playback errors

3. **Create Audio File Manager**
   - File: `app/src/main/java/com/example/p/audio/AudioFileManager.java`
   - Manage audio file storage
   - File size limits
   - Clean up old recordings

**Expected Output:**
- Voice messages can be recorded
- Voice messages can be played
- Audio files saved and managed

---

### Task 4: Background Service & Message Queue (Priority: 🔥 HIGH)

**What to do:**
1. **Create Foreground Service**
   - File: `app/src/main/java/com/example/p/service/LinkUpService.java`
   - Foreground service for Android 8+
   - Keep socket connections alive
   - Listen for incoming messages
   - Handle app background state

2. **Create Message Queue Manager**
   - File: `app/src/main/java/com/example/p/service/MessageQueueManager.java`
   - Store PENDING messages
   - Retry logic with exponential backoff
   - Update message status (PENDING → SENT → DELIVERED)
   - Handle offline queue

3. **Create Notification Helper**
   - File: `app/src/main/java/com/example/p/utils/NotificationHelper.java`
   - Show notifications for new messages
   - Notification channels (Android 8+)
   - Click to open chat

4. **Update AndroidManifest.xml**
   - Register service
   - Add required permissions

**Expected Output:**
- Service runs in background
- Messages queued when offline
- Automatic retry when online
- Notifications for new messages

---

### Task 5: Group Message Broadcasting (Priority: 🔥 HIGH)

**What to do:**
1. **Create Group Message Broadcaster**
   - File: `app/src/main/java/com/example/p/network/GroupMessageBroadcaster.java`
   - Get all group members from database
   - Send message to each member individually
   - Handle offline members (queue messages)
   - Track delivery status per member

2. **Update ChatViewModel**
   - Add group message sending logic
   - Use GroupMessageBroadcaster for group chats

**Expected Output:**
- Group messages sent to all members
- Offline members get messages when online
- Delivery status tracked

---

## 📁 FILE STRUCTURE TO CREATE

```
app/src/main/java/com/example/p/
├── network/
│   ├── MessageProtocol.java (enhance existing)
│   ├── SocketMessageHandler.java
│   ├── ConnectionManager.java (enhance existing)
│   ├── FileTransferManager.java
│   ├── FileSender.java
│   ├── FileReceiver.java
│   └── GroupMessageBroadcaster.java
├── audio/
│   ├── AudioRecorder.java
│   ├── AudioPlayer.java
│   └── AudioFileManager.java
├── service/
│   ├── LinkUpService.java
│   ├── MessageQueueManager.java
│   └── MessageRetryHandler.java
└── utils/
    ├── FileStorageManager.java
    └── NotificationHelper.java
```

---

## 🔗 INTEGRATION POINTS

### Use Existing Code:

1. **Database:**
   - `MessageRepository` - For saving messages
   - `UserRepository` - For user info
   - `GroupRepository` - For group members

2. **Message Format:**
   - Use `MessageHandler.createMessageJson()` for text messages
   - Extend for file/voice messages

3. **Socket Classes:**
   - Enhance `SocketServer.java` and `SocketClient.java`
   - Don't break existing functionality

4. **ViewModels:**
   - `ChatViewModel` - For sending messages
   - `GroupViewModel` - For group operations

---

## 📝 IMPLEMENTATION GUIDELINES

### 1. Follow MVVM Pattern:
- Business logic in Repository/Service
- ViewModels use Repositories
- Activities use ViewModels

### 2. Error Handling:
- Always handle network errors
- Log errors properly
- Show user-friendly messages

### 3. Threading:
- Network operations on background threads
- Database operations on background threads
- UI updates on main thread

### 4. File Storage:
- Use `context.getFilesDir()` for app files
- Create subdirectories: `files/`, `audio/`, `images/`
- Manage file paths in database

### 5. Permissions:
- Add to AndroidManifest.xml:
  - `READ_EXTERNAL_STORAGE`
  - `WRITE_EXTERNAL_STORAGE`
  - `RECORD_AUDIO`
  - `FOREGROUND_SERVICE`
  - `POST_NOTIFICATIONS` (Android 13+)

---

## 🎯 PRIORITY ORDER

**Do in this order:**

1. **First:** Enhanced Socket Communication
   - Critical for all other features
   - Enables group messaging

2. **Second:** Background Service & Message Queue
   - Essential for reliability
   - Enables offline support

3. **Third:** Group Message Broadcasting
   - Uses enhanced sockets
   - Core feature

4. **Fourth:** File Transfer System
   - Can work independently
   - Uses enhanced sockets

5. **Fifth:** Voice Messages
   - Can work independently
   - Uses file transfer for sending

---

## 📋 CHECKLIST

Before starting, make sure you understand:
- [ ] Project structure
- [ ] Database schema (read database entities)
- [ ] MessageHandler JSON format
- [ ] Current socket implementation
- [ ] Repository pattern usage

While implementing:
- [ ] Test each feature independently
- [ ] Handle errors gracefully
- [ ] Log important events
- [ ] Update status in database
- [ ] Don't break existing code

After completing:
- [ ] Test with multiple devices
- [ ] Test offline scenarios
- [ ] Test file transfers
- [ ] Test voice messages
- [ ] Verify background service

---

## 🚨 IMPORTANT NOTES

1. **Don't Modify:**
   - Database entities (unless adding fields)
   - Existing ViewModels (unless extending)
   - UI layouts (that's Agent 2's job)

2. **Do Modify:**
   - SocketServer/SocketClient (enhance them)
   - MessageHandler (extend for files/voice)
   - Add new services and utilities

3. **Communication:**
   - Use JSON format for all messages
   - Follow existing MessageHandler pattern
   - Maintain backward compatibility

4. **Testing:**
   - Test on real devices (Wi-Fi)
   - Test with multiple clients
   - Test offline/online scenarios

---

## 📚 REFERENCE

### JSON Message Format (from MessageHandler):
```json
{
  "type": "MESSAGE",
  "messageId": "senderId_timestamp",
  "chatId": "user_or_group_id",
  "senderId": "sender_user_id",
  "senderName": "Sender Name",
  "content": "Message text",
  "messageType": "TEXT/VOICE/IMAGE/PDF",
  "filePath": "path_if_file",
  "timestamp": 1234567890,
  "isGroupMessage": false
}
```

### File Message Format (to add):
```json
{
  "type": "FILE",
  "messageId": "unique_id",
  "chatId": "chat_id",
  "senderId": "sender_id",
  "fileName": "file.pdf",
  "fileSize": 1024000,
  "fileType": "PDF",
  "chunkIndex": 0,
  "totalChunks": 5,
  "chunkData": "base64_encoded_data"
}
```

---

## ✅ START HERE

1. **Read the logs:**
   ```
   logs/PROJECT_STATUS.md
   logs/AGENT_DIVISION.md
   logs/INTEGRATION_LOG.md
   ```

2. **Analyze existing code:**
   - MessageHandler.java
   - SocketServer.java
   - SocketClient.java
   - Database entities

3. **Start with Task 1:** Enhanced Socket Communication

4. **Work step by step** - Don't try to do everything at once

---

**Good Luck! 🚀**

**Remember:** Your work enables the app to actually communicate. Make it robust and reliable!
