package EJB.Eletros.DAO;

import EJB.Indices.*;
import java.sql.*;
import java.util.*;
import extrato.*;
import com.eletros.benef.*;
import com.util.DataEletros;
import javax.naming.*;
import javax.rmi.PortableRemoteObject;
import javax.sql.*;

/**
 * Classe que busca no banco de dados Oracle informa��es relativas ao 
 * participante e seus dependentes como por exemplo suas patrocinadoras, plano,
 * e pagamentos de joias.
 * 
 * @author  victor
 */
public class ParticipanteDAO {

    private IndicesEJB indiceEJB;

    /**
     * Construtor da classe
     */
    public ParticipanteDAO(IndicesEJB indiceEJB) {
        this.indiceEJB = indiceEJB;
    }

    /**
     * Busca uma lista de participantes referentes a um nome( parte ou totalidade). 
     * Poder� conter objetos referentes ao mesmo participante, sendo que em inscri��es
     * eletros distintas. A lista cont�m participantes em qualquer situa��o e ordenada
     * por nome e data de inscri��o.
     *  
     * @param nome
     * @return List Participante 
     * @throws Exception
     */
    public List<Participante> buscaInscricoes(String nome)
            throws Exception {
        LinkedList<Participante> partList = new LinkedList<Participante>();
        LinkedList<String> listaNumEletros = new LinkedList<String>();

        Connection conn = getConnection();

        String query = "SELECT DISTINCT PTI.NU_PTI_ICR,ETD.NM_ETD,PTI.DT_PTI_ICR_ELET " +
                "FROM PTI_CAD PTI, PF_ELET PF, ETD_ELET ETD " +
                "WHERE PF.CD_ETD_IDT = PTI.CD_ETD_IDT " +
                "AND ETD.CD_ETD_IDT = PTI.CD_ETD_IDT " +
                "AND ETD.NM_ETD like '%" + nome + "%' " +
                "ORDER BY ETD.NM_ETD,PTI.DT_PTI_ICR_ELET ";

        Statement novoStatement = conn.createStatement();
        ResultSet novoResult = novoStatement.executeQuery(query);

        while (novoResult.next()) {
            listaNumEletros.add(novoResult.getString(1));
        }

        novoResult.close();
        novoStatement.close();
        conn.close();

        for (String numEletros : listaNumEletros) {
            partList.add(this.buscaParticipante(numEletros));
        }

        return partList;
    }

    /**
     * Busca uma lista de participantes ATIVOS referentes a um nome( parte ou totalidade). 
     * Poder� conter objetos referentes ao mesmo participante, sendo que em inscri��es
     * eletros distintas. A lista � ordenada por nome.
     *  
     * @param nome
     * @return List de Participante
     * @throws Exception
     */
    public List<Participante> buscaInscricoesAtivasParticipante(String nome)
            throws Exception {

        LinkedList<Participante> partList = new LinkedList<Participante>();
        LinkedList<String> listaNumEletros = new LinkedList<String>();

        Connection conn = getConnection();

        String novoNome = "";
        StringTokenizer token = new StringTokenizer(nome, "'");
        while (token.hasMoreTokens()) {
            String part = token.nextToken();
            novoNome = novoNome + part;
        }

        String query = "SELECT DISTINCT PTI.NU_PTI_ICR, ETD.NM_ETD " +
                "FROM PTI_CAD PTI, PF_ELET PF, ETD_ELET ETD " +
                "WHERE PF.CD_ETD_IDT = PTI.CD_ETD_IDT " +
                "AND PTI.DT_PTI_DLG IS NULL " +
                "AND ETD.CD_ETD_IDT = PF.CD_ETD_IDT " +
                "AND replace(ETD.NM_ETD,'''','') " +
                "like replace('%" + novoNome.toUpperCase().trim() + "%','''','') " +
                "ORDER BY NM_ETD";

        Statement novoStatement = conn.createStatement();
        ResultSet novoResult = novoStatement.executeQuery(query);

        while (novoResult.next()) {
            listaNumEletros.add(novoResult.getString(1));
        }

        novoResult.close();
        novoStatement.close();
        conn.close();

        for (String numEletros : listaNumEletros) {
            Participante p = this.buscaParticipante(numEletros);
            if (p != null) {
                partList.add(p);
            }
        }
        return partList;
    }

    public List<Pensionista> buscaInscricoesAtivasPensionista(String nome)
            throws Exception {

        LinkedList<Pensionista> penList = new LinkedList<Pensionista>();
        LinkedList<String> listaNumEletros = new LinkedList<String>();

        Connection conn = getConnection();

        String novoNome = "";
        StringTokenizer token = new StringTokenizer(nome, "'");
        while (token.hasMoreTokens()) {
            String part = token.nextToken();
            novoNome = novoNome + part;
        }

        String query = "SELECT DISTINCT PNS.NU_PNS_ICR, ETD.NM_ETD " +
                "FROM PNS_CAD_BRC PNS, PTI_CAD PTI, PF_ELET PF, ETD_ELET ETD " +
                "WHERE PF.CD_ETD_IDT = PNS.CD_ETD_IDT " +
                "AND PNS.NU_PNS_ICR_GRC = PTI.NU_PTI_ICR " +
                "AND PNS.DT_PNS_DLG IS NULL " +
                "AND ETD.CD_ETD_IDT = PF.CD_ETD_IDT " +
                "AND replace(ETD.NM_ETD,'''','') " +
                "like replace('%" + novoNome.toUpperCase().trim() + "%','''','') " +
                "ORDER BY NM_ETD";

        Statement novoStatement = conn.createStatement();
        ResultSet novoResult = novoStatement.executeQuery(query);

        while (novoResult.next()) {
            listaNumEletros.add(novoResult.getString(1));
        }

        novoResult.close();
        novoStatement.close();
        conn.close();

        for (String numEletros : listaNumEletros) {
            Pensionista pen = this.buscaPensionista(numEletros);
            if (pen != null) {
                penList.add(pen);
            }

        }
        return penList;
    }

    /**
     * Busca uma lista de participantes que migraram do plano BD para o Plano
     * CD Eletrobrás. A lista pode contem participantes até uma data ou todos 
     * que migraram.
     * 
     * @return List<ParticipanteCDElet>
     * 
     * @throws Exception
     */
    public List<ParticipanteCDElet> buscaParticipantesMigradosCD(String data)
            throws Exception {
        Connection conn = getConnection();

        LinkedList<ParticipanteCDElet> list = new LinkedList<ParticipanteCDElet>();
        String query = null;
        if (data == null) {
            query = "select distinct sal.nrisc, SAL.NRISCBD, sal.dtmigcad " +
                    "From RSV_ELET_PTI_V3 R,SALDOSMIGCD SAL " +
                    "where SAL.NRISCBD = '00'||R.nu_pti_icr " +
                    "order by dtmigcad";
        } else {
            query = "select distinct sal.nrisc, SAL.NRISCBD, R.mm_rsv, R.aa_rsv " +
                    "From RSV_ELET_PTI_V3 R,SALDOSMIGCD SAL " +
                    "where SAL.NRISCBD = '00'||R.nu_pti_icr " +
                    " and to_date('01/'||Trim(R.mm_rsv)||'/'||Trim(R.aa_rsv),'dd/mm/yyyy') " +
                    "                                   < to_date('" + data + "','dd/mm/yyyy') " +
                    "                               order by R.aa_rsv,R.mm_rsv";
        }
        Statement stm = conn.createStatement();

        ResultSet result = stm.executeQuery(query);
        while (result.next()) {
            String numEletros = result.getString("nrisc");
            int tam = numEletros.length();
            numEletros = numEletros.substring(2, tam);
            list.add((ParticipanteCDElet) this.buscaParticipante(numEletros));
        }

        result.close();
        stm.close();

        conn.close();

        return list;
    }

