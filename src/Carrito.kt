class Carrito {

    private val items: MutableList<ItemCarrito> = mutableListOf()

    /** Agrega un producto (si existe en el carrito, suma cantidades) */
    fun agregarProducto(producto: Producto, cantidad: Int) {
        require(cantidad > 0) { "La cantidad debe ser mayor que 0" }
        val existente = items.firstOrNull { it.producto.id == producto.id }
        if (existente == null) {
            items += ItemCarrito(producto, cantidad)
        } else {
            existente.cantidad += cantidad
        }
    }

    /**
     * Elimina cantidad de un producto por ID. Si queda 0, lo remueve.
     * @return true si modificó algo, false si no estaba en el carrito.
     */
    fun eliminarProducto(idProducto: Int, cantidad: Int): Boolean {
        require(cantidad > 0) { "La cantidad debe ser mayor que 0" }
        val existente = items.firstOrNull { it.producto.id == idProducto } ?: return false
        if (cantidad >= existente.cantidad) {
            items.remove(existente)
        } else {
            existente.cantidad -= cantidad
        }
        return true
    }

    /** Lectura inmutable */
    fun obtenerItems(): List<ItemCarrito> = items.toList()

    /** Total */
    fun total(): Double = items.sumOf { it.cantidad * it.producto.precio }

    /** Vaciar carrito */
    fun vaciar() { items.clear() }
}
