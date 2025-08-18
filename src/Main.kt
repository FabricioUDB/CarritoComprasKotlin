// ===== UI Helpers (ANSI + dibujo de menú/tablas) =====
object UI {
    // Hacemos BOLD y RESET públicos para poder usarlos fuera
    const val RESET = "\u001B[0m"
    const val BOLD  = "\u001B[1m"

    private const val FG_PRIMARY = "\u001B[38;5;39m"   // azul
    private const val FG_ACCENT  = "\u001B[38;5;208m" // naranja
    private const val FG_OK      = "\u001B[38;5;34m"  // verde
    private const val FG_WARN    = "\u001B[38;5;178m" // ámbar
    private const val FG_MUTED   = "\u001B[38;5;245m" // gris

    fun clear() {
        print("\u001B[2J\u001B[H")
    }

    fun pause(msg: String = "Presiona ENTER para continuar...") {
        println()
        print(FG_MUTED + msg + RESET)
        readLine()
    }

    fun header(title: String) {
        val line = "─".repeat(title.length + 4)
        println("${FG_PRIMARY}┌$line┐$RESET")
        println("${FG_PRIMARY}│$RESET  ${BOLD}$title$RESET  ${FG_PRIMARY}│$RESET")
        println("${FG_PRIMARY}└$line┘$RESET")
    }

    fun menu(title: String, items: List<String>, footer: String? = null) {
        header(title)
        items.forEach { println("${FG_ACCENT}${it}$RESET") }
        footer?.let {
            println(FG_MUTED + " ".repeat(1) + "─".repeat(32) + RESET)
            println(FG_MUTED + it + RESET)
        }
        print("${BOLD}Selecciona una opción:${RESET} ")
    }

    fun printProductsTable(productos: List<Producto>, showStock: Boolean = true) {
        if (productos.isEmpty()) {
            println("${FG_WARN}No hay productos disponibles.$RESET"); return
        }
        val colId = 4; val colNombre = 18; val colPrecio = 10; val colStock = 7
        fun pad(s: String, w: Int) = if (s.length >= w) s.take(w - 1) + "…" else s + " ".repeat(w - s.length)

        val totalWidth = 2 + colId + 3 + colNombre + 3 + colPrecio + (if (showStock) 3 + colStock else 0) + 2
        println(FG_MUTED + "┌" + "─".repeat(totalWidth - 2) + "┐" + RESET)
        val header = buildString {
            append("│ "); append(pad("ID", colId)); append(" │ ")
            append(pad("Nombre", colNombre)); append(" │ ")
            append(pad("Precio", colPrecio))
            if (showStock) { append(" │ "); append(pad("Stock", colStock)) }
            append(" │")
        }
        println(BOLD + header + RESET)
        println(FG_MUTED + "├" + "─".repeat(totalWidth - 2) + "┤" + RESET)
        productos.forEach { p ->
            val row = buildString {
                append("│ "); append(pad(p.id.toString(), colId)); append(" │ ")
                append(pad(p.nombre, colNombre)); append(" │ ")
                append(pad("$" + "%.2f".format(p.precio), colPrecio))
                if (showStock) { append(" │ "); append(pad(p.cantidadDisponible.toString(), colStock)) }
                append(" │")
            }
            println(row)
        }
        println(FG_MUTED + "└" + "─".repeat(totalWidth - 2) + "┘" + RESET)
    }

    fun printCartTable(items: List<ItemCarrito>) {
        if (items.isEmpty()) { println("${FG_WARN}Tu carrito está vacío.$RESET"); return }
        val colId = 4; val colNombre = 18; val colCantidad = 9; val colPrecio = 10; val colSubtotal = 11
        fun pad(s: String, w: Int) = if (s.length >= w) s.take(w - 1) + "…" else s + " ".repeat(w - s.length)
        val totalWidth = 2 + colId + 3 + colNombre + 3 + colCantidad + 3 + colPrecio + 3 + colSubtotal + 2
        println(FG_MUTED + "┌" + "─".repeat(totalWidth - 2) + "┐" + RESET)
        val header = buildString {
            append("│ "); append(pad("ID", colId)); append(" │ ")
            append(pad("Producto", colNombre)); append(" │ ")
            append(pad("Cantidad", colCantidad)); append(" │ ")
            append(pad("Precio", colPrecio)); append(" │ ")
            append(pad("Subtotal", colSubtotal)); append(" │")
        }
        println(BOLD + header + RESET)
        println(FG_MUTED + "├" + "─".repeat(totalWidth - 2) + "┤" + RESET)
        items.forEach { itc ->
            val row = buildString {
                append("│ "); append(pad(itc.producto.id.toString(), colId)); append(" │ ")
                append(pad(itc.producto.nombre, colNombre)); append(" │ ")
                append(pad(itc.cantidad.toString(), colCantidad)); append(" │ ")
                append(pad("$" + "%.2f".format(itc.producto.precio), colPrecio)); append(" │ ")
                val sub = itc.cantidad * itc.producto.precio
                append(pad("$" + "%.2f".format(sub), colSubtotal)); append(" │")
            }
            println(row)
        }
        println(FG_MUTED + "└" + "─".repeat(totalWidth - 2) + "┘" + RESET)
    }

