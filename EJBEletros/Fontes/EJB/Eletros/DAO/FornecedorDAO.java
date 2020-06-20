package EJB.Eletros.DAO;

import com.eletros.fin.*;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;


public class FornecedorDAO 
{

    public FornecedorDAO() 
    {
       
    }
    
    
    /**
     * Busca as categorias que constam na tabela CTG_FNE
     * @return Collection
     * @throws Exception
     */
     public Collection buscaCategorias()
       throws Exception
     {

       LinkedList resultados = new LinkedList();

       Connection conn = getConnection();
       Statement stmt  = conn.createStatement();

       String  command ="SELECT DE_CTG,CD_CTG FROM CTG_FNE ORDER BY DE_CTG";
                //System.out.println(command);

       ResultSet rs = stmt.executeQuery(command);

       String nomeCategoria ;
       int    codCategoria;
       while (rs.next())
       {
            nomeCategoria = rs.getString(1);
            codCategoria  = rs.getInt(2);
       
            CategoriaFornecedor cat = new CategoriaFornecedor();
            cat.setCodigo( codCategoria);
            cat.setNome( nomeCategoria );
            resultados.add(cat);
       }

       conn.close();
       return resultados;
    }


    /**
     * Busca lista de todos os fornecedores cadastrados no BD
     * @return Collection of com.eletros.fin.Fornecedor
     * @throws Exception
     */
    public Collection BuscarFornecedores() 
      throws Exception
    {
        
        LinkedList list = new LinkedList();
        
        Connection conn = this.getConnection();
        
        String cmd =  "SELECT e.nm_etd, e.cd_etd_idt, e.CD_ETD_TIP "+
                      "FROM FNE_CPR f, ETD_ELET e, PF_ELET pf "+
                      "WHERE f.cd_etd_idt = e.cd_etd_idt "+
                             "and e.cd_etd_idt = pf.cd_etd_idt "+
                             "and e.CD_ETD_TIP = 'PF' "+
                      "union "+
                      "SELECT e.nm_etd, e.cd_etd_idt, e.CD_ETD_TIP "+
                      "FROM FNE_CPR f, ETD_ELET e, PJ_ELET pj "+
                      "WHERE f.cd_etd_idt = e.cd_etd_idt "+
                            "and e.cd_etd_idt = pj.cd_etd_idt "+
                            "and e.CD_ETD_TIP = 'PJ'";
                            
        Statement smt = conn.createStatement();
        ResultSet result = smt.executeQuery( cmd );
        while( result.next()) 
        {
           Fornecedor forn = new Fornecedor();
           forn.setNome(result.getString(1));
           forn.setNsu(new Long(result.getLong(2)));
           forn.setTipo(result.getString(3));
           
           forn.setCategorias( BuscarCategoriasFornecedor( forn.getNsu() ));   
            
           list.add( forn );
        }
        
        result.close();
        smt.close();
        conn.close();
        
        return list;
    }
    
    
    /**
     * Busca um fornecedor pelo nsu.
     * @param codigo
     * @return Fornecedor
     * @throws Exception
     */
    public Fornecedor BuscaFornecedor( String codigo  )
      throws Exception
    {
        String  cmd ="SELECT e.NM_ETD,e.DE_ETD_CEN,e.CD_ETD_TIP,"+
                                "f.DE_FNE_END,f.NU_FNE_TEL,f.NM_FNE_CTT,f.FLFAVDVDATV, f.CD_ETD_IDT,p.NU_PF_CPF,f.CD_BCO,f.CD_AGE,f.NU_FNE_CCO ,f.NU_FNE_CEP "+
                                "FROM ETD_ELET e,FNE_CPR f,PF_ELET p "+
                                "WHERE f.cd_etd_idt = "+codigo.trim()+" AND " +
                                "p.CD_ETD_IDT = e.CD_ETD_IDT(+) AND " +
                                "e.CD_ETD_IDT = f.cd_etd_idt  " +
                                "ORDER BY e.Nm_Etd" ;
                                
       Connection conn = this.getConnection();
        
        Fornecedor forn = null;
        
        Statement smt = conn.createStatement();
        ResultSet result = smt.executeQuery( cmd );
        if( result.next()) 
        {
           forn = this.populateFornecedor( result,"PF" );
        }
        else 
        {
            cmd ="SELECT e.NM_ETD,e.DE_ETD_CEN,e.CD_ETD_TIP,"+
                         "f.DE_FNE_END,f.NU_FNE_TEL,f.NM_FNE_CTT,f.FLFAVDVDATV, f.CD_ETD_IDT,p.NU_PJ_CGC,f.CD_BCO,f.CD_AGE,f.NU_FNE_CCO ,f.NU_FNE_CEP "+
                        "FROM ETD_ELET e,FNE_CPR f,PJ_ELET p "+
                        "WHERE f.cd_etd_idt = "+codigo.trim()+" AND " +
                                    "p.CD_ETD_IDT = e.CD_ETD_IDT(+) AND " +
                                    "e.CD_ETD_IDT = f.cd_etd_idt  " +
                       "ORDER BY e.Nm_Etd" ;
            
            result = smt.executeQuery( cmd );
            if( result.next()) 
            {
               forn = this.populateFornecedor( result,"PJ" );
            }
                       
        }
        
        conn.close(); 
       forn.setCategorias( BuscarCategoriasFornecedor( forn.getNsu() ));    
       return forn;
    }
    
    
    /**
     * Busca fornecedores tipo Pessoa Fisica pelo nome
     * @param nome
     * @return Collection
     * @throws Exception
     */
    public Collection BuscaFornecedorPFNome( String nome  )
      throws Exception
    {
        LinkedList list = new LinkedList();
        
        String  cmd ="SELECT e.NM_ETD,e.DE_ETD_CEN,e.CD_ETD_TIP,"+
                                "f.DE_FNE_END,f.NU_FNE_TEL,f.NM_FNE_CTT,f.FLFAVDVDATV, f.CD_ETD_IDT,p.NU_PF_CPF,f.CD_BCO,f.CD_AGE,f.NU_FNE_CCO ,f.NU_FNE_CEP "+
                                "FROM ETD_ELET e,FNE_CPR f,PF_ELET p "+
                                "WHERE e.NM_ETD LIKE '%"+nome.toUpperCase()+"%' AND "+
                                        "p.CD_ETD_IDT = e.CD_ETD_IDT(+) AND " +
                                        "e.CD_ETD_IDT = f.cd_etd_idt AND " +
                                        "e.CD_ETD_TIP='PF' "+
                                "ORDER BY e.Nm_Etd" ;
                                
       Connection conn = this.getConnection();
        
        Statement smt = conn.createStatement();
        ResultSet result = smt.executeQuery( cmd );
        while( result.next()) 
        {
           Fornecedor forn = this.populateFornecedor( result,"PF" );
            forn.setCategorias( BuscarCategoriasFornecedor( forn.getNsu() ));   
            
           list.add( forn );
        }      
        
       conn.close(); 
       return list;
    }
    
    
    /**
     * Busca fornecedores tipo Pessoa Juridica pelo nome
     * @param nome
     * @return Collection of com.eletros.fin.Fornecedor
     * @throws Exception
     */
    public Collection BuscaFornecedorPJNome( String nome  )
      throws Exception
    {
        LinkedList list = new LinkedList();
        
        String cmd ="SELECT e.NM_ETD,e.DE_ETD_CEN,e.CD_ETD_TIP,"+
                         "f.DE_FNE_END,f.NU_FNE_TEL,f.NM_FNE_CTT,f.FLFAVDVDATV, f.CD_ETD_IDT,p.NU_PJ_CGC,f.CD_BCO,f.CD_AGE,f.NU_FNE_CCO ,f.NU_FNE_CEP "+
                         "FROM ETD_ELET e,FNE_CPR f,PJ_ELET p "+
                         "WHERE e.NM_ETD LIKE '%"+nome.toUpperCase()+"%' AND "+
                                 "p.CD_ETD_IDT = e.CD_ETD_IDT(+) AND " +
                                 "e.CD_ETD_IDT = f.cd_etd_idt AND " +
                                 "e.CD_ETD_TIP='PJ' "+
                         "ORDER BY e.Nm_Etd" ;

        Connection conn = this.getConnection();
         
        Statement smt = conn.createStatement();
        ResultSet result = smt.executeQuery( cmd );   
        while( result.next()) 
        {
            Fornecedor forn = this.populateFornecedor( result,"PJ" );
            forn.setCategorias( BuscarCategoriasFornecedor( forn.getNsu() ));   
            
            list.add( forn );
        }
        
        conn.close();
        return list;
    }
    
    
    /**
     * Busca fornecedores tipo Pessoa Fisica pela categoria
     * @param codCategoria
     * @return Collection of com.eletros.fin.Fornecedor
     * @throws Exception
     */
    public Collection BuscaFornecedoresPFCategoria( int codCategoria  )
      throws Exception
    {
        LinkedList list = new LinkedList();
        
        String  cmd ="SELECT e.NM_ETD,e.DE_ETD_CEN,e.CD_ETD_TIP,"+
                                "f.DE_FNE_END,f.NU_FNE_TEL,f.NM_FNE_CTT,f.FLFAVDVDATV, f.CD_ETD_IDT,p.NU_PF_CPF,f.CD_BCO,f.CD_AGE,f.NU_FNE_CCO ,f.NU_FNE_CEP "+
                                "FROM ETD_ELET e,FNE_CPR f,PF_ELET p, ATC_FNE_CTG a "+
                                        "WHERE a.CD_CTG = "+codCategoria+" AND "+
                                        "a.CD_ETD_IDT = e.CD_ETD_IDT AND " +
                                        "p.CD_ETD_IDT = e.CD_ETD_IDT(+) AND " +
                                        "e.CD_ETD_IDT = f.cd_etd_idt AND " +
                                        "e.CD_ETD_TIP='PF' "+
                                        "ORDER BY e.Nm_Etd" ;

                                
       Connection conn = this.getConnection();
        
        Statement smt = conn.createStatement();
        ResultSet result = smt.executeQuery( cmd );
        while( result.next()) 
        {
           Fornecedor forn = this.populateFornecedor( result,"PF" );
           forn.setCategorias( BuscarCategoriasFornecedor( forn.getNsu() ));   
           list.add( forn );
        }
       
        conn.close(); 
       return list;
    }
    
    
    /**
     * Busca fornecedores tipo Pessoa Juridica pela categoria
     * @param codCategoria
     * @return Collection of com.eletros.fin.Fornecedor
     * @throws Exception
     */
    public Collection BuscaFornecedoresPJCategoria( int codCategoria  )
      throws Exception
    {
        LinkedList list = new LinkedList();
        
        String cmd ="SELECT e.NM_ETD,e.DE_ETD_CEN,e.CD_ETD_TIP,"+
                         "f.DE_FNE_END,f.NU_FNE_TEL,f.NM_FNE_CTT,f.FLFAVDVDATV, f.CD_ETD_IDT,p.NU_PJ_CGC,f.CD_BCO,f.CD_AGE,f.NU_FNE_CCO ,f.NU_FNE_CEP "+
                         "FROM ETD_ELET e,FNE_CPR f,PJ_ELET p, ATC_FNE_CTG a "+
                                        "WHERE a.CD_CTG = "+codCategoria+" AND "+
                                        "a.CD_ETD_IDT = e.CD_ETD_IDT AND " +
                                        "p.CD_ETD_IDT = e.CD_ETD_IDT(+) AND " +
                                        "e.CD_ETD_IDT = f.cd_etd_idt AND " +
                                        "e.CD_ETD_TIP='PJ' "+
                                        "ORDER BY e.Nm_Etd" ;

            
        Connection conn = this.getConnection();
         
         Statement smt = conn.createStatement();
         ResultSet result = smt.executeQuery( cmd );
         while( result.next()) 
         {
            Fornecedor forn = this.populateFornecedor( result,"PJ" );
             forn.setCategorias( BuscarCategoriasFornecedor( forn.getNsu() ));   
            
            list.add( forn );
         }
         
        conn.close();
         return list;
    }
    
    
      /**
        * Busca um Fornecedor pelo numero do CPF
        * @param  identificacao
        * @return com.eletros.fin.Forncedor
        */
      public Fornecedor BuscarFornecedorCPF(String identificacao)
         throws Exception
      {

            Connection conn = getConnection();
            
            Statement stmt  = conn.createStatement();
            Fornecedor forn = new Fornecedor();
            
            String  command ="SELECT e.NM_ETD,e.DE_ETD_CEN,e.CD_ETD_TIP,"+
                                    "f.DE_FNE_END,f.NU_FNE_TEL,f.NM_FNE_CTT,f.FLFAVDVDATV, f.CD_ETD_IDT,p.NU_PF_CPF,f.CD_BCO,f.CD_AGE,f.NU_FNE_CCO ,f.NU_FNE_CEP "+
                                    "FROM ETD_ELET e,FNE_CPR f,PF_ELET p "+
                                    "WHERE p.NU_PF_CPF='"+identificacao+"' AND " +
                                    "p.CD_ETD_IDT = e.CD_ETD_IDT(+) AND " +
                                    "e.CD_ETD_IDT = f.cd_etd_idt  " +
                                    "ORDER BY e.Nm_Etd" ;

            ResultSet rs = stmt.executeQuery(command);

            boolean flag = true;
            while (rs.next())
            {
                flag = false;
                forn = populateFornecedor( rs, "PF" );

            }

            if( flag )
            {
                 String  command_1 = "SELECT  CD_ETD_IDT from PF_ELET WHERE nu_pf_cpf='"+identificacao+"'";
                 rs = stmt.executeQuery(command_1);
                 int nsu =0;

                 if (rs.next())
                 {
                     nsu = rs.getInt(1);
                     forn = this.selectFornecedor(nsu,stmt);

                 }
            }
            
           conn.close();
            forn.setCategorias( BuscarCategoriasFornecedor( forn.getNsu() ));   
            return forn;
       }

       
    /**
       * Busca Fornecedor pelo numero de CGC
       * @param  cgc
       * @return Forncedor
       */
       public Fornecedor BuscarFornecedorCGC(String cgc)
          throws Exception
       {

           Connection conn = getConnection();
           Statement stmt  = conn.createStatement();
           
           Fornecedor forn = new Fornecedor();
           

           String  command ="SELECT e.NM_ETD,e.DE_ETD_CEN,e.CD_ETD_TIP,"+
                                  "f.DE_FNE_END,f.NU_FNE_TEL,f.NM_FNE_CTT,f.FLFAVDVDATV, f.CD_ETD_IDT,p.NU_PJ_CGC,f.CD_BCO,f.CD_AGE,f.NU_FNE_CCO ,f.NU_FNE_CEP "+
                                "FROM ETD_ELET e,FNE_CPR f,PJ_ELET p "+
                                "WHERE p.NU_PJ_CGC='"+cgc+"' AND " +
                                       "p.CD_ETD_IDT = e.CD_ETD_IDT(+) AND " +
                                       "e.CD_ETD_IDT = f.cd_etd_idt  " +
                                "ORDER BY e.Nm_Etd" ;

           ResultSet rs = stmt.executeQuery(command);

           while (rs.next())
           {
               forn = populateFornecedor( rs, "PJ");                
           }
           
          conn.close();
          forn.setCategorias( BuscarCategoriasFornecedor( forn.getNsu() ));   
           return forn;
      }
      
      
      
