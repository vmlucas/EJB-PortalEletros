package extrato;

import com.eletros.benef.ParticipanteCDElet;

import javax.ejb.EJBLocalObject;

public interface ExtratoEJBLocal extends EJBLocalObject {
    ExtratoCD geraExtratoCDElet(ParticipanteCDElet part) throws Exception;
    
}
