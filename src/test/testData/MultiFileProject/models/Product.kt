package models

class Product(val id: Int, val title: String) {
    fun display() = "Product: $title"
}
