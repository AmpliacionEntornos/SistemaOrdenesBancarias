package ordenes.ejecucion;

import ordenes.documentos.Movimientos;
import ordenes.documentos.Talon;
import ordenes.transferencia.OrdenDTO;
import ordenes.transferencia.SolicitudDTO;
import ordenes.validacion.TipoError;
import ordenes.validacion.ValidadorOrden;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

class ProcesadorOrdenesTest {
    private ProcesadorOrdenes procesador;

    @BeforeEach
    void prepararProcesador() {
        procesador = new ProcesadorOrdenes(new ValidadorOrden());
    }

    private OrdenDTO orden(String banco, int sucursal, int cuenta, String clave, String tipoOrden) {
        return new OrdenDTO(banco, sucursal, cuenta, clave, tipoOrden);
    }

    private void comprobarQueFallaCon(TipoError errorEsperado, OrdenDTO orden) {
        OrdenNoValida excepcion = assertThrows(OrdenNoValida.class, () -> procesador.procesar(orden));
        assertEquals(errorEsperado.getMensaje(), excepcion.getMessage());
    }

    // ##############Ordenes correctas##############

    @Test
    void seEnviaElTalonarioCuandoSePideUnTalonario() {
        SolicitudDTO solicitud = procesador.procesar(orden("", 3589, 50000, "gordo", "Talonario"));

        assertEquals(1, solicitud.getDocumentos().size());
        assertInstanceOf(Talon.class, solicitud.getDocumentos().get(0));
        assertNotNull(solicitud.getIdentificadorOperacion());
    }

    @Test
    void seEnvianSoloLosMovimientosCuandoSePidenMovimientos() {
        SolicitudDTO solicitud = procesador.procesar(orden("359", 3589, 50000, "gordo", "Movimientos"));

        assertEquals(1, solicitud.getDocumentos().size());
        assertInstanceOf(Movimientos.class, solicitud.getDocumentos().get(0));
    }

    @Test
    void seEnvianLosDosDocumentosCuandoLaOrdenVieneEnBlanco() {
        SolicitudDTO solicitud = procesador.procesar(orden("", 3589, 50000, "gordo", ""));

        assertEquals(2, solicitud.getDocumentos().size());
        assertInstanceOf(Talon.class, solicitud.getDocumentos().get(0));
        assertInstanceOf(Movimientos.class, solicitud.getDocumentos().get(1));
    }

    @Test
    void cadaSolicitudTieneUnIdentificadorDeOperacionDistinto() {
        OrdenDTO misma = orden("", 3589, 50000, "gordo", "Talonario");

        assertNotEquals(procesador.procesar(misma).getIdentificadorOperacion(),
                procesador.procesar(misma).getIdentificadorOperacion());
    }

    @Test
    void laClaveConMayusculasMinusculasYNumerosEsValida() {
        assertDoesNotThrow(() -> procesador.procesar(orden("", 3589, 50000, "aB3dE", "Talonario")));
    }

    // ############# Codigo de banco #############

    @ParameterizedTest
    @ValueSource(strings = {"1000", "187", "Boni"})
    void seRechazaUnBancoConMasDeTresDigitosMenorDe200OTextoNoNumerico(String banco) {
        comprobarQueFallaCon(TipoError.BANCO_INCORRECTO, orden(banco, 3589, 50000, "gordo", "Talonario"));
    }

    @ParameterizedTest
    @ValueSource(strings = {"200", "999"})
    void seAceptanLosExtremosValidosDelBanco(String banco) {
        assertDoesNotThrow(() -> procesador.procesar(orden(banco, 3589, 50000, "gordo", "Talonario")));
    }

    @ParameterizedTest
    @ValueSource(strings = {"199"})
    void seRechazanLosBancosJustoFueraDelRango(String banco) {
        comprobarQueFallaCon(TipoError.BANCO_INCORRECTO, orden(banco, 3589, 50000, "gordo", "Talonario"));
    }

