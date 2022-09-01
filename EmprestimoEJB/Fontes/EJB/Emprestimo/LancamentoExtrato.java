package EJB.Emprestimo;


/**
 * Classe q representa cada lancamento para o extrato de emprestimo financeiro.
 * Um lancamento do extrato � definido pela data do lancamento, a descricao,
 * o valor lancado, o saldo devedor e o juros aplicado.
 */
public class LancamentoExtrato 
{
    /**
     * Data
     */
    private String data;
    /**
     * Descri��o do lan�amento
     */
    private String descricao;
    /**
     * valor do lan�amento
     */
    private double valor;
    /**
     * valor do saldo do lan�amento
     */
    private double saldo;

    /**
     * valor do juros mensal aplicado
     */
    private double valorJuros;
    
    public LancamentoExtrato() {
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getData() {
        return data;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setValor(double valor) {
        this.valor = valor;
    }

    public double getValor() {
        return valor;
    }

    public void setSaldo(double saldo) {
        this.saldo = saldo;
    }

    public double getSaldo() {
        return saldo;
    }

    public void setValorJuros(double valorJuros) {
        this.valorJuros = valorJuros;
    }

    public double getValorJuros() {
        return valorJuros;
    }
}
