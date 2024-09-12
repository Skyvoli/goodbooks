plugins {
    id("com.android.application")
}

val zxingVersion = "4.3.0"
val jsoupVersion = "1.17.2"
val fasterxml = "2.17.0"
val appCompatVersion = "1.6.1"
val materialVersion = "1.11.0"
val constraintVersion = "2.1.4"
val lifecycleVersion = "2.7.0"
val navigationVersion = "2.7.7"
val junitVersion = "4.13.2"
val roomVersion = "2.6.1"
val databinding = "8.3.2"
val swipeVersion = "1.2.0-alpha01"
val glideVersion = "4.11.0"

dependencies {

    implementation("androidx.databinding:databinding-common:$databinding")
    implementation("androidx.databinding:databinding-runtime:$databinding")

    implementation("androidx.room:room-runtime:$roomVersion")
    // optional
    implementation("androidx.room:room-ktx:$roomVersion")
    implementation("androidx.room:room-rxjava2:$roomVersion")
    implementation("androidx.room:room-rxjava3:$roomVersion")
    implementation("androidx.room:room-guava:$roomVersion")
    implementation("androidx.room:room-paging:$roomVersion")

    implementation("com.journeyapps:zxing-android-embedded:$zxingVersion")
    implementation("org.jsoup:jsoup:$jsoupVersion")
    implementation("com.fasterxml.jackson.core:jackson-core:$fasterxml")
    implementation("com.fasterxml.jackson.core:jackson-databind:$fasterxml")
    implementation("androidx.appcompat:appcompat:$appCompatVersion")
    implementation("com.google.android.material:material:$materialVersion")
    implementation("androidx.constraintlayout:constraintlayout:$constraintVersion")
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:$lifecycleVersion")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:$lifecycleVersion")
    implementation("androidx.navigation:navigation-fragment:$navigationVersion")
    implementation("androidx.navigation:navigation-ui:$navigationVersion")
    implementation("androidx.swiperefreshlayout:swiperefreshlayout:$swipeVersion")
    implementation("com.github.bumptech.glide:glide:$glideVersion")

    annotationProcessor("com.github.bumptech.glide:compiler:$glideVersion")
    annotationProcessor("androidx.room:room-compiler:$roomVersion")
    annotationProcessor("androidx.room:room-compiler:$roomVersion")
    annotationProcessor("androidx.room:room-compiler:$roomVersion")


    testImplementation("junit:junit:$junitVersion")
    testImplementation("androidx.room:room-testing:$roomVersion")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
}

android {
    namespace = "io.skyvoli.goodbooks"
    compileSdk = 34

    defaultConfig {
        applicationId = "io.skyvoli.goodbooks"
        minSdk = 30
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        javaCompileOptions {
            annotationProcessorOptions {
                arguments["room.schemaLocation"] =
                    "$projectDir/schemas"
            }
        }
    }

    lint {
        lintConfig = file("$rootDir/glideLintDisable.xml")
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    buildFeatures {
        viewBinding = true
    }
}