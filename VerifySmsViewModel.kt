package taxi.u.viewmodel

import android.os.Build
import androidx.lifecycle.LiveData
import taxi.u.api.request.RequestSms
import taxi.u.api.request.RequestVerifyCode
import taxi.u.api.response.ResponseSms
import taxi.u.api.response.ResponseVerifyCode
import taxi.u.helper.LoadingState
import taxi.u.repositories.LoginRepository
import taxi.u.util.SingleLiveEvent

class VerifySmsViewModel(
    private val loginRepository: LoginRepository
) : BaseViewModel() {

    private val _getSmsByCallLoadingState = SingleLiveEvent<LoadingState>()
    val getSmsByCallLoadingState: LiveData<LoadingState> = _getSmsByCallLoadingState
    private val _verifySmsCodeLoadingState = SingleLiveEvent<LoadingState>()
    val verifySmsLoadingState: LiveData<LoadingState> = _verifySmsCodeLoadingState
    private val _onGoToRegisterPage = SingleLiveEvent<String>()
    val onGoToRegisterPage: LiveData<String> = _onGoToRegisterPage
    private val _onGoToNavigationPage = SingleLiveEvent<Any>()
    val onGoToHomePage: LiveData<Any> = _onGoToNavigationPage

    fun setSmsEndTime(endTime: Long) {
        loginRepository.setSMSEndTime(endTime)
    }

    fun getSmsTimerRemainInMillis(): Long = loginRepository.getSMSEndTime()

    fun getPhoneNumber(): String = loginRepository.getPhoneNumberFromPreference()

    fun getSmsByCall(requestSms: RequestSms) {
        launchOnIO {
            _getSmsByCallLoadingState.postValue(LoadingState.LOADING)
            val getSmsByCallActionResult =
                loginRepository.getSmsByCall(requestSms)
            handleActionResult(getSmsByCallActionResult) {
                handleResponseSmsByCall(it)
            }
            _getSmsByCallLoadingState.postValue(LoadingState.DATA_LOADED)
        }
    }

    fun verifySms(smsCode: String, simOperator: String, size: String) {
        launchOnIO {
            _verifySmsCodeLoadingState.postValue(LoadingState.LOADING)
            val verifySmsCodeActionResult =
                loginRepository.verifySmsCode(getRequestVerifyCode(smsCode, simOperator, size))
            handleActionResult(verifySmsCodeActionResult) {
                handleResponseVerifyCode(it)
            }
            _verifySmsCodeLoadingState.postValue(LoadingState.DATA_LOADED)
        }
    }

    private fun handleResponseVerifyCode(responseVerifyCode: ResponseVerifyCode) {
        loginRepository.setRefreshTokenToPreference(responseVerifyCode.refreshToken)
        loginRepository.setSecondaryPhoneNumberToPreference(responseVerifyCode.secondaryPhoneNumber)
        if (responseVerifyCode.registered){
            _onGoToNavigationPage.postValue(Any())
        } else {
            _onGoToRegisterPage.postValue(loginRepository.getPhoneNumberFromPreference())
        }
        loginRepository.setSecondaryPhoneNumberToPreference(responseVerifyCode.secondaryPhoneNumber)
    }

    private fun getRequestVerifyCode(smsCode: String,
                                     simOperator: String,
                                     size: String): RequestVerifyCode =
        RequestVerifyCode(
            phoneNumber = loginRepository.getPhoneNumberFromPreference(),
            verifyCode = smsCode,
            referral = null,
            version = Build.VERSION.RELEASE,
            carrier = simOperator,
            size = size
        )

    private fun handleResponseSmsByCall(it: ResponseSms) {

    }

}