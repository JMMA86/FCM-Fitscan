# Script combinado para construcción e inicio en Render
set -e

echo "=== Configurando Java ==="

# Configurar JAVA_HOME y PATH
if [ ! -d "jdk-17" ]; then
    echo "Descargando OpenJDK 17..."
    curl -L -o openjdk.tar.gz https://github.com/adoptium/temurin17-binaries/releases/download/jdk-17.0.11+9/OpenJDK17U-jdk_x64_linux_hotspot_17.0.11_9.tar.gz
    mkdir -p jdk-17
    tar -xzf openjdk.tar.gz -C jdk-17 --strip-components=1
    rm openjdk.tar.gz
    echo "OpenJDK 17 instalado correctamente"
fi

export JAVA_HOME=$PWD/jdk-17
export PATH=$JAVA_HOME/bin:$PATH

echo "Java version:"
java -version

# Si el argumento es 'build', solo construir
if [ "$1" = "build" ]; then
    echo "=== Construyendo aplicación ==="
    ./mvnw clean install
    echo "Construcción completada"
else
    echo "=== Iniciando aplicación ==="
    # Verificar que el JAR existe
    if [ ! -f "target/FCMNode-0.0.1-SNAPSHOT.jar" ]; then
        echo "JAR no encontrado, construyendo primero..."
        ./mvnw clean install
    fi
    
    echo "Iniciando Spring Boot..."
    java -jar target/FCMNode-0.0.1-SNAPSHOT.jar
fi
