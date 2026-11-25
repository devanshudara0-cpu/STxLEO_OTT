package com.stleo.ott.utils

import android.content.ContentUris
import android.content.ContentValues
import android.content.Context
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.webkit.MimeTypeMap
import androidx.documentfile.provider.DocumentFile
import java.io.Closeable
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream

enum class MediaFileContentType {
    Downloads,
    Video,
    Audio,
    Images,
}

fun Closeable?.closeQuietly() {
    try {
        this?.close()
    } catch (_: Throwable) {
    }
}

/**
 * Unified scoped-storage, SAF DocumentFile, and legacy File wrapper for LEOVONIX.
 * Replaces third-party SafeFile library cleanly with zero external package references.
 */
class SafeFile private constructor(
    private val context: Context,
    private val rawFile: File? = null,
    private val docFile: DocumentFile? = null,
    private val directUri: Uri? = null,
    private val mediaRelDir: String? = null,
    private val mediaDisplayName: String? = null,
) {
    companion object {
        fun fromFile(context: Context, file: File): SafeFile {
            return SafeFile(context.applicationContext, rawFile = file)
        }

        fun fromFilePath(context: Context, path: String): SafeFile? {
            if (path.isBlank()) return null
            if (path.startsWith("content://")) {
                return fromUri(context, Uri.parse(path))
            }
            val cleanPath = if (path.startsWith("file://")) {
                Uri.parse(path).path ?: return null
            } else {
                path
            }
            return SafeFile(context.applicationContext, rawFile = File(cleanPath))
        }

        fun fromUri(context: Context, uri: Uri): SafeFile? {
            val appCtx = context.applicationContext
            return when (uri.scheme) {
                "file" -> uri.path?.let { SafeFile(appCtx, rawFile = File(it)) }
                "content" -> {
                    val doc = runCatching {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP && isTreeUri(uri)) {
                            DocumentFile.fromTreeUri(appCtx, uri)
                        } else {
                            DocumentFile.fromSingleUri(appCtx, uri)
                        }
                    }.getOrNull()
                    SafeFile(appCtx, docFile = doc, directUri = uri)
                }
                else -> null
            }
        }

        fun fromMedia(context: Context, type: MediaFileContentType): SafeFile? {
            val appCtx = context.applicationContext
            val subDir = when (type) {
                MediaFileContentType.Downloads -> Environment.DIRECTORY_DOWNLOADS
                MediaFileContentType.Video -> Environment.DIRECTORY_MOVIES
                MediaFileContentType.Audio -> Environment.DIRECTORY_MUSIC
                MediaFileContentType.Images -> Environment.DIRECTORY_PICTURES
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q && type == MediaFileContentType.Downloads) {
                return SafeFile(appCtx, mediaRelDir = subDir)
            }
            @Suppress("DEPRECATION")
            val dir = Environment.getExternalStoragePublicDirectory(subDir)
                ?: appCtx.getExternalFilesDir(subDir)
                ?: return null
            return SafeFile(appCtx, rawFile = dir)
        }

        private fun isTreeUri(uri: Uri): Boolean {
            val paths = uri.pathSegments
            return paths.size >= 2 && paths[0] == "tree"
        }

        private fun guessMimeType(name: String): String {
            val ext = name.substringAfterLast('.', "").lowercase()
            if (ext.isBlank()) return "application/octet-stream"
            return MimeTypeMap.getSingleton().getMimeTypeFromExtension(ext)
                ?: when (ext) {
                    "mp4", "m4v" -> "video/mp4"
                    "mkv" -> "video/x-matroska"
                    "srt" -> "application/x-subrip"
                    "vtt" -> "text/vtt"
                    "txt" -> "text/plain"
                    else -> "application/octet-stream"
                }
        }

        private fun normalizeRelPath(path: String): String {
            return path.trim('/').replace("\\", "/").let {
                if (it.isEmpty()) "" else "$it/"
            }
        }
    }

    fun name(): String? {
        rawFile?.let { return it.name }
        docFile?.name?.let { return it }
        mediaDisplayName?.let { return it }
        directUri?.let { uri ->
            runCatching {
                context.contentResolver.query(
                    uri,
                    arrayOf(android.provider.OpenableColumns.DISPLAY_NAME),
                    null,
                    null,
                    null
                )?.use { c ->
                    if (c.moveToFirst()) {
                        val idx = c.getColumnIndex(android.provider.OpenableColumns.DISPLAY_NAME)
                        if (idx >= 0) return c.getString(idx)
                    }
                }
            }
            return uri.lastPathSegment?.substringAfterLast('/')
        }
        return mediaRelDir?.trimEnd('/')?.substringAfterLast('/')
    }

    fun filePath(): String? {
        rawFile?.let { return it.absolutePath }
        mediaRelDir?.let { dir ->
            val rel = dir.trimEnd('/')
            return if (mediaDisplayName != null) {
                "/storage/emulated/0/$rel/$mediaDisplayName"
            } else {
                "/storage/emulated/0/$rel"
            }
        }
        directUri?.let { uri ->
            val seg = uri.lastPathSegment ?: return uri.toString()
            if (seg.startsWith("primary:")) {
                return "/storage/emulated/0/" + seg.removePrefix("primary:")
            }
            return uri.toString()
        }
        return null
    }

    fun uri(): Uri? {
        rawFile?.let { return Uri.fromFile(it) }
        docFile?.uri?.let { return it }
        directUri?.let { return it }
        if (mediaRelDir != null && mediaDisplayName != null) {
            return queryMediaStoreUri()
        }
        return null
    }

    @Throws(IOException::class)
    fun uriOrThrow(): Uri {
        return uri() ?: throw IOException("Unable to resolve URI for ${name() ?: "file"}")
    }

    fun exists(): Boolean? {
        rawFile?.let { return it.exists() }
        docFile?.let { return it.exists() }
        if (mediaRelDir != null) {
            return if (mediaDisplayName == null) true else queryMediaStoreUri() != null
        }
        directUri?.let { u ->
            return runCatching {
                context.contentResolver.openAssetFileDescriptor(u, "r")?.use { true } ?: false
            }.getOrDefault(false)
        }
        return false
    }

    fun isDirectory(): Boolean? {
        rawFile?.let { return it.isDirectory }
        docFile?.let { return it.isDirectory }
        if (mediaRelDir != null) return mediaDisplayName == null
        return false
    }

    fun length(): Long? {
        rawFile?.let { return if (it.exists()) it.length() else 0L }
        docFile?.let { return it.length() }
        val targetUri = uri() ?: return null
        return runCatching {
            context.contentResolver.query(
                targetUri,
                arrayOf(android.provider.OpenableColumns.SIZE),
                null,
                null,
                null
            )?.use { c ->
                if (c.moveToFirst()) {
                    val idx = c.getColumnIndex(android.provider.OpenableColumns.SIZE)
                    if (idx >= 0 && !c.isNull(idx)) return c.getLong(idx)
                }
                null
            } ?: context.contentResolver.openAssetFileDescriptor(targetUri, "r")?.use { afd ->
                if (afd.length >= 0) afd.length else null
            }
        }.getOrNull()
    }

    @Throws(IOException::class)
    fun lengthOrThrow(): Long {
        return length() ?: throw IOException("Unable to determine length for ${name() ?: "file"}")
    }

    fun delete(): Boolean? {
        rawFile?.let { return it.delete() }
        docFile?.let { return it.delete() }
        val targetUri = uri() ?: return false
        return runCatching {
            context.contentResolver.delete(targetUri, null, null) > 0
        }.getOrDefault(false)
    }

    @Throws(IOException::class)
    fun deleteOrThrow() {
        if (delete() != true && exists() == true) {
            throw IOException("Failed to delete ${name() ?: "file"}")
        }
    }

    fun listFiles(): List<SafeFile>? {
        rawFile?.let { dir ->
            val children = dir.listFiles() ?: return null
            return children.map { SafeFile(context, rawFile = it) }
        }
        docFile?.let { dir ->
            if (!dir.isDirectory) return null
            return dir.listFiles().map { SafeFile(context, docFile = it, directUri = it.uri) }
        }
        if (mediaRelDir != null && mediaDisplayName == null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val normRel = normalizeRelPath(mediaRelDir)
            val result = mutableListOf<SafeFile>()
            val proj = arrayOf(
                MediaStore.Downloads._ID,
                MediaStore.Downloads.DISPLAY_NAME,
            )
            val sel = "${MediaStore.Downloads.RELATIVE_PATH} = ?"
            runCatching {
                context.contentResolver.query(
                    MediaStore.Downloads.EXTERNAL_CONTENT_URI,
                    proj,
                    sel,
                    arrayOf(normRel),
                    null
                )?.use { c ->
                    val idCol = c.getColumnIndexOrThrow(MediaStore.Downloads._ID)
                    val nameCol = c.getColumnIndexOrThrow(MediaStore.Downloads.DISPLAY_NAME)
                    while (c.moveToNext()) {
                        val id = c.getLong(idCol)
                        val displayName = c.getString(nameCol)
                        val itemUri = ContentUris.withAppendedId(
                            MediaStore.Downloads.EXTERNAL_CONTENT_URI,
                            id
                        )
                        result.add(
                            SafeFile(
                                context = context,
                                directUri = itemUri,
                                mediaRelDir = mediaRelDir,
                                mediaDisplayName = displayName
                            )
                        )
                    }
                }
            }
            return result
        }
        return null
    }

    fun findFile(displayName: String): SafeFile? {
        rawFile?.let { dir ->
            val candidate = File(dir, displayName)
            return if (candidate.exists()) SafeFile(context, rawFile = candidate) else null
        }
        docFile?.let { dir ->
            val found = dir.findFile(displayName) ?: return null
            return SafeFile(context, docFile = found, directUri = found.uri)
        }
        if (mediaRelDir != null && mediaDisplayName == null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val probe = SafeFile(context, mediaRelDir = mediaRelDir, mediaDisplayName = displayName)
            return if (probe.queryMediaStoreUri() != null) probe else null
        }
        return null
    }

    fun gotoDirectory(path: String?, createMissingDirectories: Boolean = false): SafeFile? {
        if (path.isNullOrBlank()) return this
        val segments = path.split('/', '\\').filter { it.isNotBlank() && it != "." }
        if (segments.isEmpty()) return this

        rawFile?.let { base ->
            var current = base
            for (seg in segments) {
                current = File(current, seg)
            }
            if (createMissingDirectories && !current.exists()) {
                current.mkdirs()
            }
            return if (current.exists() || createMissingDirectories) {
                SafeFile(context, rawFile = current)
            } else {
                null
            }
        }

        docFile?.let { base ->
            var current: DocumentFile = base
            for (seg in segments) {
                val existing = current.findFile(seg)
                current = when {
                    existing != null && existing.isDirectory -> existing
                    createMissingDirectories -> current.createDirectory(seg) ?: return null
                    else -> return null
                }
            }
            return SafeFile(context, docFile = current, directUri = current.uri)
        }

        if (mediaRelDir != null && mediaDisplayName == null) {
            val combined = (mediaRelDir.trimEnd('/') + "/" + segments.joinToString("/"))
            return SafeFile(context, mediaRelDir = combined)
        }
        return null
    }

    fun createFile(displayName: String): SafeFile? {
        rawFile?.let { dir ->
            if (!dir.exists()) dir.mkdirs()
            val target = File(dir, displayName)
            if (!target.exists()) target.createNewFile()
            return SafeFile(context, rawFile = target)
        }
        docFile?.let { dir ->
            val created = dir.findFile(displayName)
                ?: dir.createFile(guessMimeType(displayName), displayName)
                ?: return null
            return SafeFile(context, docFile = created, directUri = created.uri)
        }
        if (mediaRelDir != null && mediaDisplayName == null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val existing = findFile(displayName)
            if (existing != null) return existing
            val values = ContentValues().apply {
                put(MediaStore.Downloads.DISPLAY_NAME, displayName)
                put(MediaStore.Downloads.MIME_TYPE, guessMimeType(displayName))
                put(MediaStore.Downloads.RELATIVE_PATH, normalizeRelPath(mediaRelDir))
            }
            val inserted = context.contentResolver.insert(
                MediaStore.Downloads.EXTERNAL_CONTENT_URI,
                values
            ) ?: return null
            return SafeFile(
                context = context,
                directUri = inserted,
                mediaRelDir = mediaRelDir,
                mediaDisplayName = displayName
            )
        }
        return null
    }

    @Throws(IOException::class)
    fun createFileOrThrow(displayName: String): SafeFile {
        return createFile(displayName)
            ?: throw IOException("Failed to create file: $displayName")
    }

    @Throws(IOException::class)
    fun openOutputStreamOrThrow(append: Boolean = false): OutputStream {
        rawFile?.let { file ->
            file.parentFile?.mkdirs()
            return FileOutputStream(file, append)
        }
        val targetUri = uriOrThrow()
        val modes = if (append) listOf("wa", "w") else listOf("wt", "w")
        for (mode in modes) {
            try {
                context.contentResolver.openOutputStream(targetUri, mode)?.let { return it }
            } catch (_: UnsupportedOperationException) {
            } catch (_: IllegalArgumentException) {
            }
        }
        throw IOException("Unable to open output stream for $targetUri")
    }

    fun openInputStream(): InputStream? {
        rawFile?.let { return runCatching { FileInputStream(it) }.getOrNull() }
        val targetUri = uri() ?: return null
        return runCatching { context.contentResolver.openInputStream(targetUri) }.getOrNull()
    }

    private fun queryMediaStoreUri(): Uri? {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) return null
        val dir = mediaRelDir ?: return null
        val name = mediaDisplayName ?: return null
        val normRel = normalizeRelPath(dir)
        val proj = arrayOf(MediaStore.Downloads._ID)
        val sel = "${MediaStore.Downloads.RELATIVE_PATH} = ? AND ${MediaStore.Downloads.DISPLAY_NAME} = ?"
        return runCatching {
            context.contentResolver.query(
                MediaStore.Downloads.EXTERNAL_CONTENT_URI,
                proj,
                sel,
                arrayOf(normRel, name),
                null
            )?.use { c ->
                if (c.moveToFirst()) {
                    val id = c.getLong(c.getColumnIndexOrThrow(MediaStore.Downloads._ID))
                    ContentUris.withAppendedId(MediaStore.Downloads.EXTERNAL_CONTENT_URI, id)
                } else null
            }
        }.getOrNull()
    }
}
