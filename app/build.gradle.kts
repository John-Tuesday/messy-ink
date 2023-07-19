plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("com.google.devtools.ksp")
    id("com.google.dagger.hilt.android")
    id("org.jetbrains.kotlin.kapt")
}

android {
    namespace = "org.calamarfederal.messyink"
    compileSdk = 34

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

    sourceSets {
        getByName("androidTest").assets.srcDir("$projectDir/schemas")
    }

    buildTypes {
        release {
            isDebuggable = false
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
        debug {
            isDebuggable = true
        }
    }
    compileOptions {
//        sourceCompatibility JavaVersion.VERSION_1_8
//        targetCompatibility JavaVersion.VERSION_1_8
        sourceCompatibility = JavaVersion.current()
        targetCompatibility = JavaVersion.current()
    }
    kotlinOptions {
        jvmTarget = "17"
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.4.8"
    }
//    packagingOptions {
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {
    // Essentials
    implementation(platform("androidx.compose:compose-bom:2023.06.01"))
    implementation("androidx.navigation:navigation-ui-ktx:2.7.0-beta02")
    implementation("androidx.navigation:navigation-runtime-ktx:2.7.0-beta02")
    implementation("androidx.navigation:navigation-fragment-ktx:2.7.0-beta02")
    implementation("androidx.navigation:navigation-compose:2.7.0-beta02")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.6.1")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.6.1")
    implementation("androidx.core:core-ktx:1.11.0-beta02")
    implementation("androidx.compose.ui:ui:1.5.0-beta02")
    implementation("androidx.compose.ui:ui-tooling-preview:1.5.0-beta02")
    implementation("androidx.compose.ui:ui-graphics:1.5.0-beta02")
    implementation("androidx.compose.material:material-icons-extended:1.6.0-alpha01")
    implementation("androidx.compose.material3:material3:1.2.0-alpha03")
    implementation("androidx.activity:activity-ktx:1.7.2")
    implementation("androidx.activity:activity-compose:1.7.2")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.6.1")
    implementation("androidx.lifecycle:lifecycle-extensions:2.2.0")
    implementation("androidx.constraintlayout:constraintlayout-compose:1.0.1")
    testImplementation("junit:junit:4.13.2")
    testImplementation("junit:junit:4.13.2")
    debugImplementation("androidx.compose.ui:ui-tooling:1.5.0-beta02")
    debugImplementation("androidx.compose.ui:ui-test-manifest:1.5.0-beta02")
    androidTestImplementation(platform("androidx.compose:compose-bom:2023.06.01"))
    androidTestImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test:runner:1.5.2")
    androidTestImplementation("androidx.test:core:1.5.0")
    androidTestImplementation("androidx.test:core-ktx:1.5.0")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.ext:junit-ktx:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    androidTestImplementation("androidx.navigation:navigation-testing:2.7.0-beta02")
    androidTestImplementation("androidx.compose.ui:ui-test-junit4:1.5.0-beta02")
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
    val hiltVersion = "2.46.1"

    kaptAndroidTest("com.google.dagger:hilt-android-compiler:$hiltVersion")
    kapt("com.google.dagger:hilt-android-compiler:$hiltVersion")
    implementation("com.google.dagger:hilt-android:2.46.1")
    implementation("androidx.hilt:hilt-navigation-compose:1.0.0")
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
