package EJB.Eletros.DAO;

import com.util.DataEletros;
import java.util.*;
import java.sql.*;
import com.eletros.*;
import javax.naming.*;
import javax.sql.*;

/**
 * Classe que busca na base de dados oracle informações do usuário.
 * Define-se como usuário todo aquele que consta na tabela USU_ELET na base de dados
 */
public class UsuarioDAO
{

    /**
     *  Constroi um objeto UsuarioDAO
     */
    public UsuarioDAO()
    {  }

   
   /**
     * Busca uma coleção de nomes das areas subordinadas a uma area.
     * 
     * @param areaPai
     * @return Collection of java.lang.String
     * @throws SQLException
     */
   public Collection buscaAreasSubordinadas( String areaPai) 
     throws SQLException
   {
       LinkedList areas = new LinkedList();
       
       Connection conn = this.getConnection();
       
       String cmd = "select distinct u.nm_usu_are from usu_elet u " + 
                    "where u.nm_usu_ger = ( select u2.cd_usu_log from usu_elet u2 " + 
                                                   "where u2.cd_usu_log = u.nm_usu_ger " + 
                                                   "and u2.nm_usu_are = '"+areaPai+"' and u2.fl_usu_ati = 'S') "+
                            "and u.fl_usu_ati = 'S'";
       
       Statement smt = conn.createStatement();
       ResultSet result = smt.executeQuery( cmd );
       while( result.next()) 
       {
          String area = result.getString("nm_usu_are");
          areas.add( area );
       }
       
       result.close();
       smt.close();
       conn.close();
       
       return areas;
   }
   
   
    /**
     * Busca um objeto usuario através do seu login;
     *
     * @param login
     * @return Gerente ou Usuario
     * @throws Exception
     */
      public Usuario buscarUsuarioValidoLogin( String login )
         throws Exception
      {
        
         //tenta buscar um Gerente referente ao login
         Gerente gerente = buscaGerenteLogin( login );
         if( gerente == null )
         {
             //não sendo um gerente, tenta buscar um usuário
             return this.buscaUsuarioLoginValido(login );
         }
         else
         {
             return gerente;
         }
      }
   
   
  /**
   * Busca um objeto usuario através do seu login;
   *
   * @param login
   * @return Gerente ou Usuario
   * @throws Exception
   */
    public Usuario buscarUsuarioLogin( String login )
       throws Exception
    {
      
       //tenta buscar um Gerente referente ao login
       Gerente gerente = buscaGerenteLogin( login );
       if( gerente == null )
       {
           //não sendo um gerente, tenta buscar um usuário
           return buscaUsuarioLogin(login );
       }
       else
       {
           return gerente;
       }
    }
    
    
   /**
    * Busca um objeto usuario através da sua matricula;
    *
    * @param chapa
    * @return Gerente ou Usuario
    * @throws Exception
    */
    public Usuario buscarUsuarioChapa( String chapa )
      throws Exception
    {
       //tenta buscar um Gerente referente ao login
       Gerente gerente = buscaGerenteChapa( chapa );
       if( gerente == null )
       {
           //não sendo um gerente, tenta buscar um usuário
           return buscaUsuarioChapa( chapa );
       }
       else
       {
           return gerente;
       }
    }
    
