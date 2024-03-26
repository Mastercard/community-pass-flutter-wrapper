package com.mastercard.compass.cp3.lib.flutter_wrapper

import android.app.Activity
import android.app.Application
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.util.Base64
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.mastercard.compass.base.*
import com.mastercard.compass.exceptions.JwtParseException
import com.mastercard.compass.jwt.*
import com.mastercard.compass.kernel.client.Permissions
import com.mastercard.compass.kernel.client.instanceId.AppInstanceID
import com.mastercard.compass.kernel.client.service.KernelServiceConsumer
import com.mastercard.compass.kernel.client.utils.KernelServiceConfigurations
import com.mastercard.compass.kernel.client.utils.PermissionType
import com.mastercard.compass.model.ClientPublicKey
import com.mastercard.compass.model.biometric.BiometricMatchResult
import com.mastercard.compass.model.biometrictoken.FormFactor
import com.mastercard.compass.model.biometrictoken.Modality
import com.mastercard.compass.model.instanceId.ReliantAppInstanceIdRequest
import com.mastercard.compass.model.instanceId.ReliantAppInstanceIdResponse
import io.jsonwebtoken.ExpiredJwtException
import io.jsonwebtoken.MalformedJwtException
import io.jsonwebtoken.UnsupportedJwtException
import io.jsonwebtoken.security.SignatureException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.security.*
import java.security.spec.InvalidKeySpecException
import java.security.spec.PKCS8EncodedKeySpec
import java.security.spec.X509EncodedKeySpec
import java.util.*

/**
 * The Community Pass Helper Class, simplifies Reliant App implementations by offering abstracted
 * methods that perform certain functionality.
 *
 * Version Reference - 0.11.0
 */

interface CompassKernelUIController {
    // region CompassKernelUIController
    /**
     * Returns the Community Pass Instance ID if registered.
     */
    val compassInstanceID: String?
        get() = helper.getInstanceId()

    /**
     * Returns true if there exists an Instance Id
     */
    val hasCompassInstanceID: Boolean
        get() = helper.hasInstanceId()

    /**
     * Returns the JWT Reliant App Key Pair if it exists
     */
    val reliantAppJWTKeyPair: KeyPair
        get() = helper.getReliantAppJWTKeyPair()

    /**
     * Returns the Community Pass Kernel Public Key if it exists
     */
    val kernelJWTPublicKey: PublicKey?
        get() = helper.getKernelJWTPublicKey()

    /**
     * Returns true if there exists a Community Pass Kernel Service Connection
     */
    val hasActiveKernelConnection: Boolean
        get() = connectionViewModel.hasActiveKernelConnection()

    /**
     * Returns the Instance of the Community Pass Kernel Service if it exists. See [connectKernelService]
     * on more information on how to connect to a Community Pass Kernel Service
     */
    val compassKernelServiceInstance: KernelServiceConsumer
        get() = connectionViewModel.compassKernelServiceInstance

    /**
     * View model that holds the [KernelServiceConsumer] instance
     */
    val connectionViewModel: CompassConnectionViewModel

    /**
     * Reliant App GUID to be set by the implementing class
     */
    var reliantAppGUID: String

    /**
     * [AppInstanceID] to be set by the implementing class
     */
    var instance: AppInstanceID

    /**
     * [CompassHelper] to be set by the implementing class
     */
    var helper: CompassHelper

    /**
     * Callback that communicates the response on the Kernel Connection request
     */
    var responseListener: (isSuccess: Boolean, errorCode: Int?, errorMessage: String?) -> Unit

    /**
     * Activity or Fragment [Context]
     */
    var uiContext: Context

    /**
     * Application Context
     */
    var compassApplicationContext: Context

    /**
     * Community Pass Permissions to be requested before communicating with the Reliant Application
     */
    private val compassPermissions
        get() = arrayOf(Permissions.GET_INSTANCE_ID, Permissions.BIND_SERVICE)

    /**
     * Check if rationale is required for the [permission]
     */
    fun shouldShowCompassRequestPermissionRationale(permission: String): Boolean

    /**
     * Request Compass permissions and return result in [onRequestCompassPermissionsResult]
     */
    fun requestCompassPermissions(compassPermissions: Array<String>)

    /**
     * Should be called in the [Activity.onCreate] and/or [Fragment.onCreate] method
     */
    fun create() {
        instance = AppInstanceID.getInstance(compassApplicationContext)
        helper = CompassHelper(uiContext)
    }

    /**
     * Handles compass request response
     */
    fun onRequestCompassPermissionsResult(
        response: Map<String, Boolean>
    ) {
        val notGranted = response.containsValue(false)
        when {
            notGranted -> showSimpleMessage("Permissions not granted.")
            else -> getInstanceIDOrConnectKernelService()
        }
    }

    /**
     * Launch instance id intent and return result in [handleInstanceIdIntentResult]
     */
    fun launchInstanceIdIntent(intent: Intent?)

    /**
     * Handles the instance id result
     */
    fun handleInstanceIdIntentResult(result: ActivityResult) {
        val (isSuccess, message, errorCode) = helper.handleInstanceIdResult(
            result.resultCode,
            result.data
        )
        when (isSuccess) {
            true -> connectKernelServicePermissionsGranted()
            false -> responseListener(isSuccess, errorCode, message)
        }
    }

    /**
     * Attempts to connect to the Community Pass Kernel Service. States of this connection are shared in
     * the [responseListener] methods.
     * See [hasActiveKernelConnection] & [compassKernelServiceInstance]
     */
    fun connectKernelService(
        reliantAppGUID: String,
        responseListener: (isSuccess: Boolean, errorCode: Int?, errorMessage: String?) -> Unit
    ) {
        this.reliantAppGUID = reliantAppGUID
        this.responseListener = responseListener
        connectionViewModel.responseListener = responseListener
        when {
            grantedPermissions() == false -> showRationaleRequestPermissions()
            else -> getInstanceIDOrConnectKernelService()
        }
    }

    fun onKernelDisconnected(disconnectionListener: () -> Unit) {
        connectionViewModel.disconnectionListener = disconnectionListener
    }

    fun showSimpleMessage(message: String) {
        Toast.makeText(uiContext, message, Toast.LENGTH_SHORT).show()
    }

    private fun grantedPermissions() = (ContextCompat.checkSelfPermission(
        uiContext,
        Permissions.GET_INSTANCE_ID
    ) == PackageManager.PERMISSION_GRANTED) && (ContextCompat.checkSelfPermission(
        uiContext,
        Permissions.BIND_SERVICE
    ) == PackageManager.PERMISSION_GRANTED)

    private fun showRationaleRequestPermissions() {
        when {
            shouldShowCompassRequestPermissionRationale(compassPermissions[0]) ||
                    shouldShowCompassRequestPermissionRationale(compassPermissions[1]) -> showPermissionsRationaleDialog()

            else -> requestPermissions()
        }
    }

    private fun getInstanceIDOrConnectKernelService() {
        when (hasCompassInstanceID) {
            false -> getInstanceId()
            true -> connectKernelServicePermissionsGranted()
        }
    }

    private fun getInstanceId() {
        helper.deleteDataStore()
        val publicKey = helper.getReliantAppJWTKeyPair().public
        launchInstanceIdIntent(
            instance.getInstanceIdActivityIntent(
                ReliantAppInstanceIdRequest(
                    reliantAppGUID,
                    ClientPublicKey(publicKey)
                )
            )
        )
    }

    private fun connectKernelServicePermissionsGranted() {
        connectionViewModel.connectKernelServicePermissionsGranted()
    }

    private fun showPermissionsRationaleDialog() {
        AlertDialog.Builder(uiContext).setTitle("Community Pass Kernel Permissions")
            .setMessage("For our app to access Community Pass Services, we require you to grant the permissions in the next dialogs.")
            .setPositiveButton("Continue") { _, _ -> requestPermissions() }
            .setNegativeButton("Cancel") { _, _ -> }
            .create().show()
    }

    private fun requestPermissions() {
        requestCompassPermissions(
            compassPermissions
        )
    }

