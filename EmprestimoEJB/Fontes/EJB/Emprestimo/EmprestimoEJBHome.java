package EJB.Emprestimo;

import java.rmi.RemoteException;

import javax.ejb.CreateException;
import javax.ejb.EJBHome;

public interface EmprestimoEJBHome extends EJBHome {
    EmprestimoEJB create() throws RemoteException, CreateException;
}
