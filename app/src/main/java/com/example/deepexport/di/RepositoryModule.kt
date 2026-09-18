package com.example.deepexport.di

import android.content.Context
import com.example.deepexport.data.export.FileStore
import com.example.deepexport.data.export.HtmlExporter
import com.example.deepexport.data.export.JsonExporter
import com.example.deepexport.data.export.MarkdownExporter
import com.example.deepexport.data.export.TxtExporter
import com.example.deepexport.data.extraction.ExtractionCoordinator
import com.example.deepexport.data.local.dao.ConversationDao
import com.example.deepexport.data.local.dao.ExportHistoryDao
import com.example.deepexport.data.repository.ConversationRepositoryImpl
import com.example.deepexport.data.repository.ExportRepositoryImpl
import com.example.deepexport.data.repository.SettingsRepositoryImpl
import com.example.deepexport.domain.repository.ConversationRepository
import com.example.deepexport.domain.repository.ExportRepository
import com.example.deepexport.domain.repository.SettingsRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Provides
    @Singleton
    fun provideFileStore(
        @ApplicationContext context: Context
    ): FileStore {
        return FileStore(context)
    }

    @Provides
    @Singleton
    fun provideTxtExporter(): TxtExporter = TxtExporter()

    @Provides
    @Singleton
    fun provideMarkdownExporter(): MarkdownExporter = MarkdownExporter()

    @Provides
    @Singleton
    fun provideJsonExporter(): JsonExporter = JsonExporter()

    @Provides
    @Singleton
    fun provideHtmlExporter(): HtmlExporter = HtmlExporter()

    @Provides
    @Singleton
    fun provideExtractionCoordinator(): ExtractionCoordinator = ExtractionCoordinator()

    @Provides
    @Singleton
    fun provideConversationRepository(
        conversationDao: ConversationDao
    ): ConversationRepository {
        return ConversationRepositoryImpl(conversationDao)
    }

    @Provides
    @Singleton
    fun provideSettingsRepository(
        @ApplicationContext context: Context
    ): SettingsRepository {
        return SettingsRepositoryImpl(context)
    }

    @Provides
    @Singleton
    fun provideExportRepository(
        fileStore: FileStore,
        exportHistoryDao: ExportHistoryDao,
        txtExporter: TxtExporter,
        markdownExporter: MarkdownExporter,
        jsonExporter: JsonExporter,
        htmlExporter: HtmlExporter
    ): ExportRepository {
        return ExportRepositoryImpl(
            fileStore = fileStore,
            exportHistoryDao = exportHistoryDao,
            txtExporter = txtExporter,
            markdownExporter = markdownExporter,
            jsonExporter = jsonExporter,
            htmlExporter = htmlExporter
        )
    }
}
