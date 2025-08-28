/* ---- Modelos ---- */
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
