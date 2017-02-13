package com.leadbank.guardian.facade.validate;

import com.leadbank.guardian.facade.bean.AssertionRequest;
import com.leadbank.guardian.facade.bean.AssertionResponse;


public interface ServiceValidateFacade {
	
	AssertionResponse validate(AssertionRequest req) throws Exception;
}
