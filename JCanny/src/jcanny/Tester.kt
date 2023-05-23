package jcanny

import jcanny.JCanny.cannyEdges
import java.io.File
import javax.imageio.ImageIO
import org.apache.commons.io.FilenameUtils


//Canny parameters
    private const val CANNY_THRESHOLD_RATIO = .2 //Suggested range .2 - .4
    private const val CANNY_STD_DEV = 1 //Range 1-3

    //I/O parameters
    private lateinit var imgFileName: String
    private lateinit var imgOutFile: String
    private lateinit var imgExt: String



    fun picturesToCanny(listImages : List<String>, variant : Int){
        for (image in listImages) {
            imgFileName = image
            imgExt = FilenameUtils.getExtension(image)
            imgOutFile = image.split("\\.")[0]
            imgOutFile += "_canny."
            imgOutFile += imgExt
            when (variant) {
                0 -> {
                    try {
                        val input = ImageIO.read(File(imgFileName))
                        val output = cannyEdges(input, CANNY_STD_DEV, CANNY_THRESHOLD_RATIO)
                        ImageIO.write(output, imgExt, File(imgOutFile))
                    } catch (ex: Exception) {
                        println("ERROR ACCESSING IMAGE FILE: ${ex.message}".trimIndent())
                    }
                }
                1 -> {
                    try {
                        val output = OpenCVHelper.detectEdge(imgFileName)
                        ImageIO.write(output, imgExt, File(imgOutFile))
                    } catch (ex: Exception) {
                        println("ERROR ACCESSING IMAGE FILE: ${ex.message}".trimIndent())
                    }
                }
                else -> {
                    throw RuntimeException("")
                }
            }
        }
    }

