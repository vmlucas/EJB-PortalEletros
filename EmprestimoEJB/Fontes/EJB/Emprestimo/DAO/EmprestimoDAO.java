package EJB.Emprestimo.DAO;

import EJB.Emprestimo.*;

import com.util.DataEletros;

import java.util.*;
import java.sql.*;
import javax.naming.*;
import javax.sql.DataSource;


/**
 * Classe q acessa a base de dados para obter informa��es sobre emprestimos
 * financeiros relativos a um participante.
 */
public class EmprestimoDAO 
{
    private ClienteEmprestimo cliente;
    private String modalidadeEmprestimo;
    
    public EmprestimoDAO( ClienteEmprestimo cliente) 
    {
       this.cliente = cliente;
    }
    

      /**
       *  Busca todos os emprestimos concedidos ao participante. 
       *  Utiliza a tabela CNO_EPT.
       *  
       *  @return Collection of Emprestimo
       */
      public Collection buscarEmprestimos()
        throws SQLException
      {
         Connection conn = null;
         LinkedList emprestimos = new LinkedList();
         
         conn = getConnection();
            
         String query = "SELECT * FROM CNO_EPT "+
                           "WHERE NU_ICR='"+cliente.getNumEletros()+"' "+ 
                           "ORDER BY DT_CNO_SOL DESC";

         Statement statement = conn.createStatement();
         ResultSet result = statement.executeQuery( query );

         while ( result.next() )
         {
            Emprestimo emprestimo = new Emprestimo( cliente );
            emprestimo.setCodigo( result.getString( "CD_CNO" ) );              
            emprestimo.setCodigoAnterior( result.getString( "CD_CNO_ANT"));
            emprestimo.setDataLiberacao( new DataEletros( result.getDate( "DT_CNO_LBE" ).getTime()) );
            emprestimo.setDataConcessaoSaldo( new DataEletros( result.getDate( "DT_CNO_SLD_DVR" ).getTime()) );
            emprestimo.setValorSaldo( result.getDouble( "VA_CNO_SLD_DVR" ));
            
            emprestimos.add( emprestimo );
         }
         
         conn.close();
         
         return emprestimos;
      }

      
    /**
      * Busca o Emprestimo atual do cliente
      */
      public Emprestimo buscarEmprestimoAtual()
        throws Exception
      {
         Connection conn = null;
         Statement statement = null;
         ResultSet result = null;
          
         Emprestimo emprestimo = null;
         System.out.println("Nsu: "+cliente.getNsu());
         
         try
         {
           conn = getConnection();
         
           String query = "SELECT CD_CNO, CD_CNO_ANT, DT_CNO_LBE, DT_CNO_SLD_DVR, VA_CNO_SLD_DVR "+
                               "FROM CNO_EPT "+
                               "WHERE NU_ICR = '"+cliente.getNumEletros()+"' "+
                                     "AND DT_CNO_LBE = "+
                                        "(SELECT MAX(DT_CNO_LBE) "+
                                                "FROM CNO_EPT "+
                                                "WHERE NU_ICR = '"+cliente.getNumEletros()+"')";
              
           statement = conn.createStatement();
           result = statement.executeQuery( query );
             
           if( result.next() )
           {
               emprestimo = new Emprestimo( cliente );
               emprestimo.setCodigo( result.getString( "CD_CNO" ) );
               emprestimo.setCodigoAnterior( result.getString( "CD_CNO_ANT"));
               emprestimo.setDataLiberacao( new DataEletros( result.getDate( "DT_CNO_LBE" ).getTime()) );
               emprestimo.setDataConcessaoSaldo( new DataEletros( result.getDate( "DT_CNO_SLD_DVR" ).getTime()) );
               emprestimo.setValorSaldo( result.getDouble( "VA_CNO_SLD_DVR" ));
                
                
           }
           else
           {
              throw new Exception("Cliente sem empr�stimo");
           }
           
           return emprestimo;
         }
         catch(SQLException e) {
             e.printStackTrace();
             throw new Exception("Ocorreram problemas ao buscar o empr�stimo atual");
         }
         finally
         {
           if( statement != null)
             statement.close();
           if( result != null)
             result.close();
           conn.close();
         }
         
      }

     
    /**
       * busca um objeto ConcessaoEmprestimo relativo ao participante
       */
      public ConcessaoEmprestimo buscarConcessaoEmprestimo( ) 
        throws Exception
      {
         
         ConcessaoEmprestimo concessao = new ConcessaoEmprestimo( cliente );
          
         Connection conn = getConnection();
         
         String query = "SELECT M.CD_MOD, E.QT_VCL_DIA, E.QT_VCL_RMN_MIN, E.QT_VCL_RMN_MAX, E.VA_VCL_LIM_MIN, E.VA_VCL_LIM_MAX "+
                        "FROM VCL_EPT E, MOD_EPT M "+
                        "WHERE E.CD_PAT = "+cliente.getPatrocinadoraAtual().getCodigo()+" "+
                        "AND E.CD_MOD = M.CD_MOD "+
                        "AND M.CD_PAT = "+cliente.getPatrocinadoraAtual().getCodigo()+" "+
                        "AND M.DT_MOD_REF = "+
                              "(SELECT MAX(DT_MOD_REF) FROM MOD_EPT M2 "+
                              "WHERE M2.CD_PAT = "+cliente.getPatrocinadoraAtual().getCodigo()+" "+
                              "AND M2.CD_EPT = 'EF') "+
                        "AND M.CD_EPT = 'EF'";      

         Statement statement = conn.createStatement();
         ResultSet result = statement.executeQuery( query );

         if( result.next() )
         {
               modalidadeEmprestimo = result.getString("CD_MOD");
               concessao.setValorMaximoRemuneracoes( result.getDouble( "QT_VCL_RMN_MAX" ));
               concessao.setValorMinimoRemuneracoes( result.getDouble( "QT_VCL_RMN_MIN" ));
               concessao.setValorMaximoEmprestimo( result.getDouble( "VA_VCL_LIM_MAX" ));
               concessao.setCarenciaMinima( result.getInt( "QT_VCL_DIA" ));
         }

         conn.close();
       
       //busca saldo devedor, caso o emprestimo tenha sido liberado
       Emprestimo emprestimoAtual = buscarEmprestimoAtual();
       
       if(emprestimoAtual.getDataLiberacao() != null )
       {
          concessao.setSaldoDevedor( buscaSaldoDevedor( emprestimoAtual ) );
       } 
       concessao.setTaxaJuros( this.buscarTaxaJuros() );
       concessao.setTaxaConcessao( this.buscarTxConcessao());
       concessao.setTaxaRenovacao( this.buscarTxRenovacao());
        
       return concessao;
    }
      
    
    /**
      * Busca o saldo devedor do participante
      */
      public double buscaSaldoDevedor(Emprestimo emprestimo )
        throws SQLException
     {
         double saldoDevedor = 0.0;
         Connection conn = null;

         conn = getConnection();

         CallableStatement callStatement = conn.prepareCall(
                "{call O_EPT.PKG_EPT.prcalculaslddvr( ?,?,?,?,?,?,?,?,?,? ) }"
             );
         long dataMilis = System.currentTimeMillis();
         java.sql.Date data = new java.sql.Date( dataMilis); 
    
         System.out.println("nsu "+cliente.getNsu());
         System.out.println("data inicial "+emprestimo.getDataLiberacao());
         System.out.println("data final "+data);
         System.out.println("Emprestimo "+emprestimo.getCodigo());
             
         callStatement.setInt(1, Integer.parseInt(cliente.getNsu().toString()));
         callStatement.setString(2,"EF");
         callStatement.setString(3, emprestimo.getCodigo());
         callStatement.setString(4, emprestimo.getCodigo());
         callStatement.setDate(5, data );
         callStatement.setDate(6, 
                   new java.sql.Date(emprestimo.getDataConcessaoSaldo().getTime()));
         callStatement.setDouble(7, emprestimo.getValorSaldo());
         callStatement.registerOutParameter (8, Types.FLOAT);                         
         callStatement.registerOutParameter (9, Types.FLOAT);
         callStatement.registerOutParameter (10, Types.VARCHAR);
             
         callStatement.executeUpdate();
             
         String erro = callStatement.getString(10);
         if(( erro != null )&&(!erro.equals("")))
         {
             System.out.println("Erro gerado pelo proc "+ erro );
             throw new SQLException("Ocorreram erros ao se tentar encontrar o saldo devedor");
                
         }
         
         double juros = callStatement.getDouble(8);
         saldoDevedor = emprestimo.getValorSaldo() + juros;
             
         callStatement.close();
         conn.close();
        return saldoDevedor; 
    }
      
    
    /**
     * Busca o IOF na base de dados para uma simula��o de empr�stimo.
     */
    public double buscarIOFSimulacao( double valorSolicitado) 
      throws Exception
    {
          double iof = 0.0;
          Connection conn = null;
          double descontoMinimo = this.buscarValorMinimoDesconto();
          
          conn = getConnection();

              /**
               * buscando a modalidade do empr�stimo
               */
              String query = "select cd_mod from mod_ept m1 where m1.cd_pat ="+cliente.getPatrocinadoraAtual().getCodigo()+" and m1.dt_mod_ref = "+ 
                                   "(select max(m2.dt_mod_ref) from mod_ept m2 where m2.cd_pat = m1.cd_pat)";
              String cdMod = "";                     
              Statement statement = conn.createStatement();
              ResultSet result = statement.executeQuery( query );
              if(result.next())
              {
                 cdMod = result.getString("cd_mod");  
              }
              result.close();
              statement.close();
              
              //verifica se modelo de emp. � vazio
              if( cdMod.equals(""))
              {
                 throw new SQLException("cliente sem modalidade de empr�stimo");
              }
              
              
              //calculando o IOF                     
              CallableStatement callStatement = conn.prepareCall(
                 "{call O_EPT.PKG_IOF.PR_CALCULAIOF( ?,?,?,?,?,?,?,?,?,?,?,?,? ) }"
              );
              long dataMilis = System.currentTimeMillis();
              java.sql.Date dataAtual = new java.sql.Date( dataMilis); 
     
              System.out.println("C�lculo IOF nsu "+cliente.getNsu());
              System.out.println("data final "+dataAtual);
              System.out.println("valor solicitado "+valorSolicitado);
              System.out.println("desconto minimo "+descontoMinimo);
              
              callStatement.setInt(1, Integer.parseInt(cliente.getNsu().toString()));
              callStatement.setString(2, "2004/02195");
              callStatement.setInt(3,(int)cliente.getPatrocinadoraAtual().getCodigo());
              callStatement.setString(4, cdMod);
              callStatement.setString(5, "EF");
              callStatement.setDate(6, dataAtual);
              callStatement.setDate(7, dataAtual );
              callStatement.setDouble(8, valorSolicitado );
              callStatement.setDouble(9, descontoMinimo );
              callStatement.setDouble(10, 8.0 );
              callStatement.setDouble(11, 0.16 );
              
              callStatement.registerOutParameter (12, Types.FLOAT);                         
              callStatement.registerOutParameter (13, Types.VARCHAR);
              
              callStatement.executeUpdate();
              
              String erro = callStatement.getString(13);
              if(( erro != null )&&(!erro.equals("")))
              {
                 System.out.println("Erro gerado pelo proc "+ erro );
                 throw new SQLException("Ocorreram erros ao se tentar encontrar o IOF");
                 
              }
              iof = callStatement.getDouble(12);
              
              callStatement.close();
              conn.close();
    
         return iof;
    }
       
       
    /**
       * Busca o percentual de desconto do empr�stimo referente a um
       * participante e a uma modalidade de emprestimo
       */
      public double buscarValorMinimoDesconto()
        throws Exception
      {
          double desconto = 0.0;
          
          if( modalidadeEmprestimo == null )
              buscarConcessaoEmprestimo( );
              
          Connection con = getConnection();
          String query = "SELECT VA_MOD_PCT_DED_MIN "+
                           "FROM MOD_EPT "+
                           "WHERE CD_MOD = '"+modalidadeEmprestimo+"' "+
                           "AND CD_PAT = "+cliente.getPatrocinadoraAtual().getCodigo();

          Statement statement = con.createStatement();
          ResultSet result = statement.executeQuery( query );

          if(result.next())
          {
            desconto = result.getDouble( "VA_MOD_PCT_DED_MIN" );
          }

          result.close();
          statement.close();
          con.close();
    
       return desconto;
    }
      
      
    /**
     * ################### METODOS PARA OBTER TAXAS PARA SIMULA��O DE EMPRESTIMOS########
     */
     /**
        * Busca a taxa de juros relativa a um participante e sua patrocinadora
        * 
        * @return double
        */
       private double buscarTaxaJuros()
         throws SQLException
       {
           double valorJuros = 0.0;
             
           Connection conn = getConnection();

           //caso o participante j� tenha pego empr�stimos
           String query = "SELECT H.VA_HIS_IND, I.DE_IND, H.DT_HIS_REF "+ 
                              "FROM MOD_EPT M, HIS_IND H, IND_IND I, his_ept_mod his, cno_ept cno "+ 
                              "WHERE his.cd_pat = m.cd_pat "+
                                    "AND his.cd_mod = m.cd_mod "+
                                    "AND cno.cd_cno = his.cd_cno "+
                                    "and cno.nu_icr = "+cliente.getNumEletros()+" "+
                                    "and cno.dt_cno_lbe = ( select max(cno2.dt_cno_lbe) from cno_ept cno2 "+ 
                                                                   "where cno2.nu_icr = cno.nu_icr) "+
                                    "AND M.CD_IND = H.CD_IND "+ 
                                    "AND M.CD_EPT = 'EF' "+ 
                                    "AND M.DT_MOD_REF = "+ 
                                         "(SELECT MAX(DT_MOD_REF) FROM MOD_EPT M2 "+ 
                                                  "WHERE M2.CD_PAT = M.CD_PAT "+
                                                  "AND m2.cd_mod = m.cd_mod "+
                                                  "AND M2.CD_EPT = 'EF') "+
                                    "AND H.TS_HIS = (Select max(ts_his) from his_ind H2 "+  
                                                                        "where H.cd_ind = H2.CD_IND) "+
                                    "AND H.CD_IND = I.CD_IND "+
                                    "ORDER BY m.CD_PAT, m.CD_MOD DESC";

             Statement statement = conn.createStatement();
             ResultSet result = statement.executeQuery( query );
             System.out.println("Pat: "+cliente.getPatrocinadoraAtual().getCodigo());
             
             if( result.next() )
             {
                valorJuros = result.getDouble(1);
                System.out.println("j� tem empr�stimos Indice "+result.getString(2));
                System.out.println("Data "+result.getString(3));
               
             }
             else
             {
               result.close();
               statement.close();
               
               //caso o participante n�o tenho pego empr�stimo
               String query2 = "SELECT H.VA_HIS_IND, I.DE_IND, H.DT_HIS_REF "+ 
                                      "FROM MOD_EPT M, HIS_IND H, IND_IND I "+  
                                      "WHERE m.cd_pat = "+cliente.getPatrocinadoraAtual().getCodigo()+" "+
                                      "AND M.CD_IND = H.CD_IND "+ 
                                      "AND M.CD_EPT = 'EF' "+ 
                                      "AND M.DT_MOD_REF = "+ 
                                           "(SELECT MAX(DT_MOD_REF) FROM MOD_EPT M2 "+ 
                                                    "WHERE M2.CD_PAT = M.CD_PAT "+
                                                          "AND M2.CD_EPT = 'EF') "+
                                      "AND H.TS_HIS = (Select max(ts_his) from his_ind H2 "+  
                                                                     "where H.cd_ind = H2.CD_IND) "+
                                      "AND H.CD_IND = I.CD_IND"; 
               
               statement = conn.createStatement();
               result = statement.executeQuery( query2 );
               if( result.next() )
               { 
                   valorJuros = result.getDouble(1);
                   System.out.println("n�o tem empr�stimos Indice "+result.getString(2));
                   System.out.println("Data "+result.getString(3));
               }

               result.close();
               statement.close();
             }
             
           conn.close(); 
          
           return valorJuros;

       }
   
   
    /**
       * Retorna a taxa de concess�o para o empr�stimo.
       */
      private double buscarTxConcessao()
        throws SQLException
      {
         double valor = 0.0;
         
            Connection conn = this.getConnection();

            String query = "SELECT ENC.VA_ENC "+
                           "FROM ENC_EPT ENC, MOD_EPT M, ICD_EPT_ENC ICD "+
                           "WHERE M.CD_PAT = "+cliente.getPatrocinadoraAtual().getCodigo()+" "+
                           "AND M.CD_PAT = ICD.CD_PAT "+
                           "AND M.CD_MOD = ICD.CD_MOD "+
                           "AND M.CD_EPT = ICD.CD_EPT "+
                           "AND ICD.CD_ENC = ENC.CD_ENC "+                       
                           "AND M.DT_MOD_REF = "+
                              "(SELECT MAX(DT_MOD_REF) FROM MOD_EPT M2 "+
                                       "WHERE M2.CD_PAT = "+cliente.getPatrocinadoraAtual().getCodigo()+" "+
                                        "AND M2.CD_EPT = 'EF') "+
                           "AND ICD.CD_ENC = 'TXC'";
                           
            Statement statement = conn.createStatement();
            ResultSet result = statement.executeQuery( query );
            if( result.next() )
            { 
              valor = result.getDouble(1);
            }

            result.close();
            statement.close();
            conn.close();
            
          return valor;

      }
      
      
      /**
       * Retorna a taxa de renovacao para o empr�stimo.
       */
      private double buscarTxRenovacao()
        throws SQLException
      {
         double valor = 0.0;
         
            Connection conn = this.getConnection();

            String query = "SELECT ENC.VA_ENC "+
                           "FROM ENC_EPT ENC, MOD_EPT M, ICD_EPT_ENC ICD "+
                           "WHERE M.CD_PAT = "+cliente.getPatrocinadoraAtual().getCodigo()+" "+
                           "AND M.CD_PAT = ICD.CD_PAT "+
                           "AND M.CD_MOD = ICD.CD_MOD "+
                           "AND M.CD_EPT = ICD.CD_EPT "+
                           "AND ICD.CD_ENC = ENC.CD_ENC "+                       
                           "AND M.DT_MOD_REF = "+
                              "(SELECT MAX(DT_MOD_REF) FROM MOD_EPT M2 "+
                                       "WHERE M2.CD_PAT = "+cliente.getPatrocinadoraAtual().getCodigo()+" "+
                                        "AND M2.CD_EPT = 'EF') "+
                           "AND ICD.CD_ENC = 'TXR'";
                           
            Statement statement = conn.createStatement();
            ResultSet result = statement.executeQuery( query );
            if( result.next() )
            { 
              valor = result.getDouble(1);
            }

            result.close();
            statement.close();
            conn.close();
            
        return valor;

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
