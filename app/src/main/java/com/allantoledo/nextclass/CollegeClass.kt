package com.allantoledo.nextclass

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.room.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.DayOfWeek

@Entity
data class CollegeClass(
    @PrimaryKey(autoGenerate = true) val id: Int,
    @ColumnInfo(name = "matter") val matter: String,
    @ColumnInfo(name = "local") val locale: String,
    @ColumnInfo(name = "day_of_week") val dayOfWeek: Int,
    @ColumnInfo(name = "hour") val hour: Int,
    @ColumnInfo(name = "minute") val minute: Int,
    @ColumnInfo(name = "absolute_time") val absoluteTime: Int,
)

fun getCollegeClassDefault(): CollegeClass {
    return CollegeClass(-1, "", "", 0, 0, 0, 0)
}

@Dao
interface CollegeClassDao {
    companion object {
        fun getDefault(): CollegeClass {
            return CollegeClass(0, "Default", "Default", 0, 0, 0, 0)
        }
    }

    @Query("SELECT * FROM CollegeClass ORDER BY absolute_time ASC")
    fun getAll(): List<CollegeClass>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(vararg collegeClasses: CollegeClass)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(collegeClasses: CollegeClass)

    @Delete
    fun delete(collegeClass: CollegeClass)

}