    // ############# Codigo de sucursal #############

    @ParameterizedTest
    @ValueSource(ints = {77, 25000})
    void seRechazaUnaSucursalConMenosOMasDeCuatroDigitos(int sucursal) {
        comprobarQueFallaCon(TipoError.SUCURSAL_INCORRECTA, orden("359", sucursal, 50000, "gordo", "Talonario"));
    }

    @ParameterizedTest
    @ValueSource(ints = {1000, 9999})
    void seAceptanLosExtremosValidosDeLaSucursal(int sucursal) {
        assertDoesNotThrow(() -> procesador.procesar(orden("", sucursal, 50000, "gordo", "Talonario")));
    }

    @ParameterizedTest
    @ValueSource(ints = {999, 10000})
    void seRechazanLasSucursalesJustoFueraDelRangoElCeroYLosNegativos(int sucursal) {
        comprobarQueFallaCon(TipoError.SUCURSAL_INCORRECTA, orden("", sucursal, 50000, "gordo", "Talonario"));
    }

    // ############# Numero de cuenta #############

    @ParameterizedTest
    @ValueSource(ints = {999, 999999})
    void seRechazaUnaCuentaConMenosOMasDeCincoDigitos(int cuenta) {
        comprobarQueFallaCon(TipoError.CUENTA_INCORRECTA, orden("359", 3589, cuenta, "gordo", "Talonario"));
    }

    @ParameterizedTest
    @ValueSource(ints = {10000, 99999})
    void seAceptanLosExtremosValidosDeLaCuenta(int cuenta) {
        assertDoesNotThrow(() -> procesador.procesar(orden("", 3589, cuenta, "gordo", "Talonario")));
    }

    @ParameterizedTest
    @ValueSource(ints = {9999, 100000})
    void seRechazanLasCuentasJustoFueraDelRangoElCeroYLosNegativos(int cuenta) {
        comprobarQueFallaCon(TipoError.CUENTA_INCORRECTA, orden("", 3589, cuenta, "gordo", "Talonario"));
    }

    // ############# Clave personal #############

    @ParameterizedTest
    @ValueSource(strings = {"Alto", "", "@&%^"})
    void seRechazaUnaClaveCortaVaciaOConCaracteresEspeciales(String clave) {
        comprobarQueFallaCon(TipoError.CLAVE_INCORRECTA, orden("359", 3589, 50000, clave, "Talonario"));
    }

    @ParameterizedTest
    @ValueSource(strings = {"abcd", "abcdef"})
    void seRechazanLasClavesDeCuatroOSeisCaracteresYLasQueLlevanEspacios(String clave) {
        comprobarQueFallaCon(TipoError.CLAVE_INCORRECTA, orden("", 3589, 50000, clave, "Talonario"));
    }

    // ############# Tipo de orden #############

    @Test
    void seRechazaUnaOrdenDesconocida() {
        comprobarQueFallaCon(TipoError.ORDEN_INCORRECTA, orden("359", 3589, 50000, "gordo", "Saldo"));
    }

    @ParameterizedTest
    @ValueSource(strings = {"talonario", "MOVIMIENTOS"})
    void laOrdenSeExigeExactaDistingueMayusculasYNoAdmiteEspacios(String tipoOrden) {
        comprobarQueFallaCon(TipoError.ORDEN_INCORRECTA, orden("", 3589, 50000, "gordo", tipoOrden));
    }

    // #############Casos generales #############

    @Test
    void seRechazaUnaOrdenNula() {
        comprobarQueFallaCon(TipoError.ORDEN_NULA, null);
    }

    @Test
    void siFallanVariosCamposSeInformaDelPrimeroElBancoAntesQueLaSucursal() {
        comprobarQueFallaCon(TipoError.BANCO_INCORRECTO, orden("1000", 77, 50000, "gordo", "Talonario"));
    }
}