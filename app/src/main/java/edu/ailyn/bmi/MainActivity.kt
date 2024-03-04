/*
* Ailyn Diaz
* 2/26/24
* CSC ANDROID---> BMI calculator
*
* */


package edu.ailyn.bmi

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import edu.ailyn.bmi.ui.theme.BmiTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            BmiTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = Color.Gray // Set background color to grey
                ) {
                    BmiCalculator()
                }
            }
        }
    }
}

@Composable
fun BmiCalculator() {
    var weight by remember { mutableStateOf("") }
    var height by remember { mutableStateOf("") }
    var bmi by remember { mutableFloatStateOf(0f) }
    var showDialog by remember { mutableStateOf(false) }
    val focusRequester = remember { FocusRequester() }

    Column(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Header
        Text(
            text = "Calculate BMI",
            fontSize = 24.sp,
            textAlign = TextAlign.Center,
            color = Color.Black,
            fontWeight = FontWeight.Bold
        )
        // Divider for visual separation
        Divider(color = Color.Black, thickness = 2.dp)
        Text(
            text = "English Formula: weight (lb) / [height (in)]2 x 703 ",
            fontSize = 13.sp,
            textAlign = TextAlign.Center,
        )

        // Weight input
        OutlinedTextField(
            value = weight,
            onValueChange = { weight = it },
            label = { Text("Weight (lb)") },
            keyboardOptions = KeyboardOptions.Default.copy(
                keyboardType = KeyboardType.Number, // Set keyboard type to number
                imeAction = ImeAction.Next
            ),
            keyboardActions = KeyboardActions(
                onNext = { focusRequester.requestFocus() }
            )
        )

        // Height input
        OutlinedTextField(
            value = height,
            onValueChange = { height = it },
            label = { Text("Height (in)") },
            keyboardOptions = KeyboardOptions.Default.copy(
                keyboardType = KeyboardType.Number, // Set keyboard type to number
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(
                onDone = {
                    calculateBMI(weight, height)?.let {
                        bmi = it
                        showDialog = true
                    } ?: run {
                        // Show error dialog if calculation fails
                        showDialog = true
                    }
                }
            ),
            modifier = Modifier.focusRequester(focusRequester)
        )

        // Calculate button
        Button(
            onClick = {
                calculateBMI(weight, height)?.let {
                    bmi = it
                    showDialog = true
                } ?: run {
                    // Show error dialog if calculation fails
                    showDialog = true
                }
            },
            colors = ButtonDefaults.buttonColors(Color.Black)
        ) {
            Text("Calculate BMI")
        }

        // Circular progress indicator with BMI value inside
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.size(200.dp)
        ) {
            val progressValue = calculateProgress(bmi) / 100 // Normalize the progress value to be between 0 and 1
            val infiniteTransition = rememberInfiniteTransition(label = "")
            val progressAnimationValue by infiniteTransition.animateFloat(
                initialValue = 0.0f,
                targetValue = progressValue,
                animationSpec = infiniteRepeatable(
                    animation = tween(durationMillis = 900),
                    repeatMode = RepeatMode.Reverse
                ), label = ""
            )

            CircularProgressIndicator(
                progress = progressAnimationValue,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(12.dp)
                    .clip(CircleShape),
                color = determineProgressColor(bmi),
                strokeWidth = 12.dp
            )
            Text(
                text = "%.1f".format(bmi),
                fontSize = 24.sp,
                textAlign = TextAlign.Center,
                color = Color.Black
            )
        }



        // BMI categories
        Column(
            modifier = Modifier.padding(vertical = 8.dp, horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Title
            Text(
                text = "BMI Categories",
                color = Color.Black,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            // Divider for visual separation
            Divider(color = Color.Black, thickness = 2.dp, modifier = Modifier.padding(vertical = 8.dp))

            // BMI Category Rows
            val bmiCategories = listOf(
                "Underweight" to "<= 18.5",
                "Normal weight" to "18.5 - 24.9",
                "Overweight" to "25 - 29.9",
                "Obese Class I" to "30 - 34.9",
                "Obese Class II" to "35 - 39.9",
                "Obese Class III" to ">= 40"
            )

            bmiCategories.forEach { (category, range) ->
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = category,
                        color = Color.Black,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = range,
                        color = Color.Black,
                        fontSize = 16.sp
                    )
                }
            }

            // Divider for visual separation
            Divider(color = Color.Black, thickness = 1.dp, modifier = Modifier.padding(vertical = 2.dp))

            // Additional Information
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Normal weight range:",
                    color = Color.Black,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
                val normalWeightBmi = calculateBmiForNormalWeight()
                Text(
                    text = "(BMI: $normalWeightBmi lbs)",
                    color = Color.Black,
                    fontSize = 16.sp
                )
            }
        }


        // Dialog showing BMI result
        if (showDialog) {
            AlertDialog(
                onDismissRequest = { showDialog = false },
                title = { Text("BMI") },
                text = {
                    Column {
                        Text("Your BMI is $bmi")
                        val status = getBmiStatus(bmi)
                        Text("You are $status")
                    }
                },
                confirmButton = {
                    Button(
                        onClick = { showDialog = false },
                        colors = ButtonDefaults.buttonColors(Color.Blue)
                    ) {
                        Text("OK")
                    }
                }
            )
        }
    }
}

// Function to calculate the BMI value for the lower limit of the "Normal weight" category
@Composable
fun calculateBmiForNormalWeight(): Float {
    val lowerLimit = 18.5f
    val height = 70 // Assume a standard height for demonstration
    return (lowerLimit * (height * height)) / 703 // Formula for BMI: weight (kg) / height^2 (m^2)
}

fun calculateBMI(weightStr: String, heightStr: String): Float? {
    val weight = weightStr.toFloatOrNull()
    val height = heightStr.toFloatOrNull()
    return if (weight != null && height != null && height != 0f) {
        val bmi = weight / (height * height) * 703
        bmi.takeIf { it.isFinite() }
    } else {
        null
    }
}

@Composable
fun getBmiStatus(bmi: Float): String {
    return when {
        bmi < 18.5f -> "Underweight"
        bmi < 25 -> "Normal weight"
        bmi < 30 -> "Overweight"
        else -> "Obese"
    }
}

@Composable
fun calculateProgress(bmi: Float): Float {
    return (bmi / 40f) * 100 // Assuming BMI scale from 0 to 40
}

@Composable
fun determineProgressColor(bmi: Float): Color {
    return when {
        bmi < 18.5f || bmi >= 30f -> Color.Red // Underweight or Obese
        bmi < 25 -> Color.Green // Normal weight
        else -> Color.Yellow // Overweight
    }
}

@Preview(showBackground = true)
@Composable
fun BmiCalculatorPreview() {
    BmiTheme {
        BmiCalculator()
    }
}
