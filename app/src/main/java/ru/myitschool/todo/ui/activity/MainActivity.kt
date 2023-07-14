package ru.myitschool.todo.ui.activity

import android.Manifest
import android.app.Dialog
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch
import ru.myitschool.todo.App
import ru.myitschool.todo.R
import ru.myitschool.todo.databinding.ActivityMainBinding
import ru.myitschool.todo.di.components.AppComponent
import ru.myitschool.todo.ui.ViewModelFactory
import ru.myitschool.todo.ui.compose.Beige
import ru.myitschool.todo.ui.compose.GrayInactiveLight
import ru.myitschool.todo.ui.compose.GrayLight
import ru.myitschool.todo.utils.NetworkStateMonitor
import javax.inject.Inject

class MainActivity : AppCompatActivity() {

    private val viewModel: MainActivityViewModel by viewModels {
        ViewModelFactory {
            component.mainActivityViewModel()
        }
    }

    private val component: AppComponent by lazy {
        (application as App).getAppComponent()
    }

    private var _binding: ActivityMainBinding? = null

    @Inject
    lateinit var networkStateMonitor: NetworkStateMonitor

    private val binding get() = _binding!!

    private val showNotificationDialog = mutableStateOf(false)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        component.inject(this)
        setupDialog()
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                networkStateMonitor.isConnected.collect {
                    if (!it) {
                        Snackbar.make(
                            binding.fragmentContainerView,
                            resources.getString(R.string.no_connection),
                            Snackbar.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        }
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED){
                viewModel.isGranted.collect{
                    if (it == null){
                        requestNotificationPermission()
                    }
                }
            }
        }
    }
    private fun setupDialog(){
        binding.composeView.setContent {
            var showDialog by remember {
                showNotificationDialog
            }
            NotificationTheme {
                NotificationDialog(showDialog, onAllow = {
                    showDialog = false
                    viewModel.savePermissionResult(true)
                }, onDissAllow = {
                    showDialog = false
                    viewModel.savePermissionResult(false)
                })
            }
        }
    }
    private fun requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                requestPermissions(arrayOf(Manifest.permission.POST_NOTIFICATIONS), 1)
            }
        } else {
            showNotificationDialog.value = true
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (grantResults.isNotEmpty()) {
            viewModel.savePermissionResult(grantResults[0] == PackageManager.PERMISSION_GRANTED)
        }
    }
}
