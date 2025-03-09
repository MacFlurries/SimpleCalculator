package com.example.simplecalculator

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.ColumnScopeInstance.weight
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.simplecalculator.ui.theme.SimpleCalculatorTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SimpleCalculatorTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    CalculatorApp()
                }
            }
        }
    }
}

@Composable
fun CalculatorApp() {
    var input by remember { mutableStateOf("") }
    var result by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Display Input and Result
        Text(
            text = input,
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            maxLines = 1
        )
        Text(
            text = result,
            fontSize = 24.sp,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            maxLines = 1
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Calculator Buttons
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Row 1: 7, 8, 9, /
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                CalculatorButton("7", onClick = { input += "7" })
                CalculatorButton("8", onClick = { input += "8" })
                CalculatorButton("9", onClick = { input += "9" })
                CalculatorButton("/", onClick = { input += "/" })
            }

            // Row 2: 4, 5, 6, *
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                CalculatorButton("4", onClick = { input += "4" })
                CalculatorButton("5", onClick = { input += "5" })
                CalculatorButton("6", onClick = { input += "6" })
                CalculatorButton("*", onClick = { input += "*" })
            }

            // Row 3: 1, 2, 3, -
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                CalculatorButton("1", onClick = { input += "1" })
                CalculatorButton("2", onClick = { input += "2" })
                CalculatorButton("3", onClick = { input += "3" })
                CalculatorButton("-", onClick = { input += "-" })
            }

            // Row 4: 0, C, =, +
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                CalculatorButton("0", onClick = { input += "0" })
                CalculatorButton("C", onClick = { input = ""; result = "" })
                CalculatorButton("=", onClick = {
                    try {
                        result = evaluateExpression(input).toString()
                    } catch (e: Exception) {
                        result = "Error"
                    }
                })
                CalculatorButton("+", onClick = { input += "+" })
            }
        }
    }
}

@Composable
fun CalculatorButton(
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onClick,
        modifier = modifier
            .weight(1f) // ✅ Correct usage of weight
            .aspectRatio(1f) // Make the button square
    ) {
        Text(text = label, fontSize = 24.sp)
    }
}

// Function to evaluate the expression
fun evaluateExpression(expression: String): Double {
    return object : Any() {
        var pos = -1
        var ch = 0

        fun nextChar() {
            ch = if (++pos < expression.length) expression[pos].code else -1
        }

        fun eat(charToEat: Int): Boolean {
            while (ch == ' '.code) nextChar()
            if (ch == charToEat) {
                nextChar()
                return true
            }
            return false
        }

        fun parse(): Double {
            nextChar()
            val x = parseExpression()
            if (pos < expression.length) throw RuntimeException("Unexpected: ${ch.toChar()}")
            return x
        }

        fun parseExpression(): Double {
            var x = parseTerm()
            while (true) {
                when {
                    eat('+'.code) -> x += parseTerm()
                    eat('-'.code) -> x -= parseTerm()
                    else -> return x
                }
            }
        }

        fun parseTerm(): Double {
            var x = parseFactor()
            while (true) {
                when {
                    eat('*'.code) -> x *= parseFactor()
                    eat('/'.code) -> x /= parseFactor()
                    else -> return x
                }
            }
        }

        fun parseFactor(): Double {
            if (eat('+'.code)) return parseFactor()
            if (eat('-'.code)) return -parseFactor()

            var x: Double
            val startPos = pos
            if (eat('('.code)) {
                x = parseExpression()
                eat(')'.code)
            } else if (ch >= '0'.code && ch <= '9'.code || ch == '.'.code) {
                while (ch >= '0'.code && ch <= '9'.code || ch == '.'.code) nextChar()
                x = expression.substring(startPos, pos).toDouble()
            } else {
                throw RuntimeException("Unexpected: ${ch.toChar()}")
            }

            return x
        }
    }.parse()
}