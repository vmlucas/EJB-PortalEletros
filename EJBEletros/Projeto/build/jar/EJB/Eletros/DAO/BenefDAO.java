package EJB.Eletros.DAO;

import com.eletros.benef.*;
import com.util.DataEletros;
import java.sql.*;
import java.util.LinkedList;
import java.util.List;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import javax.sql.DataSource;


/**
 * Classe que busca na base de dados ORACLE informações sobre os benefícios concedidos
 * a paticipantes Eletros.
 */
public class BenefDAO 
{
    
    /**
     * Construtor da classe.
     */
    public BenefDAO() 
    {
    }
    
    
    /**
     * Busca um objeto do tipo Beneficio contendo uma lista de benefícios
     * concedidos ao participante tanto pelo INSS quanto pela Eletros.
     * 
     * @param part - Participante
     * @return Beneficio
     * @throws Exception
     */
    public Beneficio buscaBeneficiosConcedidos(Participante part)
      throws Exception
    {
        Beneficio benef = null;
        
        Connection conn = getConnection();
        List<LancBenef> lista = new LinkedList<LancBenef>();
        
        /**busca dados sobre os benefícios concedidos ao participante
         */
        String query="Select Distinct TS_BNF," + 
                             "DE_EPE," + 
                             "DE_SIT_BNF," + 
                             "DT_BNF_INSS," + 
                             "DT_BNF_ELET," + 
                             "NU_BNF_PTI, " +
                             "cd_bnf_pat_cnv "+
                      "From BNF_BNF, EPE_BNF, SIT_BNF " + 
                      "Where BNF_BNF.CD_EPE = EPE_BNF.CD_EPE " + 
                         "And BNF_BNF.CD_SIT = SIT_BNF.CD_SIT " + 
                         "And BNF_BNF.CD_PTI ='"+part.getNumEletros()+"' " + 
                      "Order by TS_BNF Asc"; 
        
        Statement statement = conn.createStatement();
        ResultSet result = statement.executeQuery( query );
        while( result.next()) {
            
            LancBenef lan = new LancBenef();
            lan.setData( new DataEletros( result.getDate(1).getTime()));
            lan.setCodigo( result.getInt(6));
            lan.setDescricao( result.getString(2) );
            lan.setSituacao( result.getString(3));
            if( result.getDate(4) != null)
               lan.setConcessaoINSS(new DataEletros(result.getDate(4).getTime()));
            if( result.getDate(5) != null)
               lan.setConcessaoEletros(new DataEletros(result.getDate(5).getTime()));            
            int codPatCnv = result.getInt(7);
            
            //busca a patrocinadora do convenio se houver
            if( codPatCnv != 0 ) {
                Connection connTemp = getConnection();
                String patQuery = "SELECT DE_PAT "+
                                  "FROM PAT_ELET_2 "+
                                  "WHERE CD_ETD_IDT ="+codPatCnv;
                
                Statement stmTemp = connTemp.createStatement();
                ResultSet resultTemp = stmTemp.executeQuery( patQuery );
                
                if( resultTemp.next())
                    lan.setPatrocinadoraConvenio( resultTemp.getString(1));
                
                resultTemp.close();
                stmTemp.close();
                connTemp.close();
            }
            else {
              lan.setPatrocinadoraConvenio( "-");    
            }
            
            lista.add( lan );            
        }
        
        result.close();
        statement.close();        
        
        /**
         * busca o ultimo valor de beneficio do INSS
         */
        statement = conn.createStatement();
        String inssQuery = "select mm_fic,aa_fic, VA_FIC_INSS "+
                           "from fic_flh "+
                           "where cd_fic_icr='"+part.getNumEletros()+"' "+ 
                                  "and cd_eve in ('101', '201') " + 
                                  "and aa_fic*100 + mm_fic = "+
                                       "(Select max(aa_fic*100 + mm_fic) " + 
                                                "From fic_flh f " + 
                                                "Where f.cd_fic_icr = fic_flh.cd_fic_icr\n" + 
                                                "and f.cd_eve = fic_flh.cd_eve)";
        result = statement.executeQuery( inssQuery );
        if( result.next()){
           benef = new Beneficio();
           benef.setLancamentos( lista );

           benef.setUltimoValorInss( result.getDouble(3)); 
           benef.setDataUltimoValorINSS( result.getString(1)+"/"+
                                         result.getString(2));
        }
        
        result.close();
        statement.close();
        conn.close();
        
        return benef;
    }
    
    
    /**
     * busca uma conexão no pool
     * @return java.sql.Connection
     * @throws SQLException
     */
    public Connection getConnection()
              throws SQLException
    {
              Connection conn = null;
       try 
       {                
             InitialContext initContext = new InitialContext();         
             DataSource ds = (DataSource)initContext.lookup("java:/jdbc/OracleLogin");
             conn = ds.getConnection();         
       }
       catch(NamingException ne)
       {                        
          System.out.println(ne.toString());            
       }
       return conn;
    }

}
