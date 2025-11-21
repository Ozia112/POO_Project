#!/bin/bash

# Script para ejecutar las pruebas de LockerEasy
# Autor: Equipo LockerEasy
# Fecha: 20 de noviembre de 2025

echo "================================"
echo "   LockerEasy - Suite de Pruebas"
echo "================================"
echo ""

# Colores para output
GREEN='\033[0;32m'
BLUE='\033[0;34m'
RED='\033[0;31m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# Función para mostrar menú
show_menu() {
    echo "Seleccione el tipo de prueba a ejecutar:"
    echo ""
    echo "1) Prueba de Consola Original (App)"
    echo "2) Ejecutar Pruebas Unitarias (JUnit)"
    echo "3) Ejecutar TODAS las Pruebas Unitarias con Reporte"
    echo "4) Compilar el proyecto"
    echo "5) Limpiar y Compilar"
    echo "0) Salir"
    echo ""
    echo -e "${YELLOW}Nota: La interfaz JavaFX está pendiente por @Nico${NC}"
    echo ""
}

# Función para ejecutar interfaz JavaFX
run_gui_test() {
    echo -e "${YELLOW}[PENDIENTE]${NC} La interfaz JavaFX será implementada por @Nico"
    echo ""
    echo "Mientras tanto, puedes:"
    echo "- Usar la prueba de consola (opción 1)"
    echo "- Ejecutar las pruebas unitarias (opción 2)"
}

# Función para ejecutar prueba de consola
run_console_test() {
    echo -e "${BLUE}[INFO]${NC} Iniciando prueba de consola..."
    echo ""
    mvn clean compile
    if [ $? -eq 0 ]; then
        echo -e "${GREEN}[OK]${NC} Compilación exitosa"
        echo -e "${BLUE}[INFO]${NC} Ejecutando App.java..."
        echo "-----------------------------------"
        mvn exec:java -Dexec.mainClass="App"
        echo "-----------------------------------"
    else
        echo -e "${RED}[ERROR]${NC} Error en la compilación"
    fi
}

# Función para ejecutar pruebas unitarias
run_unit_tests() {
    echo -e "${BLUE}[INFO]${NC} Ejecutando pruebas unitarias..."
    echo ""
    mvn test
    if [ $? -eq 0 ]; then
        echo ""
        echo -e "${GREEN}[OK]${NC} Todas las pruebas pasaron exitosamente"
    else
        echo ""
        echo -e "${RED}[ERROR]${NC} Algunas pruebas fallaron"
    fi
}

# Función para ejecutar todas las pruebas con reporte
run_all_tests_with_report() {
    echo -e "${BLUE}[INFO]${NC} Ejecutando suite completa de pruebas..."
    echo ""
    mvn clean test
    echo ""
    echo -e "${YELLOW}[INFO]${NC} Los reportes se encuentran en: target/surefire-reports/"
}

# Función para compilar
compile_project() {
    echo -e "${BLUE}[INFO]${NC} Compilando proyecto..."
    echo ""
    mvn compile
    if [ $? -eq 0 ]; then
        echo ""
        echo -e "${GREEN}[OK]${NC} Compilación exitosa"
    else
        echo ""
        echo -e "${RED}[ERROR]${NC} Error en la compilación"
    fi
}

# Función para limpiar y compilar
clean_and_compile() {
    echo -e "${BLUE}[INFO]${NC} Limpiando y compilando proyecto..."
    echo ""
    mvn clean compile
    if [ $? -eq 0 ]; then
        echo ""
        echo -e "${GREEN}[OK]${NC} Limpieza y compilación exitosas"
    else
        echo ""
        echo -e "${RED}[ERROR]${NC} Error en la compilación"
    fi
}

# Verificar que estamos en el directorio correcto
if [ ! -f "pom.xml" ]; then
    echo -e "${RED}[ERROR]${NC} No se encontró pom.xml"
    echo "Por favor, ejecute este script desde el directorio LockerEasy/"
    exit 1
fi

# Loop principal
while true; do
    show_menu
    read -p "Opción: " option
    echo ""
    
    case $option in
        1)
            run_console_test
            ;;
        2)
            run_unit_tests
            ;;
        3)
            run_all_tests_with_report
            ;;
        4)
            compile_project
            ;;
        5)
            clean_and_compile
            ;;
        0)
            echo -e "${GREEN}¡Hasta luego!${NC}"
            exit 0
            ;;
        *)
            echo -e "${RED}[ERROR]${NC} Opción inválida"
            ;;
    esac
    
    echo ""
    echo "Presione Enter para continuar..."
    read
    clear
done