    //endregion
    class CompassConnectionViewModel(
        private val helper: CompassHelper,
        private val app: Application,
        private val showSimpleMessage: (String) -> Unit
    ) : AndroidViewModel(app) {

        private val compassKernelServiceInstanceDelegate = lazy {
            KernelServiceConsumer.Builder(
                app.applicationContext,
                helper.getInstanceId()!!,
                KernelServiceConfigurations.Builder(PermissionType.RUN_TIME).build()
            ).build()
        }
        val compassKernelServiceInstance: KernelServiceConsumer by compassKernelServiceInstanceDelegate
        var compassConnected: Boolean = false
            private set

        var responseListener: ((isSuccess: Boolean, errorCode: Int?, errorMessage: String?) -> Unit)? =
            null
        var disconnectionListener: (() -> Unit)? = null

        private var serviceConnectionListener: ServiceConnectionListener =
            object : ServiceConnectionListener {
                override fun onServiceConnected() {
                    responseListener?.let { it(true, null, null) }
                    compassConnected = true
                    showSimpleMessage("Connected to Community Pass Services' Kernel")
                }

                override fun onServiceDisconnected() {
                    compassKernelServiceInstance.detachListener()
                    compassConnected = false
                    showSimpleMessage("Disconnected from Community Pass Services' Kernel")
                }

                override fun onUnableToBind(reason: Int) {
                    compassKernelServiceInstance.detachListener()
                    compassKernelServiceInstance.disconnectKernelService()
                    val message = when (reason) {
                        Errors.ERROR_CODE_PROGRAM_CONFIG_DATA_NOT_AVAILABLE -> {
                            helper.deleteDataStore()
                            "Unable to bind. Error Code: $reason. Program Configuration not available in Community Pass Kernel. Please re-fetch instance id"
                        }

                        else -> {
                            "Unable to bind. Error Code: $reason."
                        }
                    }
                    compassConnected = false
                    showSimpleMessage(message)
                    responseListener?.let { it(false, reason, message) }
                }
            }

        override fun onCleared() {
            super.onCleared()
            if (hasActiveKernelConnection()) compassKernelServiceInstance.disconnectKernelService()
        }

        fun connectKernelServicePermissionsGranted() {
            compassKernelServiceInstance.apply {
                connectKernelService()
                attachListener(serviceConnectionListener)
            }
        }

        fun hasActiveKernelConnection(): Boolean =
            compassKernelServiceInstanceDelegate.isInitialized() && compassConnected
    }

    abstract class CompassKernelActivity : AppCompatActivity(), CompassKernelUIController {
        override lateinit var reliantAppGUID: String
        override lateinit var instance: AppInstanceID
        override lateinit var helper: CompassHelper
        override lateinit var responseListener: (isSuccess: Boolean, errorCode: Int?, errorMessage: String?) -> Unit
        override lateinit var uiContext: Context
        override lateinit var compassApplicationContext: Context
        override val connectionViewModel: CompassConnectionViewModel by viewModelsFactory {
            CompassConnectionViewModel(
                helper,
                application,
                ::showSimpleMessage
            )
        }

        private val permissionsStartForActivityResult =
            registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) {
                onRequestCompassPermissionsResult(it)
            }

