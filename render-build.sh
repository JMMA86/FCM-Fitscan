# Descargar e instalar OpenJDK 17 desde Adoptium
if [ ! -d "jdk-17" ]; then
    curl -L -o openjdk.tar.gz https://github.com/adoptium/temurin17-binaries/releases/download/jdk-17.0.11+9/OpenJDK17U-jdk_x64_linux_hotspot_17.0.11_9.tar.gz
    mkdir -p jdk-17
    tar -xzf openjdk.tar.gz -C jdk-17 --strip-components=1
    rm openjdk.tar.gz
fi

# Configurar JAVA_HOME
export JAVA_HOME=$PWD/jdk-17
export PATH=$JAVA_HOME/bin:$PATH

# Ejecutar Maven Wrapper
./mvnw clean install
