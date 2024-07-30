package com.example.stock

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.darkColors
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
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
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.stock.models.Stock
import com.example.stock.ui.theme.NegativeTrend
import com.example.stock.ui.theme.PositiveTrend
import com.example.stock.ui.theme.StockTheme
import com.example.stock.ui.theme.StocksDarkPrimaryText
import com.example.stock.ui.theme.StocksDarkSecondaryText
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

    MaterialTheme(
        colors = darkColors(
            primary = Color.Black,
            onPrimary = Color.White,
            background = Color.Black,
            onBackground = Color.White,
            surface = Color.Black,
            onSurface = Color.White
        )
    ) {
        StockListScreen(viewModel)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StockListScreen(viewModel: StockViewModel) {
    val stocks by viewModel.stocks.collectAsState()
    var searchQuery by rememberSaveable { mutableStateOf("") }
    val focusManager = LocalFocusManager.current

    val cal: Calendar = Calendar.getInstance()
    val dayOfMonth: Int = cal.get(Calendar.DAY_OF_MONTH)
    val month = SimpleDateFormat("MMMM", Locale.ENGLISH).format(cal.time)
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        Row( modifier = Modifier
            .fillMaxWidth()
            .padding(start = 16.dp, bottom = 0.dp)
            .background(Color.Black),
        ){
            Text(
                "Stocks",
                color = Color.White,
                fontSize = 30.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.weight(1f))
            IconButton(
                onClick = { /* TODO */ }) {
                Icon(Icons.Default.MoreVert, contentDescription = "More", tint = Color.White)
            }
        }
        Text(
            text = "$dayOfMonth $month",
            color = Color.Gray,
            fontWeight = FontWeight.Bold,
            fontSize = 30.sp,
            modifier = Modifier.padding(start = 16.dp, top = 1.dp)
        )

        TextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp)
                .clip(RoundedCornerShape(10.dp)),
            colors = TextFieldDefaults.textFieldColors(
                cursorColor = StocksDarkPrimaryText,
                containerColor = StocksDarkSelectedChip,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent
            ),
            textStyle = TextStyle(
                color = StocksDarkPrimaryText
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
            singleLine = true,
            maxLines = 1,
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = null,
                    tint = StocksDarkSecondaryText
                )
            },
            placeholder = {
                Text(
                    color = StocksDarkSecondaryText,
                    text = stringResource(id = R.string.search_place_holder)
                )
            },
            shape = androidx.compose.material3.MaterialTheme.shapes.large
        )

        val filteredStocks = stocks.filter {
            it.symbol.contains(searchQuery, true) || it.companyName.contains(
                searchQuery,
                true
            )
        }

        LazyColumn {
            items(filteredStocks) { stock ->
                StockItem(stock)
                Box(modifier = Modifier.padding(horizontal = 16.dp)) {
                    Divider(color = StocksDarkSelectedChip, thickness = 0.2.dp)
                }
            }
        }
    }
}

@Composable
fun StockItem(stock: Stock) {
    val colorChange = if (stock.priceChange >= 0) PositiveTrend else NegativeTrend
    val sign = if (stock.priceChange >= 0) "+" else ""

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .background(Color.Black)
    ) {
        Column {
            Text(
                stock.symbol,
                style = MaterialTheme.typography.h6,
                color = Color.White
            )
            Text(
                stock.companyName,
                style = MaterialTheme.typography.body2,
                color = Color.Gray,
                overflow = TextOverflow.Ellipsis
            )
        }
        Spacer(modifier = Modifier.weight(1f))
        Column(horizontalAlignment = Alignment.End) {
            Text(
                "${stock.currentPrice}",
                style = MaterialTheme.typography.body1,
                color = Color.White
            )
            Box(
                modifier = Modifier
                    .padding(top = 4.dp)
                    .background(colorChange, shape = RoundedCornerShape(4.dp))
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Text(
                    "$sign${stock.priceChange}",
                    style = MaterialTheme.typography.body2,
                    color = Color.White
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