    /**
     *  busca na base um participante(podendo ser instancia de ParticipanteBD,
     *  ParticipanteCDNS, ParticipanteCDElet) referente ao n�mero de 
     *  inscri��o eletros passado como parametro.
     *  Participante contendo dados espec�ficos de cada plano (BD e CDElet),
     *  dependentes e seus respectivos v�nculos e hist�rico de patrocinadoras
     *  
     *  @param numEletros - numero eletros do participante
     *  @return Participante
     *  @throws Exception
     */
    public Participante buscaParticipante(String numEletros)
            throws Exception {
        if (numEletros.length() < 7) {
            int numCarac = numEletros.length();
            int numzeros = 7 - numCarac;
            String num = "";
            for (int i = 0; i < numzeros; i++) {
                num = num + "0";
            }
            numEletros = num + numEletros;
        }

        Participante participante = null;

        Connection conn = getConnection();

        String query = "SELECT ETD.CD_PES, decode(fn_cad_traz_cd_pln(PTI.NU_PTI_ICR),1,'BD',2,'CD-ONS',3,'CD-ELETROBRAS') plano, " +
                " PTI.CD_ETD_IDT, PF.NU_PF_IDE, PF.NU_PF_CPF, PF.DT_PF_NAS, PF.CD_PF_SEX, PTI.PC_PTI_JOI_ELET, " +
                "ETD.NM_ETD,PTI.DT_PTI_ICR_ELET, PTI.DT_PTI_DLG, " +
                "DECODE(PTI.CD_PTI_CON,'A','Ativo','I','Assistido','S','Assistido SEM complementacao de pensao','D','Desligado' ) STATUS, " +
                "CLI.DE_CLI_EML, CLI.CD_BCO, CLI.CD_AGE, CLI.NU_AGE_DV, CLI.NU_CLI_CCO, CLI.NU_CLI_CCO_DV, " +
                "CLI.DE_CLI_END, CLI.NU_CLI_CEP, BRR.DE_BRR BAIRRO, CID.DE_CID CIDADE, CID.CD_CID_UF UF, pas.de_pas PAIS " +
                "FROM PTI_CAD PTI, CLI_CAD CLI, BRR_ELET BRR, CID_ELET CID,PAS_ELET PAS, PF_ELET PF, ETD_ELET ETD " +
                "WHERE PF.CD_ETD_IDT = PTI.CD_ETD_IDT " +
                "AND ETD.CD_ETD_IDT = PTI.CD_ETD_IDT " +
                "AND CLI.CD_ETD_IDT = PTI.CD_ETD_IDT " +
                "AND CLI.CD_BRR = BRR.CD_BRR " +
                "and cli.cd_cid = cid.cd_cid " +
                "and cli.cd_pas = pas.cd_pas " +
                "AND PTI.NU_PTI_ICR = '" + numEletros + "'";

        Statement novoStatement = conn.createStatement();
        ResultSet novoResult = novoStatement.executeQuery(query);

        if (novoResult.next()) {
            String plano = novoResult.getString("PLANO");
            if (plano.equals("BD")) {
                participante = new ParticipanteBD();
                ((ParticipanteBD) participante).setJoia(
                        novoResult.getDouble("PC_PTI_JOI_ELET"));
            } else if (plano.equals("CD-ONS")) {
                participante = new ParticipanteCDONS();

            } else {
                participante = new ParticipanteCDElet();
            }
            participante.setNsu(new Long(novoResult.getString("CD_ETD_IDT")));
            participante.setStatus(novoResult.getString("STATUS"));
            participante.setRG(novoResult.getString("NU_PF_IDE"));
            participante.setCPF(novoResult.getString("NU_PF_CPF"));
            participante.setNumEletros(numEletros);
            participante.setSexo(novoResult.getString("CD_PF_SEX"));
            participante.setNome(novoResult.getString("NM_ETD"));
            participante.setDtNascimento(new DataEletros(novoResult.getDate("DT_PF_NAS").getTime()));
            participante.setDtInscricao(new DataEletros(novoResult.getDate("DT_PTI_ICR_ELET").getTime()));
            if (novoResult.getDate("DT_PTI_DLG") != null) {
                participante.setDtDesligamento(new DataEletros(novoResult.getDate("DT_PTI_DLG").getTime()));
            }
            participante.setEmail(novoResult.getString("DE_CLI_EML"));
            participante.setEndereco(novoResult.getString("DE_CLI_END"));
            participante.setCEP(novoResult.getString("NU_CLI_CEP"));
            participante.setBairro(novoResult.getString("BAIRRO"));
            participante.setCidade(novoResult.getString("CIDADE"));
            participante.setUF(novoResult.getString("UF"));
            participante.setPais(novoResult.getString("PAIS"));
            participante.setBanco(novoResult.getString("CD_BCO"));
            participante.setAgencia(novoResult.getString("CD_AGE"));
            participante.setContaCorrente(novoResult.getString("NU_CLI_CCO"));

        }
        novoResult.close();
        novoStatement.close();

        /**
         * busca informacoes realtivas a cada plano
         */
        if (participante != null) {
            if (participante instanceof ParticipanteBD) {
                participante = buscaDadosBD((ParticipanteBD) participante);
            }
            if (participante instanceof ParticipanteCDElet) {
                participante = buscaDadosCDElet((ParticipanteCDElet) participante);
            }
            if (participante instanceof ParticipanteCDONS) {
                participante = buscaDadosCDONS((ParticipanteCDONS) participante);
            }


            /**
             * buscando dependentes do participante - modelo novo. 
             */
            participante.setDependentes(this.buscaDependentes(participante.getNumEletros()));


            /**
             * Busca informa��es sobre as patrocinadoras do participante.
             */
            query = "select his.cd_pat, pat.de_pat, his.dt_his_pat_ads, his.dt_his_pat_dem " +
                    "from his_elet_pat his, pat_elet_2 pat " +
                    "where his.cd_pat = pat.cd_etd_idt " +
                    "and his.nu_pti_icr = '" + participante.getNumEletros() + "' " +
                    "order by his.cd_his_pat_ord";

            Statement patStatement = conn.createStatement();
            ResultSet patResult = patStatement.executeQuery(query);

            while (patResult.next()) {
                Patrocinadora pat = new Patrocinadora();
                pat.setCodigo(patResult.getInt("CD_PAT"));
                pat.setNome(patResult.getString("DE_PAT"));
                pat.setDataInscricao(new DataEletros(patResult.getDate("dt_his_pat_ads").getTime()));
                if (patResult.getDate("dt_his_pat_dem") != null) {
                    pat.setDataDesligamento(new DataEletros(patResult.getDate("dt_his_pat_dem").getTime()));
                }
                participante.addPatrocinadora(pat);
            }

            patResult.close();
            patStatement.close();
        }
        closeConnection(conn);

        return participante;
    }

