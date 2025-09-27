// ===== UI Helpers (ANSI + dibujo de menú/tablas) =====

/**
 * Objeto UI
 *
 * Este objeto contiene funciones auxiliares para mejorar la interfaz
 * en consola. Usa códigos ANSI para dar color, estilos (negrita, reset)
 * y dibuja tablas/menús de forma más visual.
 *
 * Sirve como una capa de "presentación" para mostrar información
 * de productos, carrito y mensajes de retroalimentación al usuario.
 */
object UI {
    // Códigos ANSI para resetear formato y poner texto en negrita
    const val RESET = "\u001B[0m"
    const val BOLD  = "\u001B[1m"

    // Colores de texto (paleta personalizada)
    private const val FG_PRIMARY = "\u001B[38;5;39m"   // azul
    private const val FG_ACCENT  = "\u001B[38;5;208m" // naranja
    private const val FG_OK      = "\u001B[38;5;34m"  // verde
    private const val FG_WARN    = "\u001B[38;5;178m" // ámbar
    private const val FG_MUTED   = "\u001B[38;5;245m" // gris

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
     * @param footer Texto opcional que se muestra como pie de menú
     */
    fun menu(title: String, items: List<String>, footer: String? = null) {
        header(title)
        items.forEach { println("${FG_ACCENT}${it}$RESET") }
        footer?.let {
            println(FG_MUTED + " ".repeat(1) + "─".repeat(32) + RESET)
            println(FG_MUTED + it + RESET)
        }
        print("${BOLD}Selecciona una opción:${RESET} ")
    }

    /**
     * Muestra una tabla con los productos disponibles.
     * Incluye ID, nombre, precio y stock (opcional).
     */
    fun printProductsTable(productos: List<Producto>, showStock: Boolean = true) {
        if (productos.isEmpty()) {
            println("${FG_WARN}No hay productos disponibles.$RESET"); return
        }
        // Configuración de ancho de columnas
        val colId = 4; val colNombre = 18; val colPrecio = 10; val colStock = 7
        fun pad(s: String, w: Int) = if (s.length >= w) s.take(w - 1) + "…" else s + " ".repeat(w - s.length)

        val totalWidth = 2 + colId + 3 + colNombre + 3 + colPrecio + (if (showStock) 3 + colStock else 0) + 2
        println(FG_MUTED + "┌" + "─".repeat(totalWidth - 2) + "┐" + RESET)

        // Encabezado de la tabla
        val header = buildString {
            append("│ "); append(pad("ID", colId)); append(" │ ")
            append(pad("Nombre", colNombre)); append(" │ ")
            append(pad("Precio", colPrecio))
            if (showStock) { append(" │ "); append(pad("Stock", colStock)) }
            append(" │")
        }
        println(BOLD + header + RESET)
        println(FG_MUTED + "├" + "─".repeat(totalWidth - 2) + "┤" + RESET)

        // Filas de productos
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

    /**
     * Muestra una tabla con los productos dentro del carrito.
     * Incluye ID, nombre, cantidad, precio y subtotal.
     */
    fun printCartTable(items: List<ItemCarrito>) {
        if (items.isEmpty()) { println("${FG_WARN}Tu carrito está vacío.$RESET"); return }
        val colId = 4; val colNombre = 18; val colCantidad = 9; val colPrecio = 10; val colSubtotal = 11
        fun pad(s: String, w: Int) = if (s.length >= w) s.take(w - 1) + "…" else s + " ".repeat(w - s.length)

        val totalWidth = 2 + colId + 3 + colNombre + 3 + colCantidad + 3 + colPrecio + 3 + colSubtotal + 2
        println(FG_MUTED + "┌" + "─".repeat(totalWidth - 2) + "┐" + RESET)

        // Encabezado
        val header = buildString {
            append("│ "); append(pad("ID", colId)); append(" │ ")
            append(pad("Producto", colNombre)); append(" │ ")
            append(pad("Cantidad", colCantidad)); append(" │ ")
            append(pad("Precio", colPrecio)); append(" │ ")
            append(pad("Subtotal", colSubtotal)); append(" │")
        }
        println(BOLD + header + RESET)
        println(FG_MUTED + "├" + "─".repeat(totalWidth - 2) + "┤" + RESET)

        // Filas de ítems del carrito
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

    /** Mensaje de éxito en verde */
    fun success(msg: String) = println("$FG_OK$msg$RESET")

    /** Mensaje de advertencia en ámbar */
    fun warn(msg: String) = println("$FG_WARN$msg$RESET")
}

// ===== Programa principal =====

/**
 * Función principal del sistema de Carrito de Compras.
 *
 * Implementa un menú interactivo en consola que permite:
 * - Ver productos disponibles
 * - Agregar productos al carrito
 * - Consultar el carrito y el total a pagar
 * - Eliminar productos del carrito
 * - Finalizar la compra
 * - Salir del programa
 */
fun main() {
    // Inventario inicial de productos disponibles
    val inventario = mutableListOf(
        Producto(id = 1, nombre = "Laptop",  precio = 700.0, cantidadDisponible = 5),
        Producto(id = 2, nombre = "Mouse",   precio = 25.0,  cantidadDisponible = 10),
        Producto(id = 3, nombre = "Teclado", precio = 45.0,  cantidadDisponible = 7),
        Producto(id = 4, nombre = "Monitor", precio = 250.0, cantidadDisponible = 3)
    )
    val carrito = Carrito()

    // Función para buscar producto por ID
    fun pickProductoPorId(id: Int): Producto? = inventario.firstOrNull { it.id == id }

    // ===== Opciones del menú =====

    fun verProductos() { /* ... */ }
    fun agregarProducto() { /* ... */ }
    fun verCarrito() { /* ... */ }
    fun eliminarDelCarrito() { /* ... */ }
    fun finalizarCompra() { /* ... */ }

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
        // Manejo de opciones ingresadas por el usuario
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
