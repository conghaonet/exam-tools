package com.app2m.exam

import android.content.ContentResolver
import android.content.ContentUris
import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.DocumentsContract
import android.provider.MediaStore
import android.util.Log
import java.io.File

private const val TAG = "FileChooser"
class FileChooser {
    companion object {
        fun getRealPath(context: Context, uri: Uri) : String {
            val cr = context.contentResolver
            var realPath = ""
            printAllColumns(cr, uri)
            if(ContentResolver.SCHEME_FILE == uri.scheme) {
                realPath = uri.path
            } else if(ContentResolver.SCHEME_CONTENT == uri.scheme) {
                //4.4(KitKat)及以上版本
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    if(DocumentsContract.isDocumentUri(context, uri)) {
                        val documentId = DocumentsContract.getDocumentId(uri)
                        when(uri.authority) {
                            "com.android.externalstorage.documents"-> {
                                val split = documentId.split(":")
                                    realPath = "${Environment.getExternalStorageDirectory()}${File.separator}${split[1]}"
                            } "com.android.providers.downloads.documents"-> {
                                val contentUri: Uri = ContentUris.withAppendedId(
                                        Uri.parse("content://downloads/public_downloads"), documentId.toLong())
                                realPath = getDataColumn(cr, contentUri, null, null)
                            } "com.android.providers.media.documents"-> {
                                val split = documentId.split(":")
                                val type = split[0]
                                var contentUri: Uri? = null
                                when(type) {
                                    "image" -> contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                                    "video" -> contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI
                                    "audio" -> contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI

                                }
                                val selection = "${MediaStore.Files.FileColumns._ID}=?"
                                val selectionArgs = arrayOf(split[1])
                                //未获取存储权限时，会报错
                                contentUri?.let { realPath = getDataColumn(cr, it, selection, selectionArgs) }
                            }
                        }
                    } else { realPath = getDataColumn(cr, uri, null, null) }
                } else { realPath = getDataColumn(cr, uri, null, null) }
            }
            Log.d(TAG, "realpath ==== $realPath")
            return realPath
        }

        /**
         * 查询content真是路径
         */
        private fun getDataColumn(cr: ContentResolver, uri: Uri, selection: String?, selectionArgs: Array<String>?) : String {
            var realPath = ""
            val columnData = MediaStore.Files.FileColumns.DATA
            val projection = arrayOf(columnData)
            var cursor: Cursor? = null
            try {
                cursor = cr.query(uri, projection, selection, selectionArgs, null)
                cursor?.let {
                    it.moveToFirst()
                    val columnIndexData = it.getColumnIndexOrThrow(columnData)
                    realPath = it.getString(columnIndexData)
                }
            } catch (e: RuntimeException) {
                throw e
            } finally {
                try {
                    cursor?.close()
                } catch (e: RuntimeException) {
                    throw e
                }
            }
            return realPath
        }
        /**
         * @param uri The Uri to check.
         * @return Whether the Uri authority is ExternalStorageProvider.
         */
        fun isExternalStorageDocument(uri: Uri) : Boolean {
            return "com.android.externalstorage.documents" == uri.authority
        }
        /**
         * @param uri The Uri to check.
         * @return Whether the Uri authority is DownloadsProvider.
         */
        fun isDownloadsDocument(uri: Uri) : Boolean {
            return "com.android.providers.downloads.documents" == uri.authority
        }
        /**
         * @param uri The Uri to check.
         * @return Whether the Uri authority is MediaProvider.
         */
        fun isMediaDocument(uri: Uri) : Boolean {
            return "com.android.providers.media.documents" == uri.authority
        }

        /**
         * for debug
         */
        private fun printAllColumns(cr: ContentResolver, uri: Uri) {
            Log.d(TAG, "Uri.scheme ==== ${uri.scheme}")
            Log.d(TAG, "Uri.path ==== ${uri.path}")
            Log.d(TAG, "Uri.authority ==== ${uri.authority}")
            var cursor = cr.query(uri, null, null, null, null)
            cursor?.let {
                if(it.moveToFirst()) {
                    for (colIndex in 0 until it.columnCount) {
                        var colName = it.getColumnName(colIndex)
                        Log.d(TAG, "COLUMN_${colIndex}_$colName ==== ${it.getString(colIndex)}")
                    }
                }
            }
            cursor?.close()
        }
    }
}