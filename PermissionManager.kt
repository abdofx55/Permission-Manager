// For more information about permission manager please visit :
// https://github.com/theappbusiness/android-permission-manager

package com.example.newcanalcollection.managers

import android.app.AlertDialog
import android.content.Context
import android.content.pm.PackageManager
import androidx.activity.ComponentActivity
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.newcanalcollection.MyApplication
import com.example.newcanalcollection.R
import com.example.newcanalcollection.utils.Permission

class PermissionManager private constructor(
    private val activity: ComponentActivity?,
    private val fragment: Fragment?
) {

    private val requiredPermissions = mutableListOf<Permission>()
    private var rationale: String? = null
    private var callback: (Boolean) -> Unit = {}
    private var detailedCallback: (Map<Permission, Boolean>) -> Unit = {}

    fun getContext(): Context {
        return activity ?: fragment?.requireContext()
        ?: MyApplication.appContext
    }

    private val permissionCheck =
        activity?.registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { grantResults ->
            sendResultAndCleanUp(grantResults)
        }
            ?: fragment?.registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { grantResults ->
                sendResultAndCleanUp(grantResults)
            }


    companion object {
        fun from(activity: ComponentActivity?, fragment: Fragment?) =
            PermissionManager(activity, fragment)
    }

    fun rationale(description: Int): PermissionManager {
        rationale = getContext().getString(description)
        return this
    }

    fun rationale(description: String): PermissionManager {
        rationale = description
        return this
    }

    fun request(vararg permission: Permission): PermissionManager {
        requiredPermissions.addAll(permission)
        return this
    }

    fun checkPermission(callback: (Boolean) -> Unit) {
        this.callback = callback
        handlePermissionRequest()
    }

    fun checkDetailedPermission(callback: (Map<Permission, Boolean>) -> Unit) {
        this.detailedCallback = callback
        handlePermissionRequest()
    }

    private fun handlePermissionRequest() {
        when {
            areAllPermissionsGranted() -> sendPositiveResult()
            shouldShowPermissionRationale() -> displayRationale()
            else -> requestPermissions()
        }
    }

    private fun displayRationale() {
        AlertDialog.Builder(getContext())
            .setTitle(getContext().getString(R.string.dialog_permission_title))
            .setMessage(
                rationale ?: getContext().getString(R.string.dialog_permission_default_message)
            )
            .setCancelable(false)
            .setPositiveButton(getContext().getString(R.string.dialog_permission_button_positive)) { _, _ ->
                requestPermissions()
            }
            .show()
    }

    private fun sendPositiveResult() {
        sendResultAndCleanUp(getPermissionList().associateWith { true })
    }

    private fun sendResultAndCleanUp(grantResults: Map<String, Boolean>) {
        callback(grantResults.all { it.value })
        detailedCallback(grantResults.mapKeys { Permission.from(it.key) })
        cleanUp()
    }

    private fun cleanUp() {
        requiredPermissions.clear()
        rationale = null
        callback = {}
        detailedCallback = {}
    }

    private fun requestPermissions() {
        permissionCheck?.launch(getPermissionList())
    }

    private fun areAllPermissionsGranted() =
        requiredPermissions.all { it.isGranted() }

    private fun shouldShowPermissionRationale() =
        requiredPermissions.any { it.requiresRationale() }

    private fun getPermissionList() =
        requiredPermissions.flatMap { it.permissions.toList() }.toTypedArray()

    private fun Permission.isGranted() =
        permissions.all { hasPermission(it) }

    private fun Permission.requiresRationale() =
        permissions.any { permissions ->
            activity?.let {
                ActivityCompat.shouldShowRequestPermissionRationale(it, permissions)
            }
                ?: fragment?.shouldShowRequestPermissionRationale(permissions)
                ?: false
        }

    private fun hasPermission(permission: String) =
        ContextCompat.checkSelfPermission(
            getContext(),
            permission
        ) == PackageManager.PERMISSION_GRANTED
}