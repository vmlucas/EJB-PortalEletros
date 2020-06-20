package EJB.Eletros;

import java.rmi.RemoteException;

import javax.ejb.CreateException;
import javax.ejb.EJBHome;

public interface FornecedorEJBHome extends EJBHome {
    FornecedorEJB create() throws RemoteException, CreateException;
}
