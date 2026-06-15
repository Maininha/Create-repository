```mermaid
classDiagram
    class Associado {
        -nome: String
        -cpf: String
        -dataCadastro: TimesTamp
        -tipoAssociado: String
        +cadastrar() void
        +editar() void
        +listar() void
        +buscar() void
        +excluir() void
    }

    class Usuario {
        -tipoPerfil: String
        -senha: String
        +criptografarSenhas() String
        +autenticar(cpf: String, senhaDigitada: String) Usuario
        +redefinirSenha(cpf: String, novaSenha: String) int
        +inserir(usuario: Usuario) boolean
    }

    class Endereco {
        -cidade: String
        -estado: String
        -referencia: String
        -logradouro: String
    }

    class Relatorio {
        -periodoIncial: Date
        -periodoFinal: Date
        -totalEntradas: double
        -totalSaidas: double
        -saldoFinal: double
        +preencherDados(relatorio: Relatorio) void
        +salvar() boolean
        +gerarMensal() void
        +gerarAnual() void
        +emitirPdf() void
    }

    class Financeiro {
        -data: Date
        -valor: double
        -descricao: String
        -categoria: String
        -tipo: String
        +salvar() boolean
        +listar() void
        +atualizar() boolean
        +excluir(idMov: int) boolean
    }

    class Atividade {
        -dataHora: Date
        -categoria: String
        -descricao: String
        +buscarHistoricoRecente() List~Atividade~
    }

    class GeradorPdfRelatorio {
        <<Utility>>
        +gerarRelatorioAtividades(atividades: List~Atividade~) static void
        +gerarRelatorioAssociados(associados: List~Associado~) static void
        +gerarRelatorioFinanceiro(movimentacoes: List~Financeiro~) static void
    }

    %% Relacionamentos Principais do Sistema (Regras de Negócio)
    Usuario --|> Associado : Herança
    Endereco "1" --* "1" Associado : Composição
    Financeiro "*" --o "1" Relatorio : Agregação
    Usuario "1" --> "*" Financeiro : Gerencia / Acessa

    %% Dependências de uso do utilitário (Pasta util)
    GeradorPdfRelatorio ..> Atividade : Usa dados
    GeradorPdfRelatorio ..> Relatorio : Usa dados
    GeradorPdfRelatorio ..> Associado : Usa dados

