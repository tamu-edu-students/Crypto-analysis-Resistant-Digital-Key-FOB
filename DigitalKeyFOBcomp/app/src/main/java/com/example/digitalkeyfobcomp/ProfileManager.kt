package com.example.digitalkeyfobcomp

import androidx.room.Dao
import androidx.room.Database
import androidx.room.Delete
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.RoomDatabase
import kotlinx.coroutines.flow.Flow

@Entity(tableName = "profiles")
data class ProfileEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String, // Profile Name
    val locked: Boolean = false, // Locked State
    val engine: Boolean = false, // Engine State
    val address: Int, // Bluetooth MAC address
    val sigid: String //signature id
    // Add other data points as needed
)

@Dao
interface ProfileDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProfile(profile: ProfileEntity)

    @Delete
    suspend fun deleteProfile(profile: ProfileEntity)

    @Query("SELECT * FROM profiles")
    fun getAllProfiles(): Flow<List<ProfileEntity>>

    @Query("SELECT name FROM profiles")
    fun getAllNames(): Flow<List<String>>

    @Query("DELETE FROM profiles WHERE name = :name")
    suspend fun deleteByName(name: String)

    @Query("DELETE FROM profiles")
    suspend fun deleteAll()

    @Query("SELECT * FROM profiles WHERE name = :profileName LIMIT 1")
    suspend fun getProfileByName(profileName: String): ProfileEntity

}


@Database(entities = [ProfileEntity::class], version = 1, exportSchema = false)
abstract class ProfileDatabase : RoomDatabase() {

    abstract val dao: ProfileDao

}
