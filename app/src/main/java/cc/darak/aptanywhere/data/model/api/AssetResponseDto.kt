package cc.darak.aptanywhere.data.model.api

import cc.darak.aptanywhere.data.model.AssetInfo

data class AssetDto(
    val complex: String?,
    val bld: String?,
    val unit: String?,
    val state: String?,
    val kind: String?,
    val area: AreaDto?,
    val owner: OwnerDto?,
    val tenant: TenantDto?,
    val listing: ListingDto?,
    val expirationDate: String?,
    val features: String?,
    val consultLog: String?,
    val remarks: String?
)

/**
 * Extension function to convert blank strings (empty, spaces, or only newlines) to null.
 */
fun String?.nullIfBlank(): String? = this?.takeIf { it.isNotBlank() }

/**
 * Extension function to convert API DTO to App Domain model
 */
fun AssetDto.toDomain(): AssetInfo {
    return AssetInfo(
        complex = this.complex ?: "",
        bld = this.bld ?: "",
        unit = this.unit ?: "",

        state = this.state.nullIfBlank(),
        kind = this.kind.nullIfBlank(),

        areaExclusiveSquareMeter = this.area?.exclusiveSquareMeter?.nullIfBlank(),
        areaTotalPyeong = this.area?.totalPyeong?.nullIfBlank(),

        ownerName = this.owner?.name.nullIfBlank(),
        ownerNumber = this.owner?.number.nullIfBlank(),
        tenantName = this.tenant?.name.nullIfBlank(),
        tenantNumber = this.tenant?.number.nullIfBlank(),

        saleState = this.listing?.sale?.state.nullIfBlank(),
        salePrice = this.listing?.sale?.price.nullIfBlank(),
        jeonseState = this.listing?.jeonse?.state.nullIfBlank(),
        jeonsePrice = this.listing?.jeonse?.price.nullIfBlank(),

        rentState = this.listing?.rent?.state.nullIfBlank(),
        rentPrice = this.listing?.rent?.prices.nullIfBlank(),
        rentDeposits = this.listing?.rent?.deposits.nullIfBlank(),

        expirationDate = this.expirationDate.nullIfBlank(),

        features = this.features.nullIfBlank(),
        consultLog = this.consultLog.nullIfBlank(),
        remarks = this.remarks.nullIfBlank()
    )
}

data class AreaDto(
    val exclusiveSquareMeter: String?,
    val totalPyeong: String?
)

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