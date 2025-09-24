package controller;
import java.io.*;
import java.nio.file.*;
import java.util.stream.Stream;

public class FileManager {

    private final Path filePath;

    public FileManager(String fileName) {
        this.filePath = Paths.get(fileName);
        crearDirectorio();
    }

    private void crearDirectorio() {
        try {
            Path parent = filePath.getParent();
            if (parent != null && !Files.exists(parent)) {
                Files.createDirectories(parent);
            }
        } catch (IOException e) {
            throw new RuntimeException("No se pudo crear el directorio", e);
        }
    }

    public boolean ciudadanoExiste(String curp) throws IOException {
        // Lógica para verificar si el ciudadano ya existe en el archivo
        // Si existe, lanzar una excepción o manejar el caso según sea necesario
        // Si no existe, agregar el nuevo ciudadano al archivo
        if (!Files.exists(filePath)) return false;
        try (Stream<String> lines = Files.lines(filePath)) {
            return lines.anyMatch(line -> line.equalsIgnoreCase(curp));
        } catch (IOException e) {
            throw new IOException("Error al leer el archivo", e);
        }
    }

    public void guardarCiudadano(String curp) throws IOException {
        // Lógica para guardar los datos del ciudadano en el archivo
        Files.writeString(
            filePath,
            curp + System.lineSeparator(),
            StandardOpenOption.CREATE,
            StandardOpenOption.APPEND
        );
    }
}
