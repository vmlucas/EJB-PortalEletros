package extrato;

import javax.ejb.CreateException;
import javax.ejb.EJBLocalHome;

public interface ExtratoEJBLocalHome extends EJBLocalHome {
    ExtratoEJBLocal create() throws CreateException;
}
