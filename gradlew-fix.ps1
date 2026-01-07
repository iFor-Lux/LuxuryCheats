# Script para ejecutar Gradle con el JDK de Android Studio
# Soluciona problemas de compatibilidad con Java 25

$env:JAVA_HOME="C:\Program Files\Android\Android Studio\jbr"
Write-Host "Using Android Studio JDK (Java 21)" -ForegroundColor Green
Write-Host "JAVA_HOME: $env:JAVA_HOME" -ForegroundColor Cyan
Write-Host ""

# Ejecutar gradle con los argumentos que se pasen
& .\gradlew.bat $args
