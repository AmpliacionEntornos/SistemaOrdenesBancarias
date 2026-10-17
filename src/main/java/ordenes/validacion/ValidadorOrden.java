package ordenes.validacion;
import ordenes.ejecucion.OrdenNoValida;
import ordenes.transferencia.OrdenDTO;
public class ValidadorOrden {
    public static final String TALONARIO = "Talonario";
    public static final String MOVIMIENTOS = "Movimientos";

    public void validar(OrdenDTO orden){
        if (orden == null) {
            throw new OrdenNoValida(TipoError.ORDEN_NULA.getMensaje());
        }
        if (!bancoValido(orden.getBanco())) {
            throw new OrdenNoValida(TipoError.BANCO_INCORRECTO.getMensaje());
        }
        if (orden.getSucursal() < 1000 || orden.getSucursal() > 9999) {
            throw new OrdenNoValida(TipoError.SUCURSAL_INCORRECTA.getMensaje());
        }
        if (orden.getCuenta() < 10000 || orden.getCuenta() > 99999) {
            throw new OrdenNoValida(TipoError.CUENTA_INCORRECTA.getMensaje());
        }
        if (orden.getClave() == null || !orden.getClave().matches("[A-Za-z0-9]{5}")) {
            throw new OrdenNoValida(TipoError.CLAVE_INCORRECTA.getMensaje());
        }
        if (!ordenValida(orden.getTipoOrden())) {
            throw new OrdenNoValida(TipoError.ORDEN_INCORRECTA.getMensaje());
        }
    }

    // en blanco o 3 dígitos mayor que 199
    private boolean bancoValido(String banco) {
        if (banco == null || banco.isEmpty()) {
            return true;
        }
        return banco.matches("[2-9][0-9]{2}");
    }

    // en blanco, "Talonario" o "Movimientos"
    private boolean ordenValida(String tipoOrden) {
        return tipoOrden == null || tipoOrden.isEmpty()
                || tipoOrden.equals(TALONARIO)
                || tipoOrden.equals(MOVIMIENTOS);
    }
}
