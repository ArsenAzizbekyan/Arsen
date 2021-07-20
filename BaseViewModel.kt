package taxi.u.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import taxi.u.helper.ActionResult
import taxi.u.model.Error
import taxi.u.util.SingleLiveEvent

private const val TAG = "BaseViewModel"

open class BaseViewModel : ViewModel() {

    //TODO: Single event: Done
    protected val _error = SingleLiveEvent<Error>()
    val error: LiveData<Error> = _error


    fun <RESPONSE> handleActionResult(actionResult: ActionResult<RESPONSE>,
                                      successCase: (success: RESPONSE) -> Unit) {
        when (actionResult) {
            is ActionResult.Success -> {
                successCase(actionResult.response)
            }
            is ActionResult.Failed -> {
                _error.postValue(actionResult.error)
            }
        }
    }

    fun launchOnIO(call: suspend () -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            call()
        }
    }

    fun launchOnDefault(call: suspend () -> Unit) {
        viewModelScope.launch(Dispatchers.Default) {
            call()
        }
    }
}