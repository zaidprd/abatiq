package com.abatiq.app.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

/**
 * Single Room database for Phase 1.
 *
 * Version history:
 *   - v1: Ten-question / three-card Phase 1 MVP (legacy).
 *   - v2: 60-second floating-balloon Phase 1 MVP (current brief).
 *
 * The MVP has not shipped, so a destructive migration is acceptable: any
 * pre-release installations get a fresh schema. When Phase 1 ships to the
 * Play Store we will switch to ordered Migration objects.
 */
@Database(
    entities = [SessionEntity::class, AttemptEntity::class],
    version = 2,
    exportSchema = false,
)
abstract class AbatiQDatabase : RoomDatabase() {
    abstract fun sessionDao(): SessionDao

    companion object {
        private const val DB_NAME = "abatiq.db"

        @Volatile
        private var instance: AbatiQDatabase? = null

        fun get(context: Context): AbatiQDatabase {
            return instance ?: synchronized(this) {
                instance ?: Room.databaseBuilder(
                    context.applicationContext,
                    AbatiQDatabase::class.java,
                    DB_NAME,
                )
                    .fallbackToDestructiveMigration(dropAllTables = true)
                    .build()
                    .also { instance = it }
            }
        }
    }
}
