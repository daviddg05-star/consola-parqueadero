package app;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Scanner;

public class Main {

    // Constantes del sistema
    private static final Scanner SCANNER = new Scanner(System.in);
    private static final DateTimeFormatter FORMATO_FECHA_HORA = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    private static final int CAPACIDAD_TOTAL = 10;
    private static final double TARIFA_HORA_CARRO = 4000;
    private static final double TARIFA_DIA_CARRO = 30000;
    private static final double TARIFA_HORA_MOTO = 2000;
    private static final double TARIFA_DIA_MOTO = 15000;
    private static final int UMBRAL_HORAS_DIA = 8;
    private static final int HORA_APERTURA = 8;
    private static final int HORA_CIERRE = 22;

    // Arreglos paralelos para las celdas
    private static final String[] tiposVehiculo = new String[CAPACIDAD_TOTAL];
    private static final LocalDateTime[] fechasIngreso = new LocalDateTime[CAPACIDAD_TOTAL];

    // Variables de recaudo acumulado
    private static double recaudoCarros = 0;
    private static double recaudoMotos = 0;

    public static void main(String[] args) {
        boolean salir = false;

        while (!salir) {
            mostrarMensaje("\n============================================");
            mostrarMensaje("     SISTEMA DE GESTIÓN DE PARQUEADERO     ");
            mostrarMensaje("============================================");
            mostrarMensaje("1. Registrar ingreso de vehículo");
            mostrarMensaje("2. Registrar salida y liquidar estadía");
            mostrarMensaje("3. Reporte de ocupación");
            mostrarMensaje("4. Reporte financiero");
            mostrarMensaje("5. Salir");

            int opcion = leerEnteroEnRango("Seleccione una opción (1-5): ", 1, 5);

            switch (opcion) {
                case 1:
                    registrarEntrada();
                    break;
                case 2:
                    registrarSalida();
                    break;
                case 3:
                    mostrarReporteOcupacion();
                    break;
                case 4:
                    mostrarReporteFinanciero();
                    break;
                case 5:
                    salir = true;
                    mostrarMensaje("\n¡Gracias por utilizar el sistema!");
                    break;
            }
        }

        SCANNER.close();
    }

    // ==========================================
    // MÉTODOS OBLIGATORIOS Y LÓGICA PRINCIPAL
    // ==========================================

    private static void registrarEntrada() {
        mostrarMensaje("\n--- REGISTRO DE INGRESO ---");

        int indiceLibre = buscarCeldaDisponible();
        if (indiceLibre == -1) {
            mostrarMensaje("Error: El parqueadero está lleno. No hay celdas disponibles.");
            return;
        }

        mostrarMensaje("Tipo de vehículo:");
        mostrarMensaje("1. Carro");
        mostrarMensaje("2. Moto");
        int opcionTipo = leerEnteroEnRango("Seleccione el tipo (1-2): ", 1, 2);

        String tipoVehiculo = (opcionTipo == 1) ? "Carro" : "Moto";
        LocalDateTime fechaIngreso = leerFechaHora("Ingrese fecha y hora de ingreso (yyyy-MM-dd HH:mm): ");

        tiposVehiculo[indiceLibre] = tipoVehiculo;
        fechasIngreso[indiceLibre] = fechaIngreso;

        mostrarMensaje("-> Vehículo registrado con éxito en la Celda " + (indiceLibre + 1));
    }

    private static void registrarSalida() {
        mostrarMensaje("\n--- REGISTRO DE SALIDA ---");

        int numCelda = leerEnteroEnRango("Ingrese el número de celda (1-" + CAPACIDAD_TOTAL + "): ", 1, CAPACIDAD_TOTAL);
        int indice = numCelda - 1;

        if (tiposVehiculo[indice] == null) {
            mostrarMensaje("Error: La celda " + numCelda + " se encuentra vacía.");
            return;
        }

        LocalDateTime fechaIngreso = fechasIngreso[indice];
        String tipoVehiculo = tiposVehiculo[indice];

        mostrarMensaje("Celda " + numCelda + " | Vehículo: " + tipoVehiculo + " | Ingreso: " + fechaIngreso.format(FORMATO_FECHA_HORA));

        LocalDateTime fechaSalida;
        while (true) {
            fechaSalida = leerFechaHora("Ingrese fecha y hora de salida (yyyy-MM-dd HH:mm): ");
            if (fechaSalida.isBefore(fechaIngreso)) {
                mostrarMensaje("Error: La fecha y hora de salida no puede ser anterior a la de ingreso. Reintente.");
            } else {
                break;
            }
        }

        liquidarEstadia(numCelda, tipoVehiculo, fechaIngreso, fechaSalida);

        // Liberar celda
        tiposVehiculo[indice] = null;
        fechasIngreso[indice] = null;
    }

    private static int buscarCeldaDisponible() {
        for (int i = 0; i < CAPACIDAD_TOTAL; i++) {
            if (tiposVehiculo[i] == null) {
                return i;
            }
        }
        return -1;
    }

    private static void liquidarEstadia(int celda, String tipoVehiculo, LocalDateTime ingreso, LocalDateTime salida) {
        long diasACobrar = contarDiasCompletos(ingreso, salida);
        LocalDateTime cursor = ingreso.plusDays(diasACobrar);
        long horasACobrar = calcularHorasRestantes(cursor, salida);

        if (horasACobrar >= UMBRAL_HORAS_DIA) {
            diasACobrar++;
            horasACobrar = 0;
        }

        double tarifaDia = tipoVehiculo.equalsIgnoreCase("Carro") ? TARIFA_DIA_CARRO : TARIFA_DIA_MOTO;
        double tarifaHora = tipoVehiculo.equalsIgnoreCase("Carro") ? TARIFA_HORA_CARRO : TARIFA_HORA_MOTO;

        double subtotalDias = diasACobrar * tarifaDia;
        double subtotalHoras = horasACobrar * tarifaHora;
        double totalPagar = subtotalDias + subtotalHoras;

        if (tipoVehiculo.equalsIgnoreCase("Carro")) {
            recaudoCarros += totalPagar;
        } else {
            recaudoMotos += totalPagar;
        }

        mostrarFactura(celda, tipoVehiculo, ingreso, salida, diasACobrar, horasACobrar, tarifaDia, tarifaHora, subtotalDias, subtotalHoras, totalPagar);
    }

