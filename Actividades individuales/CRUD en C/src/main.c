#include "curp_lib.h"

void leer_cadena(char *destino, int tam) {
    fgets(destino, tam, stdin);
    size_t len = strlen(destino);
    if (len > 0 && destino[len - 1] == '\n') destino[len - 1] = '\0';

    // Convertir a mayúsculas, manejando caracteres especiales
    for (int i = 0; destino[i]; i++) {
        // Verificar diferentes codificaciones para ñ:
        // UTF-8: ñ = 0xC3 0xB1, Ñ = 0xC3 0x91
        // ISO-8859-1 / Windows-1252: ñ = 0xF1, Ñ = 0xD1
        // CP437 (DOS): ñ = 0xA4, Ñ = 0xA5
        
        if ((unsigned char)destino[i] == 0xC3 && 
            i + 1 < strlen(destino) && 
            (unsigned char)destino[i+1] == 0xB1) {
            // Convertir a Ñ mayúscula (0xC3 0x91)
            destino[i+1] = 0x91;
            i++; // Saltar el siguiente byte ya procesado
        } else if ((unsigned char)destino[i] == 0xF1) {
            // Convertir a Ñ mayúscula ISO-8859-1
            destino[i] = 0xD1;
        } else if ((unsigned char)destino[i] == 0xA4) {
            // Convertir a Ñ mayúscula CP437
            destino[i] = 0xA5;
        } else {
            destino[i] = toupper((unsigned char)destino[i]);
        }
    }
}

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

int calcular_edad(struct Ciudadanos *ciudadano, int state) {
    int edad = 0;
    if (state == YEAR) {
        edad = CURRENT_YEAR - ciudadano->fecha_nacimiento.year;
    } else if (state == MONTH) {
        edad = CURRENT_YEAR - ciudadano->fecha_nacimiento.year;
        if (ciudadano->fecha_nacimiento.month > CURRENT_MONTH) {
            edad--;
        }
    } else if (state == DAY) {
        edad = CURRENT_YEAR - ciudadano->fecha_nacimiento.year;
        if (ciudadano->fecha_nacimiento.month == CURRENT_MONTH 
            && ciudadano->fecha_nacimiento.day > CURRENT_DAY) {
            edad--;
        }
    }
    return edad;
}

