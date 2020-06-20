package EJB.Eletros;

import java.rmi.RemoteException;

import javax.ejb.CreateException;
import javax.ejb.EJBHome;

public interface ParticipanteEJBHome extends EJBHome {
    ParticipanteEJB create() throws RemoteException, CreateException;
}
