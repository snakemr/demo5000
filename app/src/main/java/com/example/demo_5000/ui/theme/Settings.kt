package com.example.demo_5000.ui.theme

import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.demo_5000.R

val rounded5dp by lazy { RoundedCornerShape(5.dp) }

@Composable
fun ShowToggle(show: Boolean, onChange: (Boolean)->Unit) = IconButton( { onChange(!show) } ) {
    Icon(
        painterResource(if (show) R.drawable.eye else R.drawable.eye_slash),
        contentDescription = "show password", Modifier.size(28.dp)
    )
}