        private val compassInstanceIDStartForActivityResult =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                handleInstanceIdIntentResult(it)
            }

        override fun shouldShowCompassRequestPermissionRationale(permission: String): Boolean =
            shouldShowRequestPermissionRationale(permission)

        override fun requestCompassPermissions(
            compassPermissions: Array<String>
        ) = permissionsStartForActivityResult.launch(compassPermissions)

        override fun launchInstanceIdIntent(intent: Intent?) {
            compassInstanceIDStartForActivityResult.launch(intent)
        }

        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            uiContext = this
            compassApplicationContext = applicationContext
            create()
        }

    }

    abstract class CompassKernelFragment : Fragment(), CompassKernelUIController {
        override lateinit var reliantAppGUID: String
        override lateinit var instance: AppInstanceID
        override lateinit var helper: CompassHelper
        override lateinit var responseListener: (isSuccess: Boolean, errorCode: Int?, errorMessage: String?) -> Unit
        override lateinit var uiContext: Context
        override lateinit var compassApplicationContext: Context
        override val connectionViewModel: CompassConnectionViewModel by viewModelsFactory {
            CompassConnectionViewModel(
                helper,
                requireActivity().application,
                ::showSimpleMessage
            )
        }

        private var permissionsStartForActivityResult =
            registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) {
                onRequestCompassPermissionsResult(it)
            }

        private val compassInstanceIDStartForActivityResult =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                handleInstanceIdIntentResult(it)
            }

        override fun launchInstanceIdIntent(intent: Intent?) {
            compassInstanceIDStartForActivityResult.launch(intent)
        }

        override fun shouldShowCompassRequestPermissionRationale(permission: String): Boolean =
            shouldShowRequestPermissionRationale(permission)

        override fun requestCompassPermissions(
            compassPermissions: Array<String>
        ) = permissionsStartForActivityResult.launch(compassPermissions)

        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            uiContext = requireContext()
            compassApplicationContext = requireActivity().applicationContext
            create()
        }
    }

    abstract class CompassKernelComposeActivity : ComponentActivity(), CompassKernelUIController {
        override lateinit var reliantAppGUID: String
        override lateinit var instance: AppInstanceID
        override lateinit var helper: CompassHelper
        override lateinit var responseListener: (isSuccess: Boolean, errorCode: Int?, errorMessage: String?) -> Unit
        override lateinit var uiContext: Context
        override lateinit var compassApplicationContext: Context
        override val connectionViewModel: CompassConnectionViewModel by viewModelsFactory {
            CompassConnectionViewModel(
                helper,
                application,
                ::showSimpleMessage
            )
        }

        private val permissionsStartForActivityResult =
            registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) {
                onRequestCompassPermissionsResult(it)
            }

        private val compassInstanceIDStartForActivityResult =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                handleInstanceIdIntentResult(it)
            }

        override fun shouldShowCompassRequestPermissionRationale(permission: String): Boolean =
            shouldShowRequestPermissionRationale(permission)

        override fun requestCompassPermissions(
            compassPermissions: Array<String>
        ) = permissionsStartForActivityResult.launch(compassPermissions)

        override fun launchInstanceIdIntent(intent: Intent?) {
            compassInstanceIDStartForActivityResult.launch(intent)
        }

        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            uiContext = this
            compassApplicationContext = applicationContext
            create()
        }
    }

    //region CompassGettingStartedHelper inner class
    class CompassHelper(context: Context) {

        private val tag = "CompassGettingStartedHelper"

        private val keyStoreWrapper = CompassKeyStoreWrapper()
        private val dataStore = CompassDataStore(context)
        private val jwtHelper by lazy {
            CompassJWTHelper(
                getReliantAppJWTKeyPair().private,
                getKernelJWTPublicKey()!!,
                getKernelGuid()!!
            )
        }

        //region Accessible methods
        /**
         * Returns the JWT Key Pair. Internally generates a Key Pair if it does not exist
         * @return JWT Key Pair
         */
        /**
         * Returns the JWT Key Pair. Internally generates a Key Pair if it does not exist
         * @return JWT Key Pair
         */
        fun getReliantAppJWTKeyPair(): KeyPair = keyStoreWrapper.getKeyPair(KeyAlias.JWT_KEY)

        /**
         * Returns the Shared Space Key Paid. Internally generates a Key Pair if it does not exist
         * @return Shared Space Key Pair
         */
        /**
         * Returns the Shared Space Key Paid. Internally generates a Key Pair if it does not exist
         * @return Shared Space Key Pair
         */
        fun getSharedSpaceKeyPair(): KeyPair = keyStoreWrapper.getKeyPair(KeyAlias.SHARED_SPACE_KEY)
        fun getBuildConfigRSAKeyPair(cryptoPublicKey: String, cryptoPrivateKey: String): KeyPair = keyStoreWrapper.getRSAKeyPair(cryptoPublicKey, cryptoPrivateKey)

        /**
         * Returns the Instance ID if one exists
         * @return Instance Id
         */
        /**
         * Returns the Instance ID if one exists
         * @return Instance Id
         */
        fun getInstanceId(): String? = dataStore.getInstanceId()

        /**
         * Checks if Instance ID is present
         */
        /**
         * Checks if Instance ID is present
         */
        fun hasInstanceId(): Boolean = dataStore.getInstanceId() != null

        /**
         * Returns the Community Pass Kernel JWT Public Key if it exists
         * @return Kernel [PublicKey]
         */
        /**
         * Returns the Community Pass Kernel JWT Public Key if it exists
         * @return Kernel [PublicKey]
         */
        fun getKernelJWTPublicKey(): PublicKey? = dataStore.getServerPublicKey()

        /**
         * Returns the Community Pass Kernel Shared Space Public Key if it exists
         * @return Kernel [PublicKey]
         */
        /**
         * Returns the Community Pass Kernel Shared Space Public Key if it exists
         * @return Kernel [PublicKey]
         */
        fun getKernelSharedSpaceKey(): PublicKey? = dataStore.getSharedSpacePublicKey()

        /**
         * Stores the Community Pass Kernel Shared Space Public Key
         */
        /**
         * Stores the Community Pass Kernel Shared Space Public Key
         */
        fun saveKernelSharedSpaceKey(publicKey: PublicKey) =
            dataStore.saveSharedSpacePublicKey(publicKey)

        /**
         * Returns the Instance ID if one exists
         * @return Instance Id
         */
        /**
         * Returns the Instance ID if one exists
         * @return Instance Id
         */
        fun getKernelGuid(): String? = dataStore.getKernelGuid()

        /**
         * Handles an instance id result from the Community Pass Kernel Service. Stores all the required
         * variables in a shared preference file
         * @param resultCode the resultCode Int value returned in the Activity.registerForActivityResult
         * or onActivityResult callback
         * @param data the Intent data returned in the Activity.registerForActivityResult
         * or onActivityResult callback
         * @return A Triple containing; a Boolean that is true if successful and false if unsuccessful,
         * a String with a message illustrating the state of the response and an int if there was an error
         * while fetching the instance id. The error code ranges can be found in this class [Errors]
         */
        /**
         * Handles an instance id result from the Community Pass Kernel Service. Stores all the required
         * variables in a shared preference file
         * @param resultCode the resultCode Int value returned in the Activity.registerForActivityResult
         * or onActivityResult callback
         * @param data the Intent data returned in the Activity.registerForActivityResult
         * or onActivityResult callback
         * @return A Triple containing; a Boolean that is true if successful and false if unsuccessful,
         * a String with a message illustrating the state of the response and an int if there was an error
         * while fetching the instance id. The error code ranges can be found in this class [Errors]
         */
        fun handleInstanceIdResult(resultCode: Int, data: Intent?): Triple<Boolean, String, Int?> {
            return when (resultCode == Activity.RESULT_OK) {
                true -> {
                    val response =
                        data?.extras?.get(Constants.EXTRA_DATA) as ReliantAppInstanceIdResponse
                    dataStore.saveInstanceIdResponse(response)
                    Triple(true, "Successfully received and stored instance id", null)
                }

                false -> {
                    val errorCode = data?.extras?.getInt(Constants.EXTRA_ERROR_CODE, 0) ?: 0
                    Triple(
                        false,
                        "Error while fetching instance id. Error Code: $errorCode",
                        errorCode
                    )
                }
            }
        }

        /**
         * Generates a biometric JWT signed using the Reliant App private key
         * @return A string representation of the JWT
         */
        /**
         * Generates a biometric JWT signed using the Reliant App private key
         * @return A string representation of the JWT
         */
        fun generateJWT(
            reliantAppGUID: String,
            programGuid: String,
            modalities: List<Modality> = listOf(
                Modality.FACE,
                Modality.LEFT_PALM,
                Modality.RIGHT_PALM
            ),
            formFactor: FormFactor = FormFactor.CARD,
            mwqr: ByteArray? = null
        ): String = jwtHelper.generateJWT(reliantAppGUID, programGuid, modalities, formFactor, mwqr)

        /**
         * Generates a biotoken JWT signed using the Reliant App private key
         * @return A string representation of the JWT
         */
        /**
         * Generates a biotoken JWT signed using the Reliant App private key
         * @return A string representation of the JWT
         */
        fun generateBioTokenJWT(
            reliantAppGUID: String,
            programGuid: String,
            consentId: String,
            modalities: List<Modality> = listOf(
                Modality.FACE,
                Modality.LEFT_PALM,
                Modality.RIGHT_PALM
            )
        ): String =
            jwtHelper.generateBioTokenJWT(reliantAppGUID, programGuid, consentId, modalities)

        /**
         * Parses a biometric JWT signed using the Reliant App private key
         * @return A [CompassJWTResponse] representation of the JWT
         */
        /**
         * Parses a biometric JWT signed using the Reliant App private key
         * @return A [CompassJWTResponse] representation of the JWT
         */
        fun parseJWT(jwt: String): CompassJWTResponse = jwtHelper.parseJWT(jwt)

        /**
         * Parses a bio token JWT signed using the Reliant App private key
         * @return A [RegisterUserForBioTokenResponse] representation of the JWT
         */
        /**
         * Parses a bio token JWT signed using the Reliant App private key
         * @return A [RegisterUserForBioTokenResponse] representation of the JWT
         */
        fun parseBioTokenJWT(jwt: String): RegisterUserForBioTokenResponse =
            jwtHelper.parseBioTokenJWT(jwt)

        /**
         * Deletes all stored values in the data store. Useful while trying to re-fetch the instance id
         */
        /**
         * Deletes all stored values in the data store. Useful while trying to re-fetch the instance id
         */
        fun deleteDataStore() {
            dataStore.deleteAll()
        }

        //endregion

        //region CompassDataStore class
        private inner class CompassDataStore(context: Context) {
            private val preferenceName = "compass_helper"
            private val instanceIdKey = "instance_id"
            private val kernelGuidKey = "kernel_guid"
            private val serverPublicKeyKey = "server_public_key"
            private val serverSharedSpacePublicKey = "shared_space_server_public_key"

            private val sharedPreferences =
                context.getSharedPreferences(preferenceName, Context.MODE_PRIVATE)

            fun saveInstanceIdResponse(response: ReliantAppInstanceIdResponse) =
                sharedPreferences.edit()
                    .putString(instanceIdKey, response.reliantAppInstanceId)
                    .putString(kernelGuidKey, response.serverPublicKey.id)
                    .putString(serverPublicKeyKey, response.serverPublicKey.key.encodeToString())
                    .apply()

            fun saveSharedSpacePublicKey(publicKey: PublicKey) {
                sharedPreferences.edit()
                    .putString(serverSharedSpacePublicKey, publicKey.encodeToString())
                    .apply()
            }

            fun getInstanceId(): String? = getSharedPreferenceString(instanceIdKey)

            fun getKernelGuid(): String? = getSharedPreferenceString(kernelGuidKey)

            fun getServerPublicKey(): PublicKey? = getPublicKey(serverPublicKeyKey)

            fun getSharedSpacePublicKey(): PublicKey? = getPublicKey(serverSharedSpacePublicKey)

            private fun getPublicKey(key: String): PublicKey? {
                val stringValue = getSharedPreferenceString(key)
                return when (stringValue != null) {
                    true -> stringValue.getKey()
                    false -> null
                }
            }

            fun deleteAll() {
                sharedPreferences.edit().clear().apply()
            }

            private fun getSharedPreferenceString(key: String): String? {
                return when (sharedPreferences.contains(key)) {
                    true -> sharedPreferences.getString(key, null)
                    false -> null
                }
            }

            private fun PublicKey.encodeToString(): String? {
                try {
                    val fact = KeyFactory.getInstance(CompassEncodedKeySpec.ALGORITHM)
                    val spec: X509EncodedKeySpec =
                        fact.getKeySpec(this, X509EncodedKeySpec::class.java)
                    return Base64.encodeToString(spec.encoded, Base64.DEFAULT)
                } catch (e: InvalidKeySpecException) {
                    Log.e(tag, e.message ?: "Unknown")
                } catch (e: NoSuchAlgorithmException) {
                    Log.e(tag, e.message ?: "Unknown")
                }
                return null
            }

            private fun String.getKey(): PublicKey? {
                try {
                    val byteKey: ByteArray = Base64.decode(toByteArray(), Base64.DEFAULT)
                    val x509EncodedKeySpec = X509EncodedKeySpec(byteKey)
                    val kf: KeyFactory = KeyFactory.getInstance(CompassEncodedKeySpec.ALGORITHM)
                    return kf.generatePublic(x509EncodedKeySpec)
                } catch (e: Exception) {
                    Log.e(tag, e.message ?: "Unknown")
                }
                return null
            }
        }
        //endregion

        //region CompassKeyStoreWrapper class
        private inner class CompassKeyStoreWrapper {
            private val algorithm = CompassEncodedKeySpec.ALGORITHM
            private val provider = "AndroidKeyStore"

            private val keyStore: KeyStore = createKeyStore()

            fun getKeyPair(alias: KeyAlias): KeyPair {
                val publicKey = keyStore.getCertificate(alias.toString())?.publicKey
                val privateKey = keyStore.getKey(alias.toString(), null) as PrivateKey?

                return when (publicKey == null || privateKey == null) {
                    true -> {
                        deleteEntry(alias.toString())
                        generateKeyPair(alias)

                        val createdPublicKey = keyStore.getCertificate(alias.toString())?.publicKey
                        val createdPrivateKey =
                            keyStore.getKey(alias.toString(), null) as PrivateKey?

                        KeyPair(createdPublicKey, createdPrivateKey)
                    }

                    false -> KeyPair(publicKey, privateKey)
                }
            }

            fun getRSAKeyPair(publicKey: String, privateKey: String): KeyPair{
                val privateKeyBytes = java.util.Base64.getDecoder().decode(privateKey)
                val privateKeySpec = PKCS8EncodedKeySpec(privateKeyBytes)
                val privateKeyFactory = KeyFactory.getInstance("RSA")
                val rsaPrivateKey = privateKeyFactory.generatePrivate(privateKeySpec)

                val publicKeyBytes = java.util.Base64.getDecoder().decode(publicKey)
                val publicKeySpec = X509EncodedKeySpec(publicKeyBytes)
                val publicKeyFactory = KeyFactory.getInstance("RSA")
                val rsaPublicKey = publicKeyFactory.generatePublic(publicKeySpec)

                return KeyPair(rsaPublicKey, rsaPrivateKey)
            }

            private fun createKeyStore() = KeyStore.getInstance(provider).apply {
                load(null)
            }

            private fun generateKeyPair(alias: KeyAlias): KeyPair {
                val algorithmParameterSpec = KeyGenParameterSpec.Builder(
                    alias.toString(),
                    alias.getPurposes()
                ).apply {
                    alias.setProperties(this)
                }.build()

                return KeyPairGenerator.getInstance(algorithm, provider).apply {
                    initialize(algorithmParameterSpec)
                }.generateKeyPair()
            }

            private fun deleteEntry(alias: String) {
                keyStore.deleteEntry(alias)
            }
        }
        //endregion

        //region KeyAlias
        enum class KeyAlias {
            JWT_KEY, SHARED_SPACE_KEY;

            fun getPurposes(): Int {
                return when (this) {
                    JWT_KEY -> KeyProperties.PURPOSE_SIGN or KeyProperties.PURPOSE_VERIFY
                    SHARED_SPACE_KEY -> KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
                }
            }

            fun setProperties(builder: KeyGenParameterSpec.Builder) {
                when (this) {
                    JWT_KEY -> {
                        builder.apply {
                            setSignaturePaddings(KeyProperties.SIGNATURE_PADDING_RSA_PKCS1)
                            setDigests(KeyProperties.DIGEST_SHA256, KeyProperties.DIGEST_SHA1)
                        }
                    }

                    SHARED_SPACE_KEY -> {
                        builder.apply {
                            setBlockModes(KeyProperties.BLOCK_MODE_ECB)
                            setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_RSA_PKCS1)
                        }
                    }
                }
            }
        }
        //endregion

        //region CompassKeyStoreWrapper class
        private inner class CompassJWTHelper(
            private val privateKey: PrivateKey,
            private val publicKey: PublicKey,
            private val kernelGuid: String
        ) {
            fun generateJWT(
                reliantAppGUID: String,
                programGuid: String,
                modalities: List<Modality>,
                formFactor: FormFactor,
                mwqr: ByteArray?
            ): String {

                val calendar = Calendar.getInstance()
                val iat = calendar.time
                val exp =
                    calendar.apply { set(Calendar.MINUTE, calendar.get(Calendar.MINUTE) + 3) }.time

                val builder = JwtRegisterUserRequest.Builder(
                    reliantAppGUID,
                    iat,
                    UUID.randomUUID().toString(),
                    programGuid
                ).exp(exp)
                    .signWith(privateKey)
                    .setModalities(modalities)
                    .setFormFactor(formFactor)

                mwqr?.let { builder.setCpUserProfile(it) }
                return builder.build().compact()
            }

            fun generateBioTokenJWT(
                reliantAppGUID: String,
                programGuid: String,
                consentId: String,
                modalities: List<Modality>
            ): String {
                val calendar = Calendar.getInstance()
                val iat = calendar.time
                val exp =
                    calendar.apply { set(Calendar.MINUTE, calendar.get(Calendar.MINUTE) + 3) }.time

                return JWTRequestModel(
                    payload = RegisterUserForBioTokenRequest(
                        reliantAppGuid = reliantAppGUID,
                        programId = programGuid,
                        biometricConsentId = consentId,
                        modality = modalities,
                        encrypt = true,
                        forcedModalityFlag = true,
                        regWhenDeviceOnline = true
                    ),
                    iss = reliantAppGUID,
                    aud = kernelGuid,
                    iat = iat,
                    jti = UUID.randomUUID().toString(),
                    exp = exp
                ).compact(privateKey)
            }

            fun parseJWT(jwt: String): CompassJWTResponse {
                try {
                    val builder =
                        JwtRegisterUserResponse.ParseBuilder(jwt).setSigningKey(publicKey)
                            .build()
                    val claims = builder.claims()
                    claims?.forEach { t, u ->
                        Log.d("Claims", "parseJWT: $t: $u")
                    }
                    val isMatch = claims!![JwtConstants.CLAIM_MATCH].toString().toYesNoBoolean()
                    val rId = claims.subject
                    val biometricMatchList = builder.getMatchResultClaim(claims[JwtConstants.BIOMETRIC_MATCH_RESULT] as? String)
                    return CompassJWTResponse.Success(isMatch, rId, biometricMatchList)
                } catch (e: Exception) {
                    val error = when (e) {
                        is UnsupportedJwtException -> "Unsupported Request"
                        is MalformedJwtException -> "Malformed Request"
                        is SignatureException -> "Signature Validation Failed"
                        is ExpiredJwtException -> "Expired Request"
                        is IllegalArgumentException -> "Claims not defined"
                        is JwtParseException -> e.message!!
                        else -> "Unknown issue parsing Request"
                    }
                    return CompassJWTResponse.Error(error)
                }
            }

            fun parseBioTokenJWT(jwt: String): RegisterUserForBioTokenResponse {
                val result =
                    JWTResponseParser(publicKey).parseToken<RegisterUserForBioTokenResponse>(jwt)
                return result.payload
            }
        }

        sealed class CompassJWTResponse {
            data class Success(val isMatchFound: Boolean, val rId: String?, val biometricMatchList: List<BiometricMatchResult>?) : CompassJWTResponse()
            data class Error(val message: String) : CompassJWTResponse()
        }

        data class CompassConsentResponse(val status: ConsentStatus?, val result: ConsentResult)

        data class ConsentResult(val communityPassPrivacyPolicyAccepted: Boolean?, val communityPassBiometricNoticeAccepted: Boolean?, val partnerPrivacyPolicyAccepted: Boolean?, var additionalInfo: ConsentAdditionalInfo? = null)

        data class ConsentAdditionalInfo(val consentID: String, val responseStatus: String)

        enum class ConsentStatus { CONSENT_GRANTED, CONSENT_DENIED}
        enum class ActiveConsentList { COMMUNITY_PASS_PRIVACY_POLICY, COMMUNITY_PASS_BIOMETRIC_NOTICE, PARTNER_PRIVACY_POLICY }

        private fun String.toYesNoBoolean(): Boolean = when {
            this.lowercase() == "yes" -> true
            else -> false
        }
        //endregion
    } //endregion

    /**
     * @property partnerPrivacyPolicyTitle              This is the title of the partner privacy policy content that appears in the consent popup. It is visible if the `enablePartnerPrivacyPolicy` is set to true.
     * @property partnerPrivacyPolicyContent            This is the actual full privacy policy content that appears in the consent popup. It is visible if the `enablePartnerPrivacyPolicy` is set to true.
     * @property partnerPrivacyPolicyExcerptTitle       This is the excerpt title of the partner privacy policy. It is visible if the `enablePartnerPrivacyPolicy` is set to true.
     * @property partnerPrivacyPolicyExcerptContent     This is the excerpt content of the partner privacy policy. It is visible if the `enablePartnerPrivacyPolicy` is set to true.
     * @property acceptConsentButtonLabel               This is the label that appears on the accept consent button.
     * @property declineConsentButtonLabel              This is the label that appears on the decline or cancel consent button.
     * @property enableCommunityPassPrivacyPolicy       If true, Community Pass Privacy Policy will be enabled on the consent UI.
     * @property enableBiometricNotice                  If true, Community Pass Biometric Notice will be enabled on the consent UI.
     * @property enablePartnerPrivacyPolicy             If true, Partner Privacy Policy will be enabled on the consent UI.
     * @property acceptConsentButtonLoading             If true, a loading animation is shown on the Accept Consent button.
     * @property declineConsentButtonLoading            If true, a loading animation is shown on the Decline Consent button.
     * @property beforeYouProceedText                   This is the Consent Screen title
     * @property beforeYouProceedFontSize               This is the font-size of the title at the top of the screen.
     * @property consentTitleFontSize                   This is the font-size the consent title that appears in the consent popup.
     * @property consentContentFontSize                 This is the font-size the actual consent text that appear in the consent popup
     * @property switchLabelFontSize                    This is the font-size of the labels next to the 3 consent switches.
     * @property buttonLabelFontSize                    This is the font-size the two buttons.
     * @property buttonBorderRadius                     This is border roundness of the buttons.
     * @property buttonHeight                           This is the height of the buttons.
     * @property darkThemeColorPalette                  A complete definition of the color scheme to be used when the system is in dark mode
     * @property lightThemeColorPalette                 A complete definition of the color scheme to be used when the system is in light mode
     */


    abstract class CompassConsentFragment(
        private val partnerPrivacyPolicyTitle: String = "Partner Privacy Policy",
        private val partnerPrivacyPolicyContent: String = "The following is a placeholder text that should be replaced with an actual partner's privacy policy text. When you enrol to Community Pass, Mastercard creates your digital profile to help you and your service provider access and digitally manage records of the services you receive.\n\nTo create your digital profile, we need information about you, including your name, contact details and date of birth. This is to recognize you from other people and ensure we can uniquely identify you in the system. Mastercard is responsible for your profile data.\n\nWe store it securely in the United States. We only create the digital profile with your consent. You decide how your data is used. At any time, you can ask how we use your data or tell us you no longer want your digital profile to exist. We will delete your profile. If you have any such wish, please contact your agent or any nearby service provider.\n\nWhen you withdraw your consent to Community Pass, you have a 30-day period to change your mind and opt-in again. If you do not opt-in again within 30 days after having withdrawn your consent to Community Pass, we will automatically be deleting your profile.\n\nClick the close button to close the Community Pass Privacy Policy and come back to the above Consent Page.",
        private val partnerPrivacyPolicyExcerptTitle: String = "Consent to Partner Reliant Application",
        private val partnerPrivacyPolicyExcerptContent: String = "Partner consent excerpt: This is a placeholder text that should be replaced with an actual partner's privacy policy text.",
        private val acceptConsentButtonLabel: String = "Accept",
        private val declineConsentButtonLabel: String = "Decline",
        private val enableCommunityPassPrivacyPolicy: Boolean = true,
        private val enableBiometricNotice: Boolean = true,
        private val enablePartnerPrivacyPolicy: Boolean = true,
        private val acceptConsentButtonLoading: MutableState<Boolean> = mutableStateOf(value = false),
        private val declineConsentButtonLoading: MutableState<Boolean> = mutableStateOf(value = false),
        private val beforeYouProceedText: String = "BEFORE YOU PROCEED",
        private val beforeYouProceedFontSize: TextUnit = 18.sp,
        private val consentTitleFontSize: TextUnit = 16.sp,
        private val consentContentFontSize: TextUnit = 14.sp,
        private val switchLabelFontSize: TextUnit = 14.sp,
        private val buttonLabelFontSize: TextUnit = 14.sp,
        private val buttonBorderRadius: Dp = ButtonDefaults.MinHeight / 2,
        private val buttonHeight: Dp = ButtonDefaults.MinHeight,
        private val darkThemeColorPalette: ColorScheme = darkColorScheme(
            primary = Color(0xFFFF671B),
            onPrimary = Color.White,
            primaryContainer = Color(0xFF333333),
            onPrimaryContainer = Color(0x99FFFFFF),
            background = Color.Black,
            onBackground = Color(0x99FFFFFF),
            tertiaryContainer = Color(0xFFFFE1D1)
        ),
        private val lightThemeColorPalette: ColorScheme = lightColorScheme(
            primary = Color(0xFFFF671B),
            onPrimary = Color.White,
            primaryContainer = Color.White,
            onPrimaryContainer = Color.Black,
            background = Color(0xFFFFE1D1),
            onBackground = Color.Black,
            tertiaryContainer = Color(0xFFFFE1D1)
        ),
    ) : CompassKernelFragment() {
        abstract fun consentCallback(
            isCommunityPassPrivacyPolicyAccepted: Boolean,
            isCommunityPassBiometricNoticeAccepted: Boolean,
            isPartnerPrivacyPolicyAccepted: Boolean,
            listOfEnabledConsent: List<CompassHelper.ActiveConsentList>,
            isRepeat: Boolean,
            flag: CompassHelper.ConsentStatus?
        )

        override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
        ): View {
            return ComposeView(requireContext()).apply {
                setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)

                setContent {
                    val communityPassPrivacyPolicy = rememberSaveable { mutableStateOf(false) }
                    val communityPassBiometricNotice = rememberSaveable { mutableStateOf(false) }
                    val partnerPrivacyPolicy = rememberSaveable { mutableStateOf(false) }
                    val showCommunityPassPrivacyPolicyPopup = remember { mutableStateOf(false) }
                    val showCommunityPassBiometricNoticePopup = remember { mutableStateOf(false) }
                    val showPartnerPrivacyPolicyPopup = remember { mutableStateOf(false) }
                    val continueButtonLoading = remember { mutableStateOf(true) }
                    val consentFlag = rememberSaveable { mutableStateOf<CompassHelper.ConsentStatus?>(null) }
                    val isRepeat = rememberSaveable { mutableStateOf(consentFlag.value != null) }

                    val listOfEnabledConsent = mutableListOf<CompassHelper.ActiveConsentList>()
                    if (enableCommunityPassPrivacyPolicy) listOfEnabledConsent.add(CompassHelper.ActiveConsentList.COMMUNITY_PASS_PRIVACY_POLICY)
                    if (enableBiometricNotice) listOfEnabledConsent.add(CompassHelper.ActiveConsentList.COMMUNITY_PASS_BIOMETRIC_NOTICE)
                    if (enablePartnerPrivacyPolicy) listOfEnabledConsent.add(CompassHelper.ActiveConsentList.PARTNER_PRIVACY_POLICY)

                    CompassConsentUITheme(
                        isDarkTheme = isSystemInDarkTheme(),
                        darkThemeColorPalette = darkThemeColorPalette,
                        lightThemeColorPalette = lightThemeColorPalette,
                        content = {
                            ConsentScreen(
                                grantedCommunityPassBiometricNotice = communityPassBiometricNotice,
                                grantedCommunityPassPrivacyPolicy = communityPassPrivacyPolicy,
                                grantedPartnerPrivacyPolicy = partnerPrivacyPolicy,
                                shouldShowCommunityPassBiometricNoticePopup = showCommunityPassBiometricNoticePopup,
                                shouldShowCommunityPassPrivacyPolicyPopup = showCommunityPassPrivacyPolicyPopup,
                                shouldShowPartnerPrivacyPolicyPopup = showPartnerPrivacyPolicyPopup,
                                beforeYouProceedText = beforeYouProceedText,
                                beforeYouProceedFontSize = beforeYouProceedFontSize,
                                consentTitleFontSize = consentTitleFontSize,
                                consentContentFontSize = consentContentFontSize,
                                switchLabelFontSize = switchLabelFontSize,
                                buttonLabelFontSize = buttonLabelFontSize,
                                buttonBorderRadius = buttonBorderRadius,
                                buttonHeight = buttonHeight,
                                partnerPrivacyPolicyTitle = partnerPrivacyPolicyTitle,
                                partnerPrivacyPolicyContent = partnerPrivacyPolicyContent,
                                partnerPrivacyPolicyExcerptTitle = partnerPrivacyPolicyExcerptTitle,
                                partnerPrivacyPolicyExcerptContent = partnerPrivacyPolicyExcerptContent,
                                enableBiometricNotice = enableBiometricNotice,
                                enablePartnerPrivacyPolicy = enablePartnerPrivacyPolicy,
                                enableCommunityPassPrivacyPolicy = enableCommunityPassPrivacyPolicy,
                                acceptConsentButtonLabel = acceptConsentButtonLabel,
                                declineConsentButtonLabel = declineConsentButtonLabel,
                                acceptConsentButtonLoading = acceptConsentButtonLoading,
                                continueButtonLoading = continueButtonLoading,
                                declineConsentButtonLoading = declineConsentButtonLoading,
                                consentFlag = consentFlag,
                                isRepeat = isRepeat,
                                consentCallback = {
                                    consentCallback(
                                        isCommunityPassPrivacyPolicyAccepted = communityPassPrivacyPolicy.value,
                                        isCommunityPassBiometricNoticeAccepted = communityPassBiometricNotice.value,
                                        isPartnerPrivacyPolicyAccepted = partnerPrivacyPolicy.value,
                                        listOfEnabledConsent = listOfEnabledConsent,
                                        isRepeat = isRepeat.value,
                                        flag = consentFlag.value
                                    )
                                },
                                shouldEnableButton = { enableCommunityPassConsent: Boolean, enableBiometricConsent: Boolean, enablePartnerConsent: Boolean, communityPassPrivacyPolicy: Boolean, communityPassBiometricNotice: Boolean, partnerPrivacyPolicy: Boolean ->
                                    shouldEnableButton(
                                        enableCommunityPassConsent = enableCommunityPassConsent,
                                        enableBiometricConsent = enableBiometricConsent,
                                        enablePartnerConsent = enablePartnerConsent,
                                        grantedCommunityPassPrivacyPolicy = communityPassPrivacyPolicy,
                                        grantedCommunityPassBiometricNotice = communityPassBiometricNotice,
                                        grantedPartnerPrivacyPolicy = partnerPrivacyPolicy
                                    )
                                },
                                scope = lifecycleScope
                            )
                        }
                    )
                }
            }
        }
    }
}

