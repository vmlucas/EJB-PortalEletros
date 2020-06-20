package extrato;

import EJB.Indices.IndiceGeral;
import EJB.Indices.IndicesEJB;
import EJB.Indices.IndicesEJBHome;

import com.eletros.benef.ParticipanteCDElet;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import java.util.*;

import javax.ejb.EJBException;
import javax.ejb.SessionBean;
import javax.ejb.SessionContext;

import javax.naming.InitialContext;
import javax.naming.NamingException;

import javax.rmi.PortableRemoteObject;

import javax.sql.DataSource;
import com.util.*;

public class ExtratoEJBBean implements SessionBean {

    private SessionContext _context;
    private double sobrec_adm = 0.03;
    private double benef_risco = 0.11;
    double totalQtdCota = 0;
    double totalCotaPat = 0.0;
    double totalCotaPartic = 0.0;
    double totalCotaExtra = 0.0;
    double totalContaBenefRisco = 0.0;
    double totalCotasContaBenefRisco = 0.0;

    public void ejbCreate() {
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

    public ExtratoCD geraExtratoCDElet(ParticipanteCDElet part) throws Exception {

        //buscando inf sobre a ultima cota
        InitialContext ctx = new InitialContext();
        Object ref = ctx.lookup("IndicesEJB");
        IndicesEJBHome home = (IndicesEJBHome) PortableRemoteObject.narrow(ref, IndicesEJBHome.class);
        IndicesEJB indicesEJB = home.create();

        IndiceGeral indice = indicesEJB.buscaUltimoValorIndice("COTA_CD_PURO");
        System.out.println("Indice " + indice);

        double valorUltimaCota = indice.getValor();
        String dataUltimaCota = indice.getDataVigencia();

        ExtratoCD extrato = new ExtratoCD();
        extrato.setNumEletros(part.getNumEletros());
        extrato.setNome(part.getNome());
        //extrato.setPatrocinadora(part.getPatrocinadoraAtual().getNome());
        extrato.setPlano("CD-ELETROBRAS");

        extrato.setValorIndiceMigracao(part.getValorIndiceMigracao());
        extrato.setSaldoMigracao(part.getReservaMigracao());
        extrato.setQtdCotasMigradas(part.getQtdCotasMigradas());
        totalQtdCota = part.getQtdCotasMigradas();

        extrato.setDataRefMigracao(part.getDataRefMigracao());
        extrato.setBPDSAtual(part.getBPDSAtual());
        if (part.getInicioBPDSMigracao() != null) {
            extrato.setInicioBPDS(new DataEletros(part.getInicioBPDSMigracao().getTime()));
        }

        ///busca lan?amentos
        List list0 = buscaLanMesMigracao(part);
        List list1 = this.buscaLanBasicos(part);
        //List list2 = this.buscaLanAdicional(part);
        List list3 = this.buscaLanDotacao(part);
        List list4 = this.buscaLan13(part);
        List list5 = this.buscaLan13Adicional(part);

        LinkedList list = new LinkedList(list0);
        list.addAll(list1);
        list.addAll(list3);
        list.addAll(list4);
        list.addAll(list5);

        List lista = ordenarLista(list);

        int tam = lista.size();
        if (tam > 0) {
            Lancamento ultimo = (Lancamento) lista.get(tam - 1);
            extrato.setUltimaDataLan(ultimo.getData());
            Lancamento prime = (Lancamento) lista.get(0);

            if (extrato.getValorIndiceMigracao() == 0.0) {
                extrato.setValorIndiceMigracao(prime.getValorCota());
            }
        }


        extrato.setLancamentos(lista);
        extrato.setSaldoCotas(totalQtdCota);

        extrato.setSaldoAtual(totalQtdCota * valorUltimaCota);
        double percent = (valorUltimaCota / extrato.getValorIndiceMigracao()) - 1;
        extrato.setRentabilidade(percent * 100);
        extrato.setValorCotaRecente(valorUltimaCota);
        extrato.setDataCotaRecente(dataUltimaCota);

        extrato.setTotalCotaPartic(totalCotaPartic);
        extrato.setTotalCotaPat(totalCotaPat);
        extrato.setTotalCotaExtra(totalCotaExtra);
        extrato.setTotalContaBenefRisco(totalContaBenefRisco);
        extrato.setTotalCotaContaBenefRisco(totalCotasContaBenefRisco);

        return extrato;
    }

    private List buscaLanMesMigracao(ParticipanteCDElet part)
            throws Exception {
        Connection conn = null;
        LinkedList list = new LinkedList();
        try {
            conn = getConnection();
            Statement stm = conn.createStatement();

            String query = "SELECT c.nrisc,c.vrinf, m.dtmesref, m.cdrub, m.vrinf, m.VRIDCCOV, m.qtcotmov " +
                    "FROM movcta_tmp c, movcta m " +
                    "WHERE c.NRISC='00" + part.getNumEletros() + "' " +
                    "and c.nrisc = m.nrisc " +
                    "and c.cdrub = '0002' " +
                    "and m.cdrub = '0044' " +
                    "and c.dtmesref = m.dtmesref " +
                    "ORDER BY c.DTMESREF, c.CDRUB";

            System.out.println(query);
            stm = conn.createStatement();
            ResultSet result = stm.executeQuery(query);

            Lancamento lan = null;

            double debitos = 0.0;

            while (result.next()) {
                String dataRef = result.getString(3);
                String anoRef = dataRef.substring(0, 4);
                String mesRef = dataRef.substring(4, 6);
                int ano = Integer.parseInt(anoRef);
                int mes = Integer.parseInt(mesRef);
                System.out.println("MES " + mes);

                if (ano >= 2008) {
                    benef_risco = 0.086;
                    if ((ano >= 2009) && (mes >= 3)) {
                        benef_risco = 0.0702;
                    }
                } else {
                    benef_risco = 0.11;
                }

                lan = new Lancamento();
                ///salario
                String querySal = "SELECT m.DTMESREF, m.CDRUB, m.VRINF " +
                        "FROM  movcta_tmp m " +
                        "WHERE m.NRISC='00" + part.getNumEletros() + "' " +
                        "AND m.CDRUB   = '0001' " +
                        "AND   m.DTMESREF = '" + dataRef + "'";

                Connection connSal = this.getConnection();
                Statement stmSal = connSal.createStatement();
                ResultSet resultSal = stmSal.executeQuery(querySal);
                if (resultSal.next()) {
                    lan.setSalario(resultSal.getDouble(3));
                }
                resultSal.close();
                stmSal.close();
                connSal.close();



                lan.setData(mesRef + "/" + dataRef.substring(0, 4));
                lan.setValorAplicBasicaParticipante(result.getDouble(2));
                debitos = debitos + (result.getDouble(2) - result.getDouble(5));

                lan.setDebitos(debitos);
                lan.setValorCota(result.getDouble(6));

                totalCotaExtra = totalCotaExtra +
                        ((lan.getValorAplicExtraParticipante() * (1 - sobrec_adm)) / lan.getValorCota());

                lan.setValorTotalAplic(
                        lan.getValorAplicBasicaParticipante() -
                        lan.getDebitos());
                lan.setQuantidadeCotaLiq(
                        lan.getValorTotalAplic() / lan.getValorCota());

                totalQtdCota = totalQtdCota + lan.getQuantidadeCotaLiq();
                debitos = 0.0;
                list.add(lan);

            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new Exception("Ocorreram problemas ao buscar o extrato CD Eletrobras do participante.");
        } finally {
            if (conn != null) {
                conn.close();
            }
        }
        return list;

    }

    private List buscaLanBasicos(ParticipanteCDElet part)
            throws Exception {
        Connection conn = null;
        LinkedList list = new LinkedList();

        String dtMig = null;
        DataEletros dataMig = part.getDataMigracao();
        if (dataMig != null) {
            int iMes = dataMig.getMes();
            String sMes = null;
            if (iMes < 10) {
                sMes = "0" + String.valueOf(iMes);
            } else {
                sMes = String.valueOf(iMes);
            }

            dtMig = String.valueOf(dataMig.getAno()) + sMes;
        }

        try {
            conn = getConnection();
            Statement stm = conn.createStatement();

            String query = "SELECT c.nrisc,c.vrinf, m.dtmesref, m.cdrub, m.vrinf, m.VRIDCCOV, m.qtcotmov " +
                    "FROM movcta_tmp c, movcta m " +
                    "WHERE c.NRISC='00" + part.getNumEletros() + "' " +
                    "and c.nrisc = m.nrisc " +
                    "and c.cdrub = m.cdrub " +
                    "and m.cdrub in ('0002','0003','0007') " +
                    "and c.dtmesref = m.dtmesref ";
            if (dtMig != null) {
                query = query +
                        "and c.dtmesref != '" + dtMig + "' ";
            }
            query = query + "and c.dtmesref = m.dtmesref " +
                    "ORDER BY c.DTMESREF, c.CDRUB";

            System.out.println(query);

            stm = conn.createStatement();
            ResultSet result = stm.executeQuery(query);

            Lancamento lan = null;

            double debitos = 0.0;
            int contMes = 1;
            String dataCov = "";
            String dataRef = "";

            while (result.next()) {
                String dataCovTemp = result.getString(5);
                String rub = result.getString(4);
                String dataRefTemp = result.getString(3);
                String anoRef = dataRefTemp.substring(0, 4);
                String mesRef = dataRefTemp.substring(4, 6);
                int ano = Integer.parseInt(anoRef);
                int mes = Integer.parseInt(mesRef);
                System.out.println("MES " + mes);

                if (ano >= 2008) {
                    benef_risco = 0.086;
                    if ((ano >= 2009) && (mes >= 3)) {
                        benef_risco = 0.0702;
                    }
                } else {
                    benef_risco = 0.11;
                }

                if ((!dataCov.equals(dataCovTemp)) ||
                        (!dataRef.equals(dataRefTemp))) {
                    dataCov = dataCovTemp;
                    dataRef = dataRefTemp;

                    if (lan != null) {

                        lan.setDebitos(debitos);
                        totalCotaPartic = totalCotaPartic +
                                ((lan.getValorAplicBasicaParticipante() * (1 - sobrec_adm - benef_risco)) / lan.getValorCota());
                        totalCotaPat = totalCotaPat +
                                ((lan.getValorAplicPatrocinadora() * (1 - sobrec_adm - benef_risco)) / lan.getValorCota());
                        totalCotaExtra = totalCotaExtra +
                                ((lan.getValorAplicExtraParticipante() * (1 - sobrec_adm)) / lan.getValorCota());

                        totalContaBenefRisco = totalContaBenefRisco + (lan.getValorAplicBasicaParticipante() * benef_risco);
                        totalContaBenefRisco = totalContaBenefRisco + (lan.getValorAplicPatrocinadora() * benef_risco);

                        totalCotasContaBenefRisco = totalCotasContaBenefRisco +
                                ((lan.getValorAplicBasicaParticipante() * benef_risco) / lan.getValorCota());
                        totalCotasContaBenefRisco = totalCotasContaBenefRisco +
                                ((lan.getValorAplicPatrocinadora() * benef_risco) / lan.getValorCota());


                        lan.setValorTotalAplic(
                                lan.getValorAplicBasicaParticipante() +
                                lan.getValorAplicPatrocinadora() +
                                lan.getValorAplicExtraParticipante() -
                                lan.getDebitos());
                        lan.setQuantidadeCotaLiq(
                                lan.getValorTotalAplic() / lan.getValorCota());

                        totalQtdCota = totalQtdCota + lan.getQuantidadeCotaLiq();
                        debitos = 0.0;
                        list.add(lan);
                        contMes++;
                        lan = new Lancamento();
                        lan.setData(dataRef.substring(4, 6) + "/" + dataRef.substring(0, 4));
                        System.out.print(dataCovTemp);
                    } else {
                        lan = new Lancamento();
                        lan.setData(dataRef.substring(4, 6) + "/" + dataRef.substring(0, 4));
                    }
                }

                ///salario
                String querySal = "SELECT H.DTMESREF, H.CDRUB, H.VRSPA " +
                        "FROM  HISALARIOS H " +
                        "WHERE H.NRISC='00" + part.getNumEletros() + "' " +
                        "AND H.CDRUB   = '0001' " +
                        "AND   H.DTMESREF = '" + dataRefTemp + "'";

                Connection connSal = this.getConnection();
                Statement stmSal = connSal.createStatement();
                ResultSet resultSal = stmSal.executeQuery(querySal);
                if (resultSal.next()) {
                    lan.setSalario(resultSal.getDouble(3));
                }
                resultSal.close();
                stmSal.close();
                connSal.close();

                if (rub.equals("0002")) {//basica participante

                    lan.setValorAplicBasicaParticipante(result.getDouble(2));
                    debitos = debitos + (result.getDouble(2) - result.getDouble(5));
                } else if (rub.equals("0003")) {///basica patrocinadora

                    lan.setValorAplicPatrocinadora(result.getDouble(2));
                    debitos = debitos + (result.getDouble(2) - result.getDouble(5));
                } else {
                    lan.setValorAplicExtraParticipante(result.getDouble(2));
                    debitos = debitos + (result.getDouble(2) - result.getDouble(5));
                }

                lan.setValorCota(result.getDouble(6));

            }

            if (lan != null) {

                lan.setDebitos(debitos);
                totalCotaPartic = totalCotaPartic +
                        ((lan.getValorAplicBasicaParticipante() * (1 - sobrec_adm - benef_risco)) / lan.getValorCota());
                totalCotaPat = totalCotaPat +
                        ((lan.getValorAplicPatrocinadora() * (1 - sobrec_adm - benef_risco)) / lan.getValorCota());
                totalCotaExtra = totalCotaExtra +
                        ((lan.getValorAplicExtraParticipante() * (1 - sobrec_adm)) / lan.getValorCota());

                totalContaBenefRisco = totalContaBenefRisco + (lan.getValorAplicBasicaParticipante() * benef_risco);
                totalContaBenefRisco = totalContaBenefRisco + (lan.getValorAplicPatrocinadora() * benef_risco);

                totalCotasContaBenefRisco = totalCotasContaBenefRisco +
                        ((lan.getValorAplicBasicaParticipante() * benef_risco) / lan.getValorCota());
                totalCotasContaBenefRisco = totalCotasContaBenefRisco +
                        ((lan.getValorAplicPatrocinadora() * benef_risco) / lan.getValorCota());


                lan.setValorTotalAplic(
                        lan.getValorAplicBasicaParticipante() +
                        lan.getValorAplicPatrocinadora() +
                        lan.getValorAplicExtraParticipante() -
                        lan.getDebitos());
                lan.setQuantidadeCotaLiq(
                        lan.getValorTotalAplic() / lan.getValorCota());

                totalQtdCota = totalQtdCota + lan.getQuantidadeCotaLiq();
                debitos = 0.0;
                list.add(lan);

            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new Exception("Ocorreram problemas ao buscar o extrato CD Eletrobras do participante.");
        } finally {
            if (conn != null) {
                conn.close();
            }
        }
        return list;
    }

    private List buscaLan13(ParticipanteCDElet part)
            throws Exception {
        Connection conn = null;
        LinkedList list = new LinkedList();
        try {
            conn = getConnection();
            Statement stm = conn.createStatement();

            String query = "SELECT c.nrisc,c.vrinf, m.dtmesref, m.cdrub, m.vrinf, m.VRIDCCOV, m.qtcotmov " +
                    "FROM movcta_tmp c, movcta m " +
                    "WHERE c.NRISC='00" + part.getNumEletros() + "' " +
                    "and c.nrisc = m.nrisc " +
                    "and c.cdrub = m.cdrub " +
                    "and m.cdrub in ('0018','0019') " +
                    "and c.dtmesref = m.dtmesref " +
                    "ORDER BY c.DTMESREF, c.CDRUB";

            System.out.println(query);

            stm = conn.createStatement();
            ResultSet result = stm.executeQuery(query);

            Lancamento lan = null;
            double debitos = 0.0;

            while (result.next()) {
                String dataRef = result.getString(3);
                String anoRef = dataRef.substring(0, 4);
                String mesRef = dataRef.substring(4, 6);
                int ano = Integer.parseInt(anoRef);
                int mes = Integer.parseInt(mesRef);
                System.out.println("MES " + mes);

                if (ano >= 2008) {
                    benef_risco = 0.086;
                    if ((ano >= 2009) && (mes >= 3)) {
                        benef_risco = 0.0702;
                    }
                } else {
                    benef_risco = 0.11;
                }

                lan = new Lancamento();
                lan.setData(mesRef + "/" + anoRef);

                //salario 13
                String querySal = "SELECT H.DTMESREF, H.CDRUB, H.VRSPA " +
                        "FROM  HISALARIOS H " +
                        "WHERE H.NRISC='00" + part.getNumEletros() + "' " +
                        "AND   H.CDRUB    = '0006' " +
                        "AND   H.DTMESREF = '" + dataRef + "'";
                Connection connSal = this.getConnection();
                Statement stmSal = connSal.createStatement();
                ResultSet resultSal = stmSal.executeQuery(querySal);
                if (resultSal.next()) {
                    lan.setSalario(resultSal.getDouble(3));
                }
                resultSal.close();
                stmSal.close();
                connSal.close();

                ///lan participante
                lan.setValorAplicBasicaParticipante(result.getDouble(2));
                debitos = debitos + (result.getDouble(2) - result.getDouble(5));

                ///lan patroc
                result.next();
                lan.setValorAplicPatrocinadora(result.getDouble(2));
                debitos = debitos + (result.getDouble(2) - result.getDouble(5));

                lan.setValorCota(result.getDouble(6));
                lan.setDebitos(debitos);
                totalCotaPartic = totalCotaPartic +
                        ((lan.getValorAplicBasicaParticipante() * (1 - sobrec_adm - benef_risco)) / lan.getValorCota());
                totalCotaPat = totalCotaPat +
                        ((lan.getValorAplicPatrocinadora() * (1 - sobrec_adm - benef_risco)) / lan.getValorCota());

                totalContaBenefRisco = totalContaBenefRisco + (lan.getValorAplicBasicaParticipante() * benef_risco);
                totalContaBenefRisco = totalContaBenefRisco + (lan.getValorAplicPatrocinadora() * benef_risco);

                totalCotasContaBenefRisco = totalCotasContaBenefRisco +
                        ((lan.getValorAplicBasicaParticipante() * benef_risco) / lan.getValorCota());
                totalCotasContaBenefRisco = totalCotasContaBenefRisco +
                        ((lan.getValorAplicPatrocinadora() * benef_risco) / lan.getValorCota());

                lan.setValorTotalAplic(
                        lan.getValorAplicBasicaParticipante() +
                        lan.getValorAplicPatrocinadora() +
                        lan.getValorAplicExtraParticipante() -
                        lan.getDebitos());
                lan.setQuantidadeCotaLiq(
                        lan.getValorTotalAplic() / lan.getValorCota());

                totalQtdCota = totalQtdCota + lan.getQuantidadeCotaLiq();
                debitos = 0.0;
                list.add(lan);

            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new Exception("Ocorreram problemas ao buscar o extrato CD Eletrobras do participante.");
        } finally {
            if (conn != null) {
                conn.close();
            }
        }
        return list;
    }

    private List buscaLan13Adicional(ParticipanteCDElet part)
            throws Exception {
        Connection conn = null;
        LinkedList list = new LinkedList();
        try {
            conn = getConnection();
            Statement stm = conn.createStatement();

            String query = "SELECT c.nrisc,c.vrinf, m.dtmesref, m.cdrub, m.vrinf, m.VRIDCCOV, m.qtcotmov " +
                    "FROM movcta_tmp c, movcta m " +
                    "WHERE c.NRISC='00" + part.getNumEletros() + "' " +
                    "and c.nrisc = m.nrisc " +
                    "and c.cdrub = m.cdrub " +
                    "and m.cdrub = '0020' " +
                    "and c.dtmesref = m.dtmesref " +
                    "ORDER BY c.DTMESREF, c.CDRUB";

            System.out.println(query);
            stm = conn.createStatement();
            ResultSet result = stm.executeQuery(query);

            Lancamento lan = null;

            double debitos = 0.0;

            while (result.next()) {
                String dataRef = result.getString(3);
                String anoRef = dataRef.substring(0, 4);
                String mesRef = dataRef.substring(4, 6);
                int ano = Integer.parseInt(anoRef);
                int mes = Integer.parseInt(mesRef);
                System.out.println("MES " + mes);

                if (ano >= 2008) {
                    benef_risco = 0.086;
                    if ((ano >= 2009) && (mes >= 3)) {
                        benef_risco = 0.0702;
                    }
                } else {
                    benef_risco = 0.11;
                }

                lan = new Lancamento();
                lan.setData("12/" + dataRef.substring(0, 4));
                lan.setValorAplicExtraParticipante(result.getDouble(2));
                debitos = debitos + (result.getDouble(2) - result.getDouble(5));

                lan.setDebitos(debitos);
                lan.setValorCota(result.getDouble(6));

                totalCotaExtra = totalCotaExtra +
                        ((lan.getValorAplicExtraParticipante() * (1 - sobrec_adm)) / lan.getValorCota());

                lan.setValorTotalAplic(
                        lan.getValorAplicExtraParticipante() -
                        lan.getDebitos());
                lan.setQuantidadeCotaLiq(
                        lan.getValorTotalAplic() / lan.getValorCota());

                totalQtdCota = totalQtdCota + lan.getQuantidadeCotaLiq();
                debitos = 0.0;
                list.add(lan);

            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new Exception("Ocorreram problemas ao buscar o extrato CD Eletrobras do participante.");
        } finally {
            if (conn != null) {
                conn.close();
            }
        }
        return list;

    }

    private List buscaLanDotacao(ParticipanteCDElet part)
            throws Exception {
        Connection conn = null;
        LinkedList list = new LinkedList();
        try {
            conn = getConnection();
            Statement stm = conn.createStatement();

            String query = "SELECT c.nrisc,c.vrinf, m.dtmesref, m.cdrub, m.vrinf, m.VRIDCCOV, m.qtcotmov " +
                    "FROM movcta_tmp c, movcta m " +
                    "WHERE c.NRISC='00" + part.getNumEletros() + "' " +
                    "and c.nrisc = m.nrisc " +
                    "and c.cdrub = m.cdrub " +
                    "and m.cdrub in ('0033','0034') " +
                    "and c.dtmesref = m.dtmesref " +
                    "ORDER BY c.DTMESREF, c.CDRUB";


            System.out.println(query);
            stm = conn.createStatement();
            ResultSet result = stm.executeQuery(query);

            Lancamento lan = null;

            double debitos = 0.0;

            while (result.next()) {
                String dataRef = result.getString(3);
                String anoRef = dataRef.substring(0, 4);
                String mesRef = dataRef.substring(4, 6);
                int ano = Integer.parseInt(anoRef);
                int mes = Integer.parseInt(mesRef);
                System.out.println("MES " + mes);

                if (ano >= 2008) {
                    benef_risco = 0.086;
                    if ((ano >= 2009) && (mes >= 3)) {
                        benef_risco = 0.0702;
                    }
                } else {
                    benef_risco = 0.11;
                }

                lan = new Lancamento();
                lan.setData(dataRef.substring(4, 6) + "/" + dataRef.substring(0, 4));
                lan.setValorAplicExtraParticipante(result.getDouble(2));
                debitos = debitos + (result.getDouble(2) - result.getDouble(5));

                ///dotacao patroc
                result.next();
                lan.setValorAplicPatrocinadora(result.getDouble(2));
                debitos = debitos + (result.getDouble(2) - result.getDouble(5));

                lan.setValorCota(result.getDouble(6));

                lan.setDebitos(debitos);
                totalCotaPat = totalCotaPat +
                        ((lan.getValorAplicPatrocinadora() * (1 - sobrec_adm - benef_risco)) / lan.getValorCota());
                totalCotaExtra = totalCotaExtra +
                        ((lan.getValorAplicExtraParticipante() * (1 - sobrec_adm)) / lan.getValorCota());

                totalContaBenefRisco = totalContaBenefRisco + (lan.getValorAplicPatrocinadora() * benef_risco);

                totalCotasContaBenefRisco = totalCotasContaBenefRisco +
                        ((lan.getValorAplicPatrocinadora() * benef_risco) / lan.getValorCota());

                lan.setValorTotalAplic(
                        lan.getValorAplicBasicaParticipante() +
                        lan.getValorAplicPatrocinadora() +
                        lan.getValorAplicExtraParticipante() -
                        lan.getDebitos());
                lan.setQuantidadeCotaLiq(
                        lan.getValorTotalAplic() / lan.getValorCota());

                totalQtdCota = totalQtdCota + lan.getQuantidadeCotaLiq();
                debitos = 0.0;
                list.add(lan);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new Exception("Ocorreram problemas ao buscar o extrato CD Eletrobras do participante.");
        } finally {
            if (conn != null) {
                conn.close();
            }
        }
        return list;

    }

    private List ordenarLista(List lista) {
        ArrayList list = new ArrayList(lista);

        Collections.sort(list, new Comparator() {

            public int compare(Object o1, Object o2) {
                Lancamento lan1 =
                        (Lancamento) o1;
                Lancamento lan2 =
                        (Lancamento) o2;
                DataEletros d1 = null;
                DataEletros d2 = null;

                try {
                    d1 = new DataEletros("01/" + lan1.getData());
                    d2 = new DataEletros("01/" + lan2.getData());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return d1.compareTo(d2);
            }
        });

        return list;

    }

    /**
     *  Retorna uma conex???o 
     */
    private Connection getConnection()
            throws SQLException {
        Connection conn = null;
        try {
            InitialContext initContext = new InitialContext();
            //Context envContext  = (Context)initContext.lookup("java:/comp/env");             
            DataSource ds = (DataSource) initContext.lookup("java:/jdbc/OracleIndice");
            conn = ds.getConnection();
        } catch (NamingException ne) {
            ne.printStackTrace();
        }
        return conn;
    }
}