    // Método obligatorio: Reporte de Ocupación
    private static void mostrarReporteOcupacion() {
        int ocupadas = 0;
        for (int i = 0; i < CAPACIDAD_TOTAL; i++) {
            if (tiposVehiculo[i] != null) {
                ocupadas++;
            }
        }
        int libres = CAPACIDAD_TOTAL - ocupadas;

        mostrarMensaje("\n============================================");
        mostrarMensaje("           REPORTE DE OCUPACIÓN             ");
        mostrarMensaje("============================================");
        mostrarMensaje("Celdas Ocupadas: " + ocupadas);
        mostrarMensaje("Celdas Libres  : " + libres);
        mostrarMensaje("Capacidad Total: " + CAPACIDAD_TOTAL);
        mostrarMensaje("============================================\n");
    }

    // Método obligatorio: Reporte Financiero
    private static void mostrarReporteFinanciero() {
        double totalGeneral = recaudoCarros + recaudoMotos;

        mostrarMensaje("\n============================================");
        mostrarMensaje("            REPORTE FINANCIERO              ");
        mostrarMensaje("============================================");
        System.out.printf("Recaudo Carros : $%.2f%n", recaudoCarros);
        System.out.printf("Recaudo Motos  : $%.2f%n", recaudoMotos);
        mostrarMensaje("--------------------------------------------");
        System.out.printf("TOTAL RECAUDO  : $%.2f%n", totalGeneral);
        mostrarMensaje("============================================\n");
    }

    // ==========================================
    // MÉTODOS DE APOYO Y VALIDACIONES
    // ==========================================

    private static void mostrarMensaje(String mensaje) {
        System.out.println(mensaje);
    }

    private static void mostrarFactura(int celda, String tipoVehiculo, LocalDateTime ingreso, LocalDateTime salida,
                                       long dias, long horas, double tarifaDia, double tarifaHora,
                                       double subtotalDias, double subtotalHoras, double total) {
        mostrarMensaje("\n============================================");
        mostrarMensaje("             FACTURA DE SALIDA              ");
        mostrarMensaje("============================================");
        mostrarMensaje("Celda Liberada : " + celda);
        mostrarMensaje("Tipo Vehículo  : " + tipoVehiculo);
        mostrarMensaje("Fecha Ingreso  : " + ingreso.format(FORMATO_FECHA_HORA));
        mostrarMensaje("Fecha Salida   : " + salida.format(FORMATO_FECHA_HORA));
        mostrarMensaje("--------------------------------------------");
        mostrarMensaje("Desglose:");
        if (dias > 0) {
            System.out.printf(" - Días (%d x $%.0f)  : $%.2f%n", dias, tarifaDia, subtotalDias);
        }
        if (horas > 0 || dias == 0) {
            System.out.printf(" - Horas (%d x $%.0f) : $%.2f%n", horas, tarifaHora, subtotalHoras);
        }
        mostrarMensaje("--------------------------------------------");
        System.out.printf("TOTAL A PAGAR  : $%.2f%n", total);
        mostrarMensaje("============================================\n");
    }

    private static LocalDateTime leerFechaHora(String mensaje) {
        while (true) {
            System.out.print(mensaje);
            String texto = SCANNER.nextLine().trim();

            if (!formatoFechaHoraValido(texto)) {
                mostrarMensaje("Error: Fecha u hora inválidas. Use formato yyyy-MM-dd HH:mm y horario 08:00 a 22:00.");
                continue;
            }

            try {
                LocalDateTime fechaParsed = LocalDateTime.parse(texto, FORMATO_FECHA_HORA);
                String textoFormateado = fechaParsed.format(FORMATO_FECHA_HORA);

                if (!texto.equals(textoFormateado)) {
                    mostrarMensaje("Error: La fecha ingresada no existe en el calendario. Intente nuevamente.");
                    continue;
                }

                return fechaParsed;
            } catch (Exception e) {
                mostrarMensaje("Error al procesar la fecha. Intente nuevamente.");
            }
        }
    }

    private static boolean formatoFechaHoraValido(String texto) {
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

    private static int leerDigitos(String texto, int inicio, int cantidad) {
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

    private static int leerEnteroEnRango(String mensaje, int min, int max) {
        while (true) {
            System.out.print(mensaje);
            if (!SCANNER.hasNextInt()) {
                mostrarMensaje("Error: Ingrese un número entero válido.");
                SCANNER.nextLine();
                continue;
            }
            int valor = SCANNER.nextInt();
            SCANNER.nextLine();

            if (valor < min || valor > max) {
                mostrarMensaje("Error: Opción fuera de rango (" + min + " - " + max + ").");
                continue;
            }
            return valor;
        }
    }

    private static long contarDiasCompletos(LocalDateTime ingreso, LocalDateTime salida) {
        return ChronoUnit.DAYS.between(ingreso, salida);
    }

    private static long calcularHorasRestantes(LocalDateTime cursor, LocalDateTime salida) {
        long minutosRestantes = Duration.between(cursor, salida).toMinutes();
        return (minutosRestantes + 59) / 60;
    }
}