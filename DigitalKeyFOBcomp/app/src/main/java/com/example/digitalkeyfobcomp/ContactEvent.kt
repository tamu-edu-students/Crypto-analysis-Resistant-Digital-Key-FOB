package com.example.digitalkeyfobcomp

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
// Sealed interface representing different events related to user profiles
sealed interface ProfileEvent {
    object SaveProfile : ProfileEvent
    data class SetName(val name: String) : ProfileEvent
    data class SetLocked(val locked: Boolean) : ProfileEvent
    data class SetEngine(val engine: Boolean) : ProfileEvent
    data class Setaddress(val address: String) : ProfileEvent
    data class Setsigid(val sigid: String) : ProfileEvent
    object ShowDialog : ProfileEvent
    object HideDialong : ProfileEvent
    data class DeleteProfile(val profile: ProfileEntity) : ProfileEvent
}

// Data class representing the state of user profiles in the application
data class ProfileState(
    val profiles: List<ProfileEntity> = emptyList(),
    val name: String = "",
    val locked: Boolean = false,
    val engine: Boolean = false,
    val address: String = "",
    val sigid: String = "",
    val isAddingProfile: Boolean = false,
)

@OptIn(ExperimentalCoroutinesApi::class)
class ProfileViewModel(
    private val dao: ProfileDao
) : ViewModel() {
    // Mutable state flow to represent the current state of user profiles
    private val _state = MutableStateFlow(ProfileState())
    val state = _state

    // Flow representing all profiles retrieved from the DAO
    val allprofiles = dao.getAllProfiles()

    // Flow representing all profile names retrieved from the DAO
    val profileNames = dao.getAllNames()

    // Function to delete a profile by name using a coroutine
    fun deleteProfileByName(name: String) {
        viewModelScope.launch {
            dao.deleteByName(name)
        }
    }

    // Function to delete all profiles using a coroutine
    fun deleteAllProfiles() {
        viewModelScope.launch {
            dao.deleteAll()
        }
    }

    // Coroutine function to get a profile by name
    suspend fun getProfileByName(profileName: String): ProfileEntity? {
        return dao.getProfileByName(profileName)
    }

    // Function to handle different profile-related events
    fun onEvent(event: ProfileEvent) {
        when (event) {
            is ProfileEvent.DeleteProfile -> {
                viewModelScope.launch {
                    dao.deleteProfile(event.profile)
                }
            }
            ProfileEvent.HideDialong -> {
                // Update the state to hide the profile dialog
                _state.update { it.copy(isAddingProfile = false) }
            }
            ProfileEvent.SaveProfile -> {
                // Extract profile details from the current state
                val name = state.value.name
                val locked = state.value.engine
                val engine = state.value.engine
                val address = state.value.address
                val sigid = state.value.sigid

                // Check if the name is blank and return if true
                if (name.isBlank()) {
                    return
                }

                // Create a new profile entity
                val profile = ProfileEntity(
                    name = name,
                    locked = locked,
                    engine = engine,
                    address = address,
                    sigid = sigid
                )

                // Insert the profile into the DAO using a coroutine
                viewModelScope.launch {
                    dao.insertProfile(profile)
                }

                // Update the state to reset input fields and hide the profile dialog
                _state.update {
                    it.copy(
                        isAddingProfile = false,
                        name = "",
                        locked = false,
                        engine = false,
                        address = "",
                        sigid = "",
                    )
                }
            }
            is ProfileEvent.SetEngine -> {
                // Update the state with the new engine value
                _state.update { it.copy(engine = event.engine) }
            }
            is ProfileEvent.SetLocked -> {
                // Update the state with the new locked value
                _state.update { it.copy(locked = event.locked) }
            }
            is ProfileEvent.SetName -> {
                // Update the state with the new name value
                _state.update { it.copy(name = event.name) }
            }
            is ProfileEvent.Setaddress -> {
                // Update the state with the new address value
                _state.update { it.copy(address = event.address) }
            }
            is ProfileEvent.Setsigid -> {
                // Update the state with the new sigid value
                _state.update { it.copy(sigid = event.sigid) }
            }
            ProfileEvent.ShowDialog -> {
                // Update the state to show the profile dialog
                _state.update { it.copy(isAddingProfile = true) }
            }
        }
    }
}
