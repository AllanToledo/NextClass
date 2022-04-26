package com.allantoledo.nextclass

import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.content.res.Resources
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.view.ViewCompat
import androidx.core.view.ViewCompat.setOnApplyWindowInsetsListener
import androidx.core.view.ViewCompat.setPaddingRelative
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.lifecycle.MutableLiveData
import com.allantoledo.nextclass.components.Span
import com.allantoledo.nextclass.components.Title
import com.allantoledo.nextclass.components.convertPixelToDp
import com.allantoledo.nextclass.ui.theme.NextClassTheme
import java.time.DayOfWeek
import java.time.format.TextStyle
import java.util.*

class MainActivity : ComponentActivity() {

    protected var collegeClasses: MutableLiveData<List<CollegeClass>> = MutableLiveData(null)
    lateinit var collegeClassDao: CollegeClassDao
    protected var bottomInset = MutableLiveData(0f);
    protected var topInset = MutableLiveData(0f);

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        setOnApplyWindowInsetsListener(window.decorView) { _, windowInsets ->
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

    override fun onResume() {
        super.onResume()
        val dataBase = getNextClassDataBase(applicationContext)
        collegeClassDao = dataBase.collegeClassDao()
        collegeClasses.value = collegeClassDao.getAll()
    }


    @Composable
    fun Container() {
        val collegeClassesObserver = collegeClasses.observeAsState()
        var collegeClassSelected by remember {
            mutableStateOf(getCollegeClassDefault())
        }

        Box(
            contentAlignment = Alignment.BottomEnd,
            modifier = Modifier.fillMaxSize()
        ) {
            Column(
                Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Title("Grade de Horário")
                Spacer(Modifier.size(height = 16.dp, width = 0.dp))
                LazyColumn() {
                    if (collegeClassesObserver.value != null)
                        items(collegeClassesObserver.value!!) {
                            CardClass(collegeClass = it) { collegeClassSelected = it }
                        }
                    items(1) {
                        Spacer(Modifier.size(height = 100.dp, width = 0.dp))
                    }
                }
            }
            Button(
                modifier = Modifier
                    .absoluteOffset(
                        x = (-16).dp,
                        y = (-16).dp
                    )
                    .size(50.dp),
                onClick = {
                    startActivity(Intent(applicationContext, CreateActivity::class.java))
                },
                shape = RoundedCornerShape(100.dp),
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = MaterialTheme.colors.secondary,
                )
            ) {
                Icon(Icons.Rounded.Add, contentDescription = "Adicionar")
            }

            if (collegeClassSelected.id != -1)
                Box(
                    Modifier
                        .fillMaxSize()
                        .background(
                            color = Color(red = 16, green = 16, blue = 16, 0xA0)
                        )
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        Modifier
                            .fillMaxSize()
                            .clickable { collegeClassSelected = getCollegeClassDefault() }
                    )

                    Surface(
                        Modifier
                            .background(
                                color = MaterialTheme.colors.background,
                                shape = MaterialTheme.shapes.medium
                            ),
                        shape = MaterialTheme.shapes.medium,
                    ) {
                        Column(
                            Modifier.padding(16.dp)
                        ) {
                            Title("Deletar?")
                            CardClass(collegeClassSelected) { }
                            Row(
                                Modifier
                                    .fillMaxWidth()
                                    .padding(top = 32.dp),
                                horizontalArrangement = Arrangement.SpaceAround
                            ) {
                                Button(
                                    onClick = {
                                        collegeClassDao.delete(collegeClassSelected)
                                        collegeClasses.value = collegeClassDao.getAll()
                                        collegeClassSelected = getCollegeClassDefault()
                                    },
                                    shape = MaterialTheme.shapes.medium,
                                    colors = ButtonDefaults.buttonColors(
                                        backgroundColor = MaterialTheme.colors.background,
                                    ),
                                    elevation = ButtonDefaults.elevation(0.dp)
                                ) {
                                    Span("Deletar", color = MaterialTheme.colors.onBackground)
                                }
                                Button(
                                    onClick = {
                                        collegeClassSelected = getCollegeClassDefault()
                                    },
                                    shape = MaterialTheme.shapes.medium,
                                    colors = ButtonDefaults.buttonColors(
                                        backgroundColor = MaterialTheme.colors.secondary,
                                    ),
                                    elevation = ButtonDefaults.elevation(0.dp)
                                ) {
                                    Span("Cancelar", color = MaterialTheme.colors.onSecondary)
                                }
                            }
                        }
                    }
                }
        }
    }

    @Composable
    fun CardClass(collegeClass: CollegeClass, onClick: () -> Unit) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
                .clickable {
                    onClick()
                },
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column() {
                Title(collegeClass.matter, 16.sp)
                Span(collegeClass.locale)
            }
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Span(
                    DayOfWeek.of(collegeClass.dayOfWeek)
                        .getDisplayName(TextStyle.SHORT, Locale.forLanguageTag("PT-BR"))
                )
                Span(
                    "${
                        collegeClass.hour.toString().padStart(2, '0')
                    }:${collegeClass.minute.toString().padStart(2, '0')}"
                )
            }
        }
    }


    @Preview(showBackground = true)
    @Composable
    fun DefaultPreview() {
        NextClassTheme {
            Container()
        }
    }
}

