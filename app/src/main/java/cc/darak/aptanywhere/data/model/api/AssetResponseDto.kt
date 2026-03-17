package cc.darak.aptanywhere.data.model.api

import cc.darak.aptanywhere.data.model.AssetInfo

data class AssetDto(
    val complex: String?,
    val bld: String?,
    val unit: String?,
    val area: String?,
    val type: String?,
    val owner: OwnerDto?,
    val tenant: TenantDto?,
    val listing: ListingDto?,
    val expirationDate: String?,
    val features: List<String>?,
    val consultLog: String?,
    val remarks: String?
)

/**
 * Extension function to convert API DTO to App Domain model
 */
fun AssetDto.toDomain(): AssetInfo {
    return AssetInfo(
        complex = this.complex ?: "(정보 없음)",
        bld = this.bld ?: "(정보 없음)",
        unit = this.unit ?: "(정보 없음)",
        area = this.area ?: "(정보 없음)",
        type = this.type ?: "(정보 없음)",

        ownerName = this.owner?.name ?: "(정보 없음)",
        ownerNumber = this.owner?.number ?: "(정보 없음)",

        tenantName = this.tenant?.name ?: "(정보 없음)",
        tenantNumber = this.tenant?.number ?: "(정보 없음)",

        saleState = this.listing?.sale?.state ?: "(정보 없음)",
        salePrice = this.listing?.sale?.price ?: "(정보 없음)",

        jeonseState = this.listing?.jeonse?.price ?: "(정보 없음)",
        jeonsePrice = this.listing?.jeonse?.price ?: "(정보 없음)",

        rentState = this.listing?.rent?.state ?: "(정보 없음)",
        rentPrice = this.listing?.rent?.prices ?: "(정보 없음)",
        rentDeposits = this.listing?.rent?.deposits ?: "(정보 없음)",

        expirationDate = this.expirationDate ?: "(정보 없음)",
        features = this.features?.joinToString("\n") ?: "(정보 없음)",
        consultLog = this.consultLog ?: "(정보 없음)",
        remarks = this.remarks ?: "(정보 없음)"
    )
}

data class OwnerDto(val name: String?, val number: String?)
data class TenantDto(val name: String?, val number: String?)

data class ListingDto(
    val sale: PriceStateDto?,
    val jeonse: PriceStateDto?,
    val rent: RentDto?
)

data class PriceStateDto(val state: String?, val price: String?)
data class RentDto(
    val state: String?,
    val prices: String?,
    val deposits: String?
)