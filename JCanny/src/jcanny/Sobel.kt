package jcanny

object Sobel {

    private val MASK_H = arrayOf(intArrayOf(-1, 0, 1), intArrayOf(-2, 0, 2), intArrayOf(-1, 0, 1))
    private val MASK_V = arrayOf(intArrayOf(-1, -2, -1), intArrayOf(0, 0, 0), intArrayOf(1, 2, 1))

    /**
     * Send this method an int[][] array of grayscale pixel values to get a an image resulting
     * from the convolution of this image with the horizontal Sobel mask.
     *
     * @param raw   int[][], array of grayscale pixel values 0-255
     * @return out  int[][], output array of convolved image.
     */
    fun horizontal(raw: Array<IntArray>): Array<IntArray>? {
        var out: Array<IntArray>? = null
        val height = raw.size
        val width = raw[0].size
        if (height > 2 && width > 2) {
            out = Array(height - 2) { IntArray(width - 2) }
            for (r in 1 until height - 1) {
                for (c in 1 until width - 1) {
                    var sum = 0
                    for (kr in -1..1) {
                        for (kc in -1..1) {
                            sum += MASK_H[kr + 1][kc + 1] * raw[r + kr][c + kc]
                        }
                    }
                    out[r - 1][c - 1] = sum
                }
            }
        }
        return out
    }

    /**
     * Send this method an int[][] array of grayscale pixel values to get a an image resulting
     * from the convolution of this image with the vertical Sobel mask.
     *
     * @param raw   int[][], array of grayscale pixel values 0-255
     * @return out  int[][], output array of convolved image.
     */
    fun vertical(raw: Array<IntArray>): Array<IntArray>? {
        var out: Array<IntArray>? = null
        val height = raw.size
        val width = raw[0].size
        if (height > 2 || width > 2) {
            out = Array(height - 2) { IntArray(width - 2) }
            for (r in 1 until height - 1) {
                for (c in 1 until width - 1) {
                    var sum = 0
                    for (kr in -1..1) {
                        for (kc in -1..1) {
                            sum += MASK_V[kr + 1][kc + 1] * raw[r + kr][c + kc]
                        }
                    }
                    out[r - 1][c - 1] = sum
                }
            }
        }
        return out
    }
}
