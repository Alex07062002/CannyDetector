package com.example.demo.view

import tornadofx.*
import javafx.scene.control.TextArea
import javafx.stage.FileChooser
import jcanny.picturesToCanny
import java.io.File

class FileChooser : View() {
    override val root = borderpane()

    private val ef = arrayOf(FileChooser.ExtensionFilter("Image files (*.jpg, *.png)", "*.jpg", "*.png","*.jpeg"))

    private lateinit var tfFA: TextArea
    private lateinit var inputList: MutableList<String>

    init {
        with(root) {
            title = "FileChooserView"
            center = form {
                fieldset("File Choosers") {
                    field("Files with block parent") {
                        hbox {
                            tfFA = textarea()
                            button("open") {
                                action {
                                    val fn = chooseFile("Multi + block", ef, null, FileChooserMode.Multi, root.scene.window)
                                    if (fn.isNotEmpty()) {
                                        tfFA.text = "$fn"
                                    }
                                    inputList = mutableListOf()
                                    fn.map{ inputList.add(it.toString())}
                                }
                            }
                            button("Canny detector (kotlin realization)") {
                                action {
                                    if (inputList.isNotEmpty()) {
                                            picturesToCanny(inputList,0)
                                    } else {
                                            // TODO предупреждение
                                            }
                                        }
                                    }
                            button("Canny detector (opencv realization)") {
                                action {
                                    if (inputList.isNotEmpty()) {
                                        picturesToCanny(inputList, 1)
                                    } else {
                                        // TODO предупреждение
                                    }
                                }
                            }
                                }
                            }
                        }
                    }
                }
            }
        }