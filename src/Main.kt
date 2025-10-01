// ===== UI Helpers (ANSI + dibujo de menú/tablas) =====

/**
 * Objeto UI
 *
 * Este objeto contiene funciones auxiliares para mejorar la interfaz
 * en consola. Usa códigos ANSI para dar color, estilos (negrita, reset)
 * y dibuja tablas/menús de forma más visual.
 */
object UI {
    // Códigos ANSI
    const val RESET = "\u001B[0m"
    const val BOLD  = "\u001B[1m"

    // Colores
    private const val FG_PRIMARY = "\u001B[38;5;39m"    // azul
    private const val FG_ACCENT  = "\u001B[38;5;208m"   // naranja
    private const val FG_OK      = "\u001B[38;5;34m"    // verde
    private const val FG_WARN    = "\u001B[38;5;178m"   // ámbar
    private const val FG_MUTED   = "\u001B[38;5;245m"   // gris

    /** Limpia la pantalla de la consola */
    fun clear() {
        print("\u001B[2J\u001B[H")
    }

    /** Pausa el programa hasta que el usuario presione ENTER */
    fun pause(msg: String = "Presiona ENTER para continuar...") {
        println()
        print(FG_MUTED + msg + RESET)
        readLine()
    }

    /** Imprime un encabezado estilizado con un título */
    fun header(title: String) {
        val line = "─".repeat(title.length + 4)
        println("${FG_PRIMARY}┌$line┐$RESET")
        println("${FG_PRIMARY}│$RESET  ${BOLD}$title$RESET  ${FG_PRIMARY}│$RESET")
        println("${FG_PRIMARY}└$line┘$RESET")
    }

    /**
     * Dibuja un menú de opciones.
     * @param title Título del menú
     * @param items Lista de opciones (cada String es una línea)
     * @param footer Texto opcional como pie de menú
     */
    fun menu(title: String, items: List<String>, footer: String? = null) {
        header(title)
        items.forEach { println("${FG_ACCENT}$it$RESET") }
        footer?.let {
            println(FG_MUTED + " ".repeat(1) + "─".repeat(32) + RESET)
            println(FG_MUTED + it + RESET)
        }
        print("${BOLD}Selecciona una opción:${RESET} ")
    }

    /** Tabla de productos (ID, nombre, precio, stock) */
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

    /** Tabla del carrito (ID, nombre, cantidad, precio, subtotal) */
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
    fun warn(msg: String)    = println("$FG_WARN$msg$RESET")
}

// ===== Helpers de entrada =====
fun readIntInRange(prompt: String, range: IntRange): Int {
    while (true) {
        print(prompt)
        val n = readLine()?.trim()?.toIntOrNull()
        if (n != null && n in range) return n
        println("Entrada inválida. Intenta de nuevo.")
    }
}

fun readPositiveInt(prompt: String): Int {
    while (true) {
        print(prompt)
        val n = readLine()?.trim()?.toIntOrNull()
        if (n != null && n > 0) return n
        println("Cantidad inválida. Intenta de nuevo.")
    }
}

// ===== Programa principal =====

fun main() {
    // Inventario inicial
    val inventario = mutableListOf(
        Producto(id = 1, nombre = "Laptop",  precio = 700.0, cantidadDisponible = 5),
        Producto(id = 2, nombre = "Mouse",   precio = 25.0,  cantidadDisponible = 10),
        Producto(id = 3, nombre = "Teclado", precio = 45.0,  cantidadDisponible = 7),
        Producto(id = 4, nombre = "Monitor", precio = 250.0, cantidadDisponible = 3)
    )

    // Carrito (lista local de items)
    val carritoItems = mutableListOf<ItemCarrito>()

    // --- Opciones del menú ---

    fun verProductos() {
        UI.clear()
        UI.header("Productos disponibles")
        UI.printProductsTable(inventario, showStock = true)
        UI.pause()
    }

    fun agregarProducto() {
        UI.clear()
        UI.header("Agregar al carrito")
        UI.printProductsTable(inventario, showStock = true)

        val id = readPositiveInt("Ingresa el ID del producto: ")
        val producto = inventario.firstOrNull { it.id == id }
        if (producto == null) {
            UI.warn("No existe un producto con ID $id.")
            UI.pause(); return
        }
        if (producto.cantidadDisponible <= 0) {
            UI.warn("Sin stock para '${producto.nombre}'.")
            UI.pause(); return
        }
        val cant = readIntInRange("Cantidad (1..${producto.cantidadDisponible}): ", 1..producto.cantidadDisponible)

        val item = carritoItems.firstOrNull { it.producto.id == id }
        if (item == null) carritoItems.add(ItemCarrito(producto, cant))
        else item.cantidad += cant

        producto.cantidadDisponible -= cant

        UI.success("Se agregaron $cant x '${producto.nombre}' al carrito.")
        UI.pause()
    }

    fun verCarrito() {
        UI.clear()
        UI.header("Tu carrito")
        UI.printCartTable(carritoItems)
        val total = carritoItems.sumOf { it.cantidad * it.producto.precio }
        println("${UI.BOLD}Total: $${"%.2f".format(total)}${UI.RESET}")
        UI.pause()
    }

    fun eliminarDelCarrito() {
        UI.clear()
        UI.header("Eliminar del carrito")
        if (carritoItems.isEmpty()) {
            UI.warn("Tu carrito está vacío.")
            UI.pause(); return
        }
        UI.printCartTable(carritoItems)
        val id = readPositiveInt("ID del producto a eliminar: ")
        val item = carritoItems.firstOrNull { it.producto.id == id }
        if (item == null) {
            UI.warn("Ese producto no está en el carrito.")
            UI.pause(); return
        }
        val cant = readIntInRange("Cantidad a eliminar (1..${item.cantidad}): ", 1..item.cantidad)

        // Devolver stock
        inventario.firstOrNull { it.id == id }?.let { it.cantidadDisponible += cant }

        if (cant == item.cantidad) carritoItems.remove(item) else item.cantidad -= cant
        UI.success("Se eliminaron $cant x '${item.producto.nombre}'.")
        UI.pause()
    }

    fun finalizarCompra() {
        UI.clear()
        if (carritoItems.isEmpty()) {
            UI.warn("Tu carrito está vacío.")
            UI.pause(); return
        }
        UI.header("Resumen de compra")
        UI.printCartTable(carritoItems)
        val total = carritoItems.sumOf { it.cantidad * it.producto.precio }
        println("${UI.BOLD}Total a pagar: $${"%.2f".format(total)}${UI.RESET}")

        print("Confirmar compra (s/n): ")
        val conf = readLine()?.trim()?.lowercase()
        if (conf == "s" || conf == "si" || conf == "sí") {
            carritoItems.clear()
            UI.success("¡Compra realizada con éxito!")
        } else {
            UI.warn("Compra cancelada.")
        }
        UI.pause()
    }

    // --- Loop principal ---
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

        when (readIntInRange("", 1..6)) {
            1 -> verProductos()
            2 -> agregarProducto()
            3 -> verCarrito()
            4 -> eliminarDelCarrito()
            5 -> finalizarCompra()
            6 -> { UI.clear(); UI.success("¡Gracias por usar el sistema!"); break }
        }
    }
}
