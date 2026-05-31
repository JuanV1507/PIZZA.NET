# Documentación Detallada de Actualizaciones - Pizza.NET V.1.0.1

Este documento detalla los cambios estructurales, arquitectónicos y funcionales realizados recientemente en el sistema **Pizza.NET**, abarcando los módulos de Ventas (Punto de Venta), Impresión de Tickets Térmicos, Reportes Automatizados, Gestión de Clientes y Devoluciones.

## 1. Librerías y Tecnologías Utilizadas

- **Impresión Térmica:** Se integró la API nativa de Java **`javax.print`** para el envío de comandos crudos (RAW) y secuencias ESC/POS a impresoras térmicas. No se requirió añadir dependencias externas al `pom.xml`, ya que forma parte del JDK estándar.
- **Java 21:** El proyecto fue adaptado para compilar correctamente bajo entornos Java recientes.
- **Hibernate / JPA:** Se actualizó la configuración en `application.properties` para utilizar el estándar `org.hibernate.dialect.MySQLDialect`, eliminando advertencias de obsolescencia y fallos de sintaxis.
- **Persistencia en Front-End:** Se implementó la API nativa del navegador web, **`localStorage`**, para mantener el estado del carrito de compras durante la recarga de páginas y el filtrado por categorías.

## 2. Modelos y Entidades (Base de Datos)

Se adaptaron las siguientes entidades clave para soportar el nuevo flujo de ventas completas y devoluciones:

- **`Clientes`**: Adaptada para buscar o crear clientes dinámicamente usando el número de teléfono, capturando nombre, dirección y referencias de ubicación cruzada de calles para envíos a domicilio.
- **`Venta`**: Centraliza la información del ticket. Se añadieron campos clave como `folioVenta` (generación automática secuencial tipo A0001) y `tipo_servicio` (COMEDOR, DOMICILIO, ESPERANDO).
- **`DetalleVenta`**: Relaciona cada venta con los productos específicos (`id_producto`), almacenando el precio unitario calculado en el momento exacto de la venta y la cantidad solicitada.

## 3. Data Transfer Objects (DTOs)

Se crearon nuevos DTOs en el paquete `com.Proyecto.Web.DTO` para separar la capa de presentación de la capa de persistencia y recibir cargas de datos (payloads) complejas desde el frontend en formato JSON:

- **`VentaRequestDTO`**: Estructura principal que recibe el backend al confirmar una venta en el POS. Incluye:
  - **Datos del cliente**: `nombre`, `telefono`, `direccion`, `mesa`, `referencias`, `nota`.
  - **Datos financieros**: `tipoOrden`, `total`, `pago` (cantidad entregada por el cliente).
  - **Lista de productos**: `List<DetalleVentaRequestDTO> carrito`.
- **`DetalleVentaRequestDTO`**: Estructura anidada dentro del carrito que mapea la selección del usuario (`id`, `nombre`, `precio`, `cantidad`).
- **`HistorialVentaDTO` y `ReporteDTO`**: Utilizados para empaquetar de forma estructurada la información financiera en el módulo de reportes (ticket promedio, ingresos totales, clasificación de ventas por periodo).

## 4. Controladores y Endpoints

### `VentaController`
Maneja las vistas y el guardado de las transacciones a través de una API REST mixta.
- **`GET /ventas`**: Renderiza la vista principal del punto de venta, inyectando el modelo de productos, las categorías y generando el `siguienteFolio` en la interfaz.
- **`GET /ventas/siguiente-folio`**: Retorna vía JSON el próximo número de ticket disponible en la secuencia (ej. A0042) para actualizar el front-end en tiempo real.
- **`POST /ventas/guardar`**: Recibe el `VentaRequestDTO` en formato JSON mediante la anotación `@RequestBody`, delega todo el procesamiento transaccional al servicio y retorna un estado HTTP indicando el éxito de la venta.

### `TestImpresoraController`
- **`GET /test-impresora`**: Endpoint administrativo de diagnóstico. Ejecuta una búsqueda de las impresoras locales instaladas en el sistema del servidor (usando `PrintServiceLookup`), detecta la impresora térmica predeterminada y envía una impresión de prueba en texto plano RAW. Es fundamental para instalar hardware nuevo en el futuro.

