#include <ctype.h>
#include <string.h>
#include <stdlib.h>
#include <stdio.h>
#include <windows.h>

#define CURRENT_YEAR 2025
#define CURRENT_MONTH 8
#define CURRENT_DAY 19

#define YEAR 0
#define MONTH 1
#define DAY 2

#define PATERNO 0
#define MATERNO 1
#define NOMBRE 2

struct Direccion {
    char calle[255];
    char colonia[255];
    char numero[255];
    int codigo_postal;
};

struct Apellidos {
    char paterno[255];
    char materno[255];
};

struct FechaNacimiento {
    int year;
    int month;
    int day;
};

struct Ciudadanos {
    char nombres[255];
    char primer_nombre[255];
    char segundo_nombre[255];
    struct Apellidos apellidos;
    struct FechaNacimiento fecha_nacimiento;    
    int edad;
    struct Direccion direccion;
    char entidad[255];
    char id_entidad[3];
    char genero;
    char curp_parcial[17];
    char homoclave[3];
    char curp[19];
    char email[255];
    char telefono[11];
    int num_dependientes;
};

void parsear_nombres(const char *nombre_completo, char *primer_nombre, char *segundo_nombre) {
    // Copia el primer nombre hasta el primer espacio
    int i = 0, j = 0;
    while (nombre_completo[i] && nombre_completo[i] != ' ') {
        primer_nombre[i] = nombre_completo[i];
        i++;
    }
    primer_nombre[i] = '\0';
    // Si hay más nombres, copia el resto en segundo_nombre
    if (nombre_completo[i] == ' ') {
        i++; // Saltar el espacio
        while (nombre_completo[i]) {
            segundo_nombre[j++] = nombre_completo[i++];
        }
    }
    segundo_nombre[j] = '\0';
}

int calcular_edad(struct Ciudadanos *ciudadano) {
    int edad = 0;
        edad = CURRENT_YEAR - ciudadano->fecha_nacimiento.year;
        edad = CURRENT_YEAR - ciudadano->fecha_nacimiento.year;
        if (ciudadano->fecha_nacimiento.month > CURRENT_MONTH) {
            edad--;
        }
        edad = CURRENT_YEAR - ciudadano->fecha_nacimiento.year;
        if (ciudadano->fecha_nacimiento.month == CURRENT_MONTH 
            && ciudadano->fecha_nacimiento.day > CURRENT_DAY) {
            edad--;
        }
    return edad;
}

int agregar_fecha_nacimiento(struct Ciudadanos *ciudadano, int index) {
    switch (index) {
        case 0: // Primer ciudadano
        ciudadano->fecha_nacimiento.year = 2001;
        ciudadano->fecha_nacimiento.month = 12;
        ciudadano->fecha_nacimiento.day = 1;
        break;
        case 1: // Segundo ciudadano
        ciudadano->fecha_nacimiento.year = 1995;
        ciudadano->fecha_nacimiento.month = 6;
        ciudadano->fecha_nacimiento.day = 15;
        break;
        case 2: // Tercer ciudadano
        ciudadano->fecha_nacimiento.year = 2009;
        ciudadano->fecha_nacimiento.month = 3;
        ciudadano->fecha_nacimiento.day = 22;
        break;
    }
    ciudadano->edad = calcular_edad(ciudadano);
    return ciudadano->edad < 18 ? 0 : 1;
} 

