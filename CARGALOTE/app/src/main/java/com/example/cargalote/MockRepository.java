package com.example.cargalote;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

/** Único punto de acceso a los datos de prueba.
 *  Cuando haya API, solo cambia el interior de estos métodos. */
public class MockRepository {

    // Datos que se repiten: se definen una sola vez
    private static final String SECTOR_MUELLE_B = "Terminal 2 - Muelle B";
    private static final String SECTOR_PATIO_4 = "Patio de contenedores 4";
    private static final String SECTOR_ACCESOS = "Control de accesos - Puerta 1";

    private static final String GRUA_G07 = "Grúa pórtico G-07";
    private static final String AUTOELEVADOR_A12 = "Autoelevador A-12";
    private static final String SIN_MAQUINARIA = "Sin maquinaria";

    private static final String SUP_PEREZ = "Carlos Pérez";
    private static final String SUP_GOMEZ = "María Gómez";
    private static final String SUP_FERNANDEZ = "Luis Fernández";

    // ---- Login ----

    public static boolean login(String legajo, String pass) {
        return "1234".equals(legajo) && "demo".equals(pass);
    }

    // ---- Empleado ----

    public static Empleado getEmpleado() {
        return new Empleado("Juan Rodríguez", "1234",
                "Operador de grúa pórtico", "Terminal 2");
    }

    // ---- Operaciones ----

    public static List<Operacion> getAsignaciones() {
        // PRUEBAS (descomentá UNA línea y borrala antes de entregar):
        // return generarPrueba(0);    // lista vacía
        // return generarPrueba(1);    // solo la operación de hoy
        // return generarPrueba(30);   // muchas, para probar el scroll

        return Arrays.asList(
                new Operacion(1, "Electrodomésticos", "MSCU4837261", EstadoOperacion.EN_CURSO,
                        SECTOR_MUELLE_B, "15/11/2026", "Noche", "22:00 - 06:00",
                        GRUA_G07, SUP_PEREZ),
                new Operacion(2, "Autopartes", "MSKU9023415", EstadoOperacion.PENDIENTE,
                        SECTOR_MUELLE_B, "16/11/2026", "Noche", "22:00 - 06:00",
                        GRUA_G07, SUP_PEREZ),
                new Operacion(3, "Maquinaria agrícola", "TGHU6150238", EstadoOperacion.PENDIENTE,
                        SECTOR_PATIO_4, "17/11/2026", "Tarde", "14:00 - 22:00",
                        AUTOELEVADOR_A12, SUP_GOMEZ),
                new Operacion(4, "Textiles", "CMAU2748193", EstadoOperacion.COMPLETADA,
                        SECTOR_PATIO_4, "18/11/2026", "Tarde", "14:00 - 22:00",
                        AUTOELEVADOR_A12, SUP_GOMEZ),
                new Operacion(5, "Frutas refrigeradas", "HLXU3392054", EstadoOperacion.PENDIENTE,
                        SECTOR_ACCESOS, "19/11/2026", "Mañana", "06:00 - 14:00",
                        SIN_MAQUINARIA, SUP_FERNANDEZ)
        );
    }

    public static Operacion getHoy() {
        return getAsignaciones().get(0);
    }

    public static List<Operacion> getProximas() {
        List<Operacion> todas = getAsignaciones();
        return todas.subList(1, todas.size());
    }

    // ---- Generador para probar distintas cantidades ----

    public static List<Operacion> generarPrueba(int cantidad) {
        List<Operacion> lista = new ArrayList<>();
        EstadoOperacion[] estados = EstadoOperacion.values();

        for (int i = 0; i < cantidad; i++) {
            lista.add(new Operacion(
                    i + 1,
                    "Carga de prueba " + (i + 1),
                    String.format(Locale.US, "MSCU%07d", 1000000 + i),
                    estados[i % estados.length],
                    SECTOR_MUELLE_B,
                    "15/11/2026",
                    "Noche", "22:00 - 06:00",
                    GRUA_G07, SUP_PEREZ));
        }
        return lista;
    }
}