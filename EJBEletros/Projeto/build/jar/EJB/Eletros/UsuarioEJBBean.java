package EJB.Eletros;

import com.eletros.Usuario;
import javax.ejb.SessionBean;
import javax.ejb.SessionContext;
import EJB.Eletros.DAO.*;

import java.sql.SQLException;
import java.util.*;
import com.eletros.*;




/**
 * Classe que compoe o Session bean Usuario.
 * Session bean responsavel por buscar, atualizar ou cadastrar Usuarios e informa��es 
 * relativas a um usuario.
 */
public class UsuarioEJBBean implements SessionBean 
{
  private UsuarioDAO access;
  
    
  public UsuarioEJBBean()
  {
      access = new UsuarioDAO();  
  }
  
  
    /**
      * Busca uma cole��o de nomes das areas subordinadas a uma area.
      * 
      * @param areaPai
      * @return Collection of java.lang.String
      * @throws SQLException
      */
    public Collection buscaAreasSubordinadas( String areaPai) 
      throws Exception
    {
       return access.buscaAreasSubordinadas( areaPai );    
    }
    
    
  /**
   * Retorna um usu�rio representado pelo login. Podendo ser valido ou nao
   * @return Usuario ou Gerente
   * @throws Exception
   */
  public Usuario buscarUsuarioLogin(String login)
    throws Exception
  {
     return access.buscarUsuarioLogin(login);     
  }
  
  
  /**
   * Retorna um usu�rio representado pela matricula(chapa). 
   * @return Usuario ou Gerente
   * @throws Exception
   */
  public Usuario buscarUsuarioChapa(String chapa)
    throws Exception
  {
     return access.buscarUsuarioChapa( chapa );     
  }
  
  
  /**
   * Busca um usu�rio representado pelo c�digo
   * do usu�rio <tt>codigoUsuario</tt>
   * @return Usuario ou Gerente
   * @throws Exception
   */
  public Usuario buscarUsuarioCodigo(String codigoUsuario)
    throws Exception
  {
    return access.buscarUsuarioCodigo( codigoUsuario );
  }


  /**
   * Busca uma collection de usuarios validos
   * 
   * @return - Collection of Usuario
   * @throws java.lang.Exception
   */
  public Collection buscarUsuariosValidos()
     throws Exception
  {
     return access.buscarUsuariosValidos();
  }


  /**
   * Busca uma collection de usuarios relativos a uma divisao
   * @param dept - nome da divisao a qual pertencem os usu�rios
   * @return Collection of Usuario
   * @throws Exception
   */
  public Collection buscarUsuariosDept(String dept)
    throws Exception
  {
     return access.buscarUsuariosDept(dept);
  }
  
  
  /**
   * Busca uma cole��o com os nomes das areas cadastradas
   * @return Collection of String
   * @throws Exception
   */
  public Collection buscaUsuariosArea()
    throws Exception
  {
     return access.buscaUsuariosAreas();  
  }
  
  /**
   * Metodo de cria��o do EJB
   */
  public void ejbCreate()
  {
     
  }

  public void ejbActivate()
  {
  }

  public void ejbPassivate()
  {
  }

  public void ejbRemove()
  {
  }

  public void setSessionContext(SessionContext ctx)
  {
  }


  /**
   * Cria um novo usu�rio na base de dados ou 
   * Salva altera��es sobre um usuario j� existente.
   * @param usu - O usuario para ser persistido na base de dados
   * @throws java.lang.Exception
   */
  public void persistirUsuario(Usuario usu) 
    throws Exception
  {
     access.persistirUsuario( usu );
  }


    /**
     * Busca uma collection de usuarios validos ou nao.
     * 
     * @return - Collection of Usuario
     * @throws java.lang.Exception
     */
    public Collection buscarUsuarios() throws Exception {
        return access.buscarUsuarios();
    }


    /**
     * Busca um usuario valido, se nao for mais valido gera uma exception
     */
    public Usuario buscarUsuarioValidoLogin(String login) throws Exception {
        return access.buscarUsuarioValidoLogin(login);   
    }
}
