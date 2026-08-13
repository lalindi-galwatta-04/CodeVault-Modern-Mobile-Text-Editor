package com.example.codevault.ui.editor

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation


fun highlightKotlinCode(
    code: String
): AnnotatedString {

    val builder = AnnotatedString.Builder(code)

    val keywordColor = Color(0xFF7B1FA2)

    val stringColor = Color(0xFF2E7D32)

    val commentColor = Color(0xFF757575)

    val numberColor = Color(0xFF1565C0)


    val keywords = listOf(

        "as",
        "break",
        "class",
        "continue",
        "do",
        "else",
        "false",
        "for",
        "fun",
        "if",
        "in",
        "interface",
        "is",
        "null",
        "object",
        "package",
        "return",
        "super",
        "this",
        "throw",
        "true",
        "try",
        "typealias",
        "typeof",
        "val",
        "var",
        "when",
        "while",

        "by",
        "catch",
        "constructor",
        "delegate",
        "dynamic",
        "field",
        "file",
        "finally",
        "get",
        "import",
        "init",
        "param",
        "property",
        "receiver",
        "set",
        "setparam",
        "where"
    )


    val keywordPattern = Regex(
        "\\b(" +
                keywords.joinToString("|") +
                ")\\b"
    )

    keywordPattern
        .findAll(code)
        .forEach { match ->

            builder.addStyle(

                style = SpanStyle(

                    color = keywordColor,

                    fontWeight =
                        FontWeight.SemiBold
                ),

                start =
                    match.range.first,

                end =
                    match.range.last + 1
            )
        }


    val numberPattern = Regex(
        "\\b\\d+(\\.\\d+)?\\b"
    )

    numberPattern
        .findAll(code)
        .forEach { match ->

            builder.addStyle(

                style = SpanStyle(
                    color = numberColor
                ),

                start =
                    match.range.first,

                end =
                    match.range.last + 1
            )
        }


    val stringPattern = Regex(
        "\"(?:\\\\.|[^\"\\\\])*\""
    )

    stringPattern
        .findAll(code)
        .forEach { match ->

            builder.addStyle(

                style = SpanStyle(
                    color = stringColor
                ),

                start =
                    match.range.first,

                end =
                    match.range.last + 1
            )
        }


    val singleLineCommentPattern = Regex(
        "//.*"
    )

    singleLineCommentPattern
        .findAll(code)
        .forEach { match ->

            builder.addStyle(

                style = SpanStyle(

                    color = commentColor,

                    fontStyle =
                        FontStyle.Italic
                ),

                start =
                    match.range.first,

                end =
                    match.range.last + 1
            )
        }



    val multiLineCommentPattern = Regex(
        "/\\*[\\s\\S]*?\\*/"
    )

    multiLineCommentPattern
        .findAll(code)
        .forEach { match ->

            builder.addStyle(

                style = SpanStyle(

                    color = commentColor,

                    fontStyle =
                        FontStyle.Italic
                ),

                start =
                    match.range.first,

                end =
                    match.range.last + 1
            )
        }


    return builder.toAnnotatedString()
}



class KotlinSyntaxVisualTransformation :
    VisualTransformation {

    override fun filter(
        text: AnnotatedString
    ): TransformedText {



        val originalCode =
            text.text



        val highlightedCode =
            highlightKotlinCode(
                code = originalCode
            )

        return TransformedText(

            text = highlightedCode,

            offsetMapping =
                OffsetMapping.Identity
        )
    }
}