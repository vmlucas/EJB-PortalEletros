package EJB.Eletros;

import com.eletros.fin.*;
import com.eletros.fin.CategoriaFornecedor;

import java.rmi.RemoteException;
import java.util.Collection;
import javax.ejb.EJBObject;

public interface FornecedorEJB extends EJBObject {
    Collection BuscarFornecedores() throws Exception, RemoteException;


    Fornecedor BuscaFornecedor(String codigo) throws Exception, 
                                                     RemoteException;

    Collection buscarBancosCadastrados() throws Exception, RemoteException;

    Collection buscaCategorias() throws Exception, RemoteException;

    Collection buscarAgenciasBanco(String codBanco) throws Exception, 
                                                           RemoteException;

    Collection BuscaFornecedorPFNome(String nome) throws Exception, 
                                                         RemoteException;

    Collection BuscaFornecedoresPFCategoria(int codCategoria) throws Exception, 
                                                                     RemoteException;

    int buscaOcorrenciasAgenciasBanco(String codBanco) throws Exception, 
                                                              RemoteException;

    int buscaOcorrenciasCGC(String cgc) throws Exception, RemoteException;

    int buscaOcorrenciasCPF(String cpf) throws Exception, RemoteException;

    Fornecedor BuscarFornecedorCGC(String cgc) throws Exception, 
                                                      RemoteException;

    Fornecedor BuscarFornecedorCPF(String cpf) throws Exception, 
                                                      RemoteException;

    void persistirFornecedor(Fornecedor forn) throws Exception, 
                                                     RemoteException;

    Collection BuscaFornecedorPJNome(String nome) throws Exception, 
                                                         RemoteException;CategoriaFornecedor BuscarCategoria(int codigo) throws Exception, 
       RemoteException;

    Collection BuscaFornecedoresPJCategoria(int codCategoria) throws Exception, 
                                                                     RemoteException;

    void cadastrarCategorias(Collection categorias) throws Exception, 
                                                           RemoteException;

    void dropCategoria(CategoriaFornecedor Categoria) throws Exception, 
                                                             RemoteException;
}
