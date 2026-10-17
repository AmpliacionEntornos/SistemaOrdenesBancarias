package ordenes.validacion;

public enum TipoError {
    ORDEN_NULA("La orden no puede ser nula"),
    BANCO_INCORRECTO("Error en código de banco"),
    SUCURSAL_INCORRECTA("Error en código de sucursal"),
    CUENTA_INCORRECTA("Error en número de cuenta"),
    CLAVE_INCORRECTA("Error en clave personal"),
    ORDEN_INCORRECTA("Valor de orden incorrecto");

    private final String descripcion;

    TipoError(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public String getMensaje() {
        return name() + ": " + descripcion;
    }
}
