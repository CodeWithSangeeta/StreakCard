package com.example.streakcard.data.local

package com.streakcard.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import com.streakcard.data.model.CardColorScheme
import com.streakcard.data.model.CardShape
import com.streakcard.data.model.CardSize
import com.streakcard.data.model.MissPolicy

class Converters {
    @TypeConverter fun fromColorScheme(v: CardColorScheme): String = v.name
    @TypeConverter fun toColorScheme(v: String): CardColorScheme = CardColorScheme.valueOf(v)
    @TypeConverter fun fromShape(v: CardShape): String = v.name
    @TypeConverter fun toShape(v: String): CardShape = CardShape.valueOf(v)
    @TypeConverter fun fromSize(v: CardSize): String = v.name
    @TypeConverter fun toSize(v: String): CardSize = CardSize.valueOf(v)
    @TypeConverter fun fromMissPolicy(v: MissPolicy): String = v.name
    @TypeConverter fun toMissPolicy(v: String): MissPolicy = MissPolicy.valueOf(v)
}

@Database(entities = [GoalEntity::class], version = 1, exportSchema = false)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun goalDao(): GoalDao

    companion object {
        @Volatile private var INSTANCE: AppDatabase? = null

        fun buildInstance(context: Context): AppDatabase =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "streak_card_db"
                ).fallbackToDestructiveMigration().build().also { INSTANCE = it }
            }
    }
}
