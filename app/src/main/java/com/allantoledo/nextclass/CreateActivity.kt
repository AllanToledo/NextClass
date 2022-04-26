package com.allantoledo.nextclass

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.MutableLiveData
import com.allantoledo.nextclass.components.*
import com.allantoledo.nextclass.ui.theme.NextClassTheme
import java.time.DayOfWeek
import java.time.format.TextStyle
import java.util.*

class CreateActivity : ComponentActivity() {

    var matterSuggestions = ArrayList<String>()
    var localeSuggestions = ArrayList<String>()

    protected var bottomInset = MutableLiveData(0f);
    protected var topInset = MutableLiveData(0f);

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)

        val database = getNextClassDataBase(applicationContext)
        val collegeClassDao = database.collegeClassDao()
        val collegesClasses = collegeClassDao.getAll()
        collegesClasses.forEach { it ->
            if (!matterSuggestions.contains(it.matter))
                matterSuggestions.add(it.matter)
            if (!localeSuggestions.contains(it.locale))
                localeSuggestions.add(it.locale)
        }

        ViewCompat.setOnApplyWindowInsetsListener(window.decorView) { _, windowInsets ->
            var insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars())
            bottomInset.value = convertPixelToDp(insets.bottom, applicationContext)
            insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars())
            topInset.value = convertPixelToDp(insets.top, applicationContext)
            WindowInsetsCompat.CONSUMED
        }
        setContent {
            NextClassTheme {
                Surface(color = MaterialTheme.colors.background) {
                    val bottomPadding = bottomInset.observeAsState()
                    val topPadding = topInset.observeAsState()
                    Box(
                        Modifier.padding(
                            bottom = bottomPadding.value!!.dp,
                            top = topPadding.value!!.dp
                        )
                    ) {
                        Container()
                    }
                }
            }
        }
    }

    @Composable
    fun Container() {
        var selectedDayOfWeek by remember { mutableStateOf(1) }
        var matterName by remember { mutableStateOf("") }
        var localeName by remember { mutableStateOf("") }
        var hourSelected by remember { mutableStateOf(12) }
        var minuteSelected by remember { mutableStateOf(30) }

        Column(
            Modifier
                .padding(16.dp)
                .fillMaxSize()
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Button(
                    modifier = Modifier.size(50.dp),
                    colors = ButtonDefaults.buttonColors(backgroundColor = Color.Transparent),
                    elevation = ButtonDefaults.elevation(0.dp),
                    onClick = { finish() }) {
                    Icon(
                        Icons.Filled.ArrowBack,
                        contentDescription = "Voltar",
                    )
                }
                Title(text = "Adicionar Horário")
                Spacer(modifier = Modifier.size(50.dp))
            }
            Spacer(Modifier.size(32.dp))
            TextField(
                label = "Matéria",
                placeholder = "Cálculo I",
                onChange = { text ->
                    matterName = text
                },
                matterSuggestions.filter { suggestion ->
                    suggestion.startsWith(
                        matterName,
                        ignoreCase = true
                    )
                }.toTypedArray()
            )
            TextField(
                label = "Local",
                placeholder = "Sala 1",
                onChange = { text ->
                    localeName = text
                },
                localeSuggestions.filter { suggestion ->
                    suggestion.startsWith(
                        localeName,
                        ignoreCase = true
                    )
                }.toTypedArray()
            )
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                for (i in 1..7) {
                    Button(
                        modifier = Modifier
                            .defaultMinSize(1.dp, 1.dp)
                            .padding(0.dp),
                        onClick = { selectedDayOfWeek = i },
                        colors = ButtonDefaults.buttonColors(
                            backgroundColor = if (selectedDayOfWeek == i) MaterialTheme.colors.secondary else MaterialTheme.colors.background
                        ),
                        shape = MaterialTheme.shapes.medium,
                        elevation = ButtonDefaults.elevation(0.dp),
                        contentPadding = PaddingValues(12.dp)
                    ) {
                        Span(
                            DayOfWeek.of(i)
                                .getDisplayName(TextStyle.SHORT, Locale.forLanguageTag("PT-BR")),
                            color = if (selectedDayOfWeek == i) MaterialTheme.colors.onSecondary else MaterialTheme.colors.onBackground
                        )
                    }
                }
            }

            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(vertical = 20.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Span("Hora")
                    ScrollPicker(
                        applicationContext,
                        0,
                        24,
                        { value -> hourSelected = value }, 12
                    )
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Span("Minutos")
                    ScrollPicker(
                        applicationContext,
                        0,
                        60,
                        { value -> minuteSelected = value }, 30
                    )
                }
            }

            Button(
                modifier = Modifier
                    .fillMaxWidth(),
                onClick = {
                    val database = getNextClassDataBase(applicationContext)
                    database.collegeClassDao().insert(
                        CollegeClass(
                            0,
                            matterName,
                            localeName,
                            selectedDayOfWeek,
                            hourSelected,
                            minuteSelected,
                            (selectedDayOfWeek * 24 * 60) + (hourSelected * 60) + minuteSelected
                        )
                    )
                    finish()
                },
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = MaterialTheme.colors.secondary
                ),
                shape = MaterialTheme.shapes.medium,
                elevation = ButtonDefaults.elevation(0.dp),
                contentPadding = PaddingValues(12.dp)
            ) {
                Span(
                    "Adicionar",
                    color = MaterialTheme.colors.onSecondary
                )
            }

        }
    }

    @Preview(showBackground = true, widthDp = 380, heightDp = 640)
    @Composable
    fun PreviewBuild() {
        NextClassTheme() {
            Surface() {
                Container()
            }
        }
    }

}