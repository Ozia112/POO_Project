#include <stdio.h>
#include <stdbool.h>
#include <ctype.h>
#include <string.h>
#include <stdlib.h>
#include <windows.h>

/* ---- Fecha para la edad  ---- */
#define CURRENT_YEAR 2025
#define CURRENT_MONTH 8
#define CURRENT_DAY 18

/* ---- Modelos  ---- */
struct Domicilio {
	char calle[50];
	char colonia[50];
	char numero[10];
	int codigo_postal;
};

struct NombresAp {
	char paterno[50];
	char materno[50];
};

struct FechaNac {
	int year;
	int month;
	int day;
};

struct Registro {
	char nombre_completo[50];
	char nombre1[50];
	char nombre2[50];
	struct NombresAp apellidos;
	struct FechaNac nacimiento;    
	int edad;
	struct Domicilio domicilio;
	char estado_nacimiento[50];
	char sexo;              // 'M' o 'F'
	char clave_curp[19];
	char correo[50];
	char celular[15];
	bool mayor_edad;
	int dependientes;
};

/* ===========================================================
Helpers cortos primero 
=========================================================== */

void leerTexto(char *destino, int tam) {
	fgets(destino, tam, stdin);
	size_t len = strlen(destino);
	if (len > 0 && destino[len - 1] == '\n') destino[len - 1] = '\0';
	for (int i = 0; destino[i]; i++) {
		destino[i] = (char)toupper((unsigned char)destino[i]);
	}
}

bool mayorEdad(int edad) {
	return edad >= 18;
}

char vocalInterna(const char *str) {
	for (int i = 1; str[i] != '\0'; i++) {
		char c = (char)toupper((unsigned char)str[i]);
		if (c=='A'||c=='E'||c=='I'||c=='O'||c=='U') return c;
	}
	return 'X';
}

char consInterna(const char *str) {
	for (int i = 1; str[i] != '\0'; i++) {
		char c = (char)toupper((unsigned char)str[i]);
		if (isalpha((unsigned char)c) && !(c=='A'||c=='E'||c=='I'||c=='O'||c=='U')) return c;
	}
	return 'X';
}

/* ===========================================================
Lógica media
=========================================================== */

void separarNombres(const char *nombre_completo, char *nombre1, char *nombre2) {
	int i = 0, j = 0;
	while (nombre_completo[i] && nombre_completo[i] != ' ') {
		nombre1[i] = nombre_completo[i];
		i++;
	}
	nombre1[i] = '\0';
	if (nombre_completo[i] == ' ') {
		i++;
		while (nombre_completo[i]) {
			nombre2[j++] = nombre_completo[i++];
		}
	}
	nombre2[j] = '\0';
}

int edadActual(struct Registro *r) {
	int edad = CURRENT_YEAR - r->nacimiento.year;
	if (r->nacimiento.month > CURRENT_MONTH ||
		(r->nacimiento.month == CURRENT_MONTH && r->nacimiento.day > CURRENT_DAY)) {
		edad--;
	}
		return edad;
}

const char* codigo_estado(const char* estado) {
	if (!estado) return "NE";
	char nombre[50];
	int i = 0;
	while (estado[i] && i < 49) {
		nombre[i] = (char)toupper((unsigned char)estado[i]);
		i++;
	}
	nombre[i] = '\0';
	
	if (strcmp(nombre, "AGUASCALIENTES") == 0) return "AS";
	if (strcmp(nombre, "BAJA CALIFORNIA") == 0) return "BC";
	if (strcmp(nombre, "BAJA CALIFORNIA SUR") == 0) return "BS";
	if (strcmp(nombre, "CAMPECHE") == 0) return "CC";
	if (strcmp(nombre, "COAHUILA") == 0) return "CL";
	if (strcmp(nombre, "COLIMA") == 0) return "CM";
	if (strcmp(nombre, "CHIAPAS") == 0) return "CS";
	if (strcmp(nombre, "CHIHUAHUA") == 0) return "CH";
	if (strcmp(nombre, "CIUDAD DE MEXICO") == 0 || strcmp(nombre, "CDMX") == 0) return "DF";
	if (strcmp(nombre, "DURANGO") == 0) return "DG";
	if (strcmp(nombre, "GUANAJUATO") == 0) return "GT";
	if (strcmp(nombre, "GUERRERO") == 0) return "GR";
	if (strcmp(nombre, "HIDALGO") == 0) return "HG";
	if (strcmp(nombre, "JALISCO") == 0) return "JC";
	if (strcmp(nombre, "MEXICO") == 0 || strcmp(nombre, "ESTADO DE MEXICO") == 0) return "MC";
	if (strcmp(nombre, "MICHOACAN") == 0) return "MN";
	if (strcmp(nombre, "MORELOS") == 0) return "MS";
	if (strcmp(nombre, "NAYARIT") == 0) return "NT";
	if (strcmp(nombre, "NUEVO LEON") == 0) return "NL";
	if (strcmp(nombre, "OAXACA") == 0) return "OC";
	if (strcmp(nombre, "PUEBLA") == 0) return "PL";
	if (strcmp(nombre, "QUERETARO") == 0) return "QT";
	if (strcmp(nombre, "QUINTANA ROO") == 0) return "QR";
	if (strcmp(nombre, "SAN LUIS POTOSI") == 0) return "SP";
	if (strcmp(nombre, "SINALOA") == 0) return "SL";
	if (strcmp(nombre, "SONORA") == 0) return "SR";
	if (strcmp(nombre, "TABASCO") == 0) return "TC";
	if (strcmp(nombre, "TAMAULIPAS") == 0) return "TS";
	if (strcmp(nombre, "TLAXCALA") == 0) return "TL";
	if (strcmp(nombre, "VERACRUZ") == 0) return "VZ";
	if (strcmp(nombre, "YUCATAN") == 0) return "YN";
	if (strcmp(nombre, "ZACATECAS") == 0) return "ZS";
	if (strcmp(nombre, "NACIDO EN EL EXTRANJERO") == 0 || strcmp(nombre, "EXTRANJERO") == 0) return "NE";
	return "NE";
}

