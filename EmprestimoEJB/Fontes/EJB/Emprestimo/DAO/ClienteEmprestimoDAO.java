package EJB.Emprestimo.DAO;

import java.sql.Connection;
import java.sql.*;
import EJB.Emprestimo.*;

import com.eletros.benef.Patrocinadora;

import javax.naming.InitialContext;
import javax.naming.NamingException;

import javax.sql.*;



public class ClienteEmprestimoDAO {
    public ClienteEmprestimoDAO() {
    }
    
    
    public ClienteEmprestimo buscaClienteEmprestimo(String numEletros) 
      throws Exception
    {
        ClienteEmprestimo cliente = new ClienteEmprestimo();
        Connection conn = null;
        
        try
        {
          conn = getConnection();
          
            //buscando o nsu                    
            CallableStatement callStatement = conn.prepareCall(
               "{call O_EPT.BUSCANSU( ?,?,?,?,? ) }"
            );
            
            callStatement.setString(1, numEletros);
            
            callStatement.registerOutParameter (2, Types.FLOAT);                         
            callStatement.registerOutParameter (3, Types.VARCHAR);
            callStatement.registerOutParameter (4, Types.FLOAT);
            callStatement.registerOutParameter (5, Types.VARCHAR);
            
            callStatement.executeUpdate();
            
            String erro = callStatement.getString(5);
            String nome = callStatement.getString(3);
            if( nome == null )
            {
               System.out.println("Erro gerado pelo proc "+ erro );
               throw new Exception("O numero "+numEletros+" nao e um cliente emprestimo");
               
            }
            Long n = callStatement.getLong(4);
            Patrocinadora pat = new Patrocinadora();
            pat.setCodigo(n.intValue());
            cliente.setPatrocinadoraAtual(pat);
            //cliente.setDataInscricao();
            //cliente.setDtNascimento();
            //cliente.setEmail();
            cliente.setNome(callStatement.getString(3));
            cliente.setNsu(callStatement.getLong(2));
            cliente.setNumEletros( numEletros );
            //cliente.setSalarioAtual();
          cliente.setTipo("PF");
          //cliente.setUsuario();
              
          callStatement.close();
            
          return cliente;
        }
        catch(SQLException e) {
            e.printStackTrace();
            throw new Exception("Ocorreram poblemas ao buscar o cliente de empr�stimo");
        }
        finally {
            conn.close();
        }
        
    }
    
    
    /**
     * Obtem uma conex�o do pool de conex�es
     */
    private Connection getConnection()
       throws SQLException
    {
       Connection conn = null;
        try 
        {                
              InitialContext initContext = new InitialContext();         
              //Context envContext  = (Context)initContext.lookup("java:/comp/env");             
              DataSource ds = (DataSource)initContext.lookup("java:/jdbc/OracleCompEmprestimo");          
              conn = ds.getConnection();         
        }
        catch(NamingException ne)
        {                        
           System.out.println(ne.toString());            
        }
        return conn;
    }
}
