package org.odk.collect.android.formmanagement

import org.odk.collect.android.utilities.FileUtils.copyFile
import org.odk.collect.android.utilities.FileUtils.interuptablyWriteFile
import org.odk.collect.async.OngoingWorkListener
import org.odk.collect.forms.Form
import org.odk.collect.forms.FormSource
import org.odk.collect.forms.FormSourceException
import org.odk.collect.forms.FormsRepository
import org.odk.collect.forms.MediaFile
import org.odk.collect.shared.strings.Md5.getMd5Hash
import java.io.File
import java.io.IOException

class FormMediaDownloader(
    private val formsRepository: FormsRepository,
    private val formSource: FormSource
) {

    @Throws(IOException::class, FormSourceException::class, InterruptedException::class)
    fun download(
        formToDownload: ServerFormDetails,
        files: List<MediaFile>,
        tempMediaPath: String,
        tempDir: File,
        stateListener: OngoingWorkListener
    ): Boolean {
        var atLeastOneNewMediaFileDetected = false
        val tempMediaDir = File(tempMediaPath).also { it.mkdir() }

        files.forEachIndexed { i, mediaFile ->
            stateListener.progressUpdate(i + 1)

            val tempMediaFile = File(tempMediaDir, mediaFile.filename)

            val existingFile = searchForExistingMediaFile(formToDownload, mediaFile)
            existingFile.let {
                if (it != null) {
                    if (getMd5Hash(it).contentEquals(mediaFile.hash)) {
                        copyFile(it, tempMediaFile)
                    } else {
                        val existingFileHash = getMd5Hash(it)
                        val file = formSource.fetchMediaFile(mediaFile.downloadUrl)
                        interuptablyWriteFile(file, tempMediaFile, tempDir, stateListener)

                        if (!getMd5Hash(tempMediaFile).contentEquals(existingFileHash)) {
                            atLeastOneNewMediaFileDetected = true
                        }
                    }
                } else {
                    val file = formSource.fetchMediaFile(mediaFile.downloadUrl)
                    interuptablyWriteFile(file, tempMediaFile, tempDir, stateListener)
                    atLeastOneNewMediaFileDetected = true
                }
            }
        }

        return atLeastOneNewMediaFileDetected
    }

    private fun searchForExistingMediaFile(
        formToDownload: ServerFormDetails,
        mediaFile: MediaFile
    ): File? {
        val allFormVersions = formsRepository.getAllByFormId(formToDownload.formId)
        return allFormVersions.map { form: Form ->
            File(form.formMediaPath, mediaFile.filename)
        }.firstOrNull { file: File ->
            file.exists()
        }
    }
}
