plugins {
  alias(libs.plugins.android.application)
  alias(libs.plugins.kotlin.android)
  alias(libs.plugins.kotlin.compose)
  alias(libs.plugins.protobuf)
}

android {
  namespace = "com.signalsticker.maker"
  compileSdk = 35

  defaultConfig {
    applicationId = "com.signalsticker.maker"
    minSdk = 26
    targetSdk = 35
    versionCode = 1
    versionName = "1.0.0"
  }

  buildTypes {
    release {
      isMinifyEnabled = true
      proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
    }
  }

  compileOptions {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
  }

  kotlinOptions {
    jvmTarget = "17"
  }

  buildFeatures {
    compose = true
  }
}

protobuf {
  protoc { artifact = "com.google.protobuf:protoc:4.29.3" }
  generateProtoTasks {
    all().forEach { it.builtins { create("javalite") } }
  }
}

dependencies {
  implementation(libs.androidx.core.ktx)
  implementation(libs.androidx.lifecycle.runtime.ktx)
  implementation(libs.androidx.lifecycle.viewmodel.compose)
  implementation(libs.androidx.activity.compose)
  implementation(platform(libs.androidx.compose.bom))
  implementation(libs.androidx.ui)
  implementation(libs.androidx.ui.graphics)
  implementation(libs.androidx.ui.tooling.preview)
  implementation(libs.androidx.material3)
  implementation(libs.androidx.navigation.compose)
  implementation(libs.coil.compose)
  implementation(libs.protobuf.javalite)
  implementation(libs.kotlinx.coroutines.android)
  debugImplementation(libs.androidx.ui.tooling)
}