    /**
     * Busca um objeto usuario através do seu codigo;
     *
     * @param codigo
     * @return Gerente ou Usuario
     * @throws Exception
     */
    public Usuario buscarUsuarioCodigo( String codigo )
      throws Exception
    {
        //tenta buscar um Gerente referente ao login
        Gerente gerente = buscaGerenteCodigo( codigo );
        if( gerente == null )
        {
           //não sendo um gerente, tenta buscar um usuário
           return buscaUsuarioCodigo( codigo );
        }
        else
        {
           return gerente;
        }
    }
    
    
  /**
   * Busca uma colecao de usuarios validos.
   * 
   * @return uma colecao de objetos Usuarios 
   * @throws java.sql.SQLException
   */
    public Collection buscarUsuariosValidos()
      throws SQLException
    {
       LinkedList list = new LinkedList();
       
       Connection con = getConnection();
       String query = "SELECT * "+
                         "FROM USU_ELET "+
                         "WHERE FL_USU_ATI = 'S' "+
                         "ORDER BY NM_USU_CML";
       
       Statement statement = con.createStatement();
       ResultSet result = statement.executeQuery( query );
       while( result.next() )
       {
           Usuario usu = populateUsuario( result );
           usu.setGerente(this.buscaGerenteNome(result.getString("NM_USU_GER")));
           list.add( usu );
       }
       
       result.close();
       statement.close();
       con.close();
       
       return list;
    }
    
    
    /**
     * Busca uma colecao de todos os usuarios.
     * 
     * @return uma colecao de objetos Usuarios 
     * @throws java.sql.SQLException
     */
      public Collection buscarUsuarios()
        throws SQLException
      {
         LinkedList list = new LinkedList();
         
         Connection con = getConnection();
         String query = "SELECT * "+
                           "FROM USU_ELET "+
                           "ORDER BY NM_USU_CML";
         
         Statement statement = con.createStatement();
         ResultSet result = statement.executeQuery( query );
         while( result.next() )
         {
             Usuario usu = populateUsuario( result );
             usu.setGerente(this.buscaGerenteNome(result.getString("NM_USU_GER")));
             list.add( usu );
         }
         
         result.close();
         statement.close();
         con.close();
         
         return list;
      }
      
      
   /**
     *  retorna uma lista de todos os usuarios ativos relativos a um departamento. 
     *  Usuarios com gerentes nulos.
     *
     *  @param dept - departamento dos usuários
     *  @return java.util.Collection of Usuarios
     *  @throws SQLException
     */
    public Collection buscarUsuariosDept(String dept)
      throws SQLException
    {
        LinkedList list = new LinkedList();
        Connection conn = getConnection();
        Statement statement = conn.createStatement();

        String query = "SELECT * "+
                       "FROM USU_ELET "+
                       "WHERE NM_USU_DIV = '"+dept+"' "+
                       "AND FL_USU_ATI = 'S'";

        ResultSet result = statement.executeQuery( query );

        while(result.next())
        {
           Usuario usu = populateUsuario( result );
           
           list.add( usu );
        }

        result.close();
        statement.close();
        closeConnection(conn);

        return list;
    }
    
    
  /**
   * Busca na base de dados uma lista com os nomes das areas existentes
   * na usu_elet.
   * 
   * @return Collection of String
   * @throws java.lang.Exception
   */
    public Collection buscaUsuariosAreas()
      throws Exception
    {
       LinkedList areas = new LinkedList();
       
       Connection conn = null;
       String query = "SELECT distinct NM_USU_ARE "+
                        "FROM USU_ELET";
       try
       {
           conn = getConnection();
           Statement statement = conn.createStatement();
           ResultSet result = statement.executeQuery( query );
           
           while( result.next() )
           {
              String area = result.getString(1);
              areas.add( area ); 
           }
           
           result.close();
           statement.close();
       }
       catch(SQLException e)
       {
           e.printStackTrace();
           throw new Exception("Ocorreram erros ao se tentar buscar a lista de memorandos");
       }
       finally
       {
          try
          {
            if( conn != null )
               closeConnection( conn );
          }
          catch(SQLException ex)
          {
            ex.printStackTrace();
          }    
       }       
       
       return areas;
    }
    
    
    /**
     * Busca um gerente na base de dados representado pelo codigo do usuário.
     *
     * @param codigo
     */
    private Gerente buscaGerenteCodigo(String codigo)
      throws Exception
    {
        Gerente gerente = null;

        Connection conn = getConnection();
        Statement statement = conn.createStatement();
        String query = "SELECT * "+
                       "FROM USU_ELET "+
                       "WHERE NM_USU_GER = "+
                       "(SELECT CD_USU_LOG FROM USU_ELET "+
                       "WHERE CD_USU = '"+codigo+"')";

        ResultSet result = statement.executeQuery( query );
        boolean isGerente = false;
        if(result.next())
        {
           isGerente = true;
        }
        result.close();
        statement.close();
        closeConnection( conn );


        if( isGerente )
        {
           Usuario usu = buscaUsuarioCodigo( codigo );

           gerente = new Gerente(usu.getCodigo(), usu.getLogin(), null);
           gerente.setNome(usu.getNome());
           gerente.setDep( usu.getDep());
           gerente.setArea( usu.getArea() );
           gerente.setEmail( usu.getEmail() );
           gerente.setChapa( usu.getChapa());
           gerente.setGerente( usu.getGerente() );
           gerente.setRamal( usu.getRamal() );
        }

        return gerente;
    }


