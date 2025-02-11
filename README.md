<div align="center">
<img src="https://hermes.dio.me/tracks/a039b34c-7aa8-4a3d-b765-07c8c837f67a.png" alt="Logo BackEnd" width="80">

<img src="https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcSCcxkA70BX5H1N1FFSkncQ-InOpqloUVZLcA&usqp=CAU" alt="Logo Bootcamp" width="100">
</div>

###### #JavaCodes



<h1 align="center"> lavaCode </h1>



<p align="center">
<img src="http://img.shields.io/static/v1?label=STATUS&message=EM%20DESENVOLVIMENTO&color=GREEN&style=for-the-badge"/>
</p>

## Descrição

## 📱 Projeto Sistema de Controle de Lavação


install4j software para distribuição de aplicativos Java
### Descrição do Projeto

Este projeto tem como objetivo desenvolver um sistema de lavação de carros com funcionalidades abrangentes para o gerenciamento de ordens de serviço, cadastro e gerenciamento de veículos, clientes e serviços, geração de relatórios, e implementação de um sistema de pontuação para um plano de fidelidade.

### Funcionalidades

1. __Controle de Ordens de Serviço (OS)__:
- Criação, atualização e gerenciamento de ordens de serviço.
- Acompanhamento do status das ordens de serviço (ABERTA, FECHADA, CANCELADA).
- Cálculo do valor total do serviço e aplicação de descontos.
2. __Cadastro e Gerenciamento de Veículos, Clientes e Serviços__:
- Cadastro e atualização de informações de clientes (Pessoa Física e Pessoa Jurídica).
- Cadastro e atualização de informações de veículos, incluindo modelo, marca, cor, motor, e tipo de combustível.
- Cadastro de diferentes serviços oferecidos pelo lava a jato, com descrição, valor e pontos de fidelidade associados.
3. __Geração de Relatórios__:
- Relatórios de serviços realizados.
- Relatórios de faturamento.
- Relatórios de pontos acumulados pelos clientes.
4. __Sistema de Pontuação para Plano de Fidelidade__:
- Implementação de um sistema de pontuação onde clientes acumulam pontos com base nos serviços adquiridos.
- Consulta e gerenciamento do saldo de pontos dos clientes.
- Adição e subtração de pontos conforme necessário.

### Diagrama de Classes

<table>
  <tr>
    <td>
      <img src="https://freecomputerbooks.com/covers/UML-Process.gif" alt="UML Image" width="50">
    </td>
    <td>
      <img src="https://seeklogo.com/images/M/mermaid-logo-31DD0B8905-seeklogo.com.png" alt="UML Image" width="60">
    </td>
  </tr>
</table>

<!-- [MermaidChart: 3c7d24e8-f066-4729-bf14-ab089cf5979a] -->



<---------------------------------->
``` mermaid
classDiagram

namespace PacketCliente {
    class ICliente {
        <<Interface>>
        +getDados() String
        +getDados(String Observacao) String
    }
    
    class Cliente {
        <<abstract>>
        #int id
        #String nome
        #String telefone
        #String email
        #Date dataCadastro
        +addVeiculos(veiculo: Veiculo) void
        +removeVeiculos(veiculo: Veiculo) void
        
    }

    class PontuacaoFidelidade {
        -int pontos
        +consultarSaldo() int
        +adicionar(qtd: int) int
        +subtrair(qtd: int) int
    }
    
    class PessoaFisica {
        -Date dataNascimento
        -String cpf
        
    }

    class PessoaJuridica {
        -String inscricaoEstadual
        -String cpfCnpj
    }
}
namespace PacketVeiculo {
    class Veiculo {
        -int id
        -String placa
        -String observacoes
        +Veiculo()
        +Veiculo(placa: String)
        +Veiculo(placa: String, modelo: Modelo)
    }

    class Cor {
        -int id
        -String nome
        +Cor()
        +Cor(nome: String)
    }

    class Modelo {
        -int id
        -String descricao
        +Modelo()
        +Modelo(descricao: String, marca: Marca)
    }

    class Motor {
        -int id
        -int potencia
        
    }

    class ETipoCombustivel {
        <<Enumeration>>
        - GASOLINA
        - ETANOL
        - FLEX
        - DIESEL 
        - GNV
        - OUTRO
    }
    
    class Marca {
        -int id
        -String nome
        +Marca()
        +Marca(nome: String)
    }

    class ECategoria {
        <<Enumeration>>
        - PEQUENO
        - MEDIO 
        - GRANDE
        - MOTO 
        - PADRAO
    }
}
namespace PacketOS {
    class Servico {
        -int id
        -String descricao
        -Double valor
        -int pontos
    }

    class ItemOS {
        -String observacao
        -Double valorServico
    }

    class EStatus {
        <<Enumeration>>
        - ABERTA 
        - FECHADA
        - CANCELADA 
    }

    class OrdemDeServico {
        -long numero
        -Double total
        -float desconto
        -Date agenda: não
        +calcularServico() double
        +add(itemDeOrdemDeServico: ItemOS) void
        +remove(itemDeOrdemDeServico: ItemOS) void
    }
}

  
        
    Cliente <|-- PessoaFisica
    Cliente <|-- PessoaJuridica
    Cliente ..|> ICliente
    Cliente "0..*" -- "1" Veiculo : possui
    Cliente "0..*" *--> "1" PontuacaoFidelidade : acumula

    Veiculo "0..*" --> "1" Cor: possui
    Veiculo "0..*" o-- "1" Modelo : referencia

    Modelo "0..*" --> "1" Marca: possui
    Modelo --> "1" ECategoria: possui
    Modelo "1" --> "1" Motor: referencia

    Motor --> "1" ETipoCombustivel

    Servico --> "1" ECategoria: referencia

    OrdemDeServico "0..*" o--> "1" Veiculo : referencia
    OrdemDeServico "*" *--> "*" ItemOS : inclui
    OrdemDeServico --> EStatus
    ItemOS "*" *--> "*" Servico : inclui
    
```
# Parte - 2
## Implementação de Herança e Interface



### Como Contribuir

1. Faça um fork deste repositório.
2. Crie um branch para a sua feature (git checkout -b feature/nome-da-feature).
3. Faça commit das suas alterações (git commit -am 'Adiciona nova feature').
4. Faça push para o branch (git push origin feature/nome-da-feature).
5. Crie um novo Pull Request.

Para dúvidas ou sugestões, por favor, entre em contato via email.

# Author

| [<img src="https://avatars.githubusercontent.com/u/79289647?v=4" width=115><br><sub>Carlos Hayden</sub>](https://github.com/JunhaumHayden) |
| :---: |