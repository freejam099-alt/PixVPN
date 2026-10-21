@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class, ExperimentalLayoutApi::class)

package com.pixvpn.gram

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.automirrored.rounded.Send
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/** One consistent rounded Material icon family; directional actions auto-mirror. */
enum class Ico {
    BACK, SETTINGS, LOGS, POWER, CHECK, CHEVRON, LOCK, DOWN, UP, BOLT, GLOBE, REFRESH,
    COPY, SHARE, TRASH, INFO, CODE, SEND
}

private fun Ico.vector(): ImageVector = when (this) {
    Ico.BACK -> Icons.AutoMirrored.Rounded.ArrowBack
    Ico.SETTINGS -> Icons.Rounded.Tune
    Ico.LOGS -> Icons.Rounded.ReceiptLong
    Ico.POWER -> Icons.Rounded.PowerSettingsNew
    Ico.CHECK -> Icons.Rounded.Check
    Ico.CHEVRON -> Icons.Rounded.ExpandMore
    Ico.LOCK -> Icons.Rounded.Lock
    Ico.DOWN -> Icons.Rounded.South
    Ico.UP -> Icons.Rounded.North
    Ico.BOLT -> Icons.Rounded.Bolt
    Ico.GLOBE -> Icons.Rounded.Public
    Ico.REFRESH -> Icons.Rounded.Refresh
    Ico.COPY -> Icons.Rounded.ContentCopy
    Ico.SHARE -> Icons.Rounded.Share
    Ico.TRASH -> Icons.Rounded.DeleteOutline
    Ico.INFO -> Icons.Rounded.Info
    Ico.CODE -> Icons.Rounded.Code
    Ico.SEND -> Icons.AutoMirrored.Rounded.Send
}

@Composable
private fun Ico.label(): String = stringResource(when (this) {
    Ico.BACK -> R.string.a11y_back
    Ico.SETTINGS -> R.string.settings
    Ico.LOGS -> R.string.logs
    Ico.POWER -> R.string.a11y_connection
    Ico.CHECK -> R.string.a11y_selected
    Ico.CHEVRON -> R.string.a11y_options
    Ico.LOCK -> R.string.a11y_locked
    Ico.DOWN -> R.string.download
    Ico.UP -> R.string.upload
    Ico.BOLT -> R.string.ping_test
    Ico.GLOBE -> R.string.region
    Ico.REFRESH -> R.string.rescan
    Ico.COPY -> R.string.a11y_copy
    Ico.SHARE -> R.string.a11y_share
    Ico.TRASH -> R.string.a11y_delete
    Ico.INFO -> R.string.about
    Ico.CODE -> R.string.github
    Ico.SEND -> R.string.a11y_share
})

@Suppress("UNUSED_PARAMETER")
@Composable
fun Icon8(ico: Ico, tint: Color, sz: Dp = 22.dp, modifier: Modifier = Modifier, strokeW: Float = 2f) {
    Icon(ico.vector(), contentDescription = null, modifier = modifier.size(sz), tint = tint)
}

@Composable
fun LogoMark(sz: Dp) {
    androidx.compose.foundation.Image(
        painter = androidx.compose.ui.res.painterResource(R.drawable.ic_expressive_foreground),
        contentDescription = null,
        modifier = Modifier.size(sz).clip(RoundedCornerShape(sz * .3f))
            .background(androidx.compose.ui.graphics.Brush.linearGradient(listOf(
                Color(0xFFFFDF67), Color(0xFFFFAB65), Color(0xFF8BD9A4), Color(0xFF70BFFF))))
    )
}

@Composable
fun IconBtn(ico: Ico, onClick: () -> Unit, enabled: Boolean = true, tint: Color = MaterialTheme.colorScheme.onSurface) {
    val label = ico.label()
    TooltipBox(positionProvider = TooltipDefaults.rememberPlainTooltipPositionProvider(), tooltip = { PlainTooltip { Text(label) } }, state = rememberTooltipState()) {
        FilledTonalIconButton(onClick = onClick, enabled = enabled, shapes = IconButtonDefaults.shapes(),
            modifier = Modifier.size(48.dp).semantics { contentDescription = label }) {
            Icon8(ico, if (enabled) tint else MaterialTheme.colorScheme.onSurface.copy(alpha = .38f), 24.dp)
        }
    }
}

