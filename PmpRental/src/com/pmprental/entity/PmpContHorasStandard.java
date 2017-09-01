/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.pmprental.entity;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 *
 * @author RDR
 */
@Entity
@Table(name = "REN_PMP_CONT_HORAS_STANDARD")
public class PmpContHorasStandard implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id 
    @Column(name = "ID", nullable = false)
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;
    @Column(name = "HORAS")
    private Long horas;
    @Column(name = "STANDARD_JOB_CPTCD")
    private String standardJobCptcd;
    @Column(name = "FREQUENCIA")
    private Long frequencia;
    @Basic(optional = false)
    @Column(name = "IS_EXECUTADO")
    private String isExecutado;
    @Column(name = "HORAS_MANUTENCAO")
    private Long horasManutencao;
    @Column(name = "HORAS_REVISAO")
    private Long horasRevisao;
//    @JoinColumn(name = "ID_OS_OPERACIONAL", referencedColumnName = "ID")
//    @ManyToOne
//    private PmpOsOperacional idOsOperacional;
    
    @Column(name = "ID_OS_OPERACIONAL")
    private Long idOsOperacional;
    
    @JoinColumn(name = "ID_CONTRATO", referencedColumnName = "ID")
    @ManyToOne
    private PmpContrato idContrato;
    @Column(name = "CUSTO")
    private BigDecimal custo;
    @Column(name = "TIPO_PM")
    private String tipoPm;
    
    @Column(name = "CUSTO_MO")
    private BigDecimal custoMo;
    
    @Column(name = "CUSTO_PECAS")
    private BigDecimal custoPecas;

    public PmpContHorasStandard() {
    }

    public PmpContHorasStandard(Long id) {
        this.id = id;
    }

    public PmpContHorasStandard(Long id, String isExecutado) {
        this.id = id;
        this.isExecutado = isExecutado;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getHoras() {
        return horas;
    }

    public void setHoras(Long horas) {
        this.horas = horas;
    }

    public String getStandardJobCptcd() {
        return standardJobCptcd;
    }

    public void setStandardJobCptcd(String standardJobCptcd) {
        this.standardJobCptcd = standardJobCptcd;
    }

    public Long getFrequencia() {
        return frequencia;
    }

    public void setFrequencia(Long frequencia) {
        this.frequencia = frequencia;
    }

    public String getIsExecutado() {
        return isExecutado;
    }

    public void setIsExecutado(String isExecutado) {
        this.isExecutado = isExecutado;
    }

    public Long getHorasManutencao() {
        return horasManutencao;
    }

    public void setHorasManutencao(Long horasManutencao) {
        this.horasManutencao = horasManutencao;
    }

    public Long getIdOsOperacional() {
        return idOsOperacional;
    }

    public void setIdOsOperacional(Long idOsOperacional) {
        this.idOsOperacional = idOsOperacional;
    }

    public PmpContrato getIdContrato() {
        return idContrato;
    }

    public void setIdContrato(PmpContrato idContrato) {
        this.idContrato = idContrato;
    }

    public Long getHorasRevisao() {
		return horasRevisao;
	}

	public void setHorasRevisao(Long horasRevisao) {
		this.horasRevisao = horasRevisao;
	}

	public BigDecimal getCusto() {
		return custo;
	}

	public void setCusto(BigDecimal custo) {
		this.custo = custo;
	}

	public String getTipoPm() {
		return tipoPm;
	}

	public void setTipoPm(String tipoPm) {
		this.tipoPm = tipoPm;
	}

	public BigDecimal getCustoMo() {
		return custoMo;
	}

	public void setCustoMo(BigDecimal custoMo) {
		this.custoMo = custoMo;
	}

	public BigDecimal getCustoPecas() {
		return custoPecas;
	}

	public void setCustoPecas(BigDecimal custoPecas) {
		this.custoPecas = custoPecas;
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
        if (!(object instanceof PmpContHorasStandard)) {
            return false;
        }
        PmpContHorasStandard other = (PmpContHorasStandard) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.pmp.entity.PmpContHorasStandard[ id=" + id + " ]";
    }
    
}
