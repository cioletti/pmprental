/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.pmprental.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Collection;
import java.util.Date;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 *
 * @author RDR
 */
@Entity
@Table(name = "REN_PMP_CONFIGURACAO_PRECOS")
public class PmpConfiguracaoPrecos implements Serializable {
    private static final long serialVersionUID = 1L;
    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @Id 
    @Basic(optional = false)
    @Column(name = "ID", nullable = false)
    @GeneratedValue(strategy=GenerationType.IDENTITY) 
    private Long id;
    @Column(name = "HH_RENTAL")
    private BigDecimal hhRental;
    @Column(name = "HH_PMP")
    private BigDecimal hhPmp;
    @Column(name = "KM_PMP")
    private BigInteger kmPmp;
    @Column(name = "KM_RENTAL")
    private BigInteger kmRental;
    @Column(name = "CUSTO_NORDESTE")
    private BigDecimal custoNordeste;
    @Column(name = "VALIDADE_CONTRATO")
    private BigInteger validadeContrato;
    @Column(name = "ID_FUNCIONARIO")
    private String idFuncionario;
    @Column(name = "JUROS_VENDA")
    private BigDecimal jurosVenda;
    @Column(name = "VALOR_KM_PMP")
    private BigDecimal valorKmPmp;
    @Column(name = "VALOR_KM_RENTAL")
    private BigDecimal valorKmRental;
    @Column(name = "VALOR_HH_PMP_CUSTO")
    private BigDecimal valorHhPmpCusto;
    @Column(name = "VALOR_KM_PMP_CUSTO")
    private BigDecimal valorKmPmpCusto;
    @Column(name = "VALOR_HH_TA_CUSTO")
    private BigDecimal valorHhTaCusto;
    @Column(name = "DATE_CONFIGURACAO")
    @Temporal(TemporalType.TIMESTAMP)
    private Date dateConfiguracao;
    @Column(name = "TA_HORAS")
    private BigDecimal taHoras;
    @Column(name = "HH_TA")
    private BigDecimal hhTa;
    @Column(name = "DESCONTO_PRE_PAGO")
    private BigDecimal descontoPrePago;
    @Column(name = "DESCRICAO")
    private String descricao;
    @Column(name = "DESC_PDP")
    private BigDecimal descPdp;
    @Column(name = "COMISSAO_CONSULTOR")
    private BigDecimal comissaoConsultor;
    @Column(name = "COMISSAO_INDICACAO")
    private BigDecimal comissaoIndicacao;
    @OneToMany(mappedBy = "idConfiguracaoPreco")
    private Collection<PmpConfigManutencao> pmpConfigManutencaoCollection;
    @Column(name = "VALOR_SPOT")
    private BigDecimal valorSpot;
    @Column(name = "KM_PMP_SPOT")
    private BigInteger kmPmpSpot;
    @Column(name = "PDP_SPOT")
    private BigDecimal pdpSpot;
    @Column(name = "DESC_PECAS")
    private BigDecimal descPecas;

    public PmpConfiguracaoPrecos() {
    }

