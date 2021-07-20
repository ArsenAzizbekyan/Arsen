package taxi.u.repositories

import android.os.Build
import taxi.u.BuildConfig
import taxi.u.UTaxiPreference
import taxi.u.api.UTaxiNetworkService
import taxi.u.api.request.RequestSms
import taxi.u.api.request.RequestVerifyCode
import taxi.u.api.response.ResponseClientTutorial
import taxi.u.api.response.ResponseGetAccessToken
import taxi.u.api.response.ResponseSms
import taxi.u.api.response.ResponseVerifyCode
import taxi.u.helper.ActionResult
import taxi.u.util.ErrorMapper

interface LoginRepository {
    suspend fun getDestinationByAccessToken(): ActionResult<ResponseGetAccessToken>
    suspend fun getResponseClientTutorial(): ActionResult<ResponseClientTutorial>
    suspend fun getSms(requestSms: RequestSms): ActionResult<ResponseSms>
    suspend fun getSmsByCall(requestSms: RequestSms): ActionResult<ResponseSms>
    suspend fun verifySmsCode(requestVerifyCode: RequestVerifyCode): ActionResult<ResponseVerifyCode>
    fun getSMSEndTime(): Long
    fun setSMSEndTime(time: Long)
    fun setPhoneNumberToPreference(phoneNumber: String)
    fun getPhoneNumberFromPreference():String
    fun setSecondaryPhoneNumberToPreference(phoneNumber: String?)
    fun getSecondaryPhoneNumberFromPreference():String
    fun setRefreshTokenToPreference(token: String)
    fun getRefreshTokenFromPreference():String
    fun getIsRegistered(): Boolean
    fun setIsRegistered(isRegistered: Boolean)
    fun getLanguage(): String
    fun setLanguage(locale: String)
    fun setStartBonus(startBonus: Int)
    fun getDayNightMode(): Boolean
}

private const val TAG = "LoginRepository"

class LoginRepositoryImpl(
    private val networkService: UTaxiNetworkService,
    private val uTaxiPreference: UTaxiPreference,
    errorMapper: ErrorMapper
) : BaseRepository(errorMapper), LoginRepository {

    override suspend fun getDestinationByAccessToken(): ActionResult<ResponseGetAccessToken> =
        getActionResult { networkService.getAccessToken() }

    override suspend fun getResponseClientTutorial(): ActionResult<ResponseClientTutorial> =
        getActionResult { networkService.getTutorial() }

    override suspend fun getSms(requestSms: RequestSms): ActionResult<ResponseSms> =
        getActionResult { networkService.getSms(requestSms) }

    override suspend fun getSmsByCall(requestSms: RequestSms): ActionResult<ResponseSms> =
        getActionResult { networkService.getSmsByCall(requestSms) }

    override suspend fun verifySmsCode(requestVerifyCode: RequestVerifyCode): ActionResult<ResponseVerifyCode> =
        getActionResult { networkService.verifyCode(requestVerifyCode) }

    override fun getSMSEndTime(): Long = uTaxiPreference.smsTimer - System.currentTimeMillis()

    override fun setSMSEndTime(time: Long) {
        uTaxiPreference.smsTimer = time
    }

    override fun setPhoneNumberToPreference(phoneNumber: String) {
            uTaxiPreference.phoneNumber = phoneNumber

    }

    override fun getPhoneNumberFromPreference(): String = uTaxiPreference.phoneNumber
    override fun setSecondaryPhoneNumberToPreference(phoneNumber: String?) {
        if (phoneNumber != null) {
            uTaxiPreference.secondaryPhoneNumber = phoneNumber
        }
    }

    override fun getSecondaryPhoneNumberFromPreference(): String = uTaxiPreference.secondaryPhoneNumber

    override fun setRefreshTokenToPreference(token: String) {
        uTaxiPreference.refreshToken = token
    }

    override fun getRefreshTokenFromPreference(): String = uTaxiPreference.refreshToken

    override fun getIsRegistered(): Boolean = uTaxiPreference.isRegister

    override fun setIsRegistered(isRegistered: Boolean) {
        uTaxiPreference.isRegister = isRegistered
    }

    override fun getLanguage(): String = uTaxiPreference.language

    override fun setLanguage(locale: String) {
        uTaxiPreference.language = locale
    }

    override fun setStartBonus(startBonus: Int) {
        uTaxiPreference.clientTutorialStartBonus = startBonus
    }

    override fun getDayNightMode(): Boolean =
        uTaxiPreference.isNightMode

}