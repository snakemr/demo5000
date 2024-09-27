package com.example.demo_5000.ui.custom

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.TextFieldColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

@Composable
fun PinField(
    modifier: Modifier = Modifier,
    textStyle: TextStyle = LocalTextStyle.current.copy(textAlign = TextAlign.Center),
    pinCount: Int = 6,
    pinPaddings: PaddingValues = PaddingValues(horizontal = 8.dp),
    shape: Shape = OutlinedTextFieldDefaults.shape,
    colors: TextFieldColors = OutlinedTextFieldDefaults.colors(
        focusedBorderColor = MaterialTheme.colorScheme.onSurface,
        errorBorderColor = MaterialTheme.colorScheme.primary
    ),
    nextFocusRequester: FocusRequester? = null,
    onEnter: (Int?)->Unit
) = Row(modifier) {
    val requesters = remember(pinCount) {
        List(pinCount) { FocusRequester() }
    }
    val pins = remember(pinCount) {
        List(pinCount) { "" }.toMutableStateList()
    }
    for (i in 0..<pinCount) {
        OutlinedTextField(pins[i], { num ->
            pins[i] = num.take(1)
            if (num.isNotEmpty())
                if (i+1 < pinCount)
                    requesters[i+1].requestFocus()
                else {
                    onEnter(pins.filter { it != "" }.joinToString("").toIntOrNull() ?: 0)
                    nextFocusRequester?.requestFocus()
                }
        },  Modifier
            .weight(1f)
            .padding(pinPaddings)
            .focusRequester(requesters[i])
            .onFocusChanged {
                if (it.isFocused) {
                    if (pins[i].isNotEmpty()) onEnter(null)
                    pins[i] = ""
                }
            },
            textStyle = textStyle,
            isError = pins[i].isNotEmpty(),
            shape = shape,
            colors = colors,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Next)
        )
    }
}