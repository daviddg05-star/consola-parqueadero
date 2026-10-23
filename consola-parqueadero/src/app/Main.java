package app;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;

public class Main {

    // 1. Constantes del sistema
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

    // 2. Arreglos paralelos para las celdas
    private static final String[] tiposVehiculo = new String[CAPACIDAD_TOTAL];
    private static final LocalDateTime[] fechasIngreso = new LocalDateTime[CAPACIDAD_TOTAL];

    // 3. Variables de recaudo acumulado
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
            int opcion = Integer.parseInt(SCANNER.nextLine().trim());

            switch (opcion) {
                case 1:
                    // Se implementará en el paso de registro de entrada
                    break;
                case 2:
                    // Se implementará en el paso de registro de salida
                    break;
                case 3:
                    // Se implementará en el paso de reportes
                    break;
                case 4:
                    // Se implementará en el paso de reportes
                    break;
                case 5:
                    salir = true;
                    mostrarMensaje("\n¡Gracias por utilizar el sistema!");
                    break;
                default:
                    mostrarMensaje("Opción no válida.");
            }
        }

        SCANNER.close();
    }

    private static void mostrarMensaje(String mensaje) {
        System.out.println(mensaje);
    }
}