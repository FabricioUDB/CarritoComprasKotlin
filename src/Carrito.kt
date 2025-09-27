/**
 * Clase Carrito
 * 
 * Representa un carrito de compras en el cual se pueden agregar, 
 * eliminar y consultar productos. 
 * 
 * Internamente, el carrito administra una lista de "ItemCarrito", 
 * que asocian un producto con la cantidad seleccionada.
 */
class Carrito {

    // Lista interna de ítems en el carrito (producto + cantidad)
    private val items: MutableList<ItemCarrito> = mutableListOf()

    /**
     * Agrega un producto al carrito.
     * 
     * - Si el producto no existe en el carrito, lo inserta con la cantidad indicada.
     * - Si ya existe, simplemente aumenta la cantidad.
     * 
     * @param producto Producto que se desea agregar.
     * @param cantidad Número de unidades que se quiere añadir (debe ser mayor a 0).
     * @throws IllegalArgumentException si la cantidad es 0 o negativa.
     */
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
     * Elimina cierta cantidad de un producto identificado por su ID.
     * 
     * - Si la cantidad eliminada es igual o mayor a la que hay en el carrito,
     *   se remueve el producto completo.
     * - Si la cantidad eliminada es menor, simplemente se descuenta.
     * 
     * @param idProducto Identificador único del producto.
     * @param cantidad Número de unidades que se desea eliminar (debe ser mayor a 0).
     * @return true si se modificó el carrito (es decir, si el producto estaba dentro),
     *         false si el producto no estaba en el carrito.
     * @throws IllegalArgumentException si la cantidad es 0 o negativa.
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

    /**
     * Obtiene los ítems actuales en el carrito.
     * 
     * @return Una lista inmutable de los productos con sus cantidades.
     */
    fun obtenerItems(): List<ItemCarrito> = items.toList()

    /**
     * Calcula el total a pagar del carrito.
     * 
     * Multiplica la cantidad de cada producto por su precio y lo suma todo.
     * 
     * @return Total acumulado en formato Double.
     */
    fun total(): Double = items.sumOf { it.cantidad * it.producto.precio }

    /**
     * Vacía completamente el carrito de compras.
     * Elimina todos los productos.
     */
    fun vaciar() { items.clear() }
}