    public PmpConfiguracaoPrecos(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public BigDecimal getHhRental() {
        return hhRental;
    }

    public void setHhRental(BigDecimal hhRental) {
        this.hhRental = hhRental;
    }

    public BigDecimal getHhPmp() {
        return hhPmp;
    }

    public void setHhPmp(BigDecimal hhPmp) {
        this.hhPmp = hhPmp;
    }

    public BigInteger getKmPmp() {
        return kmPmp;
    }

    public void setKmPmp(BigInteger kmPmp) {
        this.kmPmp = kmPmp;
    }

    public BigInteger getKmRental() {
        return kmRental;
    }

    public void setKmRental(BigInteger kmRental) {
        this.kmRental = kmRental;
    }

    public BigDecimal getCustoNordeste() {
        return custoNordeste;
    }

    public void setCustoNordeste(BigDecimal custoNordeste) {
        this.custoNordeste = custoNordeste;
    }

    public BigInteger getValidadeContrato() {
        return validadeContrato;
    }

    public void setValidadeContrato(BigInteger validadeContrato) {
        this.validadeContrato = validadeContrato;
    }

    public String getIdFuncionario() {
        return idFuncionario;
    }

    public void setIdFuncionario(String idFuncionario) {
        this.idFuncionario = idFuncionario;
    }

    public BigDecimal getJurosVenda() {
        return jurosVenda;
    }

    public void setJurosVenda(BigDecimal jurosVenda) {
        this.jurosVenda = jurosVenda;
    }

    public BigDecimal getValorKmPmp() {
        return valorKmPmp;
    }

    public void setValorKmPmp(BigDecimal valorKmPmp) {
        this.valorKmPmp = valorKmPmp;
    }

    public BigDecimal getValorKmRental() {
        return valorKmRental;
    }

    public void setValorKmRental(BigDecimal valorKmRental) {
        this.valorKmRental = valorKmRental;
    }

    public Date getDateConfiguracao() {
        return dateConfiguracao;
    }

    public void setDateConfiguracao(Date dateConfiguracao) {
        this.dateConfiguracao = dateConfiguracao;
    }

    public BigDecimal getTaHoras() {
        return taHoras;
    }

    public void setTaHoras(BigDecimal taHoras) {
        this.taHoras = taHoras;
    }

    public BigDecimal getHhTa() {
        return hhTa;
    }

    public void setHhTa(BigDecimal hhTa) {
        this.hhTa = hhTa;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public BigDecimal getDescPdp() {
        return descPdp;
    }

    public void setDescPdp(BigDecimal descPdp) {
        this.descPdp = descPdp;
    }

    public Collection<PmpConfigManutencao> getPmpConfigManutencaoCollection() {
        return pmpConfigManutencaoCollection;
    }

    public void setPmpConfigManutencaoCollection(Collection<PmpConfigManutencao> pmpConfigManutencaoCollection) {
        this.pmpConfigManutencaoCollection = pmpConfigManutencaoCollection;
    }

    public BigDecimal getValorHhPmpCusto() {
		return valorHhPmpCusto;
	}

	public void setValorHhPmpCusto(BigDecimal valorHhPmpCusto) {
		this.valorHhPmpCusto = valorHhPmpCusto;
	}

	public BigDecimal getValorKmPmpCusto() {
		return valorKmPmpCusto;
	}

	public void setValorKmPmpCusto(BigDecimal valorKmPmpCusto) {
		this.valorKmPmpCusto = valorKmPmpCusto;
	}

	public BigDecimal getValorHhTaCusto() {
		return valorHhTaCusto;
	}

	public void setValorHhTaCusto(BigDecimal valorHhTaCusto) {
		this.valorHhTaCusto = valorHhTaCusto;
	}

	public BigDecimal getDescontoPrePago() {
		return descontoPrePago;
	}

	public void setDescontoPrePago(BigDecimal descontoPrePago) {
		this.descontoPrePago = descontoPrePago;
	}

	public BigDecimal getComissaoConsultor() {
		return comissaoConsultor;
	}

	public void setComissaoConsultor(BigDecimal comissaoConsultor) {
		this.comissaoConsultor = comissaoConsultor;
	}

	public BigDecimal getComissaoIndicacao() {
		return comissaoIndicacao;
	}

	public void setComissaoIndicacao(BigDecimal comissaoIndicacao) {
		this.comissaoIndicacao = comissaoIndicacao;
	}

	public BigDecimal getValorSpot() {
		return valorSpot;
	}

	public void setValorSpot(BigDecimal valorSpot) {
		this.valorSpot = valorSpot;
	}

	public BigInteger getKmPmpSpot() {
		return kmPmpSpot;
	}

	public void setKmPmpSpot(BigInteger kmPmpSpot) {
		this.kmPmpSpot = kmPmpSpot;
	}

	public BigDecimal getPdpSpot() {
		return pdpSpot;
	}

	public void setPdpSpot(BigDecimal pdpSpot) {
		this.pdpSpot = pdpSpot;
	}

	public BigDecimal getDescPecas() {
		return descPecas;
	}

	public void setDescPecas(BigDecimal descPecas) {
		this.descPecas = descPecas;
	}

	@Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof PmpConfiguracaoPrecos)) {
            return false;
        }
        PmpConfiguracaoPrecos other = (PmpConfiguracaoPrecos) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.pmp.entity.PmpConfiguracaoPrecos[ id=" + id + " ]";
    }
    
}
