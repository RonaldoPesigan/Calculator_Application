package com.example.applicationcal

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.toSize
import java.text.DecimalFormat

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalculatorScreen() {
    var firstNumber by remember { mutableStateOf("") }
    var secondNumber by remember { mutableStateOf("") }
    var selectedOperation by remember { mutableStateOf("Select Operation") }
    var result by remember { mutableStateOf("") } // Initialize as an empty string
    var isDropdownExpanded by remember { mutableStateOf(false) }
    val availableOperations = listOf(
        Operation("Addition", { a, b -> a + b }),
        Operation("Subtraction", { a, b -> a - b }),
        Operation("Multiplication", { a, b -> a * b }),
        Operation("Division", { a, b -> if (b != 0.0) a / b else Double.NaN })
    )
    var textfieldSize by remember { mutableStateOf(Size.Zero) }
    val icon = if (isDropdownExpanded)
        Icons.Filled.KeyboardArrowUp
    else
        Icons.Filled.KeyboardArrowDown

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {

        // First Number Input
        NumberInput(firstNumber, "First Number") { firstNumber = it }
        Spacer(modifier = Modifier.height(16.dp))

        // Second Number Input
        NumberInput(secondNumber, "Second Number") { secondNumber = it }

        Spacer(modifier = Modifier.height(16.dp))

        TextField(
            value = selectedOperation,
            onValueChange = { selectedOperation = it },
            modifier = Modifier
                .fillMaxWidth()
                .onGloballyPositioned { coordinates ->
                    textfieldSize = coordinates.size.toSize()
                },
            label = { Text("Operator") },
            trailingIcon = {
                Icon(
                    icon, "contentDescription",
                    Modifier.clickable { isDropdownExpanded = !isDropdownExpanded }
                )
            },
            readOnly = true
        )

        if (isDropdownExpanded) {
            OperationDropdown(
                availableOperations = availableOperations,
                selectedOperation = selectedOperation,
                onOperationSelected = { operation ->
                    selectedOperation = operation.name
                    isDropdownExpanded = false
                }
            )
        }
        Spacer(modifier = Modifier.height(165.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 4.dp),
            contentAlignment = Alignment.Center
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 4.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 4.dp)
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = result.takeIf { it.isNotEmpty() } ?: " ",
                        fontSize = 40.sp,
                        color = if (result.isNotEmpty()) Color.Black else Color.Gray
                    )
                }
            }
        }
        Spacer(modifier = Modifier.weight(1f))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Box(
                modifier = Modifier
                    .weight(1f)
            ){
                CalculatorButton("Calculate") {
                    val num1 = firstNumber.toDoubleOrNull() ?: 0.0
                    val num2 = secondNumber.toDoubleOrNull() ?: 0.0
                    val calculatedResult = availableOperations.find { it.name == selectedOperation }
                    result = if (calculatedResult != null) {
                        val value = calculatedResult.operation.invoke(num1, num2)
                        if (value.isNaN() || value.isInfinite()) {
                            "Invalid Operation"
                        } else {
                            value.formatResult()
                        }
                    } else {
                        "Invalid Operation"
                    }
                }}
            Spacer(modifier = Modifier.height(16.dp))
            Box(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 5.dp)
            ){
                CalculatorButton("Reset") {
                    firstNumber = ""
                    secondNumber = ""
                    selectedOperation = "Select Operation"
                    result = ""
                }
            }}

        Spacer(modifier = Modifier.height(70.dp))
    }
}

data class Operation(val name: String, val operation: (Double, Double) -> Double)

fun Double.formatResult(): String {
    val decimalFormat = DecimalFormat("#,###.##")
    return decimalFormat.format(this)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NumberInput(value: String, label: String, onValueChange: (String) -> Unit) {
    val shape = RoundedCornerShape(topStart = 5.dp, topEnd = 5.dp, bottomEnd = 0.dp, bottomStart = 0.dp)

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(shape)
            .background(Color(218, 227, 234))
    ) {
        TextField(
            value = value,
            onValueChange = { newText ->
                val filteredText = newText.filter { it.isDigit() || it == '.' }
                val decimalCount = filteredText.count { it == '.' }
                if (decimalCount > 1) {
                    return@TextField
                }
                if (filteredText.toDoubleOrNull() ?: 0.0 > 99999) {
                    return@TextField
                }
                onValueChange(filteredText)
            },
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth(),
            label = { Text(label) },
            shape = shape
        )
    }
}
@Composable
fun CalculatorButton(text: String, onClick: () -> Unit) {
    Button(onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 5.dp)
    ) {
        Text(text)
    }
}

@Composable
fun OperationDropdown(
    availableOperations: List<Operation>,
    selectedOperation: String,
    onOperationSelected: (Operation) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 20.dp)
            .background(Color(173, 216, 230))
    ) {
        availableOperations.forEach { operation ->
            Text(
                text = operation.name,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 10.dp)
                    .clickable {
                        onOperationSelected(operation)
                    }
            )
        }
    }
}
