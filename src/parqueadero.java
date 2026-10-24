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
        imprimir("PARQUEADERO");
        imprimir("1. Registrar entrada");
        imprimir("2. Registrar salida");
        imprimir("3. Reporte de ocupación");
        imprimir("4. Reporte financiero");
        imprimir("5. Salir");
    }

    static void imprimir(String mensaje) {
        System.out.println(mensaje);
    }

    static final int CAPACIDAD_TOTAL = 10;
    static final long TARIFA_HORA_CARRO = 4000;
    static final long TARIFA_DIA_CARRO = 30000;
    static final long TARIFA_HORA_MOTO = 2000;
    static final long TARIFA_DIA_MOTO = 15000;
    static final int UMBRAL_HORAS_DIA = 8;
    static final int HORA_APERTURA = 8;
    static final int HORA_CIERRE = 22;

    static final String CARRO = "Carro";
    static final String MOTO = "Moto";

    static final DateTimeFormatter FORMATO_FECHA_HORA = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");


    static String[] tiposVehiculo = new String[CAPACIDAD_TOTAL];
    static LocalDateTime[] fechasIngreso = new LocalDateTime[CAPACIDAD_TOTAL];


    static int celdasOcupadas;
    static int celdasLibres;
    static long recaudoCarros;
    static long recaudoMotos;
    static void inicializarArreglos() {
        for (int i = 0; i < CAPACIDAD_TOTAL; i++) {
            tiposVehiculo[i] = null;
            fechasIngreso[i] = null;
        }
        celdasOcupadas = 0;
        celdasLibres = CAPACIDAD_TOTAL;
        recaudoCarros = 0;
        recaudoMotos = 0;
    }

    static void registrarEntrada() {
        int indice = buscarCeldaDisponible();
        if (indice == -1) {
            imprimir("El parqueadero está lleno. No es posible registrar el ingreso.");
            return;
        }

        int tipo = leerEnteroValidado("Tipo de vehículo (1. Carro, 2. Moto): ", 1, 2);
        LocalDateTime ingreso = leerFechaHora("Fecha y hora de ingreso (yyyy-MM-dd HH:mm): ");

        tiposVehiculo[indice] = (tipo == 1) ? CARRO : MOTO;
        fechasIngreso[indice] = ingreso;
        celdasOcupadas++;
        celdasLibres--;

        imprimir("Ingreso registrado. " + tiposVehiculo[indice] + " asignado a la celda " + (indice + 1) + ".");
    }

    static int buscarCeldaDisponible() {
        for (int i = 0; i < CAPACIDAD_TOTAL; i++) {
            if (tiposVehiculo[i] == null) {
                return i;
            }
        }
        return -1;
    }
}