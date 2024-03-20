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

// Entity class representing a profile with associated attributes
@Entity(tableName = "profiles")
data class ProfileEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,       // Profile Name
    val locked: Boolean = false, // Locked State
    val engine: Boolean = false, // Engine State
    val address: String,        // Bluetooth MAC address
    val sigid: String        // Signature id
    // Add other data points as needed
)

// Data Access Object (DAO) interface for ProfileEntity
@Dao
interface ProfileDao {
    // Insert or replace a profile in the database
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProfile(profile: ProfileEntity)

    // Delete a profile from the database
    @Delete
    suspend fun deleteProfile(profile: ProfileEntity)

    // Query all profiles and observe changes as a Flow
    @Query("SELECT * FROM profiles")
    fun getAllProfiles(): Flow<List<ProfileEntity>>

    // Query all profile names and observe changes as a Flow
    @Query("SELECT name FROM profiles")
    fun getAllNames(): Flow<List<String>>

    // Delete a profile by its name
    @Query("DELETE FROM profiles WHERE name = :name")
    suspend fun deleteByName(name: String)

    // Delete all profiles from the database
    @Query("DELETE FROM profiles")
    suspend fun deleteAll()

    // Query a profile by its name and return a single result as a ProfileEntity
    @Query("SELECT * FROM profiles WHERE name = :profileName LIMIT 1")
    suspend fun getProfileByName(profileName: String): ProfileEntity
}

// Room Database class that includes the ProfileDao and defines the database version
@Database(entities = [ProfileEntity::class], version = 1, exportSchema = false)
abstract class ProfileDatabase : RoomDatabase() {
    // Abstract property providing access to the ProfileDao
    abstract val dao: ProfileDao
}
