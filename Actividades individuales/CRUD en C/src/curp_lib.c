#include "curp_lib.h"

void asignar_id_entidad(struct Ciudadanos *ciudadano) {
    if (!ciudadano || !ciudadano->entidad) {
        strcpy(ciudadano->id_entidad, "NE");
        return;
    }
    char nombre[50];
    int i = 0;
    while (ciudadano->entidad[i] && i < 49) {
        nombre[i] = (unsigned char)ciudadano->entidad[i];
        i++;
    }
    nombre[i] = '\0';

    const char *id = "NE";
    if (strcmp(nombre, "AGUASCALIENTES") == 0) id = "AS";
    else if (strcmp(nombre, "BAJA CALIFORNIA") == 0) id = "BC";
    else if (strcmp(nombre, "BAJA CALIFORNIA SUR") == 0) id = "BS";
    else if (strcmp(nombre, "CAMPECHE") == 0) id = "CC";
    else if (strcmp(nombre, "COAHUILA") == 0) id = "CL";
    else if (strcmp(nombre, "COLIMA") == 0) id = "CM";
    else if (strcmp(nombre, "CHIAPAS") == 0) id = "CS";
    else if (strcmp(nombre, "CHIHUAHUA") == 0) id = "CH";
    else if (strcmp(nombre, "CIUDAD DE MEXICO") == 0 || strcmp(nombre, "CDMX") == 0) id = "DF";
    else if (strcmp(nombre, "DURANGO") == 0) id = "DG";
    else if (strcmp(nombre, "GUANAJUATO") == 0) id = "GT";
    else if (strcmp(nombre, "GUERRERO") == 0) id = "GR";
    else if (strcmp(nombre, "HIDALGO") == 0) id = "HG";
    else if (strcmp(nombre, "JALISCO") == 0) id = "JC";
    else if (strcmp(nombre, "MEXICO") == 0 || strcmp(nombre, "ESTADO DE MEXICO") == 0) id = "MC";
    else if (strcmp(nombre, "MICHOACAN") == 0) id = "MN";
    else if (strcmp(nombre, "MORELOS") == 0) id = "MS";
    else if (strcmp(nombre, "NAYARIT") == 0) id = "NT";
    else if (strcmp(nombre, "NUEVO LEON") == 0) id = "NL";
    else if (strcmp(nombre, "OAXACA") == 0) id = "OC";
    else if (strcmp(nombre, "PUEBLA") == 0) id = "PL";
    else if (strcmp(nombre, "QUERETARO") == 0) id = "QT";
    else if (strcmp(nombre, "QUINTANA ROO") == 0) id = "QR";
    else if (strcmp(nombre, "SAN LUIS POTOSI") == 0) id = "SP";
    else if (strcmp(nombre, "SINALOA") == 0) id = "SL";
    else if (strcmp(nombre, "SONORA") == 0) id = "SR";
    else if (strcmp(nombre, "TABASCO") == 0) id = "TC";
    else if (strcmp(nombre, "TAMAULIPAS") == 0) id = "TS";
    else if (strcmp(nombre, "TLAXCALA") == 0) id = "TL";
    else if (strcmp(nombre, "VERACRUZ") == 0) id = "VZ";
    else if (strcmp(nombre, "YUCATAN") == 0) id = "YN";
    else if (strcmp(nombre, "ZACATECAS") == 0) id = "ZS";
    else if (strcmp(nombre, "NACIDO EN EL EXTRANJERO") == 0 || strcmp(nombre, "EXTRANJERO") == 0) id = "NE";

    strncpy(ciudadano->id_entidad, id, 3);
    ciudadano->id_entidad[2] = '\0';
}

