-- ============================================================================
-- PKG_CLAIMS  -  ClaimsCore claim adjudication package (spec)
-- Larkspur Mutual Health Plan / ClaimsCore 2.7
-- $Id: PKG_CLAIMS.pks,v 1.14 2013/11/06 15:22:41 dpreston Exp $
-- Author: D. Preston (original 2008), maintained by R. Okafor
-- ============================================================================
CREATE OR REPLACE PACKAGE PKG_CLAIMS AS

    C_DUP_WINDOW_DAYS        CONSTANT NUMBER := 30;
    C_REOPEN_WINDOW_DAYS     CONSTANT NUMBER := 90;
    C_TIMELY_FILING_DAYS     CONSTANT NUMBER := 365;
    C_HIGH_DOLLAR_THRESHOLD  CONSTANT NUMBER := 25000;

    -- FIXME CR-2013-088: body still hard-codes 0.80 instead of using this constant
    C_FALLBACK_FEE_PCT       CONSTANT NUMBER := 0.80;

    C_RESULT_APPROVED        CONSTANT VARCHAR2(4) := 'APR';
    C_RESULT_PARTIAL         CONSTANT VARCHAR2(4) := 'PAR';
    C_RESULT_DENIED          CONSTANT VARCHAR2(4) := 'DEN';
    C_RESULT_ERROR           CONSTANT VARCHAR2(4) := 'ERR';

    E_INVALID_STATUS         EXCEPTION;
    PRAGMA EXCEPTION_INIT(E_INVALID_STATUS, -20001);

    E_CLAIM_NOT_FOUND        EXCEPTION;
    PRAGMA EXCEPTION_INIT(E_CLAIM_NOT_FOUND, -20002);

    E_REOPEN_WINDOW          EXCEPTION;
    PRAGMA EXCEPTION_INIT(E_REOPEN_WINDOW, -20003);

    PROCEDURE ADJUDICATE_CLAIM(p_claim_id   IN  NUMBER,
                               p_user       IN  VARCHAR2,
                               p_result_cd  OUT VARCHAR2,
                               p_message    OUT VARCHAR2);

    PROCEDURE REOPEN_CLAIM(p_claim_id   IN  NUMBER,
                           p_user       IN  VARCHAR2,
                           p_reason     IN  VARCHAR2);

    PROCEDURE VOID_CLAIM(p_claim_id   IN  NUMBER,
                         p_user       IN  VARCHAR2,
                         p_reason     IN  VARCHAR2);

    FUNCTION CALC_PATIENT_LIABILITY(p_allowed_amt        IN NUMBER,
                                    p_deductible_applied IN NUMBER,
                                    p_copay_amt          IN NUMBER,
                                    p_coinsurance_pct    IN NUMBER) RETURN NUMBER;

END PKG_CLAIMS;
/