@Composable
fun AppCard(modifier: Modifier = Modifier, content: @Composable ColumnScope.() -> Unit) {
    Surface(modifier.fillMaxWidth(), shape = MaterialTheme.shapes.extraLarge,
        color = MaterialTheme.colorScheme.surfaceContainer, tonalElevation = 0.dp) {
        Column(content = content)
    }
}

@Composable
fun Group(title: String, content: @Composable ColumnScope.() -> Unit) {
    SectionTitle(title)
    AppCard { Column(Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(18.dp), content = content) }
}

@Composable
fun Field(title: String, content: @Composable () -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) { Label(title); content() }
}

@Composable
fun Page(title: String, onBack: () -> Unit, actions: @Composable RowScope.() -> Unit = {}, content: @Composable () -> Unit) {
    Column(Modifier.fillMaxSize().windowInsetsPadding(WindowInsets.safeDrawing)) {
        TopAppBar(title = { Text(title, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold) },
            navigationIcon = { IconBtn(Ico.BACK, onBack) }, actions = actions,
            windowInsets = WindowInsets(0, 0, 0, 0),
            colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent))
        Column(Modifier.weight(1f).padding(horizontal = 16.dp)) { content() }
    }
}

@Composable
fun Label(text: String) { Text(text, style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.onSurfaceVariant) }

@Composable
fun SectionTitle(text: String) {
    Text(text, modifier = Modifier.padding(start = 8.dp, top = 28.dp, bottom = 12.dp),
        style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
}

@Composable
fun Hint(text: String, error: Boolean = false) {
    Text(text, style = MaterialTheme.typography.bodySmall,
        color = if (error) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurfaceVariant)
}

@Composable
fun Banner(text: String) {
    val c = MaterialTheme.colorScheme
    Surface(color = c.secondaryContainer, contentColor = c.onSecondaryContainer, shape = MaterialTheme.shapes.large) {
        Row(Modifier.fillMaxWidth().padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon8(Ico.INFO, c.onSecondaryContainer); Spacer(Modifier.width(12.dp))
            Text(text, style = MaterialTheme.typography.bodyMedium)
        }
    }
}

@Composable
fun Pill(text: String, selected: Boolean, enabled: Boolean = true, onClick: () -> Unit) {
    TonalToggleButton(checked = selected, onCheckedChange = { if (!selected) onClick() }, enabled = enabled,
        modifier = Modifier.heightIn(min = 48.dp),
        shapes = ToggleButtonDefaults.shapes()) {
        if (selected) { Icon8(Ico.CHECK, LocalContentColor.current, 18.dp); Spacer(Modifier.width(6.dp)) }
        Text(text, style = MaterialTheme.typography.labelLarge)
    }
}

@Composable
fun <T> Chips(options: List<Pair<T, String>>, selected: T, enabled: Boolean = true, onSelect: (T) -> Unit) {
    FlowRow(Modifier.fillMaxWidth().selectableGroup(), horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp)) {
        options.forEach { (v, label) -> Pill(label, v == selected, enabled) { onSelect(v) } }
    }
}

/** Native Expressive group with spring width response and accessible overflow.
 * Large fonts never force options into clipped, fixed-width segments. */
@Composable
fun <T> Segmented(options: List<Pair<T, String>>, selected: T, enabled: Boolean = true, onSelect: (T) -> Unit) {
    ButtonGroup(modifier = Modifier.fillMaxWidth().selectableGroup(),
        horizontalArrangement = Arrangement.spacedBy(ButtonGroupDefaults.ConnectedSpaceBetween),
        overflowIndicator = { menu ->
            Box {
                IconBtn(Ico.CHEVRON, { menu.show() }, enabled)
                DropdownMenu(expanded = menu.isExpanded, onDismissRequest = { menu.dismiss() }) {
                    options.forEach { (value, label) ->
                        DropdownMenuItem(text = { Text(label) }, enabled = enabled,
                            leadingIcon = if (value == selected) { { Icon8(Ico.CHECK, MaterialTheme.colorScheme.primary) } } else null,
                            onClick = { if (value != selected) onSelect(value); menu.dismiss() })
                    }
                }
            }
        }) {
        options.forEach { (value, label) ->
            toggleableItem(checked = value == selected, label = label, enabled = enabled,
                onCheckedChange = { if (value != selected) onSelect(value) })
        }
    }
}

