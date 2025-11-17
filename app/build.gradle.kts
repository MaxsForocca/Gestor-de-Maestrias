plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    id("com.google.devtools.ksp")
}

android {
    namespace = "com.example.gestordemaestrias"
    compileSdk {
        version = release(36)
    }

    defaultConfig {
        applicationId = "com.example.gestordemaestrias"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        compose = true
    }
}

val room_version = "2.6.1"
val lifecycle_version = "2.7.0"
val coroutines_version = "1.7.3"

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
    // --------------- ROOM (Base de Datos) ---------------
    // Room: Implementación base
    implementation("androidx.room:room-runtime:$room_version")
    // Room: Soporte para Kotlin Coroutines/Ktx (funciones suspend)
    implementation("androidx.room:room-ktx:$room_version")
    // Room: Procesador de anotaciones (GENERACIÓN DE CÓDIGO)
    // Usar ksp si se seleccionó este plugin
    ksp("androidx.room:room-compiler:$room_version")

    // --------------- MVVM (Lifecycle & ViewModel) ---------------
    // ViewModel: Contiene la lógica de negocio y mantiene el estado de la UI
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:$lifecycle_version")
    // LiveData: El 'observador' que permite a la View reaccionar a los cambios de datos
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:$lifecycle_version")
    // Si se usa Fragmentos (Recomendado)
    implementation("androidx.fragment:fragment-ktx:1.6.2")

    // --------------- Coroutines (Asincronía) ---------------
    // Kotlin Coroutines: Librería para gestión asíncrona
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:${coroutines_version}")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:${coroutines_version}")

    // --------------- UI/Vistas ---------------
    // RecyclerView: Para mostrar la lista de registros
    implementation("androidx.recyclerview:recyclerview:1.3.2")
    // ConstraintLayout: Un sistema de diseño flexible (opcional, se puede usar LinearLayout/RelativeLayout)
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
}

