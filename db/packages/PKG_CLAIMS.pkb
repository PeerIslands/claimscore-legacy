-- ============================================================================
-- PKG_CLAIMS  -  ClaimsCore claim adjudication package (body)
-- Larkspur Mutual Health Plan / ClaimsCore 2.7
-- $Id: PKG_CLAIMS.pkb,v 1.41 2016/03/18 09:47:12 rokafor Exp $
-- Author: D. Preston (original 2008), maintained by R. Okafor
-- ============================================================================
CREATE OR REPLACE PACKAGE BODY PKG_CLAIMS AS

    -- ------------------------------------------------------------------------
    -- private helpers
    -- ------------------------------------------------------------------------

    FUNCTION PLAN_YEAR_OF(p_dt IN DATE, p_start_mm IN NUMBER) RETURN NUMBER IS
        v_year NUMBER;
        v_mm   NUMBER;
    BEGIN
        v_year := TO_NUMBER(TO_CHAR(p_dt, 'YYYY'));
        v_mm   := TO_NUMBER(TO_CHAR(p_dt, 'MM'));
        IF v_mm < NVL(p_start_mm, 1) THEN
            RETURN v_year - 1;
        END IF;
        RETURN v_year;
    END PLAN_YEAR_OF;

    FUNCTION YTD_PAID_FOR_CATEGORY(p_ph_id       IN NUMBER,
                                   p_plan_id     IN NUMBER,
                                   p_category_cd IN VARCHAR2,
                                   p_plan_year   IN NUMBER) RETURN NUMBER IS
        v_total NUMBER := 0;
    BEGIN
        -- FIXME INC-3902: uses calendar year, ignores PLANS.PLAN_YEAR_START_MM
        SELECT NVL(SUM(L.PAID_AMT), 0)
          INTO v_total
          FROM CLAIM_LINES L, CLAIMS C, CPT_CODES S
         WHERE L.CLAIM_ID = C.CLAIM_ID
           AND L.SVC_CD = S.SVC_CD
           AND C.PH_ID = p_ph_id
           AND C.PLAN_ID = p_plan_id
           AND S.CATEGORY_CD = p_category_cd
           AND C.STATUS_CD IN ('APR', 'PAR', 'PAID')
           AND L.LINE_STATUS_CD IN ('APR', 'PAID')
           AND TO_NUMBER(TO_CHAR(C.INCIDENT_DT, 'YYYY')) = p_plan_year;
        RETURN v_total;
    END YTD_PAID_FOR_CATEGORY;

    PROCEDURE LOG_STATUS_CHANGE(p_claim_id IN NUMBER,
                                p_from     IN VARCHAR2,
                                p_to       IN VARCHAR2,
                                p_user     IN VARCHAR2,
                                p_reason   IN VARCHAR2) IS
    BEGIN
        INSERT INTO CLAIM_STATUS_HISTORY
            (HIST_ID, CLAIM_ID, FROM_STATUS_CD, TO_STATUS_CD, CHANGED_BY, CHANGED_DT, REASON)
        VALUES
            (SEQ_CLAIM_HIST.NEXTVAL, p_claim_id, p_from, p_to, NVL(p_user, USER), SYSDATE,
             SUBSTR(p_reason, 1, 400));
    END LOG_STATUS_CHANGE;

    PROCEDURE ENQUEUE_WORK_ITEM(p_claim_id IN NUMBER, p_queue_name IN VARCHAR2) IS
        v_queue_id WORK_QUEUES.QUEUE_ID%TYPE;
    BEGIN
        SELECT QUEUE_ID INTO v_queue_id FROM WORK_QUEUES WHERE QUEUE_NAME = p_queue_name;
        INSERT INTO WORK_ITEMS (ITEM_ID, QUEUE_ID, CLAIM_ID, STATUS_CD, CREATED_DT)
        VALUES (SEQ_WORK_ITEM.NEXTVAL, v_queue_id, p_claim_id, 'O', SYSDATE);
    EXCEPTION
        WHEN NO_DATA_FOUND THEN
            NULL;
    END ENQUEUE_WORK_ITEM;

    PROCEDURE DENY_WHOLE_CLAIM(p_claim  IN CLAIMS%ROWTYPE,
                               p_reason IN VARCHAR2,
                               p_user   IN VARCHAR2,
                               p_note   IN VARCHAR2) IS
    BEGIN
        UPDATE CLAIM_LINES
           SET LINE_STATUS_CD = 'DEN', DENIAL_REASON_CD = p_reason, ALLOWED_AMT = 0, PAID_AMT = 0
         WHERE CLAIM_ID = p_claim.CLAIM_ID;
        INSERT INTO CLAIM_DENIAL_DETAILS
            (DENIAL_ID, CLAIM_ID, LINE_ID, DENIAL_REASON_CD, DENIED_AMT, DENIED_DT, NOTES)
        VALUES
            (SEQ_CLAIM_DENIAL.NEXTVAL, p_claim.CLAIM_ID, NULL, p_reason, p_claim.TOTAL_BILLED_AMT,
             SYSDATE, SUBSTR(p_note, 1, 1000));
        UPDATE CLAIMS
           SET STATUS_CD = 'DEN',
               DENIAL_REASON_CD = p_reason,
               TOTAL_ALLOWED_AMT = 0,
               TOTAL_PAID_AMT = 0,
               PATIENT_LIAB_AMT = 0,
               DEDUCTIBLE_APPLIED_AMT = 0,
               ADJUDICATED_DT = SYSDATE,
               UPDATED_BY = p_user,
               UPDATED_DT = SYSDATE,
               VERSION_NO = VERSION_NO + 1
         WHERE CLAIM_ID = p_claim.CLAIM_ID;
        LOG_STATUS_CHANGE(p_claim.CLAIM_ID, p_claim.STATUS_CD, 'DEN', p_user, p_note);
    END DENY_WHOLE_CLAIM;

    -- ------------------------------------------------------------------------
    -- public
    -- ------------------------------------------------------------------------

    FUNCTION CALC_PATIENT_LIABILITY(p_allowed_amt        IN NUMBER,
                                    p_deductible_applied IN NUMBER,
                                    p_copay_amt          IN NUMBER,
                                    p_coinsurance_pct    IN NUMBER) RETURN NUMBER IS
        v_coins NUMBER;
    BEGIN
        v_coins := (NVL(p_allowed_amt, 0) - NVL(p_deductible_applied, 0) - NVL(p_copay_amt, 0))
                   * NVL(p_coinsurance_pct, 0) / 100;
        IF v_coins < 0 THEN
            v_coins := 0;
        END IF;
        RETURN ROUND(NVL(p_deductible_applied, 0) + NVL(p_copay_amt, 0) + v_coins, 2);
    END CALC_PATIENT_LIABILITY;

    PROCEDURE ADJUDICATE_CLAIM(p_claim_id   IN  NUMBER,
                               p_user       IN  VARCHAR2,
                               p_result_cd  OUT VARCHAR2,
                               p_message    OUT VARCHAR2) IS
        v_claim              CLAIMS%ROWTYPE;
        v_plan               PLANS%ROWTYPE;
        v_plan_year          NUMBER;
        v_dup_count          NUMBER := 0;
        v_enroll_count       NUMBER := 0;
        v_category           CPT_CODES.CATEGORY_CD%TYPE;
        v_fee                NUMBER;
        v_allowed            NUMBER;
        v_coverage_limit     PLAN_COVERAGE_LIMITS.PER_CLAIM_LIMIT_AMT%TYPE;
        v_annual_limit       PLAN_COVERAGE_LIMITS.ANNUAL_LIMIT_AMT%TYPE;
        v_line_status        CLAIM_LINES.LINE_STATUS_CD%TYPE;
        v_line_reason        CLAIM_LINES.DENIAL_REASON_CD%TYPE;
        v_first_reason       CLAIM_LINES.DENIAL_REASON_CD%TYPE;
        v_total_allowed      NUMBER := 0;
        v_total_billed       NUMBER := 0;
        v_line_count         NUMBER := 0;
        v_denied_lines       NUMBER := 0;
        v_deductible_met     MEMBER_DEDUCTIBLES.DEDUCTIBLE_MET_AMT%TYPE;
        v_deductible_remain  NUMBER;
        v_deductible_applied NUMBER := 0;
        v_copay              NUMBER := 0;
        v_liability          NUMBER := 0;
        v_oop_met            MEMBER_OOP_ACCUM.OOP_MET_AMT%TYPE;
        v_paid               NUMBER := 0;
        v_distributed        NUMBER := 0;
        v_line_paid          NUMBER;
        v_approved_lines     NUMBER := 0;
        v_idx                NUMBER := 0;
        v_new_status         CLAIMS.STATUS_CD%TYPE;
        v_denial_reason      CLAIMS.DENIAL_REASON_CD%TYPE;

        CURSOR c_lines IS
            SELECT LINE_ID, LINE_NO, SVC_CD, UNITS, BILLED_AMT
              FROM CLAIM_LINES
             WHERE CLAIM_ID = p_claim_id
             ORDER BY LINE_NO;

        CURSOR c_approved IS
            SELECT LINE_ID, ALLOWED_AMT
              FROM CLAIM_LINES
             WHERE CLAIM_ID = p_claim_id
               AND LINE_STATUS_CD = 'APR'
             ORDER BY LINE_NO;
    BEGIN
        p_result_cd := NULL;
        p_message   := NULL;

        -- Rule 1: lock the claim and check the status is adjudicable
        BEGIN
            SELECT * INTO v_claim
              FROM CLAIMS
             WHERE CLAIM_ID = p_claim_id
               FOR UPDATE NOWAIT;
        EXCEPTION
            WHEN NO_DATA_FOUND THEN
                RAISE_APPLICATION_ERROR(-20002, 'Claim ' || p_claim_id || ' not found');
        END;
        IF v_claim.STATUS_CD NOT IN ('REC', 'PND', 'REO') THEN
            RAISE_APPLICATION_ERROR(-20001, 'Claim ' || v_claim.CLAIM_NUM || ' in status '
                || v_claim.STATUS_CD || ' cannot be adjudicated');
        END IF;
        SELECT * INTO v_plan FROM PLANS WHERE PLAN_ID = v_claim.PLAN_ID;
        v_plan_year := PLAN_YEAR_OF(v_claim.INCIDENT_DT, v_plan.PLAN_YEAR_START_MM);

        -- Rule 2: timely filing
        IF TRUNC(v_claim.RECEIVED_DT) - TRUNC(v_claim.INCIDENT_DT) > C_TIMELY_FILING_DAYS THEN
            DENY_WHOLE_CLAIM(v_claim, 'TIMELY', p_user,
                'Received ' || TO_CHAR(v_claim.RECEIVED_DT, 'MM/DD/YYYY') || ' for incident '
                || TO_CHAR(v_claim.INCIDENT_DT, 'MM/DD/YYYY'));
            p_result_cd := C_RESULT_DENIED;
            p_message   := 'Denied: timely filing limit of ' || C_TIMELY_FILING_DAYS || ' days exceeded';
            RETURN;
        END IF;

        -- Rule 3: duplicate claim within the 30 day window
        SELECT COUNT(*)
          INTO v_dup_count
          FROM CLAIMS C2
         WHERE C2.PH_ID = v_claim.PH_ID
           AND C2.PROVIDER_ID = v_claim.PROVIDER_ID
           AND C2.CLAIM_ID <> v_claim.CLAIM_ID
           AND C2.STATUS_CD NOT IN ('DEN', 'VOID')
           AND C2.INCIDENT_DT BETWEEN v_claim.INCIDENT_DT - C_DUP_WINDOW_DAYS
                                  AND v_claim.INCIDENT_DT + C_DUP_WINDOW_DAYS
           AND EXISTS (SELECT 1
                         FROM CLAIM_LINES L1, CLAIM_LINES L2
                        WHERE L1.CLAIM_ID = C2.CLAIM_ID
                          AND L2.CLAIM_ID = v_claim.CLAIM_ID
                          AND L1.SVC_CD = L2.SVC_CD);
        IF v_dup_count > 0 THEN
            DENY_WHOLE_CLAIM(v_claim, 'DUP30', p_user,
                v_dup_count || ' matching claim(s) within ' || C_DUP_WINDOW_DAYS || ' days');
            p_result_cd := C_RESULT_DENIED;
            p_message   := 'Denied: duplicate of an existing claim';
            RETURN;
        END IF;

        -- Rule 4: active enrollment on the incident date
        SELECT COUNT(*)
          INTO v_enroll_count
          FROM PH_PLAN_ENROLLMENTS E
         WHERE E.PH_ID = v_claim.PH_ID
           AND E.PLAN_ID = v_claim.PLAN_ID
           AND E.EFFECTIVE_DT <= v_claim.INCIDENT_DT
           AND (E.TERM_DT IS NULL OR E.TERM_DT >= v_claim.INCIDENT_DT);
        IF v_enroll_count = 0 THEN
            DENY_WHOLE_CLAIM(v_claim, 'NOENR', p_user,
                'No enrollment in plan ' || v_plan.PLAN_CODE || ' on '
                || TO_CHAR(v_claim.INCIDENT_DT, 'MM/DD/YYYY'));
            p_result_cd := C_RESULT_DENIED;
            p_message   := 'Denied: no active enrollment on incident date';
            RETURN;
        END IF;

        -- Rule 5: price each line (fee schedule, per-claim and annual coverage limit)
        FOR r_line IN c_lines LOOP
            v_line_status    := 'APR';
            v_line_reason    := NULL;
            v_allowed        := 0;
            v_coverage_limit := NULL;
            v_annual_limit   := NULL;
            BEGIN
                SELECT CATEGORY_CD INTO v_category FROM CPT_CODES WHERE SVC_CD = r_line.SVC_CD;
            EXCEPTION
                WHEN NO_DATA_FOUND THEN
                    v_line_status := 'DEN';
                    v_line_reason := 'INVSVC';
            END;
            IF v_line_status = 'APR' THEN
                BEGIN
                    SELECT ALLOWED_AMT * NVL(r_line.UNITS, 1)
                      INTO v_fee
                      FROM CPT_FEE_SCHEDULE
                     WHERE SVC_CD = r_line.SVC_CD
                       AND PLAN_YEAR = v_plan_year;
                EXCEPTION
                    WHEN NO_DATA_FOUND THEN
                        v_fee := r_line.BILLED_AMT * 0.80;
                END;
                v_allowed := ROUND(LEAST(r_line.BILLED_AMT, v_fee), 2);
                BEGIN
                    SELECT PER_CLAIM_LIMIT_AMT, ANNUAL_LIMIT_AMT
                      INTO v_coverage_limit, v_annual_limit
                      FROM PLAN_COVERAGE_LIMITS
                     WHERE PLAN_ID = v_claim.PLAN_ID
                       AND CATEGORY_CD = v_category;
                EXCEPTION
                    WHEN NO_DATA_FOUND THEN
                        NULL;
                END;
                IF v_coverage_limit IS NOT NULL AND v_allowed > v_coverage_limit THEN
                    v_allowed := v_coverage_limit;
                END IF;
                IF v_annual_limit IS NOT NULL
                   AND YTD_PAID_FOR_CATEGORY(v_claim.PH_ID, v_claim.PLAN_ID, v_category, v_plan_year)
                       + v_allowed > v_annual_limit THEN
                    v_line_status := 'DEN';
                    v_line_reason := 'LIMIT';
                    INSERT INTO CLAIM_DENIAL_DETAILS
                        (DENIAL_ID, CLAIM_ID, LINE_ID, DENIAL_REASON_CD, DENIED_AMT, DENIED_DT, NOTES)
                    VALUES
                        (SEQ_CLAIM_DENIAL.NEXTVAL, v_claim.CLAIM_ID, r_line.LINE_ID, 'LIMIT',
                         r_line.BILLED_AMT, SYSDATE,
                         'Annual limit ' || v_annual_limit || ' for ' || v_category || ' exceeded');
                END IF;
            END IF;
            IF v_line_status = 'DEN' THEN
                v_allowed      := 0;
                v_denied_lines := v_denied_lines + 1;
                IF v_first_reason IS NULL THEN
                    v_first_reason := v_line_reason;
                END IF;
            END IF;
            UPDATE CLAIM_LINES
               SET ALLOWED_AMT = v_allowed,
                   PAID_AMT = 0,
                   LINE_STATUS_CD = v_line_status,
                   DENIAL_REASON_CD = v_line_reason
             WHERE LINE_ID = r_line.LINE_ID;
            v_total_allowed := v_total_allowed + v_allowed;
            v_total_billed  := v_total_billed + r_line.BILLED_AMT;
            v_line_count    := v_line_count + 1;
        END LOOP;

        -- Rule 6: deductible
        BEGIN
            SELECT DEDUCTIBLE_MET_AMT
              INTO v_deductible_met
              FROM MEMBER_DEDUCTIBLES
             WHERE PH_ID = v_claim.PH_ID
               AND PLAN_ID = v_claim.PLAN_ID
               AND PLAN_YEAR = v_plan_year
               FOR UPDATE;
        EXCEPTION
            WHEN NO_DATA_FOUND THEN
                INSERT INTO MEMBER_DEDUCTIBLES
                    (ACCUM_ID, PH_ID, PLAN_ID, PLAN_YEAR, DEDUCTIBLE_MET_AMT, LAST_UPDATED_DT)
                VALUES
                    (SEQ_ACCUM.NEXTVAL, v_claim.PH_ID, v_claim.PLAN_ID, v_plan_year, 0, SYSDATE);
                v_deductible_met := 0;
        END;
        v_deductible_remain  := v_plan.ANNUAL_DEDUCTIBLE_AMT - v_deductible_met;
        v_deductible_applied := LEAST(GREATEST(v_deductible_remain, 0), v_total_allowed);
        UPDATE MEMBER_DEDUCTIBLES
           SET DEDUCTIBLE_MET_AMT = DEDUCTIBLE_MET_AMT + v_deductible_applied,
               LAST_UPDATED_DT = SYSDATE
         WHERE PH_ID = v_claim.PH_ID
           AND PLAN_ID = v_claim.PLAN_ID
           AND PLAN_YEAR = v_plan_year;

        -- Rule 7: copay, patient liability, out-of-pocket cap, paid distribution
        BEGIN
            SELECT COPAY_AMT INTO v_copay
              FROM PLAN_COPAY_SCHEDULE
             WHERE PLAN_ID = v_claim.PLAN_ID
               AND POS_CD = v_claim.POS_CD;
        EXCEPTION
            WHEN NO_DATA_FOUND THEN
                v_copay := 0;
        END;
        IF v_total_allowed = 0 THEN
            v_copay := 0;
        END IF;
        v_liability := CALC_PATIENT_LIABILITY(v_total_allowed, v_deductible_applied, v_copay,
                                              v_plan.COINSURANCE_PCT);
        BEGIN
            SELECT OOP_MET_AMT
              INTO v_oop_met
              FROM MEMBER_OOP_ACCUM
             WHERE PH_ID = v_claim.PH_ID
               AND PLAN_ID = v_claim.PLAN_ID
               AND PLAN_YEAR = v_plan_year
               FOR UPDATE;
        EXCEPTION
            WHEN NO_DATA_FOUND THEN
                INSERT INTO MEMBER_OOP_ACCUM
                    (ACCUM_ID, PH_ID, PLAN_ID, PLAN_YEAR, OOP_MET_AMT, LAST_UPDATED_DT)
                VALUES
                    (SEQ_ACCUM.NEXTVAL, v_claim.PH_ID, v_claim.PLAN_ID, v_plan_year, 0, SYSDATE);
                v_oop_met := 0;
        END;
        IF v_oop_met + v_liability > v_plan.OOP_MAX_AMT THEN
            v_liability := GREATEST(v_plan.OOP_MAX_AMT - v_oop_met, 0);
        END IF;
        UPDATE MEMBER_OOP_ACCUM
           SET OOP_MET_AMT = OOP_MET_AMT + v_liability,
               LAST_UPDATED_DT = SYSDATE
         WHERE PH_ID = v_claim.PH_ID
           AND PLAN_ID = v_claim.PLAN_ID
           AND PLAN_YEAR = v_plan_year;
        v_paid := ROUND(v_total_allowed - v_liability, 2);
        IF v_paid < 0 THEN
            v_paid := 0;
        END IF;

        v_approved_lines := v_line_count - v_denied_lines;
        v_distributed    := 0;
        v_idx            := 0;
        FOR r_apr IN c_approved LOOP
            v_idx := v_idx + 1;
            IF v_idx = v_approved_lines THEN
                v_line_paid := v_paid - v_distributed;
            ELSIF v_total_allowed > 0 THEN
                v_line_paid := ROUND(NVL(r_apr.ALLOWED_AMT, 0) * v_paid / v_total_allowed, 2);
            ELSE
                v_line_paid := 0;
            END IF;
            UPDATE CLAIM_LINES SET PAID_AMT = v_line_paid WHERE LINE_ID = r_apr.LINE_ID;
            v_distributed := v_distributed + v_line_paid;
        END LOOP;

        -- Rule 8: outcome, high-dollar routing, write the header
        IF v_line_count = 0 OR v_denied_lines = v_line_count THEN
            v_new_status    := 'DEN';
            v_denial_reason := NVL(v_first_reason, 'MANUAL');
            p_result_cd     := C_RESULT_DENIED;
            p_message       := 'Denied: all ' || v_line_count || ' line(s) denied (' || v_denial_reason || ')';
        ELSIF v_denied_lines > 0 THEN
            v_new_status    := 'PAR';
            v_denial_reason := v_first_reason;
            p_result_cd     := C_RESULT_PARTIAL;
            p_message       := 'Partially approved: ' || v_denied_lines || ' of ' || v_line_count
                               || ' line(s) denied; paid ' || TO_CHAR(v_paid, 'FM999999990.00');
        ELSE
            v_new_status    := 'APR';
            v_denial_reason := NULL;
            p_result_cd     := C_RESULT_APPROVED;
            p_message       := 'Approved: paid ' || TO_CHAR(v_paid, 'FM999999990.00')
                               || ', patient liability ' || TO_CHAR(v_liability, 'FM999999990.00');
        END IF;
        IF v_paid >= C_HIGH_DOLLAR_THRESHOLD AND v_new_status IN ('APR', 'PAR') THEN
            v_new_status := 'PND';
            ENQUEUE_WORK_ITEM(v_claim.CLAIM_ID, 'HIGH_DOLLAR');
            p_message := p_message || '; paid amount at or above ' || C_HIGH_DOLLAR_THRESHOLD
                         || ', routed to HIGH_DOLLAR queue, auto-pay suppressed';
        END IF;
        UPDATE CLAIMS
           SET STATUS_CD = v_new_status,
               DENIAL_REASON_CD = v_denial_reason,
               TOTAL_BILLED_AMT = v_total_billed,
               TOTAL_ALLOWED_AMT = v_total_allowed,
               TOTAL_PAID_AMT = v_paid,
               PATIENT_LIAB_AMT = v_liability,
               DEDUCTIBLE_APPLIED_AMT = v_deductible_applied,
               ADJUDICATED_DT = SYSDATE,
               UPDATED_BY = p_user,
               UPDATED_DT = SYSDATE,
               VERSION_NO = VERSION_NO + 1
         WHERE CLAIM_ID = v_claim.CLAIM_ID;
        LOG_STATUS_CHANGE(v_claim.CLAIM_ID, v_claim.STATUS_CD, v_new_status, p_user, p_message);

    -- Rule 9: log and rethrow
    EXCEPTION
        WHEN OTHERS THEN
            p_result_cd := C_RESULT_ERROR;
            p_message   := SUBSTR(SQLERRM, 1, 400);
            DBMS_OUTPUT.PUT_LINE('ADJUDICATE_CLAIM ' || p_claim_id || ' failed: ' || SQLERRM);
            RAISE;
    END ADJUDICATE_CLAIM;

    PROCEDURE REOPEN_CLAIM(p_claim_id   IN  NUMBER,
                           p_user       IN  VARCHAR2,
                           p_reason     IN  VARCHAR2) IS
        v_claim CLAIMS%ROWTYPE;
    BEGIN
        BEGIN
            SELECT * INTO v_claim FROM CLAIMS WHERE CLAIM_ID = p_claim_id FOR UPDATE NOWAIT;
        EXCEPTION
            WHEN NO_DATA_FOUND THEN
                RAISE_APPLICATION_ERROR(-20002, 'Claim ' || p_claim_id || ' not found');
        END;
        IF v_claim.STATUS_CD NOT IN ('DEN', 'APR', 'PAR') THEN
            RAISE_APPLICATION_ERROR(-20001, 'Claim ' || v_claim.CLAIM_NUM || ' in status '
                || v_claim.STATUS_CD || ' cannot be reopened');
        END IF;
        IF SYSDATE - NVL(v_claim.ADJUDICATED_DT, v_claim.RECEIVED_DT) > C_REOPEN_WINDOW_DAYS THEN
            RAISE_APPLICATION_ERROR(-20003, 'Reopen window of ' || C_REOPEN_WINDOW_DAYS
                || ' days exceeded for claim ' || v_claim.CLAIM_NUM);
        END IF;
        -- TODO: reverse MEMBER_DEDUCTIBLES / MEMBER_OOP_ACCUM on reopen (INC-4471)
        UPDATE CLAIMS
           SET STATUS_CD = 'REO',
               UPDATED_BY = p_user,
               UPDATED_DT = SYSDATE,
               VERSION_NO = VERSION_NO + 1
         WHERE CLAIM_ID = v_claim.CLAIM_ID;
        LOG_STATUS_CHANGE(v_claim.CLAIM_ID, v_claim.STATUS_CD, 'REO', p_user, p_reason);
        ENQUEUE_WORK_ITEM(v_claim.CLAIM_ID, 'APPEALS');
    END REOPEN_CLAIM;

    PROCEDURE VOID_CLAIM(p_claim_id   IN  NUMBER,
                         p_user       IN  VARCHAR2,
                         p_reason     IN  VARCHAR2) IS
        v_claim CLAIMS%ROWTYPE;
    BEGIN
        BEGIN
            SELECT * INTO v_claim FROM CLAIMS WHERE CLAIM_ID = p_claim_id FOR UPDATE NOWAIT;
        EXCEPTION
            WHEN NO_DATA_FOUND THEN
                RAISE_APPLICATION_ERROR(-20002, 'Claim ' || p_claim_id || ' not found');
        END;
        IF v_claim.STATUS_CD IN ('PAID', 'VOID') THEN
            RAISE_APPLICATION_ERROR(-20001, 'Claim ' || v_claim.CLAIM_NUM || ' in status '
                || v_claim.STATUS_CD || ' cannot be voided');
        END IF;
        UPDATE CLAIMS
           SET STATUS_CD = 'VOID',
               TOTAL_PAID_AMT = 0,
               UPDATED_BY = p_user,
               UPDATED_DT = SYSDATE,
               VERSION_NO = VERSION_NO + 1
         WHERE CLAIM_ID = v_claim.CLAIM_ID;
        UPDATE WORK_ITEMS
           SET STATUS_CD = 'C', COMPLETED_DT = SYSDATE
         WHERE CLAIM_ID = v_claim.CLAIM_ID
           AND STATUS_CD <> 'C';
        LOG_STATUS_CHANGE(v_claim.CLAIM_ID, v_claim.STATUS_CD, 'VOID', p_user, p_reason);
    END VOID_CLAIM;

END PKG_CLAIMS;
/
