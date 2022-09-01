package EJB.Emprestimo;

import EJB.Emprestimo.DAO.*;
import com.util.*;
import java.util.Collection;
import javax.ejb.EJBException;
import javax.ejb.SessionBean;
import javax.ejb.SessionContext;
import javax.naming.InitialContext;
import javax.rmi.PortableRemoteObject;


/**
 * Session bean para geração de extratos de empréstimos e concessão de empréstimos
 * financeiros. Atualmente só gera extratos para emprestimos de participantes plano CD ONS.
 */
public class EmprestimoEJBBean implements SessionBean {
    private SessionContext _context;
    
    public void ejbCreate() 
    {
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


    /**
     *  Busca o extrato de empréstimo financeiro para um emprestimo e uma data
     *  final passado como parametros. Por enquanto apenas participante plano CD ONS
     *  @param dataFim.
     *  @param emprestimo
     *
     *  @return - Um objeto do tipo ExtratoEmprestimo.
     */
    public ExtratoEmprestimo BuscarExtratoEmprestimo(Emprestimo emprestimo, DataEletros dataFim) 
       throws Exception
    {
        ExtratoEmprestimoDAO empDAO = ExtratoEmprestimoDAO.getInstance( );

        ExtratoEmprestimo extrato = 
                  empDAO.buscarExtratoEmprestimoCD( emprestimo, dataFim );

        if( extrato == null )
        {
           throw new Exception("Não existem lançamentos de Extrato de Empréstimo para o Participante");
        }

        /**
         * #############Calculando o juros##################
         * Usa o saldo do ultimo lancamento menos o saldo atual do
         * historico.
         */      
        LancamentoExtrato lan = 
              (LancamentoExtrato)extrato.getLancamentos().lastElement();
              
        double juros = extrato.getSaldoAtual() - lan.getSaldo();
        extrato.setJuros( juros );
        
        return extrato;        
    }


    /**
     *  Busca todos os emprestimos concedidos ao participante.
     *  @param participante
     *  @return Collection of Emprestimo
     *  @throws Exception
     */
    public Collection BuscarEmprestimos(ClienteEmprestimo cliente) throws Exception {
        EmprestimoDAO dao = new EmprestimoDAO( cliente );
        return dao.buscarEmprestimos();
    }


    public ConcessaoEmprestimo BuscarConcessaoEmprestimo(ClienteEmprestimo cliente) throws Exception {

        //Carencia Eletros
        DataEletros dataInscricao = cliente.getDataInscricao();
        DataEletros dataAtual = new DataEletros();

        int carencia = dataAtual.iNumDiasDiff( dataInscricao ); //número de dias

        EmprestimoDAO dao = new EmprestimoDAO( cliente );
        ConcessaoEmprestimo concessao = dao.buscarConcessaoEmprestimo();
        concessao.setCarencia( carencia );
        if( carencia < concessao.getCarenciaMinima())
        {
           throw new Exception("Participante não pode solicitar um empréstimo!");
        }

        return concessao;

    }


    public double BuscarIOFSimulacao(double valorSolicitado, ConcessaoEmprestimo concessao) 
       throws Exception
    {
        EmprestimoDAO dao = new EmprestimoDAO( concessao.getCliente() );
        
        return dao.buscarIOFSimulacao(valorSolicitado);
    }


    public double CalcularDescontoMinimo(int numRemuneracoes, ConcessaoEmprestimo concessao) 
       throws Exception
    {
        if( numRemuneracoes > concessao.getValorMaximoRemuneracoes() )
        {
             throw new Exception("número de remunerações acima do valor máximo permitido");
        }
        if( numRemuneracoes < concessao.getValorMinimoRemuneracoes() )
        {
             throw new Exception("número de remunerações abaixo do mínimo permitido");
        }

        double emprestimo = concessao.getCliente().getSalarioAtual() * numRemuneracoes;
        if( emprestimo > concessao.getValorMaximoEmprestimo() )
        {
            throw new Exception("Valor do empréstimo acima do valor máximo permitido");
        }
        EmprestimoDAO dao = new EmprestimoDAO( concessao.getCliente() );

        return dao.buscarValorMinimoDesconto(  );

    }

    public Emprestimo BuscarEmprestimoAtual(ClienteEmprestimo cliente) throws Exception {
        EmprestimoDAO dao = new EmprestimoDAO( cliente );
        return dao.buscarEmprestimoAtual();
    }


    public ClienteEmprestimo BuscarClienteEmprestimo(String numEletros) throws Exception {
        ClienteEmprestimoDAO dao = new ClienteEmprestimoDAO();
        
        return dao.buscaClienteEmprestimo(numEletros);
    }

    
}
