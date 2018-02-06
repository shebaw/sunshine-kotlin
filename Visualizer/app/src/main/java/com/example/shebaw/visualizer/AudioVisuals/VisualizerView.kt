package com.example.shebaw.visualizer.AudioVisuals

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.os.SystemClock
import android.support.annotation.ColorInt
import android.support.v4.content.ContextCompat
import android.util.AttributeSet
import android.view.View
import com.example.shebaw.visualizer.R

class VisualizerView(context: Context, attrs: AttributeSet) : View(context, attrs) {

    // The shapes
    private val mBassCircle: TrailedShape
    private val mMidSquare: TrailedShape
    private val mTrebleTriangle: TrailedShape

    // The array which keeps the current fft bytes
    private var mBytes: ByteArray? = null

    // The time when the animation started
    private var mStartTime: Long = 0

    // Numbers representing the current average of all the values in the bass, mid and treble range
    // in the fft
    private var bass: Float = 0.toFloat()
    private var mid: Float = 0.toFloat()
    private var treble: Float = 0.toFloat()

    // Determines whether each of these should be shown
    private var showBass: Boolean = false
    private var showMid: Boolean = false
    private var showTreble: Boolean = false

    @ColorInt
    private var bgColor: Int = 0

    init {
        mBytes = null
        TrailedShape.setMinSize(MIN_SIZE_DEFAULT)

        // Create each of the shapes and define how they are drawn on screen
        // Make bass circle
        mBassCircle = object : TrailedShape(BASS_MULTIPLIER) {
            override fun drawThisShape(shapeCenterX: Float, shapeCenterY: Float, currentSize: Float, canvas: Canvas, paint: Paint) {
                canvas.drawCircle(shapeCenterX, shapeCenterY, currentSize, paint)
            }
        }

        // Make midrange square
        mMidSquare = object : TrailedShape(MID_MULTIPLIER) {
            override fun drawThisShape(shapeCenterX: Float, shapeCenterY: Float, currentSize: Float, canvas: Canvas, paint: Paint) {
                canvas.drawRect(shapeCenterX - currentSize,
                        shapeCenterY - currentSize,
                        shapeCenterX + currentSize,
                        shapeCenterY + currentSize,
                        paint)
            }
        }

        // Make treble triangle
        mTrebleTriangle = object : TrailedShape(TREBLE_MULTIPLIER) {
            override fun drawThisShape(shapeCenterX: Float, shapeCenterY: Float, currentSize: Float, canvas: Canvas, paint: Paint) {
                val trianglePath = Path()
                trianglePath.moveTo(shapeCenterX, shapeCenterY - currentSize)
                trianglePath.lineTo(shapeCenterX + currentSize, shapeCenterY + currentSize / 2)
                trianglePath.lineTo(shapeCenterX - currentSize, shapeCenterY + currentSize / 2)
                trianglePath.lineTo(shapeCenterX, shapeCenterY - currentSize)
                canvas.drawPath(trianglePath, paint)
            }
        }
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)

        // Setup all the view measurement code after the view is laid out. If this is done any
        // earlier the height and width are not yet determined
        mStartTime = SystemClock.uptimeMillis()

        val viewCenterX = width / 2f
        val viewCenterY = height / 2f
        val shortSide = if (viewCenterX < viewCenterY) viewCenterX else viewCenterY
        TrailedShape.setViewCenterX(viewCenterX)
        TrailedShape.setViewCenterY(viewCenterY)

        mBassCircle.setShapeRadiusFromCenter(shortSide * RADIUS_BASS)
        mMidSquare.setShapeRadiusFromCenter(shortSide * RADIUS_MID)
        mTrebleTriangle.setShapeRadiusFromCenter(shortSide * RADIUS_TREBLE)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        if (mBytes == null) {
            return
        }

        // Get the current angle all of the shapes are at
        val currentAngleRadians = calcCurrentAngle()

        // Draw the background
        canvas.drawColor(bgColor)

        // Draw each shape
        if (showBass) {
            mBassCircle.draw(canvas, bass, currentAngleRadians)
        }
        if (showMid) {
            mMidSquare.draw(canvas, mid, currentAngleRadians)
        }
        if (showTreble) {
            mTrebleTriangle.draw(canvas, treble, currentAngleRadians)
        }

