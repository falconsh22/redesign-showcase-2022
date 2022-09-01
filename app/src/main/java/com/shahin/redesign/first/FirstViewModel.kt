package com.shahin.redesign.first

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class FirstViewModel: ViewModel() {

    private val _motionLayoutProgress = MutableLiveData(0.0f)
    val motionLayoutProgress: LiveData<Float> = _motionLayoutProgress

    fun updateMotionLayoutProgress(value: Float) {
        _motionLayoutProgress.postValue(value)
    }

    private val _isReEntry = MutableLiveData(false)
    val isReEntry: LiveData<Boolean> = _isReEntry

    fun setIsReEntry(isReEntry: Boolean) {
        _isReEntry.postValue(isReEntry)
    }

    private val _fastReEntry = MutableLiveData(false)
    val fastReEntry: LiveData<Boolean> = _fastReEntry

    fun setFastReEntry(shouldBeFast: Boolean) {
        _fastReEntry.postValue(shouldBeFast)
    }
}
