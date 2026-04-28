package com.example.fakestoreapp.screens

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.fakestoreapp.components.ProductItem
import com.example.fakestoreapp.models.Product
import com.example.fakestoreapp.services.ProductsService
import com.example.fakestoreapp.ui.theme.FakeStoreAppTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

@Composable
fun ProductsScreen(
    innerPadding: PaddingValues = PaddingValues(10.dp),
    navController: NavController = rememberNavController()
){
    val BASE_URL = "https://fakestoreapi.com/"
    var products by remember {
        mutableStateOf(listOf<Product>())
    }

    var isLoading by remember {
        mutableStateOf(true)
    }

    LaunchedEffect(key1 = true) {
        try{
            val retrofitBuilder = Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
            val result = async(Dispatchers.IO) {
                val productService = retrofitBuilder.create(ProductsService::class.java)
                productService.getAllProducts()
            }
            Log.i("ProductsScreen", result.await().toString())
            products = result.await()
            isLoading = false
        }
        catch (e: Exception){
            Log.e("ProductsScreen", e.message.toString())
            isLoading = false
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFE8EAF6))
            .padding(innerPadding)
    ) {
        Text(
            text = "FakeStore",
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp)
        )

        if (isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier
                    .fillMaxSize()
                    .padding(10.dp)
            ) {
                items(products) { product ->
                    ProductItem(product = product, onClick = {
                        navController.navigate("products/${product.id}")
                    })
                }
            }
        }
    }
}

@Preview(
    showSystemUi = true,
    showBackground = true
)
@Composable
fun ProductsScreenPreview(){
    FakeStoreAppTheme {
        ProductsScreen()
    }
}
