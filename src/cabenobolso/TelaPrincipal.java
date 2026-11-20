package cabenobolso;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class TelaPrincipal extends JFrame {

    private final PlanejamentoControle controle = new PlanejamentoControle();

    private JTextField txtCodigo, txtNome, txtOrcamento;
    private JTable tabelaParticipantes;
    private DefaultTableModel modelParticipantes;

    private JTextField txtDescricaoDespesa, txtValorDespesa;
    private JComboBox<String> cbTipoDespesa;
    private JList<Participante> listParticipantesDespesa;
    private DefaultListModel<Participante> listModelParticipantes;
    private JTable tabelaDespesas;
    private DefaultTableModel modelDespesas;
    private Despesa despesaEmEdicao = null;

    private JTable tabelaResumo;
    private DefaultTableModel modelResumo;
    private JLabel lblCustoTotal;

    public TelaPrincipal() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {}

        setTitle("Cabe no bolso - Planejador de Despesas Compartilhadas");
        setSize(880, 630);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        add(criarBarraArquivo(), BorderLayout.NORTH);

        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.addTab("Participantes", criarPainelParticipantes());
        tabbedPane.addTab("Despesas", criarPainelDespesas());
        tabbedPane.addTab("Resumo do Planejamento", criarPainelResumo());

        add(tabbedPane, BorderLayout.CENTER);
    }

    private JPanel criarBarraArquivo() {
        JPanel barra = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 8));
        barra.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.LIGHT_GRAY));

        JButton btnNovo = new JButton("Novo Planejamento");
        JButton btnSalvar = new JButton("Salvar Planejamento");
        JButton btnCarregar = new JButton("Carregar Planejamento");

        btnNovo.addActionListener(e -> {
            if (!controle.getParticipantes().isEmpty() || !controle.getDespesas().isEmpty()) {
                int resp = JOptionPane.showConfirmDialog(this,
                        "Deseja iniciar um novo planejamento? Dados não salvos serão perdidos.",
                        "Confirmar", JOptionPane.YES_NO_OPTION);
                if (resp != JOptionPane.YES_OPTION) return;
            }
            controle.novoPlanejamento();
            limparFormularioParticipante();
            limparFormularioDespesa();
            atualizarTodasTelas();
        });

        btnSalvar.addActionListener(e -> salvarArquivo());
        btnCarregar.addActionListener(e -> carregarArquivo());

        barra.add(btnNovo);
        barra.add(btnSalvar);
        barra.add(btnCarregar);
        return barra;
    }

    private void salvarArquivo() {
        JFileChooser fc = new JFileChooser();
        fc.setDialogTitle("Salvar Planejamento");
        fc.setFileFilter(new FileNameExtensionFilter("Arquivo de Planejamento (*.dat)", "dat"));

        if (fc.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            File arquivo = fc.getSelectedFile();
            if (!arquivo.getName().toLowerCase().endsWith(".dat")) {
                arquivo = new File(arquivo.getAbsolutePath() + ".dat");
            }
            if (arquivo.exists()) {
                int sob = JOptionPane.showConfirmDialog(this,
                        "O arquivo já existe. Deseja substituir?",
                        "Sobrescrever", JOptionPane.YES_NO_OPTION);
                if (sob != JOptionPane.YES_OPTION) return;
            }
            try {
                controle.salvar(arquivo.getAbsolutePath());
                JOptionPane.showMessageDialog(this, "Planejamento salvo com sucesso!");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Erro ao salvar: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void carregarArquivo() {
        JFileChooser fc = new JFileChooser();
        fc.setDialogTitle("Carregar Planejamento");
        fc.setFileFilter(new FileNameExtensionFilter("Arquivo de Planejamento (*.dat)", "dat"));

        if (fc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            try {
                controle.carregar(fc.getSelectedFile().getAbsolutePath());
                limparFormularioParticipante();
                limparFormularioDespesa();
                atualizarTodasTelas();
                JOptionPane.showMessageDialog(this, "Planejamento carregado com sucesso!");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this,
                        "Não foi possível carregar o arquivo selecionado.\nO planejamento atual foi mantido.",
                        "Erro ao Carregar", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private JPanel criarPainelParticipantes() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));

        JPanel pForm = new JPanel(new GridBagLayout());
        pForm.setBorder(BorderFactory.createTitledBorder("Cadastrar / Editar Participante"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 8, 5, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0;
        pForm.add(new JLabel("Código:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0;
        txtCodigo = new JTextField();
        pForm.add(txtCodigo, gbc);

        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0;
        pForm.add(new JLabel("Nome:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0;
        txtNome = new JTextField();
        pForm.add(txtNome, gbc);

        gbc.gridx = 0; gbc.gridy = 2; gbc.weightx = 0;
        pForm.add(new JLabel("Orçamento (R$):"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0;
        txtOrcamento = new JTextField();
        pForm.add(txtOrcamento, gbc);

        JPanel pBotoes = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        JButton btnSalvarParticipante = new JButton("Salvar Participante");
        btnSalvarParticipante.addActionListener(e -> salvarParticipante());

        JButton btnLimpar = new JButton("Limpar Campos");
        btnLimpar.addActionListener(e -> limparFormularioParticipante());

        pBotoes.add(btnSalvarParticipante);
        pBotoes.add(btnLimpar);

        gbc.gridx = 1; gbc.gridy = 3; gbc.fill = GridBagConstraints.NONE; gbc.anchor = GridBagConstraints.WEST;
        pForm.add(pBotoes, gbc);

        modelParticipantes = new DefaultTableModel(new String[]{"Código", "Nome", "Orçamento (R$)"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        tabelaParticipantes = new JTable(modelParticipantes);
        tabelaParticipantes.setRowHeight(24);

        tabelaParticipantes.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int row = tabelaParticipantes.getSelectedRow();
                if (row >= 0 && row < controle.getParticipantes().size()) {
                    Participante p = controle.getParticipantes().get(row);
                    txtCodigo.setText(String.valueOf(p.getCodigo()));
                    txtCodigo.setEditable(false);
                    txtNome.setText(p.getNome());
                    txtOrcamento.setText(String.format("%.2f", p.getOrcamento()));
                }
            }
        });

        JButton btnRemoverParticipante = new JButton("Remover Participante Selecionado");
        btnRemoverParticipante.addActionListener(e -> removerParticipante());

        panel.add(pForm, BorderLayout.NORTH);
        panel.add(new JScrollPane(tabelaParticipantes), BorderLayout.CENTER);
        panel.add(btnRemoverParticipante, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel criarPainelDespesas() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));

        JPanel pForm = new JPanel(new GridBagLayout());
        pForm.setBorder(BorderFactory.createTitledBorder("Cadastrar / Editar Despesa"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 8, 5, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0;
        pForm.add(new JLabel("Descrição:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0;
        txtDescricaoDespesa = new JTextField();
        pForm.add(txtDescricaoDespesa, gbc);

        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0;
        pForm.add(new JLabel("Valor (R$):"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0;
        txtValorDespesa = new JTextField();
        pForm.add(txtValorDespesa, gbc);

        gbc.gridx = 0; gbc.gridy = 2; gbc.weightx = 0;
        pForm.add(new JLabel("Tipo:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0;
        cbTipoDespesa = new JComboBox<>(new String[]{"Fixa", "Por Pessoa"});
        pForm.add(cbTipoDespesa, gbc);

        gbc.gridx = 0; gbc.gridy = 3; gbc.weightx = 0; gbc.anchor = GridBagConstraints.NORTHWEST;
        pForm.add(new JLabel("Participantes (Ctrl+Clique):"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0;
        listModelParticipantes = new DefaultListModel<>();
        listParticipantesDespesa = new JList<>(listModelParticipantes);
        listParticipantesDespesa.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        JScrollPane scrollList = new JScrollPane(listParticipantesDespesa);
        scrollList.setPreferredSize(new Dimension(200, 75));
        pForm.add(scrollList, gbc);

        JPanel pBotoes = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        JButton btnSalvarDespesa = new JButton("Salvar Despesa");
        btnSalvarDespesa.addActionListener(e -> salvarDespesa());

        JButton btnLimparD = new JButton("Limpar Campos");
        btnLimparD.addActionListener(e -> limparFormularioDespesa());

        pBotoes.add(btnSalvarDespesa);
        pBotoes.add(btnLimparD);

        gbc.gridx = 1; gbc.gridy = 4; gbc.fill = GridBagConstraints.NONE; gbc.anchor = GridBagConstraints.WEST;
        pForm.add(pBotoes, gbc);

        modelDespesas = new DefaultTableModel(new String[]{"Descrição", "Tipo", "Valor (R$)", "Custo Total (R$)", "Participantes"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        tabelaDespesas = new JTable(modelDespesas);
        tabelaDespesas.setRowHeight(24);

        tabelaDespesas.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int row = tabelaDespesas.getSelectedRow();
                if (row >= 0 && row < controle.getDespesas().size()) {
                    despesaEmEdicao = controle.getDespesas().get(row);
                    txtDescricaoDespesa.setText(despesaEmEdicao.getDescricao());
                    txtValorDespesa.setText(String.format("%.2f", despesaEmEdicao.getValor()));
                    cbTipoDespesa.setSelectedItem((despesaEmEdicao instanceof DespesaFixa) ? "Fixa" : "Por Pessoa");

                    listParticipantesDespesa.clearSelection();
                    List<Integer> indices = new ArrayList<>();
                    for (int i = 0; i < listModelParticipantes.size(); i++) {
                        Participante p = listModelParticipantes.get(i);
                        if (despesaEmEdicao.getParticipantes().contains(p)) {
                            indices.add(i);
                        }
                    }
                    int[] indArray = indices.stream().mapToInt(i -> i).toArray();
                    listParticipantesDespesa.setSelectedIndices(indArray);
                }
            }
        });

        JButton btnRemoverDespesa = new JButton("Excluir Despesa Selecionada");
        btnRemoverDespesa.addActionListener(e -> removerDespesa());

        panel.add(pForm, BorderLayout.NORTH);
        panel.add(new JScrollPane(tabelaDespesas), BorderLayout.CENTER);
        panel.add(btnRemoverDespesa, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel criarPainelResumo() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));

        lblCustoTotal = new JLabel("Custo Total do Passeio: R$ 0,00", SwingConstants.CENTER);
        lblCustoTotal.setFont(new Font("Segoe UI", Font.BOLD, 16));

        modelResumo = new DefaultTableModel(new String[]{"Código", "Participante", "Gasto Total (R$)", "Orçamento (R$)", "Situação"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        tabelaResumo = new JTable(modelResumo);
        tabelaResumo.setRowHeight(24);

        tabelaResumo.getColumnModel().getColumn(4).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                String text = (value != null) ? value.toString() : "";

                if (!isSelected) {
                    if (text.startsWith("Excedido")) {
                        c.setBackground(new Color(255, 230, 230));
                        c.setForeground(new Color(180, 0, 0));
                    } else {
                        c.setBackground(new Color(230, 255, 230));
                        c.setForeground(new Color(0, 130, 0));
                    }
                }
                setHorizontalAlignment(SwingConstants.CENTER);
                return c;
            }
        });

        panel.add(lblCustoTotal, BorderLayout.NORTH);
        panel.add(new JScrollPane(tabelaResumo), BorderLayout.CENTER);

        return panel;
    }

    private void salvarParticipante() {
        try {
            String codStr = txtCodigo.getText().trim();
            String nome = txtNome.getText().trim();
            String orcStr = txtOrcamento.getText().trim();

            if (codStr.isEmpty() || nome.isEmpty() || orcStr.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Preencha todos os campos do participante.", "Validação", JOptionPane.WARNING_MESSAGE);
                return;
            }

            int codigo = Integer.parseInt(codStr);
            double orcamento = Double.parseDouble(orcStr.replace(",", "."));
            boolean isEdicao = !txtCodigo.isEditable();

            controle.salvarParticipante(codigo, nome, orcamento, isEdicao);

            limparFormularioParticipante();
            atualizarTodasTelas();
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Código deve ser inteiro positivo e Orçamento deve ser numérico.", "Erro de Entrada", JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void removerParticipante() {
        int row = tabelaParticipantes.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Selecione um participante na tabela para remover.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            Participante p = controle.getParticipantes().get(row);
            controle.removerParticipante(p);
            limparFormularioParticipante();
            atualizarTodasTelas();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void salvarDespesa() {
        try {
            String desc = txtDescricaoDespesa.getText().trim();
            String valorStr = txtValorDespesa.getText().trim();

            if (desc.isEmpty() || valorStr.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Preencha descrição e valor da despesa.", "Validação", JOptionPane.WARNING_MESSAGE);
                return;
            }

            double valor = Double.parseDouble(valorStr.replace(",", "."));
            String tipo = (String) cbTipoDespesa.getSelectedItem();
            List<Participante> selecionados = listParticipantesDespesa.getSelectedValuesList();

            controle.salvarDespesa(despesaEmEdicao, desc, valor, tipo, selecionados);

            limparFormularioDespesa();
            atualizarTodasTelas();
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Digite um valor numérico válido para a despesa.", "Erro de Entrada", JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void removerDespesa() {
        int row = tabelaDespesas.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Selecione uma despesa na tabela para excluir.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            Despesa d = controle.getDespesas().get(row);
            controle.removerDespesa(d);
            limparFormularioDespesa();
            atualizarTodasTelas();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void limparFormularioParticipante() {
        txtCodigo.setText("");
        txtCodigo.setEditable(true);
        txtNome.setText("");
        txtOrcamento.setText("");
        tabelaParticipantes.clearSelection();
    }

    private void limparFormularioDespesa() {
        txtDescricaoDespesa.setText("");
        txtValorDespesa.setText("");
        cbTipoDespesa.setSelectedIndex(0);
        listParticipantesDespesa.clearSelection();
        tabelaDespesas.clearSelection();
        despesaEmEdicao = null;
    }

    private void atualizarTodasTelas() {
        modelParticipantes.setRowCount(0);
        listModelParticipantes.clear();
        for (Participante p : controle.getParticipantes()) {
            modelParticipantes.addRow(new Object[]{p.getCodigo(), p.getNome(), String.format("R$ %.2f", p.getOrcamento())});
            listModelParticipantes.addElement(p);
        }

        modelDespesas.setRowCount(0);
        for (Despesa d : controle.getDespesas()) {
            String tipo = (d instanceof DespesaFixa) ? "Fixa" : "Por Pessoa";
            modelDespesas.addRow(new Object[]{
                d.getDescricao(),
                tipo,
                String.format("R$ %.2f", d.getValor()),
                String.format("R$ %.2f", d.getCustoTotal()),
                d.getParticipantes().size() + " participante(s)"
            });
        }

        modelResumo.setRowCount(0);
        Map<Participante, Double> totais = controle.getTotaisIndividuais();
        for (Map.Entry<Participante, Double> entry : totais.entrySet()) {
            Participante p = entry.getKey();
            double gasto = entry.getValue();
            double orcamento = p.getOrcamento();
            String situacao = (gasto <= orcamento) ? "Dentro do limite" : String.format("Excedido em R$ %.2f", (gasto - orcamento));

            modelResumo.addRow(new Object[]{p.getCodigo(), p.getNome(), String.format("R$ %.2f", gasto), String.format("R$ %.2f", orcamento), situacao});
        }

        lblCustoTotal.setText(String.format("Custo Total do Passeio: R$ %.2f", controle.getCustoTotal()));
    }
}
