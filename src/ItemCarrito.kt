/**
 * Clase ItemCarrito
 *
 * Representa un ítem dentro del carrito de compras.
 * Cada ítem relaciona un producto específico con la cantidad
 * seleccionada por el usuario.
 *
 * Es una clase de tipo "data class" ya que su objetivo principal
 * es almacenar datos y aprovechar funciones automáticas como:
 * - equals()
 * - hashCode()
 * - toString()
 * - copy()
 *
 * @property producto El producto que el usuario agregó al carrito.
 * @property cantidad Número de unidades de ese producto.
 */
data class ItemCarrito(
    val producto: Producto,
    var cantidad: Int
)
