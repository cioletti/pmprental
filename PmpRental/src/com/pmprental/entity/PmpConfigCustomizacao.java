/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.pmprental.entity;

import java.io.Serializable;

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
 * @author Rodrigo
 */
@Entity
@Table(name = "REN_PMP_CONFIG_CUSTOMIZACAO")
public class PmpConfigCustomizacao implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @Column(name = "ID", nullable = false)
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;
    @JoinColumn(name = "ID_TIPO_CUSTOMIZACAO", referencedColumnName = "ID")
    @ManyToOne(optional = false)
    private PmpTipoCustomizacao idTipoCustomizacao;
    @JoinColumn(name = "ID_FAMILIA", referencedColumnName = "ID")
    @ManyToOne(optional = false)
    private PmpFamilia idFamilia;
    @JoinColumn(name = "ID_MODELO", referencedColumnName = "ID_ARV")
    @ManyToOne
    private ArvInspecao idModelo;

    public PmpConfigCustomizacao() {
    }

    public PmpConfigCustomizacao(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public PmpTipoCustomizacao getIdTipoCustomizacao() {
        return idTipoCustomizacao;
    }

    public void setIdTipoCustomizacao(PmpTipoCustomizacao idTipoCustomizacao) {
        this.idTipoCustomizacao = idTipoCustomizacao;
    }

    public PmpFamilia getIdFamilia() {
        return idFamilia;
    }

    public void setIdFamilia(PmpFamilia idFamilia) {
        this.idFamilia = idFamilia;
    }

    public ArvInspecao getIdModelo() {
        return idModelo;
    }

    public void setIdModelo(ArvInspecao idModelo) {
        this.idModelo = idModelo;
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
        if (!(object instanceof PmpConfigCustomizacao)) {
            return false;
        }
        PmpConfigCustomizacao other = (PmpConfigCustomizacao) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.pmp.entity.PmpConfigCustomizacao[ id=" + id + " ]";
    }
    
}
