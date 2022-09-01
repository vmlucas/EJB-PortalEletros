package EJB.Emprestimo;

import com.eletros.benef.Participante;


public class ConcessaoEmprestimo 
{
    
    private double saldoDevedor;
    private double valorMinimoRemuneracoes;
    private double valorMaximoRemuneracoes;
    private double valorMaximoEmprestimo;
    private int carenciaMinima;
    private int carencia;
    private double taxaJuros;
    private double taxaConcessao;
    private double taxaRenovacao;
    private ClienteEmprestimo cliente;
    
    public ConcessaoEmprestimo(ClienteEmprestimo cliente) 
    {
        this.cliente = cliente;
    }
    
    /**
        * Retorna o valor mínimo de remunerações permitido para empréstimo
        */
       public double getValorMinimoRemuneracoes()
       {
          return this.valorMinimoRemuneracoes;  
       }
       
       /**
        * Retorna o valor máximo de remunerações permitido para empréstimo
        */
       public double getValorMaximoRemuneracoes()
       {
          return this.valorMaximoRemuneracoes;  
       }

       /**
        * Retorna o valor máximo permitido para o empréstimo.
        * Se o número máximo de remunerações vezes o salário for menor que o valor máximo
        * de empréstimo, retorna o valor calculado. Se for maior, retorna o valor máximo do empréstimo.
        */
       public double getValorMaximoEmprestimo()
       {
          double valor = cliente.getSalarioAtual() * this.valorMaximoRemuneracoes;
          if( valor < valorMaximoEmprestimo)
          {
            return valor;
          }
          else
          {
            return valorMaximoEmprestimo;
          }
       }
       
       /**
        * Retorna o saldo devedor do participante
        */
       public double getSaldoDevedor()
       {
         return saldoDevedor;  
       }

       /**
        * Retorna a carencia do participante
        */
       public int getCarenciaMinima()
       {
         return carenciaMinima;
       }
       
    /**
         * Retorna a taxa de juros dependendo do plano vinculado ao participante
         */
        public double getTaxaJuros()
        {
          return taxaJuros;
        }

    public void setSaldoDevedor(double saldoDevedor) {
        this.saldoDevedor = saldoDevedor;
    }

    public void setValorMinimoRemuneracoes(double valorMinimoRemuneracoes) {
        this.valorMinimoRemuneracoes = valorMinimoRemuneracoes;
    }

    public void setValorMaximoRemuneracoes(double valorMaximoRemuneracoes) {
        this.valorMaximoRemuneracoes = valorMaximoRemuneracoes;
    }

    
    public void setCarenciaMinima(int carencia) {
        this.carenciaMinima = carencia;
    }

    public void setTaxaJuros(double taxaJuros) {
        this.taxaJuros = taxaJuros;
    }

    public void setValorMaximoEmprestimo(double valorMaximoEmprestimo) {
        this.valorMaximoEmprestimo = valorMaximoEmprestimo;
    }

    public void setCarencia(int carencia) {
        this.carencia = carencia;
    }

    public int getCarencia() {
        return carencia;
    }

    public void setTaxaConcessao(double taxaConcessao) {
        this.taxaConcessao = taxaConcessao;
    }

    public double getTaxaConcessao() {
        return taxaConcessao;
    }

    public void setTaxaRenovacao(double taxaRenovacao) {
        this.taxaRenovacao = taxaRenovacao;
    }

    public double getTaxaRenovacao() {
        return taxaRenovacao;
    }

    public ClienteEmprestimo getCliente() {
        return cliente;
    }
}
