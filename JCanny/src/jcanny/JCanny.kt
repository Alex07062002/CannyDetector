/**
 * Copyright 2016 Robert Streetman
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http:></http:>//www.gnu.org/licenses/>.
 *
 */
package jcanny

import jcanny.Gaussian.blurGS
import jcanny.ImageUtils.gsArray
import jcanny.ImageUtils.gsImg
import java.awt.image.BufferedImage
import kotlin.math.PI
import kotlin.math.atan2
import kotlin.math.roundToInt
import kotlin.math.sqrt

object JCanny {
    private const val GAUSSIAN_RADIUS = 7
    private const val GAUSSIAN_INTENSITY = 1.5
    private var stDev = 0 //Standard deviation in magnitude of image's pixels
    private var mean = 0 //Mean of magnitude in image's pixels
    private var numDev = 0 //Number of standard deviations above mean for high threshold
    private var tHi = 0.0 //Hysteresis high threshold; Definitely edge pixels, do not examine
    private var tLo = 0.0 //Hysteresis low threshold; possible edge pixel, examine further.
    private var tFract = 0.0 //Low threshold is this fraction of high threshold
    private lateinit var dir //Gradient direction mask. Equals Math.atan2(gy/gx)
            : Array<IntArray>
    private lateinit var gx //Mask resulting from horizontal 3x3 Sobel mask
            : Array<IntArray>
    private lateinit var gy //Mask resulting from vertical 3x3 Sobel mask
            : Array<IntArray>
    private lateinit var mag //Direction mask. Equals Math.sqrt(gx^2 * gy^2)
            : Array<DoubleArray>

    /**
     * This function accepts a single-channel (grayscale, red, blue, Y, etc) image and returns an image with detected edges.
     * Currently computes hysteresis thresholds based on an a given ratio, but in the future all parameters will be passed
     * in from an external source to allow another program to optimize them.
     *
     * @param img               A BufferedImage that is to undergo Canny edge detector.
     * @param numberDeviations  Set high threshold as a function of number of standard deviations above the mean.
     * mean + std. dev: 68% of pixel magnitudes fall below this value
     * mean + 2 * std. dev: 95% of pixel magnitudes fall below this value
     * mean + 3 * std. dev: 99.7% of pixel magnitudes fall below this value
     * @param fract             Set low threshold as a fraction of the high threshold
     * @return edges            A binary image of the edges in the input image.
     */
    fun cannyEdges(img: BufferedImage, numberDeviations: Int, fract: Double): BufferedImage? {
        lateinit var raw: Array<IntArray>
        lateinit var blurred: Array<IntArray>
        var edges: BufferedImage? = null
        numDev = numberDeviations
        tFract = fract

        //More specific bounds checking later
        if (numberDeviations > 0 && fract > 0) {
            raw = gsArray(img)
            blurred = blurGS(raw, GAUSSIAN_RADIUS, GAUSSIAN_INTENSITY)
            gx = Sobel.horizontal(blurred)!! //Convolved with 3x3 horizontal Sobel mask
            gy = Sobel.vertical(blurred)!! //Convolved with 3x3 vertical Sobel mask
            magnitude() //Find the gradient magnitude at each pixel
            direction() //Find the gradient direction at each pixel
            suppression() //Using the direction and magnitude images, identify candidate points
            edges = gsImg(hysteresis())
        }
        return edges
    }

    /**
     * Send this method the horizontal and vertical Sobel convolutions to create the gradient magnitude image.
     *
     * @return void
     */
    private fun magnitude() {
        var sum = 0.0
        var `var` = 0.0
        val height = gx.size
        val width = gx[0].size
        val pixelTotal = (height * width).toDouble()
        mag = Array(height) { DoubleArray(width) }
        for (r in 0 until height) {
            for (c in 0 until width) {
                mag[r][c] = sqrt((gx[r][c] * gx[r][c] + gy[r][c] * gy[r][c]).toDouble())
                sum += mag[r][c]
            }
        }
        mean = (sum / pixelTotal).roundToInt()

        //Get variance
        for (r in 0 until height) {
            for (c in 0 until width) {
                val diff = mag[r][c] - mean
                `var` += diff * diff
            }
        }
        stDev = sqrt(`var` / pixelTotal).toInt()
    }

