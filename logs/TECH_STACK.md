# LinkUp Project - Technology Stack

## 📱 Programming Languages

### **Primary Language: Java ✅**
- **All app code is written in Java**
- All Activities, Services, Repositories, ViewModels are `.java` files
- Total: ~50 Java files
- Java Version: **Java 11**

### **Build System: Kotlin DSL**
- **Gradle build files use Kotlin DSL** (`.kts` files)
- `build.gradle.kts` - Kotlin DSL
- `settings.gradle.kts` - Kotlin DSL
- `gradle/libs.versions.toml` - Version catalog

**Note:** Kotlin plugin is installed but **only for kapt** (Room compiler), not for writing Kotlin code.

---

## 🏗️ Frameworks & Libraries

### **1. Android Framework**
- **Native Android Development**
- **Language:** Java
- **Min SDK:** 24 (Android 7.0)
- **Target SDK:** 35 (Android 15)
- **Compile SDK:** 36

### **2. Architecture Components**
- **MVVM Architecture Pattern**
- **Room Database** (SQLite wrapper)
- **ViewModel** (Lifecycle-aware)
- **LiveData** (Observable data)
- **Repository Pattern**

### **3. UI Framework**
- **XML Layouts** (not Jetpack Compose)
- **ConstraintLayout** (for layouts)
- **RecyclerView** (for lists)
- **Material Design Components**

### **4. Networking**
- **Java TCP Sockets** (native)
- **No Retrofit/OkHttp** (custom socket implementation)
- **JSON** (org.json library)

### **5. QR Code**
- **ZXing Library** (Java)
- `com.google.zxing:core:3.5.1`
- `com.journeyapps:zxing-android-embedded:4.3.0`

### **6. Database**
- **Room Database 2.6.1**
- **SQLite** (underlying database)
- **kapt** (Kotlin Annotation Processing Tool) for Room compiler

### **7. Audio**
- **MediaRecorder** (Android native)
- **MediaPlayer** (Android native)
- No external audio libraries

### **8. File Handling**
- **Java File I/O** (native)
- **Base64** (Java 8+ standard library)
- No external file libraries

---

## 📊 Technology Breakdown

| Component | Technology | Language |
|-----------|------------|----------|
| **App Code** | Java | Java 11 |
| **Build System** | Gradle Kotlin DSL | Kotlin |
| **Database** | Room (SQLite) | Java |
| **Architecture** | MVVM | Java |
| **UI** | XML Layouts | XML |
| **Networking** | TCP Sockets | Java |
| **QR Code** | ZXing | Java |
| **Audio** | MediaRecorder/Player | Java |
| **JSON** | org.json | Java |

---

## 🔧 Build Tools

### **Gradle**
- Version: Latest (via AGP 8.13.2)
- Build Language: **Kotlin DSL** (`.kts`)
- Dependency Management: Version Catalog (`libs.versions.toml`)

### **Kotlin**
- **Purpose:** Only for kapt (Room compiler)
- **Version:** 2.1.0
- **Not used for:** Writing app code

---

## 📦 Dependencies Summary

### **AndroidX Libraries:**
- `androidx.appcompat:appcompat` - App compatibility
- `com.google.android.material:material` - Material Design
- `androidx.activity:activity` - Activity components
- `androidx.constraintlayout:constraintlayout` - Layouts
- `androidx.room:room-runtime` - Database
- `androidx.lifecycle:lifecycle-viewmodel` - ViewModel
- `androidx.lifecycle:lifecycle-livedata` - LiveData

### **Third-Party:**
- `com.google.zxing:core` - QR code generation
- `com.journeyapps:zxing-android-embedded` - QR code scanning

### **No External Libraries For:**
- Networking (using native Java sockets)
- JSON parsing (using org.json)
- File handling (using Java I/O)
- Audio (using Android MediaRecorder/Player)

---

## 🎯 Summary

### **Main Language:**
✅ **Java** (100% of app code)

### **Build Language:**
✅ **Kotlin DSL** (for Gradle build files only)

### **Framework:**
✅ **Android Native** (Java-based)

### **Architecture:**
✅ **MVVM** (Model-View-ViewModel)

### **Database:**
✅ **Room** (SQLite wrapper)

---

## 💡 Key Points

1. **Pure Java Project** - All app code is Java
2. **No Kotlin Code** - Kotlin is only for build system
3. **Native Android** - Using Android SDK directly
4. **No Heavy Frameworks** - Lightweight, custom implementation
5. **MVVM Pattern** - Modern Android architecture

---

## 🔄 Comparison

### **This Project:**
- Language: **Java**
- Build: **Kotlin DSL**
- Architecture: **MVVM**
- Database: **Room**

### **Alternative (If Kotlin):**
- Language: **Kotlin**
- Build: **Kotlin DSL**
- Architecture: **MVVM**
- Database: **Room**

**Difference:** Only the programming language (Java vs Kotlin), rest is same!

---

**Last Updated:** Current Session
