import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
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
                case 2:
                    registrarSalida();
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

    static void registrarSalida() {
        if (celdasOcupadas == 0) {
            imprimir("No hay vehículos en el parqueadero.");
            return;
        }

        int indice = leerCeldaOcupada();
        LocalDateTime ingreso = fechasIngreso[indice];
        LocalDateTime salida = leerFechaHoraSalida(ingreso);

        long total = liquidar(indice, ingreso, salida);

        if (tiposVehiculo[indice].equals(CARRO)) {
            recaudoCarros += total;
        } else {
            recaudoMotos += total;
        }

        tiposVehiculo[indice] = null;
        fechasIngreso[indice] = null;
        celdasOcupadas--;
        celdasLibres++;
    }

    static long liquidar(int indice, LocalDateTime ingreso, LocalDateTime salida) {
        boolean esCarro = tiposVehiculo[indice].equals(CARRO);
        long tarifaDia = esCarro ? TARIFA_DIA_CARRO : TARIFA_DIA_MOTO;
        long tarifaHora = esCarro ? TARIFA_HORA_CARRO : TARIFA_HORA_MOTO;

        long diasACobrar = contarDiasCompletos(ingreso, salida);
        LocalDateTime cursor = ingreso.plusDays(diasACobrar);
        long horasACobrar = calcularHorasRestantes(cursor, salida);

        if (horasACobrar >= UMBRAL_HORAS_DIA) {
            diasACobrar++;
            horasACobrar = 0;
        }

        if (diasACobrar == 0 && horasACobrar == 0) {
            horasACobrar = 1;
        }

        long subtotalDias = diasACobrar * tarifaDia;
        long subtotalHoras = horasACobrar * tarifaHora;
        long total = subtotalDias + subtotalHoras;

        imprimir("");
        imprimir("========== FACTURA ==========");
        imprimir("Celda liberada: " + (indice + 1));
        imprimir("Tipo de vehículo: " + tiposVehiculo[indice]);
        imprimir("Ingreso: " + ingreso.format(FORMATO_FECHA_HORA));
        imprimir("Salida:  " + salida.format(FORMATO_FECHA_HORA));
        if (diasACobrar > 0) {
            imprimir(diasACobrar + " día(s) x " + formatearMoneda(tarifaDia) + " = " + formatearMoneda(subtotalDias));
        }
        if (horasACobrar > 0) {
            imprimir(horasACobrar + " hora(s) x " + formatearMoneda(tarifaHora) + " = " + formatearMoneda(subtotalHoras));
        }
        imprimir("TOTAL A PAGAR: " + formatearMoneda(total));
        imprimir("=============================");

        return total;
    }

    static long contarDiasCompletos(LocalDateTime ingreso, LocalDateTime salida) {
        return ChronoUnit.DAYS.between(ingreso, salida);
    }

    static long calcularHorasRestantes(LocalDateTime cursor, LocalDateTime salida) {
        long minutosRestantes = Duration.between(cursor, salida).toMinutes();
        return (minutosRestantes + 59) / 60;
    }

    static String formatearMoneda(long valor) {
        return String.format("$%,d", valor);
    }

    static int leerCeldaOcupada() {
        while (true) {
            int celda = leerEnteroValidado("Número de celda (1-" + CAPACIDAD_TOTAL + "): ", 1, CAPACIDAD_TOTAL);
            if (tiposVehiculo[celda - 1] != null) {
                return celda - 1;
            }
            imprimir("La celda " + celda + " está libre. Indique una celda ocupada.");
        }
    }

    static LocalDateTime leerFechaHoraSalida(LocalDateTime ingreso) {
        while (true) {
            LocalDateTime salida = leerFechaHora("Fecha y hora de salida (yyyy-MM-dd HH:mm): ");
            if (!salida.isBefore(ingreso)) {
                return salida;
            }
            imprimir("La salida no puede ser anterior al ingreso (" + ingreso.format(FORMATO_FECHA_HORA) + ").");
        }
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