fun shouldEnableButton(
    enableCommunityPassConsent: Boolean,
    enableBiometricConsent: Boolean,
    enablePartnerConsent: Boolean,
    grantedCommunityPassPrivacyPolicy: Boolean,
    grantedCommunityPassBiometricNotice: Boolean,
    grantedPartnerPrivacyPolicy: Boolean
): Boolean {
    val mapOfActiveConsent = mutableMapOf<String, Boolean>()
    if (enableCommunityPassConsent) mapOfActiveConsent["grantedCommunityPassPrivacyPolicy"] =
        grantedCommunityPassPrivacyPolicy
    if (enableBiometricConsent) mapOfActiveConsent["grantedCommunityPassBiometricNotice"] =
        grantedCommunityPassBiometricNotice
    if (enablePartnerConsent) mapOfActiveConsent["grantedPartnerPrivacyPolicy"] =
        grantedPartnerPrivacyPolicy
    return !mapOfActiveConsent.containsValue(false)
}


/**
 * @param partnerPrivacyPolicyTitle              This is the title of the partner privacy policy content that appears in the consent popup. It is visible if the `enablePartnerPrivacyPolicy` is set to true.
 * @param partnerPrivacyPolicyContent            This is the actual full privacy policy content that appears in the consent popup. It is visible if the `enablePartnerPrivacyPolicy` is set to true.
 * @param partnerPrivacyPolicyExcerptTitle       This is the excerpt title of the partner privacy policy. It is visible if the `enablePartnerPrivacyPolicy` is set to true.
 * @param partnerPrivacyPolicyExcerptContent     This is the excerpt content of the partner privacy policy. It is visible if the `enablePartnerPrivacyPolicy` is set to true.
 * @param acceptConsentButtonLabel               This is the label that appears on the accept consent button.
 * @param declineConsentButtonLabel              This is the label that appears on the decline or cancel consent button.
 * @param enableCommunityPassPrivacyPolicy       If true, Community Pass Privacy Policy will be enabled on the consent UI.
 * @param enableBiometricNotice                  If true, Community Pass Biometric Notice will be enabled on the consent UI.
 * @param enablePartnerPrivacyPolicy             If true, Partner Privacy Policy will be enabled on the consent UI.
 * @param acceptConsentButtonLoading             If true, a loading animation is shown on the Accept Consent button.
 * @param declineConsentButtonLoading            If true, a loading animation is shown on the Decline Consent button.
 * @param beforeYouProceedText                   This is the Consent Screen title
 * @param beforeYouProceedFontSize               This is the font-size of the title at the top of the screen.
 * @param consentTitleFontSize                   This is the font-size the consent title that appears in the consent popup.
 * @param consentContentFontSize                 This is the font-size the actual consent text that appear in the consent popup
 * @param switchLabelFontSize                    This is the font-size of the labels next to the 3 consent switches.
 * @param buttonLabelFontSize                    This is the font-size the two buttons.
 * @param buttonBorderRadius                     This is border roundness of the buttons.
 * @param buttonHeight                           This is the height of the buttons.
 */

