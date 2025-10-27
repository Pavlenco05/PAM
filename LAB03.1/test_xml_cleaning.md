# XML Cleaning Test

## Test Cases for XML Parsing Issues

### Problematic XML Content Examples:

1. **BOM Issue:**
```xml
﻿<?xml version="1.0" encoding="UTF-8"?>
<rss version="2.0">
  <channel>
    <title>Test Feed</title>
  </channel>
</rss>
```

2. **Whitespace Before XML:**
```xml
   <?xml version="1.0" encoding="UTF-8"?>
<rss version="2.0">
  <channel>
    <title>Test Feed</title>
  </channel>
</rss>
```

3. **Control Characters:**
```xml
<?xml version="1.0" encoding="UTF-8"?>
<rss version="2.0">
  <channel>
    <title>Test Feed</title>
  </channel>
</rss>
```

## Solutions Implemented:

1. **BOM Removal**: `[\uFEFF\u200B-\u200D\uFEFF]`
2. **Content Before First <**: `^[^<]*`
3. **Control Characters**: `[\\x00-\\x1F\\x7F]`
4. **Root Element Fix**: Auto-close `<rss>` and `<feed>` tags
5. **Better Error Messages**: User-friendly XML parsing errors

## Recommended Test Feeds:

1. **BBC News** - Usually reliable: `https://feeds.bbci.co.uk/news/rss.xml`
2. **CNN** - Good format: `https://rss.cnn.com/rss/edition.rss`
3. **TechCrunch** - Modern format: `https://techcrunch.com/feed/`

## Debugging Steps:

1. Check logs for "Error refreshing feed" messages
2. Try different sample feeds if one fails
3. Look for specific XML parsing error messages
4. Verify internet connection
