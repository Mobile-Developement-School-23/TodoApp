package ru.myitschool.todo.ui.addition_fragment

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.app.TimePickerDialog.OnTimeSetListener
import android.content.Context
import android.content.res.Configuration.UI_MODE_NIGHT_NO
import android.content.res.Configuration.UI_MODE_NIGHT_YES
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import ru.myitschool.todo.App
import ru.myitschool.todo.R
import ru.myitschool.todo.data.models.Priority
import ru.myitschool.todo.data.models.TodoItem
import ru.myitschool.todo.data.repository.TodoItemsRepository
import ru.myitschool.todo.di.components.AdditionFragmentComponent
import ru.myitschool.todo.ui.ViewModelFactory
import ru.myitschool.todo.ui.compose.AppTheme
import ru.myitschool.todo.utils.notifications.NotificationScheduler
import ru.myitschool.todo.utils.UploadHelper
import java.util.Calendar
import javax.inject.Inject

class AdditionFragment : Fragment() {
    @Inject
    lateinit var scheduler: NotificationScheduler
    private val navController: NavController by lazy {
        NavHostFragment.findNavController(this)
    }
    private val component: AdditionFragmentComponent by lazy {
        (requireActivity().application as App).getAppComponent().additionFragmentComponent()
    }

    private val viewModel: AdditionViewModel by viewModels {
        ViewModelFactory {
            component
                .additionViewModel()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        recognizeScreen()
        component.inject(this)
        return ComposeView(requireContext()).apply {
            setContent {
                AppTheme {
                    MainScreen(
                        viewModel, popBackStack = { navController.popBackStack() },
                        onSave = {
                            if (it.deadline != null) {
                                scheduler.scheduleNotification(it.id, it.deadline)
                            }
                        }
                    )
                }
            }
        }
    }


    private fun recognizeScreen() {
        val id = arguments?.getString("id")
        if (id != null) {
            viewModel.loadTodoItem(id)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MainScreen(
    viewModel: AdditionViewModel,
    popBackStack: () -> Unit,
    onSave: (todoItem: TodoItem) -> Unit
) {
    val scope = rememberCoroutineScope()
    var sheetState by remember { mutableStateOf(false) }
    val scrollState = rememberScrollState()
    Scaffold(
        modifier = Modifier.background(MaterialTheme.colorScheme.background),
        topBar = {
            AppBar(scrollState, viewModel, popBackStack)
        },
    ) {
        Box(
            Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
        ) {
            Column(modifier = Modifier.align(Alignment.TopCenter)) {
                Column(
                    Modifier
                        .background(MaterialTheme.colorScheme.background)
                        .padding(it)
                        .padding(start = 16.dp, end = 16.dp, bottom = 24.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    TodoTextField(viewModel.text.collectAsState().value) { newText ->
                        viewModel.setText(newText)
                    }
                    ImportanceSelector(viewModel.priority.collectAsState().value) {
                        scope.launch { sheetState = true }
                    }
                    Divider(color = MaterialTheme.colorScheme.outline, thickness = 1.dp)
                    TimeDeadlineSelector(
                        viewModel.deadlineDate.collectAsState(initial = "").value,
                        viewModel.deadlineTime.collectAsState(initial = "").value,
                        viewModel
                    )
                }
                Divider(color = MaterialTheme.colorScheme.outline, thickness = 1.dp)
                DeleteButton(
                    isDeletable = viewModel.isUpdateScreen.collectAsState(initial = false).value,
                    viewModel
                )
            }

            Column(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(start = 10.dp, end = 10.dp, bottom = 10.dp)
            ) {
                UIStateHolder(
                    uiState = viewModel.uiState.collectAsState(initial = UIState.Default).value,
                    viewModel,
                    popBackStack,
                    onSave
                )
            }
        }
    }
    if (sheetState) {
        ModalBottomSheet(
            onDismissRequest = { sheetState = false },
            containerColor = MaterialTheme.colorScheme.onPrimary
        ) {
            BottomSheetSelectorContent(viewModel) {
                sheetState = false
            }
        }
    }
}

@Composable
@Preview(uiMode = UI_MODE_NIGHT_YES, name = "Dark mode")
private fun MainScreenPreviewDark() {
    val viewModel = AdditionViewModel(FakeRepository(), UploadHelper(FakeRepository()))
    viewModel.setText("Hype")
    viewModel.setPriority(Priority.HIGH)
    AppTheme {
        MainScreen(
            viewModel, {}, {}
        )
    }
}

@Composable
@Preview(uiMode = UI_MODE_NIGHT_NO, name = "Light mode")
private fun MainScreenPreviewLight() {
    AppTheme {
        MainScreen(
            AdditionViewModel(FakeRepository(), UploadHelper(FakeRepository())), {}, {}
        )
    }
}

@Composable
private fun UIStateHolder(
    uiState: UIState,
    viewModel: AdditionViewModel,
    popBackStack: () -> Unit,
    onSave: (todoItem: TodoItem) -> Unit
) {
    when (uiState) {
        UIState.Deleted -> {
            popBackStack.invoke()
        }


        else -> {
            if (uiState is UIState.Saved) {
                onSave.invoke(uiState.todoItem)
                popBackStack.invoke()
            }
        }
    }
    var showing by remember { mutableStateOf(false) }
    if (uiState is UIState.Error) {
        if (uiState.message == UIError.BadSave) {
            LaunchedEffect(showing) {
                showing = true
                delay(2000)
                showing = false
                viewModel.setDefaultUIState()
            }
        }
    }
    AnimatedVisibility(
        visible = showing,
        enter = slideInVertically(),
        exit = slideOutVertically()
    ) {
        Snackbar {
            Text(text = stringResource(id = R.string.addition_error))
        }
    }
}

@Composable
private fun DeleteButton(isDeletable: Boolean, viewModel: AdditionViewModel) {
    var color = MaterialTheme.colorScheme.outlineVariant
    if (isDeletable) {
        color = Color.Red
    }
    Row(
        Modifier
            .padding(8.dp)
            .clip(shape = RoundedCornerShape(8.dp))
            .clickable(enabled = isDeletable) {
                viewModel.deleteTodoItem()
            }
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Image(
            painter = painterResource(id = R.drawable.ic_delete),
            contentDescription = "Delete",
            colorFilter = ColorFilter.tint(color = color)
        )
        Text(
            text = stringResource(id = R.string.delete),
            color = color,
            fontSize = 16.sp,
            modifier = Modifier.padding(start = 12.dp)
        )
    }
}

@Composable
private fun TimeDeadlineSelector(date: String, time: String, viewModel: AdditionViewModel) {
    Column {
        DeadlineDateSelector(date, viewModel = viewModel)
        DeadlineTimeSelector(date.isNotEmpty(), time, viewModel = viewModel)
    }
}

@Composable
fun DeadlineTimeSelector(enabled: Boolean, time: String, viewModel: AdditionViewModel) {
    var switchChecked by remember {
        mutableStateOf(false)
    }
    switchChecked = time != ""
    val context = LocalContext.current
    var color = MaterialTheme.colorScheme.secondary
    if (!enabled) {
        color = MaterialTheme.colorScheme.outlineVariant
    }
    Box(Modifier.fillMaxWidth()) {
        Column(Modifier.align(Alignment.CenterStart)) {
            Text(
                text = stringResource(id = R.string.time),
                color = color,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )
            Text(
                text = time,
                color = MaterialTheme.colorScheme.primary,
            )
        }
        Image(
            painter = rememberVectorPainter(image = Icons.Filled.DateRange),
            contentDescription = "Time",
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .clickable(enabled=enabled) {
                    showTimePickerDialog(viewModel, context)
                },
            colorFilter = ColorFilter.tint(color)
        )
    }
}

@Composable
fun DeadlineDateSelector(date: String, viewModel: AdditionViewModel) {
    var switchChecked by remember {
        mutableStateOf(false)
    }
    switchChecked = date != ""
    val context = LocalContext.current
    Box(Modifier.fillMaxWidth()) {
        Column(Modifier.align(Alignment.CenterStart)) {
            Text(
                text = stringResource(id = R.string.make_until),
                color = MaterialTheme.colorScheme.secondary,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )
            Text(
                text = date,
                color = MaterialTheme.colorScheme.primary,
            )
        }
        Switch(checked = switchChecked, onCheckedChange = {
            switchChecked = it
            if (it) {
                showDatePickerDialog(viewModel = viewModel, context = context)
            } else {
                viewModel.setDeadline(null)
            }
        }, modifier = Modifier.align(Alignment.CenterEnd))
    }
}

@Composable
private fun BottomSheetSelectorContent(viewModel: AdditionViewModel, onClick: () -> Unit) {
    Column(
        verticalArrangement = Arrangement.spacedBy(10.dp),
        modifier = Modifier.padding(start = 16.dp, bottom = 10.dp, end = 16.dp)
    ) {
        Text(
            text = stringResource(id = R.string.no),
            modifier = Modifier
                .fillMaxWidth()
                .clickable {
                    viewModel.setPriority(Priority.NORMAL)
                    onClick.invoke()
                },
            fontSize = 20.sp,
            color = MaterialTheme.colorScheme.secondary
        )
        Text(
            text = stringResource(id = R.string.low),
            modifier = Modifier
                .fillMaxWidth()
                .clickable {
                    viewModel.setPriority(Priority.LOW)
                    onClick.invoke()
                },
            fontSize = 20.sp,
            color = MaterialTheme.colorScheme.secondary
        )
        Text(
            text = stringResource(id = R.string.high),
            modifier = Modifier
                .fillMaxWidth()
                .clickable {
                    viewModel.setPriority(Priority.HIGH)
                    onClick.invoke()
                },
            color = Color.Red, fontSize = 20.sp
        )
    }
}

@Composable
private fun ImportanceSelector(importance: Priority, onClick: () -> Unit) {
    var text = ""
    var color = MaterialTheme.colorScheme.secondary
    when (importance) {
        Priority.HIGH -> {
            text = stringResource(id = R.string.high)
            color = Color.Red
        }

        Priority.LOW -> {
            text = stringResource(id = R.string.low)
        }

        Priority.NORMAL -> {
            text = stringResource(id = R.string.no)
        }
    }
    Column(modifier = Modifier
        .fillMaxWidth()
        .padding(top = 12.dp)
        .clickable { onClick.invoke() }) {
        Text(
            text = stringResource(id = R.string.priority),
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp
        )
        Text(text = text, color = color)
    }
}


@Composable
private fun TodoTextField(text: String, onValueChange: (String) -> Unit = {}) {
    val containerColor = MaterialTheme.colorScheme.onPrimary
    val textColor = MaterialTheme.colorScheme.secondary
    val shape = RoundedCornerShape(8.dp)
    TextField(
        value = text,
        onValueChange = { newText -> onValueChange(newText) },
        colors = TextFieldDefaults.colors(
            focusedContainerColor = containerColor,
            unfocusedContainerColor = containerColor,
            disabledContainerColor = containerColor,
            disabledTextColor = textColor,
            focusedTextColor = textColor,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            disabledIndicatorColor = Color.Transparent
        ),
        shape = shape,
        label = {
            Text(text = stringResource(id = R.string.what_to_do))
        },
        modifier = Modifier
            .defaultMinSize(minHeight = 150.dp)
            .fillMaxWidth()
            .padding(top = 3.dp)
            .shadow(elevation = 2.dp, shape = shape)
            .padding(bottom = 2.dp)
    )
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AppBar(
    scrollState: ScrollState,
    viewModel: AdditionViewModel,
    popBackStack: () -> Unit
) {
    val elevation = (if (scrollState.value > 0) 10 else 0).dp
    TopAppBar(
        title = { },
        navigationIcon = {
            Box(
                Modifier
                    .fillMaxWidth()
                    .background(Color.Transparent)
            ) {
                IconButton(onClick = {
                    popBackStack.invoke()
                }, Modifier.align(alignment = Alignment.CenterStart)) {
                    Icon(Icons.Filled.Close, contentDescription = "Close")
                }
                Text(
                    text = stringResource(id = R.string.save),
                    color = MaterialTheme.colorScheme.primary,
                    fontSize = 20.sp,
                    modifier = Modifier
                        .align(alignment = Alignment.CenterEnd)
                        .padding(8.dp)
                        .clip(shape = RoundedCornerShape(8.dp))
                        .clickable {
                            viewModel.saveTodo()
                        }
                        .padding(8.dp),
                    fontWeight = FontWeight.Bold,
                )
            }
        }, modifier = Modifier.shadow(elevation = elevation)
    )
}

private fun showTimePickerDialog(viewModel: AdditionViewModel, context: Context) {
    val calendar = Calendar.getInstance()
    val listener = OnTimeSetListener { view, hour, minute ->
        viewModel.setDeadlineTime(hour, minute)
    }
    val timePicker = TimePickerDialog(
        context,
        listener,
        calendar.get(Calendar.HOUR),
        calendar.get(Calendar.MINUTE),
        true
    )
    timePicker.show()
}

private fun showDatePickerDialog(viewModel: AdditionViewModel, context: Context) {
    val datePicker = DatePickerDialog(context)
    datePicker.setOnDateSetListener { _, year, month, dayOfMonth ->
        val date = Calendar.getInstance()
        date.set(year, month, dayOfMonth)
        viewModel.setDeadline(date.time)
    }
    datePicker.show()
}

class FakeRepository : TodoItemsRepository {
    override suspend fun addItem(todoItem: TodoItem): Result<Boolean> {
        return Result.success(true)
    }

    override suspend fun getItemById(id: String): Result<TodoItem?> {
        return Result.success(null)
    }

    override suspend fun updateItem(todoItem: TodoItem, withUpdate: Boolean): Result<Boolean> {
        return Result.success(true)
    }

    override suspend fun getItemsByPriority(priority: Priority): Result<List<TodoItem>> {
        return Result.success(listOf())
    }

    override suspend fun deleteItem(id: String): Result<Boolean> {
        return Result.success(true)
    }

    override suspend fun loadAllItems(): Result<Boolean> {
        return Result.success(true)
    }

    override suspend fun getAllItems(): Result<List<TodoItem>> {
        return Result.success(listOf())
    }

    override suspend fun updateItems(): Result<Boolean> {
        return Result.success(true)
    }

    override fun getItemsFlow(): Flow<List<TodoItem>> {
        return MutableStateFlow(listOf())
    }

}