@Composable
fun ConsentScreen(
    grantedCommunityPassPrivacyPolicy: MutableState<Boolean>,
    grantedCommunityPassBiometricNotice: MutableState<Boolean>,
    grantedPartnerPrivacyPolicy: MutableState<Boolean>,
    shouldShowCommunityPassPrivacyPolicyPopup: MutableState<Boolean>,
    shouldShowCommunityPassBiometricNoticePopup: MutableState<Boolean>,
    shouldShowPartnerPrivacyPolicyPopup: MutableState<Boolean>,
    beforeYouProceedText: String,
    beforeYouProceedFontSize: TextUnit,
    consentTitleFontSize: TextUnit,
    consentContentFontSize: TextUnit,
    switchLabelFontSize: TextUnit,
    buttonLabelFontSize: TextUnit,
    buttonBorderRadius: Dp,
    buttonHeight: Dp,
    partnerPrivacyPolicyTitle: String,
    partnerPrivacyPolicyContent: String,
    partnerPrivacyPolicyExcerptTitle: String,
    partnerPrivacyPolicyExcerptContent: String,
    enableBiometricNotice: Boolean,
    enablePartnerPrivacyPolicy: Boolean,
    enableCommunityPassPrivacyPolicy: Boolean,
    acceptConsentButtonLabel: String,
    declineConsentButtonLabel: String,
    acceptConsentButtonLoading: MutableState<Boolean>,
    declineConsentButtonLoading: MutableState<Boolean>,
    continueButtonLoading: MutableState<Boolean>,
    consentFlag: MutableState<CompassKernelUIController.CompassHelper.ConsentStatus?>,
    isRepeat: MutableState<Boolean>,
    consentCallback: () -> Unit,
    shouldEnableButton: (
        enableCommunityPassConsent : Boolean,
        enableBiometricConsent: Boolean,
        enablePartnerConsent: Boolean,
        grantedCommunityPassPrivacyPolicy : Boolean,
        grantedCommunityPassBiometricNotice: Boolean,
        grantedPartnerPrivacyPolicy: Boolean
    ) -> Boolean,
    scope: CoroutineScope
) {
    Column(
        modifier = Modifier
            .background(MaterialTheme.colorScheme.background)
            .blur(if (shouldShowCommunityPassBiometricNoticePopup.value || shouldShowCommunityPassPrivacyPolicyPopup.value || shouldShowPartnerPrivacyPolicyPopup.value) 5.dp else 0.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    start = 20.dp,
                    end = 20.dp,
                    top = 20.dp,
                    bottom = 20.dp
                )
        ) {
            Text(
                modifier = Modifier.fillMaxWidth(),
                fontWeight = FontWeight(700),
                color = MaterialTheme.colorScheme.onBackground,
                fontSize = beforeYouProceedFontSize,
                text = beforeYouProceedText,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(20.dp))
            Surface(
                shape = RoundedCornerShape(10.dp),
                color = MaterialTheme.colorScheme.primaryContainer,
                tonalElevation = 5.dp,
                shadowElevation = 3.dp
            ) {
                Column(
                    modifier = Modifier.padding(20.dp)
                ) {
                    if (enableCommunityPassPrivacyPolicy) {
                        Text(
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            fontSize = consentTitleFontSize,
                            fontWeight = FontWeight(700),
                            text = "Consent to Community Pass"
                        )
                        Text(
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            fontSize = consentContentFontSize,
                            text = "You are enrolling to Community Pass, which enables you to keep a digital record of essential services you receive. To create your digital profile in Community Pass, Mastercard needs your personal information, as explained in the Community Pass Privacy Policy."
                        )
                        Spacer(modifier = Modifier.height(20.dp))
                    }
                    if (enableBiometricNotice) {
                        Text(
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            fontSize = consentTitleFontSize,
                            fontWeight = FontWeight(700),
                            text = "Biometric Notice"
                        )
                        Text(
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            fontSize = consentContentFontSize,
                            text = "When you register to Community Pass with biometric, your biometric data will be extracted securely so you can be automatically identified on subsequent uses."
                        )
                        Spacer(modifier = Modifier.height(20.dp))
                    }

                    if (enablePartnerPrivacyPolicy) {
                        Text(
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            fontSize = consentTitleFontSize,
                            fontWeight = FontWeight(700),
                            text = partnerPrivacyPolicyExcerptTitle
                        )
                        Text(
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            fontSize = consentContentFontSize,
                            text = partnerPrivacyPolicyExcerptContent
                        )
                    }
                }
            }
        }
        if (!isRepeat.value || acceptConsentButtonLoading.value || declineConsentButtonLoading.value) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
            ) {
                if (enableCommunityPassPrivacyPolicy) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Start,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Switch(
                            checked = grantedCommunityPassPrivacyPolicy.value,
                            onCheckedChange = { grantedCommunityPassPrivacyPolicy.value = it },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = MaterialTheme.colorScheme.primary,
                                checkedTrackColor = MaterialTheme.colorScheme.tertiaryContainer,
                                checkedBorderColor = MaterialTheme.colorScheme.primary,
                                checkedIconColor = Color.White,
                                uncheckedThumbColor = Color.Gray,
                                uncheckedTrackColor = Color.LightGray,
                                uncheckedBorderColor = Color.Gray
                            ),
                            thumbContent = if (grantedCommunityPassPrivacyPolicy.value) {
                                {
                                    Icon(
                                        imageVector = Icons.Filled.Check,
                                        contentDescription = null,
                                        modifier = Modifier.size(SwitchDefaults.IconSize),
                                    )
                                }
                            } else {
                                null
                            }
                        )
                        Spacer(modifier = Modifier.width(15.dp))
                        val communityPassPrivacyPolicyLabel = "Community Pass Privacy Policy"
                        val communityPassPrivacyPolicyAnnotatedString = buildAnnotatedString {
                            append("I agree to the ")
                            withStyle(style = SpanStyle(textDecoration = TextDecoration.Underline)) {
                                pushStringAnnotation(
                                    tag = communityPassPrivacyPolicyLabel,
                                    annotation = communityPassPrivacyPolicyLabel
                                )
                                append(communityPassPrivacyPolicyLabel)
                            }
                        }
                        ClickableText(
                            text = communityPassPrivacyPolicyAnnotatedString,
                            style = TextStyle(
                                fontSize = switchLabelFontSize,
                                color = MaterialTheme.colorScheme.onBackground
                            ),
                            onClick = { offset ->
                                scope.launch(Dispatchers.Main) {
                                    communityPassPrivacyPolicyAnnotatedString.getStringAnnotations(
                                        offset,
                                        offset
                                    ).firstOrNull()?.let {
                                        shouldShowCommunityPassPrivacyPolicyPopup.value = true
                                    }
                                }
                            }
                        )
                    }
                    Spacer(modifier = Modifier.height(15.dp))
                }
                if (enableBiometricNotice) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Start,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Switch(
                            checked = grantedCommunityPassBiometricNotice.value,
                            onCheckedChange = {
                                grantedCommunityPassBiometricNotice.value = it
                            },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = MaterialTheme.colorScheme.primary,
                                checkedTrackColor = MaterialTheme.colorScheme.tertiaryContainer,
                                checkedBorderColor = MaterialTheme.colorScheme.primary,
                                checkedIconColor = Color.White,
                                uncheckedThumbColor = Color.Gray,
                                uncheckedTrackColor = Color.LightGray,
                                uncheckedBorderColor = Color.Gray
                            ),
                            thumbContent = if (grantedCommunityPassBiometricNotice.value) {
                                {
                                    Icon(
                                        imageVector = Icons.Filled.Check,
                                        contentDescription = null,
                                        modifier = Modifier.size(SwitchDefaults.IconSize),
                                    )
                                }
                            } else {
                                null
                            }
                        )
                        Spacer(modifier = Modifier.width(15.dp))
                        val communityPassBiometricNoticeLabel =
                            "Community Pass Biometric Notice"
                        val communityPassBiometricNoticeAnnotatedString = buildAnnotatedString {
                            append("I consent to my biometric details to be captured as explained in the ")
                            withStyle(style = SpanStyle(textDecoration = TextDecoration.Underline)) {
                                pushStringAnnotation(
                                    tag = communityPassBiometricNoticeLabel,
                                    annotation = communityPassBiometricNoticeLabel
                                )
                                append(communityPassBiometricNoticeLabel)
                            }
                        }
                        ClickableText(
                            text = communityPassBiometricNoticeAnnotatedString,
                            style = TextStyle(
                                fontSize = switchLabelFontSize,
                                color = MaterialTheme.colorScheme.onBackground
                            ),
                            onClick = { offset ->
                                communityPassBiometricNoticeAnnotatedString.getStringAnnotations(
                                    offset,
                                    offset
                                )
                                    .firstOrNull()?.let {
                                        shouldShowCommunityPassBiometricNoticePopup.value = true
                                    }
                            })
                    }
                    Spacer(modifier = Modifier.height(15.dp))
                }
                if (enablePartnerPrivacyPolicy) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Start,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Switch(
                            checked = grantedPartnerPrivacyPolicy.value,
                            onCheckedChange = { grantedPartnerPrivacyPolicy.value = it },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = MaterialTheme.colorScheme.primary,
                                checkedTrackColor = MaterialTheme.colorScheme.tertiaryContainer,
                                checkedBorderColor = MaterialTheme.colorScheme.primary,
                                checkedIconColor = Color.White,
                                uncheckedThumbColor = Color.Gray,
                                uncheckedTrackColor = Color.LightGray,
                                uncheckedBorderColor = Color.Gray
                            ),
                            thumbContent = if (grantedPartnerPrivacyPolicy.value) {
                                {
                                    Icon(
                                        imageVector = Icons.Filled.Check,
                                        contentDescription = null,
                                        modifier = Modifier.size(SwitchDefaults.IconSize),
                                    )
                                }
                            } else {
                                null
                            }
                        )
                        Spacer(modifier = Modifier.width(15.dp))

                        val partnerPrivacyPolicyAnnotatedString = buildAnnotatedString {
                            append("I agree to the capture of my personal information according to the ")
                            withStyle(style = SpanStyle(textDecoration = TextDecoration.Underline)) {
                                pushStringAnnotation(
                                    tag = partnerPrivacyPolicyTitle,
                                    annotation = partnerPrivacyPolicyTitle
                                )
                                append(partnerPrivacyPolicyTitle)
                            }
                        }
                        ClickableText(
                            text = partnerPrivacyPolicyAnnotatedString,
                            style = TextStyle(
                                fontSize = switchLabelFontSize,
                                color = MaterialTheme.colorScheme.onBackground
                            ),
                            onClick = { offset ->
                                partnerPrivacyPolicyAnnotatedString.getStringAnnotations(
                                    offset,
                                    offset
                                )
                                    .firstOrNull()?.let {
                                        shouldShowPartnerPrivacyPolicyPopup.value = true
                                    }
                            })
                    }
                }
            }
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                Button(
                    shape = RoundedCornerShape(size = buttonBorderRadius),
                    modifier = Modifier
                        .height(buttonHeight)
                        .fillMaxWidth(),
                    enabled = !acceptConsentButtonLoading.value && shouldEnableButton(
                        enableCommunityPassPrivacyPolicy,
                        enableBiometricNotice,
                        enablePartnerPrivacyPolicy,
                        grantedCommunityPassPrivacyPolicy.value,
                        grantedCommunityPassBiometricNotice.value,
                        grantedPartnerPrivacyPolicy.value
                    ),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = Color.White,
                        disabledContainerColor = Color.LightGray,
                        disabledContentColor = Color.Gray
                    ),
                    onClick = {
                        scope.launch(Dispatchers.IO) {
                            withContext(Dispatchers.Main) {
                                acceptConsentButtonLoading.value = true
                            }
                            consentFlag.value =
                                CompassKernelUIController.CompassHelper.ConsentStatus.CONSENT_GRANTED
                            consentCallback()
                            delay(1000)
                            acceptConsentButtonLoading.value = false
                            isRepeat.value = true
                        }
                    }
                ) {
                    if (acceptConsentButtonLoading.value) {
                        CircularProgressIndicator(
                            modifier = Modifier.then(Modifier.size(18.dp)),
                            color = Color.White,
                            strokeWidth = 3.dp
                        )
                    } else {
                        Text(
                            fontSize = buttonLabelFontSize,
                            text = acceptConsentButtonLabel
                        )
                    }
                }
                Spacer(modifier = Modifier.height(20.dp))
                Button(
                    shape = RoundedCornerShape(size = buttonBorderRadius),
                    modifier = Modifier
                        .height(buttonHeight)
                        .fillMaxWidth(),
                    enabled = !declineConsentButtonLoading.value,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.White,
                        contentColor = Color.Black,
                        disabledContainerColor = Color.LightGray,
                        disabledContentColor = Color.Gray
                    ),
                    onClick = {
                        scope.launch(Dispatchers.IO) {
                            withContext(Dispatchers.Main) {
                                declineConsentButtonLoading.value = true
                            }
                            consentFlag.value =
                                CompassKernelUIController.CompassHelper.ConsentStatus.CONSENT_DENIED
                            grantedCommunityPassPrivacyPolicy.value = false
                            grantedCommunityPassBiometricNotice.value = false
                            grantedPartnerPrivacyPolicy.value = false
                            consentCallback()
                            delay(1000)
                            declineConsentButtonLoading.value = false
                            isRepeat.value = true
                        }
                    }
                ) {
                    Text(
                        fontSize = buttonLabelFontSize,
                        text = declineConsentButtonLabel
                    )
                }
            }
            Spacer(modifier = Modifier.height(30.dp))
        } else {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 20.dp, end = 20.dp, top = 0.dp, bottom = 20.dp)
            ) {
                Text(
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center,
                    text = buildAnnotatedString {
                        append("User responded ")
                        withStyle(style = SpanStyle(color = MaterialTheme.colorScheme.primary)) {
                            append(if (consentFlag.value == CompassKernelUIController.CompassHelper.ConsentStatus.CONSENT_DENIED) declineConsentButtonLabel else acceptConsentButtonLabel)
                        }
                        append(" when asked to give consent.")
                    }
                )
                Spacer(modifier = Modifier.height(20.dp))
                Button(
                    shape = RoundedCornerShape(size = buttonBorderRadius),
                    modifier = Modifier
                        .height(buttonHeight)
                        .fillMaxWidth(),
                    enabled = continueButtonLoading.value,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = Color.White,
                        disabledContainerColor = Color.LightGray,
                        disabledContentColor = Color.Gray
                    ),
                    onClick = {
                        scope.launch(Dispatchers.IO) {
                            consentCallback()
                            continueButtonLoading.value = false
                        }
                    }
                ) {
                    Text(
                        fontSize = buttonLabelFontSize,
                        text = "Continue"
                    )
                }
                Spacer(modifier = Modifier.height(30.dp))
            }
        }
    }

    ConsentDialog(
        shouldShowDialog = shouldShowCommunityPassPrivacyPolicyPopup,
        title = "Community Pass Privacy Policy",
        content = "When you enrol to Community Pass, Mastercard creates your digital profile to help you and your service provider access and digitally manage records of the services you receive.\n\nTo create your digital profile, we need information about you, including your name, contact details and date of birth. This is to recognize you from other people and ensure we can uniquely identify you in the system. Mastercard is responsible for your profile data.\n\nWe store it securely in the United States. We only create the digital profile with your consent. You decide how your data is used. At any time, you can ask how we use your data or tell us you no longer want your digital profile to exist. We will delete your profile. If you have any such wish, please contact your agent or any nearby service provider.\n\nWhen you withdraw your consent to Community Pass, you have a 30-day period to change your mind and opt-in again. If you do not opt-in again within 30 days after having withdrawn your consent to Community Pass, we will automatically be deleting your profile. \n\nClick the close button to close the Community Pass Privacy Policy and go back to the Consent Page.",
        titleFontSize = consentTitleFontSize,
        contentFontSize = consentContentFontSize,
        buttonLabelFontSize = buttonLabelFontSize,
        buttonBorderRadius = buttonBorderRadius,
        buttonHeight = buttonHeight
    )
    ConsentDialog(
        shouldShowDialog = shouldShowCommunityPassBiometricNoticePopup,
        title = "Biometric Privacy Notice",
        content = "When you register to Community Pass with biometric, your biometric data will be securely extracted, and a unique, irreversible and hashed token will be generated so you can be automatically identified on subsequent uses, both with and without an internet connection.\n\nYour biometric photos will never be stored and your secured biometric token will be kept until your relationship with Community Pass is ended or you remove your consent, at which time it will be destroyed securely. Your biometric token will never be sold or shared.\n\nWhen you withdraw your consent to biometric, you have a 7-day period to change your mind and opt-in again. If you do not opt-in again within 7 days after having withdrawn your consent to biometric, we will be deleting your biometric token.\n\nClick the close button to close the Biometric Privacy Notice and go back to the Consent Page.",
        titleFontSize = consentTitleFontSize,
        contentFontSize = consentContentFontSize,
        buttonLabelFontSize = buttonLabelFontSize,
        buttonBorderRadius = buttonBorderRadius,
        buttonHeight = buttonHeight
    )
    ConsentDialog(
        shouldShowDialog = shouldShowPartnerPrivacyPolicyPopup,
        title = partnerPrivacyPolicyTitle,
        content = partnerPrivacyPolicyContent,
        titleFontSize = consentTitleFontSize,
        contentFontSize = consentContentFontSize,
        buttonLabelFontSize = buttonLabelFontSize,
        buttonBorderRadius = buttonBorderRadius,
        buttonHeight = buttonHeight,
    )
}

