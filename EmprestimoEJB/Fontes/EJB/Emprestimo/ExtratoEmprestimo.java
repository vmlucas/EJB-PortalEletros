package EJB.Emprestimo;

import java.util.Date;
import java.util.Vector;


/**
 * Classe q representa um extrato de empr�stimo financeiro
 */
public class ExtratoEmprestimo 
{

    private String nomePatrocinadora;
    private String planoCliente;
    private String situacaoEmprestimo;
    
    LancamentoExtrato[] lancamentoExtrato;

    /**
     * lan�amentos do extrato
     */
    private Vector lancamentos;
    /**
     * C�digo do empr�stimo
     */
    private String codEmprestimo;
    /**
     * Data da libera��o do empr�stimo
     */
    private String dataLiberacao;
    /**
     * Taxa de concess�o
     */
    private double taxaConcessao;
    /**
     * valor do IOF
     */
    private double IOF;
    /**
     * valor l�quido
     */
    private double valorLiquido;
    /**
     * Valor concedido
     */
    private double valorConcedido;
    /**
     * Liquida��o anterior
     */
    private double liqAnterior;
    /**
     * valor da remunera��o vigente
     */
    private double remuneracaoVigente;
    /**
     * valor do percentual de desconto
     */
    private double percentualDesconto;
    /**
     * valor da taxa de renova��o
     */
    private double taxaRenovacao;
    /**
     * valor do saldo devedor;
     */
    private double saldoAtual;
    /**
     * data corrente
     */
    private Date dataAtual;
    /**
     * valor do juros
     */
    private double juros;
    private int cont;
    
    
    public ExtratoEmprestimo() 
    {
    }

    /**
     * Adiciona em ordem os lan�amentos do tipoLancamentoExtrato 
     * para o Hist�rico de Empr�stimo.
     * 
     * @param lan
     */
    public void addLancamento(LancamentoExtrato lan)
    {
      if( lancamentos == null)
      {
          lancamentos = new Vector(); 
          cont = 0;
      }
      lancamentos.add(cont,lan);
      cont++;
    }
    
    
    public void setLancamentos(Vector lancamentos) {
        this.lancamentos = lancamentos;
    }

    public Vector getLancamentos() {
        return lancamentos;
    }

    public void setCodEmprestimo(String codEmprestimo) {
        this.codEmprestimo = codEmprestimo;
    }

    public String getCodEmprestimo() {
        return codEmprestimo;
    }

    public void setDataLiberacao(String dataLiberacao) {
        this.dataLiberacao = dataLiberacao;
    }

    public String getDataLiberacao() {
        return dataLiberacao;
    }

    public void setTaxaConcessao(double taxaConcessao) {
        this.taxaConcessao = taxaConcessao;
    }

    public double getTaxaConcessao() {
        return taxaConcessao;
    }

    public void setIOF(double iOF) {
        this.IOF = iOF;
    }

    public double getIOF() {
        return IOF;
    }

    public void setValorLiquido(double valorLiquido) {
        this.valorLiquido = valorLiquido;
    }

    public double getValorLiquido() {
        return valorLiquido;
    }

    public void setValorConcedido(double valorConcedido) {
        this.valorConcedido = valorConcedido;
    }

    public double getValorConcedido() {
        return valorConcedido;
    }

    public void setLiqAnterior(double liqAnterior) {
        this.liqAnterior = liqAnterior;
    }

    public double getLiqAnterior() {
        return liqAnterior;
    }

    public void setRemuneracaoVigente(double remuneracaoVigente) {
        this.remuneracaoVigente = remuneracaoVigente;
    }

    public double getRemuneracaoVigente() {
        return remuneracaoVigente;
    }

    public void setPercentualDesconto(double percentualDesconto) {
        this.percentualDesconto = percentualDesconto;
    }

    public double getPercentualDesconto() {
        return percentualDesconto;
    }

    public void setTaxaRenovacao(double taxaRenovacao) {
        this.taxaRenovacao = taxaRenovacao;
    }

    public double getTaxaRenovacao() {
        return taxaRenovacao;
    }

    public void setSaldoAtual(double saldoAtual) {
        this.saldoAtual = saldoAtual;
    }

    public double getSaldoAtual() {
        return saldoAtual;
    }

    public void setDataAtual(Date dataAtual) {
        this.dataAtual = dataAtual;
    }

    public Date getDataAtual() {
        return dataAtual;
    }

    public void setJuros(double juros) {
        this.juros = juros;
    }

    public double getJuros() {
        return juros;
    }

    public void setNomePatrocinadora(String nomePatrocinadora) {
        this.nomePatrocinadora = nomePatrocinadora;
    }

    public String getNomePatrocinadora() {
        return nomePatrocinadora;
    }

    public void setPlanoCliente(String planoCliente) {
        this.planoCliente = planoCliente;
    }

    public String getPlanoCliente() {
        return planoCliente;
    }

    public void setSituacaoEmprestimo(String situacaoEmprestimo) {
        this.situacaoEmprestimo = situacaoEmprestimo;
    }

    public String getSituacaoEmprestimo() {
        return situacaoEmprestimo;
    }
}
