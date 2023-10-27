@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.exam

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.AlertDialog
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.ViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.exam.ui.theme.ExamTheme

class MainActivity : ComponentActivity() {

    private val user: UserViewModel by viewModels()
    @SuppressLint("ComposableDestinationInComposeScope", "UnusedMaterial3ScaffoldPaddingParameter")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ExamTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    var indi by rememberSaveable { mutableStateOf("See") }
                    var name = remember{ mutableStateListOf<String>() }
                    val navController = rememberNavController()
                    Scaffold(topBar = {TopAppBar(colors = TopAppBarDefaults.mediumTopAppBarColors(containerColor = Color.LightGray),
                        title = { Text(text = "Hello App") })},
                        bottomBar = {BottomNavigationBar(navController)}

                    ) {
                        Column(
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            NavigationController(navController = navController, user = user, indi = indi, name = name)
                        }}
                    }
                }
            }
        }
    }

@Composable
fun NavigationController(navController: NavHostController, user: UserViewModel,indi:String,name:MutableList<String>){
    NavHost(navController = navController, startDestination = "WorkScreen") {
        composable(route = Page.WorkScreen.route) {
            WorkScreen(
                NavHostController = navController,
                name = indi,
                addList = { name.add(it) })
        }

        composable(route = Page.DisplayScreen.route) {
            DisplayScreen(
                navHostController = navController,
                nameList = name,
                user=user
            )
        }
    }
}

@Composable
fun BottomNavigationBar(navController: NavHostController) {
    var selectedIndex by remember { mutableStateOf(0) }
    BottomNavigation {
            BottomNavigationItem(
                icon = { Icons.Default.Home },
                label = { Text("Work") },
                selected = selectedIndex == 0,
                onClick = {
                    selectedIndex = 0
                    navController.navigate(Page.WorkScreen.route)
                }
            )
        BottomNavigationItem(
            icon = { Icons.Default.Home },
            label = { Text("Display") },
            selected = selectedIndex == 1,
            onClick = {
                selectedIndex = 1
                navController.navigate(Page.DisplayScreen.route)
            }
        )
        }
    }
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navHostController: NavHostController,
    modifier: Modifier = Modifier,
    addName: (it:String)->Unit
) {
    var value by rememberSaveable{ mutableStateOf("") }
    Column {
        TextField(
            value = value,
            onValueChange = {
                value = it },
            modifier = modifier
        )
        Button(onClick = {
            addName(value)
            navHostController.navigate(Page.WorkScreen.route) }) {
            Text(
                text = "Next",
                modifier = modifier
            )
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun DisplayScreen(
    navHostController: NavHostController,
    nameList : MutableList<String>,
    user: UserViewModel
) {
    var name = remember { mutableStateOf(nameList) }
//    LaunchedEffect(nameList) {
//        name.addAll(nameList)
//    }
    fun goUp(it:String) {
        if(name.value.indexOf(it) !== 0 ) {
            val index2 = name.value.indexOf(it)
            val swap = name.value.get(index2 -1)

            name.value.set(index2-1, it)
            name.value.set(index2,swap)
        }
    }

    fun goDown(it:String) {
        if(name.value.indexOf(it) !== name.value.size-1 ) {
            val index2 = name.value.indexOf(it)
            val swap = name.value.get(index2 +1)

            name.value.set(index2+1, it)
            name.value.set(index2,swap)
        }
    }
    var scrollState = rememberScrollState()
    var selectedRow by remember {
        mutableStateOf("")
    }
    var openDialog by remember { mutableStateOf(false) }
    LazyColumn(
//        modifier = Modifier.verticalScroll(state = scrollState, enabled = true)
    ) {
        items(name.value, key = { it }) {
            Row(modifier = Modifier
                .animateItemPlacement()
                .clickable {
                    openDialog = true
                    selectedRow = it
                }){
                Text(text = it)
                Button(onClick = { goUp(it)
                }) {
                    Icon(imageVector = Icons.Default.KeyboardArrowUp,contentDescription = null)
                }
                Button(onClick = { goDown(it)
                }) {
                    Icon(imageVector = Icons.Default.KeyboardArrowDown,contentDescription = null)
                }

            }
            if(openDialog) {
                AlertDialog(
                    onDismissRequest= {openDialog = false},

                    title = { Text(text = selectedRow) },

                    buttons = { /*TODO*/ })
            }
        }
    }
}



@Composable
fun WorkScreen(
    NavHostController: NavHostController,
    name: String,
    addList: (it:String) -> Unit,
    modifier: Modifier = Modifier
) {
    var inputItems by rememberSaveable {
        mutableStateOf("")
    }
    Column() {
        Text(
            text = "Hello $name!",
            modifier = modifier
        )
        TextField(value = inputItems, onValueChange = {
            inputItems = it
        })

        Button(onClick = {
            var items = inputItems.split('\n')
        items.forEach({addList(it)})
        NavHostController.navigate(Page.DisplayScreen.route)}) {
            Text("Move")
        }
    }
}
sealed class Page(val route: String) {
    object WorkScreen : Page("WorkScreen")
    object DisplayScreen : Page("DisplayScreen")

}
@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    ExamTheme {

    }
}
class UserViewModel: ViewModel() {
    private var _name = mutableStateOf("")
    private var _car = ""
    private var _nameList = mutableListOf<String>()

    fun setName(new:String) {
        _name.value = new
    }
    fun getName() :String {
        return _name.value
    }
}