    public Pensionista buscaPensionista(String numEletros)
            throws Exception {
        if (numEletros.length() < 7) {
            int numCarac = numEletros.length();
            int numzeros = 7 - numCarac;
            String num = "";
            for (int i = 0; i < numzeros; i++) {
                num = num + "0";
            }
            numEletros = num + numEletros;
        }

        Pensionista pen = null;

        Connection conn = getConnection();

        String query = "SELECT ETD.CD_PES, " +
                " P.CD_ETD_IDT, PF.NU_PF_IDE, PF.NU_PF_CPF, PF.DT_PF_NAS, PF.CD_PF_SEX," +
                "ETD.NM_ETD, P.DT_PNS_ICR_ELET, P.DT_PNS_DLG " +
                "FROM PNS_CAD_BRC P, PF_ELET PF, ETD_ELET ETD " +
                "WHERE PF.CD_ETD_IDT = P.CD_ETD_IDT " +
                "AND ETD.CD_ETD_IDT = P.CD_ETD_IDT " +
                "AND P.NU_PNS_ICR = '" + numEletros + "'";

        Statement novoStatement = conn.createStatement();
        ResultSet novoResult = novoStatement.executeQuery(query);

        if (novoResult.next()) {
            pen = new Pensionista();
            pen.setNsu(new Long(novoResult.getString("CD_ETD_IDT")));
            pen.setRG(novoResult.getString("NU_PF_IDE"));
            pen.setCPF(novoResult.getString("NU_PF_CPF"));
            pen.setNumEletros(numEletros);
            pen.setSexo(novoResult.getString("CD_PF_SEX"));
            pen.setNome(novoResult.getString("NM_ETD"));
            pen.setDtNascimento(new DataEletros(novoResult.getDate("DT_PF_NAS").getTime()));
            pen.setDtInscricao(new DataEletros(novoResult.getDate("DT_PNS_ICR_ELET").getTime()));
            if (novoResult.getDate("DT_PNS_DLG") != null) {
                pen.setDtDesligamento(new DataEletros(novoResult.getDate("DT_PNS_DLG").getTime()));
            }
        }

        /**
         * buscando dependentes do participante - modelo novo. 
         */
        //participante.setDependentes( this.buscaDependentes( participante.getNumEletros() )); 
        /**
         * Busca informa��es sobre as patrocinadoras do participante.
         */
        /*query = "select his.cd_pat, pat.de_pat, his.dt_his_pat_ads, his.dt_his_pat_dem "+
        "from his_elet_pat his, pat_elet_2 pat "+
        "where his.cd_pat = pat.cd_etd_idt "+
        "and his.nu_pti_icr = '"+pen.getNumEletros()+"' "+
        "order by his.cd_his_pat_ord";
        
        Statement patStatement = conn.createStatement();
        ResultSet patResult = patStatement.executeQuery( query );
        
        while( patResult.next() )
        {
        Patrocinadora pat = new Patrocinadora(  );
        pat.setCodigo(patResult.getInt("CD_PAT"));
        pat.setNome( patResult.getString("DE_PAT"));
        pat.setDataInscricao( new DataEletros(patResult.getDate("dt_his_pat_ads").getTime()) );
        if( patResult.getDate("dt_his_pat_dem") != null )
        pat.setDataDesligamento( new DataEletros(patResult.getDate("dt_his_pat_dem").getTime()) );
        
        pen.addPatrocinadora( pat );
        }*/
        //patResult.close();
        //patStatement.close();
        //busca dados adicional
        query = "SELECT * " +
                "             FROM ACO_ELET_ADI_ASD A,epe_bnf E " +
                "             WHERE A.NU_ACO_ICR =  '" + pen.getNumEletros() + "' and E.CD_epe = A.CD_ACO_EPE";

        novoResult = novoStatement.executeQuery(query);

        if (novoResult.next()) {
            AcordoAdicional acordo = new AcordoAdicional();
            acordo.setAdicionalDevDIB(novoResult.getDouble("VA_ACO_ADI_DVD"));
            acordo.setAdicionalDevDIBURE(novoResult.getDouble("VA_ACO_ADI_DVD_URE"));
            acordo.setAdicionalPagoDIB(novoResult.getDouble("VA_ACO_ADI_CND"));
            acordo.setAdicionalPagoDIBURE(novoResult.getDouble("VA_ACO_ADI_CND_URE"));
            acordo.setCoefCalculoAdicDib(novoResult.getDouble("VA_ACO_CFT_CAL"));
            acordo.setDataDIB(novoResult.getString("AA_ACO_MM_BNF"));
            acordo.setDifAdic(novoResult.getDouble("VA_ACO_DIF_ADI"));
            acordo.setDifAdicURE(novoResult.getDouble("VA_ACO_ADI_DIF_URES"));
            acordo.setEspecieBenef(novoResult.getString("DE_EPE"));
            acordo.setSalarioMinimoDIB(novoResult.getDouble("VA_ACO_SMR_DIB"));
            acordo.setSalarioRealBenef(novoResult.getDouble("VA_ACO_SRB"));
            acordo.setTetoContribPrevSocialDIB(novoResult.getDouble("VA_ACO_TCPS_DIB"));
            acordo.setTipoContrato(novoResult.getString("CD_ACO_TIP"));
            acordo.setTotalAdic(novoResult.getDouble("VA_ACO_ADI_ATS"));
            acordo.setTotalAdicURE(novoResult.getDouble("VA_ACO_ADI_ATS_URE"));
            acordo.setTotalJuros(novoResult.getDouble("VA_ACO_JRO"));
            acordo.setTotalJurosURE(novoResult.getDouble("VA_ACO_JRO_URE"));
            acordo.setUREDIB(novoResult.getDouble("VA_ACO_URE_DIB"));

            if (acordo.getDataDIB() != null) {
                String mes = acordo.getDataDIB().substring(4, 6);
                String ano = acordo.getDataDIB().substring(0, 4);
                acordo.setDataDIB(mes + "/" + ano);
            }

            String flagJud = novoResult.getString("FL_ACO_ACA_JUD");
            if (flagJud.equals("S")) {
                pen.setAcaoJudicial(true);
            } else {
                pen.setAcaoJudicial(false);
            }
            pen.setAcordoAdicional(acordo);

        }
        novoResult.close();

        //busca mem?ria de c?lculo
        query = "SELECT * " +
                "             FROM ACO_ELET_DMO A " +
                "             WHERE A.NU_ACO_ICR =  '" + pen.getNumEletros() + "'";

        novoResult = novoStatement.executeQuery(query);
        ArrayList<CalculoAcordo> lista = new ArrayList<CalculoAcordo>();

        while (novoResult.next()) {

            CalculoAcordo cal = new CalculoAcordo();
            cal.setAno(novoResult.getInt("AA_ACO"));
            cal.setMes(novoResult.getInt("MM_ACO"));
            cal.setEvento(novoResult.getString("CD_EVE"));
            cal.setValorDevido(novoResult.getDouble("VA_ACO_DVD"));
            cal.setValorConcedido(novoResult.getDouble("VA_ACO_CON"));
            cal.setValorDiferenca(novoResult.getDouble("VA_ACO_DIF_MOE"));
            cal.setValorDiferencaURE(novoResult.getDouble("VA_ACO_DIF_URE"));
            cal.setValorJurosURE(novoResult.getDouble("VA_ACO_JRO_URE"));
            cal.setDifAtualizada(novoResult.getDouble("VA_ACO_DIF_ATU"));

            lista.add(cal);
        }
        if (lista.size() > 0) {
            AcordoAdicional acordo = pen.getAcordoAdicional();
            acordo.setListaCalculo(lista);
            pen.setAcordoAdicional(acordo);
        }
        novoResult.close();
        novoStatement.close();
        conn.close();

        return pen;
    }

