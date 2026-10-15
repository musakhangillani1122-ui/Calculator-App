package com.example.scientificcalculator

import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.Locale
import kotlin.math.*

private enum class AngleMode {
    DEG,
    RAD,
    GRAD
}

class MainActivity : AppCompatActivity() {

    private lateinit var tvStatus: TextView
    private lateinit var tvExpression: TextView
    private lateinit var tvResult: TextView

    private val expression = StringBuilder()

    private var justCalculated = false
    private var shiftMode = false

    private var angleMode = AngleMode.DEG

    private var memory = 0.0
    private var lastAnswer = 0.0


    // ============================================================
    // ACTIVITY
    // ============================================================

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        setContentView(R.layout.activity_main)

        ViewCompat.setOnApplyWindowInsetsListener(
            findViewById(R.id.main)
        ) { v, insets ->

            val systemBars =
                insets.getInsets(
                    WindowInsetsCompat.Type.systemBars()
                )

            v.setPadding(
                systemBars.left,
                systemBars.top,
                systemBars.right,
                systemBars.bottom
            )

            insets
        }

        tvStatus =
            findViewById(R.id.tvStatus)

        tvExpression =
            findViewById(R.id.tvExpression)

        tvResult =
            findViewById(R.id.tvResult)

        applyDesign()
        setupButtons()