      /**
        * Caso tenha mais de uma ocorrencia do mesmo CPF na tabela PF_ELET
        * esse método ira selecionar apenas o primeiro e retornar o objeto fornecedor.
        * @param NSU, stmt
        * @return Fornecedor
        *
        **/
       private Fornecedor selectFornecedor(int NSU,Statement stmt)
          throws Exception
       {

            Fornecedor fornecedor = new Fornecedor();
            
            String command_0 = "SELECT NM_ETD,DE_ETD_CEN FROM ETD_ELET WHERE CD_ETD_IDT='"+ NSU +"'";
            ResultSet rs_1 = stmt.executeQuery(command_0);
            
            if (rs_1.next())
            {

                String nome = rs_1.getString(1);
                String email = rs_1.getString(2);
                fornecedor.setNome(nome);
                fornecedor.setEmail(email);

             }
            
            return fornecedor;
        }    
    
    
    /**
     * Metodo para criar um Objeto Fornecedor apartir de um ResultSet
     * @param rs
     * @param tipo
     * @return
     * @throws Exception
     */
    private Fornecedor populateFornecedor( ResultSet rs, String tipo) 
      throws Exception
    {
        Fornecedor forn = new Fornecedor();
        String nomeFornecedor   = rs.getString(1);
        
        System.out.println("Nome Fornecedor:" +  nomeFornecedor);
        String emailFornecedor   = rs.getString(2);
        if (emailFornecedor==null) emailFornecedor = "";
        System.out.println("Email:" + emailFornecedor);

        String codIDFornecedor  = rs.getString(3);
        String endFornecedor    = rs.getString(4);
        if (endFornecedor==null) 
           endFornecedor = "";

        String telFornecedor    = rs.getString(5);
        if (telFornecedor==null) 
           telFornecedor = "";

        String contFornecedor   = rs.getString(6);
        if (contFornecedor==null) 
           contFornecedor = "";

        String statusFornecedor = rs.getString(7);
        String cdIdtFornecedor  = rs.getString(8);
        String idFornecedor     = rs.getString(9);

        String codBanco         = rs.getString(10);
        if (codBanco==null) 
           codBanco = "";

        String codAgencia       = rs.getString(11);
        if (codAgencia==null) 
           codAgencia = "";

        String codContaCorrente = rs.getString(12);
        if (codContaCorrente== null) 
           codContaCorrente="";

        String cepFornecedor    = rs.getString(13);
        if (cepFornecedor== null) 
           cepFornecedor="";

        forn.setNome(nomeFornecedor);
        forn.setEmail(emailFornecedor);
        forn.setIdentificacao(idFornecedor);
        forn.setTelefone(telFornecedor);
        forn.setCont( contFornecedor);
        forn.setStatus( statusFornecedor);
        forn.setTipo(codIDFornecedor);
        forn.setCodBanco(codBanco);
        forn.setAgencia(codAgencia);
        forn.setConta(codContaCorrente);
        forn.setNsu(new Long(cdIdtFornecedor) );
        forn.setCep(cepFornecedor);
        forn.setEndereco(endFornecedor);
        forn.setEmail(emailFornecedor);   
        forn.setTipo( tipo );
        
        return forn;
    }
    
    
    /**
     * Busca os bancos cadastrados na base de dados ordenado pelo nome dos bancos. 
     * 
     * @return Collection of com.eletros.fin.Banco 
     * @throws java.lang.Exception
     */
    public java.util.Collection buscarBancosCadastrados()
      throws Exception
    {
         LinkedList bancos = new LinkedList();
         
         Connection conn = null;
         String query = "SELECT * "+
                          "FROM BCO_ELET ORDER BY DE_BCO";
         conn = getConnection();
         Statement statement = conn.createStatement();
         ResultSet result = statement.executeQuery( query );
             
         while( result.next() )
         {
             String codigo = result.getString("CD_BCO");
             String descricao = result.getString("DE_BCO");
                
             Banco banco = new Banco( codigo, descricao);
             banco.setContaPrincipal(result.getString("CD_BCO_CTA_PRN"));
             banco.setContaAuxiliar(result.getString("CD_BCO_CTA_AUX"));
             banco.setBancoDiaria(result.getInt("QT_BCO_DIA_FLO"));
             java.sql.Date date = result.getDate("DT_BCO_INI_VIG");
             if( date != null )
             {
                banco.setDataInicialVigencia( new com.util.DataEletros( date.getTime()));
             }
                              
             bancos.add( banco ); 
         }
             
        conn.close();
         return bancos;
    }
    
    
    /**
       * Busca as agencias cadastradas na base de dados relativas ao banco <tt>codBanco</tt>
       * ordenado pelo codigo das agencias. 
       * @param codBanco
       * @return Collection of com.eletros.fin.Agencia 
       * @throws java.lang.Exception
       */
     public java.util.Collection buscarAgenciasBanco( String codBanco)
        throws Exception
     {
           LinkedList agencias = new LinkedList();
           
           Connection conn = null;
           String query = "SELECT * "+
                            "FROM AGE_ELET "+
                            "WHERE CD_BCO = '"+codBanco+"' ORDER BY CD_AGE";
           conn = getConnection();
           Statement statement = conn.createStatement();
           ResultSet result = statement.executeQuery( query );
               
           while( result.next() )
           {
                  Agencia agencia = 
                      new Agencia( result.getString("CD_BCO"), result.getString("CD_AGE"));
                  agencia.setCep(result.getString("NU_AGE_CEP"));
                  agencia.setDescricao(result.getString("DE_AGE"));
                  agencia.setUf(result.getString("CD_AGE_UF"));
                                
                  agencias.add( agencia ); 
           }
           
           conn.close();    
           return agencias;
      }
      
      
      /**
     * Atualiza ou cadastra um fornecedor
     * @param forn
     * @throws Exception
     */
      public void persistirFornecedor( Fornecedor forn ) 
        throws Exception
      {
         if( forn.getNsu() == null ) {
             cadastrarFornecedor( forn );
         }
         else {
             atualizaFornecedor( forn);
         }
      }
      
      
      private void cadastrarFornecedor( Fornecedor forn)
        throws Exception
      {
          if( forn.getTipo().trim().equals("PF"))
             this.CadastrarFornecedorPF( forn );
          else
             this.CadastrarFornecedorPJ( forn );
      }
    
    
       /**
        * Calcula novo CD_ETD_IDT .
        * @return int
        * @author Rejane
        */
        private int GerarNovoNsu(Statement stmt)
           throws Exception
        {

              String command_0 = "SELECT calcula_novo_nsu from dual";
              ResultSet rs_1 = stmt.executeQuery(command_0);
              int novo_nsu = 0;
              
              while (rs_1.next()){
                novo_nsu = rs_1.getInt(1);
              }
              return  novo_nsu;
           
      }
         

