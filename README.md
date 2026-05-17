# Pizza.NET - Pizzería Unicornio 🍕🦄

Sistema integral de gestión y Punto de Venta (POS) desarrollado para la **Pizzería Unicornio**. Este proyecto está diseñado para automatizar y optimizar las operaciones diarias del restaurante, desde la toma de pedidos hasta la generación de reportes financieros.

## 🚀 Características Principales

*   **Punto de Venta (POS) Avanzado:**
    *   Interfaz rápida y persistente (guarda el estado del carrito en `localStorage`).
    *   Cálculo automático de cambio y validación de pagos en efectivo.
*   **Impresión de Tickets Térmicos:**
    *   Integración directa con impresoras térmicas mediante comandos **ESC/POS**.
    *   Diseño estructurado (estilo *Receiptify*) con control de jerarquía visual (texto en negrita, diferentes tamaños).
    *   Corte automático de papel tras cada venta.
*   **Gestión de Devoluciones:**
    *   Registro detallado de devoluciones (motivo, fecha, empleado).
    *   Reversión de stock automático de productos.
*   **Reportes y Analítica Automática:**
    *   Generación de reportes de ventas, ingresos generales y nómina de empleados.
    *   Envío automático de reportes en PDF por correo electrónico (Gmail SMTP) mediante tareas programadas.
*   **Gestión Administrativa:**
    *   Módulos para administrar Productos, Empleados, Clientes y consultar el Historial de Ventas.

## 🛠️ Tecnologías Utilizadas

*   **Backend:** Java 21, Spring Boot, Hibernate / Spring Data JPA.
*   **Base de Datos:** MySQL.
*   **Frontend:** HTML5, CSS3, Vanilla JavaScript.
*   **Otros:** 
    *   Integración JavaMail Sender para correos.
    *   Comandos ESC/POS para hardware.

## ⚙️ Configuración y Despliegue

1.  **Base de datos:** Asegúrate de tener MySQL ejecutándose y configura las credenciales en `src/main/resources/application.properties`.
2.  **Impresora:** La impresora térmica debe estar configurada en el sistema (Windows) como la impresora por defecto o apuntar al nombre correcto en el `ImpresionService`.
3.  **Correo Electrónico:** Configura las credenciales de la cuenta de Gmail (App Password) en las variables de entorno o en `application.properties` para permitir el envío de reportes automáticos.
4.  **Ejecución:** Puedes ejecutar el proyecto desde tu IDE o mediante Maven:
    ```bash
    ./mvnw spring-boot:run
    ```

## 📝 Estructura de Módulos Destacados

*   `VentaService` & `ImpresionService`: Lógica principal del checkout y comunicación directa con el hardware de la impresora.
*   `ReporteScheduler` & `ReporteEmailService`: Tareas en segundo plano (cron jobs) para mantener la contabilidad al día sin intervención manual.
*   `DevolucionService`: Lógica de integridad de datos para reembolsos e inventario.

---
*Desarrollado para optimizar la magia y el sabor de Pizzería Unicornio.*
