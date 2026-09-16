package com.example.deepexport.domain.usecase

import com.example.deepexport.domain.model.ExportResult
import com.example.deepexport.domain.repository.ExportRepository
import kotlinx.coroutines.flow.Flow

class LoadHistoryUseCase(
    private val exportRepository: ExportRepository
) {
    operator fun invoke(): Flow<List<ExportResult>> {
        return exportRepository.getExportHistory()
    }
}
