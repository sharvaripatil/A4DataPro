package com.a4tech.product.service;

@FunctionalInterface
public interface IMailService {
	public boolean sendMail(String supplierId,int batchId);

}
