package EJB.Emprestimo;

import java.util.Date;
import java.util.Vector;


/**
 * Classe q representa um extrato de empréstimo financeiro
 */
public class ExtratoEmprestimo 
{

    private String nomePatrocinadora;
    private String planoCliente;
    private String situacaoEmprestimo;
    
    LancamentoExtrato[] lancamentoExtrato;

    /**
     * lançamentos do extrato
     */
    private Vector lancamentos;
    /**
     * Código do empréstimo
     */
    private String codEmprestimo;
    /**
     * Data da liberação do empréstimo
     */
    private String dataLiberacao;
    /**
     * Taxa de concessão
     */
    private double taxaConcessao;
    /**
     * valor do IOF
     */
    private double IOF;
    /**
     * valor líquido
     */
    private double valorLiquido;
    /**
     * Valor concedido
     */
    private double valorConcedido;
    /**
     * Liquidação anterior
     */
    private double liqAnterior;
    /**
     * valor da remuneração vigente
     */
    private double remuneracaoVigente;
    /**
     * valor do percentual de desconto
     */
    private double percentualDesconto;
    /**
     * valor da taxa de renovação
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
     * Adiciona em ordem os lançamentos do tipoLancamentoExtrato 
     * para o Histórico de Empréstimo.
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