    /**
     *  Busca um usuário na base de dados representado pela  chapa
     *
     *  @param codigo
     *  @return Usuario
     *  @throws Exception
     */
    private Usuario buscaUsuarioCodigo(String codigo)
       throws Exception
    {
        Usuario usuario = null;
        String nomeGerente = null;

        //Busca informações sobre o usuário
        Connection conn = getConnection();
        Statement statement = conn.createStatement();

        String query = "SELECT * "+
                       "FROM USU_ELET "+
                       "WHERE CD_USU = '"+codigo+"'";

        ResultSet result = statement.executeQuery( query );

        if(result.next())
        {
          nomeGerente = result.getString("NM_USU_GER");
          usuario = populateUsuario( result );
        }
        else
        {
           result.close();
           statement.close();
           closeConnection(conn);
           throw new Exception("usuario inválido");
        }

        result.close();
        statement.close();
        closeConnection( conn );

        usuario.setGerente(this.buscaGerenteNome(nomeGerente));

        return usuario;
    }


    /**
     * Busca um gerente na base de dados representado pela chapa do usuário.
     *
     * @param chapa
     */
    private Gerente buscaGerenteChapa(String chapa)
      throws Exception
    {
        Gerente gerente = null;

        Connection conn = getConnection();
        Statement statement = conn.createStatement();
        String query = "SELECT * "+
                       "FROM USU_ELET "+
                       "WHERE NM_USU_GER = "+
                       "(SELECT CD_USU_LOG FROM USU_ELET "+
                       "WHERE CD_USU_MAT = '"+chapa+"')";

        ResultSet result = statement.executeQuery( query );
        boolean isGerente = false;
        if(result.next())
        {
           isGerente = true;
        }
        result.close();
        statement.close();
        closeConnection( conn );


        if( isGerente )
        {
           Usuario usu = buscaUsuarioChapa(chapa );

           gerente = new Gerente(usu.getCodigo(), usu.getLogin(), null);
           gerente.setNome(usu.getNome());
           gerente.setDep( usu.getDep());
           gerente.setArea( usu.getArea() );
           gerente.setEmail( usu.getEmail() );
           gerente.setChapa( usu.getChapa());
           gerente.setGerente( usu.getGerente() );
           gerente.setRamal( usu.getRamal() );
        }

        return gerente;
    }


    /**
     *  Busca um usuário na base de dados representado pela  chapa
     *
     *  @param chapa
     *  @return Usuario
     *  @throws Exception
     */
    private Usuario buscaUsuarioChapa(String chapa)
       throws Exception
    {
        Usuario usuario = null;
        String nomeGerente = null;

        //Busca informações sobre o usuário
        Connection conn = getConnection();
        Statement statement = conn.createStatement();

        String query = "SELECT * "+
                       "FROM USU_ELET "+
                       "WHERE CD_USU_MAT = '"+chapa+"'";

        ResultSet result = statement.executeQuery( query );

        if(result.next())
        {
          nomeGerente = result.getString("NM_USU_GER");
          usuario = populateUsuario( result );
        }
        else
        {
           result.close();
           statement.close();
           closeConnection(conn);
           throw new Exception("usuario inválido");
        }

        result.close();
        statement.close();
        closeConnection( conn );

        usuario.setGerente(this.buscaGerenteNome(nomeGerente));

        return usuario;
    }


