package EJB.Eletros;
import javax.ejb.EJBHome;
import java.rmi.RemoteException;
import javax.ejb.CreateException;

public interface UsuarioEJBHome extends EJBHome 
{
  UsuarioEJB create() throws RemoteException, CreateException;
}
