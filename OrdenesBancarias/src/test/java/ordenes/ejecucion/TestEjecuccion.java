package ordenes.ejecucion;

import ordenes.transferencias.OrdenDTO;
import ordenes.transferencias.SolicitudDTO;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;



public class TestEjecuccion {

    @Test
    public void casoValidoBancoBlancoTalonario(){
        OrdenDTO ordenDTO=new OrdenDTO("",3589,50000,"gordo","Talonario");
        EjecucionOrdenDocumental ejecucionOrdenDocumental=new EjecucionOrdenDocumental();
        SolicitudDTO solicitudDTO= ejecucionOrdenDocumental.tramitar(ordenDTO);

        //evaluo que la lista en solicitudDTO tiene un solo elemento.
        Assertions.assertEquals(1,solicitudDTO.getDocumento().size());
        //evaluo que el elemento presente en la lista es un documento de tipo talon
        Assertions.assertEquals(solicitudDTO.getDocumentos().getFirst() instanceof  Talon);
    }

}