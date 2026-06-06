package com.example.pivota.dashboard.data.mapper

import com.example.pivota.core.database.entity.ServiceOfferingEntity
import com.example.pivota.dashboard.data.dto.CreatedServiceOfferingDataDto
import com.example.pivota.dashboard.data.dto.DayAvailabilityDto
import com.example.pivota.dashboard.data.dto.ServiceOfferingDto
import com.example.pivota.dashboard.domain.model.listings_models.professionals.DayAvailability
import com.example.pivota.dashboard.domain.model.listings_models.professionals.ServiceOffering
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ServiceOfferingCacheMapper @Inject constructor(
    private val moshi: Moshi
) {

    companion object {
        // Create type for List<String>
        private val STRING_LIST_TYPE = Types.newParameterizedType(List::class.java, String::class.java)

        // Create type for List<DayAvailability> (domain model)
        private val DAY_AVAILABILITY_LIST_TYPE = Types.newParameterizedType(List::class.java, DayAvailability::class.java)
    }

    // Mapping functions between DTO and Domain models
    private fun toDomain(availabilityDto: DayAvailabilityDto): DayAvailability {
        return DayAvailability(
            day = availabilityDto.day,
            open = availabilityDto.open,
            close = availabilityDto.close,
            isClosed = availabilityDto.isClosed
        )
    }

    private fun toDto(availability: DayAvailability): DayAvailabilityDto {
        return DayAvailabilityDto(
            day = availability.day,
            open = availability.open,
            close = availability.close,
            isClosed = availability.isClosed
        )
    }

    private fun toDomainAvailabilityList(availabilityDtoList: List<DayAvailabilityDto>): List<DayAvailability> {
        return availabilityDtoList.map { toDomain(it) }
    }

    private fun toDtoAvailabilityList(availabilityList: List<DayAvailability>): List<DayAvailabilityDto> {
        return availabilityList.map { toDto(it) }
    }

    fun toEntity(
        dto: ServiceOfferingDto,
        categoryId: String
    ): ServiceOfferingEntity {

        // ✅ Renamed serviceAreas to coverageAreas
        val coverageAreasJson = moshi.adapter<List<String>>(STRING_LIST_TYPE).toJson(dto.coverageAreas)

        // Convert DTO availability to domain model first, then serialize
        val availabilityJson = dto.availability?.let { availabilityDtoList ->
            val domainAvailabilityList = toDomainAvailabilityList(availabilityDtoList)
            moshi.adapter<List<DayAvailability>>(DAY_AVAILABILITY_LIST_TYPE).toJson(domainAvailabilityList)
        }

        return ServiceOfferingEntity(
            id = dto.id,
            externalId = dto.externalId,
            professionalName = dto.professionalName,
            professionalAvatar = dto.professionalAvatar,
            isVerified = dto.isVerified,
            title = dto.title,
            description = dto.description,
            categoryId = categoryId,
            categoryName = dto.categoryName,
            basePrice = dto.basePrice,
            priceUnit = dto.priceUnit,
            currency = dto.currency,
            // ❌ REMOVED locationCity and locationNeighborhood
            // ✅ ADDED coverageAreas (replaces serviceAreas)
            coverageAreas = coverageAreasJson,
            availability = availabilityJson,
            yearsExperience = dto.yearsExperience,
            hourlyRate = dto.hourlyRate,
            status = dto.status,
            averageRating = dto.averageRating,
            reviewCount = dto.reviewCount,
            createdAt = dto.createdAt,
            updatedAt = dto.updatedAt,
            lastUpdated = System.currentTimeMillis()
        )
    }

    /**
     * Convert from CreatedServiceOfferingDataDto to Entity for individual service offering (from detail endpoint)
     * This is needed when fetching single offering details
     */
    fun toEntityFromDetail(
        dto: CreatedServiceOfferingDataDto,
        categoryId: String
    ): ServiceOfferingEntity {
        // ✅ Renamed serviceAreas to coverageAreas
        val coverageAreasJson = moshi.adapter<List<String>>(STRING_LIST_TYPE).toJson(dto.coverageAreas)

        // Convert DTO availability to domain model first, then serialize
        val availabilityJson = dto.availability?.let { availabilityDtoList ->
            val domainAvailabilityList = availabilityDtoList.map { dayDto ->
                DayAvailability(
                    day = dayDto.day,
                    open = dayDto.open,
                    close = dayDto.close,
                    isClosed = dayDto.isClosed
                )
            }
            moshi.adapter<List<DayAvailability>>(DAY_AVAILABILITY_LIST_TYPE).toJson(domainAvailabilityList)
        }

        return ServiceOfferingEntity(
            id = dto.id,
            externalId = dto.externalId,
            professionalName = dto.professionalName,
            professionalAvatar = dto.professionalAvatar,
            isVerified = dto.isVerified,
            title = dto.title,
            description = dto.description,
            categoryId = categoryId,
            categoryName = dto.categoryName,
            basePrice = dto.basePrice,
            priceUnit = dto.priceUnit,
            currency = dto.currency,
            // ❌ REMOVED locationCity and locationNeighborhood
            // ✅ ADDED coverageAreas (replaces serviceAreas)
            coverageAreas = coverageAreasJson,
            availability = availabilityJson,
            yearsExperience = dto.yearsExperience,
            hourlyRate = dto.hourlyRate,
            status = dto.status,
            averageRating = dto.averageRating,
            reviewCount = dto.reviewCount,
            createdAt = dto.createdAt,
            updatedAt = dto.updatedAt,
            lastUpdated = System.currentTimeMillis()
        )
    }

    fun toDomain(entity: ServiceOfferingEntity): ServiceOffering {
        // ✅ Renamed serviceAreas to coverageAreas
        val coverageAreas = moshi.adapter<List<String>>(STRING_LIST_TYPE).fromJson(entity.coverageAreas) ?: emptyList()

        // Deserialize as domain model directly
        val availability = entity.availability?.let {
            moshi.adapter<List<DayAvailability>>(DAY_AVAILABILITY_LIST_TYPE).fromJson(it)
        } ?: emptyList()

        return ServiceOffering(
            id = entity.id,
            externalId = entity.externalId,
            professionalName = entity.professionalName,
            professionalAvatar = entity.professionalAvatar,
            isVerified = entity.isVerified,
            title = entity.title,
            description = entity.description,
            categoryId = entity.categoryId,
            categoryName = entity.categoryName,
            basePrice = entity.basePrice,
            priceUnit = entity.priceUnit,
            currency = entity.currency,
            // ❌ REMOVED locationCity and locationNeighborhood
            // ✅ ADDED coverageAreas (replaces serviceAreas)
            coverageAreas = coverageAreas,
            availability = availability,
            yearsExperience = entity.yearsExperience,
            hourlyRate = entity.hourlyRate,
            status = entity.status,
            averageRating = entity.averageRating,
            reviewCount = entity.reviewCount,
            createdAt = entity.createdAt,
            updatedAt = entity.updatedAt
        )
    }

    fun toDomainList(entities: List<ServiceOfferingEntity>): List<ServiceOffering> {
        return entities.map { toDomain(it) }
    }
}