    /**
     * Busca um gerente na base de dados representado pelo login.
     *
     * @param login
     * @return Gerente
     */
    private Gerente buscaGerenteLogin(String login)
      throws Exception
    {
        Gerente gerente = null;

        Connection conn = getConnection();
        Statement statement = conn.createStatement();
        String query = "SELECT * "+
                       "FROM USU_ELET "+
                       "WHERE NM_USU_GER = '"+login.toUpperCase()+"'";

        ResultSet result = statement.executeQuery( query );
        boolean isGerente = false;

        if(result.next())
        {
           isGerente = true;
        }
        result.close();
        statement.close();
        closeConnection( conn );

        if( isGerente )
        {
           Usuario usu = buscaUsuarioLogin(login );
           gerente = new Gerente(usu.getCodigo(), usu.getLogin(), null);
           gerente.setNome(usu.getNome());
           gerente.setDep( usu.getDep());
           gerente.setArea( usu.getArea() );
           gerente.setEmail( usu.getEmail() );
           gerente.setChapa( usu.getChapa());
           gerente.setGerente( usu.getGerente() );
           gerente.setRamal( usu.getRamal());
        }

        return gerente;
    }


    /**
     *  Busca um usuário valido na base de dados representado pelo login
     *  @param login
     *  @return Usuario
     *  @throws Exception
     */
    private Usuario buscaUsuarioLoginValido(String login )
       throws Exception
    {
        Usuario usuario = null;
        String nomeGerente = null;

        //Busca informações sobre o usuário
        Connection conn = getConnection();
        Statement statement = conn.createStatement();

        String query = "SELECT * "+
                       "FROM USU_ELET "+
                       "WHERE CD_USU_LOG = '"+login.toUpperCase() +"' and FL_USU_ATI='S'";

        ResultSet result = statement.executeQuery( query );

        if(result.next())
        {
          nomeGerente = result.getString("NM_USU_GER");
          usuario = populateUsuario( result );
        }
        else
        {
           result.close();
           statement.close();
           closeConnection(conn);
           throw new Exception("usuario inválido");
        }

        result.close();
        statement.close();
        closeConnection( conn );

        usuario.setGerente(this.buscaGerenteNome(nomeGerente));
        return usuario;
    } // end buscarCodigoUsuario


     /**
      *  Busca um usuário na base de dados representado pelo login
      *  @param login
      *  @return Usuario
      *  @throws Exception
      */
     private Usuario buscaUsuarioLogin(String login )
        throws Exception
     {
         Usuario usuario = null;
         String nomeGerente = null;

         //Busca informações sobre o usuário
         Connection conn = getConnection();
         Statement statement = conn.createStatement();

         String query = "SELECT * "+
                        "FROM USU_ELET "+
                        "WHERE CD_USU_LOG = '"+login.toUpperCase() +"'";

         ResultSet result = statement.executeQuery( query );

         if(result.next())
         {
           nomeGerente = result.getString("NM_USU_GER");
           usuario = populateUsuario( result );
         }
         else
         {
            result.close();
            statement.close();
            closeConnection(conn);
            throw new Exception("usuario inválido");
         }

         result.close();
         statement.close();
         closeConnection( conn );

         usuario.setGerente(this.buscaGerenteNome(nomeGerente));
         return usuario;
     } // end buscarCodigoUsuario
     
     
    /**
     * Cria um usuario apartir de um obejto ResultSet
     * @param result
     * @return Usuario
     * @throws SQLException
     */
    private Usuario populateUsuario( ResultSet result )
      throws SQLException
    {
        Usuario usuario = new Usuario(result.getInt( "CD_USU" ),
                     result.getString("CD_USU_LOG"), null);
        usuario.setDep(result.getString( "NM_USU_DIV" ));
        usuario.setArea( result.getString( "NM_USU_ARE" ) );
        usuario.setEmail(result.getString( "DE_USU_CEN" ));
        usuario.setNome(result.getString("NM_USU_CML"));
        usuario.setChapa(result.getString("CD_USU_MAT"));
        usuario.setRamal(result.getString("NU_USU_RAM"));
        String flag = result.getString("FL_USU_ATI");
        if( flag.equals("S"))
           usuario.setValido(true);
        else
           usuario.setValido(false);
           
        java.sql.Date data = result.getDate("DT_USU_NAS");
        if( data != null )
           usuario.setDataNascimento( new DataEletros(data.getTime()));
        
        return usuario;
    }