@Composable
private fun ConsentDialog(
    shouldShowDialog: MutableState<Boolean>,
    title: String,
    content: String,
    titleFontSize: TextUnit,
    contentFontSize: TextUnit,
    buttonLabelFontSize: TextUnit,
    buttonBorderRadius: Dp,
    buttonHeight: Dp
) {
    if (shouldShowDialog.value) {
        Dialog(
            properties = DialogProperties(
                dismissOnBackPress = true,
                dismissOnClickOutside = false,
            ),
            onDismissRequest = { shouldShowDialog.value = false }
        ) {
            Column(
                modifier = Modifier
                    .background(color = MaterialTheme.colorScheme.primaryContainer, shape = RoundedCornerShape(20.dp))
                    .clip(RoundedCornerShape(20.dp))
                    .padding(horizontal = 20.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                Spacer(modifier = Modifier.height(20.dp))
                Text(
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    fontWeight = FontWeight(700),
                    fontSize = titleFontSize, text = title
                )
                Spacer(modifier = Modifier.height(10.dp))
                Divider(
                    modifier = Modifier.fillMaxWidth(),
                    color = Color.LightGray,
                    thickness = 1.dp
                )
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    modifier = Modifier.padding(0.dp, 0.dp, 0.dp, 20.dp),
                    text = content,
                    fontSize = contentFontSize
                )
                Button(
                    shape = RoundedCornerShape(size = buttonBorderRadius),
                    modifier = Modifier
                        .height(buttonHeight)
                        .fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = Color.White,
                        disabledContainerColor = Color.LightGray,
                        disabledContentColor = Color.Gray
                    ),
                    onClick = { shouldShowDialog.value = false }
                ) {
                    Text(
                        fontSize = buttonLabelFontSize,
                        text = "Close"
                    )
                }
                Spacer(modifier = Modifier.height(20.dp))
            }
        }
    }
}