void asignar_colonia(struct Ciudadanos *ciudadano) {
    if(!ciudadano) return;

    int codigo_postal = ciudadano->direccion.codigo_postal;
    const char *colonia = "Desconocida";

    // Base de datos de códigos postales y colonias
    static const CPColonia cp_colonias[] = {
        {97000, {"Mérida Centro", "Itzaes", "Madrid", "Villa Fontana", "La Quinta", "Los Cocos", "Privada Del Maestro", "Jardines de San Sebastian"}, 8},
        {97003, {"Los Reyes"}, 1},
        {97050, {"Alcalá Martín", "Yucatán"}, 2},
        {97059, {"Señorial"}, 1},
        {97060, {"Carrillo Ancona"}, 1},
        {97069, {"Inalámbrica"}, 1},
        {97070, {"Dolores Patron", "García Gineres", "El Pedregal"}, 3},
        {97080, {"La Huerta"}, 1},
        {97088, {"Santa Cecilia"}, 1},
        {97089, {"Cupules"}, 1},
        {97098, {"Lourdes"}, 1},
        {97099, {"Waspa"}, 1},
        {97100, {"Itzimna", "Itzimna", "Itzimna 2", "Rinconada Itzmina"}, 4},
        {97107, {"Manola"}, 1},
        {97108, {"Las Arboledas"}, 1},
        {97109, {"Ferrocarrileros", "Jesús Carranza"}, 2},
        {97110, {"Revolución (Cordemex)"}, 1},
        {97113, {"Montebello", "San Antonio", "Xaman-Tan"}, 3},
        {97114, {"Monte Alban", "Residencial Sol Campestre"}, 2},
        {97115, {"Sodzil Norte", "Montes de Ame", "Gonzalo Guerrero", "Residencial Montejo Norte", "Ampliación Revolución", "Residencial San Angelo"}, 6},
        {97116, {"San Antonio Cucul", "Privada San Antonio Cucul"}, 2},
        {97117, {"San Ramon Norte", "San Ramon Sur", "San Ramon Norte I", "Villareal", "Xaman-Kab"}, 5},
        {97118, {"Plan de Ayala", "Villas Del Sol", "Ampliación Plan de Ayala (Villas del Sol)"}, 3},
        {97119, {"Benito Juárez Nte", "Villas La Hacienda", "Gonzalo Guerrero", "Villas del Rey"}, 4},
        {97120, {"Campestre", "Del Norte", "Tecnológico", "Ampliación del Norte (1a. Ampliación)"}, 4},
        {97125, {"México", "Privada Nuevo México"}, 2},
        {97127, {"Buenavista", "Montejo"}, 2},
        {97128, {"México Norte", "Privada Mediterráneo", "Residencial Colonia México"}, 3},
        {97129, {"Emiliano Zapata Nte"}, 1},
        {97130, {"Torremolinos", "Díaz Ordaz", "San Carlos", "Vista Alegre", "Residencial Palmerales de Altabrisa", "Missan II", "Residencial Altabrisa", "Montecarlo", "Vista Alegre", "Vista Alegre Norte", "Altabrisa", "San Remo", "Santa Rita Cholul"}, 13},
        {97133, {"Montecristo", "Montevideo", "Residencial Camara de Comercio Norte", "Monterreal"}, 4},
        {97134, {"Maya", "Paraíso Maya", "José María Iturralde"}, 3},
        {97135, {"Jardines de Mérida"}, 1},
        {97136, {"Felipe Carrillo Puerto Nte"}, 1},
        {97137, {"México Oriente"}, 1},
        {97138, {"Jardines del Noreste", "Los Álamos", "Residencial Del Arco", "La Florida", "Los Pinos", "Jardines Del Norte", "Jardines de Vista Alegre", "Residencial Bancarios", "San Pedro Cholul", "Santa Maria", "El Arco", "Jardines de Vista Alegre II", "Vista Alegre Lotificacion", "Pinos Norte II"}, 14},
        {97139, {"Prado Norte", "San Antonio Cinta", "Jardines del Norte de Prado Norte"}, 3},
        {97140, {"López Mateos", "San Luis", "San Miguel"}, 3},
        {97142, {"Unidad Habitacional CTM", "Antonia Jiménez Trava", "Antonia Jiménez Trava II", "San Vicente Oriente (La Isla)"}, 4},
        {97143, {"Polígono 108", "Vicente Guerrero", "Boulevares de Oriente", "Itzimna 108", "Luis Donaldo Colosio", "Leandro Valle", "Brisas Del Bosque"}, 7},
        {97144, {"Emiliano Zapata Ote", "Las Brisas", "Las Brisas Del Norte", "Ampliación las Brisas"}, 4},
        {97145, {"Las Palmas", "Pet-kanche", "San Juan Grande", "Noria II"}, 4},
        {97146, {"Nueva Alemán", "Las Flores"}, 2},
        {97147, {"Nuevo Yucatán", "San Nicolás"}, 2},
        {97148, {"Miguel Alemán"}, 1},
        {97149, {"San Esteban"}, 1},
        {97150, {"Industrial", "Trava Quintero"}, 2},
        {97155, {"Fenix", "Lourdes Industrial"}, 2},
        {97156, {"Los Reyes"}, 1},
        {97157, {"Lázaro Cárdenas Ote", "Nueva Mayapan"}, 2},
        {97158, {"Chuminopolis"}, 1},
        {97159, {"Máximo Ancona", "Manuel Ávila Camacho", "Mayapan", "Nueva Pacabtun", "Nueva Mayapan", "Lotificacion las Brisas"}, 6},
        {97160, {"Del Parque", "Pacabtun", "Manuel Ávila Camacho", "Privada Del Autotransporte CTM"}, 4},
        {97165, {"Melchor Ocampo", "Melchor Ocampo II"}, 2},
        {97166, {"Fidel Velázquez", "Salvador Alvarado Oriente", "Fidel Velázquez 2a Etapa"}, 3},
        {97167, {"Emilio Portes Gil", "Bosques de Oriente", "Privada Emilio Portes Gil"}, 3},
        {97168, {"Del Carmen", "Cortés Sarmiento", "Jardines de Miraflores", "Cerillera"}, 4},
        {97169, {"Esperanza", "Wallis"}, 2},
        {97170, {"Chichen-itza", "Nueva Chichen-itza"}, 2},
        {97173, {"Vergel", "Vergel II", "Vergel III", "Vergel IV", "San José Vergel", "Real San José", "Misne III"}, 7},
        {97174, {"Villas La Macarena", "Morelos Oriente"}, 2},
        {97175, {"Amalia Solorzano"}, 1},
        {97176, {"Misné II", "San Pablo Oriente", "Vergel 65", "San Antonio Kaua", "El Vergel"}, 5},
        {97177, {"Azcorra"}, 1},
        {97178, {"Benito Juárez Ote"}, 1},
        {97179, {"Miraflores", "Privada Miraflores"}, 2},
        {97180, {"Vicente Solís"}, 1},
        {97189, {"Canto", "San José"}, 2},
        {97190, {"Morelos", "Morelos Issste Fovissste"}, 2},
        {97195, {"Nueva Kukulkan", "San Antonio Kaua", "San Antonio Kaua II", "Miraflores II", "San Antonio Kaua I", "Aquaparque"}, 6},
        {97196, {"Salvador Alvarado Sur", "Militar", "Salvador Alvarado Sur II", "Ampliación Salvador Alvarado Sur"}, 4},
        {97198, {"Ampliación Granjas", "Reparto Granjas", "Kukulcan"}, 3},
        {97199, {"Maria Luisa"}, 1}
    };

    int num_cps = sizeof(cp_colonias) / sizeof(CPColonia);
    
    // Buscar el código postal
    for (int i = 0; i < num_cps; i++) {
        if (cp_colonias[i].cp == codigo_postal) {
            if (cp_colonias[i].num_colonias == 1) {
                colonia = cp_colonias[i].colonias[0];
            } else {
                colonia = seleccionar_colonia_menu(&cp_colonias[i]);
            }
            break;
        }
    }

    // Asignar la colonia seleccionada
    strncpy(ciudadano->direccion.colonia, colonia, sizeof(ciudadano->direccion.colonia) - 1);
    ciudadano->direccion.colonia[sizeof(ciudadano->direccion.colonia) - 1] = '\0';
    printf("[DEBUG]: Colonia: %s\n", ciudadano->direccion.colonia);
}

