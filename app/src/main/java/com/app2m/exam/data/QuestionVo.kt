package com.app2m.exam.data

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class QuestionVo(@SerializedName("@class") var class_name: String, var type: Int, var id: String, var content: Content, var source: Source, var correctResponse: CorrectResponse?) : Parcelable {

    @Parcelize
    data class Content(var identifier: String, var title: String, var label: String?, var adaptive: Boolean?,
                       var time_dependent: Boolean?, var responses: List<EmptyObj>?, var items: List<EmptyObj>?,
                       var feedbacks: List<EmptyObj>?, var preview: EmptyObj?) : Parcelable
    @Parcelize
    data class Source(var identifier: String, var title: String, var description: String?, var tech_info: TechInfo) : Parcelable {
        /**
         * ......
         * 省略若干无效字段
         * ......
         */

        @Parcelize
        data class TechInfo(var offline: SourceOfTechInfo?, var nd_href: SourceOfTechInfo?, var href: SourceOfTechInfo?, var source: SourceOfTechInfo) : Parcelable {
            /**
             * offline, nd_href, href, source的数据结构完全一样，故只定义了SourceOfTechInfo对象
             */
            @Parcelize
            data class SourceOfTechInfo(var format: String, var size: Int, var location: String, var requirements: List<EmptyObj>?,
                            var md5: String?, var secure_key: String?, var entry: String?, var printable: Boolean?) : Parcelable
        }
    }

    /**
     * 保存正确答案，通过解析XML得到该对象
     */
    @Parcelize
    data class CorrectResponse(var values: List<String>) : Parcelable
    /**
     * 不需要的对象用EmptyObj代替
     */
    @Parcelize
    data class EmptyObj(var id: String?) : Parcelable

}