/**
 * @param isDarkTheme A boolean value that indicates the system is in dark or light mode.
 * @param darkThemeColorPalette A complete definition of the color palette to be used when the system is in dark mode
 * @param lightThemeColorPalette A complete definition of the color palette to be used when the system is in light mode
 */
@Composable
fun CompassConsentUITheme(
    isDarkTheme: Boolean = isSystemInDarkTheme(),
    darkThemeColorPalette: ColorScheme,
    lightThemeColorPalette: ColorScheme,
    content: @Composable () -> Unit
) {

    val colors = if (isDarkTheme) {
        darkThemeColorPalette
    } else {
        lightThemeColorPalette
    }

    MaterialTheme(
        colorScheme = colors,
        content = content,
    )
}

suspend fun saveAllCommunityPassConsent(
    isCommunityPassPrivacyPolicyAccepted: Boolean,
    isCommunityPassBiometricNoticeAccepted: Boolean,
    isPartnerPrivacyPolicyAccepted: Boolean,
    listOfEnabledConsent: List<CompassKernelUIController.CompassHelper.ActiveConsentList>,
    flag: CompassKernelUIController.CompassHelper.ConsentStatus?
): CompassKernelUIController.CompassHelper.CompassConsentResponse = coroutineScope {
    return@coroutineScope CompassKernelUIController.CompassHelper.CompassConsentResponse(
        status = flag,
        result = CompassKernelUIController.CompassHelper.ConsentResult(
            communityPassPrivacyPolicyAccepted = if (!listOfEnabledConsent.contains(
                    CompassKernelUIController.CompassHelper.ActiveConsentList.COMMUNITY_PASS_PRIVACY_POLICY
                )
            ) null else isCommunityPassPrivacyPolicyAccepted,
            communityPassBiometricNoticeAccepted = if (!listOfEnabledConsent.contains(
                    CompassKernelUIController.CompassHelper.ActiveConsentList.COMMUNITY_PASS_BIOMETRIC_NOTICE
                )
            ) null else isCommunityPassBiometricNoticeAccepted,
            partnerPrivacyPolicyAccepted = if (!listOfEnabledConsent.contains(
                    CompassKernelUIController.CompassHelper.ActiveConsentList.PARTNER_PRIVACY_POLICY
                )
            ) null else isPartnerPrivacyPolicyAccepted
        ),
    )
}

inline fun <reified T : ViewModel> Fragment.viewModelsFactory(crossinline viewModelInitialization: () -> CompassKernelUIController.CompassConnectionViewModel): Lazy<T> {
    return activityViewModels {
        object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                @Suppress("UNCHECKED_CAST")
                return viewModelInitialization.invoke() as T
            }
        }
    }
}

inline fun <reified T : ViewModel> AppCompatActivity.viewModelsFactory(crossinline viewModelInitialization: () -> CompassKernelUIController.CompassConnectionViewModel): Lazy<T> {
    return viewModels {
        object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                @Suppress("UNCHECKED_CAST")
                return viewModelInitialization.invoke() as T
            }
        }
    }
}

inline fun <reified T : ViewModel> ComponentActivity.viewModelsFactory(crossinline viewModelInitialization: () -> T): Lazy<T> {
    return viewModels {
        object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                @Suppress("UNCHECKED_CAST")
                return viewModelInitialization.invoke() as T
            }
        }
    }
}
