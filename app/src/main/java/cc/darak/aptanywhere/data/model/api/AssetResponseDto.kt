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

        area = this.area.nullIfBlank(),
        type = this.type.nullIfBlank(),

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

        // Check both items in the list and final result
        features = this.features
            ?.filter { it.isNotBlank() }
            ?.joinToString("\n")
            .nullIfBlank(),

        consultLog = this.consultLog.nullIfBlank(),
        remarks = this.remarks.nullIfBlank()
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