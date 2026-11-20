package cabenobolso;

import java.io.Serializable;
import java.util.*;

public abstract class Despesa implements Serializable {
    private static final long serialVersionUID = 1L;

    private String descricao;
    private double valor;
    private List<Participante> participantes;

    public Despesa(String descricao, double valor, List<Participante> participantes) {
        setDescricao(descricao);
        setValor(valor);
        setParticipantes(participantes);
    }

    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) {
        if (descricao == null || descricao.trim().isEmpty()) throw new IllegalArgumentException("Descrição obrigatória.");
        this.descricao = descricao.trim();
    }

    public double getValor() { return valor; }
    public void setValor(double valor) {
        if (valor <= 0) throw new IllegalArgumentException("O valor deve ser maior que zero.");
        this.valor = valor;
    }

    public List<Participante> getParticipantes() {
        return Collections.unmodifiableList(participantes);
    }

    public void setParticipantes(List<Participante> participantes) {
        if (participantes == null || participantes.isEmpty()) {
            throw new IllegalArgumentException("Selecione ao menos um participante.");
        }
        this.participantes = new ArrayList<>(participantes);
    }

    public abstract Map<Participante, Double> calcularRateio();
    public abstract double getCustoTotal();
}

class DespesaFixa extends Despesa {
    private static final long serialVersionUID = 1L;

    public DespesaFixa(String descricao, double valor, List<Participante> participantes) {
        super(descricao, valor, participantes);
    }

    @Override
    public Map<Participante, Double> calcularRateio() {
        Map<Participante, Double> rateio = new HashMap<>();
        List<Participante> ordenados = new ArrayList<>(getParticipantes());
        Collections.sort(ordenados);

        long totalCentavos = Math.round(getValor() * 100);
        int qtd = ordenados.size();
        long base = totalCentavos / qtd;
        long resto = totalCentavos % qtd;

        for (int i = 0; i < qtd; i++) {
            long centavos = base + (i < resto ? 1 : 0);
            rateio.put(ordenados.get(i), centavos / 100.0);
        }
        return rateio;
    }

    @Override
    public double getCustoTotal() {
        return getValor();
    }
}

class DespesaPorPessoa extends Despesa {
    private static final long serialVersionUID = 1L;

    public DespesaPorPessoa(String descricao, double valorUnitario, List<Participante> participantes) {
        super(descricao, valorUnitario, participantes);
    }

    @Override
    public Map<Participante, Double> calcularRateio() {
        Map<Participante, Double> rateio = new HashMap<>();
        for (Participante p : getParticipantes()) {
            rateio.put(p, getValor());
        }
        return rateio;
    }

    @Override
    public double getCustoTotal() {
        return getValor() * getParticipantes().size();
    }
}
