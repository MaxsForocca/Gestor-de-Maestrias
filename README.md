# Gestor de Maestrías

![Android](https://img.shields.io/badge/Platform-Android-green.svg)
![Kotlin](https://img.shields.io/badge/Language-Kotlin-purple.svg)
![Jetpack Compose](https://img.shields.io/badge/UI-Jetpack%20Compose-blue.svg)
![Room](https://img.shields.io/badge/Database-Room-red.svg)
![MVVM](https://img.shields.io/badge/Architecture-MVVM-orange.svg)

Aplicación móvil Android para la gestión maestrías. Desarrollada con tecnologías modernas siguiendo las mejores prácticas de desarrollo Android.

## Tabla de Contenidos

- [Características](#características)
- [Arquitectura](#arquitectura)
- [Tecnologías](#tecnologías)
- [Estructura del Proyecto](#estructura-del-proyecto)
- [Requisitos](#requisitos)
- [Instalación](#instalación)
- [Uso](#uso)
- [Base de Datos](#base-de-datos)
- [Funcionalidades](#funcionalidades)
- [Autores](#autores)

## Características

### 🎯 Funcionalidades Principales

- ✅ **Gestión Completa de Maestrías**: CRUD completo (Crear, Leer, Actualizar, Eliminar)
- ✅ **Gestión de Tipos de Maestría**: CRUD completo (Crear, Leer, Actualizar, Eliminar)
- ✅ **Gestión de Facultades**: CRUD completo (Crear, Leer, Actualizar, Eliminar)
- ✅ **Gestión de Campus**: CRUD completo (Crear, Leer, Actualizar, Eliminar)
- ✅ **Sistema de Estados**: Activo, Inactivo, Eliminado (lógico)
- ✅ **Búsqueda Avanzada**: Por nombre y todos los campos relacionados
- ✅ **Filtros Múltiples**: Por tipo, facultad, campus y estado
- ✅ **Ordenamiento Flexible**: 10 opciones de ordenamiento diferentes (Maestria)
- ✅ **Interfaz Moderna**: Material Design 3 con Jetpack Compose
- ✅ **Base de Datos Local**: Persistencia con Room Database
- ✅ **Datos Relacionales**: Foreign Keys entre tablas

### Características de UI/UX

- 🎨 **Material Design 3**: Diseño moderno y consistente
- 🌓 **Tema Claro/Oscuro**: Soporte automático según configuración del sistema
- 🏷️ **Etiquetas Visuales**: Chips de colores para categorías
- 🔍 **Búsqueda en Tiempo Real**: Filtrado instantáneo mientras se escribe
- 📊 **Contador de Resultados**: Información clara de registros encontrados
- 🎭 **Animaciones Fluidas**: Transiciones suaves entre pantallas
- 📱 **Diseño Responsive**: Adaptable a diferentes tamaños de pantalla
- ♿ **Accesible**: Componentes optimizados para accesibilidad

## Arquitectura

El proyecto implementa la arquitectura **MVVM (Model-View-ViewModel)** siguiendo las recomendaciones:

```
┌─────────────────────────────────────────────────────┐
│                     UI Layer                        │
│              (Jetpack Compose)                      │
│  - Screens (View)                                   │
│  - Components                                       │
│  - Navigation                                       │
└─────────────────┬───────────────────────────────────┘
                  │ StateFlow / LiveData
                  │ User Events
┌─────────────────▼───────────────────────────────────┐
│              ViewModel Layer                        │
│  - Business Logic                                   │
│  - State Management                                 │
│  - Data Transformation                              │
└─────────────────┬───────────────────────────────────┘
                  │ Coroutines
                  │ Suspend Functions
┌─────────────────▼───────────────────────────────────┐
│            Repository Layer                         │
│  - Data Abstraction                                 │
│  - Single Source of Truth                           │
└─────────────────┬───────────────────────────────────┘
                  │
┌─────────────────▼───────────────────────────────────┐
│          Data Source Layer                          │
│  - Room Database (DAO)                              │
│  - Entity Models                                    │
└─────────────────────────────────────────────────────┘
```

### Principios Aplicados

- **Separation of Concerns**: Cada capa tiene una responsabilidad específica
- **Single Source of Truth**: Los datos fluyen en una única dirección
- **Unidirectional Data Flow**: Estado predecible y fácil de debuggear
- **Reactive Programming**: UI reactiva a cambios de datos con Flow/StateFlow

## Tecnologías

### Core
- **Kotlin** 2.0.21 - Lenguaje de programación
- **Android SDK** 36 - Plataforma Android
- **Gradle** 8.13 - Sistema de build

### UI
- **Jetpack Compose** - Framework UI declarativo moderno
- **Material 3** - Sistema de diseño de Google
- **Material Icons Extended** - Biblioteca extendida de iconos
- **Compose Navigation** 2.7.7 - Navegación entre pantallas

### Data & Storage
- **Room Database** 2.6.1 - Base de datos SQLite con ORM
- **KSP** 2.0.21-1.0.28 - Kotlin Symbol Processing
- **Coroutines** 1.8.0 - Programación asíncrona

### Architecture Components
- **ViewModel** 2.7.0 - Gestión de estado UI
- **LiveData** 2.7.0 - Datos observables lifecycle-aware
- **Lifecycle** 2.7.0 - Manejo del ciclo de vida

### Patterns & Practices
- **MVVM** - Arquitectura recomendada por Google
- **Repository Pattern** - Abstracción de fuentes de datos
- **Dependency Injection** - Manual (ViewModelFactory)
- **Flow & StateFlow** - Reactive Streams

## Estructura del Proyecto

```
app/src/main/
├── java/com/example/gestordemaestrias/
│   ├── data/
│   │   ├── database/
│   │   │   ├── AppDatabase.kt          # Configuración Room
│   │   │   └── DatabaseProvider.kt     # Singleton DB (Aun no implementado)
│   │   ├── dao/
│   │   │   ├── CampusDao.kt            # Operaciones Campus
│   │   │   ├── FacultadDao.kt          # Operaciones Facultad
│   │   │   ├── TipoMaestriaDao.kt      # Operaciones Tipo
│   │   │   └── MaestriaDao.kt          # Operaciones Maestría
│   │   ├── entity/
│   │   │   ├── Campus.kt               # Entidad Campus
│   │   │   ├── Facultad.kt             # Entidad Facultad
│   │   │   ├── TipoMaestria.kt         # Entidad Tipo
│   │   │   └── Maestria.kt             # Entidad Maestría
│   │   └── repository/
│   │       ├── CampusRepository.kt     # Lógica Campus
│   │       ├── FacultadRepository.kt   # Lógica Facultad
│   │       ├── TipoMaestriaRepository.kt # Logica TIpo Maestria
│   │       └── MaestriaRepository.kt   # Lógica Maestría
│   ├── ui/
│   │   ├── screens/
│   │   │   ├── MenuPrincipalScreen.kt  # Pantalla principal
│   │   │   ├── campus/
│   │   │   │   ├── CampusListScreen.kt
│   │   │   │   └── CampusFormScreen.kt
│   │   │   ├── facultad/
│   │   │   │   ├── FacultadListScreen.kt
│   │   │   │   └── FacultadFormScreen.kt
│   │   │   ├── tipomaestria/
│   │   │   │   ├── TipoMaestriaListScreen.kt
│   │   │   │   └── TipoMaestriaFormScreen.kt
│   │   │   └── maestria/
│   │   │       ├── MaestriaListScreen.kt
│   │   │       └── MaestriaFormScreen.kt
│   │   ├── components/
│   │   │   ├── CommonComponents.kt    # Componentes reutilizables (Aun no terminado/establecido)
|   |   |   ├── ActionDialog.kt  # Componente de dialog de acciones
|   |   |   ├── SimpleEmptyState.kt  # Componente de Simpl Empty State 
|   |   |   ├── SimpleFilterPanel.kt  # Panel de filtro para datos referenciales
|   |   |   ├── SimpleInfoCard.kt  # Cards de datos para datos referenciales
|   |   |   ├── SimpleSortMenu.kt  # Menu de Ordenamiento para datos referenciales
|   |   |   └── StatusChip.kt  # Chip de Estado para las Cards de datos
│   │   ├── viewmodel/
│   │   │   ├── CampusViewModel.kt
│   │   │   ├── FacultadViewModel.kt
│   │   │   ├── TipoMaestriaViewModel.kt
│   │   │   ├── MaestriaViewModel.kt
│   │   │   └── ViewModelFactory.kt    # Factories
│   │   ├── navigation/
│   │   │   └── AppNavigation.kt       # Navegación
│   │   └── theme/
│   │       └── Theme.kt                # Tema Material 3
│   └── MainActivity.kt                 # Activity principal
├── res/
│   ├── values/
│   │   ├── strings.xml                 # Strings
│   │   └── themes.xml                  # Temas XML
│   └── mipmap/                         # Iconos app
└── AndroidManifest.xml                 # Manifest
```

## Requisitos

### Requisitos del Sistema
- **Android Studio**: Ladybug o superior
- **JDK**: 11 o superior
- **Android SDK**: API 24 (Android 7.0) o superior
- **Gradle**: 8.13 o superior

### Requisitos del Dispositivo
- **Android Mínimo**: API 24 (Android 7.0 Nougat)
- **Android Target**: API 36
- **RAM Recomendada**: 2GB o más
- **Espacio**: 50MB aproximadamente

## Instalación

### 1. Clonar el Repositorio (Tambien puedes clonar desde Android Studio)

```bash
https://github.com/MaxsForocca/Gestor-de-Maestrias.git
cd Gestor-de-Maestrias
```

### 2. Abrir en Android Studio

1. Abre Android Studio
2. Selecciona `File > Open`
3. Navega a la carpeta del proyecto
4. Espera a que Gradle sincronice las dependencias

### 3. Configurar el SDK

Asegúrate de tener instalado:
- Android SDK Platform 36
- Android SDK Build-Tools
- Android Emulator (opcional)

### 4. Compilar el Proyecto

```bash
# Desde terminal
./gradlew clean build

# O desde Android Studio
Build > Make Project
```

### 5. Ejecutar la Aplicación

**Opción A: Emulador**
```bash
./gradlew installDebug
```

**Opción B: Dispositivo físico**
1. Habilita "Depuración USB" en tu dispositivo
2. Conecta el dispositivo por USB
3. Ejecuta desde Android Studio: `Run > Run 'app'`

## Uso

### Inicio Rápido

1. **Menú Principal**: Al abrir la app, verás 4 opciones:
   - 📍 Campus
   - 🏛️ Facultad
   - 📂 Tipo de Maestría
   - 🎓 Maestría

2. **Crear Registro**:
   - Selecciona una categoría
   - Presiona el botón flotante `+`
   - Completa el formulario
   - Presiona `Guardar`

3. **Buscar**:
   - Usa la barra de búsqueda superior
   - Escribe cualquier término relacionado
   - Los resultados se filtran en tiempo real

4. **Filtrar**:
   - Presiona el ícono de filtro (Ubicado en la parte superior derecha)
   - Selecciona los criterios deseados
   - Los filtros se aplican inmediatamente
   - Usa "Limpiar" para resetear

5. **Ordenar**:
   - Presiona el ícono de ordenar (Al lado del icono de filtro)
   - Selecciona el criterio de orden
   - La lista se reorganiza automáticamente

6. **Editar/Eliminar**:
   - Toca cualquier tarjeta de la lista
   - Selecciona la acción deseada
   - Confirma cuando sea necesario

## Base de Datos

### Esquema de Tablas

#### Campus
```sql
CREATE TABLE campus (
    codigo INTEGER PRIMARY KEY AUTOINCREMENT,
    nombre TEXT NOT NULL,
    estadoRegistro TEXT DEFAULT 'A'  -- A=Activo, I=Inactivo, *=Eliminado
);
```

#### Facultad
```sql
CREATE TABLE facultad (
    codigo INTEGER PRIMARY KEY AUTOINCREMENT,
    nombre TEXT NOT NULL,
    estadoRegistro TEXT DEFAULT 'A'
);
```

#### TipoMaestria
```sql
CREATE TABLE tipo_maestria (
    codigo INTEGER PRIMARY KEY AUTOINCREMENT,
    nombre TEXT NOT NULL,
    estadoRegistro TEXT DEFAULT 'A'
);
```

#### Maestria (con Foreign Keys)
```sql
CREATE TABLE maestria (
    codigo INTEGER PRIMARY KEY AUTOINCREMENT,
    nombre TEXT NOT NULL,
    tipoMaestriaCodigo INTEGER NOT NULL,
    facultadCodigo INTEGER NOT NULL,
    campusCodigo INTEGER NOT NULL,
    estadoRegistro TEXT DEFAULT 'A',
    FOREIGN KEY (tipoMaestriaCodigo) REFERENCES tipo_maestria(codigo),
    FOREIGN KEY (facultadCodigo) REFERENCES facultad(codigo),
    FOREIGN KEY (campusCodigo) REFERENCES campus(codigo)
);
```

### Datos de Ejemplo

Al instalar la app por primera vez, se crean automáticamente:

**Campus:**
- Ingenierías
- Sociales

**Facultades:**
- Facultad de Ingeniería de Producción y Servicios
- Facultad de Ciencias Histórico Sociales
- Facultad de Ciencias Naturales y Formales

**Tipos de Maestría:**
- Maestria Profesional
- Maestria Academica

**Maestrías:**
- Maestría en Ciencia de Datos
- Maestría en Ciencias: con mención en Comunicación

## Funcionalidades

### Por Entidad

#### Campus / Facultad / Tipo de Maestría
- ✅ Crear nuevo registro
- ✅ Listar todos los registros
- ✅ Buscar por nombre
- ✅ Editar registro existente
- ✅ Activar/Desactivar registro
- ✅ Eliminar registro (lógico)
- ✅ Validación de campos

#### Maestría (Funcionalidad Completa)
- ✅ Crear con relaciones (Tipo, Facultad, Campus)
- ✅ Listar con información completa
- ✅ Buscar en todos los campos
- ✅ Filtrar por:
  - Tipo de Maestría
  - Facultad
  - Campus
  - Estado (Activo/Inactivo)
- ✅ Ordenar por:
  - Código (ascendente/descendente)
  - Nombre (A-Z / Z-A)
  - Tipo (A-Z / Z-A)
  - Facultad (A-Z / Z-A)
  - Campus (A-Z / Z-A)
- ✅ Editar con actualización de relaciones
- ✅ Eliminar con confirmación
- ✅ Activar/Desactivar

### Estados de Registro

| Estado | Valor | Significado | Color |
|--------|-------|-------------|-------|
| Activo | `A` | Registro activo y visible | 🟢 Verde |
| Inactivo | `I` | Registro desactivado temporalmente | ⚪ Gris |
| Eliminado | `*` | Eliminación lógica (no se muestra) | 🔴 Rojo |

## Autores

### Equipo de Desarrollo

- **Subgrupo 34** - *Desarrollo Móvil - Negocios Electrónicos*

---

## Referencias

### Documentación Oficial
- [Android Developers](https://developer.android.com/)
- [Jetpack Compose](https://developer.android.com/jetpack/compose)
- [Room Database](https://developer.android.com/training/data-storage/room)
- [Kotlin](https://kotlinlang.org/docs/home.html)
- [Material Design 3](https://m3.material.io/)

### Tutoriales Utilizados
- [Android Studio Tutorial](https://www.youtube.com/watch?v=sILYPMvXDvY)
- [Kotlin Android desde Cero](https://www.youtube.com/watch?v=ebQphhLpJG0)
- [Tutorial Android Ya](https://www.tutorialesprogramacionya.com/javaya/androidya/androidstudioya/)

---

<div align="center">

**Desarrollado por el Subgrupo 34 - Lab Negocios Electronicos Grupo D**

[⬆ Volver arriba](#gestor-de-maestrías)

</div>
