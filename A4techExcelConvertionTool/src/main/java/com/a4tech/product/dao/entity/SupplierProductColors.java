package com.a4tech.product.dao.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name="supplier_product_colors")
public class SupplierProductColors implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = -7967828257880982231L;
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name="ID")
	private Integer pcId;
	@Column(name="COLOR_GROUP")
	private String colorGroup;
	@Column(name="COLOR_NAME")
	private String colorName;
	/*@ManyToOne
	@JoinColumn(name="ASI_NUMBER_REF",insertable=true,updatable=false,referencedColumnName="ASI_NUMBER")
	private SupplierProductColors supplierProductColors;*/

	/*public SupplierProductColors getSupplierProductColors() {
		return supplierProductColors;
	}

	public void setSupplierProductColors(SupplierProductColors supplierProductColors) {
		this.supplierProductColors = supplierProductColors;
	}
*/
	@Column(name="ASI_NUMBER")
	private Integer asiNumber;
	public Integer getAsiNumber() {
		return asiNumber;
	}

	public void setAsiNumber(Integer asiNumber) {
		this.asiNumber = asiNumber;
	}

	public Integer getPcId() {
		return pcId;
	}

	public void setPcId(Integer pcId) {
		this.pcId = pcId;
	}

	public String getColorGroup() {
		return colorGroup;
	}

	public void setColorGroup(String colorGroup) {
		this.colorGroup = colorGroup;
	}

	public String getColorName() {
		return colorName;
	}

	public void setColorName(String colorName) {
		this.colorName = colorName;
	}

}
