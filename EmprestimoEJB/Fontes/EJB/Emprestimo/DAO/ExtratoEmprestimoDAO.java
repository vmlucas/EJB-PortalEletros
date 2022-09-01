package EJB.Emprestimo.DAO;

import EJB.Emprestimo.*;
import com.util.DataEletros;
import java.sql.*;
import java.util.*;
import java.text.*;
import javax.naming.*;
import javax.sql.DataSource;

/**
 * Classe responsï¿½vel em acessar a base de dados e buscar o
 * extrato de emprï¿½stimo financeiro relativo a um participante e um emprestimo
 * especifico desse participante.
 * Utiliza o padrï¿½o SINGLETON - que permite a existencia de apenas
 * uma ï¿½nica instancia da classe, para q com isso o metodo q gera o extrato
 * possa ter exclusao mutua. ï¿½ necessï¿½rio a exclusï¿½o mutua pq o metodo nao pode ser
 * executado mais de uma vez ao mesmo tempo.
 */
public class ExtratoEmprestimoDAO 
{

  private static ExtratoEmprestimoDAO INSTANCE;
  
  
  /**
   * Construtor da classe
   * 
   */
  private ExtratoEmprestimoDAO()
  {
  }

  
  /**
   * Busca o o extrato de emprï¿½stimo do plano cd relativo 
   * a um emprï¿½stimo do participante.
   * 
   * Mï¿½todo que utiliza semï¿½foros para controlar o mï¿½todo, fazendo dele ua seï¿½ï¿½o.
   * ï¿½ necessï¿½rio o uso de semï¿½foros para q o metodo seja executado uma ï¿½nica vez a cada tempo.
   * Isso se deve ao fato q o relatorio ï¿½ gerado em uma tabela temporï¿½ria, de onde vem
   * os dados do extrato. Entao cada registro da tabela ï¿½ parte do extrato de um participante,
   * nao podendo permitir a existencia de registros de outros participantes. Toda vez
   * q o metodo ï¿½ executado a tabela temporaria REL_EPT ï¿½ apagada.
   * 
   * @return - um objeto ExtratoEmprestimo
   */
   public synchronized ExtratoEmprestimo buscarExtratoEmprestimoCD(Emprestimo emprestimo, DataEletros dataFim)
       throws Exception
   {
      ClienteEmprestimo cliente = emprestimo.getCliente();
      
      ExtratoEmprestimo extrato = null;
      
      Connection conn = null;
      
      conn = getConnection();
      
         /**
          * Apagando os registros na rel_ept
          */
         String delQuery = "DELETE FROM REL_EPT WHERE CD_USU = 'O_WEB'";

         Statement delStatement = conn.createStatement();
         
         delStatement.executeUpdate( delQuery );

         delStatement.close();
         
         /**
           * Executando a stored procedure para gerar o histï¿½rico de
           * emprï¿½stimos.
           */   
          CallableStatement callStatement = conn.prepareCall(
             "{call O_EPT.PKG_REL.PR_REL_HIS( ?,?,?,?,?,?,? ) }"
          );
          System.out.println("numEletros "+cliente.getNumEletros());
          System.out.println("data fim "+dataFim.toString());
          System.out.println("cod pessoa "+cliente.getNsu());
          System.out.println("cod empt "+emprestimo.getCodigo());
          
          callStatement.setString(1, cliente.getNumEletros());
          callStatement.setString(2, "EF");
          callStatement.setString(3, null);
          callStatement.setDate(4,new java.sql.Date(dataFim.getTime() ) );
          callStatement.setString(5, cliente.getNsu().toString());
          callStatement.setString(6, emprestimo.getCodigo());
          callStatement.setString(7, "O_WEB");
          
          ResultSet callResult = callStatement.executeQuery();
          System.out.println("Passou CallResult "+callResult ); 
          
          callResult.close();
          callStatement.close();
          
         
           /**#####################################################################
            * buscando os resultados na tabela Rel_ept e o valor do juros mensal 
            */
          /*String queryRel = "SELECT TO_CHAR(REL.DT_REL_LAN, 'DD/MM/YYYY'), REL.VA_REL_LAN, "+
                              "REL.VA_REL_SLD_DVR, EVE.DE_EVE, TO_CHAR(REL.DT_REL_LBE, 'DD/MM/YYYY'), "+
                           "REL.VA_REL_TX_COM, REL.VA_REL_IOF, REL.VA_REL_LIQ, "+
                           "REL.VA_REL_LBE, REL.VA_REL_SLD_DVR_ANT, "+
                           "REL.VA_REL_RMN, REL.VA_REL_PCT_DES, REL.VA_REL_TX_RNV, "+
                           "REL.VA_REL_SLD_DVR_ATU, "+
                           "DECODE(C.CD_CNO_SIT,'E','ENCERRADO','A','ATIVO','Q','QUITADO') SIT, " + 
                           "decode(fn_cad_traz_cd_pln( C.nu_icr, 'S' ), '1', 'BD', '2','CD-ONS', '3', 'CD-ELETROBRAS', '4', 'CD-ELETROBRAS BPDS' ,'0', 'SEM PLANO') plano, " + 
                           "D.VA_CNO_SLD_DVR, P.DE_PAT "+
                           "FROM REL_EPT REL, EVE_EPT EVE, CNO_EPT C, CNO_EPT D, LAN_EPT L, PAT_ELET_2 P "+
                           "WHERE REL.NU_ICR = '"+cliente.getNumEletros()+"' "+
                           "AND REL.CD_CNO = '"+emprestimo.getCodigo()+"' "+
                           "AND C.CD_CNO = REL.CD_CNO " + 
                           "AND REL.CD_EVE = EVE.CD_EVE "+
                           "AND REL.VA_REL_LAN != 0 " + 
                           "AND C.CD_CNO_ANT = D.CD_CNO(+) " + 
                           "AND REL.CD_CNO = L.CD_CNO " + 
                           "AND REL.VA_REL_LAN != 0 " + 
                           "AND REL.DT_REL_LAN = L.DT_LAN " + 
                           "AND REL.CD_EVE = L.CD_EVE " + 
                           "AND REL.VA_REL_LAN = L.VA_LAN " + 
                           "AND REL.CD_USU='O_WEB' "+
                           "AND P.CD_ETD_IDT = fn_cad_traz_cod_pat_nsu (c.nu_icr) " + 
                           "ORDER BY REL.DT_REL_LAN, EVE.FL_EVE_CRE_DEB, L.CD_LAN";*/
                            
          String queryRel ="SELECT TO_CHAR(REL.DT_REL_LAN, 'DD/MM/YYYY'), REL.VA_REL_LAN, "+
                              "REL.VA_REL_SLD_DVR, E.DE_EVE, TO_CHAR(REL.DT_REL_LBE, 'DD/MM/YYYY'), "+
                             "REL.VA_REL_TX_COM, REL.VA_REL_IOF, REL.VA_REL_LIQ, "+
                             "REL.VA_REL_LBE, REL.VA_REL_SLD_DVR_ANT, "+
                             "REL.VA_REL_RMN, REL.VA_REL_PCT_DES, REL.VA_REL_TX_RNV, "+
                             "REL.VA_REL_SLD_DVR_ATU, "+
                             "DECODE(C.CD_CNO_SIT,'E','ENCERRADO','A','ATIVO','Q','QUITADO') SIT, " + 
                             "decode(fn_cad_traz_cd_pln( C.nu_icr, 'S' ), '1', 'BD', '2','CD-ONS', '3', 'CD-ELETROBRAS', '4', 'CD-ELETROBRAS BPDS' ,'0', 'SEM PLANO') plano, " + 
                             "L2.VA_LAN, P.DE_PAT "+
                           "FROM REL_EPT REL, EVE_EPT E, CNO_EPT C, PAT_ELET_2 P, "+
           			" (select * from LAN_EPT where lan_ept.cd_eve in ('005', '006')) L2 "+
             		   "WHERE REL.CD_EVE  = E.CD_EVE "+
		                "AND REL.CD_USU = 'O_WEB' "+
		                "AND REL.NU_ICR  = '"+cliente.getNumEletros()+"' "+
                                "AND REL.CD_CNO = '"+emprestimo.getCodigo()+"' "+
		                "AND C.CD_CNO = REL.CD_CNO "+
		                "AND REL.VA_REL_LAN != 0 "+
		                //"AND REL.CD_CNO = L.CD_CNO "+
		                //"AND REL.DT_REL_LAN = L.DT_LAN "+
		                //"AND REL.CD_EVE = L.CD_EVE "+
		                //"AND REL.VA_REL_LAN = L.VA_LAN "+
		                "AND P.CD_ETD_IDT = fn_cad_traz_cod_pat_nsu (c.nu_icr) "+
		                "AND C.CD_CNO_ANT = L2.cd_cno(+) "+
		            "ORDER BY REL.DT_REL_LAN, E.FL_EVE_CRE_DEB, REL.CD_LAN";
          
          
          Statement relStatement = conn.createStatement();
          ResultSet relResult = relStatement.executeQuery( queryRel );

          extrato = new ExtratoEmprestimo();
          extrato.setCodEmprestimo( emprestimo.getCodigo() );
          double saldoAtual = 0.0;
             
          if(relResult.next()) 
          {
               LancamentoExtrato lan = new LancamentoExtrato();
               lan.setValor( relResult.getDouble("VA_REL_LAN"));
               lan.setData( relResult.getString(1) );
               lan.setSaldo(relResult.getDouble("VA_REL_SLD_DVR"));
               lan.setDescricao( relResult.getString("DE_EVE"));

               extrato.addLancamento( lan );
               extrato.setNomePatrocinadora(relResult.getString("DE_PAT"));
               extrato.setPlanoCliente(relResult.getString("plano"));
               extrato.setSituacaoEmprestimo(relResult.getString("SIT"));
               extrato.setDataLiberacao( relResult.getString(5));
               extrato.setTaxaConcessao( relResult.getDouble("VA_REL_TX_COM") );
               extrato.setIOF( relResult.getDouble("VA_REL_IOF") );
               extrato.setValorLiquido( relResult.getDouble("VA_REL_LBE") - relResult.getDouble( "VA_LAN" ) );
               extrato.setValorConcedido( relResult.getDouble("VA_REL_LBE") );
               extrato.setLiqAnterior( relResult.getDouble( "VA_LAN" ));
               extrato.setRemuneracaoVigente( relResult.getDouble( "VA_REL_RMN" ));
               extrato.setPercentualDesconto( relResult.getDouble( "VA_REL_PCT_DES" ));
               extrato.setTaxaRenovacao( relResult.getDouble( "VA_REL_TX_RNV" ));
               extrato.setDataAtual( dataFim );
               
               extrato.setValorLiquido(extrato.getValorConcedido()-
                                       extrato.getTaxaConcessao()-
                                       extrato.getTaxaRenovacao()-
                                       extrato.getLiqAnterior()-
                                       extrato.getIOF());
               saldoAtual = relResult.getDouble( "VA_REL_SLD_DVR_ATU" );
          }
             
          while( relResult.next() ) 
          {
               LancamentoExtrato lan = new LancamentoExtrato();
               lan.setValor( relResult.getDouble("VA_REL_LAN"));
               lan.setData( relResult.getString(1) );
               lan.setSaldo(relResult.getDouble("VA_REL_SLD_DVR"));
               lan.setDescricao( relResult.getString("DE_EVE"));
               
               extrato.addLancamento( lan );

               saldoAtual = relResult.getDouble( "VA_REL_SLD_DVR_ATU" );  
          }  

          extrato.setSaldoAtual( saldoAtual );
               
          relResult.close();
          relStatement.close();

          //busca o juros de cada lanï¿½amento
          buscarJuros( conn, extrato );
          
       return extrato;      
          
   }