void agregar_dependiente(struct Ciudadanos **ciudadano, int *num_registros, int tutor_index) {
    while ((*ciudadano)[tutor_index].num_dependientes < 3) {
        printf("¿desea agregar dependientes? (1: Sí, 0: No): ");
        int opcion;
        scanf("%d", &opcion);
        
        if (opcion != 1) {
            break; // Salir si no quiere agregar más
        }

        // Expandir memoria antes de usar el nuevo índice
        *ciudadano = (struct Ciudadanos *)realloc(*ciudadano, sizeof(struct Ciudadanos) * (*num_registros + 1));
        if (!*ciudadano) {
            printf("Error: No se pudo asignar memoria.\n");
            return;
        }

        // Usar el índice correcto (antes de incrementar num_registros)
        int index = *num_registros;

        printf("=== Datos del dependiente %d ===\n", (*ciudadano)[tutor_index].num_dependientes + 1);
        
        printf("Ingrese año de nacimiento: ");
        scanf("%d", &(*ciudadano)[index].fecha_nacimiento.year);
        printf("Ingrese mes de nacimiento: ");
        scanf("%d", &(*ciudadano)[index].fecha_nacimiento.month);
        printf("Ingrese día de nacimiento: ");
        scanf("%d", &(*ciudadano)[index].fecha_nacimiento.day);

        (*ciudadano)[index].edad = calcular_edad(&(*ciudadano)[index], DAY);
        (*ciudadano)[index].es_mayor_de_edad = ((*ciudadano)[index].edad >= 18);
        (*ciudadano)[index].num_dependientes = 0; // Los dependientes no tienen dependientes

        while (getchar() != '\n'); // Limpiar el buffer de entrada

        printf("Ingrese nombre(s): ");
        leer_cadena((*ciudadano)[index].nombres, sizeof((*ciudadano)[index].nombres));
        parsear_nombres((*ciudadano)[index].nombres, (*ciudadano)[index].primer_nombre, (*ciudadano)[index].segundo_nombre);

        printf("Ingrese apellido paterno: ");
        leer_cadena((*ciudadano)[index].apellidos.paterno, sizeof((*ciudadano)[index].apellidos.paterno));
        printf("Ingrese apellido materno: ");
        leer_cadena((*ciudadano)[index].apellidos.materno, sizeof((*ciudadano)[index].apellidos.materno));

        printf("Estado de nacimiento: ");
        leer_cadena((*ciudadano)[index].entidad, sizeof((*ciudadano)[index].entidad));
        asignar_id_entidad(&(*ciudadano)[index]);

        printf("Ingrese el género (H/M): ");
        scanf(" %c", &(*ciudadano)[index].genero);
        (*ciudadano)[index].genero = toupper((*ciudadano)[index].genero);

        // Copiar datos del tutor
        strcpy((*ciudadano)[index].direccion.calle, (*ciudadano)[tutor_index].direccion.calle);
        (*ciudadano)[index].direccion.codigo_postal = (*ciudadano)[tutor_index].direccion.codigo_postal;
        strcpy((*ciudadano)[index].direccion.colonia, (*ciudadano)[tutor_index].direccion.colonia);
        strcpy((*ciudadano)[index].direccion.numero, (*ciudadano)[tutor_index].direccion.numero);
        strcpy((*ciudadano)[index].email, (*ciudadano)[tutor_index].email);
        strcpy((*ciudadano)[index].telefono, (*ciudadano)[tutor_index].telefono);

        generar_CURP_parcial(&(*ciudadano)[index]);
        while (getchar() != '\n'); // Limpiar buffer antes de fgets
        printf("Ingrese la homoclave de su curp(Ultimos 2 caracteres): ");
        fgets((*ciudadano)[index].homoclave, sizeof((*ciudadano)[index].homoclave), stdin);
        // Eliminar el salto de línea al final
        size_t len = strlen((*ciudadano)[index].homoclave);
        if (len > 0 && (*ciudadano)[index].homoclave[len - 1] == '\n') {
            (*ciudadano)[index].homoclave[len - 1] = '\0';
        }
        
        // Convertir homoclave a mayúsculas
        for (int i = 0; (*ciudadano)[index].homoclave[i]; i++) {
            (*ciudadano)[index].homoclave[i] = toupper((unsigned char)(*ciudadano)[index].homoclave[i]);
        }

        generar_CURP(&(*ciudadano)[index]);
        printf("[DEBUG]: CURP: %s\n", (*ciudadano)[index].curp);

        // Solo incrementar el contador DESPUÉS de agregar exitosamente
        (*ciudadano)[tutor_index].num_dependientes++;
        printf("Dependiente agregado exitosamente.\n");
        (*num_registros)++; // Incrementar después de agregar el dependiente
    }
    
    if ((*ciudadano)[tutor_index].num_dependientes >= 3) {
        printf("Se ha alcanzado el límite máximo de 3 dependientes.\n");
    }
}