    fun success(msg: String) = println("$FG_OK$msg$RESET")
    fun warn(msg: String) = println("$FG_WARN$msg$RESET")
}

// ===== Programa principal =====
fun main() {
    val inventario = mutableListOf(
        Producto(id = 1, nombre = "Laptop",  precio = 700.0, cantidadDisponible = 5),
        Producto(id = 2, nombre = "Mouse",   precio = 25.0,  cantidadDisponible = 10),
        Producto(id = 3, nombre = "Teclado", precio = 45.0,  cantidadDisponible = 7),
        Producto(id = 4, nombre = "Monitor", precio = 250.0, cantidadDisponible = 3)
    )
    val carrito = Carrito()

    fun pickProductoPorId(id: Int): Producto? = inventario.firstOrNull { it.id == id }

    fun verProductos() {
        UI.clear(); UI.header("Productos disponibles")
        UI.printProductsTable(inventario); UI.pause()
    }

    fun agregarProducto() {
        UI.clear(); UI.header("Agregar producto al carrito")
        UI.printProductsTable(inventario)
        print("\nIngresa el ID del producto: ")
        val id = readLine()?.toIntOrNull()
        val p = id?.let { pickProductoPorId(it) }
        if (p == null) { UI.warn("ID inválido."); UI.pause(); return }

        print("Cantidad (stock ${p.cantidadDisponible}): ")
        val cantidad = readLine()?.toIntOrNull()
        if (cantidad == null || cantidad <= 0) { UI.warn("Cantidad inválida."); UI.pause(); return }
        if (cantidad > p.cantidadDisponible) { UI.warn("No hay suficiente stock."); UI.pause(); return }

        carrito.agregarProducto(p, cantidad)
        p.cantidadDisponible -= cantidad
        UI.success("Agregado: ${p.nombre} x$cantidad"); UI.pause()
    }

    fun verCarrito() {
        UI.clear(); UI.header("Tu carrito")
        val items = carrito.obtenerItems()
        UI.printCartTable(items)
        println()
        println("Total: ${UI.BOLD}$${"%.2f".format(carrito.total())}${UI.RESET}")
        UI.pause()
    }

    fun eliminarDelCarrito() {
        UI.clear(); UI.header("Eliminar producto del carrito")
        val items = carrito.obtenerItems()
        if (items.isEmpty()) { UI.warn("El carrito está vacío."); UI.pause(); return }
        UI.printCartTable(items)
        print("\nIngresa el ID del producto a eliminar: ")
        val id = readLine()?.toIntOrNull() ?: run { UI.warn("ID inválido."); UI.pause(); return }
        val item = items.firstOrNull { it.producto.id == id } ?: run { UI.warn("Ese producto no está en el carrito."); UI.pause(); return }
        print("Cantidad a eliminar (en carrito ${item.cantidad}): ")
        val qty = readLine()?.toIntOrNull()
        if (qty == null || qty <= 0 || qty > item.cantidad) { UI.warn("Cantidad inválida."); UI.pause(); return }

        carrito.eliminarProducto(id, qty)
        // devolver stock al inventario
        inventario.first { it.id == id }.cantidadDisponible += qty
        UI.success("Eliminado del carrito: ${item.producto.nombre} x$qty"); UI.pause()
    }

    fun finalizarCompra() {
        UI.clear(); UI.header("Finalizar compra")
        val items = carrito.obtenerItems()
        if (items.isEmpty()) { UI.warn("Tu carrito está vacío."); UI.pause(); return }
        UI.printCartTable(items)
        val total = carrito.total()
        println("\nTotal a pagar: ${UI.BOLD}$${"%.2f".format(total)}${UI.RESET}")
        print("¿Confirmar compra? (s/n): ")
        if (readLine()?.trim()?.lowercase() != "s") { UI.warn("Compra cancelada."); UI.pause(); return }
        carrito.vaciar()
        UI.success("¡Compra realizada con éxito! 🎉"); UI.pause()
    }

    // ===== Loop del menú =====
    while (true) {
        UI.clear()
        UI.menu(
            title = "Carrito de Compras",
            items = listOf(
                " 1) Ver productos",
                " 2) Agregar producto al carrito",
                " 3) Ver carrito",
                " 4) Eliminar producto del carrito",
                " 5) Finalizar compra",
                " 6) Salir"
            ),
            footer = "Usa los números 1-6"
        )
        when (readLine()?.toIntOrNull()) {
            1 -> verProductos()
            2 -> agregarProducto()
            3 -> verCarrito()
            4 -> eliminarDelCarrito()
            5 -> finalizarCompra()
            6 -> { UI.clear(); UI.success("¡Gracias por usar el sistema!"); break }
            else -> { UI.warn("Opción inválida."); UI.pause() }
        }
    }
}