char primera_vocal_interna(struct Ciudadanos *ciudadano, int state) {
    const char *str;
    if (state == PATERNO) str = ciudadano->apellidos.paterno;
    
    for (int i = 1; str[i] != '\0'; i++) {
        char c = toupper(str[i]);
        if (c == 'A' || c == 'E' || c == 'I' || c == 'O' || c == 'U') {
            return c;
        }
    }
    return 'X';
}

char primera_consonante_interna(struct Ciudadanos *ciudadano, int state) {
    const char *str;
    if (state == PATERNO) str = ciudadano->apellidos.paterno;
    else if (state == MATERNO) str = ciudadano->apellidos.materno;
    else if (state == NOMBRE) str = ciudadano->primer_nombre;

    for (int i = 1; str[i] != '\0'; i++) {
        // Verificar si es Ñ en diferentes codificaciones y saltarla
        if (((unsigned char)str[i] == 0xC3 && 
             i + 1 < strlen(str) && 
             (unsigned char)str[i+1] == 0x91) ||  // UTF-8 Ñ
            (unsigned char)str[i] == 0xD1 ||       // ISO-8859-1 Ñ
            (unsigned char)str[i] == 0xA5) {       // CP437 Ñ
            // Saltar la Ñ ya que no se considera para el CURP
            if ((unsigned char)str[i] == 0xC3) i++; // Para UTF-8, saltar byte adicional
            continue;
        }
        
        char c = toupper(str[i]);
        // Verificar que sea una letra y que no sea vocal
        if (isalpha(c) && !(c == 'A' || c == 'E' || c == 'I' || c == 'O' || c == 'U')) {
            return c;
        }
    }
    return 'X';
}

