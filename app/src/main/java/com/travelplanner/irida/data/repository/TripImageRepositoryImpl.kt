package com.travelplanner.irida.data.repository

import com.travelplanner.irida.data.local.dao.TripImageDao
import com.travelplanner.irida.data.local.entity.toDomain
import com.travelplanner.irida.data.local.entity.toEntity
import com.travelplanner.irida.domain.TripImage
import com.travelplanner.irida.domain.TripImageRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.io.File
import javax.inject.Inject

class TripImageRepositoryImpl @Inject constructor(
    private val dao: TripImageDao
) : TripImageRepository {

    override fun getImagesForTrip(tripId: String): Flow<List<TripImage>> =
        dao.getImagesForTrip(tripId).map { list -> list.map { it.toDomain() } }

    override suspend fun addImage(image: TripImage) {
        dao.insert(image.toEntity())
    }

    override suspend fun deleteImage(imageId: String) {
        // Delete the file from disk before removing the DB row
        val path = dao.getFilePathById(imageId)
        if (!path.isNullOrBlank()) {
            val file = File(path)
            if (file.exists()) file.delete()
        }
        dao.deleteById(imageId)
    }
}
