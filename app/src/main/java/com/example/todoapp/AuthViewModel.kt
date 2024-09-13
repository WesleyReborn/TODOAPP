package com.example.firebaseauthlogin

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.todoapp.exceptions.FireBaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class AuthViewModel : ViewModel() {

    private val auth : FirebaseAuth = FirebaseAuth.getInstance()

    val currentUser : FirebaseUser? get() = auth.currentUser

    private val _authState = MutableLiveData<State>()

    val authState : LiveData<State> = _authState

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
        _authState.value = null
    }
}

sealed class State {
    object Authenticated : State()
    object NotAuthenticated : State()
    object Loading : State()
    data class Error(val message : String) : State()
}
