import java.io.File
import java.text.NumberFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Currency
import java.util.Locale

// ===================== LOG A ARCHIVO =====================
object LogFile {
    private val file = File("logs/app.log")

    private fun ts(): String =
        LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))

    private fun ensureDir() { file.parentFile?.mkdirs() }

    fun i(msg: String) {
        ensureDir()
        file.appendText("${ts()} [INFO] $msg\n")
    }

    fun e(msg: String, ex: Throwable? = null) {
        ensureDir()
        file.appendText("${ts()} [ERROR] $msg\n")
        ex?.let { file.appendText(it.stackTraceToString() + "\n") }
    }
}

// ===================== UI HELPERS (ANSI + TABLAS) =====================
object UI {
    // ANSI
    const val RESET = "\u001B[0m"
    const val BOLD  = "\u001B[1m"

    // Colores
    private const val FG_PRIMARY = "\u001B[38;5;39m"    // azul
    private const val FG_ACCENT  = "\u001B[38;5;208m"   // naranja
    private const val FG_OK      = "\u001B[38;5;34m"    // verde
    private const val FG_WARN    = "\u001B[38;5;178m"   // ámbar
    private const val FG_MUTED   = "\u001B[38;5;245m"   // gris

    // Formato de moneda USD con locale es-SV
    private fun money(v: Double): String {
        val nf = NumberFormat.getCurrencyInstance(Locale("es","SV"))
        nf.currency = Currency.getInstance("USD")
        return nf.format(v)
    }

    fun clear() { print("\u001B[2J\u001B[H") }

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
        items.forEach { println("${FG_ACCENT}$it$RESET") }
        footer?.let {
            println(FG_MUTED + " ".repeat(1) + "─".repeat(32) + RESET)
            println(FG_MUTED + it + RESET)
        }
        print("${BOLD}Selecciona una opción:${RESET} ")
    }

    fun printProductsTable(productos: List<Producto>, showStock: Boolean = true) {
        if (productos.isEmpty()) { println("${FG_WARN}No hay productos disponibles.$RESET"); return }
        val colId = 4; val colNombre = 18; val colPrecio = 12; val colStock = 7
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
                append(pad(money(p.precio), colPrecio))
                if (showStock) { append(" │ "); append(pad(p.cantidadDisponible.toString(), colStock)) }
                append(" │")
            }
            println(row)
        }
        println(FG_MUTED + "└" + "─".repeat(totalWidth - 2) + "┘" + RESET)
    }

    fun printCartTable(items: List<ItemCarrito>) {
        if (items.isEmpty()) { println("${FG_WARN}Tu carrito está vacío.$RESET"); return }
        val colId = 4; val colNombre = 18; val colCantidad = 9; val colPrecio = 12; val colSubtotal = 14
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
            val sub = itc.cantidad * itc.producto.precio
            val row = buildString {
                append("│ "); append(pad(itc.producto.id.toString(), colId)); append(" │ ")
                append(pad(itc.producto.nombre, colNombre)); append(" │ ")
                append(pad(itc.cantidad.toString(), colCantidad)); append(" │ ")
                append(pad(money(itc.producto.precio), colPrecio)); append(" │ ")
                append(pad(money(sub), colSubtotal)); append(" │")
            }
            println(row)
        }
        println(FG_MUTED + "└" + "─".repeat(totalWidth - 2) + "┘" + RESET)
    }

    fun success(msg: String) = println("$FG_OK$msg$RESET")
    fun warn(msg: String)    = println("$FG_WARN$msg$RESET")
}

// ===================== HELPERS DE ENTRADA =====================
fun readIntInRange(prompt: String, range: IntRange): Int {
    while (true) {
        print(prompt)
        val n = readLine()?.trim()?.toIntOrNull()
        if (n != null && n in range) return n
        println("Entrada inválida. Intenta de nuevo.")
        LogFile.i("Entrada inválida para '$prompt'")
    }
}
fun readPositiveInt(prompt: String): Int {
    while (true) {
        print(prompt)
        val n = readLine()?.trim()?.toIntOrNull()
        if (n != null && n > 0) return n
        println("Cantidad inválida. Intenta de nuevo.")
        LogFile.i("Cantidad inválida para '$prompt'")
    }
}