/* ===========================================================
Funciones “grandes”
=========================================================== */

void crearCurp(struct Registro *r) {
	char curp[17];
	curp[0] = (char)toupper((unsigned char)r->apellidos.paterno[0]);
	curp[1] = vocalInterna(r->apellidos.paterno);
	curp[2] = (char)toupper((unsigned char)r->apellidos.materno[0]);
	curp[3] = (char)toupper((unsigned char)r->nombre_completo[0]);
	sprintf(curp + 4, "%02d%02d%02d",
			r->nacimiento.year % 100, r->nacimiento.month, r->nacimiento.day);
	curp[10] = (char)toupper((unsigned char)r->sexo);
	const char *ent = codigo_estado(r->estado_nacimiento);
	curp[11] = ent[0];
	curp[12] = ent[1];
	curp[13] = consInterna(r->apellidos.paterno);
	curp[14] = consInterna(r->apellidos.materno);
	curp[15] = consInterna(r->nombre_completo);
	curp[16] = '\0';
	strcpy(r->clave_curp, curp);
	printf("CURP generado: %s\n", r->clave_curp);
}

void nuevoRegistro(struct Registro *arr, int *num_registros) {
	int idx = *num_registros;
	
	printf("Año de nacimiento: ");
	scanf("%d", &arr[idx].nacimiento.year);
	printf("Mes de nacimiento: ");
	scanf("%d", &arr[idx].nacimiento.month);
	printf("Día de nacimiento: ");
	scanf("%d", &arr[idx].nacimiento.day);
	
	arr[idx].edad = edadActual(&arr[idx]);
	arr[idx].mayor_edad = mayorEdad(arr[idx].edad);
	
	printf("Nombre(s): ");
	getchar(); /* limpiar salto previo de scanf */
	leerTexto(arr[idx].nombre_completo, sizeof(arr[idx].nombre_completo));
	separarNombres(arr[idx].nombre_completo, arr[idx].nombre1, arr[idx].nombre2);
	
	printf("Apellido paterno: ");
	leerTexto(arr[idx].apellidos.paterno, sizeof(arr[idx].apellidos.paterno));
	printf("Apellido materno: ");
	leerTexto(arr[idx].apellidos.materno, sizeof(arr[idx].apellidos.materno));
	
	printf("Estado de nacimiento: ");
	scanf("%49s", arr[idx].estado_nacimiento);
	
	printf("Sexo (M/F): ");
	scanf(" %c", &arr[idx].sexo);
	
	printf("Correo electrónico: ");
	scanf("%49s", arr[idx].correo);
	printf("Teléfono: ");
	scanf("%14s", arr[idx].celular);
	
	printf("Calle: ");
	scanf("%49s", arr[idx].domicilio.calle);
	printf("Colonia: ");
	scanf("%49s", arr[idx].domicilio.colonia);
	printf("Número: ");
	scanf("%9s", arr[idx].domicilio.numero);
	printf("Código postal: ");
	scanf("%d", &arr[idx].domicilio.codigo_postal);
	
	arr[idx].dependientes = 0;
	
	crearCurp(&arr[idx]);
}

/* ===========================================================
main
=========================================================== */

int main(void) {
	SetConsoleOutputCP(CP_UTF8);
	int total = 0;
	struct Registro *personas = NULL;
	
	while (1) {
		printf("Agregar registro (1) o salir (0): ");
		int op;
		if (scanf("%d", &op) != 1) break;
		if (op == 0) break;
		
		personas = realloc(personas, sizeof(struct Registro) * (total + 1));
		if (!personas) {
			printf("Error de memoria\n");
			return 1;
		}
		nuevoRegistro(personas, &total);
		total++;
	}
	
	free(personas);
	return 0;
}