      /**
      * Cadastra novo fornecedor na tabela ETD_ELET,PJ_ELET ou PF_ELET,FNE_CPR,ATC_FNE_CTG
      * @return boolean
      * @author Rejane
      */
       private void CadastrarFornecedorPJ( Fornecedor forn)
         throws Exception
       {

          Connection conn = null;
          
          conn = getConnection();
          Statement stmt  = conn.createStatement();

          String  command ="SELECT count(NU_PJ_CGC) FROM PJ_ELET " +
                               "WHERE NU_PJ_CGC ='" +forn.getIdentificacao()+"'";
          
          ResultSet rs = stmt.executeQuery(command);

          int cgc = 0;
          while (rs.next()){
                cgc = rs.getInt(1);
          }
          rs.close();
          
          //verifica se o fornecedor já existe
          if (cgc !=1)
          {

              int novoNsu = this.GerarNovoNsu(stmt);
          
              String  command_1 = "INSERT INTO ETD_ELET(CD_ETD_IDT,NM_ETD,Cd_Usu,De_Etd_Cen,Cd_Etd_Tip) VALUES"+
                                        "("+ novoNsu +",'"+ forn.getNome().toUpperCase() + "',"+ forn.getUsuario().getCodigo()+",'"+forn.getEmail() +"','"+forn.getTipo() + "')" ;
              stmt.executeUpdate(command_1);

              String command_2 = "INSERT INTO PJ_ELET(CD_ETD_IDT,Nu_Pj_Cgc,NM_PJ_FAN) VALUES "+
                                       "("+novoNsu+",'"+forn.getIdentificacao()+"','"+ forn.getNome().toUpperCase() +"')";
              stmt.executeUpdate(command_2);

              String command_3 = "INSERT  INTO FNE_CPR (CD_ETD_IDT,CD_FNE_ORI,CD_BCO,CD_AGE,FL_FNE_TBT,NU_FNE_CCO,DE_FNE_END,NU_FNE_TEL,NU_FNE_CEP,NM_FNE_CTT,FL_FNE_NCS_ATZ,FL_FNE_ICD_IOF,FLFAVDVDATV)  VALUES ("
                                       +novoNsu+",'CPR','"+forn.getCodBanco()+"','"+forn.getAgencia()+"','N','"+forn.getConta()+"','"+forn.getEndereco()+"','"+forn.getTelefone()+"','"+forn.getCep()+"','"+forn.getCont()+"','N','N','N')";
              stmt.executeUpdate(command_3);
              
              conn.close();
              
              vincularCategoriasFornecedor( novoNsu, forn.getCategorias() );
          }
          else {
              throw new Exception( "Fornecedor existente");
          }
          
       }


