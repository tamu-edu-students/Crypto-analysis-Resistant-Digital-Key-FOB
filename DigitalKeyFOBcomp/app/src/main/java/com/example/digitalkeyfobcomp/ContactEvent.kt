package com.example.digitalkeyfobcomp

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

sealed interface ProfileEvent{
    object SaveProfile: ProfileEvent
    data class SetName(val name: String): ProfileEvent
    data class SetLocked(val locked: Boolean): ProfileEvent
    data class SetEngine(val engine: Boolean): ProfileEvent
    data class Setaddress(val address: Int): ProfileEvent
    data class Setsigid(val sigid: Long): ProfileEvent
    object ShowDialog: ProfileEvent
    object HideDialong: ProfileEvent
    data class DeleteProfile(val profile: ProfileEntity): ProfileEvent

}

data class ProfileState(
    val profiles: List<ProfileEntity> = emptyList(),
    val name: String = "",
    val locked: Boolean = false,
    val engine: Boolean = false,
    val address: Int = 0,
    val sigid: Long = 0,
    val isAddingProfile: Boolean = false,
)

@OptIn(ExperimentalCoroutinesApi::class)
class ProfileViewModel(
    private val dao: ProfileDao
): ViewModel(){
    private val _state = MutableStateFlow(ProfileState())
    val state = _state

    val allprofiles = dao.getAllProfiles()

    val profileNames = dao.getAllNames()

    fun deleteProfileByName(name: String) {
        viewModelScope.launch {
            dao.deleteByName(name)
        }
    }
    fun deleteAllProfiles() {
        viewModelScope.launch {
            dao.deleteAll()
        }
    }

    suspend fun getProfileByName(profileName: String): ProfileEntity? {
        return dao.getProfileByName(profileName)
    }
//    suspend fun getProfileByName(profileName: String): ProfileEntity {
//        return dao.getProfileByName(profileName)
//    }
//    fun getProfileByName(profileName: String) {
//        viewModelScope.launch {
//            dao.getProfileByName(profileName)
//        }
//    }

//    val profile = MutableLiveData<ProfileEntity>()
//
//    fun getProfileByName(profileName: String) {
//        viewModelScope.launch {
//            val result = dao.getProfileByName(profileName)
//            profile.postValue(result)
//        }
//    }
//    suspend fun fetchProfileDetails(profileName: String) {
//        val profile = dao.getProfileByName(profileName)
//        // Update the selectedProfile state with the fetched profile
//        selectedProfile = profile
//    }

    fun onEvent(event: ProfileEvent){
        when(event){
            is ProfileEvent.DeleteProfile -> {
                viewModelScope.launch{
                    dao.deleteProfile(event.profile)
                }
            }
            ProfileEvent.HideDialong -> {
                _state.update { it.copy(
                    isAddingProfile = false
                ) }
            }
            ProfileEvent.SaveProfile -> {
                val name = state.value.name
                val locked = state.value.engine
                val engine = state.value.engine
                val address = state.value.address
                val sigid = state.value.sigid

                if(name.isBlank()){
                    return
                }

                val profile = ProfileEntity(
                    name = name,
                    locked = locked,
                    engine = engine,
                    address = address,
                    sigid = sigid
                )

                viewModelScope.launch{
                    dao.insertProfile(profile)
                }
                _state.update {
                    it.copy(
                        isAddingProfile = false,
                        name = "",
                        locked = false,
                        engine= false,
                        address=0,
                        sigid= 0,

                    )
                }
            }


            is ProfileEvent.SetEngine -> {
                _state.update { it.copy(
                    engine = event.engine
                ) }
            }
            is ProfileEvent.SetLocked -> {
                _state.update { it.copy(
                    locked = event.locked
                ) }
            }
            is ProfileEvent.SetName -> {
                _state.update { it.copy(
                    name = event.name
                ) }
            }
            is ProfileEvent.Setaddress -> {
                _state.update { it.copy(
                    address = event.address
                ) }
            }
            is ProfileEvent.Setsigid -> {
                _state.update { it.copy(
                    sigid = event.sigid
                ) }
            }
            ProfileEvent.ShowDialog -> {
                _state.update { it.copy(
                    isAddingProfile = true
                ) }
            }
        }
    }
}