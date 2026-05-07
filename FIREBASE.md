# Estructura de Firebase Realtime Database

**Project ID:** `luxury-counter`
**Project Number:** `380288570955`
**URL:** `https://luxury-counter-default-rtdb.firebaseio.com/`

## Aplicaciones Registradas
- **Android App:** `1:380288570955:android:ddaebbb74c782ffeb19fce` (`com.luxury.cheats`)
- **Web App:** `1:380288570955:web:d83abaf3665cbcedb19fce`

## Estructura de Árbol ASCII

```text
luxury-counter-default-rtdb (Root)
├── active_ips
│   └── {ip_hash}
│       ├── createdAt (ISO 8601 string)
│       ├── device (string)
│       ├── expirationDate (ISO 8601 string)
│       ├── ip (string)
│       ├── country (string)
│       ├── key (string)
│       ├── status (string)
│       ├── tier (string)
│       └── used (boolean)
├── app_images
│   └── {img_id}
│       ├── title (string)
│       └── url (string)
├── app_update
│   ├── active (boolean)
│   ├── description (string)
│   ├── downloadLink (string)
│   ├── timestamp (ISO 8601 string)
│   ├── title (string)
│   └── version (string)
├── device_tokens
│   └── {token_id}
│       ├── active (boolean)
│       ├── plataform (string)
│       └── timestamp (ISO 8601 string)
├── licenses
│   └── {license_key}
│       ├── createdAt (ISO 8601 string)
│       ├── device (string)
│       ├── expirationDate (ISO 8601 string)
│       ├── ip (string)
│       ├── country (string)
│       ├── key (string)
│       ├── status (string)
│       ├── tier (string)
│       └── used (boolean)
├── notifications
│   └── {notification_id}
│       ├── active (boolean)
│       ├── description (string)
│       ├── frequency (string)
│       ├── id (string)
│       ├── image (string)
│       ├── sent (boolean)
│       ├── timestamp (ISO 8601 string)
│       ├── title (string)
│       └── type (string: "in-app" | "push")
├── urls
│   ├── Aimbot
│   │   ├── path (string)
│   │   └── url
│   │       └── url (string)
│   ├── Aimfov
│   │   ├── path (string)
│   │   └── url
│   │       └── url (string)
│   ├── Holograma
│   │   ├── path (string)
│   │   └── url
│   │       └── url (string)
│   └── WallHack
│       ├── path (string)
│       └── url
│           ├── url (string)
│           └── url2 (string)
└── users
    └── {user_id}
        ├── createdAt (ISO 8601 string)
        ├── device (string)
        ├── expirationDate (ISO 8601 string)
        ├── password (string)
        ├── tier (string)
        └── username (string)
```

## Descripción de Nodos

### `active_ips`
Controla las sesiones activas vinculadas a una dirección IP o hash de dispositivo para prevenir abusos.
- **createdAt**: Fecha en la que se registró la IP.
- **device**: ID del dispositivo vinculado a esa IP.
- **expirationDate**: Cuándo expira la sesión de esa IP.
- **ip**: Dirección IP real del usuario sin hashear.
- **country**: País de origen detectado por la IP.
- **key**: Licencia vinculada a la IP.
- **status**: Estado actual de la sesión (ej. `active`).
- **tier**: Nivel de acceso (`free` por defecto).
- **used**: Indica si la IP ya ha sido utilizada para activar un servicio.

### `app_images`
Recursos visuales dinámicos cargados desde URLs externas.
- **title**: Nombre identificador de la imagen (ej. `imagenLogin`).
- **url**: Enlace directo a la imagen hospedada.

### `app_update`
Contiene información sobre las últimas actualizaciones de la aplicación.
- **active**: Indica si la actualización está activa actualmente.
- **description**: Un breve resumen de las novedades en la actualización.
- **downloadLink**: Enlace directo para descargar la actualización.
- **timestamp**: Fecha y hora en que se publicó la actualización.
- **title**: Título de la notificación o diálogo de actualización.
- **version**: Número de versión de la actualización más reciente.

### `device_tokens`
Almacena tokens de FCM para notificaciones push.
- **active**: Estado del token del dispositivo.
- **plataform**: La plataforma del sistema operativo (ej. `android`).
- **timestamp**: Cuándo se registró el token.

### `licenses`
Almacena las llaves de acceso generadas y su estado de activación.
- **createdAt**: Fecha de creación de la licencia.
- **device**: Dispositivo que activó la licencia.
- **expirationDate**: Cuándo vence el acceso de esta licencia.
- **ip**: Dirección IP real desde la que se generó/activó.
- **country**: País de origen asociado a la IP.
- **key**: El código de la licencia (ej. `LUXURY-XXXX`).
- **status**: Estado de la licencia.
- **tier**: Nivel de acceso (`free` por defecto para licencias generadas por la web).
- **used**: Si la licencia ya ha sido canjeada.

### `notifications`
Define las notificaciones enviadas o programadas para los usuarios.
- **active**: Si la notificación está activa para ser procesada.
- **description**: Cuerpo del texto de la notificación.
- **frequency**: Alcance de la notificación (ej. `always`).
- **id**: Identificador único de la notificación.
- **image**: URL opcional para una imagen o GIF enriquecido.
- **sent**: Bandera que indica si la notificación ya ha sido enviada.
- **timestamp**: Cuándo se creó/envió la notificación.
- **title**: Título de la notificación.
- **type**: Método de entrega (`in-app` o `push`).

### `urls`
Configuración para las descargas dinámicas de funciones (cheats/mods).
- **path**: Ubicación local donde se debe guardar el archivo descargado.
- **url**: Nodo que contiene los enlaces de descarga.
  - **url**: El enlace directo de descarga principal.
  - **url2**: Enlace de descarga secundario o espejo.

### `users`
Credenciales de cuenta de usuario y datos de suscripción.
- **createdAt**: Fecha de creación de la cuenta.
- **device**: Último ID de dispositivo vinculado a la cuenta.
- **expirationDate**: Cuándo vence el acceso del usuario.
- **password**: Contraseña de autenticación del usuario.
- **tier**: Nivel de acceso (`vip` para usuarios registrados convencionales).
- **username**: Identificador único para el inicio de sesión.

---

## Configuración de Optimización (PRODUCCIÓN)
1.  **Persistencia Offline**: Habilitada (`setPersistenceEnabled(true)`).
2.  **Sincronización Crítica**: El nodo `/users` utiliza `keepSynced(true)` para validaciones instantáneas.

## Reglas de Firebase

```json
{
  "rules": {
    "users": {
      ".read": true,
      "$user_id": {
        ".write": "!data.exists() || !newData.exists() || data.child('device').val() == '' || newData.child('device').val() == '' || newData.child('expirationDate').val() != data.child('expirationDate').val()"
      },
      ".indexOn": ["username", "device"]
    },
    "app_images": {
      ".read": true,
      ".write": true,
      ".indexOn": ["title"]
    },
    "device_tokens": {
      ".read": true,
      ".write": true
    },
    "notifications": {
      ".read": true,
      ".write": false
    },
    "app_update": {
      ".read": true,
      ".write": false
    },
    "licenses": {
      ".read": true,
      ".write": true
    },
    "active_ips": {
      ".read": true,
      ".write": true
    },
    "urls": {
      ".read": true,
      ".write": false
    }
  }
}
```