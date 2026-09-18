package com.example.deepexport.di

import android.content.Context
import com.example.deepexport.data.local.AppDatabase
import com.example.deepexport.data.local.dao.ConversationDao
import com.example.deepexport.data.local.dao.ExportHistoryDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideAppDatabase(
        @ApplicationContext context: Context
    ): AppDatabase {
        return AppDatabase.getInstance(context)
    }

    @Provides
    @Singleton
    fun provideConversationDao(
        database: AppDatabase
    ): ConversationDao {
        return database.conversationDao()
    }

    @Provides
    @Singleton
    fun provideExportHistoryDao(
        database: AppDatabase
    ): ExportHistoryDao {
        return database.exportHistoryDao()
    }
}
