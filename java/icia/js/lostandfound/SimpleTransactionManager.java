package icia.js.lostandfound;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionException;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

@Service("stm")
@Scope("prototype")
public class SimpleTransactionManager extends DefaultTransactionDefinition {
	private static final long serialVersionUID = 1L;
	
	@Autowired(required = false)
	@Qualifier("tran")
	private PlatformTransactionManager tranManager;
	private TransactionStatus tranStatus;
	
	/* tranManager ?òÑ?û¨ ?Å¥?ûò?ä§Î•? ?†Ñ?ã¨?ï®?úºÎ°úÏç® Transaction?ùÑ ?ãú?ûë */
	public void tranStart() throws TransactionException {
		this.tranStatus = tranManager.getTransaction(this);
	}
	
	public void tranEnd() throws TransactionException {
		this.rollback();
	}
	
	public void commit() throws TransactionException {
		if(!this.tranStatus.isCompleted()) this.tranManager.commit(this.tranStatus);
	}
	
	public void rollback() throws TransactionException {
		if(!this.tranStatus.isCompleted()) this.tranManager.rollback(this.tranStatus);
	}
		
	public void setTransactionConf(boolean isRead) {
		this.setPropagationBehavior(PROPAGATION_REQUIRED);
		this.setIsolationLevel(ISOLATION_READ_COMMITTED);
		this.setReadOnly(isRead);
	}
	public void setTransactionConf(boolean isRead,int propagation) {
		this.setPropagationBehavior(propagation);
		this.setIsolationLevel(ISOLATION_READ_COMMITTED);
		this.setReadOnly(isRead);
	}
}
