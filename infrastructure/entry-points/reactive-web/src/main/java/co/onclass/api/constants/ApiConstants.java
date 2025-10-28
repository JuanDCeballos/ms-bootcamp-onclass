package co.onclass.api.constants;

public final class ApiConstants {

    private ApiConstants() {
    }

    private static final String API_VERSION_BASE = "/api/v1";
    private static final String BOOTCAMP_BASE = API_VERSION_BASE + "/bootcamp";

    public static final String ID_BOOTCAMP_PATH_VARIABLE = "idBootcamp";

    public static final String GUARDAR_BOOTCAMP = BOOTCAMP_BASE;
    public static final String BOOTCAMP_BY_ID = BOOTCAMP_BASE + "/{" + ID_BOOTCAMP_PATH_VARIABLE + "}";
    public static final String ASIGNAR_CAPACIDADES = BOOTCAMP_BY_ID + "/capacidades";
}
