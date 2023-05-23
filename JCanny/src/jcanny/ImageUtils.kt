package jcanny

import java.awt.image.BufferedImage
import kotlin.math.roundToInt

object ImageUtils {
    /**
     * Send this method a BufferedImage to get an RGB array (int, value 0-255).
     *
     * @param img   BufferedImage, the input image from which to extract RGB
     * @return rgb  int[][][], a 3-dimension array of RGB values from image
     */
    fun rgbArray(img: BufferedImage): Array<Array<IntArray>>? {
        var rgb: Array<Array<IntArray>>? = null
        val height = img.height
        val width = img.width
        if (height > 0 && width > 0) {
            rgb = Array(height) { Array(width) { IntArray(3) } }
            for (row in 0 until height) {
                for (column in 0 until width) {
                    rgb[row][column] = intRGB(img.getRGB(column, row))
                }
            }
        }
        return rgb
    }

    /**
     * Send this method an array of RGB pixels (int) to get a BufferedImage.
     *
     * @param raw   int[][][] representing RGB pixels of image.
     * @return img  BufferedImage built from RGB array
     */
    fun rgbImg(raw: Array<Array<IntArray>>): BufferedImage {
        val img: BufferedImage
        val height = raw.size
        val width = raw[0].size
        //if (height > 0 && width > 0 || raw[0][0].size == 3) {
        img = BufferedImage(width, height, BufferedImage.TYPE_INT_RGB)
        for (row in 0 until height) {
            for (column in 0 until width) {
                img.setRGB(
                    column,
                    row,
                    raw[row][column][0] shl 16 or (raw[row][column][1] shl 8) or raw[row][column][2]
                )
            }
        }
        // }
        return img
    }

    /**
     * Send this method a BufferedImage to get a grayscale array int, value 0-255.
     *
     * @param img   BufferedImage, the input image from which to extract grayscale
     * @return gs   int[][] array of grayscale pixel values from image.
     */
    fun gsArray(img: BufferedImage): Array<IntArray> {
        var gs: Array<IntArray> = Array(img.height) { IntArray(img.width) }
        val height = img.height
        val width = img.width
        if (height > 0 && width > 0) {
            gs = Array(height) { IntArray(width) }
            for (i in 0 until height) {
                for (j in 0 until width) {
                    val bits = img.getRGB(j, i)
                    val avg = (((bits shr 16 and 0xff) + (bits shr 8 and 0xff) + (bits and 0xff)) / 3.0).roundToInt()
                    gs[i][j] = avg
                }
            }
        }
        return gs
    }

    /**
     * Send this method an array of grayscale pixels (int) to get a BufferedImage
     *
     * @param raw   int[][] representing grayscale pixels of image.
     * @return img  BufferedImage built from grayscale array
     */
    fun gsImg(raw: Array<IntArray>): BufferedImage? {
        var img: BufferedImage? = null
        val height = raw.size
        val width = raw[0].size
        if (width > 0) {
            img = BufferedImage(width, height, BufferedImage.TYPE_INT_RGB)
            for (i in 0 until height) {
                for (j in 0 until width) {
                    img.setRGB(j, i, raw[i][j] shl 16 or (raw[i][j] shl 8) or raw[i][j])
                }
            }
        }
        return img
    }

    /**
     * Send this method a 32-bit pixel value from BufferedImage to get the RGB
     *
     * @param bits  int, 32-bit BufferedImage pixel value
     * @return rgb  int[], RGB values extracted from pixel
     */
    private fun intRGB(bits: Int): IntArray {
        val rgb = intArrayOf(bits shr 16 and 0xff, bits shr 8 and 0xff, bits and 0xff)

        //Don't propagate bad pixel values
        for (i in 0..2) {
            if (rgb[i] < 0) {
                rgb[i] = 0
            } else if (rgb[i] > 255) {
                rgb[i] = 255
            }
        }
        return rgb
    }
}