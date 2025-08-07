fun main() {
    val inventario = mutableListOf(
        Producto(1, "Laptop", 700.0, 5),
        Producto(2, "Mouse", 25.0, 10),
        Producto(3, "Teclado", 45.0, 7),
        Producto(4, "Monitor", 250.0, 3)
    )

    val carrito = Carrito()

    while (true) {
        println(
            """
            |========== MENÚ ==========
            |1. Ver productos
            |2. Agregar producto al carrito
            |3. Ver carrito
            |4. Eliminar producto del carrito
            |5. Finalizar compra
            |6. Salir
            |==========================
            |Seleccione una opción:
        """.trimMargin()
        )

        when (readLine()?.toIntOrNull()) {
            1 -> mostrarInventario(inventario)
            2 -> agregarAlCarrito(inventario, carrito)
            3 -> carrito.mostrarCarrito()
            4 -> eliminarDelCarrito(carrito)
            5 -> {
                carrito.generarFactura()
                carrito.vaciarCarrito()
            }
            6 -> {
                println("Gracias por usar el sistema. ¡Hasta luego!")
                break
            }
            else -> println("Opción inválida.")
        }
    }
}

fun mostrarInventario(inventario: List<Producto>) {
    println("\n--- Inventario ---")
    for (producto in inventario) {
        println("${producto.id}. ${producto.nombre} - Precio: $${producto.precio} - Stock: ${producto.cantidadDisponible}")
    }
}

fun agregarAlCarrito(inventario: List<Producto>, carrito: Carrito) {
    print("Ingrese el ID del producto a agregar: ")
    val id = readLine()?.toIntOrNull()
    val producto = inventario.find { it.id == id }

    if (producto != null) {
        print("Ingrese la cantidad: ")
        val cantidad = readLine()?.toIntOrNull() ?: 0
        if (cantidad in 1..producto.cantidadDisponible) {
            carrito.agregarProducto(producto, cantidad)
            println("Producto agregado.")
        } else {
            println("Cantidad inválida.")
        }
    } else {
        println("Producto no encontrado.")
    }
}

fun eliminarDelCarrito(carrito: Carrito) {
    print("Ingrese el ID del producto a eliminar del carrito: ")
    val id = readLine()?.toIntOrNull()
    if (id != null) {
        carrito.eliminarProducto(id)
    } else {
        println("ID inválido.")
    }
}
