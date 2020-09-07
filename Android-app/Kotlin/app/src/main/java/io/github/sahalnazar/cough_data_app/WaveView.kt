package io.github.sahalnazar.cough_data_app

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import java.util.*

class WaveView @JvmOverloads constructor(context: Context?, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : View(context, attrs, defStyleAttr) {
    private val datas = ArrayList<Short>()
    var max: Short = 300
    private var mWidth = 0f
    private var mHeight = 0f
    var space = 1f
    private var mWavePaint: Paint? = null
    private var baseLinePaint: Paint? = null
    private var mWaveColor = Color.BLACK
    private var mBaseLineColor = Color.BLACK
    private var waveStrokeWidth = 1f
    var invalidateTime = 1000 / 200
    private var drawTime: Long = 0
    var isMaxConstant = false
    private fun init(attrs: AttributeSet?, defStyle: Int) {
        val a = context.obtainStyledAttributes(
                attrs, R.styleable.WaveView, defStyle, 0)
        mWaveColor = a.getColor(
                R.styleable.WaveView_waveColor,
                mWaveColor)
        mBaseLineColor = a.getColor(
                R.styleable.WaveView_baselineColor,
                mBaseLineColor)
        waveStrokeWidth = a.getDimension(
                R.styleable.WaveView_waveStokeWidth,
                waveStrokeWidth)
        max = a.getInt(R.styleable.WaveView_maxValue, max.toInt()).toShort()
        invalidateTime = a.getInt(R.styleable.WaveView_invalidateTime, invalidateTime)
        space = a.getDimension(R.styleable.WaveView_space, space)
        a.recycle()
        initPainters()
    }

    private fun initPainters() {
        mWavePaint = Paint()
        mWavePaint!!.color = mWaveColor // 画笔为color
        mWavePaint!!.strokeWidth = waveStrokeWidth // 设置画笔粗细
        mWavePaint!!.isAntiAlias = true
        mWavePaint!!.isFilterBitmap = true
        mWavePaint!!.style = Paint.Style.FILL
        baseLinePaint = Paint()
        baseLinePaint!!.color = mBaseLineColor // 画笔为color
        baseLinePaint!!.strokeWidth = 1f // 设置画笔粗细
        baseLinePaint!!.isAntiAlias = true
        baseLinePaint!!.isFilterBitmap = true
        baseLinePaint!!.style = Paint.Style.FILL
    }

    fun getmWaveColor(): Int {
        return mWaveColor
    }

    fun setmWaveColor(mWaveColor: Int) {
        this.mWaveColor = mWaveColor
        invalidateNow()
    }

    fun getmBaseLineColor(): Int {
        return mBaseLineColor
    }

    fun setmBaseLineColor(mBaseLineColor: Int) {
        this.mBaseLineColor = mBaseLineColor
        invalidateNow()
    }

    fun getWaveStrokeWidth(): Float {
        return waveStrokeWidth
    }

    fun setWaveStrokeWidth(waveStrokeWidth: Float) {
        this.waveStrokeWidth = waveStrokeWidth
        invalidateNow()
    }

    /**
     * 如果改变相应配置  需要刷新相应的paint设置
     */
    fun invalidateNow() {
        initPainters()
        invalidate()
    }

    fun addData(data: Short) {
        var data = data
        if (data < 0) {
            data = (-data).toShort()
        }
        if (data > max && !isMaxConstant) {
            max = data
        }
        if (datas.size > mWidth / space) {
            synchronized(this) {
                datas.removeAt(0)
                datas.add(data)
            }
        } else {
            datas.add(data)
        }
        if (System.currentTimeMillis() - drawTime > invalidateTime) {
            invalidate()
            drawTime = System.currentTimeMillis()
        }
    }

    fun clear() {
        datas.clear()
        invalidateNow()
    }

    override fun onDraw(canvas: Canvas) {
        canvas.translate(0f, mHeight / 2)
        drawBaseLine(canvas)
        drawWave(canvas)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        mWidth = w.toFloat()
        mHeight = h.toFloat()
    }

    private fun drawWave(mCanvas: Canvas) {
        for (i in datas.indices) {
            val x = i * space
            val y = datas[i].toFloat() / max * mHeight / 2
            mCanvas.drawLine(x, -y, x, y, mWavePaint!!)
        }
    }

    private fun drawBaseLine(mCanvas: Canvas) {
        mCanvas.drawLine(0f, 0f, mWidth, 0f, baseLinePaint!!)
    }

    init {
        init(attrs, defStyleAttr)
    }
}