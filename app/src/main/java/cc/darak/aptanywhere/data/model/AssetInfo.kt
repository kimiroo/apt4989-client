package cc.darak.aptanywhere.data.model

import android.os.Parcelable
import cc.darak.aptanywhere.data.model.adapter.StringToNullAdapter
import com.google.gson.annotations.JsonAdapter
import kotlinx.parcelize.Parcelize

@Parcelize
data class AssetInfo(
    val complex: String = "",
    val bld: String = "",
    val unit: String = "",

    @JsonAdapter(StringToNullAdapter::class) val area: String? = null,
    @JsonAdapter(StringToNullAdapter::class) val type: String? = null,

    @JsonAdapter(StringToNullAdapter::class) val ownerName: String? = null,
    @JsonAdapter(StringToNullAdapter::class) val ownerNumber: String? = null,

    @JsonAdapter(StringToNullAdapter::class) val tenantName: String? = null,
    @JsonAdapter(StringToNullAdapter::class) val tenantNumber: String? = null,

    @JsonAdapter(StringToNullAdapter::class) val saleState: String? = null,
    @JsonAdapter(StringToNullAdapter::class) val salePrice: String? = null,

    @JsonAdapter(StringToNullAdapter::class) val jeonseState: String? = null,
    @JsonAdapter(StringToNullAdapter::class) val jeonsePrice: String? = null,

    @JsonAdapter(StringToNullAdapter::class) val rentState: String? = null,
    @JsonAdapter(StringToNullAdapter::class) val rentPrice: String? = null,
    @JsonAdapter(StringToNullAdapter::class) val rentDeposits: String? = null,

    @JsonAdapter(StringToNullAdapter::class) val expirationDate: String? = null,
    @JsonAdapter(StringToNullAdapter::class) val features: String? = null,
    @JsonAdapter(StringToNullAdapter::class) val consultLog: String? = null,
    @JsonAdapter(StringToNullAdapter::class) val remarks: String? = null,
) : Parcelable