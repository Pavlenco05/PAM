# Daily Planner - Organizer Mobile Application

A comprehensive Kotlin-based Android application for managing daily events and tasks with XML data storage and notification services.

## Features

### Core Functionality
- **Event Management**: Create, read, update, and delete events
- **Calendar Integration**: Visual calendar view for event scheduling
- **Search & Filter**: Search events by keywords and filter by time periods
- **XML Data Storage**: Persistent storage using XML serialization
- **Notifications**: Sound and visual notifications for upcoming events

### Activities

#### 1. MainActivity
- **Calendar View**: Interactive calendar for date selection
- **Search Functionality**: Real-time search by event title or description
- **Filter Buttons**: Filter events by Today, Tomorrow, This Week, or All Events
- **Action Buttons**: Add new events and delete selected events
- **Events List**: RecyclerView displaying all events with update options

#### 2. AddActivity
- **Date/Time Pickers**: Intuitive date and time selection
- **Text Input Fields**: Title and description input with validation
- **Save/Cancel Actions**: Save new events or cancel creation

#### 3. UpdateActivity
- **Pre-populated Fields**: Edit existing events with current data
- **Completion Status**: Mark events as completed with toggle switch
- **Update/Delete Actions**: Modify or remove existing events

### Technical Implementation

#### Data Models
- **Event**: Core data model with XML serialization annotations
- **EventList**: Container for managing collections of events
- **EventStorageManager**: Handles XML file operations and data persistence

#### Services
- **NotificationService**: Background service for event notifications
- **BootReceiver**: Ensures service restart after device reboot

#### UI Components
- **Material Design**: Modern Material 3 design system
- **Responsive Layout**: Adaptive layouts for different screen sizes
- **Custom Adapters**: Efficient RecyclerView adapters for event display

## Architecture

### Data Storage
- Events are stored in XML format using Simple XML framework
- File location: `app/files/events.xml`
- Automatic serialization/deserialization of Event objects

### Notification System
- Background service monitors upcoming events
- Notifications triggered 15 minutes before event time
- Sound and vibration alerts for user attention
- Persistent notifications across app restarts

### Event Management
- CRUD operations for event lifecycle management
- Date/time validation and formatting
- Search and filtering capabilities
- Event completion tracking

## Requirements

- **Minimum SDK**: 24 (Android 7.0)
- **Target SDK**: 34 (Android 14)
- **Kotlin**: Latest stable version
- **Dependencies**:
  - AndroidX libraries
  - Material Design Components
  - Simple XML framework
  - Work Manager for background tasks

## Permissions

- `POST_NOTIFICATIONS`: For event reminder notifications
- `VIBRATE`: For vibration alerts
- `WAKE_LOCK`: For background service operation
- `RECEIVE_BOOT_COMPLETED`: For service restart after reboot

## Usage

1. **Adding Events**: Tap the floating action button or "Add Event" button
2. **Viewing Events**: Browse events in the main list or use calendar view
3. **Searching**: Use the search bar to find specific events
4. **Filtering**: Use filter buttons to view events by time period
5. **Editing**: Tap on any event to open the update activity
6. **Deleting**: Select an event and use the delete button

## File Structure

```
src/main/
├── java/com/dailyplanner/organizer/
│   ├── MainActivity.kt
│   ├── AddActivity.kt
│   ├── UpdateActivity.kt
│   ├── Event.kt
│   ├── EventList.kt
│   ├── EventStorageManager.kt
│   ├── EventsAdapter.kt
│   ├── NotificationService.kt
│   └── BootReceiver.kt
├── res/
│   ├── layout/
│   │   ├── activity_main.xml
│   │   ├── activity_add.xml
│   │   ├── activity_update.xml
│   │   └── item_event.xml
│   ├── values/
│   │   ├── strings.xml
│   │   ├── colors.xml
│   │   ├── themes.xml
│   │   └── styles.xml
│   └── drawable/
│       └── time_background.xml
└── AndroidManifest.xml
```

## Development Notes

- The application follows Material Design guidelines
- XML serialization provides human-readable data storage
- Background services ensure reliable notification delivery
- Responsive design adapts to different screen sizes
- Error handling and validation throughout the application

This Daily Planner application provides a complete solution for personal event management with modern Android development practices and user-friendly interface design.