    /**
     * Busca uma lista com as declara��es de tempo de servi�o do participante.
     * Objeto DTS contendo o nome da empresa, data de in�cio e fim na mesma, dentre
     * outras informa��es.
     * 
     * @param part - Participante
     * @return Collection<DTS>
     * @throws Exception
     */
    public Collection<DTS> buscaDTSParticipante(Participante part)
            throws Exception {
        Connection conn = getConnection();
        List<DTS> lista = new LinkedList<DTS>();

        //busca dados sobre tempo de servi�o
        String query = "SELECT * " +
                "             FROM DECLARACOES " +
                "             WHERE N_ELETROS =  '" + part.getNumEletros() + "' " +
                "             ORDER BY DATA_INICIO";

        Statement dtsStatement = conn.createStatement();
        ResultSet dtsResult = dtsStatement.executeQuery(query);
        while (dtsResult.next()) {

            DTS dts = new DTS();
            dts.setEmpresa(dtsResult.getString("EMPRESA"));
            dts.setDataInicio(new DataEletros(dtsResult.getDate("DATA_INICIO").getTime()));
            if (dtsResult.getDate("DATA_FIM") != null) {
                dts.setDataFim(new DataEletros(dtsResult.getDate("DATA_FIM").getTime()));
            }
            dts.setSb40(dtsResult.getString("SB40"));
            dts.setAposEspecial(dtsResult.getString("APOS_ESPECIAL"));
            dts.setRestINSS(dtsResult.getString("REC_INSS"));
            dts.setRestEletros(dtsResult.getString("REC_ELETROS"));

            lista.add(dts);

        }
        dtsResult.close();
        dtsStatement.close();
        conn.close();

        return lista;
    }

    /**
     * Busca uma lista de joias pagas pelo participante, contendo o valor
     * e a data do pagamento.
     * @param numEletros
     * @return Collection<Joia>
     * @throws Exception
     */
    public Collection<Joia> buscaJoiasPagas(String numEletros)
            throws Exception {
        LinkedList<Joia> list = new LinkedList<Joia>();
        Connection conn = getConnection();
        Statement stm = conn.createStatement();

        String query = "Select DTMESREF, VRINF " +
                "From MOVCTA " +
                "Where NRISC = '00" + numEletros + "' " +
                "And CDRUB = '0314' " +
                "Order By DTMESREF";
        ResultSet result = stm.executeQuery(query);

        while (result.next()) {
            Joia joia = new Joia();
            joia.setDataRef(result.getString("DTMESREF"));
            joia.setValor(result.getDouble("VRINF"));

            list.add(joia);
        }

        result.close();
        stm.close();
        conn.close();

        return list;
    }

    /**
     * Busca as �ltimas 36 remunera��es do participante, apartir de uma dataFim.
     * 
     * @param part - Participante
     * @param dataFim - Data fim da pesquisa
     * @return
     * @throws Exception
     */
    public Collection<Double> buscaRemuneracoes(Participante part, DataEletros dataFim)
            throws Exception {
        LinkedList<Double> list = new LinkedList<Double>();
        Connection conn = getConnection();
        Statement stm = conn.createStatement();

        String query = "Select DTMESREF, VRINF " +
                "From MOVCTA " +
                "Where NRISC = '00" + part.getNumEletros() + "' " +
                "And CDRUB = '0314' " +
                "Order By DTMESREF";
        ResultSet result = stm.executeQuery(query);

        while (result.next()) {

            list.add(result.getDouble(1));
        }

        result.close();
        stm.close();
        conn.close();

        return list;

    }

