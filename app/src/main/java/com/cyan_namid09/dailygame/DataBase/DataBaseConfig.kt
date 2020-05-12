package com.cyan_namid09.dailygame.DataBase

import android.content.Context
import androidx.room.*
import kotlinx.coroutines.InternalCoroutinesApi
import java.util.*

@Entity
data class Rule (
    @PrimaryKey(autoGenerate = true) val id: Int,
    var name: String,
    var time: Date
)


@Dao
interface RuleDao {
    @Query("SELECT * FROM rule ORDER BY id DESC")
    fun getAll(): List<Rule>

    @Query("SELECT * FROM rule WHERE id IN (:ruleId)")
    fun loadById(ruleId: Int): List<Rule>

    @Insert
    fun insertAll(vararg rule: Rule)

    @Insert
    fun insert(rule: Rule): Long

    @Delete
    fun delete(rule: Rule)

    @Query("DELETE FROM rule WHERE id=:ruleId")
    fun delete(ruleId: Int)

    @Query("DELETE FROM rule")
    fun deleteAll()
}


@Database(entities = arrayOf(Rule::class), version = 1)
@TypeConverters(Converters::class)
abstract class RuleDatabase : RoomDatabase() {
    abstract fun ruleDao(): RuleDao

    companion object {
        // Volatileとは？ - スレッドが値を参照する際に、必ず最新の値を見るようにする（多分）
        @Volatile
        private var INSTANCE: RuleDatabase? = null

        fun getDataBase(context: Context): RuleDatabase {
            val tempInstance = INSTANCE
            if (tempInstance != null) {
                return tempInstance
            }

            // synchronized? - 非同期処理において同時に呼ばれない（多分）。排他処理
            synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    RuleDatabase::class.java,
                    "app-database").build()
                INSTANCE = instance
                return instance
            }
        }
    }
}


class Converters {
    @TypeConverter
    fun fromTimestamp(value: Long?): Date? {
        return value?.let { Date(it) }
    }

    @TypeConverter
    fun dateToTimestamp(date: Date?): Long? {
        return date?.time?.toLong()
    }
}
