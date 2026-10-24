import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Scanner;

public class Parqueadero {

    static final Scanner SCANNER = new Scanner(System.in);

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

    static final DateTimeFormatter FORMATO_FECHA_HORA =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    static String[] tiposVehiculo = new String[CAPACIDAD_TOTAL];
    static LocalDateTime[] fechasIngreso = new LocalDateTime[CAPACIDAD_TOTAL];

    static int celdasOcupadas;
    static int celdasLibres;
    static long recaudoCarros;
    static long recaudoMotos;

    public static void main(String[] args) {
        inicializarArreglos();

        int opcion;
        do {
            mostrarMenu();
            opcion = leerEnteroValidado("Seleccione una opción: ", 1, 5);

            switch (opcion) {
                case 1:
                    registrarEntrada();
                    break;
                case 2:
                    imprimir("Registrar salida: pendiente de implementar.");
                    break;
                case 3:
                    imprimir("Reporte de ocupación: pendiente de implementar.");
                    break;
                case 4:
                    imprimir("Reporte financiero: pendiente de implementar.");
                    break;
                case 5:
                    imprimir("Hasta pronto.");
                    break;
            }
        } while (opcion != 5);

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

        LocalDateTime ingreso;
        while (true) {
            ingreso = leerFechaHora("Fecha y hora de ingreso (yyyy-MM-dd HH:mm): ");
            int hora = ingreso.getHour();
            if (hora >= HORA_APERTURA && hora < HORA_CIERRE) {
                break;
            }
            imprimir("El parqueadero atiende de " + HORA_APERTURA + ":00 a "
                    + HORA_CIERRE + ":00. Ingrese una hora dentro del horario.");
        }

        tiposVehiculo[indice] = (tipo == 1) ? CARRO : MOTO;
        fechasIngreso[indice] = ingreso;
        celdasOcupadas++;
        celdasLibres--;

        imprimir("Ingreso registrado. " + tiposVehiculo[indice]
                + " asignado a la celda " + (indice + 1) + ".");
    }

    static int buscarCeldaDisponible() {
        for (int i = 0; i < CAPACIDAD_TOTAL; i++) {
            if (tiposVehiculo[i] == null) {
                return i;
            }
        }
        return -1;
    }

    static int leerEnteroValidado(String mensaje, int min, int max) {
        while (true) {
            System.out.print(mensaje);
            String linea = SCANNER.nextLine().trim();
            try {
                int valor = Integer.parseInt(linea);
                if (valor >= min && valor <= max) {
                    return valor;
                }
                imprimir("Ingrese un número entre " + min + " y " + max + ".");
            } catch (NumberFormatException e) {
                imprimir("Entrada inválida. Ingrese un número entero.");
            }
        }
    }

    static LocalDateTime leerFechaHora(String mensaje) {
        while (true) {
            System.out.print(mensaje);
            String linea = SCANNER.nextLine().trim();
            try {
                return LocalDateTime.parse(linea, FORMATO_FECHA_HORA);
            } catch (DateTimeParseException e) {
                imprimir("Formato inválido. Use yyyy-MM-dd HH:mm (ejemplo: 2026-10-24 09:30).");
            }
        }
    }
}