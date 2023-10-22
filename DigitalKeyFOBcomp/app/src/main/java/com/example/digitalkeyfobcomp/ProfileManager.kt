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
    val sigid: Long //signature id
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
//
//    companion object {
//        @Volatile
//        private var instance: ProfileDatabase? = null
//
//        fun getInstance(context: Context): ProfileDatabase {
//            return instance ?: synchronized(this) {
//                instance ?: Room.databaseBuilder(
//                    context.applicationContext,
//                    ProfileDatabase::class.java,
//                    "profile_database"
//                ).build().also { instance = it }
//            }
//        }
//    }
}

//class ProfileRepository(private val profileDao: ProfileDao){
//
//    val readAllData: LiveData<List<ProfileEntity>> = profileDao.getAllProfiles()
//
//    suspend fun addProfile(profileEntity: ProfileEntity){
//        profileDao.insertProfile(profileEntity)
//    }
//
//}
//class ProfileViewModel(application: Application) : AndroidViewModel(application) {
////    private val profileDao = ProfileDatabase.getInstance(application).profileDao()
//////    private val profiles: LiveData<List<ProfileEntity>> = profileDao.getAllProfiles()
//    private val getAllProfiles: LiveData<List<ProfileEntity>>
//    private val repository: ProfileRepository
//
//    init {
//        val profileDao = ProfileDatabase.getInstance(application).profileDao()
//        repository = ProfileRepository(profileDao)
//        getAllProfiles = repository.readAllData
//    }
//    fun insertProfile(profile: ProfileEntity) {
//        viewModelScope.launch(Dispatchers.IO) {
//            repository.addProfile(profile)
//        }
//    }
//
//    fun deleteProfile(profile: ProfileEntity) {
//        viewModelScope.launch(Dispatchers.IO) {
//            profileDao.deleteProfile(profile)
//        }
//    }
//}