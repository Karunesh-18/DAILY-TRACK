# Daily Track - Android Attendance Management App

A modern, clean Android application built with Kotlin, Jetpack Compose, and Material Design 3 for efficient attendance tracking and management.

## 🚀 Technology Stack & Architecture

### Core Technologies
- **Programming Language**: Kotlin - Google's preferred language with null safety and Java interoperability
- **UI Framework**: Jetpack Compose - Modern declarative UI toolkit for real-time previews and reduced complexity
- **Design System**: Material Design 3 - Latest design language with adaptive theming and dynamic colors
- **Database**: Room Database - Android's recommended persistence library with compile-time SQL verification

### Architecture Pattern
- **MVVM (Model-View-ViewModel)** with Repository pattern
- **Clean Architecture** principles for maintainability and scalability
- **Dependency Injection** for loose coupling and testability

## 📱 Core Features

### 1. Student Management System
- Add, edit, and manage student records
- Unique roll number assignment
- Student profile management

### 2. Daily Attendance Interface
- Intuitive touch-based attendance marking
- Three status options: Present, Absent, OD (On Duty)
- Leave form submission tracking
- Real-time attendance percentage calculation

### 3. Attendance Analytics
- Individual student attendance percentages
- Class-wide attendance averages
- Predictive analytics for 75% attendance requirement
- Historical attendance trends

### 4. WhatsApp Integration
- Automated attendance report generation
- Direct sharing to WhatsApp/WhatsApp Business
- Customizable report formats
- Absentee and OD student listings

## 🏗️ Project Structure

```
app/
├── src/
│   ├── main/
│   │   ├── java/com/dailytrack/
│   │   │   ├── data/
│   │   │   │   ├── database/
│   │   │   │   │   ├── entities/
│   │   │   │   │   ├── dao/
│   │   │   │   │   └── AppDatabase.kt
│   │   │   │   └── repository/
│   │   │   ├── domain/
│   │   │   │   ├── model/
│   │   │   │   └── usecase/
│   │   │   ├── presentation/
│   │   │   │   ├── ui/
│   │   │   │   │   ├── screens/
│   │   │   │   │   ├── components/
│   │   │   │   │   └── theme/
│   │   │   │   └── viewmodel/
│   │   │   ├── utils/
│   │   │   └── MainActivity.kt
│   │   ├── res/
│   │   │   ├── values/
│   │   │   ├── drawable/
│   │   │   └── mipmap/
│   │   └── AndroidManifest.xml
│   └── test/
├── build.gradle
└── proguard-rules.pro
```

## 🗃️ Database Schema

### Student Entity
```kotlin
@Entity(tableName = "students")
data class Student(
    @PrimaryKey val id: String,
    val rollNo: String,
    val name: String,
    val createdAt: Long = System.currentTimeMillis()
)
```

### Attendance Record Entity
```kotlin
@Entity(tableName = "attendance_records")
data class AttendanceRecord(
    @PrimaryKey val id: String,
    val studentId: String,
    val date: String,
    val status: AttendanceStatus,
    val leaveFormSubmitted: Boolean = false
)

enum class AttendanceStatus { PRESENT, ABSENT, OD }
```

## 🎨 UI Implementation

### Material Design 3 Features
- **Dynamic Color Theming**: Adapts to system wallpaper
- **Adaptive Components**: Responsive design for different screen sizes
- **Accessibility**: Full compliance with Android accessibility guidelines
- **Dark/Light Theme**: System-aware theme switching

### Key UI Components
- `AttendanceScreen`: Main attendance marking interface
- `StudentAttendanceCard`: Individual student attendance component
- `AnalyticsScreen`: Attendance statistics and reports
- `StudentsScreen`: Student management interface

## 📊 Attendance Calculation

### Percentage Formula
```kotlin
fun calculateAttendancePercentage(totalClasses: Int, attendedClasses: Int): Double {
    if (totalClasses == 0) return 0.0
    return (attendedClasses.toDouble() / totalClasses.toDouble()) * 100.0
}
```

### 75% Attendance Prediction
```kotlin
fun getClassesToAttendFor75Percent(currentAttended: Int, totalClasses: Int): Int {
    val requiredClasses = (0.75 * totalClasses - currentAttended) / 0.25
    return maxOf(0, kotlin.math.ceil(requiredClasses).toInt())
}
```

## 📱 WhatsApp Integration

### Report Format
```
📋 Daily Attendance Report
📅 Date: September 13, 2025

❌ Absentees:
• 21CS001 - John Doe (❌ No Leave Form)
• 21CS002 - Jane Smith (✅ Leave Form)

📝 On Duty (OD):
• 21CS003 - Mike Johnson

📊 Summary:
Present: 28
Absent: 2
OD: 1
Class Average: 93.33%
```

## 🚀 Getting Started

### Prerequisites
- Android Studio Hedgehog | 2023.1.1 or later
- Kotlin 1.9.0 or later
- Android SDK API 24+ (Android 7.0)
- Target SDK: API 34 (Android 14)

### Installation
1. Clone the repository
```bash
git clone https://github.com/Karunesh-18/DAILY-TRACK.git
```

2. Open in Android Studio
3. Sync Gradle dependencies
4. Run the app on device/emulator

### Dependencies
```kotlin
dependencies {
    // Compose BOM
    implementation platform('androidx.compose:compose-bom:2024.02.00')
    
    // Core Compose
    implementation 'androidx.compose.ui:ui'
    implementation 'androidx.compose.material3:material3'
    
    // Room Database
    implementation 'androidx.room:room-runtime:2.6.1'
    implementation 'androidx.room:room-ktx:2.6.1'
    kapt 'androidx.room:room-compiler:2.6.1'
    
    // ViewModel & LiveData
    implementation 'androidx.lifecycle:lifecycle-viewmodel-compose:2.7.0'
    
    // Navigation
    implementation 'androidx.navigation:navigation-compose:2.7.6'
    
    // Coroutines
    implementation 'org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3'
}
```

## 🔧 Configuration

### Gradle Configuration
- **Compile SDK**: 34
- **Min SDK**: 24
- **Target SDK**: 34
- **Java Compatibility**: JavaVersion.VERSION_1_8
- **Kotlin Compiler Extension**: 1.5.8

## 📈 Performance Features

- **Efficient Recomposition**: Optimized Compose UI updates
- **Background Processing**: Coroutines for database operations
- **Memory Management**: Proper lifecycle-aware components
- **60fps Rendering**: Smooth animations and transitions

## 🧪 Testing

### Unit Tests
- Repository layer testing
- ViewModel state management testing
- Attendance calculation algorithm testing

### UI Tests
- Compose UI testing with Espresso
- Navigation flow testing
- User interaction testing

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch
3. Commit your changes
4. Push to the branch
5. Create a Pull Request

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## 👨‍💻 Author

**Karunesh**
- GitHub: [@Karunesh-18](https://github.com/Karunesh-18)

## 🙏 Acknowledgments

- Google's Material Design 3 guidelines
- Android Jetpack Compose documentation
- Room database best practices
- Modern Android development patterns