        // Invalidate the view to immediately redraw
        invalidate()
    }

    /**
     * Calculates, based on the current time, the angle all of the shapes should be at
     *
     * @return The current angle, in radians, that all shapes should be at
     */
    private fun calcCurrentAngle(): Double {
        val elapsedTime = SystemClock.uptimeMillis() - mStartTime
        val revolutions = elapsedTime * REVOLUTIONS_PER_SECOND / 1000
        return revolutions.toDouble() * 2.0 * Math.PI
    }

    /**
     * This method is called by the [AudioInputReader] class to pass in the current fast
     * Fourier transform bytes. The array is then taken, divided up into segments, and each segment
     * is averaged to determine how big of a visual spike to display.
     *
     *
     * For more information on fast fourier transforms, check out this website:
     * http://cmc.music.columbia.edu/musicandcomputers/chapter3/03_04.php
     *
     * @param bytes
     */
    fun updateFFT(bytes: ByteArray) {
        mBytes = bytes

        // Calculate average for bass segment
        var bassTotal = 0f
        run {
            var i = 0
            while (i < bytes.size * BASS_SEGMENT_SIZE) {
                bassTotal += Math.abs(bytes[i].toInt()).toFloat()
                i++
            }
        }
        bass = bassTotal / (bytes.size * BASS_SEGMENT_SIZE)

        // Calculate average for mid segment
        var midTotal = 0f
        run {
            var i = (bytes.size * BASS_SEGMENT_SIZE).toInt()
            while (i < bytes.size * MID_SEGMENT_SIZE) {
                midTotal += Math.abs(bytes[i].toInt()).toFloat()
                i++
            }
        }
        mid = midTotal / (bytes.size * MID_SEGMENT_SIZE)

        // Calculate average for terble segment
        var trebleTotal = 0f
        for (i in (bytes.size * MID_SEGMENT_SIZE).toInt() until bytes.size) {
            trebleTotal += Math.abs(bytes[i].toInt()).toFloat()
        }
        treble = trebleTotal / (bytes.size * TREBLE_SEGMENT_SIZE)

        invalidate()
    }

    /**
     * Restarts the visualization
     */
    fun restart() {
        mBassCircle.restartTrail()
        mMidSquare.restartTrail()
        mTrebleTriangle.restartTrail()
    }

    /** The methods below can be called to change the visualization  */

    /**
     * Sets the visibility of the bass circle
     *
     * @param showBass boolean determining if bass circle should be shown
     */
    fun setShowBass(showBass: Boolean) {
        this.showBass = showBass
    }

    /**
     * Sets the visibility of the mid-range square
     *
     * @param showMid boolean determining if mid-range square should be shown
     */
    fun setShowMid(showMid: Boolean) {
        this.showMid = showMid
    }

    /**
     * Sets the visibility of the treble triangle
     *
     * @param showTreble boolean determining if treble triangle should be shown
     */
    fun setShowTreble(showTreble: Boolean) {
        this.showTreble = showTreble
    }

    /**
     * Sets the scale for the minimum size of the shape
     *
     * @param scale the scale for the size of the shape
     */
    fun setMinSizeScale(scale: Float) {
        TrailedShape.setMinSize(MIN_SIZE_DEFAULT * scale)
    }

    /**
     * Sets the color of the visualization. This should be one of the preference color values
     *
     * @param newColorKey
     */
    fun setColor(newColorKey: String) {

        @ColorInt
        val shapeColor: Int

        @ColorInt
        val trailColor: Int

        if (newColorKey == context.getString(R.string.pref_color_blue_value)) {
            shapeColor = ContextCompat.getColor(context, R.color.shapeBlue)
            trailColor = ContextCompat.getColor(context, R.color.trailBlue)
            bgColor = ContextCompat.getColor(context, R.color.backgroundBlue)
        } else if (newColorKey == context.getString(R.string.pref_color_green_value)) {
            shapeColor = ContextCompat.getColor(context, R.color.shapeGreen)
            trailColor = ContextCompat.getColor(context, R.color.trailGreen)
            bgColor = ContextCompat.getColor(context, R.color.backgroundGreen)
        } else {
            shapeColor = ContextCompat.getColor(context, R.color.shapeRed)
            trailColor = ContextCompat.getColor(context, R.color.trailRed)
            bgColor = ContextCompat.getColor(context, R.color.backgroundRed)
        }

        mBassCircle.setShapeColor(shapeColor)
        mMidSquare.setShapeColor(shapeColor)
        mTrebleTriangle.setShapeColor(shapeColor)

        mBassCircle.setTrailColor(trailColor)
        mMidSquare.setTrailColor(trailColor)
        mTrebleTriangle.setTrailColor(trailColor)
    }

    companion object {

        // These constants determine how much of a percentage of the audio frequencies each shape
        // represents. For example, the bass circle represents the bottom 10% of the frequencies.
        private val SEGMENT_SIZE = 100f
        private val BASS_SEGMENT_SIZE = 10f / SEGMENT_SIZE
        private val MID_SEGMENT_SIZE = 30f / SEGMENT_SIZE
        private val TREBLE_SEGMENT_SIZE = 60f / SEGMENT_SIZE

        // The minimum size of the shape, by default, before scaling
        private val MIN_SIZE_DEFAULT = 50f

        // This multiplier is used to make the frequency jumps a little more visually pronounced
        private val BASS_MULTIPLIER = 1.5f
        private val MID_MULTIPLIER = 3f
        private val TREBLE_MULTIPLIER = 5f

        private val REVOLUTIONS_PER_SECOND = .3f

        // Controls the Size of the circle each shape makes
        private val RADIUS_BASS = 20 / 100f
        private val RADIUS_MID = 60 / 100f
        private val RADIUS_TREBLE = 90 / 100f
    }
}