# Pizza.NET 
 <img width="3780" height="1890" alt="Diseño sin título" src="https://github.com/user-attachments/assets/25055a52-97c3-4823-8698-d5cc4aefa4dc" />
Sistema integral de gestión y Punto de Venta (POS) desarrollado para la **Pizzería Unicornio**. Pizza.NET automatiza el flujo operativo completo del restaurante, permitiendo gestionar ventas, inventario, impresión térmica y reportes financieros desde una única plataforma.

# 🚀 Demo del Sistema

## 🛒 Punto de Venta en Acción

<img width="1878" height="926" alt="Grabación 2026-05-17 123043 (1)" src="https://github.com/user-attachments/assets/11741b55-d3a2-4ca4-9749-fa02f4917f23" />

## 🧾 Ticket Térmico ESC/POS

<img width="1288" height="2902" alt="ticket" src="https://github.com/user-attachments/assets/f4cd7b9e-a9bc-4e89-b1dc-3892d5c59592" />

## 📊 Reportes Automáticos PDF

<img width="989" height="624" alt="image" src="https://github.com/user-attachments/assets/ca9575dd-5b3f-4af5-b20b-6b29f8f165c2" />
<img width="989" height="646" alt="image" src="https://github.com/user-attachments/assets/feabc295-d1e1-462e-847f-c384ce609b2e" />
<img width="993" height="514" alt="image" src="https://github.com/user-attachments/assets/3c7d1266-d8f9-4a90-8fd4-63844ecb97bd" />

## 📧 Envío Automático de Reportes

 <img width="1504" height="700" alt="image" src="https://github.com/user-attachments/assets/2e26701b-849f-4b28-a4f8-205168698f02" />
 <img width="1507" height="700" alt="image" src="https://github.com/user-attachments/assets/6410e5ae-b6b7-4261-ab5c-029951dddc12" />


## 🚀 Características Principales

*   **Punto de Venta (POS) Avanzado:**
    *   Interfaz rápida y persistente (guarda el estado del carrito en `localStorage`).
    *   Cálculo automático de cambio y validación de pagos en efectivo.
      
      <img width="394" height="828" alt="image" src="https://github.com/user-attachments/assets/9206e7a3-5034-43c9-a748-e696133e9f12" />

*   **Impresión de Tickets Térmicos:**
    *   Integración directa con impresoras térmicas mediante comandos **ESC/POS**.
    *   Diseño estructurado (estilo *Receiptify*) con control de jerarquía visual (texto en negrita, diferentes tamaños).
    *   Corte automático de papel tras cada venta.
      
      <img width="1288" height="2902" alt="ticket" src="https://github.com/user-attachments/assets/f4cd7b9e-a9bc-4e89-b1dc-3892d5c59592" />
      
*   **Gestión de Devoluciones:**
    *   Registro detallado de devoluciones (motivo, fecha, empleado).
    *   Reversión de stock automático de productos.
      
*   **Reportes y Analítica Automática:**
    *   Generación de reportes de ventas, ingresos generales y nómina de empleados.
    *   Envío automático de reportes en PDF por correo electrónico (Gmail SMTP) mediante tareas programadas.
      
      <img width="1605" height="920" alt="image" src="https://github.com/user-attachments/assets/1bb3cd18-7452-455a-8548-119b8ec8407d" />
      
*   **Gestión Administrativa:**
    *   Módulos para administrar Productos, Empleados, Clientes y consultar el Historial de Ventas.

     <img width="1492" height="897" alt="image" src="https://github.com/user-attachments/assets/878f866b-654b-465f-a7ae-44dee44e8e8e" />
     
     <img width="1627" height="805" alt="image" src="https://github.com/user-attachments/assets/90d3d8c6-5a0b-4a6c-8be1-e94388268aff" />
     
     <img width="1577" height="891" alt="image" src="https://github.com/user-attachments/assets/afc9711d-bcea-4e2d-b8c6-22df202eed20" />

## 🎥 Contenido adaptado para cualquier dispositivo


<img width="420" height="838" alt="Grabación 2026-05-17 124953 (1)" src="https://github.com/user-attachments/assets/af858861-471b-4265-b679-34439fd817ed" />


# 🛠️ Tecnologías Utilizadas

## Backend
- Java 21
- Spring Boot
- Spring Data JPA
- Hibernate

## Frontend
- HTML5
- CSS3
- JavaScript (ES6+)

## Base de Datos
- MySQL

## Integraciones
- JavaMailSender
- ESC/POS
- Scheduler Tasks

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
