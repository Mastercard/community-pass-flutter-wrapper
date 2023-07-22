import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.Parcelable
import android.util.Log
import androidx.lifecycle.lifecycleScope
import com.mastercard.compass.cp3.lib.flutter_wrapper.CompassKernelUIController
import com.mastercard.compass.cp3.lib.flutter_wrapper.R
import com.mastercard.compass.cp3.lib.flutter_wrapper.ui.util.CompassIntentResponse
import com.mastercard.compass.cp3.lib.flutter_wrapper.ui.util.CompassResultContract
import com.mastercard.compass.cp3.lib.flutter_wrapper.util.Key.DATA
import com.mastercard.compass.cp3.lib.flutter_wrapper.util.Key.ERROR_CODE
import com.mastercard.compass.cp3.lib.flutter_wrapper.util.Key.ERROR_MESSAGE
import com.mastercard.compass.cp3.lib.flutter_wrapper.util.Key.RELIANT_APP_GUID
import com.mastercard.compass.model.card.RegistrationStatusData
import com.mastercard.compass.model.consent.ConsentResponse

abstract class CompassApiHandlerActivity<T : Any> : CompassKernelUIController.CompassKernelActivity() {

    companion object {
        protected const val TAG = "CompassApiIntentHandlerActivity"
    }

    protected val compassApiActivityResult = registerForActivityResult(CompassResultContract<T>()){
        when(it){
            is CompassIntentResponse.Success -> successFinishActivity(it.data)
            is CompassIntentResponse.Error -> errorFoundFinishActivity(it.code, it.message)
        }
    }

    protected fun getNonIntentCompassApiResults(value: T) {
        when(value){
            is ConsentResponse -> successFinishActivity(value)
            is String -> successFinishActivity(value)
            else -> errorFoundFinishActivity(0, "Something went wrong")
        }
    }

    private lateinit var reliantAppGuid: String

    abstract suspend fun callCompassApi()

    private fun successFinishActivity(data: T){
        val intent = Intent().apply {
            when (data) {
                is ConsentResponse -> putExtra(DATA, data)
                is RegistrationStatusData -> putExtra(DATA, data)
                is String -> putExtra(DATA, data)
                is Parcelable -> putExtra(DATA, data)
            }
        }
        setResult(Activity.RESULT_OK, intent)
        finish()
    }

    private fun errorFoundFinishActivity(errorCode: Int?, errorMessage: String?) {
        Log.d(TAG, errorMessage.toString())
        val intent = Intent().apply {
            putExtra(ERROR_CODE, errorCode ?: UNKNOWN)
            putExtra(ERROR_MESSAGE, errorMessage ?: getString(R.string.error_unknown))
        }
        setResult(RESULT_CANCELED, intent)
        finish()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.Theme_Transparent)

        reliantAppGuid = intent.getStringExtra(RELIANT_APP_GUID)!!
        connectKernelService()
    }

    private fun connectKernelService() {
        connectKernelService(reliantAppGuid) { isSuccess, errorCode, errorMessage ->
            when (isSuccess) {
                true -> {
                    Log.d(TAG, "Connected to Kernel successfully")
                    startCompassCoroutine()
                }
                false -> {
                    Log.e(
                        TAG,
                        "Could not connect to Kernel. Code: $errorCode. Message: $errorMessage"
                    )
                    errorFoundFinishActivity(errorCode, errorMessage)
                }
            }
        }
    }

    private fun startCompassCoroutine() = lifecycleScope.launchWhenCreated {
        callCompassApi()
    }
}