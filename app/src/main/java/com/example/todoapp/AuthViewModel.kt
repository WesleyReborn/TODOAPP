package com.example.firebaseauthlogin

import androidx.lifecycle.ViewModel
import com.example.todoapp.exceptions.FireBaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class AuthViewModel : ViewModel() {

    private val auth : FirebaseAuth = FirebaseAuth.getInstance()

    val currentUser : FirebaseUser? get() = auth.currentUser

    private val _authState = MutableStateFlow<State>(State.NotAuthenticated)

    val authState: StateFlow<State> = _authState

    init {
        authStatus()
    }

    fun authStatus() {
        if(auth.currentUser == null) {
            _authState.value = State.NotAuthenticated
        }else {
            _authState.value = State.Authenticated
        }
    }

    fun login(email : String, password : String) {

        if(email.isEmpty() || password.isEmpty()) {
            _authState.value = State.Error("E-mail e senha não podem estar vazios")
            return
        }
        _authState.value = State.Loading
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener{ task ->
                if(task.isSuccessful) {
                    _authState.value = State.Authenticated
                }else {
                    val exception = task.exception
                    val errorMessage = FireBaseException.fireBaseError(exception)
                    _authState.value = State.Error(errorMessage)
                }
            }
    }

    fun signup(email : String, password : String) {

        if(email.isEmpty() || password.isEmpty()) {
            _authState.value = State.Error("E-mail e senha não podem estar vazios")
            return
        }
        _authState.value = State.Loading
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener{ task ->
                if(task.isSuccessful) {
                    _authState.value = State.Authenticated
                }else {
                    val exception = task.exception
                    val errorMessage = FireBaseException.fireBaseError(exception)
                    _authState.value = State.Error(errorMessage)
                }
            }
    }

    fun logout(){
        auth.signOut()
        _authState.value = State.NotAuthenticated
    }

    fun resetState(){
        _authState.value = State.NotAuthenticated
    }
}

sealed class State {
    object Authenticated : State()
    object NotAuthenticated : State()
    object Loading : State()
    data class Error(val message : String) : State()
}
