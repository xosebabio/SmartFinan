plugins {
    id("com.android.application") version "8.1.2" apply false
    id("com.android.library") version "8.1.2" apply false
    id("org.jetbrains.kotlin.android") version "1.9.0" apply false
    id("org.sonarqube") version "3.3"
}

sonarqube {
    properties {
        property("sonar.projectKey", "xosebabio_SmartFinan")
        property("sonar.organization", "xosebabio")
        property("sonar.host.url", "https://sonarcloud.io")
        property("sonar.login", "440baa6c37ec49722d55985b816b6690b3487af2")
    }
}