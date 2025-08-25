#ifndef CURP_LIB_H
#define CURP_LIB_H

#include <stdbool.h>
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
    bool es_mayor_de_edad;
    int num_dependientes;
};

typedef struct {
    int cp;
    char colonias[50][60];
    int num_colonias;
} CPColonia;

// Prototipos de funciones

void asignar_id_entidad(struct Ciudadanos *ciudadano);
void asignar_colonia(struct Ciudadanos *ciudadano);
char primera_vocal_interna(struct Ciudadanos *ciudadano, int state);
char primera_consonante_interna(struct Ciudadanos *ciudadano, int state);
void generar_CURP_parcial(struct Ciudadanos *ciudadano);
void generar_CURP(struct Ciudadanos *ciudadano);
const char* seleccionar_colonia_menu(const CPColonia *cp_data);

#endif // CURP_LIB_H