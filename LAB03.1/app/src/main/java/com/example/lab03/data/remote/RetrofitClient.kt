package com.example.lab03.data.remote

import android.util.Log
import okhttp3.Cache
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import okhttp3.ResponseBody
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.simplexml.SimpleXmlConverterFactory
import java.io.File
import java.io.IOException
import java.util.concurrent.TimeUnit

object RetrofitClient {
    private const val TIMEOUT = 10L // Reduced from 30 to 10 seconds
    private const val CACHE_SIZE = 10 * 1024 * 1024L // 10 MB cache

    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BASIC // Enable basic logging for debugging
    }

    private val headerInterceptor = Interceptor { chain ->
        val original = chain.request()
        val request = original.newBuilder()
            .header(
                "User-Agent",
                "Mozilla/5.0 (Linux; Android 12; Mobile) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/122.0.0.0 Mobile Safari/537.36"
            )
            .header("Accept", "application/rss+xml, application/xml, text/xml, */*;q=0.8")
            .header("Accept-Charset", "UTF-8")
            .header("Accept-Encoding", "gzip, deflate")
            .header("Cache-Control", "no-cache")
            .build()
        chain.proceed(request)
    }

    // XML cleaning interceptor to fix parsing issues
    private val xmlCleaningInterceptor = Interceptor { chain ->
        val response = chain.proceed(chain.request())
        
        if (response.isSuccessful && response.body != null) {
            val contentType = response.header("Content-Type") ?: ""
            if (contentType.contains("xml") || contentType.contains("rss")) {
                try {
                    val responseBody = response.body!!
                    val xmlContent = responseBody.string()
                    
                    // Clean XML content
                    val cleanedXml = cleanXmlContent(xmlContent)
                    
                    val newResponseBody = ResponseBody.create(
                        responseBody.contentType(),
                        cleanedXml
                    )
                    
                    response.newBuilder()
                        .body(newResponseBody)
                        .build()
                } catch (e: Exception) {
                    // If cleaning fails, return original response
                    response
                }
            } else {
                response
            }
        } else {
            response
        }
    }

    // XML content cleaning function
    private fun cleanXmlContent(xmlContent: String): String {
        return try {
            xmlContent
                // Remove BOM and other invisible characters
                .replace(Regex("[\uFEFF\u200B-\u200D\uFEFF]"), "")
                // Remove any content before the first < character
                .replace(Regex("^[^<]*"), "")
                // Remove any content after the last > character
                .replace(Regex("[^>]*$"), "")
                // Remove control characters except newlines and tabs
                .replace(Regex("[\\x00-\\x08\\x0B\\x0C\\x0E-\\x1F\\x7F]"), "")
                // Remove any leading/trailing whitespace
                .trim()
                // Fix common XML issues
                .let { content ->
                    // Ensure proper XML declaration
                    if (!content.startsWith("<?xml")) {
                        "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n$content"
                    } else {
                        content
                    }
                }
                // Remove any remaining problematic characters
                .replace(Regex("[\\x00-\\x1F\\x7F]"), "")
                // Ensure single root element
                .let { content ->
                    if (content.contains("<rss") && !content.contains("</rss>")) {
                        "$content</rss>"
                    } else if (content.contains("<feed") && !content.contains("</feed>")) {
                        "$content</feed>"
                    } else {
                        content
                    }
                }
        } catch (e: Exception) {
            // If cleaning fails, return original content
            xmlContent
        }
    }

    // Optimize Retrofit and OkHttp for better performance
    private val okHttpClient = OkHttpClient.Builder()
        .connectTimeout(10, TimeUnit.SECONDS) // Fast connection timeout
        .readTimeout(10, TimeUnit.SECONDS) // Fast read timeout
        .writeTimeout(10, TimeUnit.SECONDS)
        .addInterceptor(headerInterceptor)
        .addInterceptor(xmlCleaningInterceptor) // Add XML cleaning
        .addInterceptor(loggingInterceptor)
        // Enable HTTP/2 for faster requests
        .protocols(listOf(okhttp3.Protocol.HTTP_2, okhttp3.Protocol.HTTP_1_1))
        .build()

    fun createRssService(): RssService {
        val retrofit = Retrofit.Builder()
            .baseUrl("https://example.com/") // Base URL is required but will be ignored when using @Url
            .client(okHttpClient)
            .addConverterFactory(SimpleXmlConverterFactory.createNonStrict())
            .build()
        return retrofit.create(RssService::class.java)
    }
}