    /**
     * Busca informa��es espec�ficas para os participante do plano BD.
     * @param part - ParticipanteBD
     * @return ParticipanteBD
     * @throws Exception
     */
    private ParticipanteBD buscaDadosBD(ParticipanteBD part)
            throws Exception {
        Connection conn = getConnection();

        //busca dados migracao BD
        /*String query = "SELECT EXT.VA_EXT_RSV_MIG, EXT.DT_EXT_BNF, EXT.VA_EXT_BNF, " +
        "             EXT.DT_EXT_BPD, EXT.VA_EXT_BPD, CD_EXT_RGL, DT_EXT_REF " +
        "             FROM EXT_ELET_SMP EXT " +
        "             WHERE EXT.NU_EXT_ICR =  '" + part.getNumEletros() + "' " +
        "             and ts_ext = ( select max(ts_ext) from EXT_ELET_SMP EXT2 " +
        "                                               where EXT.NU_EXT_ICR = EXT2.NU_EXT_ICR )";

         */
        //busca dados assistidos BD
        String query = "select mm_fic,aa_fic, VA_FIC_ELET " +
                "from fic_flh " +
                "where cd_fic_icr='" + part.getNumEletros() + "' " +
                "and cd_eve in ('102','103','104','105','202','203','204','205') " +
                "and aa_fic*100 + mm_fic = " +
                "(Select max(aa_fic*100 + mm_fic) " +
                "From fic_flh f " +
                "Where f.cd_fic_icr = fic_flh.cd_fic_icr)";
        Statement bdStatement = conn.createStatement();
        ResultSet bdResult = bdStatement.executeQuery(query);
        while (bdResult.next()) {
            //part.setReservaMatematica(bdResult.getDouble("VA_EXT_RSV_MIG"));
            //part.setReservaPoupanca(bdResult.getDouble("VA_EXT_RSV_MIG"));
            //part.setInicioBeneficio(new DataEletros(bdResult.getDate("DT_EXT_BNF").getTime()));
            part.setValorBeneficio(part.getValorBeneficio() + bdResult.getDouble(3));
            //part.setInicioBPDS(new DataEletros(bdResult.getDate("DT_EXT_BPD").getTime()));
            //part.setValorBPDS(bdResult.getDouble("VA_EXT_BPD"));
            //part.setDataRef(bdResult.getString("DT_EXT_REF"));

        }
        bdResult.close();

        //busca dados adicional
        query = "SELECT * " +
                "             FROM ACO_ELET_ADI_ASD A,epe_bnf E " +
                "             WHERE A.NU_ACO_ICR =  '" + part.getNumEletros() + "' and E.CD_epe = A.CD_ACO_EPE";

        bdStatement = conn.createStatement();
        bdResult = bdStatement.executeQuery(query);

        if (bdResult.next()) {
            AcordoAdicional acordo = new AcordoAdicional();
            acordo.setAdicionalDevDIB(bdResult.getDouble("VA_ACO_ADI_DVD"));
            acordo.setAdicionalDevDIBURE(bdResult.getDouble("VA_ACO_ADI_DVD_URE"));
            acordo.setAdicionalPagoDIB(bdResult.getDouble("VA_ACO_ADI_CND"));
            acordo.setAdicionalPagoDIBURE(bdResult.getDouble("VA_ACO_ADI_CND_URE"));
            acordo.setCoefCalculoAdicDib(bdResult.getDouble("VA_ACO_CFT_CAL"));
            acordo.setDataDIB(bdResult.getString("AA_ACO_MM_BNF"));
            acordo.setDifAdic(bdResult.getDouble("VA_ACO_DIF_ADI"));
            acordo.setDifAdicURE(bdResult.getDouble("VA_ACO_ADI_DIF_URES"));
            acordo.setEspecieBenef(bdResult.getString("DE_EPE"));
            acordo.setSalarioMinimoDIB(bdResult.getDouble("VA_ACO_SMR_DIB"));
            acordo.setSalarioRealBenef(bdResult.getDouble("VA_ACO_SRB"));
            acordo.setTetoContribPrevSocialDIB(bdResult.getDouble("VA_ACO_TCPS_DIB"));
            acordo.setTipoContrato(bdResult.getString("CD_ACO_TIP"));
            acordo.setTotalAdic(bdResult.getDouble("VA_ACO_ADI_ATS"));
            acordo.setTotalAdicURE(bdResult.getDouble("VA_ACO_ADI_ATS_URE"));
            acordo.setTotalJuros(bdResult.getDouble("VA_ACO_JRO"));
            acordo.setTotalJurosURE(bdResult.getDouble("VA_ACO_JRO_URE"));
            acordo.setUREDIB(bdResult.getDouble("VA_ACO_URE_DIB"));

            if (acordo.getDataDIB() != null) {
                String mes = acordo.getDataDIB().substring(4, 6);
                String ano = acordo.getDataDIB().substring(0, 4);
                acordo.setDataDIB(mes + "/" + ano);
            }

            String flagJud = bdResult.getString("FL_ACO_ACA_JUD");
            if (flagJud.equals("S")) {
                part.setAcaoJudicial(true);
            } else {
                part.setAcaoJudicial(false);
            }
            part.setAcordoAdicional(acordo);

        }
        bdResult.close();

        //busca memoria de calculo
        query = "SELECT * " +
                "             FROM ACO_ELET_DMO A " +
                "             WHERE A.NU_ACO_ICR =  '" + part.getNumEletros() + "'";

        bdStatement = conn.createStatement();
        bdResult = bdStatement.executeQuery(query);
        ArrayList<CalculoAcordo> lista = new ArrayList<CalculoAcordo>();

        while (bdResult.next()) {

            CalculoAcordo cal = new CalculoAcordo();
            cal.setAno(bdResult.getInt("AA_ACO"));
            cal.setMes(bdResult.getInt("MM_ACO"));
            cal.setEvento(bdResult.getString("CD_EVE"));
            cal.setValorDevido(bdResult.getDouble("VA_ACO_DVD"));
            cal.setValorConcedido(bdResult.getDouble("VA_ACO_CON"));
            cal.setValorDiferenca(bdResult.getDouble("VA_ACO_DIF_MOE"));
            cal.setValorDiferencaURE(bdResult.getDouble("VA_ACO_DIF_URE"));
            cal.setValorJurosURE(bdResult.getDouble("VA_ACO_JRO_URE"));
            cal.setDifAtualizada(bdResult.getDouble("VA_ACO_DIF_ATU"));

            lista.add(cal);
        }
        if (lista.size() > 0) {
            AcordoAdicional acordo = part.getAcordoAdicional();
            acordo.setListaCalculo(lista);
            part.setAcordoAdicional(acordo);
        }
        bdResult.close();
        bdStatement.close();
        conn.close();

        //busca o regulamento
        return buscaRegulamentoBD(part);
    }

    /**
     * Busca o regulamento do participante do plano BD
     * @param part
     * @return
     * @throws Exception
     */
    private ParticipanteBD buscaRegulamentoBD(ParticipanteBD part)
            throws Exception {
        Connection conn = getConnection();

        String query = "select cd_rgl from rgl_elet, pti_cad " +
                "where pti_cad.nu_pti_icr = '" + part.getNumEletros() + "' " +
                "and pti_cad.dt_pti_icr_elet >= rgl_elet.dt_rgl_ini " +
                "and (pti_cad.dt_pti_icr_elet <= rgl_elet.dt_rgl_fim or rgl_elet.dt_rgl_fim is Null)";
        Statement stm = conn.createStatement();
        ResultSet result = stm.executeQuery(query);
        if (result.next()) {
            part.setRegulamento(result.getString(1));
        }

        result.close();
        stm.close();
        conn.close();

        return part;
    }

    /**
     * Busca dados de reserva do participante separados por conta
     * conta 01 - participante
     * conta 02 - extra participante
     * conta 03 - patrocinadora
     * conta 04 - aposentado
     * conta 13 - portabilidade
     *
     */
    public double buscaSaldoReservaAtualParticipanteCD(String numEletros, String conta)
            throws Exception {

        double totalQtdCotas = 0.0;
        double reserva = 0.0;
        String plano = null;

        Connection conn = getConnection();

        String query = "SELECT S.DTMESAPR, S.CDCTA, S.QTCOTCTB, P.NRPLA " +
                "FROM  SDOCTA S, PARPLA P " +
                "WHERE S.CDFUN = P.CDFUN " +
                "AND   S.CDPAT = P.CDPAT " +
                "AND   S.NRPLA = P.NRPLA " +
                "AND   S.NRISC = P.NRISC " +
                "AND   S.NRISC = '00" + numEletros + "' " +
                "AND   S.CDCTA = '" + conta + "' " +
                "AND   S.DTMESAPR = (SELECT MAX(S2.DTMESAPR) " +
                "FROM  SDOCTA S2 " +
                "WHERE S2.CDFUN = S.CDFUN " +
                "AND   S2.CDPAT = S.CDPAT " +
                "AND   S2.NRPLA = S.NRPLA " +
                "AND   S2.NRISC = S.NRISC " +
                "AND   S2.CDCTA = S.CDCTA)";

        Statement stm = conn.createStatement();
        ResultSet result = stm.executeQuery(query);
        if (result.next()) {

            totalQtdCotas = result.getDouble("QTCOTCTB");
            plano = result.getString("NRPLA");

            IndiceGeral indice = null;
            if (plano.equals("02")) {
                indice = indiceEJB.buscaUltimoValorIndice("COTA_CD_ONS");
            } else if (plano.equals("03")) {
                indice = indiceEJB.buscaUltimoValorIndice("COTA_CD_PURO");
            } else {
                indice = indiceEJB.buscaUltimoValorIndice("COTA_CD_SALDADO");
            }

            reserva = totalQtdCotas * indice.getValor();
        }

        result.close();
        stm.close();
        conn.close();

        return reserva;
    }