    /**
     * Busca um gerente pelo nome.
     * @param nomeGerente
     * @return Gerente
     * @throws SQLException
     */
    private Gerente buscaGerenteNome(String nomeGerente)
      throws SQLException
    {
        Gerente gerente = null;

        //busca informações sobre o gerente do usuário
        if(( nomeGerente != null)&&(!nomeGerente.equals("")))
        {
           Connection conn = getConnection();
           Statement statement = conn.createStatement();

           String query = "SELECT CD_USU, DE_USU_CEN "+
                          "FROM USU_ELET "+
                          "WHERE CD_USU_LOG = '"+nomeGerente.toUpperCase()+"'";

           ResultSet resultGerente = statement.executeQuery( query );

           if(resultGerente.next())
           {
              int codigoGerente = resultGerente.getInt( "CD_USU" );
              gerente = new Gerente( codigoGerente, nomeGerente, null);
              String emailGerente = resultGerente.getString( "DE_USU_CEN" );
              gerente.setEmail( emailGerente );
              gerente.setNome( nomeGerente);

           }

           resultGerente.close();
           statement.close();
           closeConnection( conn );
        }

        return gerente;
    }

   
   /**
     * Cadastra ou atualiza um usuario
     * @param usu - Obejt Usuario
     * @throws Exception
     */
   public void persistirUsuario( Usuario usu )
     throws Exception
   {
      if( usu.getCodigo() > 0 )
         atualizarUsuario( usu );
      else
         inserirUsuario( usu );
   }
   
   
  /**
   * insere um novo usuario.
   * 
   * @param usu - usuario
   * @throws java.sql.SQLException
   */
    private void inserirUsuario( Usuario usu )
      throws Exception
    {
        Connection conn = null;
        Statement statement = null;
        ResultSet result = null;
        
        try
        {
          conn = getConnection();
          statement = conn.createStatement();
        
          //verifica se o login ja existe
          String query = "SELECT CD_USU_LOG FROM USU_ELET WHERE CD_USU_LOG = '"+
                                      usu.getLogin()+"'";
          result = statement.executeQuery( query );
          if( result.next() )
          {
             throw new Exception("Login do usuáio já existe");  
          }
          
          //busca o ultimo codigo de usuario cadstrado
          query = "SELECT MAX(CD_USU) from usu_elet";
          result = statement.executeQuery( query );
          int number = 1;
          if( result.next() )
          {
             number = result.getInt(1);
             number++;
          }
          
          //insere o usuario na base
         String query2 = "INSERT INTO USU_ELET(CD_USU_LOG,NM_USU_DIV,CD_USU,NM_USU_CML,NM_USU_GER,DE_USU_CEN,NM_USU_ARE,CD_USU_MAT,FL_USU_ATI,DT_USU_NAS,NU_USU_RAM) "+ 
                       "VALUES('"+usu.getLogin()+
                       "','"+usu.getDep().trim()+
                       "',"+number+
                       ",'"+usu.getNome()+
                       "','"+usu.getGerente().getLogin().trim()+
                       "','"+usu.getEmail()+
                       "','"+usu.getArea()+"',";
         if((usu.getChapa() != null)&&(!usu.getChapa().equals(""))) {
             query2 = query2 + "'"+usu.getChapa()+"',"; 
         }
         else{
             query2 = query2 + "NULL,";
         }
            query2 = query2 + "'S',"+
                       "to_date('"+usu.getDataNascimento().toString()+"','dd/mm/yyyy'),";
                       
         if((usu.getRamal() != null)&&(!usu.getRamal().equals(""))) {
                query2 = query2 + "'"+usu.getRamal()+"')"; 
         }
         else{
                query2 = query2 + "NULL)";
         }
                                
          System.out.println( query2 ); 
          statement.executeUpdate( query2 );
        }
        catch(SQLException e)
        {
            e.printStackTrace();
            throw new Exception(e.getMessage());
        }
        finally
        {
            try
            {
              result.close();
              statement.close();
              conn.close();
            }
            catch(SQLException e)
            {
              e.printStackTrace();
              throw new Exception("Erro ao fechar statement e conexao");
            }
        }
    }
    
    
  /**
   * atualiza os campos do usuario.
   * 
   * @param usu - usuario
   * @throws java.sql.SQLException
   */
    private void atualizarUsuario( Usuario usu )
      throws Exception
    {
        Connection conn = null;
        Statement statement = null;
        char flag = 'S';
        if( !usu.isValido() )
          flag = 'N';
        
        try
        {
          conn = getConnection();
          
          statement = conn.createStatement();
          
          String query = null;
          
          //atualiza a tabela de ferias AGD_ELET_FER  
          if((usu.getChapa() != null)&&(!usu.getChapa().equals(""))) 
          {
              //verifica se a nova matricula do usuario já existe
              query = "SELECT CD_USU_LOG, CD_USU_MAT FROM USU_ELET WHERE CD_USU_MAT = '"+
                                          usu.getChapa().trim()+"'";
              ResultSet result = statement.executeQuery( query );
              if( result.next() )
              {
                 String tempLog = result.getString(1);
                 if( !tempLog.trim().equals(usu.getLogin().trim()))
                   throw new Exception("Matrícula do usuáio já existente");  
              }
             
             query = "UPDATE AGD_ELET_FER A SET A.CD_USU_MAT = '"+usu.getChapa().trim()+"' "+ 
                             "WHERE A.CD_USU_MAT = (SELECT U.CD_USU_MAT FROM USU_ELET U "+
                                                         "WHERE U.CD_USU_LOG = '"+usu.getLogin().trim()+"')";  
             statement.executeUpdate( query ); 
            
          } 
          
           query = "UPDATE USU_ELET "+ 
                       "SET NM_USU_CML='"+usu.getNome()+
                       "',NM_USU_ARE='"+usu.getArea().trim()+
                       "',NM_USU_DIV='"+usu.getDep().trim()+
                       "',DE_USU_CEN='"+usu.getEmail().trim()+"',";
            if((usu.getChapa() != null)&&(!usu.getChapa().equals(""))) {
                query = query + "CD_USU_MAT='"+usu.getChapa()+"',"; 
            }
            if((usu.getRamal() != null)&&(!usu.getRamal().equals(""))) {
                   query = query + "NU_USU_RAM='"+usu.getRamal()+"',"; 
            }
                query = query +"NM_USU_GER='"+usu.getGerente().getLogin().trim()+
                       "',FL_USU_ATI='"+flag+
                       "',DT_USU_NAS=to_date('"+usu.getDataNascimento().toString()+"','dd/mm/yyyy') "+
                       "WHERE CD_USU_LOG = '"+usu.getLogin().trim()+"'";
                       
          System.out.println( query ); 
          statement.executeUpdate( query );
          
          
        }
        catch(SQLException e)
        {
            e.printStackTrace();
            throw new Exception(e.getMessage());
        }
        finally
        {
            try
            {
              statement.close();
              conn.close();
            }
            catch(SQLException e)
            {
              e.printStackTrace();
              throw new Exception("Erro ao fechar statement e conexao");
            }
        }
    }
    
    
    /**
     *  retorna uma conexão do pool de conexão
     *  @returns java.sql.Connection
     *  @throws SQLException
     */
    private Connection getConnection()
       throws SQLException
    {
       Connection conn = null;
       try
       {
             InitialContext initContext = new InitialContext();
             //Context envContext  = (Context)initContext.lookup("java:/comp/env");
             DataSource ds = (DataSource)initContext.lookup("java:/jdbc/OracleLogin");
             conn = ds.getConnection();
       }
       catch(NamingException ne)
       {
          System.out.println(ne.toString());
       }
       System.out.println("Obtendo conexao");
       return conn;
    }

    /**
     *  devolve a conexão para o pool
     *  @throws SQLException
     */
    private void closeConnection(Connection conn)
      throws SQLException
    {
       conn.close();
       System.out.println("Conexão Fechada");
    }

}



