package com.travelplanner.irida.data.repository

import android.util.Log
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

    companion object {
        private const val TAG = "TripImageRepository"
    }

    override fun getImagesForTrip(tripId: String): Flow<List<TripImage>> =
        dao.getImagesForTrip(tripId).map { list -> list.map { it.toDomain() } }

    override suspend fun addImage(image: TripImage) {
        Log.d(TAG, "addImage() → tripId=${image.tripId} path=${image.filePath}")
        dao.insert(image.toEntity())
        Log.i(TAG, "addImage() completado → id=${image.id}")
    }

    override suspend fun deleteImage(imageId: String) {
        Log.d(TAG, "deleteImage() → id=$imageId")
        val path = dao.getFilePathById(imageId)
        if (!path.isNullOrBlank()) {
            val file = File(path)
            val deleted = if (file.exists()) file.delete() else false
            Log.d(TAG, "deleteImage() fichero eliminado=$deleted path=$path")
        } else {
            Log.e(TAG, "deleteImage() no se encontró path para id=$imageId")
        }
        dao.deleteById(imageId)
        Log.i(TAG, "deleteImage() completado → id=$imageId")
    }
}
