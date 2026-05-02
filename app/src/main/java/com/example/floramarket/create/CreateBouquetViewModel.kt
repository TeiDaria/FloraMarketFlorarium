package com.example.floramarket.create

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.floramarket.model.BouquetDraft
import com.example.floramarket.viewmodel.GeneratorViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class CreateBouquetViewModel : ViewModel() {
    private val generatorViewModel = GeneratorViewModel()

    var imageUrls by mutableStateOf<List<String>>(emptyList())
        private set

    var name by mutableStateOf("")
        private set

    var shortDescription by mutableStateOf("")
        private set

    var fullDescription by mutableStateOf("")
        private set

    var priceInput by mutableStateOf("")
        private set

    var quantityInput by mutableStateOf("")
        private set

    val price: Double?
        get() = priceInput.toDoubleOrNull()

    val quantity: Int?
        get() = quantityInput.toIntOrNull()

    var isGeneratingName by mutableStateOf(false)
        private set

    var generationError by mutableStateOf<String?>(null)
        private set

    // Список сгенерированных названий
    var generatedNames by mutableStateOf<List<String>>(emptyList())
        private set

    // Флаг для показа диалога подтверждения повторной генерации
    var showRegenerateDialog by mutableStateOf(false)
        private set

    fun updateName(newName: String){
        name = newName
    }

    fun updateShortDescription(newDesc: String) {
        shortDescription = newDesc
    }

    fun updateFullDescription(newDesc: String) {
        fullDescription = newDesc
    }

    fun updatePrice(newPrice: String){
        if (newPrice.isEmpty() || newPrice.matches(Regex("^\\d*\\.?\\d*\$"))){
            val doubleValue = newPrice.toDoubleOrNull()
            if (doubleValue == null || doubleValue <= 1_000_000.0) {
                priceInput = newPrice
            }
        }
    }

    fun updateQuantity(newQuantity: String) {
        if (newQuantity.isEmpty() || newQuantity.matches(Regex("^\\d+$"))) {
            val intValue = newQuantity.toIntOrNull()
            if (intValue==null || intValue <= 10_000){
                quantityInput = newQuantity
            }
        }
    }

    fun addImage(url: String){
        imageUrls = imageUrls + url
    }

    fun removeImage(url: String) {
        val newList = imageUrls.filter { it != url }
        imageUrls = newList
    }

    fun onGenerateClick() {
        if (shortDescription.isBlank()) {
            generationError = "Введите описание букета для генерации"
            return
        }

        if (generatedNames.isNotEmpty()) {
            showRegenerateDialog = true
            return
        }

        startGeneration()
    }

    fun confirmRegenerate() {
        showRegenerateDialog = false
        startGeneration()
    }

    fun cancelRegenerate() {
        showRegenerateDialog = false
    }

    private fun startGeneration(){
        isGeneratingName = true
        generationError = null
        generatedNames = emptyList()

        Log.d("CreateBouquetVM", "startGeneration()")

        generatorViewModel.generateName(shortDescription)

        viewModelScope.launch {
            var waitTime = 0
            val maxWaitTime = 30000L // 30 секунд максимум

            Log.d("CreateBouquetVM", "Ждём завершения генерации...")

            while (generatorViewModel.isLoading && waitTime < maxWaitTime){
                delay(200)
                waitTime += 200
            }

            Log.d("CreateBouquetVM", "Ожидание завершено, waitTime=$waitTime ms")

            isGeneratingName = false

            if (waitTime >= maxWaitTime) {
                Log.e("CreateBouquetVM", "Таймаут! Ждали ${maxWaitTime}ms")
                generationError = "Превышено время ожидания (30 секунд)"
                return@launch
            }

            Log.d("CreateBouquetVM", "generatedName='${generatorViewModel.generatedName}'")
            Log.d("CreateBouquetVM", "error='${generatorViewModel.error}'")

            if (generatorViewModel.generatedName.isNotEmpty()){
                val names = generatorViewModel.generatedName
                    .split("\n")
                    .map{it.trim()}
                    .filter { it.isNotEmpty() }

                Log.d("CreateBouquetVM", "Распаршено названий: ${names.size}")
                names.forEachIndexed { i, n ->
                    Log.d("CreateBouquetVM", "   ${i+1}. '$n'")
                }

                if (names.isNotEmpty()){
                    generatedNames = names
                } else {
                    generationError = "Не удалось распознать сгенерированные названия"
                }
            } else if (generatorViewModel.error != null){
                generationError = generatorViewModel.error
            } else {
                generationError = "Модель не вернула результат"
            }
        }
    }

    fun selectGeneratedName(selectedName: String){
        name = selectedName
    }

    fun clearGenerationError(){
        generationError = null
        generatorViewModel.clearError()
    }

    fun publishBouquet(
        onSuccess: (BouquetDraft) -> Unit,
        onError: (String) -> Unit
    ) {
        when {
            imageUrls.isEmpty() -> {
                onError("Добавьте хотя бы одно фото букета")
                return
            }
            name.isBlank() -> {
                onError("Введите название букета")
                return
            }

            price == null -> {
                onError("Укажите корректную цену")
                return
            }
            price!! > 1_000_000 -> {
                onError("Максимальная цена — 1 000 000 ₽")
                return
            }
            quantity == null -> {
                onError("Укажите количество в наличии")
                return
            }
            quantity!! > 10_000 -> {
                onError("Максимальное количество — 10 000 шт.")
                return
            }
            quantity!! <= 0 -> {
                onError("Количество должно быть больше 0")
                return
            }
        }

        val draft = BouquetDraft(
            imageUrls = imageUrls,
            name = name,
            shortDescription = shortDescription,
            fullDescription = fullDescription,
            price = price,
            quantity = quantity
        )

        onSuccess(draft)
    }

    fun isValid(): Boolean {
        return imageUrls.isNotEmpty() &&
                name.isNotBlank() &&
                price != null &&
                quantity != null
    }

    fun resetForm() {
        imageUrls = emptyList()
        name = ""
        shortDescription = ""
        fullDescription = ""
        priceInput = ""
        quantityInput = ""
        isGeneratingName = false
        generationError = null
        generatedNames = emptyList()
        showRegenerateDialog = false
    }

}