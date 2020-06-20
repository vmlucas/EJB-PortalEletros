package extrato;

import java.rmi.RemoteException;

import javax.ejb.CreateException;
import javax.ejb.EJBHome;

public interface ExtratoEJBHome extends EJBHome {
    ExtratoEJB create() throws RemoteException, CreateException;
}
