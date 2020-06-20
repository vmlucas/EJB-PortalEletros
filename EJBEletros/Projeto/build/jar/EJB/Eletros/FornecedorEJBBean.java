package EJB.Eletros;

import java.util.Collection;
import javax.ejb.EJBException;
import javax.ejb.SessionBean;
import javax.ejb.SessionContext;
import EJB.Eletros.DAO.*;

import com.eletros.fin.CategoriaFornecedor;
import com.eletros.fin.Fornecedor;


/**
 * Classe que compoe o Session bean de fornecedores.
 * Session bean responsavel por buscar listas de fornecedores atravez de varios parametros e
 * persistir um fornecedor. Tambem edita e apaga as categorias cadastradas e vinculadas a um
 * fornecedor. Busca tambem informações úteis para um sistema de fornecedor, tais como os bancos
 * e agencias cadastradas. 
 */
public class FornecedorEJBBean implements SessionBean {
    
    private SessionContext _context;
    private FornecedorDAO fornDAO;
    
    /**
     * Método usado para informar ao container EJB que o ejb precisa ser usado.
     */
    public void ejbCreate() 
    {
      fornDAO = new FornecedorDAO();
    }

    public void setSessionContext(SessionContext context) throws EJBException {
        _context = context;
    }

    public void ejbRemove() throws EJBException {
    }

    public void ejbActivate() throws EJBException {
    }

    public void ejbPassivate() throws EJBException {
    }

    
    /**
     * Busca uma "collection of objects com.eletros.fin.Fornecedor" com
     * todos os fornecedores cadastrados
     */
    public Collection BuscarFornecedores() 
       throws Exception 
    {
        return fornDAO.BuscarFornecedores();
    }

    
     /**
      * Busca um fornecedor pelo seu nsu.
      * @param codigo do fornecedor
      * @return - com.eletros.fin.Fornecedor
      * @throws Exception
      */
    public Fornecedor BuscaFornecedor(String codigo) 
       throws Exception 
    {
        return fornDAO.BuscaFornecedor( codigo );
    }


    /**
     * Busca uma colecao de bancos cadastrados na base de dados.
     * @return Collection of objects com.eletros.fin.Banco. 
     * @throws Exception
     */
    public Collection buscarBancosCadastrados()
        throws Exception
    {
         return fornDAO.buscarBancosCadastrados();    
    }

    
    /**
     * Busca uma colecao de agencias cadastradas na base de dados relativas a um banco 
     * @params codigo do banco
     * @return Collection of objects com.eletros.fin.Agencia. 
     * @throws Exception
     */
    public Collection buscarAgenciasBanco(String codBanco) 
       throws Exception 
    {
        return fornDAO.buscarAgenciasBanco( codBanco );
    }
    
    
    /**
     * Busca uma colecao de categorias cadastradas  
     * @return Collection of objects com.eletros.fin.CategoriaFornecedor. 
     * @throws Exception
     */
    public Collection buscaCategorias() throws Exception {
        return fornDAO.buscaCategorias();
    }

    
    /**
     * Busca uma colecao de fornecedores tipo pessoa fisica pela razão social dos mesmos  
     * @params razao social do fornecedor
     * @return Collection of objects com.eletros.fin.Fornecedor. 
     * @throws Exception
     */
    public Collection BuscaFornecedorPFNome(String nome) throws Exception {
        return fornDAO.BuscaFornecedorPFNome( nome );
    }
    
    
    /**
     * Busca uma colecao de fornecedores tipo pessoa fisica pelo codigo da categoria  
     * @params codigo da categoria
     * @return Collection of objects com.eletros.fin.Fornecedor. 
     * @throws Exception
     */
    public Collection BuscaFornecedoresPFCategoria(int codCategoria) throws Exception {
        return fornDAO.BuscaFornecedoresPFCategoria( codCategoria );
    }


    /**
     * Busca o numero de agencias de um banco  
     * @params codigo do banco
     * @return int 
     * @throws Exception
     */
    public int buscaOcorrenciasAgenciasBanco(String codBanco) throws Exception {
        return fornDAO.buscaOcorrenciasAgenciasBanco( codBanco);
    }


    /**
     * Busca o numero de ocorrencias do mesmo CGC  
     * @params numero do CGC
     * @return int 
     * @throws Exception
     */
    public int buscaOcorrenciasCGC(String cgc) throws Exception {
        return fornDAO.buscaOcorrenciasCGC( cgc );
    }


     /**
      * Busca o numero de ocorrencias do mesmo CPF  
      * @params numero do CPF
      * @return int 
      * @throws Exception
      */
    public int buscaOcorrenciasCPF(String cpf) throws Exception {
        return fornDAO.buscaOcorrenciasCPF( cpf );
    }


    /**
     * Busca um fornecedor pelo seu CGC.
     * @params numero do CGC
     * @return - com.eletros.fin.Fornecedor
     * @throws Exception
     */
    public Fornecedor BuscarFornecedorCGC(String cgc) throws Exception {
        return fornDAO.BuscarFornecedorCGC( cgc );
    }


    /**
     * Busca um fornecedor pelo seu CPF.
     * @params - numero do CPF
     * @return - com.eletros.fin.Fornecedor
     * @throws Exception
     */
    public Fornecedor BuscarFornecedorCPF(String cpf) throws Exception {
        return fornDAO.BuscarFornecedorCPF( cpf );
    }


    /**
     * Atualiza ou insere um novo fornecedor
     * @params Object com.eletros.fin.Fornecedor
     * @throws Exception
     */
    public void persistirFornecedor(Fornecedor forn) throws Exception {
        fornDAO.persistirFornecedor( forn );
    }


    /**
     * Busca um ou mais fornecedores tipo PJ pelo sua razao social.
     * @params - razao social do fornecedor
     * @return - Collection of objects com.eletros.fin.Fornecedor
     * @throws Exception
     */
    public Collection BuscaFornecedorPJNome(String nome) throws Exception {
        return fornDAO.BuscaFornecedorPJNome( nome );
    }


    /**
     * Busca uma categoria pelo seu codigo.
     * @params - codigo da categoria
     * @return - Object com.eletros.fin.CategoriaFornecedor
     * @throws Exception
     */
    public CategoriaFornecedor BuscarCategoria(int codigo) throws Exception {
        return fornDAO.BuscarCategoria( codigo );
    }


    /**
     * Busca um ou mais fornecedores tipo PJ pelo sua categoria.
     * @params - Categoria do fornecedor
     * @return - Collection of objects com.eletros.fin.Fornecedor
     * @throws Exception
     */
    public Collection BuscaFornecedoresPJCategoria(int codCategoria) throws Exception {
        return fornDAO.BuscaFornecedoresPJCategoria( codCategoria );
    }


    /**
     * Cadastra novas categorias
     * @params - Collection of objects com.eletros.fin.CategoriaFornecedor
     * @throws Exception
     */
    public void cadastrarCategorias(Collection categorias) throws Exception 
    {
       fornDAO.cadastrarCategorias( categorias );
    }


    /**
     * Apaga uma categoria
     * @param categoria 
     * @throws Exception
     */
    public void dropCategoria(CategoriaFornecedor categoria) throws Exception {
       fornDAO.dropCategoria( categoria);
    }
}
