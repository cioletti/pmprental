/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.pmprental.entity;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.Collection;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;

/**
 *
 * @author sandro
 */
@Entity
@Table(name = "TW_FILIAL")
@NamedQueries({
    @NamedQuery(name = "TwFilial.findAll", query = "SELECT t FROM TwFilial t")})
public class TwFilial implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @Column(name = "STNO")
    private Long stno;
    @Column(name = "STNM")
    private String stnm;
    @Column(name = "REGIONAL")
    private BigInteger regional;
    @Column(name = "DESCRICAO_STRATEC")
    private String descricaoStratec;
    @Column(name = "SIGLA_RENTAL")
    private String siglaRental;
    

    public TwFilial() {
    }

    public TwFilial(Long stno) {
        this.stno = stno;
    }

    public Long getStno() {
        return stno;
    }

    public void setStno(Long stno) {
        this.stno = stno;
    }

    public String getStnm() {
        return stnm;
    }

    public void setStnm(String stnm) {
        this.stnm = stnm;
    }

    public BigInteger getRegional() {
        return regional;
    }

    public void setRegional(BigInteger regional) {
        this.regional = regional;
    }



    public String getDescricaoStratec() {
		return descricaoStratec;
	}

	public void setDescricaoStratec(String descricaoStratec) {
		this.descricaoStratec = descricaoStratec;
	}

	public String getSiglaRental() {
		return siglaRental;
	}

	public void setSiglaRental(String siglaRental) {
		this.siglaRental = siglaRental;
	}

	@Override
    public int hashCode() {
        int hash = 0;
        hash += (stno != null ? stno.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof TwFilial)) {
            return false;
        }
        TwFilial other = (TwFilial) object;
        if ((this.stno == null && other.stno != null) || (this.stno != null && !this.stno.equals(other.stno))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.treinamento.entity.TwFilial[ stno=" + stno + " ]";
    }
    
}
