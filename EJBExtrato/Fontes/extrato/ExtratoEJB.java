package extrato;

import com.eletros.benef.ParticipanteCDElet;

import java.rmi.RemoteException;

import javax.ejb.EJBObject;

public interface ExtratoEJB extends EJBObject {
    ExtratoCD geraExtratoCDElet(ParticipanteCDElet part) throws Exception, 
                                                                RemoteException;

}