    /**
     * Busca informacoes especificas para o participante do plano CD ONS.
     * @param part - ParticipanteCDONS
     * @return ParticipanteCDONS
     * @throws Exception
     */
    private ParticipanteCDONS buscaDadosCDONS(ParticipanteCDONS part)
            throws Exception {
        double reserva = this.buscaSaldoReservaAtualParticipanteCD(part.getNumEletros(), "01");
        reserva = reserva + buscaSaldoReservaAtualParticipanteCD(part.getNumEletros(), "02");
        reserva = reserva + buscaSaldoReservaAtualParticipanteCD(part.getNumEletros(), "03");
        reserva = reserva + buscaSaldoReservaAtualParticipanteCD(part.getNumEletros(), "04");
        reserva = reserva + buscaSaldoReservaAtualParticipanteCD(part.getNumEletros(), "13");

        part.setReservaMatematica(reserva);
        part.setDataReservaMatematica(new DataEletros(System.currentTimeMillis()));

        return part;
    }

    /**
     * Busca informa��es espec�ficas para o participante do plano CD Elet.
     * @param part - ParticipanteCDElet
     * @return ParticipanteCDElet
     * @throws Exception
     */
    private ParticipanteCDElet buscaDadosCDElet(ParticipanteCDElet part)
            throws Exception {
        Connection conn = getConnection();

        /*String query = "select RSV.VA_RSV_MAT_CD, NVL(RSV.VA_RSV_JOI,0)," + 
        "       NVL(RSV.VA_RSV_DOT_CD,0),NVL(RSV.VA_RSV_POU_ICR_ANT,0), RSV.aa_rsv," + 
        "       RSV.mm_rsv," + 
        "       INDEXADOR_VALOR.VL_INDEXADOR,RSV.nu_pti_icr, " + 
        "       SAL.OPCMIG, RSV.VA_RSV_BPD, RSV.DT_RSV_BPD, RSV.ts_rsv "+ 
        "    From RSV_ELET_PTI_V3 RSV, INDEXADOR_VALOR, SALDOSMIGCD SAL " + 
        "    Where INDEXADOR_VALOR.ID_INDEXADOR = 'ELETROS-PU' " + 
        "       And INDEXADOR_VALOR.DT_INDEXADOR = (Select Max(T.DT_INDEXADOR) From INDEXADOR_VALOR T " + 
        "                             Where T.ID_INDEXADOR = INDEXADOR_VALOR.ID_INDEXADOR " + 
        "                             And To_Char(T.DT_INDEXADOR, 'yyyymm') = Trim(To_Char(RSV.aa_rsv, '0000'))||Trim(To_Char(RSV.mm_rsv, '00'))) "+
        "       and SAL.NRISCBD = '00'||RSV.nu_pti_icr " +
        "       and SAL.NRISC = '00'||'"+part.getNumEletros()+"' "+
        "       and RSV.ts_rsv = "+
        "               (select max(rsv2.ts_rsv) from RSV_ELET_PTI_V3 rsv2 "+
        "                                        where rsv2.nu_pti_icr=rsv.nu_pti_icr)";*/
        String query = "select RSV.VA_RSV_MAT_CD, NVL(RSV.VA_RSV_JOI,0)," +
                "       NVL(RSV.VA_RSV_DOT_CD,0),NVL(RSV.VA_RSV_POU_ICR_ANT,0), RSV.aa_rsv," +
                "       RSV.mm_rsv, RSV.VA_RSV_MAT_BPD," +
                "       RSV.nu_pti_icr, " +
                "       SAL.OPCMIG, RSV.VA_RSV_BPD, RSV.DT_RSV_BPD, RSV.ts_rsv " +
                "    From RSV_ELET_PTI_V3 RSV, SALDOSMIGCD SAL " +
                "    Where SAL.NRISCBD = '00'||RSV.nu_pti_icr " +
                "       and SAL.NRISC = '00'||'" + part.getNumEletros() + "' " +
                "       and RSV.ts_rsv = " +
                "               (select max(rsv2.ts_rsv) from RSV_ELET_PTI_V3 rsv2 " +
                "                                        where rsv2.nu_pti_icr=rsv.nu_pti_icr)";

        Statement stm = conn.createStatement();
        double totalQtdCota = 0.0;
        double totalQtdCotaBPDS = 0.0;
        ResultSet result = stm.executeQuery(query);

        if (result.next()) {

            String dia;
            String mes = result.getString("mm_rsv");
            String ano = result.getString("aa_rsv");
            if (mes.length() == 1) {
                mes = "0" + mes;
            }
            part.setDataRefMigracao(mes + "/" + ano);

            if (mes.equals("02")) {
                dia = "28";
            } else if (mes.equals("01") || mes.equals("03") || mes.equals("05") || mes.equals("07") || mes.equals("08") || mes.equals("10") || mes.equals("12")) {
                dia = "31";
            } else {
                dia = "30";
            }
            Collection indicesCD = indiceEJB.buscaIndicesGerais("01", "COTA_CD_PURO", "01/" + mes + "/" + ano, dia + "/" + mes + "/" + ano);
            Collection indicesBPDS = indiceEJB.buscaIndicesGerais("01", "COTA_CD_SALD", "01/" + mes + "/" + ano, dia + "/" + mes + "/" + ano);
            IndiceGeral indiceCD = (IndiceGeral) indicesCD.iterator().next();
            IndiceGeral indiceBPDS = null;
            if (indicesBPDS.size() > 0) {
                indiceBPDS = (IndiceGeral) indicesBPDS.iterator().next();
            } else {
                indiceBPDS = new IndiceGeral("01", "COTA_CD_SALDADO");
                indiceBPDS.setValor(1.0);
            }

            part.setNumEletrosBD(result.getString("nu_pti_icr"));
            part.setValorIndiceMigracao(indiceCD.getValor());
            //if() 
            //part.setDataReservaPoupanca( new DataEletros(result.getDate(11).getTime()));

            String opcao = result.getString("OPCMIG");
            part.setOpcaoMigracao(opcao);

            if (opcao.trim().equals("1")) { //100% CD

                part.setReservaMigracao(result.getDouble("VA_RSV_MAT_CD") - result.getDouble(2) - result.getDouble(3) + result.getDouble(4));
            }
            if (opcao.trim().equals("2")) { //100% BPDS

                if (result.getDouble(4) != 0.0) {
                    part.setReservaMigracao(result.getDouble(4));
                }

                if (result.getDate("DT_RSV_BPD") != null) {
                    part.setInicioBPDSMigracao(
                            new DataEletros(result.getDate("DT_RSV_BPD").getTime()));
                }
                part.setBPDSMigracao(result.getDouble("VA_RSV_BPD"));
                part.setReservaBPDSMigracao(result.getDouble("VA_RSV_MAT_BPD") - result.getDouble(2) - result.getDouble(3) + result.getDouble(4));
            }
            if (opcao.trim().equals("3")) { //50% BPDS
                ///calculo dos 50 % de reserva CD

                part.setReservaMigracao(result.getDouble("VA_RSV_MAT_CD") - result.getDouble(2) - result.getDouble(3));
                part.setReservaMigracao(part.getReservaMigracao() / 2.0);
                part.setReservaMigracao(part.getReservaMigracao() + result.getDouble(4));

                ///calculo dos 50 % de reserva BPDS
                part.setReservaBPDSMigracao(result.getDouble("VA_RSV_MAT_BPD") - result.getDouble(2) - result.getDouble(3));
                part.setReservaBPDSMigracao(part.getReservaBPDSMigracao() / 2.0);
                part.setReservaBPDSMigracao(part.getReservaBPDSMigracao() + result.getDouble(4));

                if (result.getDate("DT_RSV_BPD") != null) {
                    part.setInicioBPDSMigracao(
                            new DataEletros(result.getDate("DT_RSV_BPD").getTime()));
                }
                part.setBPDSMigracao(result.getDouble("VA_RSV_BPD") / 2.0);
            }
            totalQtdCota = part.getReservaMigracao() / part.getValorIndiceMigracao();
            if (part.getReservaBPDSMigracao() > 0.0) {
                totalQtdCotaBPDS = part.getReservaBPDSMigracao() / indiceBPDS.getValor();
            }
            part.setQtdCotasMigradas(totalQtdCota);
            part.setQtdCotasBPDSMigradas(totalQtdCotaBPDS);

        }

        result.close();
        stm.close();
        conn.close();
        part = buscaDataMigracao(part);

        if (part.getBPDSMigracao() != 0.0) {
            part = atualizarBPDS(part);
        }

        double reserva = this.buscaSaldoReservaAtualParticipanteCD(part.getNumEletros(), "01");
        reserva = reserva + buscaSaldoReservaAtualParticipanteCD(part.getNumEletros(), "02");
        reserva = reserva + buscaSaldoReservaAtualParticipanteCD(part.getNumEletros(), "03");
        reserva = reserva + buscaSaldoReservaAtualParticipanteCD(part.getNumEletros(), "04");
        reserva = reserva + buscaSaldoReservaAtualParticipanteCD(part.getNumEletros(), "13");

        part.setReservaMatematica(reserva);
        part.setDataReservaMatematica(new DataEletros(System.currentTimeMillis()));

        if (reserva == 0.0) {
            //busca a reserva matermatica atual para participantes que n?o est?o no sistema oficial ainda
            //como os Autopatrocinadores ainda.
            //busca o EJB de extrato na base de dados
            InitialContext ctx = new InitialContext();
            Object ref = ctx.lookup("ExtratoEJB");
            ExtratoEJBHome extHome = (ExtratoEJBHome) PortableRemoteObject.narrow(ref, ExtratoEJBHome.class);
            ExtratoEJB extratoEJB = extHome.create();

            ExtratoCD extrato = extratoEJB.geraExtratoCDEletAutoPatroc(part);
            part.setReservaMatematica(extrato.getSaldoAtual());
            part.setDataReservaMatematica(new DataEletros(System.currentTimeMillis()));

            if ((part.getStatus().equals("Assistido")) ||
                    (part.getStatus().equals("Assistido SEM complementacao de pensao"))) {

                part = buscaBeneficioCD(part);
            }

        }
        return part;
    }