void agregar_ciudadano(struct Ciudadanos *ciudadano, int *num_registros) {
    int index = *num_registros;

    ciudadano[index].edad = 0; // Inicializa la edad
    ciudadano[index].es_mayor_de_edad = true; // Inicializa el estado de mayor de edad
    ciudadano[index].num_dependientes = 0; // Inicializa dependientes

    printf("Ingrese año de nacimiento: ");
    scanf("%d", &ciudadano[index].fecha_nacimiento.year);
    ciudadano[index].edad = calcular_edad(&ciudadano[index], YEAR);
    if (ciudadano[index].edad < 18) {
        printf("Debes de ser mayor de edad para registrarte.\n");
        return;
    }

    printf("Ingrese mes de nacimiento: ");
    scanf("%d", &ciudadano[index].fecha_nacimiento.month);
    ciudadano[index].edad = calcular_edad(&ciudadano[index], MONTH);
    if (ciudadano[index].edad < 18) {
        printf("Debes de ser mayor de edad para registrarte.\n");
        return;
    }

    printf("Ingrese día de nacimiento: ");
    scanf("%d", &ciudadano[index].fecha_nacimiento.day);

    ciudadano[index].edad = calcular_edad(&ciudadano[index], DAY);
    if (ciudadano[index].edad < 18) {
        printf("Debes de ser mayor de edad para registrarte.\n");
        return;
    }
    ciudadano[index].es_mayor_de_edad = true; // Si llegó aquí, es mayor de edad

    while (getchar() != '\n'); // Limpiar el buffer de entrada

    printf("Ingrese nombre(s): ");
    leer_cadena(ciudadano[index].nombres, sizeof(ciudadano[index].nombres));
    parsear_nombres(ciudadano[index].nombres, ciudadano[index].primer_nombre, ciudadano[index].segundo_nombre);

    printf("Ingrese apellido paterno: ");
    leer_cadena(ciudadano[index].apellidos.paterno, sizeof(ciudadano[index].apellidos.paterno));
    printf("[DEBUG]: Apellido paterno: %s\n", ciudadano[index].apellidos.paterno);
    
    // Debug adicional: mostrar los bytes del apellido almacenado
    printf("[DEBUG HEX FINAL]: Apellido paterno almacenado: ");
    for (int i = 0; ciudadano[index].apellidos.paterno[i]; i++) {
        printf("%02X ", (unsigned char)ciudadano[index].apellidos.paterno[i]);
    }
    printf("\n");
    
    printf("Ingrese apellido materno: ");
    leer_cadena(ciudadano[index].apellidos.materno, sizeof(ciudadano[index].apellidos.materno));
    printf("[DEBUG]: Apellido materno: %s\n", ciudadano[index].apellidos.materno);

    printf("Estado de nacimiento: ");
    leer_cadena(ciudadano[index].entidad, sizeof(ciudadano[index].entidad));
    printf("[DEBUG]: Entidad: %s\n", ciudadano[index].entidad);
    asignar_id_entidad(&ciudadano[index]);
    printf("[DEBUG]: ID Entidad: %s\n", ciudadano[index].id_entidad);

    printf("Ingrese el género (H/M): ");
    scanf(" %c", &ciudadano[index].genero);
    ciudadano[index].genero = toupper(ciudadano[index].genero);

    printf("Ingrese la calle: ");
    scanf("%49s", ciudadano[index].direccion.calle);
    printf("Ingrese el código postal: ");
    scanf("%d", &ciudadano[index].direccion.codigo_postal);
    asignar_colonia(&ciudadano[index]);
    printf("Ingrese el número de domicilio: ");
    scanf("%9s", ciudadano[index].direccion.numero);
    printf("[DEBUG]: Direccion: Calle %s, No. %s, Col. %s, CP: %d\n", ciudadano[index].direccion.calle, ciudadano[index].direccion.numero, ciudadano[index].direccion.colonia, ciudadano[index].direccion.codigo_postal);
    printf("Ingrese el email: ");
    scanf("%49s", ciudadano[index].email);
    printf("Ingrese el teléfono: ");
    scanf("%14s", ciudadano[index].telefono);
    while (getchar() != '\n'); // Limpiar buffer antes de fgets
    generar_CURP_parcial(&ciudadano[index]);
    
    printf("Ingrese la homoclave de su curp(Ultimos 2 caracteres): ");
    fgets(ciudadano[index].homoclave, sizeof(ciudadano[index].homoclave), stdin);
    // Eliminar el salto de línea al final
    size_t len = strlen(ciudadano[index].homoclave);
    if (len > 0 && ciudadano[index].homoclave[len - 1] == '\n') {
        ciudadano[index].homoclave[len - 1] = '\0';
    }
    
    // Convertir homoclave a mayúsculas
    for (int i = 0; ciudadano[index].homoclave[i]; i++) {
        ciudadano[index].homoclave[i] = toupper((unsigned char)ciudadano[index].homoclave[i]);
    }
    
    printf("[DEBUG]: Homoclave ingresada: '%s'\n", ciudadano[index].homoclave);

    generar_CURP(&ciudadano[index]);
    printf("[DEBUG]: CURP: %s\n", ciudadano[index].curp);

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