const char* seleccionar_colonia_menu(const CPColonia *cp_data) {
    while (true) {
        int opc;
        printf("Seleccione una colonia:\n");
        for (int i = 0; i < cp_data->num_colonias; i++) {
            printf("%d. %s\n", i + 1, cp_data->colonias[i]);
        }
        printf("Opción: ");
        
        if (scanf("%d", &opc) != 1 || opc < 1 || opc > cp_data->num_colonias) {
            printf("Opción inválida. Intente de nuevo.\n");
            while(getchar() != '\n'); // Limpiar el buffer
            continue;
        }
        return cp_data->colonias[opc - 1];
    }
}

void generar_CURP_parcial(struct Ciudadanos *ciudadano) {
    char curp_parcial[17];
    // 1-4: Primera letra apellido paterno + primera vocal interna apellido paterno + primera letra apellido materno + primera letra nombre
    curp_parcial[0] = ciudadano->apellidos.paterno[0];
    curp_parcial[1] = primera_vocal_interna(ciudadano, PATERNO);
    curp_parcial[2] = ciudadano->apellidos.materno[0];
    curp_parcial[3] = ciudadano->primer_nombre[0];
    // 5-10: fecha YYMMDD
    sprintf(curp_parcial + 4, "%02d%02d%02d", ciudadano->fecha_nacimiento.year % 100,
            ciudadano->fecha_nacimiento.month, ciudadano->fecha_nacimiento.day);
    // 11: sexo
    curp_parcial[10] = ciudadano->genero;
    // 12-13: entidad
    curp_parcial[11] = ciudadano->id_entidad[0];
    curp_parcial[12] = ciudadano->id_entidad[1];
    // 14-16: consonantes internas
    curp_parcial[13] = primera_consonante_interna(ciudadano, PATERNO);
    curp_parcial[14] = primera_consonante_interna(ciudadano, MATERNO);
    curp_parcial[15] = primera_consonante_interna(ciudadano, NOMBRE);

    curp_parcial[16] = '\0'; // Terminar la cadena

    strcpy(ciudadano->curp_parcial, curp_parcial);
    printf("CURP parcial generado: %s\n", ciudadano->curp_parcial);
}

void generar_CURP(struct Ciudadanos *ciudadano) {
    char curp[19];
    
    sprintf(curp, "%s", ciudadano->curp_parcial);
    curp[16] = ciudadano->homoclave[0];
    curp[17] = ciudadano->homoclave[1];
    curp[18] = '\0'; // Terminar la cadena
    
    // Copiar el CURP completo al campo del ciudadano
    strcpy(ciudadano->curp, curp);
}