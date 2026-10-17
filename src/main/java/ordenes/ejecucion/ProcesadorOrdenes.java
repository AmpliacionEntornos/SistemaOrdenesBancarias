package ordenes.ejecucion;

import ordenes.documentos.Documento;
import ordenes.documentos.Movimientos;
import ordenes.documentos.Talon;
import ordenes.transferencia.OrdenDTO;
import ordenes.transferencia.SolicitudDTO;
import ordenes.validacion.ValidadorOrden;

import java.util.List;

public class ProcesadorOrdenes {
    private final ValidadorOrden validador;

    public ProcesadorOrdenes(ValidadorOrden validador) {
        this.validador = validador;
    }

    public SolicitudDTO procesar(OrdenDTO orden) {
        validador.validar(orden);

        String tipo = orden.getTipoOrden();
        List<Documento> documentos;

        if (ValidadorOrden.TALONARIO.equals(tipo)) {
            documentos = List.of(new Talon());
        } else if (ValidadorOrden.MOVIMIENTOS.equals(tipo)) {
            documentos = List.of(new Movimientos());
        } else {
            documentos = List.of(new Talon(), new Movimientos());
        }

        return new SolicitudDTO(documentos);
    }
}
