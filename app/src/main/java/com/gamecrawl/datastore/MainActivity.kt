package com.gamecrawl.datastore

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.core.Preferences.Key
import androidx.datastore.preferences.preferencesDataStore
import com.gamecrawl.datastore.ui.theme.DataStoreTheme
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import java.io.IOException

class StoreUserEmail(private val context: Context) {

    companion object {
        private val Context.dataStore: DataStore<Preferences>
                by preferencesDataStore(name = "UserEmail")

        val USER_EMAIL_KEY = stringPreferencesKey("user_email")
    }

    val getEmail: Flow<String?>
        get() = context.dataStore.data.map { preferences ->
            preferences[USER_EMAIL_KEY] ?: ""
        }

    suspend fun setEmail(email: String) {
        context.dataStore.edit { preferences ->
            preferences[USER_EMAIL_KEY] = email
        }
    }
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            DataStoreTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    MainScreen()
                }
            }
        }
    }
}

@Composable
fun MainScreen() {

    // context
    val context = LocalContext.current
    // scope
    val scope = rememberCoroutineScope()
    // datastore Email
    val dataStore = StoreUserEmail(context)
    // get saved email
    val savedEmail = dataStore.getEmail.collectAsState(initial = "")

    var email by remember { mutableStateOf("") }

    Column(modifier = Modifier.fillMaxSize()) {

        Text(
            modifier = Modifier
                .padding(16.dp, top = 30.dp),
            text = "Email",
            color = Color.Gray,
            fontSize = 12.sp
        )
        //email field
        OutlinedTextField(
            modifier = Modifier
                .padding(start = 16.dp, end = 16.dp)
                .fillMaxWidth(),
            value = email,
            onValueChange = { email = it },
        )
        Spacer(modifier = Modifier.height(120.dp))
        // save button
        Button(
            modifier = Modifier
                .fillMaxWidth()
                .height(60.dp)
                .padding(start = 16.dp, end = 16.dp),
            onClick = {
                //launch the class in a coroutine scope
                scope.launch {
                    dataStore.setEmail(email)
                }
            },
        ) {
            // button text
            Text(
                text = "Save",
                color = Color.White,
                fontSize = 18.sp
            )
        }

        Text(
            text = savedEmail.value!!,
            color = Color.Black,
            fontSize = 18.sp
        )
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    DataStoreTheme {
        MainScreen()
    }
}
