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

        boolean salir = false;
        while (!salir) {
            mostrarMenu();
            int opcion = leerEnteroValidado("Seleccione una opción: ", 1, 5);
            switch (opcion) {
                case 1:
                    registrarEntrada();
                    break;
                case 5:
                    salir = true;
                    break;
                default:
                    imprimir("Opción aún no disponible.");
                    break;
            }
        }

        imprimir("Gracias por usar el sistema. ¡Hasta pronto!");
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
            if (ingreso.getHour() < HORA_CIERRE) {
                break;
            }
            imprimir("No se permiten ingresos a la hora de cierre. Ingrese antes de las "
                    + HORA_CIERRE + ":00.");
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

    static int leerEnteroValidado(String mensaje, int minimo, int maximo) {
        while (true) {
            System.out.print(mensaje);
            String texto = SCANNER.nextLine().trim();
            if (enteroEnRango(texto, minimo, maximo)) {
                return Integer.parseInt(texto);
            }
            imprimir("Dato inválido. Ingrese un número entre " + minimo + " y " + maximo + ".");
        }
    }

    static boolean enteroEnRango(String texto, int minimo, int maximo) {
        if (texto.isEmpty() || texto.length() > 9) {
            return false;
        }
        int valor = leerDigitos(texto, 0, texto.length());
        return valor >= minimo && valor <= maximo;
    }

    static LocalDateTime leerFechaHora(String mensaje) {
        while (true) {
            System.out.print(mensaje);
            String texto = SCANNER.nextLine().trim();
            if (!formatoFechaHoraValido(texto)) {
                imprimir("Fecha u hora inválidas. Use yyyy-MM-dd HH:mm y el horario 08:00-22:00.");
                continue;
            }

            try {
                LocalDateTime fecha = LocalDateTime.parse(texto, FORMATO_FECHA_HORA);
                if (!fecha.format(FORMATO_FECHA_HORA).equals(texto)) {
                    imprimir("La fecha no existe en el calendario.");
                    continue;
                }
                return fecha;
            } catch (DateTimeParseException e) {
                imprimir("La fecha no existe en el calendario.");
            }
        }
    }

    static boolean formatoFechaHoraValido(String texto) {
        if (texto.length() != 16
                || texto.charAt(4) != '-'
                || texto.charAt(7) != '-'
                || texto.charAt(10) != ' '
                || texto.charAt(13) != ':') {
            return false;
        }

        int anio = leerDigitos(texto, 0, 4);
        int mes = leerDigitos(texto, 5, 2);
        int dia = leerDigitos(texto, 8, 2);
        int hora = leerDigitos(texto, 11, 2);
        int minuto = leerDigitos(texto, 14, 2);

        return anio >= 1 && mes >= 1 && mes <= 12 && dia >= 1 && dia <= 31
                && hora >= HORA_APERTURA && hora <= HORA_CIERRE
                && (hora != HORA_CIERRE || minuto <= 0)
                && minuto >= 0 && minuto <= 59;
    }

    static int leerDigitos(String texto, int inicio, int cantidad) {
        int valor = 0;
        for (int i = inicio; i < inicio + cantidad; i++) {
            char caracter = texto.charAt(i);
            if (caracter < '0' || caracter > '9') {
                return -1;
            }
            valor = valor * 10 + caracter - '0';
        }
        return valor;
    }
}