package cc.darak.aptanywhere.util

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLinkStyles
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration

fun String.toPhoneAnnotatedString(onPhoneClick: (String) -> Unit): AnnotatedString {
    val phoneRegex = Regex("""\d{2,3}-?\d{3,4}-?\d{4}""")

    return buildAnnotatedString {
        append(this@toPhoneAnnotatedString)

        phoneRegex.findAll(this@toPhoneAnnotatedString).forEach { result ->
            val start = result.range.first
            val end = result.range.last + 1
            val pureNumber = result.value.replace("-", "")

            addLink(
                clickable = LinkAnnotation.Clickable(
                    tag = "phone",
                    styles = TextLinkStyles(
                        style = SpanStyle(
                            color = Color(0xFF2196F3),
                            textDecoration = TextDecoration.Underline,
                            fontWeight = FontWeight.Bold
                        )
                    ),
                    // Connect callback
                    linkInteractionListener = { _ -> onPhoneClick(pureNumber) }
                ),
                start = start,
                end = end
            )
        }
    }
}