int agregar_nombres(struct Ciudadanos *ciudadano, int index) {
    switch (index) {
        case 0: // Primer ciudadano
        strncpy(ciudadano->nombres, "ISAAC ALEJANDRO", sizeof(ciudadano->nombres) - 1);
        ciudadano->nombres[sizeof(ciudadano->nombres) - 1] = '\0';
        if (strlen(ciudadano->nombres) == 0) return 0;
        parsear_nombres(ciudadano->nombres, ciudadano->primer_nombre, ciudadano->segundo_nombre);

        strncpy(ciudadano->apellidos.paterno, "ORTIZ", sizeof(ciudadano->apellidos.paterno) - 1);
        ciudadano->apellidos.paterno[sizeof(ciudadano->apellidos.paterno) - 1] = '\0';
        if (strlen(ciudadano->apellidos.paterno) == 0) return 0;

        strncpy(ciudadano->apellidos.materno, "ZALDIVAR", sizeof(ciudadano->apellidos.materno) - 1);
        ciudadano->apellidos.materno[sizeof(ciudadano->apellidos.materno) - 1] = '\0';
        if (strlen(ciudadano->apellidos.materno) == 0) return 0;
        break;
        case 1: // Segundo ciudadano
        strncpy(ciudadano->nombres, "MARIA FERNANDA", sizeof(ciudadano->nombres) - 1);
        ciudadano->nombres[sizeof(ciudadano->nombres) - 1] = '\0';
        if (strlen(ciudadano->nombres) == 0) return 0;
        parsear_nombres(ciudadano->nombres, ciudadano->primer_nombre, ciudadano->segundo_nombre);
        strncpy(ciudadano->apellidos.paterno, "LOPEZ", sizeof(ciudadano->apellidos.paterno) - 1);
        ciudadano->apellidos.paterno[sizeof(ciudadano->apellidos.paterno) - 1] = '\0';
        if (strlen(ciudadano->apellidos.paterno) == 0) return 0;
        strncpy(ciudadano->apellidos.materno, "GOMEZ", sizeof(ciudadano->apellidos.materno) - 1);
        ciudadano->apellidos.materno[sizeof(ciudadano->apellidos.materno) - 1] = '\0';
        if (strlen(ciudadano->apellidos.materno) == 0) return 0;
        break;
        case 2: // Tercer ciudadano
        strncpy(ciudadano->nombres, "JUAN CARLOS", sizeof(ciudadano->nombres) - 1);
        ciudadano->nombres[sizeof(ciudadano->nombres) - 1] = '\0';
        if (strlen(ciudadano->nombres) == 0) return 0;
        parsear_nombres(ciudadano->nombres, ciudadano->primer_nombre, ciudadano->segundo_nombre);
        strncpy(ciudadano->apellidos.paterno, "HERNANDEZ", sizeof(ciudadano->apellidos.paterno) - 1);
        ciudadano->apellidos.paterno[sizeof(ciudadano->apellidos.paterno) - 1] = '\0';
        if (strlen(ciudadano->apellidos.paterno) == 0) return 0;
        strncpy(ciudadano->apellidos.materno, "MARTINEZ", sizeof(ciudadano->apellidos.materno) - 1);
        ciudadano->apellidos.materno[sizeof(ciudadano->apellidos.materno) - 1] = '\0';
        if (strlen(ciudadano->apellidos.materno) == 0) return 0;
        break;
    }
    
    return 1;
}

int agregar_ciudadano(struct Ciudadanos *ciudadano, int *num_registros) {


    int index = *num_registros;
    
    if (agregar_fecha_nacimiento(&ciudadano[index], index) == 0) return 0;
    if (agregar_nombres(&ciudadano[index], index) == 0) return 0;

    // Direccion:
    strncpy(ciudadano[index].direccion.calle, "22", sizeof(ciudadano[index].direccion.calle) - 1);
    ciudadano[index].direccion.calle[sizeof(ciudadano[index].direccion.calle) - 1] = '\0';
    
    ciudadano[index].direccion.codigo_postal = 97170;
    asignar_colonia(&ciudadano[index]);
    
    strncpy(ciudadano[index].direccion.numero, "131c", sizeof(ciudadano[index].direccion.numero) - 1);
    ciudadano[index].direccion.numero[sizeof(ciudadano[index].direccion.numero) - 1] = '\0';

    // Contacto
    strncpy(ciudadano[index].email, "macintosh22plust@hotmail.com", sizeof(ciudadano[index].email) - 1);
    ciudadano[index].email[sizeof(ciudadano[index].email) - 1] = '\0';

    strncpy(ciudadano[index].telefono, "9994588510", sizeof(ciudadano[index].telefono) - 1);
    ciudadano[index].telefono[sizeof(ciudadano[index].telefono) - 1] = '\0';
    generar_CURP_parcial(&ciudadano[index]);
    
    strncpy(ciudadano[index].curp, "OIZI001201HYNRLSA5", sizeof(ciudadano[index].curp) - 1);
    ciudadano[index].curp[sizeof(ciudadano[index].curp) - 1] = '\0';
    if (strlen(ciudadano[index].curp) != 18) {
        printf("CURP inválido. Debe tener 18 caracteres.\n");
        return 0;
        for (int i = 0; i < index; i++) {
            // Comparar con otros CURP para evitar duplicados
            if (strcmp(ciudadano[i].curp, ciudadano[index].curp) == 0) {
                printf("CURP ya registrado. Intente de nuevo.\n");
                return 0;
            }
        }
    }

    printf("Ciudadano agregado exitosamente.\n");
    (*num_registros)++; // Incrementar después de agregar el ciudadano principal exitosamente
    
    struct Ciudadanos **ptr_ciudadano = &ciudadano;
    agregar_dependiente(ptr_ciudadano, num_registros, index);
}



int main() {
    SetConsoleOutputCP(CP_UTF8); // Configura la consola para UTF-8
    int registros = 0;
    struct Ciudadanos *ciudadano = NULL;
    while (1) {
        printf("[DEBUG]: Registros actuales: %d\n", registros);
        printf("Agregar ciudadano (1) o salir (0): ");
        int opcion;
        scanf("%d", &opcion);
        if (opcion == 0) break;
        ciudadano = realloc(ciudadano, sizeof(struct Ciudadanos) * (registros + 1)); // Agregar un nuevo ciudadano
        
        if (!ciudadano) {
            printf("Error de memoria\n");
            return 1;
        }

        agregar_ciudadano(ciudadano, &registros);
        printf("[DEBUG]: Registros actuales tras agregar ciudadano: %d\n", registros);
    }
    free(ciudadano);
    return 0;
}