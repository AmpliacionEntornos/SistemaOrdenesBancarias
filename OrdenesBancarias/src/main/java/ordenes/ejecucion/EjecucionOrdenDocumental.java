package ordenes.ejecucion;

import ordenes.documentos.Documento;
import ordenes.documentos.Talon;
import ordenes.transferencias.OrdenDTO;
import ordenes.transferencias.SolicitudDTO;

import java.util.ArrayList;
import java.util.List;

public class EjecucionOrdenDocumental {
    public SolicitudDTO tramitar(OrdenDTO ordenDTO){
        SolicitudDTO solicitudDTO;

         solicitudDTO=new SolicitudDTO(List.of(new Talon()));
        return solicitudDTO;
    }
}
