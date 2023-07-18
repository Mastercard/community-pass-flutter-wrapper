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
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.mastercard.compass.base.Constants
import com.mastercard.compass.base.Errors
import com.mastercard.compass.base.ServiceConnectionListener
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
import java.security.*
import java.security.spec.InvalidKeySpecException
import java.security.spec.X509EncodedKeySpec
import java.util.*

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
    fun handleInstanceIdIntentResult(result: ActivityResult){
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

    fun onKernelDisconnected(disconnectionListener: () -> Unit){
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

        var responseListener: ((isSuccess: Boolean, errorCode: Int?, errorMessage: String?) -> Unit)? = null
        var disconnectionListener: (() -> Unit)? = null

        private var serviceConnectionListener: ServiceConnectionListener = object : ServiceConnectionListener {
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
            if(hasActiveKernelConnection()) compassKernelServiceInstance.disconnectKernelService()
        }

        fun connectKernelServicePermissionsGranted() {
            compassKernelServiceInstance.apply {
                connectKernelService()
                attachListener(serviceConnectionListener)
            }
        }

        fun hasActiveKernelConnection(): Boolean = compassKernelServiceInstanceDelegate.isInitialized() && compassConnected
    }

    abstract class CompassKernelActivity : AppCompatActivity(), CompassKernelUIController {
        override lateinit var reliantAppGUID: String
        override lateinit var instance: AppInstanceID
        override lateinit var helper: CompassHelper
        override lateinit var responseListener: (isSuccess: Boolean, errorCode: Int?, errorMessage: String?) -> Unit
        override lateinit var uiContext: Context
        override lateinit var compassApplicationContext: Context
        override val connectionViewModel: CompassConnectionViewModel by viewModelsFactory { CompassConnectionViewModel(
            helper,
            application,
            ::showSimpleMessage
        ) }

        private val permissionsStartForActivityResult = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()){
            onRequestCompassPermissionsResult(it)
        }

        private val compassInstanceIDStartForActivityResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
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
        override val connectionViewModel: CompassConnectionViewModel by viewModelsFactory { CompassConnectionViewModel(
            helper,
            requireActivity().application,
            ::showSimpleMessage
        ) }

        private var permissionsStartForActivityResult = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()){
            onRequestCompassPermissionsResult(it)
        }

        private val compassInstanceIDStartForActivityResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
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
        fun getReliantAppJWTKeyPair(): KeyPair = keyStoreWrapper.getKeyPair(KeyAlias.JWT_KEY)

        /**
         * Returns the Shared Space Key Paid. Internally generates a Key Pair if it does not exist
         * @return Shared Space Key Pair
         */
        fun getSharedSpaceKeyPair(): KeyPair = keyStoreWrapper.getKeyPair(KeyAlias.SHARED_SPACE_KEY)

        /**
         * Returns the Instance ID if one exists
         * @return Instance Id
         */
        fun getInstanceId(): String? = dataStore.getInstanceId()

        /**
         * Checks if Instance ID is present
         */
        fun hasInstanceId(): Boolean = dataStore.getInstanceId() != null

        /**
         * Returns the Community Pass Kernel JWT Public Key if it exists
         * @return Kernel [PublicKey]
         */
        fun getKernelJWTPublicKey(): PublicKey? = dataStore.getServerPublicKey()

        /**
         * Returns the Community Pass Kernel Shared Space Public Key if it exists
         * @return Kernel [PublicKey]
         */
        fun getKernelSharedSpaceKey(): PublicKey? = dataStore.getSharedSpacePublicKey()

        /**
         * Stores the Community Pass Kernel Shared Space Public Key
         */
        fun saveKernelSharedSpaceKey(publicKey: PublicKey) = dataStore.saveSharedSpacePublicKey(publicKey)

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
        fun parseJWT(jwt: String): CompassJWTResponse = jwtHelper.parseJWT(jwt)

        /**
         * Parses a bio token JWT signed using the Reliant App private key
         * @return A [RegisterUserForBioTokenResponse] representation of the JWT
         */
        fun parseBioTokenJWT(jwt: String): RegisterUserForBioTokenResponse = jwtHelper.parseBioTokenJWT(jwt)

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

            fun saveSharedSpacePublicKey(publicKey: PublicKey){
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
                        val createdPrivateKey = keyStore.getKey(alias.toString(), null) as PrivateKey?

                        KeyPair(createdPublicKey, createdPrivateKey)
                    }
                    false -> KeyPair(publicKey, privateKey)
                }
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
                return when(this){
                    JWT_KEY -> KeyProperties.PURPOSE_SIGN or KeyProperties.PURPOSE_VERIFY
                    SHARED_SPACE_KEY -> KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
                }
            }

            fun setProperties(builder: KeyGenParameterSpec.Builder){
                when(this){
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

                    claims.entries
                    return CompassJWTResponse.Success(
                        isMatch, rId,
                        biometricMatchList
                    )
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

        private fun String.toYesNoBoolean(): Boolean = when {
            this.lowercase() == "yes" -> true
            else -> false
        }
        //endregion
    }
    //endregion
}

inline fun <reified T : ViewModel> Fragment.viewModelsFactory(crossinline viewModelInitialization: () -> T): Lazy<T> {
    return activityViewModels {
        object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                @Suppress("UNCHECKED_CAST")
                return viewModelInitialization.invoke() as T
            }
        }
    }
}

inline fun <reified T : ViewModel> AppCompatActivity.viewModelsFactory(crossinline viewModelInitialization: () -> T): Lazy<T> {
    return viewModels {
        object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                @Suppress("UNCHECKED_CAST")
                return viewModelInitialization.invoke() as T
            }
        }
    }
}