# Estructura de Firebase Realtime Database

**URL:** `https://luxury-counter-default-rtdb.firebaseio.com/`

## Estructura de Árbol ASCII

```text
luxury-counter-default-rtdb (Root)
├── app_update
│   ├── active (boolean)
│   ├── descripcion (string)
│   ├── downloadlink (string)
│   ├── timestamp (ISO 8601 string)
│   ├── title (string)
│   └── version (string)
├── device_tokens
│   └── {token_id}
│       ├── active (boolean)
│       ├── plataform (string)
│       └── timestamp (ISO 8601 string)
├── notifications
│   └── {notification_id}
│       ├── active (boolean)
│       ├── description (string)
│       ├── frecuency (string)
│       ├── id (string)
│       ├── image (string)
│       ├── sent (boolean)
│       ├── timestamp (ISO 8601 string)
│       ├── title (string)
│       └── type (string: "in-app" | "push")
├── urls
│   ├── aimbot
│   │   ├── path (string)
│   │   ├── url (string)
│   │   └── url 1 (string)
│   ├── aimfov
│   │   ├── path (string)
│   │   ├── url (string)
│   │   └── url 1 (string)
│   ├── Holograma
│   │   ├── path (string)
│   │   ├── url (string)
│   │   └── url 1 (string)
│   └── Wall hack
│       ├── path (string)
│       ├── url (string)
│       └── url 1 (string)
└── users
    └── {username}
        ├── createdAt (ISO 8601 string)
        ├── device (string)
        ├── expirationDate (ISO 8601 string)
        ├── password (string)
        └── username (string)
```

## Descripción de Nodos

### `app_update`
Contiene información sobre las últimas actualizaciones de la aplicación.
- **active**: Indica si la actualización está activa actualmente.
- **descripcion**: Un breve resumen de las novedades en la actualización.
- **downloadlink**: Enlace directo para descargar la actualización.
- **timestamp**: Fecha y hora en que se publicó la actualización (ej. `2025-12-09T18:49:15.140Z`).
- **title**: Título de la notificación o diálogo de actualización.
- **version**: Número de versión de la actualización más reciente.

### `device_tokens`
Almacena tokens de FCM (Firebase Cloud Messaging) de dispositivos registrados para notificaciones push.
- **active**: Estado del token del dispositivo.
- **plataform**: La plataforma del sistema operativo (ej. `android`).
- **timestamp**: Cuándo se registró el token.

### `notifications`
Define las notificaciones enviadas o programadas para los usuarios.
- **active**: Si la notificación está activa para ser procesada.
- **description**: Cuerpo del texto de la notificación.
- **frecuency**: Alcance de la notificación (a cuántos usuarios llega).
- **id**: Identificador único de la notificación.
- **image**: URL opcional para una imagen enriquecida en la notificación.
- **sent**: Bandera que indica si la notificación ya ha sido enviada.
- **timestamp**: Cuándo se creó/envió la notificación.
- **title**: Título de la notificación.
- **type**: Método de entrega (`in-app` o `push`).

### `urls`
Configuración para las descargas dinámicas de funciones (cheats/mods). Cada sub-nodo (ej. `aimbot`, `aimfov`, `Holograma`, `Wall hack`) contiene:
- **path**: Ubicación en el sistema de archivos local donde se debe guardar el archivo descargado.
- **url**: Enlace de descarga de la fuente principal.
- **url 1**: Enlace de descarga secundario/espejo.

### `users`
Credenciales de cuenta de usuario y datos de suscripción.
- **createdAt**: Fecha de creación de la cuenta.
- **device**: Último ID de dispositivo vinculado a la cuenta.
- **expirationDate**: Cuándo vence el acceso del usuario.
- **password**: Contraseña de autenticación del usuario.
- **username**: Identificador único para el inicio de sesión del usuario.

---

## Sugerencias Profesionales de Arquitectura (NO USAR)

Para mantener la base de datos escalable, segura y eficiente, se recomiendan las siguientes prácticas:

1.  **Indexación de Consultas**: 
    - Configura reglas `.indexOn` en Firebase para `username` y `expirationDate`. Esto evitará que Firebase descargue todos los usuarios para encontrar uno solo, mejorando drásticamente el rendimiento cuando la base de datos crezca.

2.  **Seguridad de Reglas (Filtro por Escritura)**:
    - Asegúrate de que solo el Dashboard Administrativo tenga permisos de escritura (`.write: true`) en nodos críticos como `app_update`, `notifications` y `urls`.
    - La App solo debería tener permiso de lectura y, opcionalmente, de escritura en `device_tokens` y su propio nodo de `users`.

3.  **Optimización de Notificaciones**:
    - Considera mover las notificaciones antiguas a un nodo llamado `notifications_history`. Mantener muchas notificaciones en el nodo activo puede ralentizar la carga inicial de la App.

4.  **Validación de Versiones**:
    - En `app_update`, asegúrate de usar un formato de versión semántico (ej: `1.0.2`). Esto facilita la lógica en la App para comparar si la versión instalada es menor que la disponible y forzar la actualización si es necesario.

5.  **Protección de Datos Sensibles**:
    - Aunque el campo `password` es necesario para el login, se recomienda almacenar una versión "hasheada" (cifrada) en lugar del texto plano para proteger la seguridad de los usuarios en caso de una brecha de datos.

6.  **Aplanamiento de Datos**:
    - Si el nodo `users` llega a miles de registros, evita anidar demasiada información dentro de cada usuario. Si necesitas guardar configuraciones pesadas de la app por usuario, muévelas a un nodo separado llamado `user_settings` mapeado por el mismo ID.


## Reglas de firebase : 

{
  "rules": {
    ".read": true,
    ".write": true,
    "users": {
      ".indexOn": ["username", "device"]
    },
    "notifications": {
      ".read": true,
      ".write": true
    },
    "app_update": {
      ".read": true,
      ".write": true
    }
  }
}