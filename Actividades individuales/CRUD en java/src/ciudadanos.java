// ---- Modelos  ----

class Domicilio {
    String calle;
    String colonia;
    String numero;
    int codigo_postal;
}

class NombresAp {
    String paterno;
    String materno;
}

class FechaNac {
    int year;
    int month;
    int day;
}

class Registro {
    String nombre_completo;
    String nombre1;
    String nombre2;
    NombresAp apellidos = new NombresAp();
    FechaNac nacimiento = new FechaNac();
    int edad;
    Domicilio domicilio = new Domicilio();
    String estado_nacimiento;
    char sexo;              // 'M' o 'F'
    String clave_curp;
    String correo;
    String celular;
    boolean mayor_edad;
    int dependientes;
}
