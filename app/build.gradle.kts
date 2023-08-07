import java.io.FileInputStream
import java.util.Properties

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("com.google.devtools.ksp")
    id("com.google.dagger.hilt.android")
    id("org.jetbrains.kotlin.kapt")
}

val keystorePropertiesFile: File = rootProject.file("keystore.properties")
val keystoreProperties = Properties()
keystoreProperties.load(FileInputStream(keystorePropertiesFile))

android {
    namespace = "org.calamarfederal.messyink"
    compileSdk = 34

    signingConfigs {
        create("releaseConfig") {
            keyAlias = keystoreProperties["keyAlias"] as String
            keyPassword = keystoreProperties["keyPassword"] as String
            storeFile = rootProject.file(keystoreProperties["storeFile"] as String)
            storePassword = keystoreProperties["storePassword"] as String
        }
    }

    defaultConfig {
        applicationId = "org.calamarfederal.messyink"
        minSdk = 33
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "org.calamarfederal.messyink.HiltTestRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    buildTypes {
        release {
            signingConfigs {
                signingConfig = getByName("releaseConfig")
            }
            isDebuggable = false
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
        debug {
            isDebuggable = true
            applicationIdSuffix = ".debug"
            versionNameSuffix = ".debug"
        }
    }

    sourceSets {
        getByName("androidTest") {
            assets.srcDir("$projectDir/schemas")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.1"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
    buildToolsVersion = "34.0.0"
}

dependencies {
    // Essentials
    testImplementation("junit:junit:4.13.2")
    implementation(platform("androidx.compose:compose-bom:2023.06.01"))
    implementation("androidx.navigation:navigation-ui-ktx:2.7.0-rc01")
    implementation("androidx.navigation:navigation-runtime-ktx:2.7.0-rc01")
    implementation("androidx.navigation:navigation-fragment-ktx:2.7.0-rc01")
    implementation("androidx.navigation:navigation-compose:2.7.0-rc01")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.6.1")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.6.1")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.6.1")
    implementation("androidx.lifecycle:lifecycle-runtime-compose:2.6.1")
    implementation("androidx.lifecycle:lifecycle-extensions:2.2.0")
    implementation("androidx.core:core-ktx:1.12.0-beta01")
    implementation("androidx.constraintlayout:constraintlayout-compose:1.1.0-alpha11")
    implementation("androidx.compose.ui:ui:1.6.0-alpha02")
    implementation("androidx.compose.ui:ui-tooling-preview:1.6.0-alpha02")
    implementation("androidx.compose.ui:ui-graphics:1.6.0-alpha02")
    implementation("androidx.compose.material:material-icons-extended:1.6.0-alpha02")
    implementation("androidx.compose.material3:material3:1.2.0-alpha04")
    implementation("androidx.activity:activity-ktx:1.8.0-alpha06")
    implementation("androidx.activity:activity-compose:1.8.0-alpha06")
    // force upgrade to 1.1.0 because its required by androidTestImplementation,
    // and without this statement AGP will silently downgrade to tracing:1.0.0
    implementation("androidx.tracing:tracing:1.1.0")
    debugImplementation("androidx.compose.ui:ui-tooling:1.6.0-alpha02")
    debugImplementation("androidx.compose.ui:ui-test-manifest:1.6.0-alpha02")
    androidTestImplementation(platform("androidx.compose:compose-bom:2023.06.01"))
    androidTestImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test:runner:1.6.0-alpha03")
    androidTestImplementation("androidx.test:core:1.6.0-alpha01")
    androidTestImplementation("androidx.test:core-ktx:1.6.0-alpha01")
    androidTestImplementation("androidx.test.ext:junit:1.2.0-alpha01")
    androidTestImplementation("androidx.test.ext:junit-ktx:1.2.0-alpha01")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.6.0-alpha01")
    androidTestImplementation("androidx.navigation:navigation-testing:2.7.0-rc01")
    androidTestImplementation("androidx.compose.ui:ui-test-junit4:1.6.0-alpha02")
    androidTestImplementation("androidx.arch.core:core-testing:2.2.0")

    // Datetime - Kotlin
    implementation("org.jetbrains.kotlinx:kotlinx-datetime:0.4.0")

    // Room
    val roomVersion = "2.6.0-alpha02"

    ksp("androidx.room:room-compiler:$roomVersion")
    implementation("androidx.room:room-runtime:2.6.0-alpha02")
    implementation("androidx.room:room-ktx:$roomVersion")
    testImplementation("androidx.room:room-testing:$roomVersion")

    // Hilt
    val hiltVersion = "2.47"

    kaptAndroidTest("com.google.dagger:hilt-android-compiler:$hiltVersion")
    kapt("com.google.dagger:hilt-android-compiler:$hiltVersion")
    implementation("com.google.dagger:hilt-android:2.47")
    implementation("androidx.hilt:hilt-navigation-compose:1.1.0-alpha01")
    androidTestImplementation("com.google.dagger:hilt-android-testing:$hiltVersion")
}

/**
 * Argument provider specifying the directory to export Room schema
 */
class RoomSchemaArgProvider(
    /**
     * Path to export schema
     */
    @get:InputDirectory
    @get:PathSensitive(PathSensitivity.RELATIVE)
    val schemaDir: File
) : CommandLineArgumentProvider {
    /**
     * Provides path to export schema
     */
    override fun asArguments(): Iterable<String> {
        return listOf("room.schemaLocation=${schemaDir.path}")
    }
}

ksp {
    arg(RoomSchemaArgProvider(File(projectDir, "schemas")))
}

kapt {
    correctErrorTypes = true
}
