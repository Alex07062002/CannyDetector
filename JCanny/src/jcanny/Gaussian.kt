package jcanny

import kotlin.math.PI
import kotlin.math.exp
import kotlin.math.roundToInt
import kotlin.math.sqrt

object Gaussian {
    //This seems like a very costly operation, only doing this once.
    private val SQRT2PI = sqrt(2 * PI)

    /**
     * Send this method an int[][][] RGB array, an int radius, and a double intensity to blur the
     * image with a Gaussian filter of that radius and intensity.
     *
     * @param raw       int[][][], an array of RGB values to be blurred
     * @param rad       int, the radius of the Gaussian filter (filter width = 2 * r + 1)
     * @param intens    double, the intensity of the Gaussian blur
     * @return outRGB   int[][][], an array of RGB values from blurring input image with Gaussian filter
     */
    fun blurRGB(raw: Array<Array<IntArray>>, rad: Int, intens: Double): Array<Array<IntArray>> {
        val height = raw.size
        val width = raw[0].size
        val intensSquared2 = 2 * intens * intens
        //This also seems very costly, do it as little as possible
        val invIntensSqrPi = 1 / (SQRT2PI * intens)
        var norm = 0.0
        val mask = DoubleArray(2 * rad + 1)
        val outRGB = Array(height - 2 * rad) { Array(width - 2 * rad) { IntArray(3) } }

        //Create Gaussian kernel
        for (x in -rad until rad + 1) {
            val exp = exp(-(x * x / intensSquared2))
            mask[x + rad] = invIntensSqrPi * exp
            norm += mask[x + rad]
        }

        //Convolve image with kernel horizontally
        for (r in rad until height - rad) {
            for (c in rad until width - rad) {
                val sum = DoubleArray(3)
                for (mr in -rad until rad + 1) {
                    for (chan in 0..2) {
                        sum[chan] += mask[mr + rad] * raw[r][c + mr][chan]
                    }
                }

                //Normalize channels after blur
                for (chan in 0..2) {
                    sum[chan] /= norm
                    outRGB[r - rad][c - rad][chan] = sum[chan].roundToInt()
                }
            }
        }

        //Convolve image with kernel vertically
        for (r in rad until height - rad) {
            for (c in rad until width - rad) {
                val sum = DoubleArray(3)
                for (mr in -rad until rad + 1) {
                    for (chan in 0..2) {
                        sum[chan] += mask[mr + rad] * raw[r + mr][c][chan]
                    }
                }

                //Normalize channels after blur
                for (chan in 0..2) {
                    sum[chan] /= norm
                    outRGB[r - rad][c - rad][chan] = sum[chan].roundToInt()
                }
            }
        }
        return outRGB
    }

    /**
     * Send this method an int[][] grayscale array, an int radius, and a double intensity to blur the
     * image with a Gaussian filter of that radius and intensity.
     *
     * @param raw       int[][], an array of grayscale values to be blurred
     * @param rad       int, the radius of the Gaussian filter (filter width = 2 * r + 1)
     * @param intens    double, the intensity of the Gaussian blur
     * @return outRGB   int[][], an array of grayscale values from blurring input image with Gaussian filter
     */
    fun blurGS(raw: Array<IntArray>, rad: Int, intens: Double): Array<IntArray> {
        val height = raw.size
        val width = raw[0].size
        var norm = 0.0
        val intensSquared2 = 2 * intens * intens
        //This also seems very costly, do it as little as possible
        val invIntensSqrPi = 1 / (SQRT2PI * intens)
        val mask = DoubleArray(2 * rad + 1)
        val outGS = Array(height - 2 * rad) { IntArray(width - 2 * rad) }

        //Create Gaussian kernel
        for (x in -rad until rad + 1) {
            val exp = exp(-(x * x / intensSquared2))
            mask[x + rad] = invIntensSqrPi * exp
            norm += mask[x + rad]
        }

        //Convolve image with kernel horizontally
        for (r in rad until height - rad) {
            for (c in rad until width - rad) {
                var sum = 0.0
                for (mr in -rad until rad + 1) {
                    sum += mask[mr + rad] * (raw[r][c + mr])
                }

                //Normalize channel after blur
                sum /= norm
                outGS[r - rad][c - rad] = sum.roundToInt()
            }
        }

        //Convolve image with kernel vertically
        for (r in rad until height - rad) {
            for (c in rad until width - rad) {
                var sum = 0.0
                for (mr in -rad until rad + 1) {
                    sum += mask[mr + rad] * (raw[r + mr][c])
                }

                //Normalize channel after blur
                sum /= norm
                outGS[r - rad][c - rad] = sum.roundToInt()
            }
        }
        return outGS
    }
}
