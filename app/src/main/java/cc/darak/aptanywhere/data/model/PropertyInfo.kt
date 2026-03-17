package cc.darak.aptanywhere.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class PropertyInfo(
    val complex: String = "(정보 없음)",
    val bld: String = "(정보 없음)",
    val unit: String = "(정보 없음)",
    val area: String = "(정보 없음)",
    val type: String = "(정보 없음)",

    val ownerName: String = "(정보 없음)",
    val ownerNumber: String = "(정보 없음)",

    val tenantName: String = "(정보 없음)",
    val tenantNumber: String = "(정보 없음)",

    val saleState: String = "(정보 없음)",
    val salePrice: String = "(정보 없음)",

    val jeonseState: String = "(정보 없음)",
    val jeonsePrice: String = "(정보 없음)",

    val rentState: String = "(정보 없음)",
    val rentPrice: String = "(정보 없음)",
    val rentDeposits: String = "(정보 없음)",

    val expirationDate: String = "(정보 없음)",
    val features: String = "(정보 없음)",
    val consultLog: String = "(정보 없음)",
    val remarks: String = "(정보 없음)",
) : Parcelable