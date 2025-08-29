package com.topdon.module.thermal.ir.view

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Rect
import android.util.AttributeSet
import android.view.SurfaceView
import com.energy.iruvc.sdkisp.LibIRProcess
import com.energy.iruvc.sdkisp.LibIRProcess.ImageRes_t
import com.energy.iruvc.utils.CommonParams.IRPROCSRCFMTType
import com.energy.iruvc.utils.CommonParams.PseudoColorType
import com.infisense.usbir.utils.IRImageHelp
import com.infisense.usbir.utils.OpencvTools
import com.infisense.usbir.utils.PseudocodeUtils
import com.topdon.lib.core.bean.AlarmBean
import com.topdon.pseudo.bean.CustomPseudoBean
import java.nio.ByteBuffer

/**
 * preview
 *
 * Created by LCG on 2024/11/30.
 */
class HikSurfaceView : SurfaceView {
    companion object {
        /**
         * [Technical comment in Chinese - content removed for ASCII compatibility]
         */
        private const val MULTIPLE = 2
    }


    /**
     * [Technical comment in Chinese - content removed for ASCII compatibility]
     */
    var isOpenAmplify: Boolean = false
        set(value) {
            field = value
            val isPortrait = rotateAngle == 90 || rotateAngle == 270
            val width = (if (isPortrait) 192 else 256) * (if (value) MULTIPLE else 1)
            val height = (if (isPortrait) 256 else 192) * (if (value) MULTIPLE else 1)
            bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        }

    /**
     * rotation angle
     */
    @Volatile
    var rotateAngle: Int = 270
        set(value) {
            field = value
            val isPortrait = value == 90 || value == 270
            val width = (if (isPortrait) 192 else 256) * (if (isOpenAmplify) MULTIPLE else 1)
            val height = (if (isPortrait) 256 else 192) * (if (isOpenAmplify) MULTIPLE else 1)
            bitmap.reconfigure(width, height, bitmap.config)
        }

    /**
     * temperature
     */
    var alarmBean = AlarmBean()

    /**
     * low
     */
    var limitTempMin = Float.MIN_VALUE
    /**
     * high
     */
    var limitTempMax = Float.MAX_VALUE


    /**
     * temperature
     */
    private val irImageHelp = IRImageHelp()

    /**
     * configuration
     */
    fun refreshCustomPseudo(it: CustomPseudoBean) {
        irImageHelp.setColorList(it.getColorList(), it.getPlaceList(), it.isUseGray, it.maxTemp, it.minTemp)
    }


    /**
 * pseudo color.
     */
    @Volatile
    private var pseudoType: PseudoColorType = PseudoColorType.PSEUDO_3
    /**
 * settingspseudo color
     *
 * 1- 3- 4-1 5-2 6-3 7- 8- 9-4 10-5 11-
     */
    fun setPseudoCode(code: Int) {
        pseudoType = PseudocodeUtils.changePseudocodeModeByOld(code)
    }


    /**
     * temperature
     */
    private val imageRes = ImageRes_t()
    /**
     * image
     */
    private var bitmap: Bitmap = Bitmap.createBitmap(192, 256, Bitmap.Config.ARGB_8888)
    /**
 * ARGB .
     */
    private val sourceArgbArray = ByteArray(256 * 192 * 4)
    /**
 * ARGB .
     */
    private val rotateArgbArray = ByteArray(256 * 192 * 4)
    /**
 * ARGB .
     */
    private val amplifyArray = ByteArray(256 * MULTIPLE * 192 * MULTIPLE * 4)
    /**
     * temperature
     */
    private val tempArray = ByteArray(256 * 192 * 2)


    constructor(context: Context) : this(context, null)

    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : this(context, attrs, defStyleAttr, 0)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int) : super(context, attrs, defStyleAttr, defStyleRes) {
        imageRes.width = 256.toChar()
        imageRes.height = 192.toChar()
    }


    /**
     * image
     */
    fun getScaleBitmap(): Bitmap = synchronized(this) {
        Bitmap.createScaledBitmap(bitmap, width, height, true)
    }

    /**
     * data
     */
    fun refresh(yuvArray: ByteArray, newTempArray: ByteArray) {
        // high
        val sourceWidth = 256
        val sourceHeight = 192

        System.arraycopy(newTempArray, 0, tempArray, 0, tempArray.size)

 //pseudo colormode
        val pseudo: PseudoColorType = if (irImageHelp.getColorList() == null) pseudoType else PseudoColorType.PSEUDO_1
        LibIRProcess.convertYuyvMapToARGBPseudocolor(yuvArray, (sourceWidth * sourceHeight).toLong(), pseudo, sourceArgbArray)
        // [Technical comment in Chinese - content removed for ASCII compatibility]
        irImageHelp.customPseudoColor(sourceArgbArray, tempArray, sourceWidth, sourceHeight)
        // [Technical comment in Chinese - content removed for ASCII compatibility]
        irImageHelp.setPseudoColorMaxMin(sourceArgbArray, tempArray, limitTempMax, limitTempMin, sourceWidth, sourceHeight)
        // temperature
        val newArray = irImageHelp.contourDetection(alarmBean, sourceArgbArray, tempArray, sourceWidth, sourceHeight) ?: sourceArgbArray
        // [Technical comment in Chinese - content removed for ASCII compatibility]
        when (rotateAngle) {
            90 -> LibIRProcess.rotateLeft90(newArray, imageRes, IRPROCSRCFMTType.IRPROC_SRC_FMT_ARGB8888, rotateArgbArray)
            180 -> LibIRProcess.rotate180(newArray, imageRes, IRPROCSRCFMTType.IRPROC_SRC_FMT_ARGB8888, rotateArgbArray)
            270 -> LibIRProcess.rotateRight90(newArray, imageRes, IRPROCSRCFMTType.IRPROC_SRC_FMT_ARGB8888, rotateArgbArray)
            else  -> System.arraycopy(newArray, 0, rotateArgbArray, 0, rotateArgbArray.size)
        }
        // [Technical comment in Chinese - content removed for ASCII compatibility]
        if (isOpenAmplify) {
            val width: Int = if (rotateAngle == 90 || rotateAngle == 270) sourceWidth else sourceHeight
            val height: Int = if (rotateAngle == 90 || rotateAngle == 270) sourceHeight else sourceWidth
            OpencvTools.supImage(rotateArgbArray, width, height, amplifyArray)
        }

        synchronized(this) {
            bitmap.copyPixelsFromBuffer(ByteBuffer.wrap(if (isOpenAmplify) amplifyArray else rotateArgbArray))
        }

        val canvas: Canvas = holder.lockCanvas() ?: return
        canvas.drawBitmap(bitmap, null, Rect(0, 0, width, height), null)
        holder.unlockCanvasAndPost(canvas)
    }
}