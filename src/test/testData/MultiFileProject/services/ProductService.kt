package services

import models.Product

object ProductService {
    fun getProduct(id: Int) = Product(id, "Sample Product")
}