    private ParticipanteCDElet buscaBeneficioCD(ParticipanteCDElet part)
            throws Exception {
        Connection conn = getConnection();

        //beneficio CD Puro
        String query = "select mm_fic,aa_fic, VA_FIC_ELET " +
                "from fic_flh " +
                "where cd_fic_icr='" + part.getNumEletros() + "' " +
                "and cd_eve in ('107','207') " +
                "and aa_fic*100 + mm_fic = " +
                "(Select max(aa_fic*100 + mm_fic) " +
                "From fic_flh f " +
                "Where f.cd_fic_icr = fic_flh.cd_fic_icr)";
        Statement stm = conn.createStatement();
        ResultSet result = stm.executeQuery(query);
        while (result.next()) {
            //part.setReservaMatematica(bdResult.getDouble("VA_EXT_RSV_MIG"));
            //part.setReservaPoupanca(bdResult.getDouble("VA_EXT_RSV_MIG"));
            //part.setInicioBeneficio(new DataEletros(bdResult.getDate("DT_EXT_BNF").getTime()));
            part.setValorBeneficioCD(part.getValorBeneficioCD() + result.getDouble(3));
            //part.setInicioBPDS(new DataEletros(bdResult.getDate("DT_EXT_BPD").getTime()));
            //part.setValorBPDS(bdResult.getDouble("VA_EXT_BPD"));
            //part.setDataRef(bdResult.getString("DT_EXT_REF"));

        }

        //beneficio BPDS
        part.setBPDSAtual(0.0);
        query = "select mm_fic,aa_fic, VA_FIC_ELET " +
                "from fic_flh " +
                "where cd_fic_icr='" + part.getNumEletros() + "' " +
                "and cd_eve in ('108','208') " +
                "and aa_fic*100 + mm_fic = " +
                "(Select max(aa_fic*100 + mm_fic) " +
                "From fic_flh f " +
                "Where f.cd_fic_icr = fic_flh.cd_fic_icr)";
        result = stm.executeQuery(query);
        while (result.next()) {
            //part.setReservaMatematica(bdResult.getDouble("VA_EXT_RSV_MIG"));
            //part.setReservaPoupanca(bdResult.getDouble("VA_EXT_RSV_MIG"));
            //part.setInicioBeneficio(new DataEletros(bdResult.getDate("DT_EXT_BNF").getTime()));
            part.setBPDSAtual(part.getBPDSAtual() + result.getDouble(3));
            //part.setInicioBPDS(new DataEletros(bdResult.getDate("DT_EXT_BPD").getTime()));
            //part.setValorBPDS(bdResult.getDouble("VA_EXT_BPD"));
            //part.setDataRef(bdResult.getString("DT_EXT_REF"));

        }

        result.close();
        stm.close();
        conn.close();

        return part;
    }

    /**
     * M�todo para reajustar o valor do BPDS do participante CD Elet, tendo ele 
     * optado por 50% ou 100% BPDS na migra��o. 
     * Utiliza �ndices IAP para reajustar o valor, tomando por base a data de migra��o e o m�s 
     * de vencimento de reajuste anual que � em junho.
     * 
     * @param part - ParticipanteCDElet
     * @return ParticipanteCDElet
     * @throws Exception
     */
    private ParticipanteCDElet atualizarBPDS(ParticipanteCDElet part)
            throws Exception {
        double bpds = part.getBPDSMigracao();

        DataEletros dataAtual = new DataEletros(System.currentTimeMillis());
        GregorianCalendar calendar = new GregorianCalendar();
        calendar.setTime(dataAtual);
        DataEletros dataFim = null;
        GregorianCalendar iniCalendar = new GregorianCalendar();
        iniCalendar.setTime(part.getDataMigracao());

        if (calendar.get(Calendar.MONTH) >= 5) {
            dataFim = new DataEletros("01/05/" + calendar.get(Calendar.YEAR));
            System.out.println(dataFim);
        } else {
            dataFim = new DataEletros("01/05/" + (calendar.get(Calendar.YEAR) - 1));
            System.out.println(dataFim);
        }

        int iniMonth = iniCalendar.get(Calendar.MONTH) + 1;
        String initialMonth = String.valueOf(iniMonth);
        if (iniMonth < 10) {
            initialMonth = "0" + initialMonth;
        }

        System.out.println("Datas " + "01/" + initialMonth + "/" + iniCalendar.get(Calendar.YEAR) + " e " + dataFim.toString());
        Collection<IndiceGeral> indices =
                indiceEJB.buscaIndicesGerais("030", "IAP",
                "01/" + initialMonth + "/" + iniCalendar.get(Calendar.YEAR), dataFim.toString());
        double fator = 1.0;
        for (IndiceGeral indice : indices) {
            double i = indice.getValor();
            i = (i / 100.0) + 1.0;
            fator = fator * i;
        }
        bpds = bpds * fator;
        part.setBPDSAtual(bpds);

        return part;
    }