        updateStatus()
        updateShiftLabels()
    }


    // ============================================================
    // DESIGN
    // ============================================================

    private fun applyDesign() {

        val displayPanel =
            findViewById<LinearLayout>(
                R.id.displayPanel
            )

        displayPanel.setBackgroundColor(
            Color.rgb(216, 216, 210)
        )

        tvStatus.setTextColor(
            Color.BLACK
        )

        tvExpression.setTextColor(
            Color.rgb(55, 55, 55)
        )

        tvResult.setTextColor(
            Color.BLACK
        )

        tvStatus.typeface =
            Typeface.create(
                Typeface.MONOSPACE,
                Typeface.BOLD
            )

        tvExpression.typeface =
            Typeface.create(
                Typeface.MONOSPACE,
                Typeface.NORMAL
            )

        tvResult.typeface =
            Typeface.create(
                Typeface.MONOSPACE,
                Typeface.BOLD
            )


        // ========================================================
        // TOP CONTROL KEYS
        // ========================================================

        styleGroup(
            listOf(
                R.id.btnShift,
                R.id.btnDrg,
                R.id.btnMc,
                R.id.btnMr,
                R.id.btnMemory
            ),
            "#D0D0D0",
            "#111111"
        )


        // ========================================================
        // SCIENTIFIC KEYS
        // ========================================================

        styleGroup(
            listOf(
                R.id.btnSin,
                R.id.btnCos,
                R.id.btnTan,
                R.id.btnLog,
                R.id.btnLn,
                R.id.btnSquare,
                R.id.btnPower,
                R.id.btnSqrt,
                R.id.btnFactorial,
                R.id.btnInverse,
                R.id.btnNcr,
                R.id.btnNpr,
                R.id.btnAbs,
                R.id.btnExp,
                R.id.btnTenPower
            ),
            "#F0F0F0",
            "#111111"
        )


        // ========================================================
        // CONTROL KEYS
        // ========================================================

        styleGroup(
            listOf(
                R.id.btnOpen,
                R.id.btnClose,
                R.id.btnPercent
            ),
            "#BEBEBE",
            "#111111"
        )


        // ========================================================
        // AC / DELETE
        // ========================================================

        styleGroup(
            listOf(
                R.id.btnClear,
                R.id.btnDelete
            ),
            "#A9A9A9",
            "#111111"
        )


        // ========================================================
        // NUMBER KEYS
        // ========================================================

        styleGroup(
            listOf(
                R.id.btn0,
                R.id.btn1,
                R.id.btn2,
                R.id.btn3,
                R.id.btn4,
                R.id.btn5,
                R.id.btn6,
                R.id.btn7,
                R.id.btn8,
                R.id.btn9,
                R.id.btnDot,
                R.id.btnPi,
                R.id.btnE,
                R.id.btnSign,
                R.id.btnAns
            ),
            "#252525",
            "#FFFFFF"
        )


        // ========================================================
        // OPERATORS
        // ========================================================

        styleGroup(
            listOf(
                R.id.btnDivide,
                R.id.btnMultiply,
                R.id.btnMinus,
                R.id.btnPlus
            ),
            "#E6E6E6",
            "#111111"
        )


        // ========================================================
        // EQUAL
        // ========================================================

        styleGroup(
            listOf(
                R.id.btnEquals
            ),
            "#FFFFFF",
            "#000000"
        )
    }


    private fun styleGroup(
        ids: List<Int>,
        backgroundColor: String,
        textColor: String
    ) {

        ids.forEach { id ->

            val button =
                findViewById<Button>(id)

            val background =
                Color.parseColor(
                    backgroundColor
                )

            val foreground =
                Color.parseColor(
                    textColor
                )

            button.backgroundTintList =
                ColorStateList.valueOf(
                    background
                )

            button.setTextColor(
                foreground
            )

            button.setAllCaps(false)

            button.typeface =
                Typeface.create(
                    Typeface.SANS_SERIF,
                    Typeface.BOLD
                )

            button.stateListAnimator = null

            button.minimumHeight = 0
            button.minimumWidth = 0

            button.setPadding(
                0,
                0,
                0,
                0
            )
        }
    }


    // ============================================================
    // BUTTON SETUP
    // ============================================================

    private fun setupButtons() {

        // SHIFT
        findViewById<Button>(
            R.id.btnShift
        ).setOnClickListener {

            shiftMode =
                !shiftMode

            updateShiftLabels()
            updateStatus()
        }


        // DEG / RAD / GRAD
        findViewById<Button>(
            R.id.btnDrg
        ).setOnClickListener {

            angleMode =
                when (angleMode) {

                    AngleMode.DEG ->
                        AngleMode.RAD

                    AngleMode.RAD ->
                        AngleMode.GRAD

                    AngleMode.GRAD ->
                        AngleMode.DEG
                }

            updateStatus()
        }


        // MEMORY CLEAR
        findViewById<Button>(
            R.id.btnMc
        ).setOnClickListener {

            memory = 0.0

            updateStatus()
        }


        // MEMORY RECALL
        findViewById<Button>(
            R.id.btnMr
        ).setOnClickListener {

            insertNumber(memory)
        }


        // MEMORY ADD / SUBTRACT
        findViewById<Button>(
            R.id.btnMemory
        ).setOnClickListener {

            val value =
                currentValue()

            if (value != null) {

                if (shiftMode) {

                    memory -= value

                } else {

                    memory += value
                }

                shiftMode = false

                updateShiftLabels()
                updateStatus()
            }
        }


        // NUMBERS

        findViewById<Button>(
            R.id.btn0
        ).setOnClickListener {
            appendNumber("0")
        }

        findViewById<Button>(
            R.id.btn1
        ).setOnClickListener {
            appendNumber("1")
        }

        findViewById<Button>(
            R.id.btn2
        ).setOnClickListener {
            appendNumber("2")
        }

        findViewById<Button>(
            R.id.btn3
        ).setOnClickListener {
            appendNumber("3")
        }

        findViewById<Button>(
            R.id.btn4
        ).setOnClickListener {
            appendNumber("4")
        }

        findViewById<Button>(
            R.id.btn5
        ).setOnClickListener {
            appendNumber("5")
        }

        findViewById<Button>(
            R.id.btn6
        ).setOnClickListener {
            appendNumber("6")
        }

        findViewById<Button>(
            R.id.btn7
        ).setOnClickListener {
            appendNumber("7")
        }

        findViewById<Button>(
            R.id.btn8
        ).setOnClickListener {
            appendNumber("8")
        }

        findViewById<Button>(
            R.id.btn9
        ).setOnClickListener {
            appendNumber("9")
        }


        // DECIMAL

        findViewById<Button>(
            R.id.btnDot
        ).setOnClickListener {

            if (justCalculated) {

                expression.clear()
                justCalculated = false
            }

            appendDecimal()
        }


        // OPERATORS

        findViewById<Button>(
            R.id.btnPlus
        ).setOnClickListener {

            appendOperator("+")
        }

        findViewById<Button>(
            R.id.btnMinus
        ).setOnClickListener {

            appendOperator("-")
        }

        findViewById<Button>(
            R.id.btnMultiply
        ).setOnClickListener {

            appendOperator("*")
        }

        findViewById<Button>(
            R.id.btnDivide
        ).setOnClickListener {

            appendOperator("/")
        }

        findViewById<Button>(
            R.id.btnPower
        ).setOnClickListener {

            appendOperator("^")
        }


        // PARENTHESES

        findViewById<Button>(
            R.id.btnOpen
        ).setOnClickListener {

            appendOpenParenthesis()
        }

        findViewById<Button>(
            R.id.btnClose
        ).setOnClickListener {

            appendCloseParenthesis()
        }


        // PERCENT

        findViewById<Button>(
            R.id.btnPercent
        ).setOnClickListener {

            if (
                expression.isNotEmpty()
            ) {

                expression.append("%")

                updateDisplay()
            }
        }


        // SIN

        findViewById<Button>(
            R.id.btnSin
        ).setOnClickListener {

            if (shiftMode) {

                appendFunction("asin")
                finishShift()

            } else {

                appendFunction("sin")
            }
        }


        // COS

        findViewById<Button>(
            R.id.btnCos
        ).setOnClickListener {

            if (shiftMode) {

                appendFunction("acos")
                finishShift()

            } else {

                appendFunction("cos")
            }
        }


        // TAN

        findViewById<Button>(
            R.id.btnTan
        ).setOnClickListener {

            if (shiftMode) {

                appendFunction("atan")
                finishShift()

            } else {

                appendFunction("tan")
            }
        }


        // LOG

        findViewById<Button>(
            R.id.btnLog
        ).setOnClickListener {

            if (shiftMode) {

                appendRawFunction("10^(")
                finishShift()

            } else {

                appendFunction("log")
            }
        }


        // LN

        findViewById<Button>(
            R.id.btnLn
        ).setOnClickListener {

            if (shiftMode) {

                appendFunction("exp")
                finishShift()

            } else {

                appendFunction("ln")
            }
        }


        // SQUARE

        findViewById<Button>(
            R.id.btnSquare
        ).setOnClickListener {

            if (shiftMode) {

                applyPowerToCurrent(3.0)
                finishShift()

            } else {

                applyPowerToCurrent(2.0)
            }
        }


        // ROOT

        findViewById<Button>(
            R.id.btnSqrt
        ).setOnClickListener {

            if (shiftMode) {

                appendFunction("cbrt")
                finishShift()

            } else {

                appendFunction("sqrt")
            }
        }


        // FACTORIAL

        findViewById<Button>(
            R.id.btnFactorial
        ).setOnClickListener {

            if (
                expression.isNotEmpty() ||
                justCalculated
            ) {

                if (justCalculated) {

                    expression.clear()

                    expression.append(
                        formatForExpression(
                            lastAnswer
                        )
                    )

                    justCalculated = false
                }

                expression.append("!")

                updateDisplay()
            }
        }


        // INVERSE / ABS

        findViewById<Button>(
            R.id.btnInverse
        ).setOnClickListener {

            if (shiftMode) {

                applyUnaryFunction("abs")
                finishShift()

            } else {

                applyReciprocal()
            }
        }


        // nCr

        findViewById<Button>(
            R.id.btnNcr
        ).setOnClickListener {

            appendFunction("nCr")
        }


        // nPr

        findViewById<Button>(
            R.id.btnNpr
        ).setOnClickListener {

            appendFunction("nPr")
        }


        // ABS

        findViewById<Button>(
            R.id.btnAbs
        ).setOnClickListener {

            appendFunction("abs")
        }


        // EXP

        findViewById<Button>(
            R.id.btnExp
        ).setOnClickListener {

            appendFunction("exp")
        }


        // 10^X

        findViewById<Button>(
            R.id.btnTenPower
        ).setOnClickListener {

            appendRawFunction("10^(")
        }


        // PI

        findViewById<Button>(
            R.id.btnPi
        ).setOnClickListener {

            appendConstant("pi")
        }


        // E

        findViewById<Button>(
            R.id.btnE
        ).setOnClickListener {

            appendConstant("e")
        }


        // ANS

        findViewById<Button>(
            R.id.btnAns
        ).setOnClickListener {

            appendConstant("ans")
        }


        // SIGN

        findViewById<Button>(
            R.id.btnSign
        ).setOnClickListener {

            toggleSign()
        }


        // DELETE

        findViewById<Button>(
            R.id.btnDelete
        ).setOnClickListener {

            deleteLast()
        }


        // CLEAR

        findViewById<Button>(
            R.id.btnClear
        ).setOnClickListener {

            expression.clear()

            tvExpression.text = ""
            tvResult.text = "0"

            justCalculated = false
            shiftMode = false

            updateShiftLabels()
            updateStatus()
        }


        // EQUAL

        findViewById<Button>(
            R.id.btnEquals
        ).setOnClickListener {

            calculateResult()
        }
    }


    // ============================================================
    // SHIFT LABELS
    // ============================================================

    private fun updateShiftLabels() {

        val sin =
            findViewById<Button>(
                R.id.btnSin
            )

        val cos =
            findViewById<Button>(
                R.id.btnCos
            )

        val tan =
            findViewById<Button>(
                R.id.btnTan
            )

        val log =
            findViewById<Button>(
                R.id.btnLog
            )

        val ln =
            findViewById<Button>(
                R.id.btnLn
            )

        val square =
            findViewById<Button>(
                R.id.btnSquare
            )

        val sqrt =
            findViewById<Button>(
                R.id.btnSqrt
            )

        val inverse =
            findViewById<Button>(
                R.id.btnInverse
            )

        val memoryButton =
            findViewById<Button>(
                R.id.btnMemory
            )


        if (shiftMode) {

            findViewById<Button>(
                R.id.btnShift
            ).text = "SHIFT*"

            sin.text = "sin⁻¹"
            cos.text = "cos⁻¹"
            tan.text = "tan⁻¹"

            log.text = "10ˣ"
            ln.text = "eˣ"

            square.text = "x³"
            sqrt.text = "∛"

            inverse.text = "abs"

            memoryButton.text = "M−"

        } else {

            findViewById<Button>(
                R.id.btnShift
            ).text = "SHIFT"

            sin.text = "sin"
            cos.text = "cos"
            tan.text = "tan"

            log.text = "log"
            ln.text = "ln"

            square.text = "x²"
            sqrt.text = "√"

            inverse.text = "1/x"

            memoryButton.text = "M+"
        }
    }


    private fun finishShift() {

        shiftMode = false

        updateShiftLabels()
        updateStatus()
    }


    // ============================================================
    // STATUS
    // ============================================================

    private fun updateStatus() {

        val modeText =
            angleMode.name

        val memoryText =
            if (
                abs(memory) > 1e-12
            ) {
                "   M"
            } else {
                ""
            }

        val shiftText =
            if (shiftMode) {
                "   SHIFT"
            } else {
                ""
            }

        val buttonText =
            when (angleMode) {

                AngleMode.DEG ->
                    "DEG"

                AngleMode.RAD ->
                    "RAD"

                AngleMode.GRAD ->
                    "GRAD"
            }

        findViewById<Button>(
            R.id.btnDrg
        ).text = buttonText

        tvStatus.text =
            "$modeText$memoryText$shiftText"
    }


    // ============================================================
    // FIXED NUMBER INPUT
    // ============================================================

    private fun appendNumber(
        number: String
    ) {

        /*
         * IMPORTANT:
         *
         * If the user enters:
         *
         * 333
         *
         * we must create:
         *
         * 333
         *
         * NOT:
         *
         * 3 × 3 × 3
         *
         *
         * Automatic multiplication is only needed when a
         * number follows a completed value such as:
         *
         * 5)
         * 5!
         * 5%
         * π2
         * e2
         * Ans2
         *
         * It is NOT needed when the previous character is
         * simply another digit.
         */

        if (justCalculated) {

            expression.clear()

            justCalculated = false
        }


        if (needsMultiplicationBeforeNumber()) {

            expression.append("*")
        }


        expression.append(number)

        updateDisplay()
    }


    private fun needsMultiplicationBeforeNumber(): Boolean {

        if (expression.isEmpty()) {
            return false
        }


        val text =
            expression.toString()


        /*
         * Number immediately after ')' requires multiplication.
         *
         * Example:
         *
         * (5+3)2
         *
         * becomes:
         *
         * (5+3) × 2
         */

        if (text.endsWith(")")) {
            return true
        }


        /*
         * Factorial followed by number.
         *
         * 5!2
         *
         * becomes:
         *
         * 5! × 2
         */

        if (text.endsWith("!")) {
            return true
        }


        /*
         * Percentage followed by number.
         */

        if (text.endsWith("%")) {
            return true
        }


        /*
         * Constants followed by number.
         */

        if (text.endsWith("pi")) {
            return true
        }

        if (text.endsWith("ans")) {
            return true
        }

        if (text.endsWith("e")) {
            return true
        }


        /*
         * If the expression ends in a digit,
         * we are continuing the SAME number.
         *
         * Example:
         *
         * 3 → 33 → 333
         *
         * so DO NOT insert ×.
         */

        return false
    }


    private fun appendDecimal() {

        val current =
            expression.toString()

        val numberPart =
            current.takeLastWhile {
                it.isDigit() || it == '.'
            }


        if (!numberPart.contains(".")) {

            if (numberPart.isEmpty()) {

                if (
                    endsWithValue() &&
                    needsMultiplicationBeforeNumber()
                ) {
                    expression.append("*")
                }

                expression.append("0.")

            } else {

                expression.append(".")
            }
        }

        updateDisplay()
    }


    private fun appendOperator(
        operator: String
    ) {

        if (justCalculated) {

            expression.clear()

            expression.append(
                formatForExpression(
                    lastAnswer
                )
            )

            justCalculated = false
        }


        if (expression.isEmpty()) {

            if (operator == "-") {

                expression.append("-")

                updateDisplay()
            }

            return
        }


        val last =
            expression.last()


        if (
            last == '+' ||
            last == '-' ||
            last == '*' ||
            last == '/' ||
            last == '^'
        ) {

            expression.deleteCharAt(
                expression.length - 1
            )
        }


        expression.append(operator)

        updateDisplay()
    }


    private fun appendOpenParenthesis() {

        if (justCalculated) {

            expression.clear()
            justCalculated = false
        }


        if (endsWithValue()) {

            expression.append("*")
        }


        expression.append("(")

        updateDisplay()
    }


    private fun appendCloseParenthesis() {

        if (expression.isEmpty()) return


        val balance =
            countOpenParentheses() -
                    countCloseParentheses()


        if (balance <= 0) return


        val last =
            expression.last()


        if (
            last.isDigit() ||
            last == ')' ||
            last == '!' ||
            last == '%'
        ) {

            expression.append(")")

            updateDisplay()
        }
    }


    // ============================================================
    // FUNCTIONS
    // ============================================================

    private fun appendFunction(
        function: String
    ) {

        if (justCalculated) {

            val value =
                formatForExpression(
                    lastAnswer
                )

            expression.clear()

            expression.append(function)
            expression.append("(")
            expression.append(value)
            expression.append(")")

            justCalculated = false

        } else {

            if (endsWithValue()) {

                expression.append("*")
            }

            expression.append(function)
            expression.append("(")
        }

        updateDisplay()
    }


    private fun appendRawFunction(
        raw: String
    ) {

        if (justCalculated) {

            expression.clear()
            justCalculated = false
        }


        if (endsWithValue()) {

            expression.append("*")
        }


        expression.append(raw)

        updateDisplay()
    }


    private fun appendConstant(
        constant: String
    ) {

        if (justCalculated) {

            expression.clear()
            justCalculated = false
        }


        if (endsWithValue()) {

            expression.append("*")
        }


        expression.append(constant)

        updateDisplay()
    }


    private fun insertNumber(
        value: Double
    ) {

        val formatted =
            formatForExpression(
                value
            )


        if (justCalculated) {

            expression.clear()
            justCalculated = false
        }


        if (
            expression.isNotEmpty() &&
            needsMultiplicationBeforeNumber()
        ) {

            expression.append("*")
        }


        expression.append(formatted)

        updateDisplay()
    }


    // ============================================================
    // SIGN
    // ============================================================

    private fun toggleSign() {

        val source =
            getCurrentSource()

        expression.clear()

        expression.append(
            "(-1)*($source)"
        )

        justCalculated = false

        updateDisplay()
    }


    // ============================================================
    // ADVANCED OPERATIONS
    // ============================================================

    private fun applyPowerToCurrent(
        power: Double
    ) {

        val source =
            getCurrentSource()

        expression.clear()

        expression.append("(")
        expression.append(source)
        expression.append(")^")
        expression.append(
            power.toInt()
        )

        justCalculated = false

        updateDisplay()
    }


    private fun applyUnaryFunction(
        function: String
    ) {

        val source =
            getCurrentSource()

        expression.clear()

        expression.append(function)
        expression.append("(")
        expression.append(source)
        expression.append(")")

        justCalculated = false

        updateDisplay()
    }


    private fun applyReciprocal() {

        val source =
            getCurrentSource()

        expression.clear()

        expression.append("(1/(")
        expression.append(source)
        expression.append("))")

        justCalculated = false

        updateDisplay()
    }


    private fun getCurrentSource(): String {

        return if (
            expression.isNotEmpty() &&
            !justCalculated
        ) {

            expression.toString()

        } else {

            formatForExpression(
                lastAnswer
            )
        }
    }


    // ============================================================
    // DELETE
    // ============================================================

    private fun deleteLast() {

        if (expression.isEmpty()) return


        val specialTokens =
            listOf(
                "nCr(",
                "nPr(",
                "asin(",
                "acos(",
                "atan(",
                "sqrt(",
                "cbrt(",
                "sin(",
                "cos(",
                "tan(",
                "log(",
                "ln(",
                "abs(",
                "exp(",
                "10^(",
                "ans"
            )


        for (token in specialTokens) {

            if (
                expression
                    .toString()
                    .endsWith(token)
            ) {

                repeat(
                    token.length
                ) {

                    expression.deleteCharAt(
                        expression.length - 1
                    )
                }

                updateDisplay()

                return
            }
        }


        if (
            expression
                .toString()
                .endsWith("pi")
        ) {

            expression.delete(
                expression.length - 2,
                expression.length
            )

        } else {

            expression.deleteCharAt(
                expression.length - 1
            )
        }

        updateDisplay()
    }


    // ============================================================
    // DISPLAY
    // ============================================================

    private fun updateDisplay() {

        val displayText =
            expression
                .toString()
                .replace(
                    "pi",
                    "π"
                )
                .replace(
                    "ans",
                    "Ans"
                )
                .replace(
                    "*",
                    "×"
                )
                .replace(
                    "/",
                    "÷"
                )
                .replace(
                    "sqrt(",
                    "√("
                )
                .replace(
                    "cbrt(",
                    "∛("
                )


        tvExpression.text =
            displayText


        if (expression.isEmpty()) {

            tvExpression.text = ""
            tvResult.text = "0"
        }
    }


    // ============================================================
    // CALCULATE
    // ============================================================

    private fun calculateResult() {

        if (expression.isEmpty()) {
            return
        }


        try {

            val result =
                ExpressionParser(
                    expression.toString(),
                    angleMode,
                    lastAnswer
                ).parse()


            if (
                result.isNaN() ||
                result.isInfinite()
            ) {

                tvResult.text = "Error"

                return
            }


            lastAnswer = result

            tvResult.text =
                formatResult(result)

            justCalculated = true


        } catch (
            e: Exception
        ) {

            tvResult.text = "Error"
        }
    }


    // ============================================================
    // CURRENT VALUE
    // ============================================================

    private fun currentValue(): Double? {

        return try {

            if (
                expression.isEmpty() ||
                justCalculated
            ) {

                lastAnswer

            } else {

                ExpressionParser(
                    expression.toString(),
                    angleMode,
                    lastAnswer
                ).parse()
            }

        } catch (
            e: Exception
        ) {

            null
        }
    }


    // ============================================================
    // HELPERS
    // ============================================================

    private fun endsWithValue(): Boolean {

        if (expression.isEmpty()) {
            return false
        }


        val text =
            expression
                .toString()
                .lowercase()


        return text.endsWith("pi") ||
                text.endsWith("ans") ||
                text.endsWith("e") ||
                text.endsWith(")") ||
                text.endsWith("!") ||
                text.endsWith("%") ||
                text.lastOrNull()
                    ?.isDigit() == true
    }


    private fun countOpenParentheses(): Int {

        return expression.count {
            it == '('
        }
    }


    private fun countCloseParentheses(): Int {

        return expression.count {
            it == ')'
        }
    }


    private fun formatForExpression(
        value: Double
    ): String {

        return formatResult(value)
    }


    private fun formatResult(
        value: Double
    ): String {

        if (
            abs(value) < 1e-12
        ) {

            return "0"
        }


        val symbols =
            DecimalFormatSymbols(
                Locale.US
            )


        val formatter =
            DecimalFormat(
                "0.###############",
                symbols
            )


        return formatter.format(value)
    }


    // ============================================================
    // EXPRESSION PARSER
    // ============================================================

    private class ExpressionParser(
        private val input: String,
        private val angleMode: AngleMode,
        private val previousAnswer: Double
    ) {

        private var position = 0


        fun parse(): Double {

            val result =
                parseExpression()

            skipSpaces()


            if (
                position != input.length
            ) {

                throw IllegalArgumentException(
                    "Unexpected character"
                )
            }


            return result
        }


        // ========================================================
        // + -
        // ========================================================

        private fun parseExpression(): Double {

            var value =
                parseTerm()


            while (true) {

                skipSpaces()


                if (match("+")) {

                    value +=
                        parseTerm()

                } else if (match("-")) {

                    value -=
                        parseTerm()

                } else {

                    break
                }
            }


            return value
        }


        // ========================================================
        // * /
        // ========================================================

        private fun parseTerm(): Double {

            var value =
                parseUnary()


            while (true) {

                skipSpaces()


                if (match("*")) {

                    value *=
                        parseUnary()

                } else if (match("/")) {

                    val divisor =
                        parseUnary()


                    if (
                        divisor == 0.0
                    ) {

                        throw ArithmeticException(
                            "Division by zero"
                        )
                    }


                    value /=
                        divisor

                } else {

                    break
                }
            }


            return value
        }


        // ========================================================
        // UNARY
        // ========================================================

        private fun parseUnary(): Double {

            skipSpaces()


            if (match("+")) {

                return parseUnary()
            }


            if (match("-")) {

                return -parseUnary()
            }


            return parsePower()
        }


        // ========================================================
        // POWER
        // ========================================================

        private fun parsePower(): Double {

            var value =
                parsePostfix()


            skipSpaces()


            if (match("^")) {

                val exponent =
                    parseUnary()

                value =
                    value.pow(exponent)
            }


            return value
        }


        // ========================================================
        // POSTFIX
        // ========================================================

        private fun parsePostfix(): Double {

            var value =
                parsePrimary()


            while (true) {

                skipSpaces()


                if (match("%")) {

                    value /= 100.0

                } else if (match("!")) {

                    value =
                        factorial(value)

                } else {

                    break
                }
            }


            return value
        }


        // ========================================================
        // PRIMARY
        // ========================================================

        private fun parsePrimary(): Double {

            skipSpaces()


            if (
                position >= input.length
            ) {

                throw IllegalArgumentException(
                    "Unexpected end"
                )
            }


            if (match("(")) {

                val value =
                    parseExpression()


                skipSpaces()


                if (!match(")")) {

                    throw IllegalArgumentException(
                        "Missing )"
                    )
                }


                return value
            }


            if (
                input[position].isDigit() ||
                input[position] == '.'
            ) {

                return parseNumber()
            }


            if (
                input[position].isLetter()
            ) {

                val start =
                    position


                while (
                    position < input.length &&
                    input[position].isLetter()
                ) {

                    position++
                }


                val name =
                    input.substring(
                        start,
                        position
                    ).lowercase()


                when (name) {

                    "pi" ->
                        return PI

                    "e" ->
                        return E

                    "ans" ->
                        return previousAnswer
                }


                if (!match("(")) {

                    throw IllegalArgumentException(
                        "Missing ("
                    )
                }


                val first =
                    parseExpression()


                skipSpaces()


                var second:
                        Double? = null


                if (match(",")) {

                    second =
                        parseExpression()
                }


                skipSpaces()


                if (!match(")")) {

                    throw IllegalArgumentException(
                        "Missing )"
                    )
                }


                return evaluateFunction(
                    name,
                    first,
                    second
                )
            }


            throw IllegalArgumentException(
                "Invalid expression"
            )
        }


        // ========================================================
        // FUNCTIONS
        // ========================================================

        private fun evaluateFunction(
            name: String,
            argument: Double,
            secondArgument: Double?
        ): Double {

            return when (name) {

                "sin" ->
                    sin(
                        toRadians(argument)
                    )


                "cos" ->
                    cos(
                        toRadians(argument)
                    )


                "tan" -> {

                    val radians =
                        toRadians(argument)

                    val cosine =
                        cos(radians)


                    if (
                        abs(cosine) < 1e-12
                    ) {

                        throw ArithmeticException(
                            "Undefined"
                        )
                    }


                    tan(radians)
                }


                "asin" ->
                    fromRadians(
                        asin(argument)
                    )


                "acos" ->
                    fromRadians(
                        acos(argument)
                    )


                "atan" ->
                    fromRadians(
                        atan(argument)
                    )


                "log" -> {

                    if (
                        argument <= 0
                    ) {

                        throw ArithmeticException(
                            "Invalid log"
                        )
                    }

                    log10(argument)
                }


                "ln" -> {

                    if (
                        argument <= 0
                    ) {

                        throw ArithmeticException(
                            "Invalid ln"
                        )
                    }

                    ln(argument)
                }


                "sqrt" -> {

                    if (
                        argument < 0
                    ) {

                        throw ArithmeticException(
                            "Invalid sqrt"
                        )
                    }

                    sqrt(argument)
                }


                "cbrt" ->
                    cbrt(argument)


                "abs" ->
                    abs(argument)


                "exp" ->
                    exp(argument)


                "ncr" -> {

                    if (
                        secondArgument == null
                    ) {

                        throw IllegalArgumentException(
                            "nCr requires two values"
                        )
                    }


                    combination(
                        argument,
                        secondArgument
                    )
                }


                "npr" -> {

                    if (
                        secondArgument == null
                    ) {

                        throw IllegalArgumentException(
                            "nPr requires two values"
                        )
                    }


                    permutation(
                        argument,
                        secondArgument
                    )
                }


                else ->
                    throw IllegalArgumentException(
                        "Unknown function"
                    )
            }
        }


        // ========================================================
        // NUMBER
        // ========================================================

        private fun parseNumber(): Double {

            val start =
                position


            var hasDecimal =
                false


            while (
                position < input.length
            ) {

                val ch =
                    input[position]


                if (ch.isDigit()) {

                    position++

                } else if (
                    ch == '.' &&
                    !hasDecimal
                ) {

                    hasDecimal = true
                    position++

                } else {

                    break
                }
            }


            val text =
                input.substring(
                    start,
                    position
                )


            if (
                text == "." ||
                text.isEmpty()
            ) {

                throw IllegalArgumentException(
                    "Invalid number"
                )
            }


            return text.toDouble()
        }


        // ========================================================
        // FACTORIAL
        // ========================================================

        private fun factorial(
            value: Double
        ): Double {

            if (
                !value.isFinite() ||
                value < 0
            ) {

                throw ArithmeticException(
                    "Invalid factorial"
                )
            }


            val n =
                round(value)


            if (
                abs(value - n) > 1e-10
            ) {

                throw ArithmeticException(
                    "Factorial needs an integer"
                )
            }


            if (n > 170) {

                throw ArithmeticException(
                    "Factorial too large"
                )
            }


            var result =
                1.0


            var i = 2


            while (
                i <= n.toInt()
            ) {

                result *= i

                i++
            }


            return result
        }


        // ========================================================
        // nCr
        // ========================================================

        private fun combination(
            nValue: Double,
            rValue: Double
        ): Double {

            val n =
                toInteger(nValue)

            val r =
                toInteger(rValue)


            if (
                n < 0 ||
                r < 0 ||
                r > n
            ) {

                throw ArithmeticException(
                    "Invalid nCr"
                )
            }


            val k =
                minOf(
                    r,
                    n - r
                )


            var result =
                1.0


            for (i in 1..k) {

                result *=
                    (n - k + i).toDouble()

                result /=
                    i.toDouble()
            }


            return result
        }


        // ========================================================
        // nPr
        // ========================================================

        private fun permutation(
            nValue: Double,
            rValue: Double
        ): Double {

            val n =
                toInteger(nValue)

            val r =
                toInteger(rValue)


            if (
                n < 0 ||
                r < 0 ||
                r > n
            ) {

                throw ArithmeticException(
                    "Invalid nPr"
                )
            }


            var result =
                1.0


            for (i in 1..r) {

                result *=
                    (n - i + 1).toDouble()
            }


            return result
        }


        private fun toInteger(
            value: Double
        ): Int {

            if (!value.isFinite()) {

                throw ArithmeticException(
                    "Invalid integer"
                )
            }


            val rounded =
                round(value)


            if (
                abs(value - rounded) > 1e-10
            ) {

                throw ArithmeticException(
                    "Integer required"
                )
            }


            return rounded.toInt()
        }


        // ========================================================
        // ANGLE MODE
        // ========================================================

        private fun toRadians(
            value: Double
        ): Double {

            return when (angleMode) {

                AngleMode.DEG ->
                    Math.toRadians(value)

                AngleMode.RAD ->
                    value

                AngleMode.GRAD ->
                    value * PI / 200.0
            }
        }


        private fun fromRadians(
            value: Double
        ): Double {

            return when (angleMode) {

                AngleMode.DEG ->
                    Math.toDegrees(value)

                AngleMode.RAD ->
                    value

                AngleMode.GRAD ->
                    value * 200.0 / PI
            }
        }


        // ========================================================
        // MATCH
        // ========================================================

        private fun skipSpaces() {

            while (
                position < input.length &&
                input[position].isWhitespace()
            ) {

                position++
            }
        }


        private fun match(
            value: String
        ): Boolean {

            skipSpaces()


            if (
                input.startsWith(
                    value,
                    position,
                    ignoreCase = false
                )
            ) {

                position += value.length

                return true
            }


            return false
        }
    }
}