    /**
     * Send this method the horizontal and vertical Sobel convolutions to create the gradient direction image.
     *
     * @return void
     */
    private fun direction() {
        val height = gx.size
        val width = gx[0].size
        val piRad = 180 / PI
        dir = Array(height) { IntArray(width) }
        for (r in 0 until height) {
            for (c in 0 until width) {
                var angle = atan2(gy[r][c].toDouble(), gx[r][c].toDouble()) * piRad //Convert radians to degrees

                //Check for negative angles
                if (angle < 0) angle += 360.0

                //Each pixels ACTUAL angle is examined and placed in 1 of four groups (for the four searched 45-degree neighbors)
                //Reorder this for optimization
                when(angle){
                    in 0.0 .. 22.5 -> dir[r][c] = 0
                    in 22.5..67.5 -> dir[r][c] = 45
                    in 67.5..112.5 -> dir[r][c] = 90
                    in 112.5 .. 157.5 ->  dir[r][c] = 135
                    in 157.5..202.5 -> dir[r][c] = 0
                    in 202.5..247.5  -> dir[r][c] = 45
                    in 247.5..292.5 -> dir[r][c] = 90
                    in 292.5 .. 337.5 ->  dir[r][c] = 135
                    in 337.5 .. 360.0 -> dir[r][c] = 0
                    else -> throw NumberFormatException()
                }
            }
        }
    }

    /**
     * Call this method to use gradient direction and magnitude to suppress lesser pixels.
     *
     * @return void
     */
    private fun suppression() {
        val height = mag.size - 1
        val width = mag[0].size - 1
        for (r in 1 until height) {
            for (c in 1 until width) {
                val magnitude = mag[r][c]
                when (dir[r][c]) {
                    0 -> if (magnitude < mag[r][c - 1] && magnitude < mag[r][c + 1]) {
                        mag[r - 1][c - 1] = 0.0
                    }

                    45 -> if (magnitude < mag[r - 1][c + 1] && magnitude < mag[r + 1][c - 1]) {
                        mag[r - 1][c - 1] = 0.0
                    }

                    90 -> if (magnitude < mag[r - 1][c] && magnitude < mag[r + 1][c]) {
                        mag[r - 1][c - 1] = 0.0
                    }

                    135 -> if (magnitude < mag[r - 1][c - 1] && magnitude < mag[r + 1][c + 1]) {
                        mag[r - 1][c - 1] = 0.0
                    }
                }
            }
        }
    }

    /**
     * Call this method to use an upper and lower threshold to decided which non-suppressed pixels are edges.
     *
     * @return bin  int[][], the binary image showing edges in the original.
     */
    private fun hysteresis(): Array<IntArray> {
        val height = mag.size - 1
        val width = mag[0].size - 1
        val bin = Array(height - 1) { IntArray(width - 1) }
        tHi = (mean + numDev * stDev).toDouble() //Magnitude greater than or equal to high threshold is an edge pixel
        tLo = tHi * tFract //Magnitude less than low threshold not an edge, equal or greater possible edge
        for (r in 1 until height) {
            for (c in 1 until width) {
                val magnitude = mag[r][c]
                if (magnitude >= tHi) {
                    bin[r - 1][c - 1] = 255
                } else if (magnitude < tLo) {
                    bin[r - 1][c - 1] = 0
                } else {    //This could be separate method or lambda
                    var connected = false
                    for (nr in -1..1) {
                        for (nc in -1..1) {
                            if (mag[r + nr][c + nc] >= tHi) {
                                connected = true
                            }
                        }
                    }
                    bin[r - 1][c - 1] = if (connected) 255 else 0
                }
            }
        }
        return bin
    }
}
