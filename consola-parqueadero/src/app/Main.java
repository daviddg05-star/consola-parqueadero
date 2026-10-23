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
            mostrarMensaje("3. Reporte de ocupación");
            mostrarMensaje("4. Reporte financiero");
            mostrarMensaje("5. Salir");

            System.out.print("Seleccione una opción (1-5): ");
            String entrada = SCANNER.nextLine().trim();

            if (entrada.equals("1")) {
                registrarEntrada();
            } else if (entrada.equals("2")) {
                // Se implementará en el paso de registro de salida
            } else if (entrada.equals("3")) {
                // Se implementará en el paso de reportes
            } else if (entrada.equals("4")) {
                // Se implementará en el paso de reportes
            } else if (entrada.equals("5")) {
                salir = true;
                mostrarMensaje("\n¡Gracias por utilizar el sistema!");
            } else {
                mostrarMensaje("Opción no válida.");
            }
        }

        SCANNER.close();
    }

    // Método obligatorio: Registro de Entrada
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
        System.out.print("Seleccione el tipo (1-2): ");
        String opcionTipo = SCANNER.nextLine().trim();

        String tipoVehiculo = opcionTipo.equals("2") ? "Moto" : "Carro";
        LocalDateTime fechaIngreso = leerFechaHora("Ingrese fecha y hora de ingreso (yyyy-MM-dd HH:mm): ");

        tiposVehiculo[indiceLibre] = tipoVehiculo;
        fechasIngreso[indiceLibre] = fechaIngreso;

        mostrarMensaje("-> Vehículo registrado con éxito en la Celda " + (indiceLibre + 1));
    }

    // Método auxiliar para buscar espacio libre
    private static int buscarCeldaDisponible() {
        for (int i = 0; i < CAPACIDAD_TOTAL; i++) {
            if (tiposVehiculo[i] == null) {
                return i;
            }
        }
        return -1;
    }

    // Lectura inicial de fecha y hora
    private static LocalDateTime leerFechaHora(String mensaje) {
        while (true) {
            System.out.print(mensaje);
            String texto = SCANNER.nextLine().trim();

            try {
                return LocalDateTime.parse(texto, FORMATO_FECHA_HORA);
            } catch (Exception e) {
                mostrarMensaje("Error: Formato inválido. Use yyyy-MM-dd HH:mm.");
            }
        }
    }

    private static void mostrarMensaje(String mensaje) {
        System.out.println(mensaje);
    }
}