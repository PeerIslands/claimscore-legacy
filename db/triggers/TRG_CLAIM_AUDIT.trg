-- ============================================================================
-- TRG_CLAIM_AUDIT  -  row-level audit of CLAIMS into CLAIM_AUDIT_LOG
-- Larkspur Mutual Health Plan / ClaimsCore 2.7
-- $Id: TRG_CLAIM_AUDIT.trg,v 1.6 2011/08/22 17:03:55 dpreston Exp $
-- Author: D. Preston
-- ============================================================================
CREATE OR REPLACE TRIGGER TRG_CLAIM_AUDIT
AFTER INSERT OR UPDATE ON CLAIMS
FOR EACH ROW
DECLARE
    v_action_cd CLAIM_AUDIT_LOG.ACTION_CD%TYPE;
    v_detail    CLAIM_AUDIT_LOG.DETAIL%TYPE;
BEGIN
    IF INSERTING THEN
        v_action_cd := 'INS';
        v_detail    := 'Claim ' || :NEW.CLAIM_NUM || ' created in status ' || :NEW.STATUS_CD;
    ELSIF NVL(:OLD.STATUS_CD, '~') <> NVL(:NEW.STATUS_CD, '~') THEN
        v_action_cd := 'STS';
        v_detail    := 'Status ' || :OLD.STATUS_CD || ' -> ' || :NEW.STATUS_CD
                       || ' (version ' || :NEW.VERSION_NO || ')';
    ELSIF NVL(:OLD.TOTAL_PAID_AMT, -1) <> NVL(:NEW.TOTAL_PAID_AMT, -1) THEN
        v_action_cd := 'PAY';
        v_detail    := 'Paid ' || NVL(:OLD.TOTAL_PAID_AMT, 0) || ' -> ' || NVL(:NEW.TOTAL_PAID_AMT, 0);
    ELSE
        v_action_cd := 'UPD';
        v_detail    := 'Header updated (version ' || :NEW.VERSION_NO || ')';
    END IF;

    -- TODO INC-4102: AuditLogDAO.log writes the same row from the app tier
    INSERT INTO CLAIM_AUDIT_LOG
        (AUDIT_ID, CLAIM_ID, ACTION_CD, OLD_STATUS_CD, NEW_STATUS_CD,
         OLD_PAID_AMT, NEW_PAID_AMT, CHANGED_BY, CHANGED_DT, OS_USER, TERMINAL, DETAIL)
    VALUES
        (SEQ_AUDIT.NEXTVAL, :NEW.CLAIM_ID, v_action_cd, :OLD.STATUS_CD, :NEW.STATUS_CD,
         :OLD.TOTAL_PAID_AMT, :NEW.TOTAL_PAID_AMT, NVL(:NEW.UPDATED_BY, USER), SYSDATE,
         SYS_CONTEXT('USERENV', 'OS_USER'), SYS_CONTEXT('USERENV', 'TERMINAL'),
         SUBSTR(v_detail, 1, 1000));
END;
/
