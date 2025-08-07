class Carrito {
    private val items = mutableListOf<ItemCarrito>()

    fun agregarProducto(producto: Producto, cantidad: Int) {
        val existente = items.find { it.producto.id == producto.id }
        if (existente != null) {
            existente.cantidad += cantidad
        } else {
            items.add(ItemCarrito(producto, cantidad))
        }
        producto.cantidadDisponible -= cantidad
    }

    fun eliminarProducto(id: Int) {
        val item = items.find { it.producto.id == id }
        if (item != null) {
            item.producto.cantidadDisponible += item.cantidad
            items.remove(item)
            println("Producto eliminado del carrito.")
        } else {
            println("Producto no encontrado en el carrito.")
        }
    }

    fun mostrarCarrito() {
        if (items.isEmpty()) {
            println("El carrito está vacío.")
            return
        }
        println("\n--- Carrito de Compras ---")
        for (item in items) {
            val total = item.cantidad * item.producto.precio
            println("${item.producto.nombre} - Cant: ${item.cantidad}, Unit: $${item.producto.precio}, Total: $${"%.2f".format(total)}")
        }
        println("Total General: $${"%.2f".format(calcularTotal())}")
    }

    fun calcularTotal(): Double {
        return items.sumOf { it.cantidad * it.producto.precio }
    }

    fun generarFactura() {
        println("\n======= FACTURA =======")
        mostrarCarrito()
        println("========================")
    }

    fun vaciarCarrito() {
        items.clear()
    }
}
