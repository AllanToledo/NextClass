package com.allantoledo.nextclass.components

import android.content.Context
import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.interaction.FocusInteraction
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.PopupProperties
import kotlinx.coroutines.runBlocking
import kotlin.math.roundToInt

@Composable
fun Span(
    text: String,
    fontSize: TextUnit = 12.sp,
    color: Color = MaterialTheme.colors.onBackground
) {
    Text(
        text,
        fontSize = fontSize,
        style = MaterialTheme.typography.body1,
        color = color
    )
}

@Composable
fun Title(text: String, fontSize: TextUnit = 24.sp) {
    Text(
        text,
        fontSize = fontSize,
        style = MaterialTheme.typography.h1,
        color = MaterialTheme.colors.onBackground
    )
}

@Composable
fun TextField(
    label: String,
    placeholder: String,
    onChange: (String) -> Unit,
    suggestions: Array<String> = emptyArray<String>()
) {
    var textContent by remember { mutableStateOf("") }
    var hasFocus by remember { mutableStateOf(false) }
    var focusManager = LocalFocusManager.current

    Box(
        Modifier.padding(vertical = 16.dp),
        contentAlignment = Alignment.TopStart
    ) {
        Box(
            Modifier
                .fillMaxWidth()
                .background(
                    color = MaterialTheme.colors.background,
                    shape = MaterialTheme.shapes.medium,
                )
                .border(
                    border = BorderStroke(1.dp, color = MaterialTheme.colors.onBackground),
                    shape = MaterialTheme.shapes.medium
                ),
        ) {
            Column() {
                BasicTextField(
                    value = textContent,
                    singleLine = true,
                    onValueChange = { text ->
                        textContent = text
                        onChange(text)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp)
                        .onFocusChanged { focusState ->
                            hasFocus = focusState.isFocused
                        },
                    keyboardOptions = KeyboardOptions(
                        imeAction = ImeAction.Done
                    ),
                    keyboardActions = KeyboardActions(
                        onDone = { focusManager.clearFocus() },
                    )
                )
                DropdownMenu(
                    expanded = suggestions.isNotEmpty() && hasFocus && textContent.isNotEmpty(),
                    onDismissRequest = { },
                    modifier = Modifier
                        .fillMaxWidth(0.9f)
                        .background(color = MaterialTheme.colors.background),
                    // This line here will accomplish what you want
                    properties = PopupProperties(focusable = false)
                ) {
                    suggestions.forEach { label ->
                        DropdownMenuItem(
                            onClick = {
                                textContent = label
                                onChange(label)
                                focusManager.clearFocus()
                            },
                            modifier = Modifier.background(color = MaterialTheme.colors.background)
                        ) {
                            Span(label)
                        }
                    }
                }
            }
        }
        Box(
            Modifier
                .absoluteOffset(x = 16.dp, y = (-8).dp)
                .background(
                    color = MaterialTheme.colors.background,
                )
                .padding(horizontal = 8.dp, vertical = 0.dp)
        ) {
            Span(label, 12.sp)
        }
    }

}

@Composable
fun ScrollPicker(
    context: Context,
    startValue: Int,
    endValue: Int,
    onSelect: (Int) -> Unit,
    startPosition: Int = startValue,
) {
    var offset by remember { mutableStateOf(0f) }
    var position by remember { mutableStateOf(startPosition) }
    val scrollState = rememberScrollState()

    val heightInDp = 30
    val heightInPx = convertDpToPixel(heightInDp.toFloat(), context)


    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        //Icon(Icons.Filled.KeyboardArrowUp, "")

        Column(
            modifier = Modifier
                .size(width = 50.dp, height = (heightInDp * 3).dp)
                .verticalScroll(scrollState)
                .pointerInput(Unit) {
                    detectDragGestures(
                        onDragEnd = {
                            position = (offset / heightInPx).roundToInt()
                            onSelect(position)
                            offset = position * heightInPx
                            runBlocking { scrollState.scrollTo(offset.toInt()) }

                        },
                        onDrag = { _, dragAmount ->
                            offset -= dragAmount.y
                            if (offset < 0)
                                offset = 0f
                            if (offset > scrollState.maxValue)
                                offset = scrollState.maxValue.toFloat()

                            position = (offset / heightInPx).roundToInt()


                            runBlocking {
                                scrollState.scrollTo(offset.toInt())
                            }
                        }
                    )
                }
        ) {
            Spacer(modifier = Modifier.size(height = heightInDp.dp, width = 50.dp))
            repeat(endValue - startValue) { i ->
                val item = i + startValue
                Text(
                    item.toString().padStart(2, '0'),
                    modifier = Modifier
                        .size(height = heightInDp.dp, width = 50.dp)
                        .alpha(if (item == position) 1f else 0.5f),
                    textAlign = TextAlign.Center,
                    fontSize = 24.sp,
                )
            }
            Spacer(modifier = Modifier.size(height = heightInDp.dp, width = 50.dp))
        }

        //Icon(Icons.Filled.KeyboardArrowDown, "")
    }

    LaunchedEffect(Unit) {
        offset = startPosition * heightInPx
        scrollState.scrollTo((startPosition * heightInPx).toInt())
    }

}


fun convertDpToPixel(dp: Float, context: Context): Float {
    return dp * context.resources.displayMetrics.density
}

fun convertPixelToDp(px: Int, context: Context): Float {
    return px / context.resources.displayMetrics.density
}

@Preview
@Composable
fun Preview() {
    TextField(
        label = "Matéria",
        placeholder = "Cálculo I",
        onChange = { _ -> },
        arrayOf("Teste", "Teste")
    )
}