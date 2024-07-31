package com.example.stock

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
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
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.stock.models.Stock
import com.example.stock.ui.theme.NegativeTrend
import com.example.stock.ui.theme.PositiveTrend
import com.example.stock.ui.theme.StockBlue
import com.example.stock.ui.theme.StockTheme
import com.example.stock.ui.theme.StocksDarkPrimaryText
import com.example.stock.ui.theme.StocksDarkSelectedChip
import com.example.stock.viewmodels.StockViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val stockViewModel: StockViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            StockTrackerApp()
        }
        lifecycle.addObserver(stockViewModel)
    }
}

@Composable
fun StockTrackerApp() {
    val viewModel: StockViewModel = hiltViewModel()

    MaterialTheme() {
        StockListScreen(viewModel)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StockListScreen(viewModel: StockViewModel) {
    val stocks by viewModel.stocks.collectAsState()
    var searchQuery by rememberSaveable { mutableStateOf("") }

    val cal: Calendar = Calendar.getInstance()
    val dayOfMonth: Int = cal.get(Calendar.DAY_OF_MONTH)
    val month = SimpleDateFormat("MMMM", Locale.ENGLISH).format(cal.time)
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 10.dp)
        ) {
            Box {
                Text(
                    "Stocks",
                    color = Color.White,
                    fontSize = 25.sp,
                    fontWeight = FontWeight.Bold,
                )
                Text(
                    text = "$dayOfMonth $month",
                    color = Color.Gray,
                    fontWeight = FontWeight.Bold,
                    fontSize = 25.sp,
                    modifier = Modifier.padding(top = 28.dp)
                )
            }
            Spacer(modifier = Modifier.weight(1f))
            IconButton(
                onClick = { /* TODO */ },
                modifier = Modifier
                    .align(Alignment.CenterVertically)
                    .clip(CircleShape)
                    .size(30.dp)
                    .padding(5.dp)
                    .background(StocksDarkSelectedChip)
            ) {
                Icon(
                    Icons.Default.MoreVert,
                    contentDescription = "More",
                    tint = StockBlue,
                    modifier = Modifier.graphicsLayer(rotationZ = 90f)
                )
            }
        }

        CustomTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            placeholder = stringResource(id = R.string.search_place_holder),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .height(40.dp)
                .clip(RoundedCornerShape(10.dp))
        )
        val filteredStocks = stocks.filter {
            it.symbol.contains(searchQuery, true) || it.companyName.contains(
                searchQuery,
                true
            )
        }

        Row(
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .clickable {/* TODO */ }
                .padding(top = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "My Symbols",
                color = StockBlue,
                fontWeight = FontWeight.Bold,
            )
            Box {
                Icon(
                    painter = painterResource(id = R.drawable.down),
                    contentDescription = "Drop Up",
                    tint = StockBlue,
                    modifier = Modifier.size(20.dp).graphicsLayer(rotationZ = 180f)
                )
                Box(
                    modifier = Modifier.padding(top = 8.dp)
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.down),
                        contentDescription = "Drop Dow",
                        tint = StockBlue,
                        modifier = Modifier.size(20.dp)
                    )
                }

            }
        }

        LazyColumn {
            items(filteredStocks.size) { index ->
                val stock = filteredStocks[index]
                Column {
                    StockItem(stock)
                    if (index < filteredStocks.size - 1) {
                        Box(modifier = Modifier.padding(horizontal = 16.dp)) {
                            Divider(color = StocksDarkSelectedChip, thickness = 0.2.dp)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CustomTextField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    modifier: Modifier = Modifier,
) {
    val focusManager = LocalFocusManager.current
    BasicTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier,
        cursorBrush = SolidColor(StocksDarkPrimaryText),
        textStyle = TextStyle(
            color = StocksDarkPrimaryText,
            fontSize = 15.sp
        ),
        keyboardOptions = KeyboardOptions.Default.copy(
            imeAction = ImeAction.Done,
            autoCorrect = false,
            capitalization = KeyboardCapitalization.None
        ),
        keyboardActions = KeyboardActions(
            onDone = {
                focusManager.clearFocus()
            }
        ),
        decorationBox = { innerTextField ->
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxSize()
                    .background(StocksDarkSelectedChip)
            ) {
                Icon(
                    imageVector = Icons.Filled.Search,
                    contentDescription = "Search Icon",
                    tint = Color.Gray,
                    modifier = Modifier.padding(start = 4.dp)
                )
                Box(modifier = Modifier.weight(1f)) {
                    if (value.isEmpty()) {
                        Text(
                            text = placeholder,
                            color = Color.Gray,
                            fontSize = 16.sp,
                            textAlign = TextAlign.Start,
                            modifier = Modifier.padding(start = 5.dp)
                        )
                    }
                    innerTextField() // Draw the actual text field content
                }
            }
        },
        singleLine = true,
        maxLines = 1
    )
}

@Composable
fun StockItem(stock: Stock) {
    val colorChange = if (stock.priceChange >= 0) PositiveTrend else NegativeTrend
    val sign = if (stock.priceChange >= 0) "+" else ""

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 10.dp)
            .background(Color.Black)
    ) {
        Column {
            Text(
                stock.symbol,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            Text(
                stock.companyName,
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.padding(top = 5.dp)
            )
        }
        Spacer(modifier = Modifier.weight(1f))
        Column(horizontalAlignment = Alignment.End) {
            Text(
                "${stock.currentPrice}",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.White
            )
            Box(
                modifier = Modifier
                    .padding(top = 4.dp)
                    .width(60.dp)
                    .background(colorChange, shape = RoundedCornerShape(4.dp))
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Text(
                    "$sign${stock.priceChange}",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.White,
                    modifier = Modifier.align(Alignment.CenterEnd)
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    StockTheme {

    }
}