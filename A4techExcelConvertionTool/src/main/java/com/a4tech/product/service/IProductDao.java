package com.a4tech.product.service;

import java.util.List;

import com.a4tech.core.errors.ErrorMessage;
import com.a4tech.core.model.ExternalAPIResponse;

public interface IProductDao {
	public void save(ExternalAPIResponse errors ,String productNo ,Integer asiNumber);
	public int createBatchId(int asiNumber);
	public void save(List<ErrorMessage> errors ,String productNo ,Integer asiNumber,int batchId);
	public void saveErrorLog(int asiNumber ,int batchId);
	public boolean ftpFileProcessStatus(String fileName,Integer asiNumber);
}