### `ReporteController`
- Se corrigieron los problemas de inicialización (`UnsatisfiedDependencyException`) asegurando que este controlador pueda instanciar correctamente al servicio de reportes por correo electrónico para las tareas de contabilidad programadas.

## 5. Lógica de Negocio (Servicios)

### `ImpresionService`
El núcleo de la comunicación con el hardware físico. Transforma los datos abstractos de `VentaRequestDTO` en un ticket físico estructurado.
- **Tecnología**: Emite secuencias y comandos en **bytes hexadecimales (ESC/POS)** directamente a la cola de impresión de Windows. Ej: `0x1B, 0x40` para resetear impresora, `0x1D, 0x56, 0x41, 0x00` para realizar el **corte automático del papel**.
- **Diseño del Ticket**: Crea un layout al estilo minimalista ("Receiptify"), inyectando alineaciones automáticas, negritas, y textos de doble ancho/alto.
- **Condicionales Físicas**: Si la venta es "Domicilio", el servicio imprime la dirección, teléfono e instrucciones de entrega. Si es "Comedor", imprime en grande el número de la mesa.
- **Alineación Dinámica**: El método interno `formatItemLine(...)` calcula mediante un algoritmo de espaciado la cantidad exacta de espacios en blanco necesarios para que las columnas numéricas de la derecha (Importe) queden perfectamente alineadas al margen en un ancho de papel de 32 caracteres.

### `VentaService`
Responsable del procesamiento seguro, atómico e íntegro de las transacciones monetarias.
- **`procesarVentaCompleta(VentaRequestDTO)`**: Flujo central que abarca:
  1. **Gestión de Cliente**: Busca en la base de datos si el cliente existe por su teléfono. Si no existe, lo registra de inmediato. Si existe, actualiza sus referencias de domicilio.
  2. **Auditoría**: Extrae el nombre de usuario del cajero autenticado en sesión mediante `SecurityContextHolder` y lo liga a la venta.
  3. **Folio de Venta**: Genera el folio secuencial llamando al método privado `generarSiguienteFolio()`.
  4. **Guardado en Cascada**: Inserta el registro padre `Venta` e itera sobre el DTO para insertar cada registro hijo en `DetalleVenta`.
  5. **Impresión Síncrona**: Al finalizar la transacción de base de datos exitosamente, llama a `impresionService.imprimirTicket(...)` para emitir el recibo en mostrador.

### `ReporteEmailService`
- Corregida la inicialización de Beans que rompían el arranque de Spring Boot y re-estructurado el envío SMTP, permitiendo que la tabla interna de la base de datos sea renderizada en formato PDF adjunto.

## 6. Front-End (JavaScript, UI/UX)

- **`JSVentas.js`**: 
  - **Local Storage API**: Se reescribió la lógica del carrito de compras (`carritoProductos`). Ahora se respalda en la memoria del navegador. Esto soluciona un bug grave donde el cajero perdía todo el carrito si recargaba la página accidentalmente o filtraba los productos por la categoría "Pizzas" o "Bebidas".
  - **Modal de Cobro Dinámico (Checkout)**: Un pop-up modal profesional que exige el método de venta (Mesa/Domicilio). Posee validaciones matemáticas en tiempo real (calcula automáticamente cuánto dinero en **cambio** se le debe dar al cliente basándose en el `total` y la entrada `pago`).
  - **AJAX Asíncrono (`fetch`)**: Empaqueta silenciosamente todo el JSON complejo y lo envía a Spring Boot. En caso de éxito, despliega notificaciones tipo SweetAlert y vacía la memoria.

## 7. Módulo de Devoluciones y Clientes
Se fortaleció el panel administrativo de clientes para mostrar todo el historial detallado de compras por usuario. Al mismo tiempo, se finalizó una interfaz de control donde se capturan motivos de cancelación y devolución de productos, garantizando así un control cruzado en la caja para arqueos financieros perfectos.
