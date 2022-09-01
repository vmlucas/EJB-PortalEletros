package EJB.Emprestimo;

import com.util.DataEletros;
import java.rmi.RemoteException;
import java.util.Collection;
import javax.ejb.EJBObject;


public interface EmprestimoEJB extends EJBObject {
    ExtratoEmprestimo BuscarExtratoEmprestimo(Emprestimo emprestimo, 
                                              DataEletros dataFim) throws Exception, 
                                                                          RemoteException;

    Collection BuscarEmprestimos(ClienteEmprestimo cliente) throws Exception, 
                                                                   RemoteException;

    ConcessaoEmprestimo BuscarConcessaoEmprestimo(ClienteEmprestimo cliente) throws Exception, 
                                                                                    RemoteException;

    double BuscarIOFSimulacao(double valorSolicitado, 
                              ConcessaoEmprestimo concessao) throws Exception, 
                                                                    RemoteException;

    double CalcularDescontoMinimo(int numRemuneracoes, 
                                  ConcessaoEmprestimo concessao) throws Exception, 
                                                                        RemoteException;

    Emprestimo BuscarEmprestimoAtual(ClienteEmprestimo cliente) throws Exception, 
                                                                       RemoteException;

    ClienteEmprestimo BuscarClienteEmprestimo(String numEletros) throws Exception, 
                                                                        RemoteException;
}
