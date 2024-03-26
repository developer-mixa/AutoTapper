package com.example.autotapper.presentation.custom_views

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.text.StaticLayout
import android.text.TextPaint
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatTextView
import com.example.autotapper.R

class StrokedTextView @JvmOverloads constructor(
    context: Context,
    attr: AttributeSet? = null,
    defStyleAttr: Int = 0,
) : AppCompatTextView(context, attr, defStyleAttr) {

    private var strokeColor: Int = Color.BLACK
    private var strokeWidth: Float = 8f

    private lateinit var staticLayout: StaticLayout

    private var calculateWidth = 0

    private val textPaint by lazy {
        TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
            textSize = this@StrokedTextView.textSize
            typeface = this@StrokedTextView.typeface
        }
    }

    init {
        initializeAttributes(attr, defStyleAttr)
    }

    private fun initializeAttributes(attrs: AttributeSet?, defStyleAttr: Int) {
        if (attrs == null) return

        val typedArray =
            context.obtainStyledAttributes(attrs, R.styleable.StrokedTextView, defStyleAttr, 0)

        typedArray.apply {
            strokeWidth = getDimensionPixelSize((R.styleable.StrokedTextView_textStrokeWidth), 12).toFloat()
            strokeColor = getColor(typedArray.getIndex(R.styleable.StrokedTextView_textStrokeColor), Color.BLACK)
        }

        typedArray.recycle()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val halfWidth = strokeWidth.toInt() / 2

        /**Change the padding depending on the thickness of the stroke*/
        setPadding(paddingStart + halfWidth, paddingTop, paddingRight + halfWidth, paddingBottom)

        /**Calculates the size of the container where the text will be drawn, otherwise the text may be cut off*/
        calculateWidth = (MeasureSpec.getSize(widthMeasureSpec) - paddingStart)

        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
    }

    override fun onDraw(canvas: Canvas) {

        setupText()
        with(canvas) {
            save()
            translate(paddingStart.toFloat(), 0f)

            // Draw stroke
            textPaint.configureForStroke()
            staticLayout.draw(this)

            // Draw text
            textPaint.configureForFill()
            staticLayout.draw(this)

            restore()
        }
    }

    private fun setupText() {
        staticLayout =
            StaticLayout.Builder
                .obtain(text, 0, text.length, textPaint, calculateWidth)
                .setLineSpacing(lineSpacingExtra, lineSpacingMultiplier)
                .build()
    }

    private fun Paint.configureForFill() {
        style = Paint.Style.FILL
        color = textColors.defaultColor
    }

    private fun Paint.configureForStroke() {
        style = Paint.Style.STROKE
        color = strokeColor
        strokeWidth = this@StrokedTextView.strokeWidth
    }

}