package icia.js.lostandfound;

import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

@Service("tranAssistant")
public class TransactionAssistant {
	@Autowired
	protected SqlSessionTemplate session;
	@Autowired
	ApplicationContext applicationContext;
	
	protected SimpleTransactionManager getTransaction(boolean isRead) {
		SimpleTransactionManager tranMgr = applicationContext.getBean(SimpleTransactionManager.class);
		tranMgr.setTransactionConf(isRead);
		return tranMgr;
	}
	protected SimpleTransactionManager getTransaction(boolean isRead,int propagation) {
		SimpleTransactionManager tranMgr = applicationContext.getBean(SimpleTransactionManager.class);
		tranMgr.setTransactionConf(isRead,propagation);
		return tranMgr;
	}
}
