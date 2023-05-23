package jcanny

import org.opencv.core.Core
import org.opencv.core.Mat
import org.opencv.core.MatOfByte
import org.opencv.imgcodecs.Imgcodecs
import org.opencv.imgproc.Imgproc
import java.awt.image.BufferedImage
import java.io.ByteArrayInputStream
import java.io.InputStream
import javax.imageio.ImageIO


object OpenCVHelper {

    private fun getMatObject(image : String): Mat {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME)
        return Imgcodecs.imread(image)
    }

    private fun matToBufferedImage(mat: Mat): BufferedImage {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME)
        val matOfByte = MatOfByte()
        Imgcodecs.imencode(".jpg", mat, matOfByte)
        val byteArray = matOfByte.toArray()
        val output: InputStream = ByteArrayInputStream(byteArray)
        return ImageIO.read(output)
    }

    fun detectEdge(image: String): BufferedImage {
        val src = getMatObject(image)
        val edge = Mat()
        Imgproc.Canny(src, edge, 50.0, 150.0)
        return matToBufferedImage(edge)
    }
}