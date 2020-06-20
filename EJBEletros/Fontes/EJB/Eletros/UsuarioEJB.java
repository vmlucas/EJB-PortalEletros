package EJB.Eletros;

import com.eletros.Usuario;

import java.rmi.*;

import java.util.Collection;

import javax.ejb.EJBObject;
import java.util.*;
import java.rmi.RemoteException;
import java.sql.SQLException;
import com.eletros.*;

/**
 * Interface de acesso remoto. Usado pelo cliente para acessar o bean em si no Servidor.
 */
public interface UsuarioEJB extends EJBObject 
{
   
    /**
      * Busca uma cole��o de nomes das areas subordinadas a uma areaPai.
      */
    public Collection buscaAreasSubordinadas( String areaPai) 
      throws RemoteException,Exception;
    
   /**
   * Retorna um usu�rio representado pelo login. 
   * Usuario esse que pode ser um Usuario ou um Gerente.
   */
   public Usuario buscarUsuarioLogin(String login)
    throws RemoteException,Exception;
   
   
   /**
   * Retorna um usu�rio representado pela matricula(chapa). 
   * Usuario esse que pode ser um Usuario ou um Gerente.
   */ 
   public Usuario buscarUsuarioChapa(String chapa)
    throws RemoteException,Exception;  
   
   
   /**
   * Busca um usu�rio representado pelo c�digo
   * do usu�rio <tt>codigoUsuario</tt>
   */
   public Usuario buscarUsuarioCodigo(String codigoUsuario)
    throws RemoteException,Exception;
   
   
   /**
   * Busca uma collection de usuarios validos sem informacao de gerente
   */
   public Collection buscarUsuariosValidos()
     throws RemoteException,Exception;
   
   
   /**
   * Busca uma collection de usuarios relativos a uma divisao
   */
   public Collection buscarUsuariosDept(String dept)
    throws RemoteException,Exception;
   
   
   /**
   * Busca uma cole��o com os nomes das areas cadastradas na base de dados
   */
   public Collection buscaUsuariosArea()
    throws RemoteException,Exception; 

  /**
   * Salva altera��es sobre um usuario j� existente. Por enquanto nao cria um usuario novo
   */
  void persistirUsuario(Usuario usu) throws RemoteException, Exception;

    Collection buscarUsuarios() throws Exception, RemoteException;

    Usuario buscarUsuarioValidoLogin(String login) throws Exception, 
                                                          RemoteException;
}
