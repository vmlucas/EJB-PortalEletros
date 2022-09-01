package EJB.Emprestimo;

import com.eletros.benef.*;
import com.util.DataEletros;


/**
 * Classe que representa um emprestimo concedido.
 */
public class Emprestimo
{
    
    private String codigo;
    private String codigoAnterior;
    private DataEletros dataLiberacao;
    private DataEletros dataConcessaoSaldo;
    private double valorSaldo; 
    private ClienteEmprestimo cliente;
    
    public Emprestimo( ClienteEmprestimo cliente ) 
    {
       this.setCliente( cliente );
    }

    
    /**
     * Define o codigo de concessao do emprestimo
     * @param codigo
     */
    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }


    /**
     * 
     * @return codigo de concessao do emprestimo
     */
    public String getCodigo() {
        return codigo;
    }

    
    /**
     * Define o codigo de concessao do emprestimo anterior
     * @param codigoAnterior
     */
    public void setCodigoAnterior(String codigoAnterior) {
        this.codigoAnterior = codigoAnterior;
    }


    /**
     * 
     * @return codigo de concessao do emprestimo anterior para o participante
     */
    public String getCodigoAnterior() {
        return codigoAnterior;
    }


    /**
     * Define a data de liberacao do emprestimo
     * @param dataLiberacao
     */
    public void setDataLiberacao(DataEletros dataLiberacao) {
        this.dataLiberacao = dataLiberacao;
    }


    /**
     * 
     * @return data de liberacao do emprestimo
     */
    public DataEletros getDataLiberacao() {
        return dataLiberacao;
    }


    /**
     * Define a data do saldo devedor
     * @param dataConcessaoSaldo
     */
    public void setDataConcessaoSaldo(DataEletros dataConcessaoSaldo) {
        this.dataConcessaoSaldo = dataConcessaoSaldo;
    }


    /**
     * 
     * @return a data do saldo devedor
     */
    public DataEletros getDataConcessaoSaldo() {
        return dataConcessaoSaldo;
    }

    
    /**
     * Define o saldo devedor do emprestimo
     * @param valorSaldo
     */
    public void setValorSaldo(double valorSaldo) {
        this.valorSaldo = valorSaldo;
    }


    /**
     * 
     * @return o Saldo devedor
     */
    public double getValorSaldo() {
        return valorSaldo;
    }


    public void setCliente(ClienteEmprestimo cliente) {
        this.cliente = cliente;
    }

    public ClienteEmprestimo getCliente() {
        return cliente;
    }
}
