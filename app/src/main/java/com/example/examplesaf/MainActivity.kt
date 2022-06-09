package com.example.examplesaf

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.example.examplesaf.SafManager.REQUEST_CODE_CREATE_SAF
import com.example.examplesaf.SafManager.REQUEST_CODE_OPEN_SAF
import com.example.examplesaf.databinding.ActivityMainBinding
import kotlinx.android.synthetic.main.layout_file_contents.view.*
import kotlinx.android.synthetic.main.layout_file_info.view.*
import java.io.File
import java.io.FileOutputStream

class MainActivity : AppCompatActivity() {

    lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        binding.btnCreate.setOnClickListener {
            SafManager.createFileSAF(this, "*/*", "test.txt")
        }

        binding.btnOpen.setOnClickListener {
            SafManager.openFileSAF(this, "*/*")
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == RESULT_OK) {
            when (requestCode) {
                REQUEST_CODE_CREATE_SAF -> {
                    val uri = data?.data
                    uri?.let {

                        /**************************
                         * 생성된 파일 uri 활용하기
                         **************************/

                        // Hello World 쓰기
                        try {
                            val fileDescriptor = contentResolver.openFileDescriptor(uri, "w")
                            val fileOutputStream = FileOutputStream(fileDescriptor?.fileDescriptor)
                            fileOutputStream.write("Hello World".toByteArray())
                            fileOutputStream.close()
                            fileDescriptor?.close()
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }

                        // 파일 정보 가져오기
                        val file = SafManager.getFileFromSAF(this, uri)
                        setFileInfo(uri, file)

                        // 파일 내용 가져오기
                        when (file.extension) {
                            "txt" -> {
                                val contents = SafManager.getTextFromSAF(this, uri)
                                binding.layoutFileContents.tvContents.text = contents
                                binding.layoutFileContents.ivImage.setImageBitmap(null)
                            }
                            else -> {
                                binding.layoutFileContents.tvContents.text = ""
                                binding.layoutFileContents.ivImage.setImageBitmap(null)
                            }
                        }
                    }
                }
                REQUEST_CODE_OPEN_SAF -> {
                    val uri = data?.data
                    uri?.let {

                        /**************************
                         * 선택한 파일 uri 활용하기
                         **************************/

                        // 파일 정보 가져오기
                        val file = SafManager.getFileFromSAF(this, uri)
                        setFileInfo(uri, file)

                        // 파일 내용 가져오기 (txt 또는 jpg,png)
                        when (file.extension) {
                            "txt" -> {
                                val contents = SafManager.getTextFromSAF(this, uri)
                                binding.layoutFileContents.tvContents.text = contents
                                binding.layoutFileContents.ivImage.setImageBitmap(null)
                            }
                            "jpg",
                            "png" -> {
                                val bitmap = SafManager.getImageFromSAF(this, uri)
                                binding.layoutFileContents.tvContents.text = ""
                                binding.layoutFileContents.ivImage.setImageBitmap(bitmap)
                            }
                            else -> {
                                binding.layoutFileContents.tvContents.text = ""
                                binding.layoutFileContents.ivImage.setImageBitmap(null)
                            }
                        }
                    }
                }
            }
        }
    }

    private fun setFileInfo(uri: Uri, file: File) {
        binding.layoutFileInfo.tvUri.text = uri.toString()
        binding.layoutFileInfo.tvName.text = file.name
        binding.layoutFileInfo.tvExtension.text = file.extension
        binding.layoutFileInfo.tvSize.text = file.length().toString()
    }
}