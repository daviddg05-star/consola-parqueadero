package app;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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
            mostrarMensaje("3. Reporte de ocupación / Estado de celdas");
            mostrarMensaje("4. Reporte financiero");
            mostrarMensaje("5. Salir");

            int opcion = leerEnteroEnRango("Seleccione una opción (1-5): ", 1, 5);

            switch (opcion) {
                case 1:
                    registrarEntrada();
                    break;
                case 2:
                    // Se implementará en el paso de registro de salida
                    break;
                case 3:
                    mostrarEstadoCeldas();
                    break;
                case 4:
                    // Se implementará en el paso de reportes
                    break;
                case 5:
                    salir = true;
                    mostrarMensaje("\n¡Gracias por utilizar el sistema!");
                    break;
            }
        }

        SCANNER.close();
    }

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

    private static int buscarCeldaDisponible() {
        for (int i = 0; i < CAPACIDAD_TOTAL; i++) {
            if (tiposVehiculo[i] == null) {
                return i;
            }
        }
        return -1;
    }

    private static void mostrarEstadoCeldas() {
        mostrarMensaje("\n--- ESTADO DE CELDAS DISPONIBLES ---");
        for (int i = 0; i < CAPACIDAD_TOTAL; i++) {
            String estado = (tiposVehiculo[i] == null) ? "[ LIBRE ]" : "[ OCUPADA - " + tiposVehiculo[i] + " ]";
            mostrarMensaje("Celda " + (i + 1) + ": " + estado);
        }
    }

    // ==========================================
    // MÉTODOS OBLIGATORIOS DE VALIDACIÓN DE DATOS
    // ==========================================

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

                // Comprobación estricta de existencia en el calendario (ej: 30 de febrero)
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
                SCANNER.nextLine(); // Limpiar entrada
                continue;
            }
            int valor = SCANNER.nextInt();
            SCANNER.nextLine(); // Consumir salto de línea pendiente

            if (valor < min || valor > max) {
                mostrarMensaje("Error: Opción fuera de rango (" + min + " - " + max + ").");
                continue;
            }
            return valor;
        }
    }

    private static void mostrarMensaje(String mensaje) {
        System.out.println(mensaje);
    }
}