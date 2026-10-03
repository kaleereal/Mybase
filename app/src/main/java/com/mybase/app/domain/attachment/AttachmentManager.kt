package com.mybase.app.domain.attachment

import android.content.Context
import com.mybase.app.data.repository.MyBaseRepository
import com.mybase.app.domain.model.AttachmentEntity
import java.io.File
import java.io.InputStream
import java.util.UUID

class AttachmentManager(
    private val repository: MyBaseRepository,
    private val context: Context
) {
    suspend fun saveAttachment(
        entryId: String,
        fieldId: String,
        fileName: String,
        mimeType: String,
        inputStream: InputStream
    ): AttachmentEntity {
        val attachmentsDir = File(context.filesDir, "attachments/$entryId").apply { mkdirs() }
        val targetFile = File(attachmentsDir, "${System.currentTimeMillis()}_$fileName")

        targetFile.outputStream().use { output ->
            inputStream.copyTo(output)
        }

        val attachment = AttachmentEntity(
            id = UUID.randomUUID().toString(),
            entryId = entryId,
            fieldId = fieldId,
            fileName = fileName,
            filePath = targetFile.absolutePath,
            mimeType = mimeType,
            fileSize = targetFile.length()
        )

        repository.insertAttachment(attachment)
        return attachment
    }

    suspend fun deleteAttachment(attachment: AttachmentEntity) {
        val file = File(attachment.filePath)
        if (file.exists()) {
            file.delete()
        }
        repository.deleteAttachment(attachment)
    }
}