@Composable
fun TonalPill(ico: Ico, text: String, enabled: Boolean = true, danger: Boolean = false, onClick: () -> Unit) {
    val c = MaterialTheme.colorScheme
    FilledTonalButton(onClick = onClick, enabled = enabled, shapes = ButtonDefaults.shapes(),
        modifier = Modifier.heightIn(min = 48.dp),
        colors = if (danger) ButtonDefaults.filledTonalButtonColors(containerColor = c.errorContainer, contentColor = c.onErrorContainer)
        else ButtonDefaults.filledTonalButtonColors()) {
        Icon8(ico, LocalContentColor.current, 20.dp); Spacer(Modifier.width(8.dp)); Text(text)
    }
}

@Composable
fun ActionRow(ico: Ico, text: String, enabled: Boolean = true, danger: Boolean = false, onClick: () -> Unit) {
    val c = MaterialTheme.colorScheme
    Surface(onClick = onClick, enabled = enabled, shape = MaterialTheme.shapes.large,
        color = if (danger) c.errorContainer else c.surfaceContainerHigh,
        contentColor = if (danger) c.onErrorContainer else c.onSurface) {
        Row(Modifier.fillMaxWidth().heightIn(min = 60.dp).padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon8(ico, if (enabled) LocalContentColor.current else c.onSurface.copy(alpha = .38f))
            Spacer(Modifier.width(14.dp))
            Text(text, Modifier.weight(1f), style = MaterialTheme.typography.bodyLarge)
        }
    }
}

@Composable
private fun fieldColors() = OutlinedTextFieldDefaults.colors(
    focusedContainerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
    unfocusedContainerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
    unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant)

@Composable
fun SearchField(value: String, onChange: (String) -> Unit, hint: String) {
    OutlinedTextField(value = value, onValueChange = onChange, singleLine = true, shape = RoundedCornerShape(28.dp),
        label = { Text(hint) }, leadingIcon = { Icon(Icons.Rounded.Search, null) }, colors = fieldColors(), modifier = Modifier.fillMaxWidth())
}

@Composable
fun SwitchRow(title: String, checked: Boolean, onChange: (Boolean) -> Unit) {
    Row(Modifier.fillMaxWidth().heightIn(min = 56.dp).clip(MaterialTheme.shapes.medium)
        .toggleable(value = checked, role = Role.Switch, onValueChange = onChange), verticalAlignment = Alignment.CenterVertically) {
        Text(title, Modifier.weight(1f).padding(end = 12.dp), style = MaterialTheme.typography.bodyLarge)
        Switch(checked = checked, onCheckedChange = null,
            thumbContent = if (checked) { { Icon8(Ico.CHECK, MaterialTheme.colorScheme.onPrimaryContainer, 16.dp) } } else null)
    }
}

@Composable
fun TextRow(title: String, value: String, hint: String = "", singleLine: Boolean = true, isError: Boolean = false, onChange: (String) -> Unit) {
    OutlinedTextField(value = value, onValueChange = onChange, singleLine = singleLine, isError = isError,
        label = { Text(title) }, shape = MaterialTheme.shapes.medium, colors = fieldColors(),
        supportingText = if (hint.isNotEmpty()) { { Text(hint) } } else null, modifier = Modifier.fillMaxWidth())
}

@Composable
fun NumberRow(title: String, value: Int, onChange: (Int) -> Unit) {
    var t by remember(value) { mutableStateOf(value.toString()) }
    OutlinedTextField(value = t, onValueChange = { v ->
        t = v.filter { it.isDigit() }.take(5); t.toIntOrNull()?.let(onChange)
    }, label = { Text(title) }, singleLine = true, shape = MaterialTheme.shapes.medium, colors = fieldColors(),
        keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(keyboardType = androidx.compose.ui.text.input.KeyboardType.Number),
        modifier = Modifier.fillMaxWidth())
}
