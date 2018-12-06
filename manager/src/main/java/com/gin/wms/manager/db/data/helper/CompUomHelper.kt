package com.gin.wms.manager.db.data.helper

import com.bosnet.ngemart.libgen.Common
import com.gin.wms.manager.db.data.CompUomData
import com.gin.wms.manager.db.data.CompUomItemData
import java.text.NumberFormat

/**
 * Created by manbaul on 5/24/2018.
 */
class CompUomHelper(compUomData: CompUomData?) {
    private var compUomData: CompUomData?
    private var reversedCompUomItems = compUomData?.compUomItems?.asReversed()
    init {
        this.compUomData = compUomData
    }

    fun getUomTail(): CompUomItemData? {
        return reversedCompUomItems?.get(0)
    }

    fun getTotalFromCompUomValue(listOfCompQty: List<Int>): Int {
        var result = 0;
        if (compUomData != null) {
            val tail = getUomTail()
            var iterator = listOfCompQty.size - compUomData!!.compUomItems?.size!!
            compUomData!!.compUomItems.forEach {
                result += getCompQtyResult(listOfCompQty.get(iterator), it, tail)
                iterator ++
            }
        } else {
            result += listOfCompQty.asReversed().get(0)
        }
        return result;
    }

    private fun getCompQtyResult(compQty: Int, compUomItem: CompUomItemData, tail: CompUomItemData?): Int {
        var result = 0
        compUomItem.let {
            val uomConv = compUomData?.uomConversions?.filter { uomConversionData ->
                uomConversionData.uomId == it.uomId && uomConversionData.toUomId == tail?.uomId
            }?.firstOrNull()

            uomConv?.let {
                result += compQty * uomConv.value
            }
        }
        return result
    }

    fun getTotalFromCompUomValue(value: String): Int {

        if (compUomData != null) {
            val uomTail = getUomTail()
            val strValues = value.split(compUomData!!.separator.toRegex()).dropLastWhile({ it.isEmpty() }).toTypedArray()
            if (strValues.size != compUomData!!.compUomItems.size)
                throw ArrayIndexOutOfBoundsException("ERROR : Jumlah item satuan komposit tidak sama")

            var result = 0
            for (i in strValues.indices) {
                val strItem = strValues[i]
                val compUomItemData = compUomData!!.compUomItems.get(i)

                for (uomConversionData in compUomData!!.uomConversions) {
                    if (compUomItemData.uomId == uomConversionData.uomId && uomTail?.uomId == uomConversionData.toUomId) {
                        result += NumberFormat.getIntegerInstance().parse(strItem).toInt() * uomConversionData.value
                    }
                }
            }

            return result

        } else {
            return value.toInt()
        }


    }

    fun getCompUomValueFromTotal(value: Double): String {
        if (compUomData == null)
            return Common.GetNumberSignedWithoutFractionFormat().format(value)
        else {
            val uomTail = getUomTail()

            var lastValue = value
            val result = StringBuilder()

            for (item in compUomData!!.compUomItems) {
                for (conItem in compUomData!!.uomConversions) {
                    if (lastValue == 0.0) {
                        result.append(String.format("%03d", 0))
                        result.append(compUomData!!.separator)
                        break
                    } else {
                        if (conItem.uomId == item.uomId && conItem.toUomId == uomTail!!.uomId) {

                            if (conItem.value == 0)
                                throw ArithmeticException("ERROR : Nilai konversi tidak ada")

                            val modResult = lastValue % conItem.value
                            val divResult = (lastValue - modResult) / conItem.value
                            lastValue = modResult

                            result.append(String.format("%03d", divResult.toInt()))
                            result.append(compUomData!!.separator)
                            break
                        }
                    }
                }
            }
            result.delete(result.length - compUomData!!.separator.length, result.length)
            return result.toString()
        }
    }

}