       /**
      * Cadastra novo fornecedor na tabela ETD_ELET,PF_ELET,FNE_CPR,ATC_FNE_CTG
      * @author Rejane
      */
       private void CadastrarFornecedorPF( Fornecedor forn)
         throws Exception
       {

          Connection conn = getConnection();
          
          Statement stmt  = conn.createStatement();
          String  command ="SELECT count(nu_pf_cpf) FROM PF_ELET " +
                               "WHERE nu_pf_cpf ='" + forn.getIdentificacao() +"'";
          
          ResultSet rs = stmt.executeQuery(command);

          int cpf = 0;
          while (rs.next())
          {
              cpf = rs.getInt(1);
          }
          rs.close();
          
          //verifica se o fornecedor já existe
          if (cpf==1)
          {
              throw new Exception( "Fornecedor existente");
          }
          else
          {

             int novoNsu = this.GerarNovoNsu(stmt);
          
             String  command_1 = "INSERT INTO ETD_ELET(CD_ETD_IDT,NM_ETD,Cd_Usu,De_Etd_Cen,Cd_Etd_Tip) VALUES"+
                                        "("+ novoNsu +",'"+ forn.getNome().toUpperCase() + "',"+forn.getUsuario().getCodigo()+",'"+forn.getEmail() +"','"+forn.getTipo() + "')" ;
             stmt.executeUpdate(command_1);


             String command_2 = "INSERT INTO PF_ELET(CD_ETD_IDT,nu_pf_cpf,CD_PF_SEX,CD_PF_EST_CIV) VALUES "+
                                       "("+novoNsu+",'"+forn.getIdentificacao()+"','N','1')";
             stmt.executeUpdate(command_2);

             String command_3 = "INSERT  INTO FNE_CPR (CD_ETD_IDT,CD_FNE_ORI,CD_BCO,CD_AGE,FL_FNE_TBT,NU_FNE_CCO,DE_FNE_END,NU_FNE_CEP,FL_FNE_NCS_ATZ,FL_FNE_ICD_IOF,FLFAVDVDATV)  VALUES ("
                                       +novoNsu+",'CPR','"+forn.getCodBanco()+"','"+forn.getAgencia()+"','N','"+forn.getConta()+"','"+forn.getEndereco()+"','"+forn.getCep()+"','N','N','N')";
             stmt.executeUpdate(command_3);
             
             conn.close();
             vincularCategoriasFornecedor( novoNsu, forn.getCategorias() );
             
           }  
       }

       
    /**
     * Atualiza ou cadastra uma categoria. 
     * @param categorias - Collection of com.eletros.fin.CategoriaFornecedor
     * @throws Exception
     */
       public void cadastrarCategorias( Collection categorias ) 
         throws Exception
       {
           Connection conn = getConnection();
           Statement stmt  = conn.createStatement();
           
           Iterator it = categorias.iterator();
           while( it.hasNext() ) 
           {
               CategoriaFornecedor categoria = (CategoriaFornecedor)it.next();
               
               if( categoria.getCodigo() == 0)
               {
                 String  command_1 = "INSERT INTO CTG_FNE(CD_CTG,DE_CTG) VALUES"+
                                          "(SELECT MAX(NVL(CD_CTG,0))+1 FROM CTG_FNE,'"+categoria.getNome()+"')" ;
                 stmt.executeUpdate(command_1);
               }
               else 
               {
                   String  command_1 = "UPDATE CTG_FNE SET DE_CTG='"+categoria.getNome()+"' WHERE CD_CTG='"+categoria.getCodigo()+"'";    
                   stmt.executeUpdate(command_1);
               }
           }
           
           conn.close();
       }
       
       
    /**
       * Apaga uma categoria da Base de Dados
       * @param  categoria - Objeto com.eletros.fin.CategoriaFornecedor
       * @throws Exception
       */
       public void dropCategoria(CategoriaFornecedor categoria)
         throws Exception
       {

           Connection conn = getConnection();
           
           Statement stmt  = conn.createStatement();
           String  command_1 = "DELETE FROM CTG_FNE WHERE CD_CTG='"+categoria.getCodigo()+"'";

           stmt.executeUpdate(command_1);
           conn.close();
       }
       
       
       private void vincularCategoriasFornecedor( int nsuFornecedor, Collection categorias ) 
          throws Exception
       {
           Connection conn = getConnection();
           Statement stmt  = conn.createStatement();
           
           Iterator it = categorias.iterator();
           while( it.hasNext() ) 
           {
               CategoriaFornecedor categoria = (CategoriaFornecedor)it.next();
               
               String  command_1 = "INSERT INTO ATC_FNE_CTG(CD_CTG,CD_ETD_IDT) VALUES"+
                                             "("+categoria.getCodigo()+",'"+nsuFornecedor+"')" ;
               stmt.executeUpdate(command_1);
               
           }
           conn.close();
       }
 
 
       /**
         * Alterar o Cadastro do Fornecedor
         */
        private void atualizaFornecedor( Fornecedor forn)
          throws Exception
        {

            Connection conn = getConnection();
            Statement stmt  = conn.createStatement();
            
            //apaga todos os vinculos com categorias
            String  cmd = "DELETE FROM ATC_FNE_CTG WHERE CD_ETD_IDT='"+ forn.getNsu()+"'";
            stmt.executeUpdate(cmd);
              
            String  command_1 = "UPDATE ETD_ELET SET NM_ETD='"+forn.getNome().toUpperCase()+"',Cd_Usu="+forn.getUsuario().getCodigo()+",De_Etd_Cen='"+forn.getEmail() +"' WHERE CD_ETD_IDT='"+forn.getNsu()+"'" ;
            stmt.executeUpdate(command_1);
            
            if( forn.getTipo().trim().equals("PJ"))
            {
              String command_2 = "UPDATE PJ_ELET SET NM_PJ_FAN='"+forn.getNome().toUpperCase()+"' WHERE CD_ETD_IDT='"+forn.getNsu()+"'";
              stmt.executeUpdate(command_2);
            }
            
            String command_3 = "UPDATE FNE_CPR SET CD_BCO='"+forn.getCodBanco()+"',CD_AGE='"+forn.getAgencia()+"',NU_FNE_CCO='"+forn.getConta()+"',DE_FNE_END='"+forn.getEndereco()+"',NU_FNE_TEL='"+forn.getTelefone()+"',NU_FNE_CEP='"+forn.getCep()+"',NM_FNE_CTT='"+forn.getCont()+"' WHERE CD_ETD_IDT='"+forn.getNsu()+"'";
            stmt.executeUpdate(command_3);
            
             conn.close();
            //vincular categorias novamente
             vincularCategoriasFornecedor( forn.getNsu().intValue(), forn.getCategorias() );
         }
      
      
      /**
     * Busca uma categoria pelo seu codigo
     * @param codigo
     * @return CategoriaFornecedor
     * @throws Exception
     */
      public CategoriaFornecedor BuscarCategoria( int codigo ) 
        throws Exception
      {
          CategoriaFornecedor categoria = null;
          
          Connection conn = getConnection();
          Statement stmt  = conn.createStatement();

          String  command ="SELECT DE_CTG FROM CTG_FNE " +
                                  "WHERE CD_CTG =" + codigo;
                         

          ResultSet rs = stmt.executeQuery(command);

          while (rs.next())
          {
              categoria = new CategoriaFornecedor();
              categoria.setCodigo( codigo );
              categoria.setNome( rs.getString(1) );
          }
          
          conn.close();
          return categoria;
             
      }
      
      
    private Collection BuscarCategoriasFornecedor( Long nsu ) 
      throws Exception
    {
        LinkedList list = new LinkedList();
        
        Connection conn = getConnection();
        Statement stmt  = conn.createStatement();

        String  command ="SELECT ctg.CD_CTG, ctg.DE_CTG "+
                                "FROM ETD_ELET e, ATC_FNE_CTG a, CTG_FNE ctg " +
                                "WHERE e.cd_etd_idt =" + nsu.toString()+" "+
                                       "and a.cd_etd_idt = e.cd_etd_idt "+
                                       "and a.cd_ctg = ctg.cd_ctg";
                       
        ResultSet rs = stmt.executeQuery(command);

        while (rs.next())
        {
            CategoriaFornecedor categoria = new CategoriaFornecedor();
            categoria.setCodigo( rs.getInt(1) );
            categoria.setNome( rs.getString(2) );
        }

        conn.close();
        return list;
           
    }
      
      
      /**
        * Busca a quantidade de ocorrencias de um numero de cpf
        * @param cpf - Numero de CPF
        * @return int
        * @throws Exception
        */
       public int buscaOcorrenciasCPF(String cpf)
          throws Exception
       {

            Connection conn = getConnection();
            Statement stmt  = conn.createStatement();
            
            String  command_0 ="SELECT count(*) from PF_ELET where nu_pf_cpf ='"+cpf+"'";
            ResultSet rs_0 = stmt.executeQuery(command_0);

            int count =0;

            while (rs_0.next())
            {
               count = rs_0.getInt(1);
            }
            
            conn.close();
            return count;
        }
      
      
      /**
       * Verifica numero de agencias de um banco.
       * @param codBanco
       * @return int
       * @throws Exception
       */
       public int buscaOcorrenciasAgenciasBanco(String codBanco)
          throws Exception
       {

           Connection conn = getConnection();
           Statement stmt  = conn.createStatement();

           String  command_1 ="SELECT count(cd_age) from age_elet where cd_bco='"+codBanco+"'";
           System.out.println(command_1);


           ResultSet rs_1 = stmt.executeQuery(command_1);

           int count =0;

           while (rs_1.next())
           {
              count = rs_1.getInt(1);
           }
           
           conn.close();
           return count;
       }
       
       
      /**
        * Verifica a quantidade de ocorrencias de um numero de CGC
        * @param cgc - numero do CGC
        * @return int
        * @throws Exception
        */
        public int buscaOcorrenciasCGC(String cgc)
           throws Exception
        {

            Connection conn = getConnection();
            Statement stmt  = conn.createStatement();

            String  command_1 ="SELECT count(*) from PJ_ELET where nu_pj_cgc ='"+cgc+"'";
            ResultSet rs_1 = stmt.executeQuery(command_1);

            int count =0;
            while (rs_1.next())
            {
               count = rs_1.getInt(1);
            }
            
            conn.close();
            return count;
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
