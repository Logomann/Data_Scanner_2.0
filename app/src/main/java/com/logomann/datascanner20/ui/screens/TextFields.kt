package com.logomann.datascanner20.ui.screens

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Error
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.logomann.datascanner20.R


@Composable
fun CreateVinField(
    charMax: Int = 17,
    charMin: Int = 1,
    text: () -> String,
    setText: (String) -> Unit,
    validateVin: (String) -> Unit,
    isError: Boolean,
    modifier: Modifier,
    name: String = stringResource(id = R.string.vin_code),
    trailingIconEndPadding: Int = 48
) {
    TextField(
        value = text(),
        onValueChange = {
            if (it.length <= charMax) {
                setText(it)
                validateVin(it)
            }
        },
        maxLines = 1,
        isError = isError,
        label = {
            Text(
                name,
                fontSize = 20.sp
            )
        },
        supportingText = {
            if (isError) {
                Text(
                    text = stringResource(
                        R.string.vin_support_error, name, pluralStringResource(
                            id = R.plurals.symbol_plurals,
                            count = charMin,
                            charMin
                        )
                    )
                )
            }
        },
        trailingIcon = {
            if (isError) {
                Icon(
                    Icons.Filled.Error,
                    contentDescription = null,
                    modifier = Modifier.padding(end = trailingIconEndPadding.dp)
                )
            }
        },

        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Text
        ),
        shape = RoundedCornerShape(8.dp),
        modifier = modifier
            .fillMaxWidth()
            .padding(start = 16.dp, end = 16.dp),
        colors = TextFieldDefaults.colors(
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            errorIndicatorColor = Color.Transparent
        )
    )
}

@Composable
fun CreateCompoundField(
    text: () -> String,
    setText: (String) -> Unit,
    charMax: Int,
    isError: Boolean,
    setError: (Boolean) -> Unit,
    modifier: Modifier,
    name: String
) {
    fun validateText(field: String) {
        setError(field.isEmpty())
    }

    TextField(
        value = text(),
        onValueChange = {
            if (it.length <= charMax) {
                setText(it)
                validateText(it)
            }
        },
        maxLines = 1,
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Number
        ),
        isError = isError,
        label = {
            Text(
                text = name,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        },
        shape = RoundedCornerShape(8.dp),
        modifier = modifier
            .width(70.dp),
        colors = TextFieldDefaults.colors(
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            errorIndicatorColor = Color.Transparent
        )
    )
}