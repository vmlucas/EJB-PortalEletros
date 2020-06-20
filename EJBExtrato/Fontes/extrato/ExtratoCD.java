package extrato;

import com.util.DataEletros;

import java.util.List;

public class ExtratoCD {
    
    /**
     * Informações do participante
     */
    private String numEletros;
    private String nome;
    private String plano;
    private String patrocinadora;
    
    /**
     * Valores de migração
     */
    private String msgErroReserva;
    private double saldoMigracao;
    private String dataRefMigracao;
    private double valorIndiceMigracao;
    private double qtdCotasMigradas;
    private double BPDSAtual;
    private DataEletros inicioBPDS;

  /**
   * Saldos atuais
   */
    private double saldoCotas; 
    private double saldoAtual;
    private double valorCotaRecente; 
    private String dataCotaRecente;
    private double rentabilidade;
    private double rentabilidadeMes;
    private String ultimaDataLan;
    private double totalCotaPartic;
    private double totalCotaPat;
    private double totalCotaExtra;
    private double totalContaBenefRisco;
    private double totalCotaContaBenefRisco;


  /**
   * @associates <{extratocdelet.Lancamento}>
   */
  private List lancamentos;


  public void setNumEletros(String numEletros) {
        this.numEletros = numEletros;
    }

    public String getNumEletros() {
        return numEletros;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getNome() {
        return nome;
    }

    public void setLancamentos(List lancamentos) {
        this.lancamentos = lancamentos;
    }

    public List getLancamentos() {
        return lancamentos;
    }

    public void setSaldoAtual(double saldoAtual) {
        this.saldoAtual = saldoAtual;
    }

    public double getSaldoAtual() {
        return saldoAtual;
    }

    public void setSaldoMigracao(double saldoMigracao) {
        this.saldoMigracao = saldoMigracao;
    }

    public double getSaldoMigracao() {
        return saldoMigracao;
    }

    public void setDataRefMigracao(String dataRefMigracao) {
        this.dataRefMigracao = dataRefMigracao;
    }

    public String getDataRefMigracao() {
        return dataRefMigracao;
    }

    public void setValorIndiceMigracao(double valorIndiceMigracao) {
        this.valorIndiceMigracao = valorIndiceMigracao;
    }

    public double getValorIndiceMigracao() {
        return valorIndiceMigracao;
    }

    public void setRentabilidade(double rentabilidade) {
        this.rentabilidade = rentabilidade;
    }

    public double getRentabilidade() {
        return rentabilidade;
    }

    public void setPatrocinadora(String patrocinadora) {
        this.patrocinadora = patrocinadora;
    }

    public String getPatrocinadora() {
        return patrocinadora;
    }

    public void setValorCotaRecente(double valorCotaRecente) {
        this.valorCotaRecente = valorCotaRecente;
    }

    public double getValorCotaRecente() {
        return valorCotaRecente;
    }

    public void setPlano(String plano) {
        this.plano = plano;
    }

    public String getPlano() {
        return plano;
    }

    public void setMsgErroReserva(String msgErroReserva) {
        this.msgErroReserva = msgErroReserva;
    }

    public String getMsgErroReserva() {
        return msgErroReserva;
    }

    public void setSaldoCotas(double saldoCotas) {
        this.saldoCotas = saldoCotas;
    }

    public double getSaldoCotas() {
        return saldoCotas;
    }

    public void setRentabilidadeMes(double rentabilidadeMes) {
        this.rentabilidadeMes = rentabilidadeMes;
    }

    public double getRentabilidadeMes() {
        return rentabilidadeMes;
    }

    public void setQtdCotasMigradas(double qtdCotasMigradas) {
        this.qtdCotasMigradas = qtdCotasMigradas;
    }

    public double getQtdCotasMigradas() {
        return qtdCotasMigradas;
    }

    public void setDataCotaRecente(String dataCotaRecente) {
        this.dataCotaRecente = dataCotaRecente;
    }

    public String getDataCotaRecente() {
        return dataCotaRecente;
    }

    public void setUltimaDataLan(String ultimaDataLan) {
        this.ultimaDataLan = ultimaDataLan;
    }

    public String getUltimaDataLan() {
        return ultimaDataLan;
    }

    public void setBPDSAtual(double bPDSAtual) {
        this.BPDSAtual = bPDSAtual;
    }

    public double getBPDSAtual() {
        return BPDSAtual;
    }

    public void setInicioBPDS(DataEletros inicioBPDS) {
        this.inicioBPDS = inicioBPDS;
    }

    public DataEletros getInicioBPDS() {
        return inicioBPDS;
    }

    public void setTotalCotaPartic(double totalCotaPartic) {
        this.totalCotaPartic = totalCotaPartic;
    }

    public double getTotalCotaPartic() {
        return totalCotaPartic;
    }

    public void setTotalCotaPat(double totalCotaPat) {
        this.totalCotaPat = totalCotaPat;
    }

    public double getTotalCotaPat() {
        return totalCotaPat;
    }

    public void setTotalCotaExtra(double totalCotaExtra) {
        this.totalCotaExtra = totalCotaExtra;
    }

    public double getTotalCotaExtra() {
        return totalCotaExtra;
    }

    public void setTotalContaBenefRisco(double totalContaBenefRisco) {
        this.totalContaBenefRisco = totalContaBenefRisco;
    }

    public double getTotalContaBenefRisco() {
        return totalContaBenefRisco;
    }

    public void setTotalCotaContaBenefRisco(double totalCotaContaBenefRisco) {
        this.totalCotaContaBenefRisco = totalCotaContaBenefRisco;
    }

    public double getTotalCotaContaBenefRisco() {
        return totalCotaContaBenefRisco;
    }
}