// ===================== PROGRAMA PRINCIPAL =====================
fun main() {
    LogFile.i("Aplicación iniciada")

    // Inventario inicial
    val inventario = mutableListOf(
        Producto(id = 1, nombre = "Laptop",  precio = 700.0, cantidadDisponible = 5),
        Producto(id = 2, nombre = "Mouse",   precio = 25.0,  cantidadDisponible = 10),
        Producto(id = 3, nombre = "Teclado", precio = 45.0,  cantidadDisponible = 7),
        Producto(id = 4, nombre = "Monitor", precio = 250.0, cantidadDisponible = 3)
    )
    val carritoItems = mutableListOf<ItemCarrito>()

    val IVA = 0.13
    val cupones = mapOf("UDB10" to 0.10, "UDB20" to 0.20)

    fun subtotalCarrito(): Double = carritoItems.sumOf { it.cantidad * it.producto.precio }

    fun guardarRecibo(texto: String) {
        val dir = File("recibos").apply { if (!exists()) mkdirs() }
        val ts  = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss"))
        val f   = File(dir, "recibo-$ts.txt")
        f.writeText(texto)
        UI.success("Recibo guardado en: ${f.absolutePath}")
        LogFile.i("Recibo guardado: ${f.absolutePath}")
    }

    // -------- Opciones del menú --------
    fun verProductos() {
        UI.clear()
        UI.header("Productos disponibles")
        UI.printProductsTable(inventario.sortedBy { it.id }, showStock = true)
        UI.pause()
    }

    fun buscarProducto() {
        UI.clear()
        UI.header("Buscar producto")
        print("Texto a buscar: ")
        val q = readLine()?.trim()?.lowercase().orEmpty()
        val res = inventario.filter { it.nombre.lowercase().contains(q) }
        if (res.isEmpty()) UI.warn("Sin coincidencias para \"$q\".")
        else UI.printProductsTable(res, showStock = true)
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
            LogFile.i("Intento de agregar ID inexistente $id")
            UI.pause(); return
        }
        if (producto.cantidadDisponible <= 0) {
            UI.warn("Sin stock para '${producto.nombre}'.")
            UI.pause(); return
        }
        val cant = readIntInRange("Cantidad (1..${producto.cantidadDisponible}): ", 1..producto.cantidadDisponible)

        val item = carritoItems.firstOrNull { it.producto.id == id }
        if (item == null) carritoItems.add(ItemCarrito(producto, cant)) else item.cantidad += cant
        producto.cantidadDisponible -= cant

        UI.success("Se agregaron $cant x '${producto.nombre}' al carrito.")
        LogFile.i("Agregado al carrito: $cant x ${producto.nombre} (ID=$id)")
        UI.pause()
    }

    fun verCarrito() {
        UI.clear()
        UI.header("Tu carrito")
        UI.printCartTable(carritoItems)
        val nf = NumberFormat.getCurrencyInstance(Locale("es","SV")).apply { currency = Currency.getInstance("USD") }
        val sub = subtotalCarrito()
        println("${UI.BOLD}Subtotal: ${nf.format(sub)}${UI.RESET}")
        println("IVA (13%): ${nf.format(sub * IVA)}")
        println("Total aprox.: ${nf.format(sub * (1 + IVA))}")
        UI.pause()
    }

    fun eliminarDelCarrito() {
        UI.clear()
        UI.header("Eliminar del carrito")
        if (carritoItems.isEmpty()) { UI.warn("Tu carrito está vacío."); UI.pause(); return }
        UI.printCartTable(carritoItems)

        val id = readPositiveInt("ID del producto a eliminar: ")
        val item = carritoItems.firstOrNull { it.producto.id == id }
        if (item == null) {
            UI.warn("Ese producto no está en el carrito.")
            UI.pause(); return
        }
        val cant = readIntInRange("Cantidad a eliminar (1..${item.cantidad}): ", 1..item.cantidad)

        inventario.firstOrNull { it.id == id }?.let { it.cantidadDisponible += cant }
        if (cant == item.cantidad) carritoItems.remove(item) else item.cantidad -= cant

        UI.success("Se eliminaron $cant x '${item.producto.nombre}'.")
        LogFile.i("Eliminado del carrito: $cant x ${item.producto.nombre} (ID=$id)")
        UI.pause()
    }

    fun finalizarCompra() {
        UI.clear()
        if (carritoItems.isEmpty()) { UI.warn("Tu carrito está vacío."); UI.pause(); return }

        val nf = NumberFormat.getCurrencyInstance(Locale("es","SV")).apply { currency = Currency.getInstance("USD") }
        val sub = subtotalCarrito()
        UI.header("Resumen de compra")
        UI.printCartTable(carritoItems)
        println("Subtotal: ${nf.format(sub)}")

        print("¿Tienes cupón (UDB10/UDB20)? (deja vacío si no): ")
        val cup = readLine()?.trim()?.takeIf { it.isNotBlank() }?.uppercase()
        val descPct = cupones[cup] ?: 0.0
        if (descPct > 0) println("Cupón aplicado: -${(descPct*100).toInt()}%")

        val desc = sub * descPct
        val base = sub - desc
        val iva  = base * IVA
        val total = base + iva

        println("Descuento: ${nf.format(desc)}")
        println("IVA (13%): ${nf.format(iva)}")
        println("${UI.BOLD}Total a pagar: ${nf.format(total)}${UI.RESET}")

        print("Confirmar compra (s/n): ")
        val conf = readLine()?.trim()?.lowercase()
        if (conf == "s" || conf == "si" || conf == "sí") {
            val detalles = buildString {
                appendLine("=== RECIBO ===")
                appendLine("Fecha: ${LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))}")
                carritoItems.forEach {
                    appendLine("- ${it.producto.nombre} x${it.cantidad} @ ${nf.format(it.producto.precio)} = ${nf.format(it.cantidad * it.producto.precio)}")
                }
                appendLine("Subtotal: ${nf.format(sub)}")
                if (descPct > 0) appendLine("Cupón $cup (-${(descPct*100).toInt()}%): ${nf.format(desc)}")
                appendLine("IVA (13%): ${nf.format(iva)}")
                appendLine("TOTAL: ${nf.format(total)}")
            }
            guardarRecibo(detalles)
            carritoItems.clear()
            UI.success("¡Compra realizada con éxito!")
            LogFile.i("Compra confirmada. Total=${nf.format(total)}")
        } else {
            UI.warn("Compra cancelada.")
            LogFile.i("Compra cancelada por el usuario")
        }
        UI.pause()
    }

    // -------- Loop principal con manejo de errores --------
    while (true) {
        UI.clear()
        UI.menu(
            title = "Carrito de Compras",
            items = listOf(
                " 1) Ver productos",
                " 2) Buscar producto",
                " 3) Agregar producto al carrito",
                " 4) Ver carrito",
                " 5) Eliminar producto del carrito",
                " 6) Finalizar compra",
                " 7) Salir"
            ),
            footer = "Usa los números 1-7"
        )

        val opcion = readIntInRange("", 1..7)
        try {
            when (opcion) {
                1 -> verProductos()
                2 -> buscarProducto()
                3 -> agregarProducto()
                4 -> verCarrito()
                5 -> eliminarDelCarrito()
                6 -> finalizarCompra()
                7 -> { UI.clear(); UI.success("¡Gracias por usar el sistema!"); LogFile.i("Aplicación finalizada"); break }
            }
        } catch (ex: Exception) {
            UI.warn("Ocurrió un error inesperado. Revisa logs/app.log")
            LogFile.e("Fallo al ejecutar opción $opcion", ex)
            UI.pause()
        }
    }
}