    /**
     * Busca a data de migra��o de um participante CD Elet.
     * @param part - ParticipanteCDElet
     * @return ParticipanteCDElet
     * @throws Exception
     */
    private ParticipanteCDElet buscaDataMigracao(ParticipanteCDElet part)
            throws Exception {
        Connection conn = getConnection();

        String query = "select dt_sit_ini_vig " +
                "    From sit_cad_pf sit " +
                "    Where sit.nu_sit_icr = '" + part.getNumEletros() + "' " +
                "       And sit.cd_sit=11";

        Statement stm = conn.createStatement();
        ResultSet result = stm.executeQuery(query);

        if (result.next()) {
            part.setDataMigracao(new DataEletros(result.getDate(1).getTime()));
        }

        result.close();
        stm.close();
        conn.close();

        return part;
    }

    /**
     *  Busca uma lista de dependentes relativos ao 
     *  n�mero de inscri��o do participante e seus respectivos v�nculos com esse
     *  participante. 
     *  
     *  @return java.util.List
     *  @throws SQLException
     */
    private List<Dependente> buscaDependentes(String numEletros) throws SQLException {
        List<Dependente> dependentes = new LinkedList<Dependente>();

        Connection conn = getConnection();

        /*String query = "SELECT E.CD_ETD_IDT, E.NM_ETD, P.DT_PF_NAS "+
        "FROM VCL_ELET_PF V, ETD_ELET E, PF_ELET P "+
        "WHERE V.NSU_TIT = "+codPessoaTit+" "+
        "AND V.NSU_VCL = E.CD_ETD_IDT "+
        "AND E.CD_ETD_IDT = P.CD_ETD_IDT";
         */
        String query = "Select Distinct ETD_ELET.NM_ETD," +
                "PF_ELET.DT_PF_NAS," +
                "PNT_ELET.DE_PNT," +
                "ETD_ELET.CD_ETD_IDT," +
                "VCL_ELET_PF.CD_PNT " +
                "From ETD_ELET, PF_ELET, VCL_ELET_PF, PNT_ELET " +
                "Where ETD_ELET.CD_ETD_IDT = PF_ELET.CD_ETD_IDT " +
                "And VCL_ELET_PF.CD_PNT = PNT_ELET.CD_PNT(+) " +
                "And VCL_ELET_PF.NSU_VCL = ETD_ELET.CD_ETD_IDT " +
                "And VCL_ELET_PF.NU_VCL_ICR = '" + numEletros + "' " +
                "And VCL_ELET_PF.CD_VCL_PRD = 'CAD' " +
                "And Exists (Select 1 From VCL_ELET_PF V " +
                "Where V.NSU_TIT = VCL_ELET_PF.NSU_TIT " +
                "And V.NSU_VCL = VCL_ELET_PF.NSU_VCL " +
                "And V.CD_VCL_PRD = VCL_ELET_PF.CD_VCL_PRD " +
                "And V.FL_VCL_VLD = VCL_ELET_PF.FL_VCL_VLD " +
                "And V.DT_FIM_VCL_REF Is Null )" +
                "And VCL_ELET_PF.FL_VCL_VLD = 'S'";

        Statement statement = conn.createStatement();
        ResultSet result = statement.executeQuery(query);

        while (result.next()) {
            Dependente dependente = new Dependente();

            dependente.setNsu(new Long(result.getString(4)));
            dependente.setNome(result.getString(1));
            if (result.getDate(2) != null) {
                dependente.setDtNascimento(new DataEletros(result.getDate(2).getTime()));
            }
            dependente.setParentesco(result.getString(3));
            dependente.setTiposVinculo(this.buscaTiposVinculos(dependente, numEletros));

            dependentes.add(dependente);
        }

        result.close();
        statement.close();

        closeConnection(conn);


        return dependentes;
    }

    /**
     * Busca um lista de Vinculos relativos a um dependente de um participante
     * 
     * @param dep - Dependente
     * @param numEletros - n�mero eletros do participante
     * @return - Collection<Vinculo>
     * @throws SQLException
     */
    private Collection<Vinculo> buscaTiposVinculos(Dependente dep, String numEletros)
            throws SQLException {
        List<Vinculo> vinculos = new LinkedList<Vinculo>();

        Connection conn = getConnection();

        String query = "Select VCL_ELET.DE_VCL," +
                "VCL_ELET_PF.CD_VCL_TIP," +
                "VCL_ELET_PF.DT_INI_VCL_REF," +
                "VCL_ELET_PF.DT_FIM_VCL_REF," +
                "Decode( VCL_ELET_PF.CD_VCL_MOT_CAN, 'FD', 'Falecimento Dependende'," +
                "'FT', 'Falecimento Titular'," +
                "'DD', 'Desligamento Dependente'," +
                "'DT', 'Desligamento Titular') " +
                "From VCL_ELET_PF, VCL_ELET " +
                "Where VCL_ELET_PF.CD_VCL_TIP = VCL_ELET.CD_VCL_TIP " +
                "And VCL_ELET_PF.NU_VCL_ICR = '" + numEletros + "' " +
                "And VCL_ELET_PF.NSU_VCL = " + dep.getNsu() +
                " And VCL_ELET_PF.CD_VCL_PRD = 'CAD' " +
                "And VCL_ELET_PF.FL_VCL_VLD = 'S'";

        Statement statement = conn.createStatement();
        ResultSet result = statement.executeQuery(query);

        while (result.next()) {
            Vinculo vinculo = new Vinculo();
            vinculo.setTipo(result.getString(1));
            vinculo.setDataInicio(new DataEletros(result.getDate(3).getTime()));
            if (result.getDate(4) != null) {
                vinculo.setDataFim(new DataEletros(result.getDate(4).getTime()));
            }
            vinculo.setMotivoCancelamento(result.getString(5));

            vinculos.add(vinculo);
        }

        result.close();
        statement.close();

        closeConnection(conn);

        return vinculos;
    }

    /**
     * busca uma conex�o no pool
     * @return java.sql.Connection
     * @throws SQLException
     */
    public Connection getConnection()
            throws SQLException {
        Connection conn = null;
        try {
            InitialContext initContext = new InitialContext();
            DataSource ds = (DataSource) initContext.lookup("java:/jdbc/OracleLogin");
            conn = ds.getConnection();
        } catch (NamingException ne) {
            System.out.println(ne.toString());
        }
        return conn;
    }

    /**
     * retorna a conex�o ao pool
     * @throws SQLException
     */
    public void closeConnection(Connection conn)
            throws SQLException {
        conn.close();
        System.out.println("Fechando conexao");
    }
}
