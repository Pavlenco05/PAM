# RSS Reader Android App

A simple Android RSS feed reader application that meets the requirements for programmatic emulation of a web service.

## Features

✅ **Multiple RSS Feeds**: Add and manage 2 or more RSS feeds
✅ **Navigation to Posts**: Click on feed items to open them in the browser
✅ **Local Storage**: Feeds and items are saved locally using SQLite database
✅ **Feed Management**: Add, delete, and switch between different feeds
✅ **Save Items**: Long-press items to save them locally
✅ **Offline Support**: Previously loaded feeds are available offline

## Requirements Met

1. **HTTP Protocol Methods**: Uses GET requests to fetch RSS feeds
2. **Multiple RSS Feeds**: Supports adding multiple feeds with easy switching
3. **Navigation to Posts**: Tap items to open in browser
4. **Local Saving**: SQLite database stores feeds and items persistently

## How to Use

1. **Launch the App**: The app will automatically try to load the YAM News feed
2. **Add Feeds**: Tap the "+" button to add new RSS feeds
3. **Choose Sample Feeds**: Select from pre-configured popular feeds or enter custom URLs
4. **Switch Feeds**: Use the "Change Feed" button to switch between added feeds
5. **Read Articles**: Tap on any item to open it in your browser
6. **Save Items**: Long-press items to save them for later
7. **Refresh**: Use the refresh button to update the current feed

## Sample Feeds Included

- YAM News (Romanian)
- BBC News
- CNN
- Reuters
- TechCrunch
- The Verge

## Technical Details

- **Architecture**: MVVM with Repository pattern
- **UI**: Jetpack Compose
- **Network**: Retrofit with OkHttp (optimized with HTTP/2, compression, 10s timeouts)
- **XML Parsing**: Simple XML framework
- **Database**: SQLite with custom helper (batch inserts for speed)
- **Dependencies**: Modern Android libraries

## Performance Optimizations

- ⚡ **Fast Timeouts**: 10-second timeouts (reduced from 30s) for quicker failures
- ⚡ **HTTP/2 Support**: Enabled for faster connection multiplexing
- ⚡ **Gzip Compression**: Enabled to reduce data transfer
- ⚡ **No Logging**: Disabled verbose logging in production for speed
- ⚡ **Batch Inserts**: Database operations optimized for bulk inserts
- ⚡ **Silent Error Handling**: Skips invalid items without logging overhead

## XML Parsing Fixes

- 🔧 **BOM Removal**: Automatically removes Byte Order Mark and invisible characters
- 🔧 **Content Cleaning**: Strips invalid characters before XML parsing
- 🔧 **Error Recovery**: Better error messages for XML parsing issues
- 🔧 **Non-Strict Parsing**: Uses lenient XML parser for malformed feeds
- 🔧 **Response Interceptor**: Cleans XML content before parsing

## Error Handling

The app includes comprehensive error handling for:
- Network connectivity issues
- Invalid RSS feeds
- Missing feed data
- Parsing errors

## Local Storage

- Feeds are stored in SQLite database
- Items are cached locally for offline access
- User can save specific items for later reading
- Data persists between app sessions
