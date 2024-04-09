package com.example.calculator

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import com.example.calculator.databinding.ActivityMainBinding
import java.util.Stack

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        val textView = binding.textView3
        val inputView = binding.textView4

        // Find all buttons
        val buttons = listOf(
                binding.button0,
                binding.button1,
                binding.button2,
                binding.button3,
                binding.button4,
                binding.button5,
                binding.button6,
                binding.button7,
                binding.button8,
                binding.button9,
                binding.buttonDivide,
                binding.buttonMultiply,
                binding.buttonPlus,
                binding.buttonSubtract
        )

        // Set click listeners for each button
        for (button in buttons) {
            button.setOnClickListener {
                // Append the text of the clicked button to the TextView
                textView.append((it as Button).text)
            }
        }

        binding.buttonEqual.setOnClickListener {
            val infixExpression = textView.text.toString()
            val result = evaluateExpression(infixExpression)
            inputView.text = infixExpression
            textView.text = result
        }

        binding.buttonAC.setOnClickListener {
            textView.text = ""
            inputView.text = ""
        }
    }

    fun evaluateExpression(expression: String): String {
        val operatorStack = Stack<Char>()
        val operandStack = Stack<Double>()
        var currentNumber = ""
        var isError = false
        var lastCharWasOperator = true // To handle unary minus

        fun applyOperator(operator: Char) {
            val operand2 = operandStack.pop()
            val operand1 = operandStack.pop()
            when (operator) {
                '+' -> operandStack.push(operand1 + operand2)
                '-' -> operandStack.push(operand1 - operand2)
                '*' -> operandStack.push(operand1 * operand2)
                '/' -> {
                    if (operand2 == 0.0) {
                        isError = true
                        return
                    }
                    operandStack.push(operand1 / operand2)
                }
            }
        }

        fun precedence(operator: Char): Int {
            return when (operator) {
                '+', '-' -> 1
                '*', '/' -> 2
                else -> 0
            }
        }

        for (char in expression) {
            if (char in '0'..'9' || char == '.') {
                currentNumber += char
                lastCharWasOperator = false
            } else {
                if (char == '-' && lastCharWasOperator) {
                    currentNumber += char // This is unary minus
                } else {
                    if (currentNumber.isNotEmpty()) {
                        operandStack.push(currentNumber.toDouble())
                        currentNumber = ""
                    }
                    while (!operatorStack.isEmpty() && precedence(operatorStack.peek()) >= precedence(char)) {
                        applyOperator(operatorStack.pop())
                    }
                    operatorStack.push(char)
                }
                lastCharWasOperator = true
            }
        }
        if (currentNumber.isNotEmpty()) {
            operandStack.push(currentNumber.toDouble())
        }
        while (!operatorStack.isEmpty()) {
            applyOperator(operatorStack.pop())
        }
        if (isError || operandStack.isEmpty() || operandStack.size > 1) {
            return "Wrong input"
        }
        return String.format("%.2f", operandStack.pop())
    }
}