   /**
    * Busca o juros de cada lanï¿½amento contido no objeto <tt>historico<tt>
    * 
    * @param conn - a conexï¿½o com o banco de dados
    * @param extrato - um objeto HistoricoEmprestimo
    * 
    * @throws SQLException
    */
   private void buscarJuros(Connection conn, ExtratoEmprestimo extrato)
     throws SQLException
   {

       String query = "select his.va_his_ind "+ 
                      "from his_ind his "+
                      "where his.cd_ind = "+
                          "(Select  M.cd_ind "+
                               "From mod_ept M, his_ept_mod H "+
                               "Where M.cd_mod = H.cd_mod "+
                               "And M.cd_pat = H.cd_pat "+
                               "And M.cd_ept = H.cd_ept "+
                               "And H.cd_cno = ? "+
                               "And (H.dt_his_fim > to_date(?,'dd/mm/yyyy') OR "+
                               "H.dt_his_fim IS NULL) "+
                               ") "+
                      "and his.dt_his_ref = (select max(his2.dt_his_ref) "+ 
                                             "from his_ind his2 "+ 
                                             "where his2.cd_ind = his.cd_ind "+ 
                                             "and his2.dt_his_ref <= to_date(?,'dd/mm/yyyy'))";

       PreparedStatement statement = conn.prepareStatement( query );
       Iterator it = extrato.getLancamentos().iterator();
       while( it.hasNext() )
       {
          LancamentoExtrato lan = (LancamentoExtrato) it.next();

          statement.setString(1,extrato.getCodEmprestimo());
          statement.setString(2,lan.getData());
          statement.setString(3,lan.getData());

          ResultSet result = statement.executeQuery();
          if( result.next() )
          {
            lan.setValorJuros( result.getDouble(1));
          }
          
          result.close();
       }

       statement.close();        
       closeConnection( conn );
   }

   
   /**
    * Obtem uma conexï¿½o do pool de conexï¿½es
    */
   private Connection getConnection()
      throws Exception
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

   /**
    * Fecha a conexï¿½o.
    */
   private void closeConnection(Connection conn)
      throws SQLException
   {
      conn.close();
      System.out.println("Fechando conexï¿½o");
   }

   /**
    * Mï¿½todo que retorna um objeto ï¿½nico da classe.
    */
    public static ExtratoEmprestimoDAO getInstance()
    {
        if( INSTANCE == null )
        {
           INSTANCE = new ExtratoEmprestimoDAO(  );
        }

        return INSTANCE;
    }
    
}
