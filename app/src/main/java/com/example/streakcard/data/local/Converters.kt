package com.example.streakcard.data.local

import androidx.room.TypeConverter
import com.example.streakcard.data.model.CardColorScheme
import com.example.streakcard.data.model.CardShape
import com.example.streakcard.data.model.CardSize
import com.example.streakcard.data.model.MissPolicy
import java.time.LocalDate

class Converters {
    @TypeConverter fun fromLocalDate(date: LocalDate?): String? = date?.toString()
    @TypeConverter fun toLocalDate(value: String?): LocalDate? = value?.let { LocalDate.parse(it) }

    @TypeConverter fun fromColorScheme(v: CardColorScheme): String = v.name
    @TypeConverter fun toColorScheme(v: String): CardColorScheme = CardColorScheme.valueOf(v)
    @TypeConverter fun fromShape(v: CardShape): String = v.name
    @TypeConverter fun toShape(v: String): CardShape = CardShape.valueOf(v)
    @TypeConverter fun fromSize(v: CardSize): String = v.name
    @TypeConverter fun toSize(v: String): CardSize = CardSize.valueOf(v)
    @TypeConverter fun fromMissPolicy(v: MissPolicy): String = v.name
    @TypeConverter fun toMissPolicy(v: String): MissPolicy = MissPolicy.valueOf(v)
}