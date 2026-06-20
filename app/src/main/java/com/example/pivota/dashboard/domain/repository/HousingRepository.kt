package com.example.pivota.dashboard.domain.repository

import com.example.pivota.core.network.ApiResult
import com.example.pivota.dashboard.domain.model.listings_models.housing.*

interface HousingRepository {

    // GET /housing-module/listings
    suspend fun getAllHousingListings(
        params: GetAllHousingParams
    ): ApiResult<HouseListingsResponse>

    // GET /housing-module/listings/search
    suspend fun searchHousingListings(
        params: SearchHousingParams
    ): ApiResult<HouseListingsResponse>

    // GET /housing-module/listings/category
    suspend fun getHousingListingsByCategory(
        params: GetHousingByCategoryParams
    ): ApiResult<HouseListingsResponse>

    // GET /housing-module/details/:id
    suspend fun getHouseListingById(
        params: GetHouseByIdParams
    ): ApiResult<HouseListingResponse>

    // GET /housing-module/my-listings
    suspend fun getOwnHousingListings(
        params: GetOwnHousingParams
    ): ApiResult<HouseListingsResponse>

    // POST /housing-module/listings
    suspend fun createHouseListing(
        params: CreateHouseParams
    ): ApiResult<HouseCreateResponse>

    // PATCH /housing-module/listings/:id
    suspend fun updateHouseListing(
        listingId: String,
        params: UpdateHouseParams
    ): ApiResult<HouseListingResponse>

    // POST /housing-module/listings/:id/viewing
    suspend fun scheduleViewing(
        listingId: String,
        params: ScheduleViewingParams
    ): ApiResult<HouseViewingResponse>

    // ======================================================
    // ADMIN ENDPOINTS
    // ======================================================

    // GET /housing-module/admin/listings
    suspend fun getAdminHousingListings(
        params: GetAdminHousingParams
    ): ApiResult<HouseListingsResponse>

    // POST /housing-module/admin/accounts/:accountId/listings
    suspend fun adminCreateHouseListing(
        accountId: String,
        params: AdminCreateHouseParams
    ): ApiResult<HouseCreateResponse>

    // PATCH /housing-module/admin/listings/:id
    suspend fun adminUpdateHouseListing(
        listingId: String,
        params: AdminUpdateHouseParams
    ): ApiResult<HouseListingResponse>

    // POST /housing-module/admin/listings/:id/viewing
    suspend fun adminScheduleViewing(
        listingId: String,
        params: AdminScheduleViewingParams
    ): ApiResult<HouseViewingResponse>
}