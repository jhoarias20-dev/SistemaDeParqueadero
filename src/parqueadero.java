import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import java.util.Scanner;

public class Parqueadero {

    // Único Scanner del programa
    static final Scanner SCANNER = new Scanner(System.in);

    public static void main(String[] args) {
        mostrarMenu();
        SCANNER.close();
    }

    static void mostrarMenu() {
        imprimir("");
        imprimir("===== PARQUEADERO =====");
        imprimir("1. Registrar entrada");
        imprimir("2. Registrar salida");
        imprimir("3. Reporte de ocupación");
        imprimir("4. Reporte financiero");
        imprimir("5. Salir");
    }

    static void imprimir(String mensaje) {
        System.out.println(mensaje);
    }
}